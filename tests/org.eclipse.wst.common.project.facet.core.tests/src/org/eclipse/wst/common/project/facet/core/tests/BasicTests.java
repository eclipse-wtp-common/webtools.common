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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.ICategory;
import org.eclipse.wst.common.project.facet.core.IConstraint;
import org.eclipse.wst.common.project.facet.core.IGroup;
import org.eclipse.wst.common.project.facet.core.IPreset;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class BasicTests

    extends TestCase
    
{
    private static final String PLUGIN_ID 
        = "org.eclipse.wst.common.project.facet.core.tests";
    
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
    
    private BasicTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "Basic Tests" );

        suite.addTest( new BasicTests( "testProjectFacetExtensionPoint" ) );
        suite.addTest( new BasicTests( "testProjectFacetVersionExtensionPoint" ) );
        suite.addTest( new BasicTests( "testCategoryExtensionPoint" ) );
        suite.addTest( new BasicTests( "testPresetExtensionPoint" ) );
        suite.addTest( new BasicTests( "testDefaultVersionComparator" ) );
        suite.addTest( new BasicTests( "testCustomVersionComparator" ) );
        suite.addTest( new BasicTests( "testVersionExpressions" ) );
        suite.addTest( new BasicTests( "testVersionExpressionsWithUnknownVersions" ) );
        suite.addTest( new BasicTests( "testConstraints" ) );
        suite.addTest( new BasicTests( "testConstraintApi" ) );
        
        return suite;
    }
    
    public void testProjectFacetExtensionPoint()
    {
        assertTrue( ProjectFacetsManager.isProjectFacetDefined( "facet1" ) );
        final IProjectFacet f1 = ProjectFacetsManager.getProjectFacet( "facet1" );
        assertTrue( ProjectFacetsManager.getProjectFacets().contains( f1 ) );
        
        assertEquals( f1.getId(), "facet1" );
        assertEquals( f1.getLabel(), "Facet 1" );
        assertEquals( f1.getDescription(), "This is the description of facet1." );
        assertEquals( f1.getPluginId(), PLUGIN_ID );
        
        assertTrue( ProjectFacetsManager.isProjectFacetDefined( "facet2" ) );
        final IProjectFacet f2 = ProjectFacetsManager.getProjectFacet( "facet2" );
        assertTrue( ProjectFacetsManager.getProjectFacets().contains( f2 ) );
        
        assertEquals( f2.getId(), "facet2" );
        assertEquals( f2.getLabel(), "facet2" );
        assertEquals( f2.getDescription(), "" );
        assertEquals( f2.getPluginId(), PLUGIN_ID );
    }
    
    public void testProjectFacetVersionExtensionPoint()
    {
        assertTrue( f1.hasVersion( "1.0" ) );
        final IProjectFacetVersion f1v10 = f1.getVersion( "1.0" );
        assertEquals( f1v10.getVersionString(), "1.0" );
        assertEquals( f1v10.getProjectFacet(), f1 );
        assertEquals( f1v10.getPluginId(), PLUGIN_ID );

        assertTrue( f1.hasVersion( "1.2" ) );
        final IProjectFacetVersion f1v12 = f1.getVersion( "1.2" );
        assertEquals( f1v12.getVersionString(), "1.2" );
        assertEquals( f1v12.getProjectFacet(), f1 );
        assertEquals( f1v12.getPluginId(), PLUGIN_ID );

        assertTrue( f1.hasVersion( "1.2.1" ) );
        final IProjectFacetVersion f1v121 = f1.getVersion( "1.2.1" );
        assertEquals( f1v121.getVersionString(), "1.2.1" );
        assertEquals( f1v121.getProjectFacet(), f1 );
        assertEquals( f1v121.getPluginId(), PLUGIN_ID );

        assertTrue( f1.hasVersion( "1.3" ) );
        final IProjectFacetVersion f1v13 = f1.getVersion( "1.3" );
        assertEquals( f1v13.getVersionString(), "1.3" );
        assertEquals( f1v13.getProjectFacet(), f1 );
        assertEquals( f1v13.getPluginId(), PLUGIN_ID );

        assertTrue( f1.hasVersion( "2.0" ) );
        final IProjectFacetVersion f1v20 = f1.getVersion( "2.0" );
        assertEquals( f1v20.getVersionString(), "2.0" );
        assertEquals( f1v20.getProjectFacet(), f1 );
        assertEquals( f1v20.getPluginId(), PLUGIN_ID );
        
        assertEquals( f1.getVersions(),
                      asSet( f1v10, f1v12, f1v121, f1v13, f1v20 ) );

        assertTrue( f2.hasVersion( "3.5" ) );
        final IProjectFacetVersion f2v35 = f2.getVersion( "3.5" );
        assertEquals( f2v35.getVersionString(), "3.5" );
        assertEquals( f2v35.getProjectFacet(), f2 );
        assertEquals( f2v35.getPluginId(), PLUGIN_ID );

        assertTrue( f2.hasVersion( "3.5#a" ) );
        final IProjectFacetVersion f2v35a = f2.getVersion( "3.5#a" );
        assertEquals( f2v35a.getVersionString(), "3.5#a" );
        assertEquals( f2v35a.getProjectFacet(), f2 );
        assertEquals( f2v35a.getPluginId(), PLUGIN_ID );
        
        assertTrue( f2.hasVersion( "4.7" ) );
        final IProjectFacetVersion f2v47 = f2.getVersion( "4.7" );
        assertEquals( f2v47.getVersionString(), "4.7" );
        assertEquals( f2v47.getProjectFacet(), f2 );
        assertEquals( f2v47.getPluginId(), PLUGIN_ID );

        assertTrue( f2.hasVersion( "4.7#b" ) );
        final IProjectFacetVersion f2v47b = f2.getVersion( "4.7#b" );
        assertEquals( f2v47b.getVersionString(), "4.7#b" );
        assertEquals( f2v47b.getProjectFacet(), f2 );
        assertEquals( f2v47b.getPluginId(), PLUGIN_ID );

        assertTrue( f2.hasVersion( "4.7#c" ) );
        final IProjectFacetVersion f2v47c = f2.getVersion( "4.7#c" );
        assertEquals( f2v47c.getVersionString(), "4.7#c" );
        assertEquals( f2v47c.getProjectFacet(), f2 );
        assertEquals( f2v47c.getPluginId(), PLUGIN_ID );
        
        assertEquals( f2.getVersions(), 
                      asSet( f2v35, f2v35a, f2v47, f2v47b, f2v47c ) );
        
        // Check for version that doesn't exist.
        
        assertFalse( f2.hasVersion( "6.9" ) );
        
        try
        {
            f2.getVersion( "6.9" );
            fail();
        }
        catch( IllegalArgumentException e ) {}
    }
    
    public void testCategoryExtensionPoint()
    {
        assertTrue( ProjectFacetsManager.isCategoryDefined( "cat1" ) );
        final ICategory cat1 = ProjectFacetsManager.getCategory( "cat1" );
        assertTrue( ProjectFacetsManager.getCategories().contains( cat1 ) );
        
        assertEquals( cat1.getId(), "cat1" );
        assertEquals( cat1.getLabel(), "Category 1" );
        assertEquals( cat1.getDescription(), "This is the category description." );
        assertEquals( cat1.getPluginId(), PLUGIN_ID );

        assertEquals( cat1.getProjectFacets(), asSet( f2, f2ext ) );
        assertEquals( f2.getCategory(), cat1 );
        assertEquals( f2ext.getCategory(), cat1 );
        
        assertTrue( ProjectFacetsManager.isCategoryDefined( "cat2" ) );
        final ICategory cat2 = ProjectFacetsManager.getCategory( "cat2" );
        assertTrue( ProjectFacetsManager.getCategories().contains( cat2 ) );
        
        assertEquals( cat2.getId(), "cat2" );
        assertEquals( cat2.getLabel(), "cat2" );
        assertEquals( cat2.getDescription(), "" );
        assertEquals( cat2.getPluginId(), PLUGIN_ID );
        
        assertEquals( cat2.getProjectFacets(), asSet( f3a, f3b, f3c ) );
        assertEquals( f3a.getCategory(), cat2 );
        assertEquals( f3b.getCategory(), cat2 );
        assertEquals( f3c.getCategory(), cat2 );
    }
    
    public void testPresetExtensionPoint()
    {
        assertTrue( ProjectFacetsManager.isPresetDefined( "preset1" ) );
        final IPreset preset1 = ProjectFacetsManager.getPreset( "preset1" );
        assertTrue( ProjectFacetsManager.getPresets().contains( preset1 ) );        
        
        assertEquals( preset1.getId(), "preset1" );
        assertEquals( preset1.getLabel(), "Preset 1" );
        assertEquals( preset1.getDescription(), "This is the description for the first preset." );
        assertEquals( preset1.getProjectFacets(), asSet( f1v20, f2v35a, f2extv10 ) );
        assertFalse( preset1.isUserDefined() );
        
        assertTrue( ProjectFacetsManager.isPresetDefined( "preset2" ) );
        final IPreset preset2 = ProjectFacetsManager.getPreset( "preset2" );
        assertTrue( ProjectFacetsManager.getPresets().contains( preset2 ) );        
        
        assertEquals( preset2.getId(), "preset2" );
        assertEquals( preset2.getLabel(), "preset2" );
        assertEquals( preset2.getDescription(), "" );
        assertEquals( preset2.getProjectFacets(), asSet( f3av10, f3bv10, f3cv10 ) );
        assertFalse( preset2.isUserDefined() );
    }
    
    @SuppressWarnings( "unchecked" )
    public void testDefaultVersionComparator()
    
        throws CoreException
        
    {
        final Comparator<String> comp = f1.getVersionComparator();
        
        assertEquals( comp.getClass().getName(), 
                      "org.eclipse.wst.common.project.facet.core.DefaultVersionComparator" );
        
        assertTrue( comp.compare( "1.0", "1.2" ) < 0 );
        assertTrue( comp.compare( "1.2", "1.2.1" ) < 0 );
        assertTrue( comp.compare( "1.2.1", "2.0" ) < 0 );
        
        assertTrue( comp.compare( "1.2", "1.0" ) > 0 );
        assertTrue( comp.compare( "1.2.1", "1.2" ) > 0 );
        assertTrue( comp.compare( "2.0", "1.2.1" ) > 0 );
        
        assertTrue( comp.compare( "1.0", "1.0" ) == 0 );
        assertTrue( comp.compare( "1.2", "1.2" ) == 0 );
        assertTrue( comp.compare( "1.2.1", "1.2.1" ) == 0 );
        assertTrue( comp.compare( "2.0", "2.0" ) == 0 );
        
        assertTrue( f1v10.compareTo( f1v12 ) < 0 );
        assertTrue( f1v12.compareTo( f1v121 ) < 0 );
        assertTrue( f1v121.compareTo( f1v20 ) < 0 );
        
        assertTrue( f1v12.compareTo( f1v10 ) > 0 );
        assertTrue( f1v121.compareTo( f1v12 ) > 0 );
        assertTrue( f1v20.compareTo( f1v121 ) > 0 );
        
        assertTrue( f1v10.compareTo( f1v10 ) == 0 );
        assertTrue( f1v12.compareTo( f1v12 ) == 0 );
        assertTrue( f1v121.compareTo( f1v121 ) == 0 );
        assertTrue( f1v20.compareTo( f1v20 ) == 0 );

        assertEquals( f1.getLatestVersion(), f1v20 );
        
        final List asc = f1.getSortedVersions( true );
        
        assertEquals( asc.size(), 5 );
        assertEquals( asc.get( 0 ), f1v10 );
        assertEquals( asc.get( 1 ), f1v12 );
        assertEquals( asc.get( 2 ), f1v121 );
        assertEquals( asc.get( 3 ), f1v13 );
        assertEquals( asc.get( 4 ), f1v20 );
        
        final List desc = f1.getSortedVersions( false );
        
        assertEquals( desc.size(), 5 );
        assertEquals( desc.get( 0 ), f1v20 );
        assertEquals( desc.get( 1 ), f1v13 );
        assertEquals( desc.get( 2 ), f1v121 );
        assertEquals( desc.get( 3 ), f1v12 );
        assertEquals( desc.get( 4 ), f1v10 );
    }

    @SuppressWarnings( "unchecked" )
    public void testCustomVersionComparator()
    
        throws CoreException
        
    {
        final Comparator<String> comp = f2.getVersionComparator();
        
        assertEquals( comp.getClass().getName(), 
                      "org.eclipse.wst.common.project.facet.core.tests.support.CustomVersionComparator" );
        
        assertTrue( comp.compare( "3.5", "4.7" ) < 0 );
        assertTrue( comp.compare( "3.5", "3.5#a" ) < 0 );
        assertTrue( comp.compare( "4.7#c", "4.7#b" ) < 0 );
        
        assertTrue( comp.compare( "4.7", "3.5" ) > 0 );
        assertTrue( comp.compare( "3.5#a", "3.5" ) > 0 );
        assertTrue( comp.compare( "4.7#b", "4.7#c" ) > 0 );
        
        assertTrue( comp.compare( "3.5", "3.5" ) == 0 );
        assertTrue( comp.compare( "3.5#a", "3.5#a" ) == 0 );
        assertTrue( comp.compare( "4.7", "4.7" ) == 0 );
        assertTrue( comp.compare( "4.7#b", "4.7#b" ) == 0 );

        assertTrue( f2v35.compareTo( f2v47 ) < 0 );
        assertTrue( f2v35.compareTo( f2v35a ) < 0 );
        assertTrue( f2v47c.compareTo( f2v47b ) < 0 );
        
        assertTrue( f2v47.compareTo( f2v35 ) > 0 );
        assertTrue( f2v35a.compareTo( f2v35 ) > 0 );
        assertTrue( f2v47b.compareTo( f2v47c ) > 0 );
        
        assertTrue( f2v35.compareTo( f2v35 ) == 0 );
        assertTrue( f2v35a.compareTo( f2v35a ) == 0 );
        assertTrue( f2v47.compareTo( f2v47 ) == 0 );
        assertTrue( f2v47b.compareTo( f2v47b ) == 0 );
        
        assertEquals( f2.getLatestVersion(), f2v47b );
        
        final List asc = f2.getSortedVersions( true );
        
        assertEquals( asc.size(), 5 );
        assertEquals( asc.get( 0 ), f2v35 );
        assertEquals( asc.get( 1 ), f2v35a );
        assertEquals( asc.get( 2 ), f2v47 );
        assertEquals( asc.get( 3 ), f2v47c );
        assertEquals( asc.get( 4 ), f2v47b );
        
        final List desc = f2.getSortedVersions( false );
        
        assertEquals( desc.size(), 5 );
        assertEquals( desc.get( 0 ), f2v47b );
        assertEquals( desc.get( 1 ), f2v47c );
        assertEquals( desc.get( 2 ), f2v47 );
        assertEquals( desc.get( 3 ), f2v35a );
        assertEquals( desc.get( 4 ), f2v35 );
    }
    
    public void testVersionExpressions()
    
        throws CoreException
        
    {
        assertEquals( f1.getVersions( "1.2" ), 
                      asSet( f1v12 ) );
        
        assertEquals( f1.getVersions( "1.2,1.3" ), 
                      asSet( f1v12, f1v13 ) );
        
        assertEquals( f1.getVersions( "1.0,1.2,1.2.1,1.3,2.0" ), 
                      asSet( f1v10, f1v12, f1v121, f1v13, f1v20 ) );
        
        assertEquals( f1.getVersions( "[1.2" ),
                      asSet( f1v12, f1v121, f1v13, f1v20 ) );
        
        assertEquals( f1.getVersions( "(1.2" ),
                      asSet( f1v121, f1v13, f1v20 ) );
        
        assertEquals( f1.getVersions( "1.3]" ),
                      asSet( f1v10, f1v12, f1v121, f1v13 ) );
        
        assertEquals( f1.getVersions( "1.3)" ),
                      asSet( f1v10, f1v12, f1v121 ) );
        
        assertEquals( f1.getVersions( "[1.2-1.3]" ),
                      asSet( f1v12, f1v121, f1v13 ) );
        
        assertEquals( f1.getVersions( "[1.2-1.3)" ),
                      asSet( f1v12, f1v121 ) );
        
        assertEquals( f1.getVersions( "(1.2-1.3]" ),
                      asSet( f1v121, f1v13 ) );
        
        assertEquals( f1.getVersions( "1.0,(1.2-1.3],2.0" ),
                      asSet( f1v10, f1v121, f1v13, f1v20 ) );
    }
    
    /**
     * Tests the cases where version expressions make references to facet versions that don't
     * actually exist.
     * 
     * @throws CoreException
     */
    
    public void testVersionExpressionsWithUnknownVersions()
    
        throws CoreException
        
    {
        assertEquals( f1.getVersions( "[0.5-1.2]" ), asSet( f1v10, f1v12 ) );
        assertEquals( f1.getVersions( "[1.2.2-2.0]" ), asSet( f1v13, f1v20 ) );
        assertEquals( f1.getVersions( "[0.5-10.0)" ), asSet( f1v10, f1v12, f1v121, f1v13, f1v20 ) );
        assertEquals( f1.getVersions( "[10.0-25]" ), Collections.emptySet() );
        assertEquals( f1.getVersions( "3.0,4.5" ), Collections.emptySet() );
        assertEquals( f1.getVersions( "5.7" ), Collections.emptySet() );
    }
    
    @SuppressWarnings( "unchecked" )
    public void testConstraints()
    {
        /*
         * Version: 3.5
         * 
         * <requires facet="facet1" version="1.0"/>
         */ 

        assertFalse( f2v35.getConstraint().check( Collections.EMPTY_SET ).isOK() );
        assertTrue( f2v35.getConstraint().check( asSet( f1v10 ) ).isOK() );
        assertFalse( f2v35.getConstraint().check( asSet( f1v12 ) ).isOK() );
        assertFalse( f2v35.getConstraint().check( asSet( f1v121 ) ).isOK() );
        assertFalse( f2v35.getConstraint().check( asSet( f1v13 ) ).isOK() );
        assertFalse( f2v35.getConstraint().check( asSet( f1v20 ) ).isOK() );
        
        /*
         * Version: 3.5#a
         * 
         * <and>
         *   <requires facet="facet1" version="[1.2-1.3)"/>
         * </and>
         */
        
        assertFalse( f2v35a.getConstraint().check( Collections.EMPTY_SET  ).isOK() );
        assertFalse( f2v35a.getConstraint().check( asSet( f1v10 ) ).isOK() );
        assertTrue( f2v35a.getConstraint().check( asSet( f1v12 ) ).isOK() );
        assertTrue( f2v35a.getConstraint().check( asSet( f1v121 ) ).isOK() );
        assertFalse( f2v35a.getConstraint().check( asSet( f1v13 ) ).isOK() );
        assertFalse( f2v35a.getConstraint().check( asSet( f1v20 ) ).isOK() );
        
        /*
         * Version: 4.7
         * 
         * <or>
         *   <requires facet="facet1" version="[1.3"/>
         * </or>
         */
        
        assertFalse( f2v47.getConstraint().check( Collections.EMPTY_SET  ).isOK() );
        assertFalse( f2v47.getConstraint().check( asSet( f1v10 ) ).isOK() );
        assertFalse( f2v47.getConstraint().check( asSet( f1v12 ) ).isOK() );
        assertFalse( f2v47.getConstraint().check( asSet( f1v121 ) ).isOK() );
        assertTrue( f2v47.getConstraint().check( asSet( f1v13 ) ).isOK() );
        assertTrue( f2v47.getConstraint().check( asSet( f1v20 ) ).isOK() );
        
        /*
         * Version: 4.7#b
         * 
         * <or>
         *   <requires facet="facet1" version="1.3"/>
         *   <requires facet="facet1" version="2.0"/>
         * </or>
         */
        
        assertFalse( f2v47b.getConstraint().check( Collections.EMPTY_SET  ).isOK() );
        assertFalse( f2v47b.getConstraint().check( asSet( f1v10 ) ).isOK() );
        assertFalse( f2v47b.getConstraint().check( asSet( f1v12 ) ).isOK() );
        assertFalse( f2v47b.getConstraint().check( asSet( f1v121 ) ).isOK() );
        assertTrue( f2v47b.getConstraint().check( asSet( f1v13 ) ).isOK() );
        assertTrue( f2v47b.getConstraint().check( asSet( f1v20 ) ).isOK() );
        
        /*
         * Version: 4.7#c
         * 
         * <and>
         *   <or>
         *     <requires facet="facet1" version="1.2.1"/>
         *     <requires facet="facet1" version="1.3"/>
         *     <requires facet="facet1" version="2.0"/>
         *   </or>
         *   <conflicts facet="facet3a"/>
         *   <conflicts facet="facet3b" version="1.0"/>
         *   <conflicts group="group1"/>
         * </and>
         */
        
        assertFalse( f2v47c.getConstraint().check( Collections.EMPTY_SET  ).isOK() );
        assertFalse( f2v47c.getConstraint().check( asSet( f1v10 ) ).isOK() );
        assertFalse( f2v47c.getConstraint().check( asSet( f1v12 ) ).isOK() );
        assertTrue( f2v47c.getConstraint().check( asSet( f1v121 ) ).isOK() );
        assertTrue( f2v47c.getConstraint().check( asSet( f1v13 ) ).isOK() );
        assertTrue( f2v47c.getConstraint().check( asSet( f1v20 ) ).isOK() );
        
        assertFalse( f2v47c.getConstraint().check( asSet( f1v20, f3av10 ) ).isOK() );
        assertFalse( f2v47c.getConstraint().check( asSet( f1v20, f3av20 ) ).isOK() );
        assertFalse( f2v47c.getConstraint().check( asSet( f1v20, f3bv10 ) ).isOK() );
        assertTrue( f2v47c.getConstraint().check( asSet( f1v20, f3bv20 ) ).isOK() );
        assertFalse( f2v47c.getConstraint().check( asSet( f1v20, f3cv10 ) ).isOK() );
        assertFalse( f2v47c.getConstraint().check( asSet( f1v20, f3cv20 ) ).isOK() );
    }
    
    public void testConstraintApi()
    {
        assertEquals( IConstraint.Type.AND.name(), "and" );
        assertTrue( IConstraint.Type.valueOf( "and" ) == IConstraint.Type.AND );
        assertTrue( IConstraint.Type.valueOf( "aNd" ) == IConstraint.Type.AND );
        
        assertEquals( IConstraint.Type.OR.name(), "or" );
        assertTrue( IConstraint.Type.valueOf( "or" ) == IConstraint.Type.OR );
        assertTrue( IConstraint.Type.valueOf( "oR" ) == IConstraint.Type.OR );
        
        assertEquals( IConstraint.Type.REQUIRES.name(), "requires" );
        assertTrue( IConstraint.Type.valueOf( "requires" ) == IConstraint.Type.REQUIRES );
        assertTrue( IConstraint.Type.valueOf( "rEqUiRes" ) == IConstraint.Type.REQUIRES );
        
        assertEquals( IConstraint.Type.CONFLICTS.name(), "conflicts" );
        assertTrue( IConstraint.Type.valueOf( "conflicts" ) == IConstraint.Type.CONFLICTS );
        assertTrue( IConstraint.Type.valueOf( "cOnFlIcTs" ) == IConstraint.Type.CONFLICTS );
        
        /*
         * <and>
         *   <or>
         *     <requires facet="facet1" version="1.2.1"/>
         *     <requires facet="facet1" version="1.3"/>
         *     <requires facet="facet1" version="2.0"/>
         *   </or>
         *   <conflicts facet="facet3a"/>
         *   <conflicts facet="facet3b" version="1.0"/>
         *   <conflicts group="group1"/>
         * </and>
         */
        
        assertTrue( ProjectFacetsManager.isGroupDefined( "group1" ) );
        final IGroup g = ProjectFacetsManager.getGroup( "group1" );
        assertEquals( g.getId(), "group1" );
        assertEquals( g.getMembers(), asSet( f2v47c, f3cv10, f3cv20 ) );
        
        final IConstraint root = f2v47c.getConstraint();
        checkConstraint( root, IConstraint.Type.AND, null, null, null, null );
        
        IConstraint c1, c2;
        
        c1 = (IConstraint) root.getOperand( 0 );
        checkConstraint( c1, IConstraint.Type.OR, null, null, null );
        
        c2 = (IConstraint) c1.getOperand( 0 );
        checkConstraint( c2, IConstraint.Type.REQUIRES, f1, "1.2.1", Boolean.FALSE );
        
        c2 = (IConstraint) c1.getOperand( 1 );
        checkConstraint( c2, IConstraint.Type.REQUIRES, f1, "1.3", Boolean.FALSE );
        
        c2 = (IConstraint) c1.getOperand( 2 );
        checkConstraint( c2, IConstraint.Type.REQUIRES, f1, "2.0", Boolean.FALSE );
        
        c1 = (IConstraint) root.getOperand( 1 );
        checkConstraint( c1, IConstraint.Type.CONFLICTS, f3a, "*" );
        
        c1 = (IConstraint) root.getOperand( 2 );
        checkConstraint( c1, IConstraint.Type.CONFLICTS, f3b, "1.0" );
        
        c1 = (IConstraint) root.getOperand( 3 );
        checkConstraint( c1, IConstraint.Type.CONFLICTS, g );
    }
    
    private static void checkConstraint( final IConstraint c,
                                         final IConstraint.Type expectedType,
                                         final Object expectedOperand )
    {
        checkConstraint( c, expectedType, new Object[] { expectedOperand } );
    }

    private static void checkConstraint( final IConstraint c,
                                         final IConstraint.Type expectedType,
                                         final Object expectedOperand1,
                                         final Object expectedOperand2 )
    {
        checkConstraint( c, expectedType, 
                         new Object[] { expectedOperand1, expectedOperand2 } );
    }

    private static void checkConstraint( final IConstraint c,
                                         final IConstraint.Type expectedType,
                                         final Object expectedOperand1,
                                         final Object expectedOperand2,
                                         final Object expectedOperand3 )
    {
        checkConstraint( c, expectedType, 
                         new Object[] { expectedOperand1, expectedOperand2,
                                        expectedOperand3 } );
    }

    private static void checkConstraint( final IConstraint c,
                                         final IConstraint.Type expectedType,
                                         final Object expectedOperand1,
                                         final Object expectedOperand2,
                                         final Object expectedOperand3,
                                         final Object expectedOperand4 )
    {
        checkConstraint( c, expectedType, 
                         new Object[] { expectedOperand1, expectedOperand2,
                                        expectedOperand3, expectedOperand4 } );
    }
    
    private static void checkConstraint( final IConstraint c,
                                         final IConstraint.Type expectedType,
                                         final Object[] expectedOperands )
    {
        assertEquals( c.getType(), expectedType );

        final int count = c.getOperands().size();
        assertEquals( count, expectedOperands.length );
        
        final List<Object> list = new ArrayList<Object>();
        
        for( int i = 0; i < count; i++ )
        {
            final Object operand = c.getOperand( i );
            final Object expected = expectedOperands[ i ];
            
            if( expected != null )
            {
                if( ( expected instanceof String ) &&
                    ! ( operand instanceof String ) )
                {
                    assertEquals( operand.toString(), expected );
                }
                else
                {
                    assertEquals( operand, expected );
                }
            }
            
            list.add( c.getOperand( i ) );
        }
        
        assertEquals( c.getOperands(), list );
    }
    
}
