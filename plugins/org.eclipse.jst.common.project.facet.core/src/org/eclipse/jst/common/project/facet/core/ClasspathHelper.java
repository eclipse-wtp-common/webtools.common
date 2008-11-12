/******************************************************************************
 * Copyright (c) 2008 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.jst.common.project.facet.core;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jst.common.project.facet.core.internal.ClasspathUtil;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

/**
 * <p>A utility used in conjunction with IClasspathProvider in order to manage
 * the bookkeeping when project facets are installed and uninstalled, and when
 * the bound runtime changes. This utility tracks which classpath entries were
 * added to the project by which facet and stores this information in a project
 * metadata file. This enables the classpath entries to be removed without
 * knowing what they are. It is only necessary to know which facet added them.
 * This utility supports the case where the same classpath entry is added by
 * multiple project facets. In this situation, a classpath entry is only
 * removed when the removal has been requested for all of the facets that added
 * it.</p>
 * 
 * <p>Typically the project facet author will write something like this in the
 * install delegate:</p>
 * 
 * <pre>
 * if( ! ClasspathHelper.addClasspathEntries( project, fv )
 * {
 *     // Handle the case when there is no bound runtime or when the bound
 *     // runtime cannot provide classpath entries for this facet.
 *     
 *     final List alternate = ...;
 *     ClasspathHelper.addClasspathEntries( project, fv, alternate );
 * }
 * </pre>
 * 
 * <p>And something like this in the uninstall delegate:</p>
 * 
 * <pre>
 * ClasspathHelper.removeClasspathEntries( project, fv );
 * </pre>
 * 
 * <p>And something like this in the runtime changed delegate:</p>
 * 
 * <pre>
 * ClasspathHelper.removeClasspathEntries( project, fv );
 * 
 * if( ! ClasspathHelper.addClasspathEntries( project, fv )
 * {
 *     // Handle the case when there is no bound runtime or when the bound
 *     // runtime cannot provide classpath entries for this facet.
 *     
 *     final List alternate = ...;
 *     ClasspathHelper.addClasspathEntries( project, fv, alternate );
 * }
 * </pre>
 * 
 * <p>And something like this in the version change delegate:</p>
 * 
 * <pre>
 * final IProjectFacetVersion oldver
 *   = fproj.getInstalledVersion( fv.getProjectFacet() );
 * 
 * ClasspathHelper.removeClasspathEntries( project, oldver );
 * 
 * if( ! ClasspathHelper.addClasspathEntries( project, fv )
 * {
 *     // Handle the case when there is no bound runtime or when the bound
 *     // runtime cannot provide classpath entries for this facet.
 *     
 *     final List alternate = ...;
 *     ClasspathHelper.addClasspathEntries( project, fv, alternate );
 * }
 * </pre>
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ClasspathHelper
{
    /**
     * @since 1.4
     */
    
    public static final String LEGACY_METADATA_FILE_NAME = ClasspathUtil.LEGACY_METADATA_FILE_NAME;
    
    private ClasspathHelper() {}
    
    /**
     * Convenience method for adding to the project the classpath entries
     * provided for the specified project facet by the runtime bound to the
     * project. The entries are marked as belonging to the specified project
     * facet.
     *   
     * @param project the project
     * @param fv the project facet version that will own these entries
     * @return <code>true</code> if classpath entries were added, or
     *   <code>false</code> if there is no runtime bound to the project or if
     *   it cannot provide classpath entries for the specified facet
     * @throws CoreException if failed while adding the classpath entries
     */
    
    public static boolean addClasspathEntries( final IProject project,
                                               final IProjectFacetVersion fv )
    
        throws CoreException
        
    {
        final IFacetedProject fproj = ProjectFacetsManager.create( project );
        final IRuntime runtime = fproj.getPrimaryRuntime();
        
        if( runtime != null )
        {
            final IClasspathProvider cpprov 
                = (IClasspathProvider) runtime.getAdapter( IClasspathProvider.class );
            
            final List<IClasspathEntry> cpentries = cpprov.getClasspathEntries( fv );
            
            if( cpentries != null )
            {
                addClasspathEntries( project, fv, cpentries );
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Add the provided classpath entries to project and marks them as belonging
     * to the specified project facet.
     * 
     * @param project the project
     * @param fv the project facet version that will own these entries
     * @param cpentries the classpath entries (element type: 
     *   {@see IClasspathEntry})
     * @throws CoreException if failed while adding the classpath entries
     */
    
    public static void addClasspathEntries( final IProject project,
                                            final IProjectFacetVersion fv,
                                            final List<IClasspathEntry> cpentries )
    
        throws CoreException
        
    {
        ClasspathUtil.addClasspathEntries( project, fv.getProjectFacet(), cpentries );
    }
    
    /**
     * Removes the classpath entries belonging to the specified project facet.
     * Any entries that also belong to another facet are left in place.
     * 
     * @param project the project
     * @param fv the project facet that owns the entries that should be removed
     * @throws CoreException if failed while removing classpath entries
     */
    
    public static void removeClasspathEntries( final IProject project,
                                               final IProjectFacetVersion fv )
    
        throws CoreException
        
    {
        ClasspathUtil.removeClasspathEntries( project, fv.getProjectFacet() );
    }
    

}
