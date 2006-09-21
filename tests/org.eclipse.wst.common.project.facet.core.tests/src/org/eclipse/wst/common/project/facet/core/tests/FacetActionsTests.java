package org.eclipse.wst.common.project.facet.core.tests;

import java.io.ByteArrayInputStream;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

public class FacetActionsTests

    extends AbstractTests
    
{
    private static IProjectFacet f1;
    private static IProjectFacetVersion f1v1;
    private static IProjectFacetVersion f1v2;
    
    static
    {
        f1 = ProjectFacetsManager.getProjectFacet( "fat_f1" );
        f1v1 = f1.getVersion( "1.0" );
        f1v2 = f1.getVersion( "2.0" );
    }
    
    private FacetActionsTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "Facet Actions Tests" );

        suite.addTest( new FacetActionsTests( "testMultiVersionInstall1" ) );
        suite.addTest( new FacetActionsTests( "testMultiVersionInstall2" ) );
        
        return suite;
    }
    
    /*
     * The following test cases test the scenario where different 
     * implementations of the INSTALL action are provided for two versions of 
     * the same facet.
     * 
     * Scenario Test Code:
     * 
     *   testMultiVersionInstall1
     *   testMultiVersionInstall2
     *   F1v1InstallDelegate
     *   F1v2InstallDelegate
     */
    
    public void testMultiVersionInstall1()
    
        throws CoreException
        
    {
        final IFacetedProject fpj = createFacetedProject();
        final IProject pj = fpj.getProject();
        fpj.installProjectFacet( f1v1, null, null );
        
        assertTrue( F1v1InstallDelegate.getMarkerFile( pj ).exists() );
        assertFalse( F1v2InstallDelegate.getMarkerFile( pj ).exists() );
    }

    public void testMultiVersionInstall2()
    
        throws CoreException
        
    {
        final IFacetedProject fpj = createFacetedProject();
        final IProject pj = fpj.getProject();
        fpj.installProjectFacet( f1v2, null, null );
        
        assertFalse( F1v1InstallDelegate.getMarkerFile( pj ).exists() );
        assertTrue( F1v2InstallDelegate.getMarkerFile( pj ).exists() );
    }
    
    public static final class F1v1InstallDelegate
    
        implements IDelegate
        
    {
        public static IFile getMarkerFile( final IProject project )
        {
            return project.getFile( "v1marker" );
        }
        
        public void execute( final IProject project,
                             final IProjectFacetVersion fv,
                             final Object config,
                             final IProgressMonitor monitor ) 
        
            throws CoreException
            
        {
            final IFile marker = getMarkerFile( project );
            
            final ByteArrayInputStream emptyStream 
                = new ByteArrayInputStream( new byte[ 0 ] );
            
            marker.create( emptyStream, false, null );
        }
    }

    public static final class F1v2InstallDelegate
    
        implements IDelegate
        
    {
        public static IFile getMarkerFile( final IProject project )
        {
            return project.getFile( "v2marker" );
        }
        
        public void execute( final IProject project,
                             final IProjectFacetVersion fv,
                             final Object config,
                             final IProgressMonitor monitor ) 
        
            throws CoreException
            
        {
            final IFile marker = getMarkerFile( project );
            
            final ByteArrayInputStream emptyStream 
                = new ByteArrayInputStream( new byte[ 0 ] );
            
            marker.create( emptyStream, false, null );
        }
    }
    
}
