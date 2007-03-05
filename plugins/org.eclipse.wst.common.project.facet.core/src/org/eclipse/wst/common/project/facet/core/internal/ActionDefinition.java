/******************************************************************************
 * Copyright (c) 2005-2007 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.internal;

import static org.eclipse.wst.common.project.facet.core.internal.util.PluginUtil.instantiate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.project.facet.core.IActionConfig;
import org.eclipse.wst.common.project.facet.core.IActionConfigFactory;
import org.eclipse.wst.common.project.facet.core.IActionDefinition;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.IVersionExpr;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;

/**
 * The implementation of the <code>IActionDefinition</code> interface.
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class ActionDefinition

    implements IActionDefinition
    
{
    private String id;
    private String pluginId;
    private IProjectFacet facet;
    private IVersionExpr versionMatchExpr;
    private Action.Type type;
    private final Map<String,Object> properties;
    private final Map<String,Object> propertiesReadOnly;
    private String delegateClassName;
    private IDelegate delegate;
    private String configFactoryClassName;
    
    ActionDefinition()
    {
        this.properties = new HashMap<String,Object>();
        this.propertiesReadOnly = Collections.unmodifiableMap( this.properties );
    }
    
    public String getId()
    {
        return this.id;
    }
    
    void setId( final String id )
    {
        this.id = id;
    }
    
    void setPluginId( final String pluginId )
    {
        this.pluginId = pluginId;
    }
    
    public IProjectFacet getProjectFacet()
    {
        return this.facet;
    }
    
    void setProjectFacet( final IProjectFacet facet )
    {
        this.facet = facet;
    }
    
    public IVersionExpr getVersionExpr()
    {
        return this.versionMatchExpr;
    }
    
    @SuppressWarnings( "unchecked" )
    void setVersionExpr( final IVersionExpr expr )
    {
        this.versionMatchExpr = expr;
    }
    
    public Action.Type getActionType()
    {
        return this.type;
    }
    
    void setActionType( final Action.Type type )
    {
        this.type = type;
    }
    
    public Map<String,Object> getProperties()
    {
        return this.propertiesReadOnly;
    }
    
    void setProperty( final String name,
                      final Object value )
    {
        this.properties.put( name, value );
    }
    
    public Object getProperty( final String name )
    {
        return this.properties.get( name );
    }
    
    public Object createConfigObject( final IProjectFacetVersion fv,
                                      final String pjname )
    
        throws CoreException
        
    {
        if( this.configFactoryClassName == null )
        {
            return null;
        }
        else
        {
            final Object factory 
                = instantiate( this.pluginId, this.configFactoryClassName, 
                               IActionConfigFactory.class );
            
            final Object config = ( (IActionConfigFactory) factory ).create();
            
            IActionConfig cfg = null;
            
            if( config instanceof IActionConfig )
            {
                cfg = (IActionConfig) config;
            }
            else
            {
                final IAdapterManager m = Platform.getAdapterManager();
                cfg = (IActionConfig) m.loadAdapter( config, IActionConfig.class.getName() );
            }
            
            if( cfg != null )
            {
                cfg.setVersion( fv );
                cfg.setProjectName( pjname );
            }
            
            return config;
        }
    }
    
    String getDelegateClassName()
    {
        return this.delegateClassName;
    }
    
    void setDelegateClassName( final String delegateClassName )
    {
        this.delegateClassName = delegateClassName;
    }
    
    IDelegate getDelegate()
    
        throws CoreException
        
    {
        if( this.delegate == null )
        {
            final Object obj 
                = instantiate( this.pluginId, this.delegateClassName, IDelegate.class );
            
            this.delegate = (IDelegate) obj;
        }
        
        return this.delegate;
    }
    
    String getConfigFactoryClassName()
    {
        return this.configFactoryClassName;
    }
    
    void setConfigFactoryClassName( final String configFactoryClassName )
    {
        this.configFactoryClassName = configFactoryClassName;
    }
    
}