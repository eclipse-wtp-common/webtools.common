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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class GroupingConstraintOperator

    extends ConstraintOperator
    
{
    private final List<ConstraintOperator> children;
    private final List<ConstraintOperator> childrenReadOnly;
    
    public GroupingConstraintOperator( final Type type )
    {
        super( type );
        
        this.children = new ArrayList<ConstraintOperator>();
        this.childrenReadOnly = Collections.unmodifiableList( this.children );
    }
    
    public List<ConstraintOperator> getChildren()
    {
        return this.childrenReadOnly;
    }
    
    public void addChild( final ConstraintOperator child )
    {
        this.children.add( child );
    }
    
    public void addChildren( final Collection<ConstraintOperator> children )
    {
        this.children.addAll( children );
    }
    
    public void removeChild( final ConstraintOperator child )
    {
        this.children.remove( child );
    }
    
    public void removeChildren( final Collection<ConstraintOperator> children )
    {
        this.children.removeAll( children );
    }
    
}
