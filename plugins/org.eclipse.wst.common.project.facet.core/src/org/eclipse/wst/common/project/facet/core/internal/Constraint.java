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

package org.eclipse.wst.common.project.facet.core.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IConstraint;
import org.eclipse.wst.common.project.facet.core.IGroup;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class Constraint

    implements IConstraint
    
{
    private final IProjectFacetVersion fv;
    private final Type type;
    private final List operands;
    
    public Constraint( final IProjectFacetVersion fv,
                       final Type type,
                       final Object[] operands )
    {
        this.fv = fv;
        this.type = type;
        
        final ArrayList temp = new ArrayList();
        
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
    
    public List getOperands()
    {
        return this.operands;
    }
    
    public Object getOperand( final int index )
    {
        return this.operands.get( index );
    }
    
    public IStatus check( final Collection facets )
    {
        return check( facets, false );
    }
    
    public IStatus check( final Collection facets,
                          final boolean validateSoftDeps )
    {
        final MultiStatus result = createMultiStatus(); 
        
        if( this.type == Type.AND )
        {
            for( Iterator itr = this.operands.iterator(); itr.hasNext(); )
            {
                final IConstraint operand 
                    = (IConstraint) itr.next();
                
                final IStatus st = operand.check( facets, validateSoftDeps );
                
                if( st.getSeverity() != IStatus.OK )
                {
                    result.addAll( st );
                }
            }
        }
        else if( this.type == Type.OR )
        {
            boolean someBranchWorks = false;
            
            for( Iterator itr = this.operands.iterator(); itr.hasNext();  )
            {
                final IConstraint operand
                    = (IConstraint) itr.next();
                
                if( operand.check( facets, validateSoftDeps ).isOK() )
                {
                    someBranchWorks = true;
                    break;
                }
            }
            
            if( ! someBranchWorks )
            {
                final ValidationProblem problem
                    = new ValidationProblem( ValidationProblem.Type.COMPLEX,
                                             this.fv );
                
                result.add( problem );
            }
        }
        else if( this.type == Type.REQUIRES )
        {
            final String name = (String) this.operands.get( 0 );
            final String version = (String) this.operands.get( 1 );
            
            final boolean allowNewer 
                = ( (Boolean) this.operands.get( 2 ) ).booleanValue();
            
            final boolean soft
                = ( (Boolean) this.operands.get( 3 ) ).booleanValue();
            
            if( ! soft || validateSoftDeps )
            {
                final IProjectFacet rf 
                    = ProjectFacetsManager.getProjectFacet( name );
                
                final Comparator comp = rf.getVersionComparator();
                
                boolean found = false;
                
                for( Iterator itr = facets.iterator(); itr.hasNext(); )
                {
                    final IProjectFacetVersion fv 
                        = (IProjectFacetVersion) itr.next();
                    
                    final String fvstr = fv.getVersionString();
                    
                    if( fv.getProjectFacet() == rf )
                    {
                        final int compres = comp.compare( fvstr, version );
                        
                        if( ( allowNewer && compres >= 0 ) ||
                            ( ! allowNewer && compres == 0 ) )
                        {
                            found = true;
                        }
                    }
                }
                
                if( ! found )
                {
                    final ValidationProblem.Type ptype
                        = allowNewer ? ValidationProblem.Type.REQUIRES :
                          ValidationProblem.Type.REQUIRES_EXACT;
                    
                    final ValidationProblem problem
                        = new ValidationProblem( ptype, this.fv, rf.getLabel(), 
                                                 version );
                    
                    result.add( problem );
                }
            }
        }
        else if( this.type == Type.CONFLICTS )
        {
            final String gid = (String) this.operands.get( 0 );
            final IGroup group = ProjectFacetsManager.getGroup( gid );
            
            for( Iterator itr = group.getMembers().iterator(); itr.hasNext(); )
            {
                final IProjectFacetVersion member
                    = (IProjectFacetVersion) itr.next();
                
                if( member != this.fv && facets.contains( member ) )
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
            throw new IllegalStateException();
        }
        
        return result;
    }
    
    static MultiStatus createMultiStatus()
    {
        return createMultiStatus( new IStatus[ 0 ] );
    }

    static MultiStatus createMultiStatus( final IStatus[] children )
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
