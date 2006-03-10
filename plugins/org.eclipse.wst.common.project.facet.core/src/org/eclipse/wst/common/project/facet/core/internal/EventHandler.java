/******************************************************************************
 * Copyright (c) 2006 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;

/**
 * Describes a single faceted project event handler registered via the
 * <code>org.eclipse.wst.common.project.facet.core.facets</code> extension
 * point.
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class EventHandler
{
    public static final class Type
    {
        private static final Map items = new HashMap();

        public static final Type PRE_INSTALL 
            = new Type( "PRE_INSTALL" ); //$NON-NLS-1$
        
        public static final Type POST_INSTALL 
            = new Type( "POST_INSTALL" ); //$NON-NLS-1$
        
        public static final Type PRE_UNINSTALL 
            = new Type( "PRE_UNINSTALL" ); //$NON-NLS-1$
        
        public static final Type POST_UNINSTALL 
            = new Type( "POST_UNINSTALL" ); //$NON-NLS-1$
        
        public static final Type PRE_VERSION_CHANGE
            = new Type( "PRE_VERSION_CHANGE" ); //$NON-NLS-1$
        
        public static final Type POST_VERSION_CHANGE
            = new Type( "POST_VERSION_CHANGE" ); //$NON-NLS-1$
        
        public static final Type RUNTIME_CHANGED
            = new Type( "RUNTIME_CHANGED" ); //$NON-NLS-1$
        
        private final String name;
        
        private Type( final String name )
        {
            this.name = name;
            items.put( name, this );
        }
        
        public static Type valueOf( final String name )
        {
            return (Type) items.get( name );
        }
        
        public String name()
        {
            return this.name;
        }
        
        public String toString()
        {
            return this.name;
        }
    }
    
    private IProjectFacet facet;
    private VersionExpr vexpr;
    private Type type;
    private String pluginId;
    private Object delegate;
    
    EventHandler() {}
    
    IProjectFacet getProjectFacet()
    {
        return this.facet;
    }
    
    void setProjectFacet( final IProjectFacet facet )
    {
        this.facet = facet;
    }
    
    VersionExpr getVersionExpr()
    {
        return this.vexpr;
    }
    
    void setVersionExpr( final VersionExpr vexpr )
    {
        this.vexpr = vexpr;
    }
    
    Type getType()
    {
        return this.type;
    }
    
    void setType( final Type type )
    {
        this.type = type;
    }
    
    String getPluginId()
    {
        return this.pluginId;
    }
    
    void setPluginId( final String pluginId )
    {
        this.pluginId = pluginId;
    }
    
    boolean hasDelegate()
    {
        return ( this.delegate != null );
    }
    
    IDelegate getDelegate()
        
        throws CoreException
        
    {
        if( this.delegate instanceof String )
        {
            this.delegate 
                = FacetCorePlugin.instantiate( this.pluginId, 
                                               (String) this.delegate, 
                                               IDelegate.class );
        }
        
        return (IDelegate) this.delegate;
    }
    
    void setDelegate( final String className )
    {
        this.delegate = className;
    }
    
}
