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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.wst.common.project.facet.core.IDefaultVersionProvider;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public class DefaultVersionTests

    extends TestCase
    
{
    private static IProjectFacet f1;
    private static IProjectFacetVersion f1v4;
    
    private static IProjectFacet f2;
    private static IProjectFacetVersion f2v2;
    
    private static IProjectFacet f3;
    private static IProjectFacetVersion f3v3;
    
    private static IProjectFacet f4;
    private static IProjectFacetVersion f4v4;

    private static IProjectFacet f5;
    private static IProjectFacetVersion f5v4;
    
    private static IProjectFacet f6;
    private static IProjectFacetVersion f6v4;

    private static IProjectFacet f7;
    private static IProjectFacetVersion f7v4;
    
    static
    {
        f1 = ProjectFacetsManager.getProjectFacet( "dvt_f1" );
        f1v4 = f1.getVersion( "4.0" );

        f2 = ProjectFacetsManager.getProjectFacet( "dvt_f2" );
        f2v2 = f2.getVersion( "2.0" );

        f3 = ProjectFacetsManager.getProjectFacet( "dvt_f3" );
        f3v3 = f3.getVersion( "3.0" );

        f4 = ProjectFacetsManager.getProjectFacet( "dvt_f4" );
        f4v4 = f4.getVersion( "4.0" );

        f5 = ProjectFacetsManager.getProjectFacet( "dvt_f5" );
        f5v4 = f5.getVersion( "4.0" );

        f6 = ProjectFacetsManager.getProjectFacet( "dvt_f6" );
        f6v4 = f6.getVersion( "4.0" );

        f7 = ProjectFacetsManager.getProjectFacet( "dvt_f7" );
        f7v4 = f7.getVersion( "4.0" );
    }
    
    private DefaultVersionTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "Default Version Tests" );

        suite.addTest( new DefaultVersionTests( "testUnspecifiedDefaultVersion" ) );
        suite.addTest( new DefaultVersionTests( "testStaticDefaultVersion" ) );
        suite.addTest( new DefaultVersionTests( "testDefaultVersionProvider" ) );
        suite.addTest( new DefaultVersionTests( "testBadDefaultVersionProvider1" ) );
        suite.addTest( new DefaultVersionTests( "testBadDefaultVersionProvider2" ) );
        suite.addTest( new DefaultVersionTests( "testBadDefaultVersionProvider3" ) );
        suite.addTest( new DefaultVersionTests( "testBadDefaultVersion" ) );
        
        return suite;
    }
    
    public void testUnspecifiedDefaultVersion()
    {
        assertEquals( f1.getDefaultVersion(), f1v4 );
    }
    
    public void testStaticDefaultVersion()
    {
        assertEquals( f2.getDefaultVersion(), f2v2 );
    }
    
    public void testDefaultVersionProvider()
    {
        assertEquals( f3.getDefaultVersion(), f3v3 );
    }
    
    /**
     * Tests the handling of the case where the associated default version
     * provider returns a version that doesn't belong to the facet that the
     * version provider is associated with.
     */

    public void testBadDefaultVersionProvider1()
    {
        assertEquals( f4.getDefaultVersion(), f4v4 );
    }
    
    /**
     * Tests the handling of the case where the associated default version
     * provider throws an exception when invoked.
     */

    public void testBadDefaultVersionProvider2()
    {
        assertEquals( f5.getDefaultVersion(), f5v4 );
    }
    
    /**
     * Tests the handling of the case where the associated default version
     * provider returns null.
     */
    
    public void testBadDefaultVersionProvider3()
    {
        assertEquals( f6.getDefaultVersion(), f6v4 );
    }

    /**
     * Tests the handling of the case where the specified static default version
     * is not defined for the facet.
     */

    public void testBadDefaultVersion()
    {
        assertEquals( f7.getDefaultVersion(), f7v4 );
    }
    
    public static final class FacetDvtF3DefaultVersionProvider

        implements IDefaultVersionProvider
        
    {
    
        public IProjectFacetVersion getDefaultVersion()
        {
            return ProjectFacetsManager.getProjectFacet( "dvt_f3" ).getVersion( "3.0" );
        }
    }
    
    public static final class FacetDvtF5DefaultVersionProvider

        implements IDefaultVersionProvider
        
    {
        public IProjectFacetVersion getDefaultVersion()
        {
            throw new RuntimeException();
        }
    }
    
    public static final class FacetDvtF6DefaultVersionProvider

        implements IDefaultVersionProvider
        
    {
        public IProjectFacetVersion getDefaultVersion()
        {
            return null;
        }
    }
    
}
