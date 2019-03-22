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

import static org.eclipse.wst.common.project.facet.core.tests.support.TestUtils.readFromFile;
import static org.eclipse.wst.common.project.facet.core.tests.support.TestUtils.writeToFile;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.FacetedProjectFramework;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.internal.FacetedProject;
import org.eclipse.wst.common.project.facet.core.tests.support.TestUtils;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class ProjectCreationTests

    extends AbstractTests
    
{
    private static final String FACETED_PROJECT_NATURE
        = "org.eclipse.wst.common.project.facet.core.nature";
    
    private static IProjectFacet f1;
    private static IProjectFacetVersion f1v1;

    private static IProjectFacet f2;
    private static IProjectFacetVersion f2v1;
    private static IProjectFacetVersion f2v2;
    
    static
    {
        f1 = ProjectFacetsManager.getProjectFacet( "pct-f1" );
        f1v1 = f1.getVersion( "1.0" );

        f2 = ProjectFacetsManager.getProjectFacet( "pct-f2" );
        f2v1 = f2.getVersion( "1.0" );
        f2v2 = f2.getVersion( "2.0" );
    }

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
        suite.addTest( new ProjectCreationTests( "testCreationFromScratch4" ) );
        suite.addTest( new ProjectCreationTests( "testCreationFromNonFacetedProject1" ) );
        suite.addTest( new ProjectCreationTests( "testCreationFromNonFacetedProject2" ) );
        suite.addTest( new ProjectCreationTests( "testCreationFromNonFacetedProject3" ) );
        suite.addTest( new ProjectCreationTests( "testCreationFromNonFacetedProject4" ) );
        suite.addTest( new ProjectCreationTests( "testWrapperCreation1" ) );
        suite.addTest( new ProjectCreationTests( "testWrapperCreation2" ) );
        suite.addTest( new ProjectCreationTests( "testWrapperCreation3" ) );
        suite.addTest( new ProjectCreationTests( "testHasProjectFacet1" ) );
        suite.addTest( new ProjectCreationTests( "testHasProjectFacet2" ) );
        
        return suite;
    }

    /**
     * Tests {@link ProjectFacetsManager.create(String,IPath,IProgressMonitor)}
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
     * Tests {@link ProjectFacetsManager.create(String,IPath,IProgressMonitor)}
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
     * Tests {@link ProjectFacetsManager.create(String,IPath,IProgressMonitor)}
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
        project.open( null );
        
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
     * Tests {@link ProjectFacetsManager.create(String,IPath,IProgressMonitor)} method. In this 
     * scenario, there was previously a faceted project at the specified location. The test 
     * verifies that previously-installed facets are recognized after the project is resurrected.
     * 
     * @throws CoreException
     * @throws IOException
     */
    
    public void testCreationFromScratch4()
    
        throws CoreException, IOException
    
    {
        IFacetedProject fproj;
        
        fproj = ProjectFacetsManager.create( "abc", null, null );
        this.resourcesToCleanup.add( fproj.getProject() );
        
        fproj.installProjectFacet( f1v1, null, null );
        fproj.installProjectFacet( f2v1, null, null );
        
        fproj.getProject().delete( false, false, null );
        
        fproj = ProjectFacetsManager.create( "abc", null, null );
        
        assertEquals( fproj.getFixedProjectFacets().size(), 0 );
        assertEquals( fproj.getProjectFacets().size(), 2 );
        assertTrue( fproj.hasProjectFacet( f1v1 ) );
        assertTrue( fproj.hasProjectFacet( f2v1 ) );
        assertEquals( fproj.getTargetedRuntimes().size(), 0 );
        assertNull( fproj.getPrimaryRuntime() );
    }
    
    /**
     * Tests {@link ProjectFacetsManager.create(IProject,boolean,IProgressMonitor)}
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
        project.open( null );
        
        this.resourcesToCleanup.add( project );
        
        final IFacetedProject fproj
            = ProjectFacetsManager.create( project, true, null );
        
        assertEquals( fproj.getFixedProjectFacets().size(), 0 );
        assertEquals( fproj.getProjectFacets().size(), 0 );
        assertEquals( fproj.getTargetedRuntimes().size(), 0 );
        assertNull( fproj.getPrimaryRuntime() );
    }

    /**
     * Tests {@link ProjectFacetsManager.create(IProject,boolean,IProgressMonitor)}
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
     * Tests {@link ProjectFacetsManager.create(IProject,boolean,IProgressMonitor)}
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
     * Tests {@link ProjectFacetsManager.create(IProject,boolean,IProgressMonitor)}
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
        project.open( null );
        
        this.resourcesToCleanup.add( project );
        
        assertNull( ProjectFacetsManager.create( project, false, null ) );
    }
    
    /**
     * Tests {@link ProjectFacetsManager.create(IProject)} method. This scenario
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
     * Tests {@link ProjectFacetsManager.create(IProject)} method. In this
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
     * Tests {@link ProjectFacetsManager.create(IProject)} method. In this
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
    
    /**
     * Tests the various methods that check whether a particular facet is present in a project:<br/><br/>
     * 
     * {@link IFacetedProject.hasProjecFacet(IProjectFacet)}<br/>
     * {@link IFacetedProject.hasProjecFacet(IProjectFacet)}<br/>
     * {@link FacetedProjectFramework.hasProjectFacet(IProject,String,String)}</br><br/>
     * 
     * In this scenario, the facets being tested are known to the framework.
     * 
     * @throws CoreException
     */
    
    public void testHasProjectFacet1()
    
        throws CoreException
        
    {
        final IFacetedProject fproj 
            = ProjectFacetsManager.create( "abc", null, null );
        
        final IProject proj = fproj.getProject();
        this.resourcesToCleanup.add( proj );
        
        fproj.installProjectFacet( f1v1, null, null );
        fproj.installProjectFacet( f2v1, null, null );
        
        assertTrue( fproj.hasProjectFacet( f1 ) );
        assertTrue( fproj.hasProjectFacet( f1v1 ) );

        assertTrue( fproj.hasProjectFacet( f2 ) );
        assertTrue( fproj.hasProjectFacet( f2v1 ) );
        assertFalse( fproj.hasProjectFacet( f2v2 ) );
        
        assertTrue( FacetedProjectFramework.hasProjectFacet( proj, f1.getId() ) );
        assertTrue( FacetedProjectFramework.hasProjectFacet( proj, f1.getId(), null ) );
        assertTrue( FacetedProjectFramework.hasProjectFacet( proj, f1.getId(), "1.0" ) );
        assertTrue( FacetedProjectFramework.hasProjectFacet( proj, f1.getId(), "[0.5-3.7]" ) );
        assertFalse( FacetedProjectFramework.hasProjectFacet( proj, f1.getId(), "2.3" ) );
        assertFalse( FacetedProjectFramework.hasProjectFacet( proj, f1.getId(), "[2.3-5.8)" ) );
        
        assertTrue( FacetedProjectFramework.hasProjectFacet( proj, f2.getId() ) );
        assertTrue( FacetedProjectFramework.hasProjectFacet( proj, f2.getId(), null ) );
        assertTrue( FacetedProjectFramework.hasProjectFacet( proj, f2.getId(), "1.0" ) );
        assertTrue( FacetedProjectFramework.hasProjectFacet( proj, f2.getId(), "[0.5-3.7]" ) );
        assertFalse( FacetedProjectFramework.hasProjectFacet( proj, f2.getId(), "2.3" ) );
        assertFalse( FacetedProjectFramework.hasProjectFacet( proj, f2.getId(), "[2.3-5.8)" ) );
    }
    
    /**
     * Tests the various methods that check whether a particular facet is present in a project:<br/><br/>
     * 
     * {@link FacetedProjectFramework.hasProjectFacet(IProject,String,String)}</br><br/>
     * 
     * In this scenario, the facets being tested are not known to the framework. This comes up as part of
     * upgrade and transition scenarios where facets previously installed are no longer defined.
     * 
     * @throws CoreException
     */
    
    public void testHasProjectFacet2()
    
        throws CoreException, IOException
        
    {
        final IFacetedProject fproj 
            = ProjectFacetsManager.create( "abc", null, null );
        
        final IProject proj = fproj.getProject();
        this.resourcesToCleanup.add( proj );
        
        fproj.installProjectFacet( f1v1, null, null );
        fproj.installProjectFacet( f2v1, null, null );
        
        final IFile fpjMdFile = proj.getFile( FacetedProject.METADATA_FILE );
        
        String fpjMdFileContents = readFromFile( fpjMdFile );
        fpjMdFileContents = fpjMdFileContents.replace( "pct-f1", "foo" );
        fpjMdFileContents = fpjMdFileContents.replace( "<installed facet=\"pct-f2\" version=\"1.0\"/>", "<installed facet=\"pct-f2\" version=\"1.1\"/>" );
        writeToFile( fpjMdFile, fpjMdFileContents );
        
        assertTrue( FacetedProjectFramework.hasProjectFacet( proj, "foo" ) );
        assertTrue( FacetedProjectFramework.hasProjectFacet( proj, "foo", null ) );
        assertTrue( FacetedProjectFramework.hasProjectFacet( proj, "foo", "1.0" ) );
        assertTrue( FacetedProjectFramework.hasProjectFacet( proj, "foo", "[0.5-3.7]" ) );
        assertFalse( FacetedProjectFramework.hasProjectFacet( proj, "foo", "2.3" ) );
        assertFalse( FacetedProjectFramework.hasProjectFacet( proj, "foo", "[2.3-5.8)" ) );
        
        assertTrue( FacetedProjectFramework.hasProjectFacet( proj, f2.getId() ) );
        assertTrue( FacetedProjectFramework.hasProjectFacet( proj, f2.getId(), null ) );
        assertTrue( FacetedProjectFramework.hasProjectFacet( proj, f2.getId(), "1.1" ) );
        assertTrue( FacetedProjectFramework.hasProjectFacet( proj, f2.getId(), "[0.5-3.7]" ) );
        assertFalse( FacetedProjectFramework.hasProjectFacet( proj, f2.getId(), "2.3" ) );
        assertFalse( FacetedProjectFramework.hasProjectFacet( proj, f2.getId(), "[2.3-5.8)" ) );

        assertFalse( FacetedProjectFramework.hasProjectFacet( proj, "abc" ) );
        assertFalse( FacetedProjectFramework.hasProjectFacet( proj, "abc", null ) );
    }
}
