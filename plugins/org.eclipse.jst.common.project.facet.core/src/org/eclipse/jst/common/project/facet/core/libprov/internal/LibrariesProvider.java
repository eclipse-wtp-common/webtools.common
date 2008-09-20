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

package org.eclipse.jst.common.project.facet.core.libprov.internal;

import static org.eclipse.jst.common.project.facet.core.internal.FacetedProjectFrameworkJavaPlugin.log;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.instantiate;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.loadClass;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jst.common.project.facet.core.libprov.LibrariesProviderActionType;
import org.eclipse.jst.common.project.facet.core.libprov.ILibrariesProvider;
import org.eclipse.jst.common.project.facet.core.libprov.LibrariesProviderOperation;
import org.eclipse.jst.common.project.facet.core.libprov.LibrariesProviderOperationConfig;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class LibrariesProvider

    implements ILibrariesProvider
    
{
    private final class ActionDef
    {
        private final String configClassName;
        private Class<LibrariesProviderOperationConfig> configClass;
        private final String operationClassName;
        private Class<LibrariesProviderOperation> operationClass;
        
        public ActionDef( final String configClassName,
                          final String operationClassName )
        {
            this.configClassName = configClassName;
            this.configClass = null;
            this.operationClassName = operationClassName;
            this.operationClass = null;
        }
        
        public String getPluginId()
        {
            return LibrariesProvider.this.getPluginId();
        }
        
        public synchronized Class<LibrariesProviderOperationConfig> getConfigClass()
        {
            if( this.configClass == null && this.configClassName != null )
            {
                this.configClass = loadClass( getPluginId(), this.configClassName,
                                              LibrariesProviderOperationConfig.class );
            }
            
            return this.configClass;
        }
        
        public synchronized Class<LibrariesProviderOperation> getOperationClass()
        {
            if( this.operationClass == null )
            {
                this.operationClass 
                    = loadClass( getPluginId(), this.operationClassName, 
                                 LibrariesProviderOperation.class );
            }
            
            return this.operationClass;
        }
    }
    
    private static final String EXPR_VAR_REQUESTING_PROJECT_FACET 
        = "requestingProjectFacet"; //$NON-NLS-1$
    
    private static final String EXPR_VAR_PROJECT_FACETS 
        = "projectFacets"; //$NON-NLS-1$
    
    private static final String EXPR_VAR_TARGETED_RUNTIMES 
        = "targetedRuntimes"; //$NON-NLS-1$
    
    private String id;
    private String pluginId;
    private String label;
    private ILibrariesProvider base;
    private boolean isAbstract;
    private boolean isHidden;
    private Expression enablementCondition;
    private Integer priority;
    private final Map<String,String> params;
    private final Map<LibrariesProviderActionType,ActionDef> actionDefs;
    
    LibrariesProvider()
    {
        this.id = null;
        this.pluginId = null;
        this.label = null;
        this.base = null;
        this.isAbstract = false;
        this.isHidden = false;
        this.enablementCondition = null;
        this.priority = null;
        this.params = new HashMap<String,String>();
        this.actionDefs = new EnumMap<LibrariesProviderActionType,ActionDef>( LibrariesProviderActionType.class );
    }
    
    public String getId()
    {
        return this.id;
    }
    
    void setId( final String id )
    {
        this.id = id;
    }
    
    public String getPluginId()
    {
        return this.pluginId;
    }
    
    void setPluginId( final String pluginId )
    {
        this.pluginId = pluginId;
    }
    
    public String getLabel()
    {
        final String result;
        
        if( this.label != null )
        {
            result = this.label;
        }
        else
        {
            if( this.base != null )
            {
                result = this.base.getLabel();
            }
            else
            {
                result = this.id;
            }
        }
        
        return result;
    }
    
    void setLabel( final String label )
    {
        this.label = label;
    }
    
    public ILibrariesProvider getBaseProvider()
    {
        return this.base;
    }
    
    void setBaseProvider( final ILibrariesProvider base )
    {
        this.base = base;
    }
    
    public ILibrariesProvider getRootProvider()
    {
        ILibrariesProvider prov = this;
        
        while( prov.getBaseProvider() != null )
        {
            prov = prov.getBaseProvider();
        }
        
        return prov;
    }
    
    public boolean isAbstract()
    {
        return this.isAbstract;
    }
    
    void setIsAbstract( final boolean isAbstract )
    {
        this.isAbstract = isAbstract;
    }
    
    public boolean isHidden()
    {
        return this.isHidden;
    }
    
    void setIsHidden( final boolean isHidden )
    {
        this.isHidden = isHidden;
    }
    
    public int getPriority()
    {
        if( this.priority != null )
        {
            return this.priority.intValue();
        }
        else
        {
            if( this.base != null )
            {
                return this.base.getPriority();
            }
            else
            {
                return 0;
            }
        }
    }
    
    void setPriority( final int priority )
    {
        this.priority = Integer.valueOf( priority );
    }
    
    public boolean isEnabledFor( final IFacetedProjectWorkingCopy fpjwc,
                                 final IProjectFacetVersion fv )
    {
        if( this.base == null || this.base.isEnabledFor( fpjwc, fv ) )
        {
            if( this.enablementCondition == null )
            {
                return true;
            }
            
            final EvaluationContext evalContext = new EvaluationContext( null, fv );
            evalContext.addVariable( EXPR_VAR_REQUESTING_PROJECT_FACET, fv );
            evalContext.addVariable( EXPR_VAR_PROJECT_FACETS, fpjwc.getProjectFacets() );
            evalContext.addVariable( EXPR_VAR_TARGETED_RUNTIMES, fpjwc.getTargetedRuntimes() );
            evalContext.setAllowPluginActivation( true );
        
            try
            {
                final EvaluationResult evalResult 
                    = this.enablementCondition.evaluate( evalContext );
                
                return ( evalResult == EvaluationResult.TRUE );
            }
            catch( CoreException e )
            {
                log( e );
            }
        }

        return false;
    }
    
    void setEnablementCondition( final Expression enablementCondition )
    {
        this.enablementCondition = enablementCondition;
    }
    
    Map<String,String> getParams()
    {
        final Map<String,String> result = new HashMap<String,String>();
        
        if( this.base != null )
        {
            result.putAll( ( (LibrariesProvider) this.base ).getParams() );
        }
        
        result.putAll( this.params );
        
        return result;
    }
    
    void addParam( final String name,
                   final String value )
    {
        this.params.put( name, value );
    }

    public boolean isActionSupported( final LibrariesProviderActionType type )
    {
        return ( getActionDef( type ) != null );
    }
    
    public LibrariesProviderOperation createOperation( final LibrariesProviderActionType type )
    {
        final ActionDef actionDef = getActionDef( type );
        
        if( actionDef == null )
        {
            throw new IllegalArgumentException();
        }
        
        final Class<LibrariesProviderOperation> cl = actionDef.getOperationClass();
        
        if( cl == null )
        {
            // The operation class could not be loaded. The problem has been reported to 
            // the user via the log.
            
            return null;
        }
        
        return instantiate( actionDef.getPluginId(), cl );
    }

    public LibrariesProviderOperationConfig createOperationConfig( final LibrariesProviderActionType type )
    {
        final ActionDef actionDef = getActionDef( type );
        
        if( actionDef == null )
        {
            throw new IllegalArgumentException();
        }
        
        final Class<LibrariesProviderOperationConfig> cl = actionDef.getConfigClass();
        
        if( cl == null )
        {
            // Either the action has no config or the config class could not be loaded. In
            // the latter case, the problem has been repoted to the user via the log.
            
            return null;
        }
        
        final LibrariesProviderOperationConfig cfg = instantiate( actionDef.getPluginId(), cl );
        
        cfg.setLibrariesProvider( this );
        cfg.setParams( getParams() );
        
        return cfg;
    }
    
    ActionDef getActionDef( final LibrariesProviderActionType type )
    {
        ActionDef actionDef = this.actionDefs.get( type );
        
        if( actionDef == null && this.base != null )
        {
            actionDef = ( (LibrariesProvider) this.base ).getActionDef( type );
        }
        
        return actionDef;
    }
    
    void addActionDef( final LibrariesProviderActionType type,
                       final String configClassName,
                       final String operationClassName )
    {
        final ActionDef actionDef = new ActionDef( configClassName, operationClassName );
        this.actionDefs.put( type, actionDef );
    }
    
    public int compareTo( final ILibrariesProvider other )
    {
        final int p1 = getPriority();
        final int p2 = other.getPriority();
        
        int result = ( p1 < p2 ? -1 : ( p1 == p2 ? 0 : 1 ) );
        
        if( result == 0 )
        {
            result = getId().compareTo( other.getId() );
        }
        
        return result;
    }
    
}
