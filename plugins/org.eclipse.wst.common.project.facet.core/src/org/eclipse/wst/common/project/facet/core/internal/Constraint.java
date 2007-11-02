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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IConstraint;
import org.eclipse.wst.common.project.facet.core.IGroup;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.IVersionExpr;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class Constraint

    implements IConstraint
    
{
    private final IProjectFacetVersion fv;
    private final Type type;
    private final List<Object> operands;
    
    Constraint( final IProjectFacetVersion fv,
                final Type type,
                final Object[] operands )
    {
        this.fv = fv;
        this.type = type;
        
        final List<Object> temp = new ArrayList<Object>();
        
        for( int i = 0; i < operands.length; i++ )
        {
            temp.add( operands[ i ] );
        }
        
        this.operands = Collections.unmodifiableList( temp );
    }
    
    public Type getType()
    {
        return this.type;
    }
    
    public List<Object> getOperands()
    {
        return this.operands;
    }
    
    public Object getOperand( final int index )
    {
        return this.operands.get( index );
    }
    
    public IStatus check( final Collection<IProjectFacetVersion> facets )
    {
        return check( facets, false );
    }
    
    public IStatus check( final Collection<IProjectFacetVersion> facets,
                          final boolean validateSoftDeps )
    {
        final MultiStatus result = createMultiStatus(); 
        
        if( this.type == Type.AND )
        {
            for( Object operand : this.operands )
            {
                final IConstraint childConstraint = (IConstraint) operand;
                final IStatus st = childConstraint.check( facets, validateSoftDeps );
                
                if( st.getSeverity() != IStatus.OK )
                {
                    result.addAll( st );
                }
            }
        }
        else if( this.type == Type.OR )
        {
            boolean someBranchWorks = false;
            
            for( Object operand : this.operands )
            {
                final IConstraint childConstraint = (IConstraint) operand;
                
                if( childConstraint.check( facets, validateSoftDeps ).isOK() )
                {
                    someBranchWorks = true;
                    break;
                }
            }
            
            if( ! someBranchWorks )
            {
                ValidationProblem p = null;
                
                if( this.operands.size() == 2 && containsOnlyRequires() )
                {
                    final ProjectFacetRef[] frefs = new ProjectFacetRef[ 2 ];
                    
                    for( int i = 0; i < 2; i++ )
                    {
                        final IConstraint c = (IConstraint) this.operands.get( i );
                        final IProjectFacet rf = (IProjectFacet) c.getOperand( 0 );
                        final IVersionExpr vexpr = (IVersionExpr) c.getOperand( 1 );
                        
                        frefs[ i ] = new ProjectFacetRef( rf, vexpr );
                    }
                    
                    p = new ValidationProblem( ValidationProblem.Type.REQUIRES_ONE_OF_TWO,
                                               this.fv, frefs[ 0 ], 
                                               frefs[ 1 ] );
                }
                
                if( p == null )
                {
                    p = new ValidationProblem( ValidationProblem.Type.COMPLEX,
                                               this.fv );
                }
                
                result.add( p );
            }
        }
        else if( this.type == Type.REQUIRES )
        {
            final Boolean soft
                = ( (Boolean) this.operands.get( this.operands.size() - 1 ) );
        
            if( ! soft.equals( Boolean.TRUE ) || validateSoftDeps )
            {
                final Object firstOperand = this.operands.get( 0 );
                
                if( firstOperand instanceof IGroup )
                {
                    final IGroup group = (IGroup) firstOperand;
                    
                    if( ! containsAny( facets, group.getMembers() ) )
                    {
                        final ValidationProblem.Type ptype
                            = ValidationProblem.Type.REQUIRES_GROUP;
                        
                        final ValidationProblem problem
                            = new ValidationProblem( ptype, this.fv, group );
                        
                        result.add( problem );
                    }
                }
                else
                {
                    final IProjectFacet rf = (IProjectFacet) firstOperand;
                    final IVersionExpr vexpr = (IVersionExpr) this.operands.get( 1 );
                    
                    boolean found = false;
                    
                    for( IProjectFacetVersion fv : facets )
                    {
                        if( fv.getProjectFacet() == rf )
                        {
                            if( vexpr.check( fv ) )
                            {
                                found = true;
                            }
                            
                            break;
                        }
                    }
                    
                    if( ! found )
                    {
                        final ValidationProblem.Type ptype = ValidationProblem.Type.REQUIRES;
                        final ProjectFacetRef fref = new ProjectFacetRef( rf, vexpr );
                        
                        final ValidationProblem problem 
                            = new ValidationProblem( ptype, this.fv, fref ); 
                        
                        result.add( problem );
                    }
                }
            }
        }
        else if( this.type == Type.CONFLICTS )
        {
            final Object firstOperand = this.operands.get( 0 );
            
            if( firstOperand instanceof IGroup )
            {
                final IGroup group = (IGroup) firstOperand;
            
                for( IProjectFacetVersion member : group.getMembers() )
                {
                    if( member.getProjectFacet() != this.fv.getProjectFacet() && 
                        facets.contains( member ) )
                    {
                        final ValidationProblem.Type t 
                            = ValidationProblem.Type.CONFLICTS;
                                
                        final ValidationProblem problem
                            = new ValidationProblem( t, this.fv, member );
                        
                        result.add( problem );
                    }
                }
            }
            else
            {
                final IProjectFacet f = (IProjectFacet) firstOperand;
                
                final IVersionExpr vexpr
                    = this.operands.size() == 2 ? (IVersionExpr) this.operands.get( 1 ) : null;
                
                for( IProjectFacetVersion fver : facets )
                {
                    if( fver.getProjectFacet() == f )
                    {
                        if( vexpr == null || vexpr.check( fver ) )
                        {
                            final ValidationProblem.Type t 
                                = ValidationProblem.Type.CONFLICTS;
                                    
                            final ValidationProblem problem
                                = new ValidationProblem( t, this.fv, fver );
                            
                            result.add( problem );
                            
                            break;
                        }
                    }
                }
            }
        }
        else
        {
            throw new IllegalStateException();
        }
        
        return result;
    }
    
    /**
     * Prunes the constraint by removing parts that are not relevant or satisfied by default
     * in the context of the provided fixed facets.
     * 
     * <ol>
     *   <li>An AND or an OR operator with no children is eliminated.</li>
     *   <li>An AND or an OR operator with one child is replaced by that child.</li>
     *   <li>A REQUIRES_FACET operator is eliminated if all applicable versions of
     *     that facet are incompatible with the fixed facets.</li>
     *   <li>A REQUIRED_FACET operator is eliminated if it will accept every version
     *     of a fixed facet.</li>
     *   <li>A CONFLICTS_WITH_FACET operator is eliminated if none of the matched versions
     *     of that facet are compatible with the fixed facets.</li>
     *   <li>A CONFLICTS_WITH_GROUP operator is eliminated if all members of the group
     *     (not counting this facet) are incompatible with the fixed facets.</li>
     * </ol>
     */
    
    public static IConstraint pruneConstraint( final IProjectFacetVersion facet,
                                               final Set<IProjectFacet> fixedFacets )
    {
        return pruneConstraint( facet, facet.getConstraint(), fixedFacets );
    }

    private static IConstraint pruneConstraint( final IProjectFacetVersion facet,
                                                final IConstraint constraint,
                                                final Set<IProjectFacet> fixedFacets )
    {
        final IConstraint.Type type = constraint.getType();
        
        if( type == IConstraint.Type.AND || type == IConstraint.Type.OR )
        {
            final List<IConstraint> newOperands = new ArrayList<IConstraint>();
            boolean changed = false;
            
            for( Object operand : constraint.getOperands() )
            {
                final IConstraint newOperand 
                    = pruneConstraint( facet, (IConstraint) operand, fixedFacets );
                
                if( newOperand == null )
                {
                    changed = true;
                }
                else if( operand != newOperand )
                {
                    changed = true;
                    newOperands.add( newOperand );
                }
                else
                {
                    newOperands.add( (IConstraint) operand );
                }
            }
            
            final int newOperandCount = newOperands.size();
            
            if( newOperandCount == 0 )
            {
                return null;
            }
            else if( newOperandCount == 1 )
            {
                return newOperands.get( 0 );
            }
            else if( changed )
            {
                return new Constraint( ( (Constraint) constraint ).fv, type,
                                       newOperands.toArray() );
            }
        }
        else if( type == IConstraint.Type.REQUIRES )
        {
            final Object firstOperand = constraint.getOperand( 0 );
            
            if( firstOperand instanceof IProjectFacet )
            {
                final IProjectFacet f = (IProjectFacet) firstOperand;
                final IVersionExpr vexpr = (IVersionExpr) constraint.getOperand( 1 );
                
                int acceptableVersionCount = 0;
                
                for( IProjectFacetVersion fv : f.getVersions() )
                {
                    if( vexpr.check( fv ) && fv.isValidFor( fixedFacets ) )
                    {
                        acceptableVersionCount++;
                    }
                }
                
                if( acceptableVersionCount == 0 )
                {
                    return null;
                }
                
                if( fixedFacets.contains( f ) && 
                    f.getVersions().size() == acceptableVersionCount )
                {
                    return null;
                }
            }
        }
        else if( type == IConstraint.Type.CONFLICTS )
        {
            final Object firstOperand = constraint.getOperand( 0 );
            
            if( firstOperand instanceof IProjectFacet )
            {
                final IProjectFacet f = (IProjectFacet) firstOperand;
                final IVersionExpr vexpr = (IVersionExpr) constraint.getOperand( 1 );
                
                boolean isValidForContext = false;
                
                for( IProjectFacetVersion fv : f.getVersions() )
                {
                    if( vexpr.check( fv ) && fv.isValidFor( fixedFacets ) )
                    {
                        isValidForContext = true;
                        break;
                    }
                }
                
                if( ! isValidForContext )
                {
                    return null;
                }
            }
            else if( firstOperand instanceof IGroup )
            {
                final IGroup group = (IGroup) firstOperand;
                
                boolean atLeastOneIsValid = false;
                
                for( IProjectFacetVersion member : group.getMembers() )
                {
                    if( member.getProjectFacet() != facet.getProjectFacet() && 
                        member.isValidFor( fixedFacets ) )
                    {
                        atLeastOneIsValid = true;
                        break;
                    }
                }
                
                if( ! atLeastOneIsValid )
                {
                    return null;
                }
            }
        }
        
        return constraint;
    }
    
    private boolean containsOnlyRequires()
    {
        for( Object operand : this.operands )
        {
            if( ( (IConstraint) operand ).getType() != Type.REQUIRES )
            {
                return false;
            }
        }
        
        return true;
    }
    
    private static boolean containsAny( final Collection<? extends Object> a,
                                        final Collection<? extends Object> b )
    {
        for( Object x : a )
        {
            if( b.contains( x ) )
            {
                return true;
            }
        }
        
        return false;
    }
    
    static MultiStatus createMultiStatus()
    {
        return createMultiStatus( new IStatus[ 0 ] );
    }

    public static MultiStatus createMultiStatus( final IStatus[] children )
    {
        return new MultiStatus( FacetCorePlugin.PLUGIN_ID, 0, children, 
                                Resources.validationProblems, null );
    }

    private static final class Resources
    
        extends NLS
        
    {
        public static String validationProblems;
        
        static
        {
            initializeMessages( Constraint.class.getName(), 
                                Resources.class );
        }
    }
    
}
