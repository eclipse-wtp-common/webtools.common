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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IActionDefinition;
import org.eclipse.wst.common.project.facet.core.IConstraint;
import org.eclipse.wst.common.project.facet.core.IGroup;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.IVersion;
import org.eclipse.wst.common.project.facet.core.IVersionExpr;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.util.internal.Versionable;

/**
 * The implementation of the <code>IProjectFacetVersion</code> interface.
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class ProjectFacetVersion 

    implements IProjectFacetVersion
    
{
    private ProjectFacet facet;
    private String version;
    private IConstraint constraint;
    private String plugin;
    private Map<IProjectFacetVersion,Integer> compTable = Collections.emptyMap();
    private final Map<String,Object> properties;
    private final Map<String,Object> propertiesReadOnly;
    
    ProjectFacetVersion() 
    {
        this.properties = new HashMap<String,Object>();
        this.propertiesReadOnly = Collections.unmodifiableMap( this.properties );
    }
    
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
    
    public Versionable<IProjectFacetVersion> getVersionable()
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
    
    void setComparisonTable( final Map<IProjectFacetVersion,Integer> compTable )
    {
        this.compTable = compTable;
    }
    
    public boolean supports( final Set<IProjectFacetVersion> base,
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
    
    public Set<IActionDefinition> getActionDefinitions()
    {
        return this.facet.getActionDefinitions( this );
    }
    
    public Set<IActionDefinition> getActionDefinitions( final Action.Type type )
    {
        final Set<IActionDefinition> result = new HashSet<IActionDefinition>();
        
        for( IActionDefinition def : getActionDefinitions() )
        {
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
    
    public IActionDefinition getActionDefinition( final Set<IProjectFacetVersion> base,
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
    
    private IActionDefinition getActionDefinitionInternal( final Set<IProjectFacetVersion> base,
                                                           final Action.Type type )
    
        throws CoreException
        
    {
        final Set<IActionDefinition> definitions = getActionDefinitions( type );
        
        if( definitions.size() > 0 )
        {
            if( type == Action.Type.VERSION_CHANGE )
            {
                IProjectFacetVersion fromVersion = null;
                
                for( IProjectFacetVersion x : base )
                {
                    if( x.getProjectFacet() == this.facet )
                    {
                        fromVersion = x;
                        break;
                    }
                }
                
                if( fromVersion != null )
                {
                    for( IActionDefinition def : definitions )
                    {
                        final IVersionExpr vexpr 
                            = (IVersionExpr) def.getProperty( IActionDefinition.PROP_FROM_VERSIONS );
                        
                        if( vexpr == null || vexpr.check( fromVersion ) )
                        {
                            return def;
                        }
                    }
                }
            }
            else
            {
                return definitions.iterator().next();
            }
        }

        return null;
    }
    
    /**
     * @deprecated
     */
    
    @SuppressWarnings( "unchecked" )
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
    
    public boolean isValidFor( final Set<IProjectFacet> fixed )
    {
        for( IProjectFacet f : fixed )
        {
            if( this.facet == f )
            {
                return true;
            }
        }
        
        for( IProjectFacet f : fixed )
        {
            boolean conflictsWithAllVersions = true;

            for( IProjectFacetVersion fv : f.getVersions() )
            {
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
            for( Object operand : op.getOperands() )
            {
                if( conflictsWith( fv, (IConstraint) operand ) )
                {
                    return true;
                }
            }
            
            return false;
        }
        else if( op.getType() == IConstraint.Type.OR )
        {
            boolean allBranchesConflict = true;
            
            for( Object operand : op.getOperands() )
            {
                if( ! conflictsWith( fv, (IConstraint) operand ) )
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
                final IVersionExpr vexpr = (IVersionExpr) op.getOperand( 1 );
                
                if( fv.getProjectFacet() == f )
                {
                    if( vexpr.check( fv ) )
                    {
                        return true;
                    }
                }
                
                return false;
            }
        }
        else if( op.getType() == IConstraint.Type.REQUIRES )
        {
            final Boolean soft
                = (Boolean) op.getOperand( op.getOperands().size() - 1 );
        
            if( soft.equals( Boolean.TRUE ) )
            {
                return false;
            }
            else
            {
                final Object firstOperand = op.getOperand( 0 );
                boolean conflictsWithAll = true;
                
                if( firstOperand instanceof IGroup )
                {
                    final IGroup group = (IGroup) firstOperand;
                    
                    for( IProjectFacetVersion member : group.getMembers() )
                    {
                        if( ! member.conflictsWith( fv ) )
                        {
                            conflictsWithAll = false;
                            break;
                        }
                    }
                }
                else
                {
                    final IProjectFacet rf = (IProjectFacet) firstOperand;
                    final IVersionExpr vexpr = (IVersionExpr) op.getOperand( 1 );
                    
                    try
                    {
                        final String vexprstr = vexpr.toString();
                        
                        for( IProjectFacetVersion rfv : rf.getVersions( vexprstr ) )
                        {
                            if( ! rfv.conflictsWith( fv ) )
                            {
                                conflictsWithAll = false;
                                break;
                            }
                        }
                    }
                    catch( CoreException e )
                    {
                        FacetCorePlugin.log( e );
                        return false;
                    }
                }
                
                return conflictsWithAll;
            }
        }
        else
        {
            throw new IllegalStateException();
        }
    }
    
    public int compareTo( final Object obj )
    {
        if( obj == this )
        {
            return 0;
        }
        
        if( obj instanceof IProjectFacetVersion )
        {
            final IProjectFacetVersion fv = (IProjectFacetVersion) obj;
            
            if( fv.getProjectFacet() != this.facet )
            {
                final String msg
                    = Resources.bind( Resources.cannotCompareVersionsOfDifferentFacets,
                                      this.facet.getId(), this.version,
                                      fv.getProjectFacet().getId(), 
                                      fv.getVersionString() );
                
                throw new RuntimeException( msg );
            }
            
            final Integer cachedResult = this.compTable.get( fv );
            
            if( cachedResult != null )
            {
                return cachedResult.intValue();
            }
        }

        try
        {
            final Comparator<String> comp = this.facet.getVersionComparator();
            return comp.compare( this.version, ( (IVersion) obj ).getVersionString() );
        }
        catch( Exception e )
        {
            FacetCorePlugin.log( e );
            return 0;
        }
    }
    
    public Map<String,Object> getProperties()
    {
        return this.propertiesReadOnly;
    }
    
    public Object getProperty( final String name )
    {
        return this.properties.get( name );
    }

    void setProperty( final String name,
                      final Object value )
    {
        this.properties.put( name, value );
    }
    
    public String toString()
    {
        if( this.facet.isVersionHidden() )
        {
            return this.facet.getLabel();
        }
        else
        {
            return this.facet.getLabel() + " " + this.version; //$NON-NLS-1$
        }
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String actionNotSupported;
        public static String multipleActionDefinitions;
        public static String cannotCompareVersionsOfDifferentFacets;
        
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

        public static String bind( final String template,
                                   final Object arg1,
                                   final Object arg2,
                                   final Object arg3,
                                   final Object arg4 )
        {
            return NLS.bind( template, new Object[] { arg1, arg2, arg3, arg4 } );
        }
    }

}
