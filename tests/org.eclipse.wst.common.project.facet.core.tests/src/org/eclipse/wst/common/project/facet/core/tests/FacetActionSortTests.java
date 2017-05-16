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

import static java.util.Arrays.asList;
import static org.eclipse.wst.common.project.facet.core.tests.support.TestUtils.asSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

@SuppressWarnings( "unused" )
public class FacetActionSortTests

    extends TestCase
    
{
    private static IProjectFacet f1;
    private static IProjectFacetVersion f1v10;
    private static IProjectFacetVersion f1v12;
    private static IProjectFacetVersion f1v121;
    private static IProjectFacetVersion f1v13;
    private static IProjectFacetVersion f1v20;
    
    private static IProjectFacet f2;
    private static IProjectFacetVersion f2v35;
    private static IProjectFacetVersion f2v35a;
    private static IProjectFacetVersion f2v47;
    private static IProjectFacetVersion f2v47b;
    private static IProjectFacetVersion f2v47c;
    
    private static IProjectFacet f2ext;
    private static IProjectFacetVersion f2extv10;

    private static IProjectFacet f3a;
    private static IProjectFacetVersion f3av10;
    private static IProjectFacetVersion f3av20;

    private static IProjectFacet f3b;
    private static IProjectFacetVersion f3bv10;
    private static IProjectFacetVersion f3bv20;

    private static IProjectFacet f3c;
    private static IProjectFacetVersion f3cv10;
    private static IProjectFacetVersion f3cv20;

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
            
            f2 = ProjectFacetsManager.getProjectFacet( "facet2" );
            f2v35 = f2.getVersion( "3.5" );
            f2v35a = f2.getVersion( "3.5#a" );
            f2v47 = f2.getVersion( "4.7" );
            f2v47b = f2.getVersion( "4.7#b" );
            f2v47c = f2.getVersion( "4.7#c" );
            
            f2ext = ProjectFacetsManager.getProjectFacet( "facet2ext" );
            f2extv10 = f2ext.getVersion( "1.0" );

            f3a = ProjectFacetsManager.getProjectFacet( "facet3a" );
            f3av10 = f3a.getVersion( "1.0" );
            f3av20 = f3a.getVersion( "2.0" );

            f3b = ProjectFacetsManager.getProjectFacet( "facet3b" );
            f3bv10 = f3b.getVersion( "1.0" );
            f3bv20 = f3b.getVersion( "2.0" );

            f3c = ProjectFacetsManager.getProjectFacet( "facet3c" );
            f3cv10 = f3c.getVersion( "1.0" );
            f3cv20 = f3c.getVersion( "2.0" );
        }
        catch( Exception e )
        {
            // Ignore failures. This api is tested explicitly.
        }
    }
    
    private FacetActionSortTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "Facet Action Sort Tests" );

        suite.addTest( new FacetActionSortTests( "testUninstallComesFirst1" ) );
        suite.addTest( new FacetActionSortTests( "testUninstallComesFirst2" ) );
        suite.addTest( new FacetActionSortTests( "testSortStability1" ) );
        suite.addTest( new FacetActionSortTests( "testSortStability2" ) );
        
        return suite;
    }
    
    /**
     * Tests whether the sort places uninstall actions first. This is the 
     * control test case. The input already places uninstall first.
     */
    
    public void testUninstallComesFirst1()
    {
        final Action a1 = new Action( Action.Type.INSTALL, f1v12, null );
        final Action a2 = new Action( Action.Type.UNINSTALL, f3av10, null );
        
        final List<Action> actions = new ArrayList<Action>();
        
        actions.add( a2 );
        actions.add( a1 );
        
        ProjectFacetsManager.sort( asSet( f3av10 ), actions );
        assertEquals( actions, asList( a2, a1 ) );
    }
    
    /**
     * Tests whether the sort places uninstall actions first. The test case 
     * places an install action in front of the uninstall action and checks
     * whether the sort algorithm reverses the order. 
     */
    
    public void testUninstallComesFirst2()
    {
        final Action a1 = new Action( Action.Type.INSTALL, f1v12, null );
        final Action a2 = new Action( Action.Type.UNINSTALL, f3av10, null );
        
        final List<Action> actions = new ArrayList<Action>();
        
        actions.add( a1 );
        actions.add( a2 );
        
        ProjectFacetsManager.sort( asSet( f3av10 ), actions );
        assertEquals( actions, asList( a2, a1 ) );
    }

    /**
     * Tests whether the sort produces "stable" results given unrelated facets.
     * This is the control test case. The input is already in the correct order. 
     */
    
    @SuppressWarnings( "unchecked" )
    public void testSortStability1()
    {
        final Action a1 = new Action( Action.Type.INSTALL, f1v12, null );
        final Action a2 = new Action( Action.Type.INSTALL, f3av10, null );
        
        final List<Action> actions = new ArrayList<Action>();
        
        actions.add( a1 );
        actions.add( a2 );
        
        ProjectFacetsManager.sort( Collections.EMPTY_SET, actions );
        assertEquals( actions, asList( a1, a2 ) );
    }

    /**
     * Tests whether the sort produces "stable" results given unrelated facets.
     * The input facets are in the reverse order.
     */
    
    @SuppressWarnings( "unchecked" )
    public void testSortStability2()
    {
        final Action a1 = new Action( Action.Type.INSTALL, f1v12, null );
        final Action a2 = new Action( Action.Type.INSTALL, f3av10, null );
        
        final List<Action> actions = new ArrayList<Action>();
        
        actions.add( a2 );
        actions.add( a1 );
        
        ProjectFacetsManager.sort( Collections.EMPTY_SET, actions );
        assertEquals( actions, asList( a1, a2 ) );
    }
    
}
