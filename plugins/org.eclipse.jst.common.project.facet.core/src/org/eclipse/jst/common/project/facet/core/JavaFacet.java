/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
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
