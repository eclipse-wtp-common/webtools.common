package org.eclipse.wst.common.project.facet.core.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.tests.support.TestUtils;

public class FacetConstraintsTests

    extends TestCase
    
{
    private static IProjectFacet f1;
    private static IProjectFacetVersion f1v10;
    
    private static IProjectFacet f2;
    private static IProjectFacetVersion f2v10;
    
    private static IProjectFacet f3;
    private static IProjectFacetVersion f3v10;

    private static IProjectFacet f4;
    private static IProjectFacetVersion f4v10;

    private static IProjectFacet f5;
    private static IProjectFacetVersion f5v10;
    
    static
    {
        f1 = ProjectFacetsManager.getProjectFacet( "fct_f1" );
        f1v10 = f1.getVersion( "1.0" );

        f2 = ProjectFacetsManager.getProjectFacet( "fct_f2" );
        f2v10 = f2.getVersion( "1.0" );

        f3 = ProjectFacetsManager.getProjectFacet( "fct_f3" );
        f3v10 = f3.getVersion( "1.0" );

        f4 = ProjectFacetsManager.getProjectFacet( "fct_f4" );
        f4v10 = f4.getVersion( "1.0" );

        f5 = ProjectFacetsManager.getProjectFacet( "fct_f5" );
        f5v10 = f5.getVersion( "1.0" );
    }
    
    private FacetConstraintsTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "Facet Constraint Tests" );

        suite.addTest( new FacetConstraintsTests( "testIndirectConflict1" ) );
        suite.addTest( new FacetConstraintsTests( "testIndirectConflict2" ) );
        suite.addTest( new FacetConstraintsTests( "testIndirectConflict3" ) );
        suite.addTest( new FacetConstraintsTests( "testIndirectConflict4" ) );
        suite.addTest( new FacetConstraintsTests( "testIndirectConflict5" ) );
        suite.addTest( new FacetConstraintsTests( "testIndirectConflict6" ) );
        suite.addTest( new FacetConstraintsTests( "testIndirectConflict7" ) );
        suite.addTest( new FacetConstraintsTests( "testIndirectConflict8" ) );
        
        return suite;
    }
    
    /*
     * Tests whether the conflict detection code picks up on an indirect
     * conflict. Also tests that a soft constraint is not used to flag a
     * conflict.
     * 
     * Here is the relationship diagram between the five facets involved in 
     * this test: 
     * 
     *           conflicts            requires
     *   f1 ----------------> f2 <---------------- f3
     *   f4 <----------------    <---------------- f5
     *           conflicts         soft requires
     * 
     * These case should come back positive for conflict:
     * 
     *   f1 with f3
     *   f3 with f1
     *   f4 with f3
     *   f3 with f4
     *   
     * These case should come back negative for conflict:
     * 
     *   f1 with f5
     *   f5 with f1
     *   f4 with f5
     *   f5 with f4
     */
    
    public void testIndirectConflict1()
    {
        assertTrue( f1v10.conflictsWith( f3v10 ) );
    }
    
    public void testIndirectConflict2()
    {
        assertTrue( f3v10.conflictsWith( f1v10 ) );
    }

    public void testIndirectConflict3()
    {
        assertTrue( f4v10.conflictsWith( f3v10 ) );
    }

    public void testIndirectConflict4()
    {
        assertTrue( f3v10.conflictsWith( f4v10 ) );
    }
    
    public void testIndirectConflict5()
    {
        assertFalse( f1v10.conflictsWith( f5v10 ) );
    }
    
    public void testIndirectConflict6()
    {
        assertFalse( f5v10.conflictsWith( f1v10 ) );
    }

    public void testIndirectConflict7()
    {
        assertFalse( f4v10.conflictsWith( f5v10 ) );
    }

    public void testIndirectConflict8()
    {
        assertFalse( f5v10.conflictsWith( f4v10 ) );
    }
    
}
