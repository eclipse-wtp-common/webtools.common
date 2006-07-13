/******************************************************************************
 * Copyright (c) 2005, 2006 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.internal;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IActionDefinition;
import org.eclipse.wst.common.project.facet.core.IConstraint;
import org.eclipse.wst.common.project.facet.core.IGroup;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.IVersionExpr;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;

/**
 * The implementation of the <code>IProjectFacetVersion</code> interface.
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class ProjectFacetVersion 

    implements IProjectFacetVersion, IVersion 
    
{
    private ProjectFacet facet;
    private String version;
    private IConstraint constraint;
    private String plugin;
    
    ProjectFacetVersion() {}
    
    public IProjectFacet getProjectFacet() 
    {
        return this.facet;
    }
    
    void setProjectFacet( final ProjectFacet facet )
    {
        this.facet = facet;
    }

    public String getVersionString() 
    {
        return this.version;
    }
    
    void setVersionString( final String version )
    {
        this.version = version;
    }
    
    public Versionable getVersionable()
    {
        return this.facet;
    }

    public IConstraint getConstraint()
    {
        if( this.constraint == null )
        {
            this.constraint = new Constraint( this, IConstraint.Type.AND, new Object[ 0 ] );
        }
        
        return this.constraint;
    }
    
    void setConstraint( final IConstraint constraint )
    {
        this.constraint = constraint;
    }
    
    public String getPluginId()
    {
        return this.plugin;
    }
    
    void setPluginId( final String plugin )
    {
        this.plugin = plugin;
    }
    
    public boolean supports( final Set base,
                             final Action.Type type )
    {
        try
        {
            return ( getActionDefinitionInternal( base, type ) != null );
        }
        catch( CoreException e )
        {
            FacetCorePlugin.log( e );
            return false;
        }
    }
    
    /**
     * @deprecated
     */
    
    public boolean supports( final Action.Type type )
    {
        try
        {
            return getActionDefinition( type ) != null;
        }
        catch( CoreException e )
        {
            FacetCorePlugin.log( e );
            return false;
        }
    }
    
    public Set getActionDefinitions()
    {
        return this.facet.getActionDefinitions( this );
    }
    
    public Set getActionDefinitions( final Action.Type type )
    {
        final Set result = new HashSet();
        
        for( Iterator itr = getActionDefinitions().iterator(); itr.hasNext(); )
        {
            final IActionDefinition def = (IActionDefinition) itr.next();
            
            if( def.getActionType() == type )
            {
                result.add( def );
            }
        }

        if( result.size() > 1 && type != Action.Type.VERSION_CHANGE )
        {
            final String msg
                = Resources.bind( Resources.multipleActionDefinitions,
                                  this.facet.getId(), this.version,
                                  type.toString() );
            
            FacetCorePlugin.logWarning( msg, true );
        }
        
        return result;
    }
    
    public IActionDefinition getActionDefinition( final Set base,
                                                  final Action.Type type )
    
        throws CoreException
        
    {
        final IActionDefinition def = getActionDefinitionInternal( base, type );
        
        if( def == null )
        {
            final String msg 
                = NLS.bind( Resources.actionNotSupported, toString(), 
                            type.toString() );
            
            throw new CoreException( FacetCorePlugin.createErrorStatus( msg ) );
        }
        
        return def;
    }
    
    private IActionDefinition getActionDefinitionInternal( final Set base,
                                                           final Action.Type type )
    
        throws CoreException
        
    {
        final Set definitions = getActionDefinitions( type );
        
        if( definitions.size() > 0 )
        {
            if( type == Action.Type.VERSION_CHANGE )
            {
                String fromVersion = null;
                
                for( Iterator itr = base.iterator(); itr.hasNext(); )
                {
                    final IProjectFacetVersion x = (IProjectFacetVersion) itr.next();
                    
                    if( x.getProjectFacet() == this.facet )
                    {
                        fromVersion = x.getVersionString();
                        break;
                    }
                }
                
                if( fromVersion != null )
                {
                    for( Iterator itr = definitions.iterator(); itr.hasNext(); )
                    {
                        final IActionDefinition def = (IActionDefinition) itr.next();
                        
                        final IVersionExpr vexpr 
                            = (IVersionExpr) def.getProperty( IActionDefinition.PROP_FROM_VERSIONS );
                        
                        if( vexpr == null || vexpr.evaluate( fromVersion ) )
                        {
                            return def;
                        }
                    }
                }
            }
            else
            {
                return (IActionDefinition) definitions.iterator().next();
            }
        }

        return null;
    }
    
    /**
     * @deprecated
     */
    
    public IActionDefinition getActionDefinition( final Action.Type type )
    
        throws CoreException
        
    {
        final Set definitions = getActionDefinitions( type );
        
        if( definitions.size() == 0 )
        {
            return null;
        }
        else
        {
            return (IActionDefinition) definitions.iterator().next();
        }
    }
    
    /**
     * @deprecated
     */
    
    public Object createActionConfig( final Action.Type type,
                                      final String pjname )
    
        throws CoreException
        
    {
        if( ! supports( type ) )
        {
            final String msg 
                = NLS.bind( Resources.actionNotSupported, toString(), 
                            type.toString() );
            
            throw new CoreException( FacetCorePlugin.createErrorStatus( msg ) );
        }
        
        final IActionDefinition def = getActionDefinition( type );
        
        if( def == null )
        {
            return null;
        }
        else
        {
            return def.createConfigObject( this, pjname );
        }
    }
    
    /**
     * @deprecated
     */
    
    public boolean isSameActionConfig( final Action.Type type,
                                       final IProjectFacetVersion fv )
    
        throws CoreException
        
    {
        return ( (ProjectFacetVersion) fv ).getActionDefinition( type ) == getActionDefinition( type );
    }
    
    public boolean isValidFor( final Set fixed )
    {
        for( Iterator itr = fixed.iterator(); itr.hasNext(); )
        {
            final IProjectFacet f = (IProjectFacet) itr.next();
            
            if( this.facet == f )
            {
                return true;
            }
        }
        
        for( Iterator itr1 = fixed.iterator(); itr1.hasNext(); )
        {
            final IProjectFacet f = (IProjectFacet) itr1.next();
            
            boolean conflictsWithAllVersions = true;
            
            for( Iterator itr2 = f.getVersions().iterator(); itr2.hasNext(); )
            {
                final IProjectFacetVersion fv 
                    = (IProjectFacetVersion) itr2.next();
                
                if( ! conflictsWith( fv ) )
                {
                    conflictsWithAllVersions = false;
                    break;
                }
            }
            
            if( conflictsWithAllVersions )
            {
                return false;
            }
        }
        
        return true;
    }
    
    public boolean conflictsWith( final IProjectFacetVersion fv )
    {
        if( this == fv )
        {
            return false;
        }
        else if( this.facet == fv.getProjectFacet() )
        {
            return true;
        }
        else
        {
            return conflictsWith( fv, getConstraint() ) || 
                   conflictsWith( this, fv.getConstraint() );
        }
    }
    
    private boolean conflictsWith( final IProjectFacetVersion fv,
                                   final IConstraint op )
    {
        if( op.getType() == IConstraint.Type.AND )
        {
            for( Iterator itr = op.getOperands().iterator(); itr.hasNext(); )
            {
                if( conflictsWith( fv, (IConstraint) itr.next() ) )
                {
                    return true;
                }
            }
            
            return false;
        }
        else if( op.getType() == IConstraint.Type.OR )
        {
            boolean allBranchesConflict = true;
            
            for( Iterator itr = op.getOperands().iterator(); itr.hasNext(); )
            {
                if( ! conflictsWith( fv, (IConstraint) itr.next() ) )
                {
                    allBranchesConflict = false;
                    break;
                }
            }
            
            return allBranchesConflict;
        }
        else if( op.getType() == IConstraint.Type.CONFLICTS )
        {
            final Object firstOperand = op.getOperand( 0 );
            
            if( firstOperand instanceof IGroup )
            {
                final IGroup group = (IGroup) firstOperand;
                return group.getMembers().contains( fv );
            }
            else
            {
                final IProjectFacet f = (IProjectFacet) firstOperand;
                
                final VersionExpr vexpr
                    = op.getOperands().size() == 2 
                      ? (VersionExpr) op.getOperand( 1 ) : null;
                
                try
                {
                    if( fv.getProjectFacet() == f )
                    {
                        if( vexpr == null || vexpr.evaluate( (IVersion ) fv ) )
                        {
                            return true;
                        }
                    }
                    
                    return false;
                }
                catch( CoreException e )
                {
                    FacetCorePlugin.log( e );
                    return false;
                }
            }
        }
        else if( op.getType() == IConstraint.Type.REQUIRES )
        {
            final IProjectFacet rf = (IProjectFacet) op.getOperand( 0 );
            final VersionExpr vexpr = (VersionExpr) op.getOperand( 1 );
            
            final boolean soft
                = ( (Boolean) op.getOperand( 2 ) ).booleanValue();
        
            if( soft )
            {
                return false;
            }
            else
            {
                boolean conflictsWithAllVersions = true;
                
                try
                {
                    final String vexprstr = vexpr.toString();
                    
                    for( Iterator itr = rf.getVersions( vexprstr ).iterator();
                         itr.hasNext(); )
                    {
                        final IProjectFacetVersion rfv 
                            = (IProjectFacetVersion) itr.next();
                        
                        if( ! rfv.conflictsWith( fv ) )
                        {
                            conflictsWithAllVersions = false;
                            break;
                        }
                    }
                }
                catch( CoreException e )
                {
                    FacetCorePlugin.log( e );
                    return false;
                }
            
                return conflictsWithAllVersions;
            }
        }
        else
        {
            throw new IllegalStateException();
        }
    }
    
    public String toString()
    {
        return this.facet.getLabel() + " " + this.version; //$NON-NLS-1$
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String actionNotSupported;
        public static String multipleActionDefinitions;
        
        static
        {
            initializeMessages( ProjectFacetVersion.class.getName(), 
                                Resources.class );
        }
        
        public static String bind( final String template,
                                   final Object arg1,
                                   final Object arg2,
                                   final Object arg3 )
        {
            return NLS.bind( template, new Object[] { arg1, arg2, arg3 } );
        }
    }

}
