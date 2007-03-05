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

import static org.eclipse.wst.common.project.facet.core.tests.support.TestUtils.asSet;

import java.util.Collections;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public class FacetConstraintsTests

    extends TestCase
    
{
    private static IProjectFacet f1;
    private static IProjectFacetVersion f1v10;
    
    private static IProjectFacet f3;
    private static IProjectFacetVersion f3v10;

    private static IProjectFacet f4;
    private static IProjectFacetVersion f4v10;

    private static IProjectFacet f5;
    private static IProjectFacetVersion f5v10;
    
    private static IProjectFacet f6;
    private static IProjectFacetVersion f6v10;
    private static IProjectFacetVersion f6v23;
    private static IProjectFacetVersion f6v37;
    private static IProjectFacetVersion f6v40;
    private static IProjectFacetVersion f6v45;

    private static IProjectFacet f7;
    private static IProjectFacetVersion f7v10;
    private static IProjectFacetVersion f7v20;

    private static IProjectFacet f8;
    private static IProjectFacetVersion f8v10;
    private static IProjectFacetVersion f8v20;
    private static IProjectFacetVersion f8v30;
    
    private static IProjectFacet f9;
    private static IProjectFacetVersion f9v10;
    
    private static IProjectFacet f10;
    private static IProjectFacetVersion f10v10;
    private static IProjectFacetVersion f10v20;
    
    static
    {
        f1 = ProjectFacetsManager.getProjectFacet( "fct_f1" );
        f1v10 = f1.getVersion( "1.0" );

        f3 = ProjectFacetsManager.getProjectFacet( "fct_f3" );
        f3v10 = f3.getVersion( "1.0" );

        f4 = ProjectFacetsManager.getProjectFacet( "fct_f4" );
        f4v10 = f4.getVersion( "1.0" );

        f5 = ProjectFacetsManager.getProjectFacet( "fct_f5" );
        f5v10 = f5.getVersion( "1.0" );

        f6 = ProjectFacetsManager.getProjectFacet( "fct_f6" );
        f6v10 = f6.getVersion( "1.0" );
        f6v23 = f6.getVersion( "2.3" );
        f6v37 = f6.getVersion( "3.7" );
        f6v40 = f6.getVersion( "4.0" );
        f6v45 = f6.getVersion( "4.5" );

        f7 = ProjectFacetsManager.getProjectFacet( "fct_f7" );
        f7v10 = f7.getVersion( "1.0" );
        f7v20 = f7.getVersion( "2.0" );

        f8 = ProjectFacetsManager.getProjectFacet( "fct_f8" );
        f8v10 = f8.getVersion( "1.0" );
        f8v20 = f8.getVersion( "2.0" );
        f8v30 = f8.getVersion( "3.0" );

        f9 = ProjectFacetsManager.getProjectFacet( "fct_f9" );
        f9v10 = f9.getVersion( "1.0" );

        f10 = ProjectFacetsManager.getProjectFacet( "fct_f10" );
        f10v10 = f10.getVersion( "1.0" );
        f10v20 = f10.getVersion( "2.0" );
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
        suite.addTest( new FacetConstraintsTests( "testRequiresWithNoVersion" ) );
        suite.addTest( new FacetConstraintsTests( "testRequiresWithUnknownVersion" ) );
        suite.addTest( new FacetConstraintsTests( "testGroupRequires" ) );
        suite.addTest( new FacetConstraintsTests( "testGroupRequiresSoft" ) );
        
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
    
    /*
     * Tests the following constraint:
     * 
     *   <requires facet="fct_f6"/>
     */
    
    @SuppressWarnings( "unchecked" )
    public void testRequiresWithNoVersion()
    {
        assertFalse( f7v10.getConstraint().check( Collections.EMPTY_SET ).isOK() );
        assertTrue( f7v10.getConstraint().check( asSet( f6v10 ) ).isOK() );
        assertTrue( f7v10.getConstraint().check( asSet( f6v23 ) ).isOK() );
        assertTrue( f7v10.getConstraint().check( asSet( f6v37 ) ).isOK() );
        assertTrue( f7v10.getConstraint().check( asSet( f6v40 ) ).isOK() );
        assertTrue( f7v10.getConstraint().check( asSet( f6v45 ) ).isOK() );
    }
    
    /*
     * Tests the following constraint that specifies versions that don't exist:
     * 
     *   <requires facet="fct_f6" version="[5.0-6.5)"/>
     */
    
    @SuppressWarnings( "unchecked" )
    public void testRequiresWithUnknownVersion()
    {
        assertFalse( f7v20.getConstraint().check( Collections.EMPTY_SET ).isOK() );
        assertFalse( f7v20.getConstraint().check( asSet( f6v10 ) ).isOK() );
        assertFalse( f7v20.getConstraint().check( asSet( f6v23 ) ).isOK() );
        assertFalse( f7v20.getConstraint().check( asSet( f6v37 ) ).isOK() );
        assertFalse( f7v20.getConstraint().check( asSet( f6v40 ) ).isOK() );
        assertFalse( f7v20.getConstraint().check( asSet( f6v45 ) ).isOK() );
    }
    
    /*
     * Tests the "requires any group member" constraint:
     * 
     *   <requires group="fct_g1"/>
     */
    
    @SuppressWarnings( "unchecked" )
    public void testGroupRequires()
    {
        assertFalse( f10v10.getConstraint().check( Collections.EMPTY_SET ).isOK() );
        assertTrue( f10v10.getConstraint().check( asSet( f8v10 ) ).isOK() );
        assertTrue( f10v10.getConstraint().check( asSet( f8v20 ) ).isOK() );
        assertFalse( f10v10.getConstraint().check( asSet( f8v30 ) ).isOK() );
        assertTrue( f10v10.getConstraint().check( asSet( f9v10 ) ).isOK() );
        assertFalse( f10v10.getConstraint().check( asSet( f1v10 ) ).isOK() );
    }

    /*
     * Tests the soft version of the "requires any group member" constraint:
     * 
     *   <requires group="fct_g1" soft="true"/>
     */
    
    @SuppressWarnings( "unchecked" )
    public void testGroupRequiresSoft()
    {
        assertTrue( f10v20.getConstraint().check( Collections.EMPTY_SET ).isOK() );
        assertTrue( f10v20.getConstraint().check( asSet( f8v10 ) ).isOK() );
        assertTrue( f10v20.getConstraint().check( asSet( f8v20 ) ).isOK() );
        assertTrue( f10v20.getConstraint().check( asSet( f8v30 ) ).isOK() );
        assertTrue( f10v20.getConstraint().check( asSet( f9v10 ) ).isOK() );
        assertTrue( f10v20.getConstraint().check( asSet( f1v10 ) ).isOK() );
    }

}
