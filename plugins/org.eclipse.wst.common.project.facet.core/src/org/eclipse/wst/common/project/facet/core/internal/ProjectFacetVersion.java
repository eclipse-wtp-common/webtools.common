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

import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.project.facet.core.IConstraint;
import org.eclipse.wst.common.project.facet.core.IDelegate;
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

    implements IProjectFacetVersion 
    
{
    private IProjectFacet facet;
    private String version;
    private IConstraint constraint;
    private String plugin;
    private final HashMap delegates = new HashMap();
    
    public IProjectFacet getProjectFacet() 
    {
        return this.facet;
    }
    
    void setProjectFacet( final IProjectFacet facet )
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
    
    IDelegate getDelegate( final IDelegate.Type type )
    {
        Object delegate = this.delegates.get( type );
        
        if( delegate == null || delegate == ProjectFacetsManagerImpl.NOOP )
        {
            return null;
        }
        else if( delegate instanceof String )
        {
            final String clname = (String) delegate;
            delegate = create( clname );
            
            if( ! ( delegate instanceof IDelegate ) )
            {
                // TODO: Handle this better.
                throw new RuntimeException();
            }
            
            this.delegates.put( type, delegate );
        }
        
        return (IDelegate) delegate;
    }
    
    void setDelegate( final IDelegate.Type type,
                      final String delegateClassName )
    {
        this.delegates.put( type, delegateClassName );
    }
    
    public boolean supports( final Action.Type type )
    {
        return this.delegates.containsKey( IDelegate.Type.get( type ) );
    }
    
    private Object create( final String clname )
    {
        final Bundle bundle = Platform.getBundle( this.plugin );
        
        try
        {
            final Class cl = bundle.loadClass( clname );
            return cl.newInstance();
        }
        catch( Exception e )
        {
            // TODO: handle this.
            return null;
        }
    }
    
    public String toString()
    {
        return this.facet.getLabel() + " " + this.version;
    }

}
