/******************************************************************************
 * Copyright (c) 2010, 2023 Oracle and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation
 *    Carl Anderson - Java 9 support
 *    John Collier - Java 10-11, 13-15 support
 *    Leon Keuroglian - Java 12 support
 *    Nitin Dahyabhai - Java 12, 16, 17 support
 ******************************************************************************/

package org.eclipse.jst.common.project.facet.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.common.project.facet.core.internal.FacetedProjectFrameworkJavaPlugin;
import org.eclipse.wst.common.project.facet.core.FacetedProjectFramework;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class JavaFacet 
{
    public static final String ID = "java"; //$NON-NLS-1$
    public static final IProjectFacet FACET = ProjectFacetsManager.getProjectFacet( ID );
    public static final IProjectFacetVersion VERSION_1_3 = FACET.getVersion( "1.3" ); //$NON-NLS-1$
    public static final IProjectFacetVersion VERSION_1_4 = FACET.getVersion( "1.4" ); //$NON-NLS-1$
    public static final IProjectFacetVersion VERSION_1_5 = FACET.getVersion( "1.5" ); //$NON-NLS-1$
    public static final IProjectFacetVersion VERSION_1_6 = FACET.getVersion( "1.6" ); //$NON-NLS-1$
    public static final IProjectFacetVersion VERSION_1_7 = FACET.getVersion( "1.7" ); //$NON-NLS-1$
    public static final IProjectFacetVersion VERSION_1_8 = FACET.getVersion( "1.8" ); //$NON-NLS-1$
    public static final IProjectFacetVersion VERSION_9 = FACET.getVersion( "9" ); //$NON-NLS-1$
    public static final IProjectFacetVersion VERSION_10 = FACET.getVersion( "10" ); //$NON-NLS-1$
    public static final IProjectFacetVersion VERSION_11 = FACET.getVersion( "11" ); //$NON-NLS-1$
    public static final IProjectFacetVersion VERSION_12 = FACET.getVersion( "12" ); //$NON-NLS-1$
    public static final IProjectFacetVersion VERSION_13 = FACET.getVersion( "13" ); //$NON-NLS-1$
    public static final IProjectFacetVersion VERSION_14 = FACET.getVersion( "14" ); //$NON-NLS-1$
    public static final IProjectFacetVersion VERSION_15 = FACET.getVersion( "15" ); //$NON-NLS-1$
    public static final IProjectFacetVersion VERSION_16 = FACET.getVersion( "16" ); //$NON-NLS-1$
    public static final IProjectFacetVersion VERSION_17 = FACET.getVersion( "17" ); //$NON-NLS-1$
    public static final IProjectFacetVersion VERSION_18 = FACET.getVersion( "18" ); //$NON-NLS-1$
    public static final IProjectFacetVersion VERSION_19 = FACET.getVersion( "19" ); //$NON-NLS-1$
    public static final IProjectFacetVersion VERSION_20 = FACET.getVersion( "20" ); //$NON-NLS-1$

    @Deprecated
    public static final IProjectFacetVersion JAVA_13 = VERSION_1_3;
    
    @Deprecated
    public static final IProjectFacetVersion JAVA_14 = VERSION_1_4;
    
    @Deprecated
    public static final IProjectFacetVersion JAVA_50 = VERSION_1_5;
    
    @Deprecated
    public static final IProjectFacetVersion JAVA_60 = VERSION_1_6;

    public static boolean isInstalled( final IProject project )
    {
        try
        {
            return FacetedProjectFramework.hasProjectFacet( project, ID );
        }
        catch( CoreException e )
        {
            FacetedProjectFrameworkJavaPlugin.log( e );
            return false;
        }
    }
    
    /**
     * Checks whether the specified project is a Java project.
     * 
     * @param pj the project to check.
     * @return <code>true</code> if the project is a Java project
     * @since 1.4
     */
    
    public static boolean isJavaProject( final IProject project )
    {
        try
        {
            return project.getNature( JavaCore.NATURE_ID ) != null;
        }
        catch( CoreException e )
        {
            return false;
        }
    }
    
}
