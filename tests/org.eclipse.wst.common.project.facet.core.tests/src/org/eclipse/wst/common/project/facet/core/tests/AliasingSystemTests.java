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

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.FacetedProjectFramework;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class AliasingSystemTests

    extends AbstractTests
    
{
    private static IProjectFacet f1;
    private static IProjectFacetVersion f1v16;
    
    static
    {
        f1 = ProjectFacetsManager.getProjectFacet( "ast-f1" );
        f1v16 = f1.getVersion( "1.6" );
    }

    private AliasingSystemTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "Aliasing System Tests" );

        suite.addTest( new AliasingSystemTests( "testVersionExpr" ) );
        
        return suite;
    }

    /**
     * Tests that the version expressions work with aliases.
     * 
     * @throws CoreException
     * @throws IOException
     */
    
    public void testVersionExpr()
    
        throws CoreException, IOException
    
    {
        final IFacetedProject proj = createFacetedProject();
        proj.installProjectFacet( f1v16, null, null );

        // Base line.
        
        assertTrue( FacetedProjectFramework.hasProjectFacet( proj.getProject(), f1.getId(), "1.6" ) );
        assertFalse( FacetedProjectFramework.hasProjectFacet( proj.getProject(), f1.getId(), "1.5" ) );
        
        // Actual test case.
        
        assertTrue( FacetedProjectFramework.hasProjectFacet( proj.getProject(), f1.getId(), "6.0" ) );
        assertFalse( FacetedProjectFramework.hasProjectFacet( proj.getProject(), f1.getId(), "5.0" ) );
    }
    
}
