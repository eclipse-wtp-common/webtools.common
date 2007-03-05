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

package org.eclipse.wst.common.project.facet.core.tests;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.tests.support.TestUtils;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class ProjectCreationTests

    extends AbstractTests
    
{
    private static final String FACETED_PROJECT_NATURE
        = "org.eclipse.wst.common.project.facet.core.nature";
    
    private ProjectCreationTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "Project Creation Tests" );

        suite.addTest( new ProjectCreationTests( "testCreationFromScratch1" ) );
        suite.addTest( new ProjectCreationTests( "testCreationFromScratch2" ) );
        suite.addTest( new ProjectCreationTests( "testCreationFromScratch3" ) );
        suite.addTest( new ProjectCreationTests( "testCreationFromNonFacetedProject1" ) );
        suite.addTest( new ProjectCreationTests( "testCreationFromNonFacetedProject2" ) );
        suite.addTest( new ProjectCreationTests( "testCreationFromNonFacetedProject3" ) );
        suite.addTest( new ProjectCreationTests( "testCreationFromNonFacetedProject4" ) );
        suite.addTest( new ProjectCreationTests( "testWrapperCreation1" ) );
        suite.addTest( new ProjectCreationTests( "testWrapperCreation2" ) );
        suite.addTest( new ProjectCreationTests( "testWrapperCreation3" ) );
        
        return suite;
    }

    /**
     * Tests {@see ProjectFacetsManager.create(String,IPath,IProgressMonitor)}
     * method. In this scenario, there is no project with the same name.
     * 
     * @throws CoreException
     * @throws IOException
     */
    
    public void testCreationFromScratch1()
    
        throws CoreException, IOException
        
    {
        final IFacetedProject fproj 
            = ProjectFacetsManager.create( "abc", null, null );
        
        final IProject proj = fproj.getProject();
        
        assertNotNull( proj );
        assertTrue( proj.exists() );
        
        this.resourcesToCleanup.add( proj );
        
        TestUtils.assertFileContains( proj.getFile( ".project" ), FACETED_PROJECT_NATURE );
        
        assertEquals( fproj.getFixedProjectFacets().size(), 0 );
        assertEquals( fproj.getProjectFacets().size(), 0 );
        assertEquals( fproj.getTargetedRuntimes().size(), 0 );
        assertNull( fproj.getPrimaryRuntime() );
    }
    
    /**
     * Tests {@see ProjectFacetsManager.create(String,IPath,IProgressMonitor)}
     * method. In this scenario, there is a faceted project with the same name.
     * 
     * @throws CoreException
     * @throws IOException
     */

    public void testCreationFromScratch2()
    
        throws CoreException, IOException
    
    {
        final IFacetedProject fproj 
            = ProjectFacetsManager.create( "abc", null, null );
        
        this.resourcesToCleanup.add( fproj.getProject() );
        
        try
        {
            ProjectFacetsManager.create( "abc", null, null );
            fail();
        }
        catch( CoreException e )
        {
            // expected
        }
    }

    /**
     * Tests {@see ProjectFacetsManager.create(String,IPath,IProgressMonitor)}
     * method. In this scenario, there is a non-faceted project with the same
     * name.
     * 
     * @throws CoreException
     * @throws IOException
     */
    
    public void testCreationFromScratch3()
    
        throws CoreException, IOException
    
    {
        final IProject project = ws.getRoot().getProject( "abc" );
        final IProjectDescription desc = ws.newProjectDescription( "abc" );

        desc.setLocation( null );
                
        project.create( desc, null );
        project.open( IResource.BACKGROUND_REFRESH, null );
        
        this.resourcesToCleanup.add( project );
        
        try
        {
            ProjectFacetsManager.create( "abc", null, null );
            fail();
        }
        catch( CoreException e )
        {
            // expected
        }
    }
    
    /**
     * Tests {@see ProjectFacetsManager.create(IProject,boolean,IProgressMonitor)}
     * method. In this scenario project is not faceted and convertIfNecessary
     * is set to true.
     * 
     * @throws CoreException
     * @throws IOException
     */
    
    public void testCreationFromNonFacetedProject1()
    
        throws CoreException, IOException
    
    {
        final IProject project = ws.getRoot().getProject( "abc" );
        final IProjectDescription desc = ws.newProjectDescription( "abc" );
    
        desc.setLocation( null );
                
        project.create( desc, null );
        project.open( IResource.BACKGROUND_REFRESH, null );
        
        this.resourcesToCleanup.add( project );
        
        final IFacetedProject fproj
            = ProjectFacetsManager.create( project, true, null );
        
        assertEquals( fproj.getFixedProjectFacets().size(), 0 );
        assertEquals( fproj.getProjectFacets().size(), 0 );
        assertEquals( fproj.getTargetedRuntimes().size(), 0 );
        assertNull( fproj.getPrimaryRuntime() );
    }

    /**
     * Tests {@see ProjectFacetsManager.create(IProject,boolean,IProgressMonitor)}
     * method. In this scenario project is faceted and convertIfNecessary
     * is set to true.
     * 
     * @throws CoreException
     * @throws IOException
     */
    
    public void testCreationFromNonFacetedProject2()
    
        throws CoreException, IOException
    
    {
        final IFacetedProject prior 
            = ProjectFacetsManager.create( "abc", null, null );
        
        final IProject project = prior.getProject();
        
        this.resourcesToCleanup.add( project );
        
        final IFacetedProject fproj
            = ProjectFacetsManager.create( project, true, null );
        
        assertEquals( fproj.getFixedProjectFacets().size(), 0 );
        assertEquals( fproj.getProjectFacets().size(), 0 );
        assertEquals( fproj.getTargetedRuntimes().size(), 0 );
        assertNull( fproj.getPrimaryRuntime() );
    }
    
    /**
     * Tests {@see ProjectFacetsManager.create(IProject,boolean,IProgressMonitor)}
     * method. In this scenario project is faceted and convertIfNecessary
     * is set to false.
     * 
     * @throws CoreException
     * @throws IOException
     */
    
    public void testCreationFromNonFacetedProject3()
    
        throws CoreException, IOException
    
    {
        final IFacetedProject prior 
            = ProjectFacetsManager.create( "abc", null, null );
        
        final IProject project = prior.getProject();
        
        this.resourcesToCleanup.add( project );
        
        final IFacetedProject fproj
            = ProjectFacetsManager.create( project, false, null );
        
        assertEquals( fproj.getFixedProjectFacets().size(), 0 );
        assertEquals( fproj.getProjectFacets().size(), 0 );
        assertEquals( fproj.getTargetedRuntimes().size(), 0 );
        assertNull( fproj.getPrimaryRuntime() );
    }
    
    /**
     * Tests {@see ProjectFacetsManager.create(IProject,boolean,IProgressMonitor)}
     * method. In this scenario project is not faceted and convertIfNecessary
     * is set to false.
     * 
     * @throws CoreException
     * @throws IOException
     */
    
    public void testCreationFromNonFacetedProject4()
    
        throws CoreException, IOException
    
    {
        final IProject project = ws.getRoot().getProject( "abc" );
        final IProjectDescription desc = ws.newProjectDescription( "abc" );
    
        desc.setLocation( null );
                
        project.create( desc, null );
        project.open( IResource.BACKGROUND_REFRESH, null );
        
        this.resourcesToCleanup.add( project );
        
        assertNull( ProjectFacetsManager.create( project, false, null ) );
    }
    
    /**
     * Tests {@see ProjectFacetsManager.create(IProject)} method. This scenario
     * validates that the wrapper cache is working and the same instance is
     * returned when the create method is called multiple times.
     * 
     * @throws CoreException
     */
    
    public void testWrapperCreation1()
    
        throws CoreException
        
    {
        final IFacetedProject fproj 
            = ProjectFacetsManager.create( "abc", null, null );
        
        final IProject proj = fproj.getProject();
        this.resourcesToCleanup.add( proj );
        
        assertTrue( fproj == ProjectFacetsManager.create( proj ) );
        assertTrue( fproj == ProjectFacetsManager.create( proj ) );
        assertTrue( fproj == ProjectFacetsManager.create( proj ) );
        assertTrue( fproj == ProjectFacetsManager.create( proj ) );
        assertTrue( fproj == ProjectFacetsManager.create( proj ) );
    }

    /**
     * Tests {@see ProjectFacetsManager.create(IProject)} method. In this
     * scenario, the input project does not exist.
     * 
     * @throws CoreException
     */
    
    public void testWrapperCreation2()
    
        throws CoreException
        
    {
        final IProject proj = ws.getRoot().getProject( "abc" );
        assertNull( ProjectFacetsManager.create( proj ) );
    }

    /**
     * Tests {@see ProjectFacetsManager.create(IProject)} method. In this
     * scenario, the input project is closed.
     * 
     * @throws CoreException
     */
    
    public void testWrapperCreation3()
    
        throws CoreException
        
    {
        final IFacetedProject fproj 
            = ProjectFacetsManager.create( "abc", null, null );
        
        final IProject proj = fproj.getProject();
        this.resourcesToCleanup.add( proj );
        
        proj.close( null );
        
        assertNull( ProjectFacetsManager.create( proj ) );
    }
    
}
