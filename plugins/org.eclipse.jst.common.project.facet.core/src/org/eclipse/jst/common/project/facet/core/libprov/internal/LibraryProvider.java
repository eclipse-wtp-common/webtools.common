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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jst.common.project.facet.core.libprov.EnablementExpressionContext;
import org.eclipse.jst.common.project.facet.core.libprov.ILibraryProvider;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryInstallDelegate;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryProviderActionType;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryProviderInstallOperationConfig;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryProviderOperation;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryProviderOperationConfig;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectBase;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class LibraryProvider

    implements ILibraryProvider
    
{
    private final class ActionDef
    {
        private final String configClassName;
        private Class<LibraryProviderOperationConfig> configClass;
        private final String operationClassName;
        private Class<LibraryProviderOperation> operationClass;
        
        public ActionDef( final String configClassName,
                          final String operationClassName )
        {
            if( configClassName == null )
            {
                this.configClassName = null;
                this.configClass = LibraryProviderOperationConfig.class;
            }
            else
            {
                this.configClassName = configClassName;
                this.configClass = null;
            }
            
            this.operationClassName = operationClassName;
            this.operationClass = null;
        }
        
        public String getPluginId()
        {
            return LibraryProvider.this.getPluginId();
        }
        
        public synchronized Class<LibraryProviderOperationConfig> getConfigClass()
        {
            if( this.configClass == null )
            {
                this.configClass = loadClass( getPluginId(), this.configClassName,
                                              LibraryProviderOperationConfig.class );
            }
            
            return this.configClass;
        }
        
        public synchronized Class<LibraryProviderOperation> getOperationClass()
        {
            if( this.operationClass == null )
            {
                this.operationClass 
                    = loadClass( getPluginId(), this.operationClassName, 
                                 LibraryProviderOperation.class );
            }
            
            return this.operationClass;
        }
    }
    
    private static final String EXPR_VAR_CONTEXT 
        = "context"; //$NON-NLS-1$

    private static final String EXPR_VAR_REQUESTING_PROJECT_FACET 
        = "requestingProjectFacet"; //$NON-NLS-1$
    
    private static final String EXPR_VAR_PROJECT_FACETS 
        = "projectFacets"; //$NON-NLS-1$
    
    private static final String EXPR_VAR_TARGETED_RUNTIMES 
        = "targetedRuntimes"; //$NON-NLS-1$
    
    private static final String EXPR_VAR_PROVIDER
        = "provider"; //$NON-NLS-1$
    
    private String id;
    private String pluginId;
    private String label;
    private ILibraryProvider base;
    private boolean isAbstract;
    private boolean isHidden;
    private Expression enablementCondition;
    private Integer priority;
    private final Map<String,String> params;
    private final Map<LibraryProviderActionType,ActionDef> actionDefs;
    
    LibraryProvider()
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
        this.actionDefs = new EnumMap<LibraryProviderActionType,ActionDef>( LibraryProviderActionType.class );
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
    
    public ILibraryProvider getBaseProvider()
    {
        return this.base;
    }
    
    void setBaseProvider( final ILibraryProvider base )
    {
        this.base = base;
    }
    
    public ILibraryProvider getRootProvider()
    {
        ILibraryProvider prov = this;
        
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
    
    public boolean isEnabledFor( final IFacetedProjectBase fproj,
                                 final IProjectFacetVersion fv )
    {
        return isEnabledFor( fproj, fv, null );
    }
    
    public boolean isEnabledFor( final IFacetedProjectBase fproj,
                                 final IProjectFacetVersion fv,
                                 final Map<String,Object> customVariables )
    {
        final EvaluationContext evalContext = new EvaluationContext( null, fv );
        final EnablementExpressionContext context = new EnablementExpressionContext( fproj, fv, this );
        evalContext.setAllowPluginActivation( true );
        
        if( customVariables != null )
        {
            for( Map.Entry<String,Object> entry : customVariables.entrySet() )
            {
                evalContext.addVariable( entry.getKey(), entry.getValue() );
            }
        }
        
        evalContext.addVariable( EXPR_VAR_CONTEXT, context );
        evalContext.addVariable( EXPR_VAR_REQUESTING_PROJECT_FACET, fv );
        evalContext.addVariable( EXPR_VAR_PROJECT_FACETS, fproj.getProjectFacets() );
        evalContext.addVariable( EXPR_VAR_TARGETED_RUNTIMES, fproj.getTargetedRuntimes() );
        evalContext.addVariable( EXPR_VAR_PROVIDER, this );
        
        for( Expression expression : getEnablementConditions() )
        {
            try
            {
                final EvaluationResult evalResult = expression.evaluate( evalContext );
                
                if( evalResult == EvaluationResult.FALSE )
                {
                    return false;
                }
            }
            catch( CoreException e )
            {
                log( e );
            }
        }

        return true;
    }
    
    void setEnablementCondition( final Expression enablementCondition )
    {
        this.enablementCondition = enablementCondition;
    }
    
    private List<Expression> getEnablementConditions()
    {
        final List<Expression> expressions = new ArrayList<Expression>();
        LibraryProvider provider = this;
        
        while( provider != null )
        {
            if( provider.enablementCondition != null )
            {
                expressions.add( provider.enablementCondition );
            }
            
            provider = (LibraryProvider) provider.base;
        }
        
        return expressions;
    }
    
    public Map<String,String> getParams()
    {
        final Map<String,String> result = new HashMap<String,String>();
        
        if( this.base != null )
        {
            result.putAll( ( (LibraryProvider) this.base ).getParams() );
        }
        
        result.putAll( this.params );
        
        return result;
    }
    
    void addParam( final String name,
                   final String value )
    {
        this.params.put( name, value );
    }

    public boolean isActionSupported( final LibraryProviderActionType type )
    {
        return ( getActionDef( type ) != null );
    }
    
    public LibraryProviderOperation createOperation( final LibraryProviderActionType type )
    {
        final ActionDef actionDef = getActionDef( type );
        
        if( actionDef == null )
        {
            throw new IllegalArgumentException();
        }
        
        final Class<LibraryProviderOperation> cl = actionDef.getOperationClass();
        
        if( cl == null )
        {
            // The operation class could not be loaded. The problem has been reported to 
            // the user via the log.
            
            return null;
        }
        
        return instantiate( actionDef.getPluginId(), cl );
    }

    public LibraryProviderOperationConfig createInstallOperationConfig( final LibraryInstallDelegate libraryInstallDelegate )
    {
        final ActionDef actionDef = getActionDef( LibraryProviderActionType.INSTALL );
        
        if( actionDef == null )
        {
            throw new IllegalArgumentException();
        }
        
        final Class<LibraryProviderOperationConfig> cl = actionDef.getConfigClass();
        
        if( cl == null )
        {
            // Either the action has no config or the config class could not be loaded. In
            // the latter case, the problem has been reported to the user via the log.
            
            return null;
        }
        
        final LibraryProviderOperationConfig cfg = instantiate( actionDef.getPluginId(), cl );
        
        if( cfg instanceof LibraryProviderInstallOperationConfig )
        {
            ( (LibraryProviderInstallOperationConfig) cfg ).init( libraryInstallDelegate, this );
        }
        else
        {
            cfg.init( libraryInstallDelegate.getFacetedProject(), 
                      libraryInstallDelegate.getProjectFacetVersion(), this );
        }
        
        return cfg;
    }
    
    public LibraryProviderOperationConfig createOperationConfig( final IFacetedProjectBase fproj,
                                                                 final IProjectFacetVersion fv,
                                                                 final LibraryProviderActionType type )
    {
        final ActionDef actionDef = getActionDef( type );
        
        if( actionDef == null )
        {
            throw new IllegalArgumentException();
        }
        
        final Class<LibraryProviderOperationConfig> cl = actionDef.getConfigClass();
        
        if( cl == null )
        {
            // Either the action has no config or the config class could not be loaded. In
            // the latter case, the problem has been repoted to the user via the log.
            
            return null;
        }
        
        final LibraryProviderOperationConfig cfg = instantiate( actionDef.getPluginId(), cl );
        cfg.init( fproj, fv, this );
        
        return cfg;
    }
    
    ActionDef getActionDef( final LibraryProviderActionType type )
    {
        ActionDef actionDef = this.actionDefs.get( type );
        
        if( actionDef == null && this.base != null )
        {
            actionDef = ( (LibraryProvider) this.base ).getActionDef( type );
        }
        
        return actionDef;
    }
    
    void addActionDef( final LibraryProviderActionType type,
                       final String configClassName,
                       final String operationClassName )
    {
        final ActionDef actionDef = new ActionDef( configClassName, operationClassName );
        this.actionDefs.put( type, actionDef );
    }
    
    public int compareTo( final ILibraryProvider other )
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
