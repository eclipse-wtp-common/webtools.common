package org.eclipse.wst.common.project.facet.core.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests

    extends TestCase
    
{
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "All Faceted Project Framework Tests" );
        
        suite.addTest( BasicTests.suite() );
        suite.addTest( ProjectCreationTests.suite() );
        suite.addTest( BasicFacetActionTests.suite() );
        suite.addTest( FacetActionSortTests.suite() );
        suite.addTest( FacetActionsTests.suite() );
        suite.addTest( ProjectChangeReactionTests.suite() );
        suite.addTest( FacetConstraintsTests.suite() );
        suite.addTest( DefaultVersionTests.suite() );
        
        return suite;
    }

}
