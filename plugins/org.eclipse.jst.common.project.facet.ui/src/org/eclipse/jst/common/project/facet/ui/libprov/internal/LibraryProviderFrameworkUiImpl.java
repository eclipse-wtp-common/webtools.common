/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.jst.common.project.facet.ui.libprov.internal;

import static org.eclipse.jst.common.project.facet.core.libprov.internal.LibraryProviderFrameworkImpl.reportProviderNotDefined;
import static org.eclipse.jst.common.project.facet.ui.internal.FacetedProjectFrameworkJavaExtUiPlugin.PLUGIN_ID;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.findRequiredAttribute;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.instantiate;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.loadClass;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jst.common.project.facet.core.libprov.ILibraryProvider;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryInstallDelegate;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryProviderActionType;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryProviderFramework;
import org.eclipse.jst.common.project.facet.ui.libprov.LibraryProviderOperationPanel;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.InvalidExtensionException;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class LibraryProviderFrameworkUiImpl
{
    private static final String EXTENSION_POINT_ID = "libraryProviderActionPanels"; //$NON-NLS-1$
    
    private static final String EL_PANEL = "panel"; //$NON-NLS-1$
    private static final String ATTR_CLASS = "class"; //$NON-NLS-1$
    private static final String ATTR_PROVIDER = "provider"; //$NON-NLS-1$

    private static LibraryProviderFrameworkUiImpl instance = null;
    
    private final Map<ILibraryProvider,Map<LibraryProviderActionType,PanelDef>> panels;
    
    public LibraryProviderFrameworkUiImpl()
    {
        this.panels = new HashMap<ILibraryProvider,Map<LibraryProviderActionType,PanelDef>>();
        readExtensions();
    }
    
    public static synchronized LibraryProviderFrameworkUiImpl get()
    {
        if( instance == null )
        {
            instance = new LibraryProviderFrameworkUiImpl();
        }
        
        return instance;
    }
    
    public Control createInstallLibraryPanel( final Composite parent,
                                              final LibraryInstallDelegate delegate,
                                              final String label )
    {
        return new LibraryInstallPanel( parent, delegate, label );
    }
    
    public LibraryProviderOperationPanel getOperationPanel( final ILibraryProvider provider,
                                                               final LibraryProviderActionType actionType )
    {
        ILibraryProvider prov = provider;
        PanelDef panelDef = null;
        
        while( prov != null && panelDef == null )
        {
            final Map<LibraryProviderActionType,PanelDef> providerPanelDefs = this.panels.get( prov );
            
            if( providerPanelDefs != null )
            {
                panelDef = providerPanelDefs.get( actionType );
            }
            
            prov = prov.getBaseProvider();
        }
        
        if( panelDef == null )
        {
            return null;
        }
        
        final Class<LibraryProviderOperationPanel> panelClass = panelDef.getPanelClass();
        
        if( panelClass == null )
        {
            return null;
        }
        
        return instantiate( panelDef.getPluginId(), panelClass );
    }
    
    private void readExtensions()
    {
        final IExtensionRegistry registry = Platform.getExtensionRegistry();
        
        final IExtensionPoint point 
            = registry.getExtensionPoint( PLUGIN_ID, EXTENSION_POINT_ID );
        
        if( point == null )
        {
            throw new IllegalStateException();
        }
        
        final List<IConfigurationElement> cfgels = new ArrayList<IConfigurationElement>();
        
        for( IExtension extension : point.getExtensions() )
        {
            for( IConfigurationElement element : extension.getConfigurationElements() )
            {
                cfgels.add( element );
            }
        }

        for( IConfigurationElement element : cfgels )
        {
            if( ! element.getName().equals( EL_PANEL ) )
            {
                continue;
            }
            
            try
            {
                final String pluginId = element.getContributor().getName();
                
                final String providerId = findRequiredAttribute( element, ATTR_PROVIDER );
                
                if( ! LibraryProviderFramework.isProviderDefined( providerId ) )
                {
                    reportProviderNotDefined( providerId, pluginId );
                    throw new InvalidExtensionException();
                }
                
                final ILibraryProvider provider 
                    = LibraryProviderFramework.getProvider( providerId );
                
                final String panelClassName = findRequiredAttribute( element, ATTR_CLASS );
                
                final PanelDef panelDef = new PanelDef( pluginId, panelClassName );
                
                Map<LibraryProviderActionType,PanelDef> providerPanelDefs = this.panels.get( provider );
                
                if( providerPanelDefs == null )
                {
                    providerPanelDefs = new EnumMap<LibraryProviderActionType,PanelDef>( LibraryProviderActionType.class );
                    this.panels.put( provider, providerPanelDefs );
                }
                
                providerPanelDefs.put( LibraryProviderActionType.INSTALL, panelDef );
            }
            catch( InvalidExtensionException e )
            {
                // Ignore and continue. The problem has already been reported to the user
                // in the log.
            }
        }
    }
    
    private static final class PanelDef
    {
        private final String pluginId;
        private final String className;
        private Class<LibraryProviderOperationPanel> panelClass;
        
        public PanelDef( final String pluginId,
                         final String className )
        {
            this.pluginId = pluginId;
            this.className = className;
            this.panelClass = null;
        }
        
        public String getPluginId()
        {
            return this.pluginId;
        }
        
        public synchronized Class<LibraryProviderOperationPanel> getPanelClass()
        {
            if( this.panelClass == null )
            {
                this.panelClass = loadClass( this.pluginId, this.className );
            }
            
            return this.panelClass;
        }
    }

}
