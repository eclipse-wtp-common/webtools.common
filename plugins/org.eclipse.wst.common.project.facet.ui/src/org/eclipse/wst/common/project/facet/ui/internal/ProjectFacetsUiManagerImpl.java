/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.ui.internal;

import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.findExtensions;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.getTopLevelElements;
import static org.eclipse.wst.common.project.facet.ui.internal.FacetUiPlugin.PLUGIN_ID;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IActionDefinition;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.internal.ProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentType;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.common.project.facet.ui.IFacetWizardPage;
import org.osgi.framework.Bundle;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ProjectFacetsUiManagerImpl 
{
    private static final String WIZARD_PAGES_EXTENSION_ID = "wizardPages"; //$NON-NLS-1$
    private static final String WIZARD_PAGES_EXTENSION_ID_OLD = "wizard"; //$NON-NLS-1$
    private static final String IMAGES_EXTENSION_ID = "images"; //$NON-NLS-1$
    
    private static final String EL_WIZARD_PAGES = "wizard-pages"; //$NON-NLS-1$
    private static final String EL_PAGE = "page"; //$NON-NLS-1$
    private static final String EL_IMAGE = "image"; //$NON-NLS-1$
    private static final String ATTR_PATH = "path"; //$NON-NLS-1$
    private static final String ATTR_RUNTIME_COMPONENT_TYPE = "runtime-component-type"; //$NON-NLS-1$
    private static final String ATTR_CATEGORY = "category"; //$NON-NLS-1$
    private static final String ATTR_CLASS = "class"; //$NON-NLS-1$
    private static final String ATTR_VERSION = "version"; //$NON-NLS-1$
    private static final String ATTR_FACET = "facet"; //$NON-NLS-1$
    private static final String ATTR_ACTION = "action"; //$NON-NLS-1$

    private static final Map<String,WizardPagesInfo> wizardPages;
    
    /**
     * Maps either IProjectFacet or IRuntimeComponentType to an ImageDescriptor.
     */
    
    private static final Map<Object,ImageDescriptor> icons = new HashMap<Object,ImageDescriptor>();
    private static ImageDescriptor defaultIcon;
    
    static
    {
        // Make sure that the core extensions are loaded first.
        
        ProjectFacetsManager.getProjectFacets();
        RuntimeManager.getRuntimeComponentTypes();
        
        wizardPages = new HashMap<String,WizardPagesInfo>();
        
        final Bundle bundle = Platform.getBundle( FacetUiPlugin.PLUGIN_ID );
        final URL url = bundle.getEntry( "images/unknown.gif" ); //$NON-NLS-1$
        defaultIcon = ImageDescriptor.createFromURL( url );
        
        readWizardPagesExtensions();
        readWizardPagesExtensionsOld();
        readImagesExtensions();
    }
    
    private ProjectFacetsUiManagerImpl() {}
    
    public static ImageDescriptor getIcon( final Object obj )
    {
        final ImageDescriptor imgdesc = icons.get( obj );
        return imgdesc != null ? imgdesc : defaultIcon;
    }
    
    public static List<IFacetWizardPage> getWizardPages( final String actionId )
    {
        final WizardPagesInfo info = wizardPages.get( actionId );
        
        if( info != null )
        {
            return getWizardPages( info.plugin, info.pages );
        }
        
        return Collections.emptyList();
    }

    /**
     * @return (element type: {@link IFacetWizardPage})
     * @deprecated
     */
    
    @SuppressWarnings( "unchecked" )
    public static List getWizardPages( final Action.Type actionType,
                                       final IProjectFacetVersion fv )
    {
        if( fv.supports( actionType ) )
        {
            try
            {
                final IActionDefinition def = ( (ProjectFacetVersion) fv ).getActionDefinition( actionType );
                return getWizardPages( def.getId() );
            }
            catch( CoreException e )
            {
                FacetUiPlugin.log( e );
            }
        }

        return Collections.EMPTY_LIST;
    }

    private static List<IFacetWizardPage> getWizardPages( final String plugin,
                                                          final List<String> clnames )
    {
        final List<IFacetWizardPage> pages = new ArrayList<IFacetWizardPage>();
        
        for( String clname : clnames )
        {
            try
            {
                pages.add( (IFacetWizardPage) create( plugin, clname ) );
            }
            catch( CoreException e )
            {
                FacetUiPlugin.log( e );
            }
        }
        
        return pages;
    }
    
    private static Object create( final String plugin,
                                  final String clname )
    
        throws CoreException
        
    {
        final Bundle bundle = Platform.getBundle( plugin );
        
        try
        {
            final Class<?> cl = bundle.loadClass( clname );
            return cl.newInstance();
        }
        catch( Exception e )
        {
            final String msg
                = NLS.bind( Resources.failedToCreate, clname );
            
            final IStatus st = FacetUiPlugin.createErrorStatus( msg, e );
            
            throw new CoreException( st );
        }
    }
    
    private static void readWizardPagesExtensions()
    {
        for( IConfigurationElement config
             : getTopLevelElements( findExtensions( PLUGIN_ID, WIZARD_PAGES_EXTENSION_ID ) ) )
        {
            final String ename = config.getName();
            
            if( ename.equals( EL_WIZARD_PAGES ) )
            {
                final String action = config.getAttribute( ATTR_ACTION );

                if( action == null )
                {
                    reportMissingAttribute( config, ATTR_ACTION );
                    return;
                }
                
                final String pluginId = config.getContributor().getName();
                
                if( ProjectFacetsManager.isActionDefined( action ) )
                {
                    final WizardPagesInfo pagesInfo = new WizardPagesInfo();
                    
                    pagesInfo.plugin = pluginId;
                    pagesInfo.pages = readPageList( config );
                    
                    wizardPages.put( action, pagesInfo );
                }
                else
                {
                    final String msg = NLS.bind( Resources.actionNotDefined, pluginId, action );
                    FacetUiPlugin.logError( msg );
                }
            }
        }
    }
    
    private static void readWizardPagesExtensionsOld()
    {
        for( IConfigurationElement config
            : getTopLevelElements( findExtensions( PLUGIN_ID, WIZARD_PAGES_EXTENSION_ID_OLD ) ) )
        {
            final String ename = config.getName();
            
            if( ename.equals( EL_WIZARD_PAGES ) )
            {
                readWizardPagesOld( config );
            }
        }
    }
    
    @SuppressWarnings( "deprecation" )
    private static void readWizardPagesOld( final IConfigurationElement config )
    {
        final String pluginId = config.getContributor().getName();

        final String depMsg 
            = NLS.bind( Resources.wizardExtensionPointDeprecated, pluginId );
        
        FacetUiPlugin.logWarning( depMsg, true );
        
        final String id = config.getAttribute( ATTR_FACET );

        if( id == null )
        {
            reportMissingAttribute( config, ATTR_FACET );
            return;
        }
        
        if( ! ProjectFacetsManager.isProjectFacetDefined( id ) )
        {
            final String msg
                = NLS.bind( Resources.facetNotDefined, 
                            config.getContributor().getName(), id );
            
            FacetUiPlugin.log( msg );
            
            return;
        }
        
        final IProjectFacet f = ProjectFacetsManager.getProjectFacet( id );

        final String version = config.getAttribute( ATTR_VERSION );

        if( version == null )
        {
            reportMissingAttribute( config, ATTR_VERSION );
            return;
        }
        
        if( ! f.hasVersion( version ) )
        {
            final String[] params
                = new String[] { config.getContributor().getName(), id, 
                                 version };
            
            final String msg
                = NLS.bind( Resources.facetVersionNotDefined, 
                            params ); 
            
            FacetUiPlugin.log( msg );
            
            return;
        }
        
        final IProjectFacetVersion fv = f.getVersion( version );
        final IConfigurationElement[] children = config.getChildren();
        
        for( int i = 0; i < children.length; i++ )
        {
            final IConfigurationElement child = children[ i ];
            final String childName = child.getName();
            final Action.Type actionType = Action.Type.valueOf( childName );
            
            if( actionType == null )
            {
                final String msg
                    = NLS.bind( Resources.invalidActionType, 
                                config.getContributor().getName(), childName );
                
                FacetUiPlugin.log( msg );
                
                return;
            }
            
            if( fv.supports( actionType ) )
            {
                final IActionDefinition def;
                
                try
                {
                    def = ( (ProjectFacetVersion) fv ).getActionDefinition( actionType );
                }
                catch( CoreException e )
                {
                    FacetUiPlugin.log( e );
                    return;
                }
                
                final WizardPagesInfo pagesInfo = new WizardPagesInfo();
                
                pagesInfo.plugin = pluginId;
                pagesInfo.pages = readPageList( child );
                
                wizardPages.put( def.getId(), pagesInfo );
            }
        }
    }

    private static List<String> readPageList( final IConfigurationElement config )
    {
        final List<String> list = new ArrayList<String>();
        final IConfigurationElement[] children = config.getChildren();
        
        for( int i = 0; i < children.length; i++ )
        {
            final IConfigurationElement child = children[ i ];
            final String childName = child.getName();
            
            if( childName.equals( EL_PAGE ) )
            {
                final String clname = child.getAttribute( ATTR_CLASS );
                
                if( clname == null )
                {
                    reportMissingAttribute( config, ATTR_CLASS );
                    continue;
                }
                
                list.add( clname );
            }
        }
        
        return list;
    }
    
    private static void readImagesExtensions()
    {
        for( IConfigurationElement config
            : getTopLevelElements( findExtensions( PLUGIN_ID, IMAGES_EXTENSION_ID ) ) )
        {
            final String ename = config.getName();
            
            if( ename.equals( EL_IMAGE ) )
            {
                final String fid = config.getAttribute( ATTR_FACET );
                final String cid = config.getAttribute( ATTR_CATEGORY );
                final String rct = config.getAttribute( ATTR_RUNTIME_COMPONENT_TYPE );
                
                final Set<Object> targets = new HashSet<Object>();
                
                if( fid != null )
                {
                    if( ! ProjectFacetsManager.isProjectFacetDefined( fid ) )
                    {
                        final String msg
                            = NLS.bind( Resources.facetNotDefined, 
                                        config.getContributor().getName(), fid );
                        
                        FacetUiPlugin.log( msg );
                        
                        break;
                    }
                    
                    targets.add( ProjectFacetsManager.getProjectFacet( fid ) );
                }
                else if( cid != null )
                {
                    if( ! ProjectFacetsManager.isCategoryDefined( cid ) )
                    {
                        final String msg
                            = NLS.bind( Resources.categoryNotDefined, 
                                        config.getContributor().getName(), fid );
                        
                        FacetUiPlugin.log( msg );
                        
                        break;
                    }
                    
                    targets.add( ProjectFacetsManager.getCategory( cid ) );
                }
                else if( rct != null )
                {
                    if( ! RuntimeManager.isRuntimeComponentTypeDefined( rct ) )
                    {
                        final String msg
                            = NLS.bind( Resources.runtimeComponentTypeNotDefined, 
                                        config.getContributor().getName(), rct );
                        
                        FacetUiPlugin.log( msg );
                        
                        break;
                    }
                    
                    final IRuntimeComponentType type = RuntimeManager.getRuntimeComponentType( rct );
                    final String vexpr = config.getAttribute( ATTR_VERSION );
                    
                    if( vexpr == null )
                    {
                        targets.addAll( type.getVersions() );
                    }
                    else
                    {
                        try
                        {
                            targets.addAll( type.getVersions( vexpr ) );
                        }
                        catch( Exception e )
                        {
                            FacetUiPlugin.log( e );
                        }
                    }
                }
                else
                {
                    reportMissingAttribute( config, ATTR_FACET );
                    break;
                }
                
                final String path = config.getAttribute( ATTR_PATH );
                
                if( path == null )
                {
                    reportMissingAttribute( config, ATTR_PATH );
                }
                        
                final String plugin = config.getContributor().getName();
                final Bundle bundle = Platform.getBundle( plugin );
                final URL url = FileLocator.find( bundle, new Path( path ), null );
                
                if( url == null )
                {
                    final String msg
                        = NLS.bind( Resources.iconNotFound, plugin, path );
                    
                    FacetUiPlugin.log( msg );
                }
                else
                {
                    final ImageDescriptor imgdesc
                        = ImageDescriptor.createFromURL( url );
                    
                    for( Object target : targets )
                    {
                        icons.put( target, imgdesc );
                    }
                }
            }
        }
    }
    
    private static void reportMissingAttribute( final IConfigurationElement el,
                                                final String attribute )
    {
        final String[] params 
            = new String[] { el.getContributor().getName(), el.getName(), 
                             attribute };
        
        final String msg = NLS.bind( Resources.missingAttribute, params ); 
    
        FacetUiPlugin.log( msg );
    }
    
    private static class WizardPagesInfo
    {
        public String plugin;
        public List<String> pages;
    }

    private static final class Resources
    
        extends NLS
        
    {
        public static String missingAttribute;
        public static String facetNotDefined;
        public static String facetVersionNotDefined;
        public static String categoryNotDefined;
        public static String runtimeComponentTypeNotDefined;
        public static String failedToCreate;
        public static String invalidActionType;
        public static String iconNotFound;
        public static String wizardExtensionPointDeprecated;
        public static String actionNotDefined;
        
        static
        {
            initializeMessages( ProjectFacetsUiManagerImpl.class.getName(), 
                                Resources.class );
        }
    }
    
}
