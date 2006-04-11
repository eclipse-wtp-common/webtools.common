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

import org.eclipse.wst.common.project.facet.core.IActionDefinition;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
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
    private IProjectFacet facet;
    private IVersionExpr versionMatchExpr;
    private Action.Type type;
    private String delegateClassName;
    private String configFactoryClassName;
    
    public String getId()
    {
        return this.id;
    }
    
    void setId( final String id )
    {
        this.id = id;
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
    
    String getDelegateClassName()
    {
        return this.delegateClassName;
    }
    
    void setDelegateClassName( final String delegateClassName )
    {
        this.delegateClassName = delegateClassName;
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