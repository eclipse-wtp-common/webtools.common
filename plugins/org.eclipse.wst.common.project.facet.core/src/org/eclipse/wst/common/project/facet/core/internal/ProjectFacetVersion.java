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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IActionConfig;
import org.eclipse.wst.common.project.facet.core.IActionConfigFactory;
import org.eclipse.wst.common.project.facet.core.IConstraint;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.internal.ProjectFacet.ActionDefinition;
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
    
    void setPlugin( final String plugin )
    {
        this.plugin = plugin;
    }
    
    public boolean supports( final Action.Type type )
    {
        try
        {
            return this.facet.getActionDefinition( this, IDelegate.Type.get( type ) ) != null;
        }
        catch( CoreException e )
        {
            FacetCorePlugin.log( e );
            return false;
        }
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
            = this.facet.getActionDefinition( this, IDelegate.Type.get( type ) );
        
        if( def == null || def.configFactoryClassName == null )
        {
            return null;
        }
        else
        {
            final String clname = def.configFactoryClassName;
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
        final IDelegate.Type t = IDelegate.Type.get( type );
        
        return this.facet.getActionDefinition( fv, t )
               == this.facet.getActionDefinition( this, t );
    }
    
    IDelegate getDelegate( final IDelegate.Type type )
    
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
            
            final String clname = def.delegateClassName;
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
        return this.facet.getLabel() + " " + this.version;
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
