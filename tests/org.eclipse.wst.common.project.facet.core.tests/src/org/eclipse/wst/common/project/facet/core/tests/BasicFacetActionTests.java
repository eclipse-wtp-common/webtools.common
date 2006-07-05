package org.eclipse.wst.common.project.facet.core.tests;

import java.io.IOException;
import java.util.Collections;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.tests.support.TestUtils;

public class BasicFacetActionTests

    extends TestCase
    
{
    private static final String TEST_PROJECT_NAME = "testProject";
    private static final IWorkspace ws = ResourcesPlugin.getWorkspace();
    
    private static final IFile facet1ArtifactFile
        = ws.getRoot().getProject( TEST_PROJECT_NAME ).getFile( "facet1.txt" );
    
    private static IProjectFacet f1;
    private static IProjectFacetVersion f1v10;
    private static IProjectFacetVersion f1v12;
    private static IProjectFacetVersion f1v121;
    private static IProjectFacetVersion f1v13;
    private static IProjectFacetVersion f1v20;

    static
    {
        try
        {
            f1 = ProjectFacetsManager.getProjectFacet( "facet1" );
            f1v10 = f1.getVersion( "1.0" );
            f1v12 = f1.getVersion( "1.2" );
            f1v121 = f1.getVersion( "1.2.1" );
            f1v13 = f1.getVersion( "1.3" );
            f1v20 = f1.getVersion( "2.0" );
        }
        catch( Exception e )
        {
            // Ignore failures. This api is tested explicitly.
        }
    }
    
    private IFacetedProject fpj;
    
    private BasicFacetActionTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "Basic Facet Action Tests" );

        suite.addTest( new BasicFacetActionTests( "testFacetInstall1" ) );
        suite.addTest( new BasicFacetActionTests( "testFacetInstall2" ) );
        suite.addTest( new BasicFacetActionTests( "testFacetInstall3" ) );
        suite.addTest( new BasicFacetActionTests( "testFacetUninstall1" ) );
        suite.addTest( new BasicFacetActionTests( "testFacetUninstall2" ) );
        suite.addTest( new BasicFacetActionTests( "testFacetUninstall3" ) );
        suite.addTest( new BasicFacetActionTests( "testFacetVersionChange1" ) );
        suite.addTest( new BasicFacetActionTests( "testFacetVersionChange2" ) );
        suite.addTest( new BasicFacetActionTests( "testActionSeries" ) );
        
        return suite;
    }
    
    protected void setUp()
    
        throws CoreException
        
    {
        assertFalse( ws.getRoot().getProject( TEST_PROJECT_NAME ).exists() );
        this.fpj = ProjectFacetsManager.create( TEST_PROJECT_NAME, null, null );
        assertTrue( fpj.getProject().exists() );
    }
    
    protected void tearDown()
    
        throws CoreException
        
    {
        this.fpj.getProject().delete( true, null );
    }
    
    public void testFacetInstall1()
    
        throws CoreException, IOException
        
    {
        this.fpj.installProjectFacet( f1v10, null, null );
        TestUtils.assertEquals( facet1ArtifactFile, f1v10.getVersionString() );
    }
    
    public void testFacetInstall2()
    
        throws CoreException, IOException
        
    {
        this.fpj.installProjectFacet( f1v121, null, null );
        TestUtils.assertEquals( facet1ArtifactFile, f1v121.getVersionString() );
    }
    
    public void testFacetInstall3()
    
        throws CoreException, IOException
        
    {
        final Action action = new Action( Action.Type.INSTALL, f1v20, null );
        this.fpj.modify( Collections.singleton( action ), null );
        TestUtils.assertEquals( facet1ArtifactFile, f1v20.getVersionString() );
    }
    
    public void testFacetUninstall1()
    
        throws CoreException, IOException
        
    {
        this.fpj.installProjectFacet( f1v10, null, null );
        this.fpj.uninstallProjectFacet( f1v10, null, null );
        assertFalse( facet1ArtifactFile.exists() );
    }

    public void testFacetUninstall2()
    
        throws CoreException, IOException
        
    {
        this.fpj.installProjectFacet( f1v121, null, null );
        this.fpj.uninstallProjectFacet( f1v121, null, null );
        assertFalse( facet1ArtifactFile.exists() );
    }

    public void testFacetUninstall3()
    
        throws CoreException, IOException
        
    {
        this.fpj.installProjectFacet( f1v20, null, null );
        this.fpj.uninstallProjectFacet( f1v20, null, null );
        assertFalse( facet1ArtifactFile.exists() );
    }
    
    public void testFacetVersionChange1()
    
        throws CoreException, IOException
        
    {
        this.fpj.installProjectFacet( f1v10, null, null );
        final Action action = new Action( Action.Type.VERSION_CHANGE, f1v121, null );
        this.fpj.modify( Collections.singleton( action ), null );
        TestUtils.assertEquals( facet1ArtifactFile, f1v121.getVersionString() );
    }

    public void testFacetVersionChange2()
    
        throws CoreException, IOException
        
    {
        this.fpj.installProjectFacet( f1v13, null, null );
        final Action action = new Action( Action.Type.VERSION_CHANGE, f1v12, null );
        this.fpj.modify( Collections.singleton( action ), null );
        TestUtils.assertEquals( facet1ArtifactFile, f1v12.getVersionString() );
    }
    
    public void testActionSeries()
    
        throws CoreException, IOException
    
    {
        Action action;
        
        action = new Action( Action.Type.INSTALL, f1v10, null );
        this.fpj.modify( Collections.singleton( action ), null );
        TestUtils.assertEquals( facet1ArtifactFile, f1v10.getVersionString() );
        
        action = new Action( Action.Type.VERSION_CHANGE, f1v12, null );
        this.fpj.modify( Collections.singleton( action ), null );
        TestUtils.assertEquals( facet1ArtifactFile, f1v12.getVersionString() );

        action = new Action( Action.Type.VERSION_CHANGE, f1v121, null );
        this.fpj.modify( Collections.singleton( action ), null );
        TestUtils.assertEquals( facet1ArtifactFile, f1v121.getVersionString() );

        action = new Action( Action.Type.VERSION_CHANGE, f1v13, null );
        this.fpj.modify( Collections.singleton( action ), null );
        TestUtils.assertEquals( facet1ArtifactFile, f1v13.getVersionString() );
        
        action = new Action( Action.Type.VERSION_CHANGE, f1v20, null );
        this.fpj.modify( Collections.singleton( action ), null );
        TestUtils.assertEquals( facet1ArtifactFile, f1v20.getVersionString() );
        
        action = new Action( Action.Type.UNINSTALL, f1v20, null );
        this.fpj.modify( Collections.singleton( action ), null );
        assertFalse( facet1ArtifactFile.exists() );

        action = new Action( Action.Type.INSTALL, f1v12, null );
        this.fpj.modify( Collections.singleton( action ), null );
        TestUtils.assertEquals( facet1ArtifactFile, f1v12.getVersionString() );
        
        action = new Action( Action.Type.VERSION_CHANGE, f1v13, null );
        this.fpj.modify( Collections.singleton( action ), null );
        TestUtils.assertEquals( facet1ArtifactFile, f1v13.getVersionString() );
        
        action = new Action( Action.Type.VERSION_CHANGE, f1v10, null );
        this.fpj.modify( Collections.singleton( action ), null );
        TestUtils.assertEquals( facet1ArtifactFile, f1v10.getVersionString() );
        
        action = new Action( Action.Type.VERSION_CHANGE, f1v20, null );
        this.fpj.modify( Collections.singleton( action ), null );
        TestUtils.assertEquals( facet1ArtifactFile, f1v20.getVersionString() );
        
        action = new Action( Action.Type.VERSION_CHANGE, f1v121, null );
        this.fpj.modify( Collections.singleton( action ), null );
        TestUtils.assertEquals( facet1ArtifactFile, f1v121.getVersionString() );
        
        action = new Action( Action.Type.UNINSTALL, f1v121, null );
        this.fpj.modify( Collections.singleton( action ), null );
        assertFalse( facet1ArtifactFile.exists() );
    }
    
}
