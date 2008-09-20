/******************************************************************************
 * Copyright (c) 2008 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.jst.common.project.facet.core.libprov;

import static org.eclipse.jst.common.project.facet.core.internal.FacetedProjectFrameworkJavaPlugin.createErrorStatus;
import static org.eclipse.jst.common.project.facet.core.internal.FacetedProjectFrameworkJavaPlugin.log;
import static org.eclipse.wst.common.project.facet.core.util.internal.ProgressMonitorUtil.beginTask;
import static org.eclipse.wst.common.project.facet.core.util.internal.ProgressMonitorUtil.done;
import static org.eclipse.wst.common.project.facet.core.util.internal.ProgressMonitorUtil.submon;
import static org.eclipse.wst.common.project.facet.core.util.internal.ProgressMonitorUtil.worked;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jst.common.project.facet.core.libprov.internal.LibrariesInstallRecord;
import org.eclipse.jst.common.project.facet.core.libprov.internal.LibrariesProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.util.internal.CollectionsUtil;
import org.eclipse.wst.common.project.facet.core.util.internal.MiscUtil;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class LibrariesInstallDelegate

    extends LibrariesDelegate
    
{
    private static final String CLASS_NAME = LibrariesInstallDelegate.class.getName();
    
    public static final String PROP_AVAILABLE_PROVIDERS 
        = CLASS_NAME + ".AVAILABLE_PROVIDERS"; //$NON-NLS-1$
    
    public static final String PROP_SELECTED_PROVIDER 
        = CLASS_NAME + ".SELECTED_PROVIDER"; //$NON-NLS-1$
    
    private List<ILibrariesProvider> providers;
    private List<ILibrariesProvider> providersReadOnly;
    private ILibrariesProvider selectedProvider;
    private boolean selectedProviderIsDefault;
    private final Map<ILibrariesProvider,LibrariesProviderOperationConfig> configs;
    private final IPropertyChangeListener providerConfigListener;
    
    public LibrariesInstallDelegate()
    {
        this.providers = Collections.emptyList();
        this.providersReadOnly = Collections.emptyList();
        this.selectedProvider = null;
        this.selectedProviderIsDefault = true;
        this.configs = new HashMap<ILibrariesProvider,LibrariesProviderOperationConfig>();
        
        this.providerConfigListener = new IPropertyChangeListener()
        {
            public void propertyChanged( final String property,
                                         final Object oldValue,
                                         final Object newValue )
            {
                notifyListeners( property, oldValue, newValue );
            }
        };
    }
    
    @Override
    public synchronized void setFacetedProjectWorkingCopy( final IFacetedProjectWorkingCopy fpjwc )
    {
        super.setFacetedProjectWorkingCopy( fpjwc );
        
        fpjwc.addListener
        (
            new IFacetedProjectListener()
            {
                public void handleEvent( final IFacetedProjectEvent event )
                {
                    refresh();
                }
            },
            IFacetedProjectEvent.Type.PROJECT_MODIFIED
        );
        
        refresh();
    }

    @Override
    public synchronized void setProjectFacetVersion( final IProjectFacetVersion fv )
    {
        super.setProjectFacetVersion( fv );
        refresh();
    }

    public synchronized final List<ILibrariesProvider> getLibrariesProviders()
    {
        return this.providersReadOnly;
    }
    
    @Override
    public synchronized final ILibrariesProvider getLibrariesProvider()
    {
        return this.selectedProvider;
    }
    
    public synchronized final void setLibrariesProvider( final ILibrariesProvider provider )
    {
        setLibrariesProvider( provider, false );
    }

    private synchronized final void setLibrariesProvider( final ILibrariesProvider provider,
                                                          final boolean isDefaultSelection )
    {
        if( ! this.providers.contains( provider ) &&
            ! ( this.providers.size() == 0 && provider == null ) )
        {
            throw new IllegalArgumentException();
        }
        
        if( ! MiscUtil.equal( this.selectedProvider, provider ) )
        {
            final ILibrariesProvider oldSelectedProvider = this.selectedProvider;
            this.selectedProvider = provider;
            this.selectedProviderIsDefault = isDefaultSelection;
            notifyListeners( PROP_SELECTED_PROVIDER, oldSelectedProvider, this.selectedProvider );
        }
    }
    
    @Override
    public synchronized final LibrariesProviderOperationConfig getLibrariesProviderOperationConfig()
    {
        if( this.selectedProvider == null )
        {
            return null;
        }
        else
        {
            return this.configs.get( this.selectedProvider );
        }
    }
    
    public synchronized final Object getLibrariesProviderOperationConfig( final ILibrariesProvider provider )
    {
        if( ! this.providers.contains( provider ) )
        {
            throw new IllegalArgumentException();
        }
        
        return this.configs.get( provider );
    }
    
    @Override
    public synchronized IStatus validate()
    {
        IStatus st = super.validate();
        
        if( ! st.isOK() )
        {
            return st;
        }
        
        if( this.providers.size() == 0 )
        {
            st = createErrorStatus( Resources.noProvidersAvailable );
        }
        else
        {
            final Object providerInstallOpConfig = this.configs.get( this.selectedProvider );
            
            if( providerInstallOpConfig instanceof LibrariesProviderOperationConfig )
            {
                st = ( (LibrariesProviderOperationConfig) providerInstallOpConfig ).validate();
            }
        }
        
        return st;
    }
    
    public synchronized void execute( final IProgressMonitor monitor )
    
        throws CoreException
        
    {
        beginTask( monitor, "", 10 ); //$NON-NLS-1$
        
        try
        {
            final IFacetedProjectWorkingCopy fpjwc = getFacetedProjectWorkingCopy();
            final IProjectFacetVersion fv = getProjectFacetVersion();
            final LibrariesProvider provider = (LibrariesProvider) getLibrariesProvider();
            
            // Install the libraries.
            
            final LibrariesProviderOperation librariesInstallOp 
                = provider.createOperation( LibrariesProviderActionType.INSTALL );
            
            final LibrariesProviderOperationConfig librariesInstallOpConfig 
                = getLibrariesProviderOperationConfig();
            
            librariesInstallOp.execute( fpjwc.getFacetedProject(), librariesInstallOpConfig, submon( monitor, 8 ) );
            
            // Record which libraries provider was used for this facet in workspace prefs. This 
            // will be used to default the provider selection the next time this facet is installed.
            
            LibrariesProviderFramework.setLastProviderUsed( fv, provider );
            worked( monitor, 1 );
            
            // Record which libraries provider was used for this facet in project metadata. This
            // will be used to know which provider to use when the facet is being uninstalled or
            // the version is being changed.
            
            final LibrariesInstallRecord installRecord = new LibrariesInstallRecord( fpjwc.getProject() );
            
            final LibrariesInstallRecord.Entry entry
                = new LibrariesInstallRecord.Entry( fv.getProjectFacet(), provider );
            
            installRecord.addEntry( entry );
            installRecord.save();
            
            worked( monitor, 1 );
        }
        finally
        {
            done( monitor );
        }
    }
    
    @Override
    public synchronized void dispose()
    {
        for( LibrariesProviderOperationConfig cfg : this.configs.values() )
        {
            try
            {
                cfg.dispose();
            }
            catch( Exception e )
            {
                log( e );
            }
        }
    }
    
    private final void refresh()
    {
        final IProjectFacetVersion fv = getProjectFacetVersion();
        final IFacetedProjectWorkingCopy fpjwc = getFacetedProjectWorkingCopy();
        
        if( fv == null || fpjwc == null )
        {
            return; 
        }
        
        final Set<ILibrariesProvider> newProvidersSet 
            = LibrariesProviderFramework.getProviders( fpjwc, fv );
            
        final List<ILibrariesProvider> newProviders 
            = new ArrayList<ILibrariesProvider>( newProvidersSet );
        
        final Comparator<ILibrariesProvider> comp = CollectionsUtil.getInvertingComparator();
        Collections.sort( newProviders, comp );
        
        if( newProviders.equals( this.providers ) )
        {
            for( LibrariesProviderOperationConfig config : this.configs.values() )
            {
                config.setProjectFacetVersion( fv );
            }
            
            return;
        }
        
        final List<ILibrariesProvider> oldProviders = this.providers;
        
        this.providers = newProviders;
        this.providersReadOnly = Collections.unmodifiableList( this.providers );
        
        for( ILibrariesProvider provider : this.providers )
        {
            final LibrariesProvider prov = (LibrariesProvider) provider;
            
            if( ! this.configs.containsKey( provider ) )
            {
                final LibrariesProviderOperationConfig config 
                    = prov.createOperationConfig( LibrariesProviderActionType.INSTALL );

                config.setProjectFacetVersion( fv );
                config.setParent( this );
                config.addListener( this.providerConfigListener );
                
                this.configs.put( provider, config );
            }
        }
        
        for( ILibrariesProvider provider : oldProviders )
        {
            if( ! this.providers.contains( provider ) )
            {
                final LibrariesProviderOperationConfig config = this.configs.remove( provider );
                config.removeListener( this.providerConfigListener );
                config.dispose();
            }
        }

        notifyListeners( PROP_AVAILABLE_PROVIDERS, oldProviders, this.providersReadOnly );
        
        if( this.selectedProvider == null || this.selectedProviderIsDefault ||
            ! this.providers.contains( this.selectedProvider ) )
        {
            if( this.providers.size() > 0 )
            {
                ILibrariesProvider provider = LibrariesProviderFramework.getLastProviderUsed( fv );
                
                if( provider == null || ! this.providers.contains( provider ) )
                {
                    provider = this.providers.iterator().next();
                }
                    
                setLibrariesProvider( provider, true );
            }
            else
            {
                setLibrariesProvider( null, true );
            }
        }
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String noProvidersAvailable;
        
        static
        {
            initializeMessages( LibrariesInstallDelegate.class.getName(), 
                                Resources.class );
        }
    }
    
}
