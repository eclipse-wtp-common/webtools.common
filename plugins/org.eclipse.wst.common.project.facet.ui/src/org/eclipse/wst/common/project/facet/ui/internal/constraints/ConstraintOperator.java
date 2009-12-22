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

package org.eclipse.wst.common.project.facet.ui.internal.constraints;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class ConstraintOperator
{
    public static enum Type
    {
        AND,
        OR,
        REQUIRES_ALL,
        REQUIRES_ONE,
        CONFLICTS
    }
    
    private Type type;
    
    protected ConstraintOperator( final Type type )
    {
        this.type = type;
    }
    
    public Type getType()
    {
        return this.type;
    }
    
    public void setType( final Type type )
    {
        this.type = type;
    }
}
