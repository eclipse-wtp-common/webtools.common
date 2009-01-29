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

import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.common.project.facet.core.libprov.internal.LibraryProvider;
import org.eclipse.jst.common.project.facet.core.libprov.internal.LibraryProviderFrameworkImpl;
import org.eclipse.jst.common.project.facet.core.libprov.internal.PropertiesHost;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectBase;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.util.internal.CollectionsUtil;
import org.eclipse.wst.common.project.facet.core.util.internal.MiscUtil;

/**
 * Used for configuring and then installing a library via the Library Provider Framework.
 * Instance of this class would typically be embedded in facet action config objects and then
 * executed during the execution of those actions. Can also be used stand-alone when it is
 * necessary to change libraries outside facet lifecycle actions.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @since 1.4
 * @noextend
 */

public final class LibraryInstallDelegate

    extends PropertiesHost
    
{
    private static final String CLASS_NAME = LibraryInstallDelegate.class.getName();
    
    /**
     * The property corresponding to the list of available providers.
     */
    
    public static final String PROP_AVAILABLE_PROVIDERS 
        = CLASS_NAME + ".AVAILABLE_PROVIDERS"; //$NON-NLS-1$
    
    /**
     * The property corresponding to the currently-selected provider.
     */
    
    public static final String PROP_SELECTED_PROVIDER 
        = CLASS_NAME + ".SELECTED_PROVIDER"; //$NON-NLS-1$
    
    private final IFacetedProjectBase fproj;
    private IProjectFacetVersion fv;
    private List<ILibraryProvider> providers;
    private List<ILibraryProvider> providersReadOnly;
    private ILibraryProvider selectedProvider;
    private boolean isDefaultSelection;
    private final Map<ILibraryProvider,LibraryProviderOperationConfig> configs;
    private final IPropertyChangeListener providerConfigListener;
    private LibraryUninstallDelegate uninstallDelegate = null;
    
    /**
     * Constructs a new library install delegate. 
     * 
     * @param fproj the faceted project (or a working copy)
     * @param fv the project facet that is requesting libraries
     */
    
    public LibraryInstallDelegate( final IFacetedProjectBase fproj,
                                   final IProjectFacetVersion fv )
    {
        this.fproj = fproj;
        this.fv = fv;
        this.providers = Collections.emptyList();
        this.providersReadOnly = Collections.emptyList();
        this.selectedProvider = null;
        this.isDefaultSelection = true;
        this.configs = new HashMap<ILibraryProvider,LibraryProviderOperationConfig>();
        
        this.providerConfigListener = new IPropertyChangeListener()
        {
            public void propertyChanged( final String property,
                                         final Object oldValue,
                                         final Object newValue )
            {
                notifyListeners( property, oldValue, newValue );
            }
        };

        getFacetedProject().addListener
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
        
        reset();
    }
    
    /**
     * Returns the faceted project that this install delegate was configured to use. Can be a working
     * copy.
     * 
     * @return the faceted project that this install delegate was configured to use
     */
    
    public IFacetedProjectBase getFacetedProject()
    {
        return this.fproj;
    }
    
    /**
     * Returns the project facet that made the request for libraries.
     * 
     * @return the project facet that made the request for libraries
     */
    
    public IProjectFacet getProjectFacet()
    {
        return this.fv.getProjectFacet();
    }
    
    /**
     * Returns the project facet version that made the request for libraries.
     * 
     * @return the project facet version that made the request for libraries
     */
    
    public IProjectFacetVersion getProjectFacetVersion()
    {
        return this.fv;
    }
    
    /**
     * Returns the list of library providers that are currently available. The list is sorted
     * by library provider priority (highest priority first). To listen for changes to this
     * list use PROP_AVAILABLE_PROVIDERS.
     * 
     * @return the list of library providers that are currently available
     */
    
    public synchronized List<ILibraryProvider> getLibraryProviders()
    {
        return this.providersReadOnly;
    }
    
    /**
     * Returns the library provider that is currently selected. To list for changes to this
     * property use PROP_SELECTED_PROVIDER.
     * 
     * @return the library provider that is currently selected
     */
    
    public synchronized ILibraryProvider getLibraryProvider()
    {
        return this.selectedProvider;
    }
    
    /**
     * Sets the current library provider. 
     * 
     * @param provider the provider that should be selected
     */
    
    public synchronized void setLibraryProvider( final ILibraryProvider provider )
    {
        setLibraryProvider( provider, false );
    }

    private synchronized void setLibraryProvider( final ILibraryProvider provider,
                                                  final boolean isDefaultSelection )
    {
        if( ! this.providers.contains( provider ) &&
            ! ( this.providers.size() == 0 && provider == null ) )
        {
            throw new IllegalArgumentException();
        }
        
        if( ! MiscUtil.equal( this.selectedProvider, provider ) )
        {
            final ILibraryProvider oldSelectedProvider = this.selectedProvider;
            
            this.selectedProvider = provider;
            this.isDefaultSelection = isDefaultSelection;
            
            final LibraryProviderOperationConfig config = this.configs.get( this.selectedProvider );
            
            if( config != null )
            {
                config.reset();
            }
            
            notifyListeners( PROP_SELECTED_PROVIDER, oldSelectedProvider, this.selectedProvider );
        }
    }
    
    /**
     * Returns the install operation config of the currently-selected provider. This property's
     * lifecycle is bound to the current provider changes, so listen for changes using 
     * PROP_SELECTED_PROVIDER.
     * 
     * @return the install operation config of the currently-selected provider
     */
    
    public synchronized LibraryProviderOperationConfig getLibraryProviderOperationConfig()
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
    
    /**
     * Returns the install operation config for any of the available library providers.
     * 
     * @param provider the library provider to look up install operation config
     * @return the install operation config for any of the available library providers
     */
    
    public synchronized LibraryProviderOperationConfig getLibraryProviderOperationConfig( final ILibraryProvider provider )
    {
        if( ! this.providers.contains( provider ) )
        {
            throw new IllegalArgumentException();
        }
        
        return this.configs.get( provider );
    }
    
    private static final String EXPR_VAR_CONTEXT 
        = "context"; //$NON-NLS-1$
    
    private static final String EXPR_VAR_REQUESTING_PROJECT_FACET 
        = "requestingProjectFacet"; //$NON-NLS-1$
    
    private static final String EXPR_VAR_PROJECT_FACETS 
        = "projectFacets"; //$NON-NLS-1$
    
    private static final String EXPR_VAR_TARGETED_RUNTIMES 
        = "targetedRuntimes"; //$NON-NLS-1$

    /**
     * This method is not api-ready. Don't use it yet.
     */
    
    public synchronized EvaluationContext createEvaluationContent()
    {
        final EvaluationContext evalContext = new EvaluationContext( null, this.fv );
        final EnablementExpressionContext context = new EnablementExpressionContext( this.fproj, this.fv );
        evalContext.addVariable( EXPR_VAR_CONTEXT, context );
        evalContext.addVariable( EXPR_VAR_REQUESTING_PROJECT_FACET, this.fv );
        evalContext.addVariable( EXPR_VAR_PROJECT_FACETS, this.fproj.getProjectFacets() );
        evalContext.addVariable( EXPR_VAR_TARGETED_RUNTIMES, this.fproj.getTargetedRuntimes() );
        evalContext.setAllowPluginActivation( true );
    
        return evalContext;
    }
    
    /**
     * Refreshes the list of available library providers and resets the current library provider
     * if the one currently selected is not in the available list any longer.
     */
    
    public synchronized void refresh()
    {
        final IProjectFacetVersion fv = getProjectFacetVersion();
        final IFacetedProjectBase fproj = getFacetedProject();
        
        final List<ILibraryProvider> oldProviders = this.providers;
        
        for( ILibraryProvider provider : oldProviders )
        {
            final LibraryProviderOperationConfig config = this.configs.remove( provider );
            
            if( config != null )
            {
                config.removeListener( this.providerConfigListener );
                config.dispose();
            }
        }
        
        final List<ILibraryProvider> newProviders = new ArrayList<ILibraryProvider>();
    
        for( ILibraryProvider provider : LibraryProviderFramework.getProviders() )
        {
            if( ! provider.isAbstract() && ! provider.isHidden() 
                && provider.isEnabledFor( fproj, fv ) )
            {
                newProviders.add( provider );
            }
        }
        
        final Comparator<ILibraryProvider> comp = CollectionsUtil.getInvertingComparator();
        Collections.sort( newProviders, comp );
        
        this.providers = newProviders;
        this.providersReadOnly = Collections.unmodifiableList( this.providers );
        
        for( ILibraryProvider provider : this.providers )
        {
            final LibraryProvider prov = (LibraryProvider) provider;
            final LibraryProviderOperationConfig config = prov.createInstallOperationConfig( this );
            config.addListener( this.providerConfigListener );
            this.configs.put( provider, config );
        }
        
        notifyListeners( PROP_AVAILABLE_PROVIDERS, oldProviders, this.providersReadOnly );
        
        if( this.providers.size() > 0 )
        {
            if( this.selectedProvider == null || this.isDefaultSelection || 
                ! this.providers.contains( this.selectedProvider ) )
            {
                ILibraryProvider provider = LibraryProviderFrameworkImpl.get().getLastProviderUsed( fv );
                
                if( provider == null || ! this.providers.contains( provider ) )
                {
                    provider = this.providers.iterator().next();
                }
                    
                setLibraryProvider( provider, true );
            }
        }
        else
        {
            setLibraryProvider( null, true );
        }
    }

    /**
     * Resets this install delegate to its initial state (prior to any user changes).
     */

    public synchronized void reset()
    {
        refresh();
        
        final IProjectFacet facet = this.fv.getProjectFacet();
        final IFacetedProject fpj;
        
        if( this.fproj instanceof IFacetedProject )
        {
            fpj = (IFacetedProject) this.fproj;
        }
        else
        {
            fpj = ( (IFacetedProjectWorkingCopy) this.fproj ).getFacetedProject();
        }
        
        ILibraryProvider provider = null;
        
        if( fpj != null && fpj.hasProjectFacet( facet ) )
        {
            this.uninstallDelegate = new LibraryUninstallDelegate( fpj, this.fv );
            
            provider = LibraryProviderFramework.getCurrentProvider( fpj.getProject(), facet );
            
            if( provider == null )
            {
                throw new RuntimeException();
            }
            
            if( ! this.providers.contains( provider ) )
            {
                final List<ILibraryProvider> oldProviders = this.providersReadOnly;
                
                this.providers = new ArrayList<ILibraryProvider>( this.providers );
                this.providersReadOnly = Collections.unmodifiableList( this.providers );
                this.providers.add( provider );
                
                notifyListeners( PROP_AVAILABLE_PROVIDERS, oldProviders, this.providersReadOnly );
            }
        }
        else
        {
            this.uninstallDelegate = null;
            
            provider = LibraryProviderFrameworkImpl.get().getLastProviderUsed( this.fv );
        
            if( provider == null || ! this.providers.contains( provider ) )
            {
                provider = this.providers.iterator().next();
            }
        }
        
        setLibraryProvider( provider );
        
        for( LibraryProviderOperationConfig config : this.configs.values() )
        {
            config.reset();
        }
    }

    /**
     * Checks the validity of the library install configuration. 
     * 
     * @return a status object describing configuration problems, if any
     */
    
    public synchronized IStatus validate()
    {
        IStatus st = Status.OK_STATUS;
        
        if( this.providers.size() == 0 )
        {
            st = createErrorStatus( Resources.noProvidersAvailable );
        }
        else
        {
            final Object providerInstallOpConfig = this.configs.get( this.selectedProvider );
            
            if( providerInstallOpConfig instanceof LibraryProviderOperationConfig )
            {
                st = ( (LibraryProviderOperationConfig) providerInstallOpConfig ).validate();
            }
        }
        
        return st;
    }
    
    /**
     * Executes the library install operation.
     * 
     * @param monitor the progress monitor for reporting status and handling cancellation requests
     * @throws CoreException if failed for some reason while executing the install operation
     */
    
    public synchronized void execute( final IProgressMonitor monitor )
    
        throws CoreException
        
    {
        beginTask( monitor, "", 10 ); //$NON-NLS-1$
        
        try
        {
            final IFacetedProjectBase fproj = getFacetedProject();
            final IProjectFacetVersion fv = getProjectFacetVersion();
            final LibraryProvider provider = (LibraryProvider) getLibraryProvider();
            
            // Uninstall the previous library, if applicable.
            
            if( this.uninstallDelegate != null )
            {
                this.uninstallDelegate.execute( new NullProgressMonitor() );
            }
            
            // Install the library.
            
            final LibraryProviderOperation libraryInstallOp 
                = provider.createOperation( LibraryProviderActionType.INSTALL );
            
            final LibraryProviderOperationConfig libraryInstallOpConfig 
                = getLibraryProviderOperationConfig();
            
            libraryInstallOp.execute( libraryInstallOpConfig, submon( monitor, 8 ) );
            
            // Record which library provider was used for this facet in workspace prefs. This 
            // will be used to default the provider selection the next time this facet is installed.
            
            LibraryProviderFrameworkImpl.get().setLastProviderUsed( fv, provider );
            worked( monitor, 1 );
            
            // Record which library provider was used for this facet in project metadata. This
            // will be used to know which provider to use when the facet is being uninstalled or
            // the version is being changed.
            
            LibraryProviderFrameworkImpl.get().setCurrentProvider( fproj.getProject(), fv.getProjectFacet(), provider );
            
            worked( monitor, 1 );
        }
        finally
        {
            done( monitor );
        }
    }
    
    /**
     * Cleans up allocated resources. Client code that instantiates this class is responsible that the
     * instance is properly disposed by calling the dispose method.
     */
    
    public synchronized void dispose()
    {
        for( LibraryProviderOperationConfig cfg : this.configs.values() )
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
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String noProvidersAvailable;
        
        static
        {
            initializeMessages( LibraryInstallDelegate.class.getName(), 
                                Resources.class );
        }
    }
    
}
