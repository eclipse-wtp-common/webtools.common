/******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.ui.internal;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.osgi.framework.Bundle;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class ProjectFacetsUiManagerImpl 
{
    private static final String EXTENSION_ID = "wizard"; //$NON-NLS-1$
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

    private static final HashMap metadata;
    
    /**
     * Maps either IProjectFacet or IRuntimeComponentType to an ImageDescriptor.
     */
    
    private static final HashMap icons = new HashMap();
    private static ImageDescriptor defaultIcon;
    
    static
    {
        // Make sure that the core extensions are loaded first.
        
        ProjectFacetsManager.getProjectFacets();
        RuntimeManager.getRuntimeComponentTypes();
        
        metadata = new HashMap();
        
        final Bundle bundle = Platform.getBundle( FacetUiPlugin.PLUGIN_ID );
        final URL url = bundle.getEntry( "images/unknown.gif" ); //$NON-NLS-1$
        defaultIcon = ImageDescriptor.createFromURL( url );
        
        readExtensions();
        readImagesExtensions();
    }
    
    private ProjectFacetsUiManagerImpl() {}
    
    public static ImageDescriptor getIcon( final Object obj )
    {
        final ImageDescriptor imgdesc = (ImageDescriptor) icons.get( obj );
        return imgdesc != null ? imgdesc : defaultIcon;
    }
    
    /**
     * @return (element type: {@see IFacetWizardPage})
     */
    
    public static List getWizardPages( final Action.Type actionType,
                                       final IProjectFacetVersion f )
    {
        final WizardPagesInfo info = (WizardPagesInfo) metadata.get( f );
        
        if( info != null )
        {
            final List clnames = (List) info.pagesets.get( actionType );
            
            if( clnames != null )
            {
                return getWizardPages( info.plugin, clnames );
            }
        }
        
        return Collections.EMPTY_LIST;
    }

    private static List getWizardPages( final String plugin,
                                        final List clnames )
    {
        final List pages = new ArrayList();
        
        for( Iterator itr = clnames.iterator(); itr.hasNext(); )
        {
            try
            {
                pages.add( create( plugin, (String) itr.next() ) );
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
            final Class cl = bundle.loadClass( clname );
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
    
    private static void readExtensions()
    {
        final IExtensionRegistry registry = Platform.getExtensionRegistry();
        
        final IExtensionPoint point 
            = registry.getExtensionPoint( FacetUiPlugin.PLUGIN_ID, 
                                          EXTENSION_ID );
        
        if( point == null )
        {
            throw new RuntimeException( "Extension point not found!" ); //$NON-NLS-1$
        }
        
        final IExtension[] extensions = point.getExtensions();
        
        for( int i = 0; i < extensions.length; i++ )
        {
            final IConfigurationElement[] elements 
                = extensions[ i ].getConfigurationElements();
            
            for( int j = 0; j < elements.length; j++ )
            {
                final IConfigurationElement config = elements[ j ];
                final String ename = config.getName();
                
                if( ename.equals( EL_WIZARD_PAGES ) )
                {
                    readWizardPagesInfo( config );
                }
            }
        }
    }
    
    private static void readWizardPagesInfo( final IConfigurationElement config )
    {
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
        
        final WizardPagesInfo info = new WizardPagesInfo();
        info.plugin = config.getContributor().getName();
        
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
            
            info.pagesets.put( actionType, readPageList( child ) );
        }
        
        metadata.put( fv, info );
    }

    private static List readPageList( final IConfigurationElement config )
    {
        final ArrayList list = new ArrayList();
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
        final IExtensionRegistry registry = Platform.getExtensionRegistry();
        
        final IExtensionPoint point 
            = registry.getExtensionPoint( FacetUiPlugin.PLUGIN_ID, 
                                          IMAGES_EXTENSION_ID );
        
        if( point == null )
        {
            throw new RuntimeException( "Extension point not found!" ); //$NON-NLS-1$
        }
        
        final IExtension[] extensions = point.getExtensions();
        
        for( int i = 0; i < extensions.length; i++ )
        {
            final IConfigurationElement[] elements 
                = extensions[ i ].getConfigurationElements();
            
            for( int j = 0; j < elements.length; j++ )
            {
                final IConfigurationElement config = elements[ j ];
                final String ename = config.getName();
                
                if( ename.equals( EL_IMAGE ) )
                {
                    readImage( config );
                }
            }
        }
    }
    
    private static void readImage( final IConfigurationElement config )
    {
        final String fid = config.getAttribute( ATTR_FACET );
        final String cid = config.getAttribute( ATTR_CATEGORY );
        final String rct = config.getAttribute( ATTR_RUNTIME_COMPONENT_TYPE );
        
        final Object target;
        
        if( fid != null )
        {
            if( ! ProjectFacetsManager.isProjectFacetDefined( fid ) )
            {
                final String msg
                    = NLS.bind( Resources.facetNotDefined, 
                                config.getContributor().getName(), fid );
                
                FacetUiPlugin.log( msg );
                
                return;
            }
            
            target = ProjectFacetsManager.getProjectFacet( fid );
        }
        else if( cid != null )
        {
            if( ! ProjectFacetsManager.isCategoryDefined( cid ) )
            {
                final String msg
                    = NLS.bind( Resources.categoryNotDefined, 
                                config.getContributor().getName(), fid );
                
                FacetUiPlugin.log( msg );
                
                return;
            }
            
            target = ProjectFacetsManager.getCategory( cid );
        }
        else if( rct != null )
        {
            if( ! RuntimeManager.isRuntimeComponentTypeDefined( rct ) )
            {
                final String msg
                    = NLS.bind( Resources.runtimeComponentTypeNotDefined, 
                                config.getContributor().getName(), rct );
                
                FacetUiPlugin.log( msg );
                
                return;
            }
            
            target = RuntimeManager.getRuntimeComponentType( rct );
        }
        else
        {
            reportMissingAttribute( config, ATTR_FACET );
            return;
        }
        
        final String path = config.getAttribute( ATTR_PATH );
        
        if( path == null )
        {
            reportMissingAttribute( config, ATTR_PATH );
        }
                
        final String plugin = config.getContributor().getName();
        final Bundle bundle = Platform.getBundle( plugin );
        final URL url = bundle.getEntry( path );
        
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
            
            icons.put( target, imgdesc );
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
        public HashMap pagesets = new HashMap();
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
        
        static
        {
            initializeMessages( ProjectFacetsUiManagerImpl.class.getName(), 
                                Resources.class );
        }
    }
    
}
