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

package org.eclipse.wst.common.project.facet.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;

/**
 * Models a single constraint in the constraint expression. 
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public interface IConstraint 
{
    /**
     * The enumeration of operator types.
     */
    
    static final class Type
    {
        private static final Map<String,Type> items = new HashMap<String,Type>();
        public static final Type AND = new Type( "and" ); //$NON-NLS-1$
        public static final Type OR = new Type( "or" ); //$NON-NLS-1$
        public static final Type REQUIRES = new Type( "requires" ); //$NON-NLS-1$
        public static final Type CONFLICTS = new Type( "conflicts" ); //$NON-NLS-1$
        
        private final String name;
        
        private Type( final String name )
        {
            this.name = name;
            items.put( name, this );
        }
        
        public static Type valueOf( final String name )
        {
            return items.get( name.toLowerCase() );
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
    
    /**
     * Returns the operator type
     * 
     * @return the operator type
     */
    
    Type getType();
    
    /**
     * Returns the list operands.
     * 
     * @return the list of operands
     */
    
    List<Object> getOperands();
    
    /**
     * Returns the operand at the specified position.
     * 
     * @param index the position of the operand in the list of operands
     * @return the operand
     */
    
    Object getOperand( int index );
    
    /**
     * Checks this constraint against the given set of project facets.
     * 
     * @param facets a set of project facets
     * @return a status object with severity of {@link IStatus#OK} if the
     *   constraint was satisfied or otherwise a {@link MultiStatus} composed of
     *   individual status objects for each of the problems
     */
    
    IStatus check( Collection<IProjectFacetVersion> facets );
    
    /**
     * Checks this constraint against the given set of project facets.
     * 
     * @param facets a set of project facets
     * @param validateSoftDependencies if <code>true</code> soft dependencies
     *   will be treated as required
     * @return a status object with severity of {@link IStatus#OK} if the
     *   constraint was satisfied or otherwise a {@link MultiStatus} composed of
     *   individual status objects for each of the problems
     */

    IStatus check( Collection<IProjectFacetVersion> facets,
                   boolean validateSoftDependencies );
}
