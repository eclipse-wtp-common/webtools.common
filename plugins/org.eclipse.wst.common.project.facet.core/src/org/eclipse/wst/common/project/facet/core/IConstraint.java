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

package org.eclipse.wst.common.project.facet.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;

/**
 * Models a single constraint in the constraint expression. 
 *  
 * <p><i>This class is part of an interim API that is still under development 
 * and expected to change significantly before reaching stability. It is being 
 * made available at this early stage to solicit feedback from pioneering 
 * adopters on the understanding that any code that uses this API will almost 
 * certainly be broken (repeatedly) as the API evolves.</i></p>
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public interface IConstraint 
{
    /**
     * The enumeration of operator types.
     *  
     * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
     */
    
    static final class Type
    {
        private static final Map items = new HashMap();

        public static final Type AND 
            = new Type( "and" ); //$NON-NLS-1$
        
        public static final Type OR 
            = new Type( "or" ); //$NON-NLS-1$
        
        public static final Type REQUIRES 
            = new Type( "requires" ); //$NON-NLS-1$
        
        public static final Type CONFLICTS 
            = new Type( "conflicts" ); //$NON-NLS-1$
        
        private final String name;
        
        private Type( final String name )
        {
            this.name = name;
            items.put( name, this );
        }
        
        public static Type valueOf( final String name )
        {
            return (Type) items.get( name.toLowerCase() );
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
     * @return the list of operands (element type: {@link Object})
     */
    
    List getOperands();
    
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
     * @param facets a set of project facets (element type:
     *   {@link IProjectFacetVersion})
     * @return a status object with severity of {@see IStatus#OK} if the
     *   constraint was satisfied or otherwise a {@see MultiStatus} composed of
     *   {@see ValidationProblem} status objects
     */
    
    IStatus check( Collection facets );
    
    /**
     * Checks this constraint against the given set of project facets.
     * 
     * @param facets a set of project facets (element type:
     *   {@link IProjectFacetVersion})
     * @param validateSoftDependencies if <code>true</code> soft dependencies
     *   will be treated as required
     * @return a status object with severity of {@see IStatus#OK} if the
     *   constraint was satisfied or otherwise a {@see MultiStatus} composed of
     *   {@see ValidationProblem} status objects
     */

    IStatus check( Collection facets,
                   boolean validateSoftDependencies );
}
