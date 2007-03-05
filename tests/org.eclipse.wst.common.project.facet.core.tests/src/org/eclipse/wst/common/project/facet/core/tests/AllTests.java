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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

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
        suite.addTest( EventDeliveryTests.suite() );
        
        return suite;
    }

}
