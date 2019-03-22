/******************************************************************************
 * Copyright (c) 2005-2007 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *    Konstantin Komissarchik
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.tests;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public abstract class AbstractTests

    extends TestCase
    
{
    protected static final String DEFAULT_TEST_PROJECT_NAME = "testProject";
    protected static final IWorkspace ws = ResourcesPlugin.getWorkspace();
    protected final Set<IResource> resourcesToCleanup = new HashSet<IResource>();
    protected final List<Runnable> tearDownOperations = new ArrayList<Runnable>();
    
    protected AbstractTests( final String name )
    {
        super( name );
    }
    
    protected void tearDown()
        
        throws CoreException
        
    {
        for( IResource r : this.resourcesToCleanup )
        {
            r.delete( true, null );
        }
        
        for( Runnable runnable : this.tearDownOperations )
        {
            runnable.run();
        }
    }
    
    protected final void addResourceToCleanup( final IResource resource )
    {
        this.resourcesToCleanup.add( resource );
    }
    
    protected final void addTearDownOperation( final Runnable runnable )
    {
        this.tearDownOperations.add( runnable );
    }
    
    protected final IFacetedProject createFacetedProject()
    
        throws CoreException
        
    {
        return createFacetedProject( DEFAULT_TEST_PROJECT_NAME );
    }
    
    protected IFacetedProject createFacetedProject( final String name )

        throws CoreException
        
    {
        assertFalse( ws.getRoot().getProject( name ).exists() );
        final IFacetedProject fpj = ProjectFacetsManager.create( name, null, null );
        final IProject pj = fpj.getProject();
        assertTrue( pj.exists() );
        addResourceToCleanup( pj );
        
        return fpj;
    }
    
}
