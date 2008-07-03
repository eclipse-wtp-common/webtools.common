/******************************************************************************
 * Copyright (c) 2008 BEA Systems, Inc.
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.IConstraint;
import org.eclipse.wst.common.project.facet.core.IGroup;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.IVersionExpr;
import org.eclipse.wst.common.project.facet.core.internal.ProjectFacetRef;
import org.eclipse.wst.common.project.facet.core.util.internal.VersionExpr;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class ConstraintUtil
{
    private ConstraintUtil() {}
    
    public static ConstraintOperator convert( final IConstraint inputConstraint )
    {
        final IConstraint.Type inputConstraintType = inputConstraint.getType();
        
        if( inputConstraintType == IConstraint.Type.AND ||
            inputConstraintType == IConstraint.Type.OR )
        {
            final ConstraintOperator.Type outputConstraintType 
                = ( inputConstraintType == IConstraint.Type.AND 
                    ? ConstraintOperator.Type.AND : ConstraintOperator.Type.OR );
            
            final GroupingConstraintOperator outputConstraint
                = new GroupingConstraintOperator( outputConstraintType );
            
            for( Object child : inputConstraint.getOperands() )
            {
                outputConstraint.addChild( convert( (IConstraint) child ) );
            }
            
            return outputConstraint;
        }
        else if( inputConstraintType == IConstraint.Type.REQUIRES )
        {
            final Object firstOperand = inputConstraint.getOperand( 0 );

            final MultiFacetConstraintOperator outputConstraint
                = new MultiFacetConstraintOperator( ConstraintOperator.Type.REQUIRES_ONE );
            
            if( firstOperand instanceof IProjectFacet )
            {
                final IProjectFacet f = (IProjectFacet) firstOperand;
                final IVersionExpr vexpr = (IVersionExpr) inputConstraint.getOperand( 1 );
                outputConstraint.addProjectFacetRef( new ProjectFacetRef( f, vexpr ) );
            }
            else
            {
                final IGroup group = (IGroup) firstOperand;
                outputConstraint.addProjectFacetRefs( convertGroupToFacetRefs( group ) );
            }
            
            return outputConstraint;
        }
        else if( inputConstraintType == IConstraint.Type.CONFLICTS )
        {
            final Object firstOperand = inputConstraint.getOperand( 0 );

            final MultiFacetConstraintOperator outputConstraint
                = new MultiFacetConstraintOperator( ConstraintOperator.Type.CONFLICTS );
            
            if( firstOperand instanceof IProjectFacet )
            {
                final IProjectFacet f = (IProjectFacet) firstOperand;
                final IVersionExpr vexpr = (IVersionExpr) inputConstraint.getOperand( 1 );
                outputConstraint.addProjectFacetRef( new ProjectFacetRef( f, vexpr ) );
            }
            else
            {
                final IGroup group = (IGroup) firstOperand;
                outputConstraint.addProjectFacetRefs( convertGroupToFacetRefs( group ) );
            }
            
            return outputConstraint;
        }
        else
        {
            throw new IllegalStateException( inputConstraintType.name() );
        }
    }
    
    private static Collection<ProjectFacetRef> convertGroupToFacetRefs( final IGroup group )
    {
        final Map<IProjectFacet,List<IProjectFacetVersion>> members
            = new HashMap<IProjectFacet,List<IProjectFacetVersion>>();
        
        for( IProjectFacetVersion fv : group.getMembers() )
        {
            final IProjectFacet f = fv.getProjectFacet();
            List<IProjectFacetVersion> versions = members.get( f );
            
            if( versions == null )
            {
                versions = new ArrayList<IProjectFacetVersion>();
                members.put( f, versions );
            }
            
            versions.add( fv );
        }

        final List<ProjectFacetRef> facetRefs = new ArrayList<ProjectFacetRef>( members.size() );
        
        for( Map.Entry<IProjectFacet,List<IProjectFacetVersion>> entry : members.entrySet() )
        {
            final IProjectFacet f = entry.getKey();
            final List<IProjectFacetVersion> versions = entry.getValue();
            final String vexprString;
            
            if( versions.size() == f.getVersions().size() )
            {
                vexprString = IVersionExpr.WILDCARD_SYMBOL;
            }
            else
            {
                final StringBuilder buf = new StringBuilder();
                
                for( IProjectFacetVersion fv : versions )
                {
                    if( buf.length() > 0 )
                    {
                        buf.append( ',' );
                    }
                    
                    buf.append( fv.getVersionString() );
                }
                
                vexprString = buf.toString();
            }
            
            final IVersionExpr vexpr;
            
            try
            {
                vexpr = new VersionExpr<IProjectFacetVersion>( f, vexprString, null );
            }
            catch( CoreException e )
            {
                throw new RuntimeException( e );
            }
            
            facetRefs.add( new ProjectFacetRef( f, vexpr ) );
        }
        
        return facetRefs;
    }
    
    public static ConstraintOperator normalize( final ConstraintOperator input )
    {
        if( input instanceof GroupingConstraintOperator )
        {
            final List<ConstraintOperator> oldChildren = ( (GroupingConstraintOperator) input ).getChildren();
            final List<ConstraintOperator> newChildren = new ArrayList<ConstraintOperator>( oldChildren.size() );
            
            for( ConstraintOperator child : oldChildren )
            {
                newChildren.add( normalize( child ) );
            }
            
            if( input.getType() == ConstraintOperator.Type.AND )
            {
                // Can merge any REQUIRES_ALL operators under an AND. Note that a REQUIRES_ONE with
                // a single facet can be treated as REQUIRES_ALL.
                
                MultiFacetConstraintOperator firstRequiresAll = null;
                
                for( Iterator<ConstraintOperator> itr = newChildren.iterator(); itr.hasNext(); )
                {
                    final ConstraintOperator op = itr.next();
                    
                    if( op.getType() == ConstraintOperator.Type.REQUIRES_ALL ||
                        ( op.getType() == ConstraintOperator.Type.REQUIRES_ONE &&
                          ( (MultiFacetConstraintOperator) op ).getProjectFacetRefs().size() == 1 ) )
                    {
                        final MultiFacetConstraintOperator mfop = (MultiFacetConstraintOperator) op;
                        
                        if( firstRequiresAll == null )
                        {
                            firstRequiresAll = mfop;
                        }
                        else
                        {
                            firstRequiresAll.addProjectFacetRefs( mfop.getProjectFacetRefs() );
                            itr.remove();
                        }
                    }
                }
                
                if( firstRequiresAll != null && 
                    firstRequiresAll.getType() == ConstraintOperator.Type.REQUIRES_ONE && 
                    firstRequiresAll.getProjectFacetRefs().size() > 1 )
                {
                    firstRequiresAll.setType( ConstraintOperator.Type.REQUIRES_ALL );
                }
                
                // Can merge any CONFLICTS operators under an AND.
                
                MultiFacetConstraintOperator firstConflicts = null;
                
                for( Iterator<ConstraintOperator> itr = newChildren.iterator(); itr.hasNext(); )
                {
                    final ConstraintOperator op = itr.next();
                    
                    if( op.getType() == ConstraintOperator.Type.CONFLICTS )
                    {
                        final MultiFacetConstraintOperator mfop = (MultiFacetConstraintOperator) op;
                        
                        if( firstConflicts == null )
                        {
                            firstConflicts = mfop;
                        }
                        else
                        {
                            firstConflicts.addProjectFacetRefs( mfop.getProjectFacetRefs() );
                            itr.remove();
                        }
                    }
                }
            }
            else
            {
                // Can merge any REQUIRES_ONE operators under an OR that have only one child.
                
                MultiFacetConstraintOperator firstRequiresOne = null;
                
                for( Iterator<ConstraintOperator> itr = newChildren.iterator(); itr.hasNext(); )
                {
                    final ConstraintOperator op = itr.next();
                    
                    if( op.getType() == ConstraintOperator.Type.REQUIRES_ONE )
                    {
                        final MultiFacetConstraintOperator mfop = (MultiFacetConstraintOperator) op;
                        
                        if( firstRequiresOne == null )
                        {
                            firstRequiresOne = mfop;
                        }
                        else
                        {
                            firstRequiresOne.addProjectFacetRefs( mfop.getProjectFacetRefs() );
                            itr.remove();
                        }
                    }
                }
            }

            if( newChildren.size() == 1 )
            {
                return newChildren.get( 0 );
            }
            else
            {
                final GroupingConstraintOperator output = new GroupingConstraintOperator( input.getType() );
                output.addChildren( newChildren );
                return output;
            }
        }
        else
        {
            return input;
        }
    }
    
}
