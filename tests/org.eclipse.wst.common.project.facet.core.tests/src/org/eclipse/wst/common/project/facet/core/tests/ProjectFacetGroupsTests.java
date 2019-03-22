/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.tests;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.IGroup;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@SuppressWarnings( "unused" )

public final class ProjectFacetGroupsTests

    extends AbstractTests
    
{
    private static IProjectFacet f1;
    private static IProjectFacetVersion f1v10;
    private static IProjectFacetVersion f1v12;
    private static IProjectFacetVersion f1v20;
    
    private static IProjectFacet f2;
    private static IProjectFacetVersion f2v10;
    private static IProjectFacetVersion f2v12;
    private static IProjectFacetVersion f2v20;
    
    private static IProjectFacet f3;
    private static IProjectFacetVersion f3v10;
    private static IProjectFacetVersion f3v12;
    private static IProjectFacetVersion f3v20;
    
    private static IProjectFacet f4;
    private static IProjectFacetVersion f4v10;
    private static IProjectFacetVersion f4v12;
    private static IProjectFacetVersion f4v20;
    
    private static IProjectFacet f5;
    private static IProjectFacetVersion f5v10;
    private static IProjectFacetVersion f5v12;
    private static IProjectFacetVersion f5v20;
    
    private static IProjectFacet f6;
    private static IProjectFacetVersion f6v10;
    private static IProjectFacetVersion f6v12;
    private static IProjectFacetVersion f6v20;
    
    static
    {
        f1 = ProjectFacetsManager.getProjectFacet( "grp-f1" );
        f1v10 = f1.getVersion( "1.0" );
        f1v12 = f1.getVersion( "1.2" );
        f1v20 = f1.getVersion( "2.0" );

        f2 = ProjectFacetsManager.getProjectFacet( "grp-f2" );
        f2v10 = f2.getVersion( "1.0" );
        f2v12 = f2.getVersion( "1.2" );
        f2v20 = f2.getVersion( "2.0" );
        
        f3 = ProjectFacetsManager.getProjectFacet( "grp-f3" );
        f3v10 = f3.getVersion( "1.0" );
        f3v12 = f3.getVersion( "1.2" );
        f3v20 = f3.getVersion( "2.0" );
        
        f4 = ProjectFacetsManager.getProjectFacet( "grp-f4" );
        f4v10 = f4.getVersion( "1.0" );
        f4v12 = f4.getVersion( "1.2" );
        f4v20 = f4.getVersion( "2.0" );
        
        f5 = ProjectFacetsManager.getProjectFacet( "grp-f5" );
        f5v10 = f5.getVersion( "1.0" );
        f5v12 = f5.getVersion( "1.2" );
        f5v20 = f5.getVersion( "2.0" );
        
        f6 = ProjectFacetsManager.getProjectFacet( "grp-f6" );
        f6v10 = f6.getVersion( "1.0" );
        f6v12 = f6.getVersion( "1.2" );
        f6v20 = f6.getVersion( "2.0" );
    }

    private ProjectFacetGroupsTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "Project Facet Groups Tests" );

        suite.addTest( new ProjectFacetGroupsTests( "testCreationAndEnlistment" ) );
        
        return suite;
    }

    /**
     * Tests creation of groups and enlistment of facets.
     * 
     * @throws CoreException
     * @throws IOException
     */
    
    public void testCreationAndEnlistment()
    
        throws CoreException, IOException
    
    {
        final Set<IProjectFacetVersion> expected = new HashSet<IProjectFacetVersion>();
        
        expected.add( f1v10 );
        expected.add( f1v12 );
        expected.add( f1v20 );
        expected.add( f2v12 );
        expected.add( f3v12 );
        expected.add( f3v20 );
        expected.add( f4v10 );
        expected.add( f4v12 );
        expected.add( f4v20 );
        expected.add( f5v10 );
        expected.add( f5v12 );
        expected.add( f6v12 );
        expected.add( f6v20 );
        
        assertTrue( ProjectFacetsManager.isGroupDefined( "grp-g1" ) );
        final IGroup g1 = ProjectFacetsManager.getGroup( "grp-g1" );
        assertEquals( expected, g1.getMembers() );
        assertEquals( "Group 1", g1.getLabel() );
        assertEquals( "Description for Group 1.", g1.getDescription() );
        
        assertTrue( ProjectFacetsManager.isGroupDefined( "grp-g2" ) );
        final IGroup g2 = ProjectFacetsManager.getGroup( "grp-g2" );
        assertEquals( expected, g2.getMembers() );
        assertEquals( "Group 2", g2.getLabel() );
        assertEquals( "Description for Group 2.", g2.getDescription() );
        
        assertTrue( ProjectFacetsManager.isGroupDefined( "grp-g3" ) );
        final IGroup g3 = ProjectFacetsManager.getGroup( "grp-g3" );
        assertEquals( expected, g3.getMembers() );
        assertEquals( "grp-g3", g3.getLabel() );
        assertEquals( "", g3.getDescription() );
    }
    
}
