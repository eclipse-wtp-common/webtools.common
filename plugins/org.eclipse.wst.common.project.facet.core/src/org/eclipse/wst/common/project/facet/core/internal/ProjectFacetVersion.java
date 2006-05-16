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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IActionConfig;
import org.eclipse.wst.common.project.facet.core.IActionConfigFactory;
import org.eclipse.wst.common.project.facet.core.IActionDefinition;
import org.eclipse.wst.common.project.facet.core.IConstraint;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IGroup;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.osgi.framework.Bundle;

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
    private final HashMap delegates = new HashMap();
    
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
    
    public IActionDefinition getActionDefinition( final Action.Type type )
    
        throws CoreException
        
    {
        return this.facet.getActionDefinition( this, type );
    }
    
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
        
        final ActionDefinition def
            = this.facet.getActionDefinition( this, type );
        
        if( def == null || def.getConfigFactoryClassName() == null )
        {
            return null;
        }
        else
        {
            final String clname = def.getConfigFactoryClassName();
            final Object temp = create( clname );
            
            if( ! ( temp instanceof IActionConfigFactory ) )
            {
                final String msg
                    = NLS.bind( Resources.notInstanceOf, clname,
                                IActionConfigFactory.class.getName() );
                
                throw new CoreException( FacetCorePlugin.createErrorStatus( msg ) );
            }
            
            final Object config = ( (IActionConfigFactory) temp ).create();
            
            IActionConfig cfg = null;
            
            if( config instanceof IActionConfig )
            {
                cfg = (IActionConfig) config;
            }
            else
            {
                final IAdapterManager m = Platform.getAdapterManager();
                cfg = (IActionConfig) m.loadAdapter( config, IActionConfig.class.getName() );
            }
            
            if( cfg != null )
            {
                cfg.setProjectName( pjname );
                cfg.setVersion( this );
            }
            
            return config;
        }
    }
    
    public boolean isSameActionConfig( final Action.Type type,
                                       final IProjectFacetVersion fv )
    
        throws CoreException
        
    {
        return this.facet.getActionDefinition( fv, type )
               == this.facet.getActionDefinition( this, type );
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
                
                if( ! ( this.conflictsWith( fv ) || fv.conflictsWith( this ) ) )
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
            return conflictsWith( fv, getConstraint() );
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
                return true;
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
    
    IDelegate getDelegate( final Action.Type type )
    
        throws CoreException
        
    {
        Object delegate = this.delegates.get( type );
        
        if( delegate == null )
        {
            final ActionDefinition def
                = this.facet.getActionDefinition( this, type );
            
            if( def == null )
            {
                return null;
            }
            
            final String clname = def.getDelegateClassName();
            delegate = create( clname );
            
            if( ! ( delegate instanceof IDelegate ) )
            {
                final String msg
                    = NLS.bind( Resources.notInstanceOf, clname,
                                IDelegate.class.getName() );
                
                throw new CoreException( FacetCorePlugin.createErrorStatus( msg ) );
            }
            
            this.delegates.put( type, delegate );
        }
        
        return (IDelegate) delegate;
    }
    
    private Object create( final String clname )
    
        throws CoreException
        
    {
        final Bundle bundle = Platform.getBundle( this.plugin );
        
        try
        {
            final Class cl = bundle.loadClass( clname );
            return cl.newInstance();
        }
        catch( Exception e )
        {
            final String msg
                = NLS.bind( Resources.failedToCreate, clname );
            
            final IStatus st = FacetCorePlugin.createErrorStatus( msg, e );
            
            throw new CoreException( st );
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
        public static String notInstanceOf;
        public static String failedToCreate;
        
        static
        {
            initializeMessages( ProjectFacetVersion.class.getName(), 
                                Resources.class );
        }
    }

}
