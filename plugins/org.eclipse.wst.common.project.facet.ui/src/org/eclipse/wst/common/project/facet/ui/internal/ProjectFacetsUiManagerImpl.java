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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.ui.internal.FacetUiPlugin;
import org.osgi.framework.Bundle;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class ProjectFacetsUiManagerImpl 
{
    private static final String EXTENSION_ID = "wizard";

    private final HashMap metadata;
    
    public ProjectFacetsUiManagerImpl()
    {
        this.metadata = new HashMap();
        
        readExtensions();
    }
    
    public Object getConfig( final Action.Type actionType,
                             final IProjectFacetVersion f )
    {
        final WizardPagesInfo info = (WizardPagesInfo) this.metadata.get( f );
        
        if( info != null )
        {
            final String clname = (String) info.configs.get( actionType );
            
            if( clname != null )
            {
                return create( info.plugin, clname );
            }
        }
        
        return null;
    }
    
    /**
     * @return (element type: {@see IFacetWizardPage})
     */
    
    public List getWizardPages( final Action.Type actionType,
                                final IProjectFacetVersion f )
    {
        final WizardPagesInfo info = (WizardPagesInfo) this.metadata.get( f );
        
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

    private List getWizardPages( final String plugin,
                                 final List clnames )
    {
        final List pages = new ArrayList();
        
        for( Iterator itr = clnames.iterator(); itr.hasNext(); )
        {
            pages.add( create( plugin, (String) itr.next() ) );
        }
        
        return pages;
    }
    
    private Object create( final String plugin,
                           final String clname )
    {
        final Bundle bundle = Platform.getBundle( plugin );
        
        try
        {
            final Class cl = bundle.loadClass( clname );
            return cl.newInstance();
        }
        catch( Exception e )
        {
            // TODO: handle this better.
            throw new RuntimeException( e );
        }
    }
    
    private void readExtensions()
    {
        final IExtensionRegistry registry = Platform.getExtensionRegistry();
        
        final IExtensionPoint point 
            = registry.getExtensionPoint( FacetUiPlugin.PLUGIN_ID, 
                                          EXTENSION_ID );
        
        if( point == null )
        {
            throw new RuntimeException( "Extension point not found!" );
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
                
                if( ename.equals( "wizard-pages" ) )
                {
                    readWizardPagesInfo( config );
                }
                else
                {
                    // TODO: handle this better.
                    throw new IllegalStateException();
                }
            }
        }
    }
    
    private void readWizardPagesInfo( final IConfigurationElement config )
    {
        final String name = config.getAttribute( "facet" );

        if( name == null )
        {
            // TODO: handle this better.
            throw new IllegalStateException();
        }

        final String version = config.getAttribute( "version" );

        if( version == null )
        {
            // TODO: handle this better.
            throw new IllegalStateException();
        }
        
        //TODO: Handle the case where the facet is not available.
        
        final IProjectFacetVersion fv
            = ProjectFacetsManager.getProjectFacet( name ).getVersion( version );
        
        final WizardPagesInfo info = new WizardPagesInfo();
        info.plugin = config.getDeclaringExtension().getNamespace();
        
        final IConfigurationElement[] children = config.getChildren();
        
        for( int i = 0; i < children.length; i++ )
        {
            final IConfigurationElement child = children[ i ];
            final String childName = child.getName();
            
            final Action.Type actionType;
            
            if( childName.equals( "install" ) )
            {
                actionType = Action.Type.INSTALL;
            }
            else if( childName.equals( "uninstall" ) )
            {
                actionType = Action.Type.UNINSTALL;
            }
            else
            {
                // TODO: handle this better.
                throw new IllegalStateException();
            }
            
            info.configs.put( actionType, readConfigClass( child ) );
            info.pagesets.put( actionType, readPageList( child ) );
        }
        
        this.metadata.put( fv, info );
    }

    private String readConfigClass( final IConfigurationElement config )
    {
        final IConfigurationElement[] children = config.getChildren();
        
        for( int i = 0; i < children.length; i++ )
        {
            final IConfigurationElement child = children[ i ];
            final String childName = child.getName();
            
            if( childName.equals( "config" ) )
            {
                final String clname = child.getAttribute( "class" );
                
                if( clname == null )
                {
                    // TODO: handle this better.
                    throw new IllegalStateException();
                }
                
                return clname;
            }
        }

        // TODO: handle this better.
        throw new IllegalStateException();
    }
    
    private List readPageList( final IConfigurationElement config )
    {
        final ArrayList list = new ArrayList();
        final IConfigurationElement[] children = config.getChildren();
        
        for( int i = 0; i < children.length; i++ )
        {
            final IConfigurationElement child = children[ i ];
            final String childName = child.getName();
            
            if( childName.equals( "page" ) )
            {
                final String clname = child.getAttribute( "class" );
                
                if( clname == null )
                {
                    // TODO: handle this better.
                    throw new IllegalStateException();
                }
                
                list.add( clname );
            }
        }
        
        return list;
    }
    
    private static class WizardPagesInfo
    {
        public String plugin;
        public HashMap configs = new HashMap();
        public HashMap pagesets = new HashMap();
    }
    
}
