/******************************************************************************
 * Copyright (c) 2008 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectFrameworkEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectFrameworkListener;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.internal.FacetedProjectFrameworkImpl;
import org.eclipse.wst.common.project.facet.core.internal.FacetedProjectNature;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class FacetedProjectFramework
{
    public static final String PLUGIN_ID 
        = "org.eclipse.wst.common.project.facet.core"; //$NON-NLS-1$
    
    /**
     * The id of a built-in preset that provides default configuration. The contents of this preset
     * are calculated as follows:
     * 
     * <ol>
     *   <li>If a runtime is selected, this preset will contain default facets as specified by
     *     {@see IRuntime.getDefaultFacets(Set)}.</li>
     *   <li>If no runtime is selected, this preset will contain default versions for all of the 
     *     fixed facets as specified by {@see IProjectFacet.getDefaultVersion()}.
     * </ol>
     * 
     * @since 2.0
     */
    
    public static final String DEFAULT_CONFIGURATION_PRESET_ID 
        = "default.configuration"; //$NON-NLS-1$
    
    private static FacetedProjectFrameworkImpl impl = null;
    
    private FacetedProjectFramework() { }
    
    public static IFacetedProjectWorkingCopy createNewProject()
    {
        initialize();
        return impl.createNewProject();
    }
    
    /**
     * <p>Determines whether the specified project facet is installed in the
     * provided project. Returns <code>false</code> if the project is not 
     * accessible, the project is not faceted or the facet id is unrecognized.</p>
     * 
     * <p>This method is explicitly designed to avoid activation of the Faceted
     * Project Framework if the project is not faceted. For the code that
     * operates in the context where it can be assumed that the framework has
     * started already, better performance can be achieved by storing 
     * {@see IProjectFacet} and {@see IProjectFacetVersion} instances using the
     * singleton pattern and using the
     * {@see IFacetedProject.hasProjectFacet(IProjectFacet)} or
     * {@see IFacetedProject.hasProjectFacet(IProjectFacetVersion)} methods.</p>
     * 
     * <p>This method is equivalent to calling 
     * {@see hasProjectFacet(IProject,String,String)} with <code>null</code>
     * version expression parameter.</p>
     * 
     * @param project the project to check for the facet presence
     * @param fid the project facet id
     * @throws CoreException if failed while reading faceted project metadata
     */

    public static boolean hasProjectFacet( final IProject project,
                                           final String fid )
    
        throws CoreException
        
    {
        return hasProjectFacet( project, fid, null );
    }
    
    /**
     * <p>Determines whether the specified project facet is installed in the
     * provided project. Returns <code>false</code> if the project is not 
     * accessible, the project is not faceted or the facet id is unrecognized.</p>
     * 
     * <p>This method is explicitly designed to avoid activation of the Faceted
     * Project Framework if the project is not faceted. For the code that
     * operates in the context where it can be assumed that the framework has
     * started already, better performance can be achieved by storing 
     * {@see IProjectFacet} and {@see IProjectFacetVersion} instances using the
     * singleton pattern and using the
     * {@see IFacetedProject.hasProjectFacet(IProjectFacet)} or
     * {@see IFacetedProject.hasProjectFacet(IProjectFacetVersion)} methods.</p>
     * 
     * @param project the project to check for the facet presence
     * @param fid the project facet id
     * @param vexpr the version match expression, or <code>null</code> to
     *   match any version
     * @throws CoreException if failed while reading faceted project metadata;
     *   if the version expression is invalid
     */

    public static boolean hasProjectFacet( final IProject project,
                                           final String fid,
                                           final String vexpr )
    
        throws CoreException
        
    {
        if( project.isAccessible() &&
            project.isNatureEnabled( FacetedProjectNature.NATURE_ID ) )
        {
            initialize();
            
            final IFacetedProject fproj = ProjectFacetsManager.create( project );
            
            if( fproj != null )
            {
                if( ProjectFacetsManager.isProjectFacetDefined( fid ) )
                {
                    final IProjectFacet f = ProjectFacetsManager.getProjectFacet( fid );
                    
                    if( vexpr == null )
                    {
                        return fproj.hasProjectFacet( f );
                    }
                    else
                    {
                        final IProjectFacetVersion fv = fproj.getInstalledVersion( f );
                        
                        if( fv != null )
                        {
                            return f.getVersions( vexpr ).contains( fv );
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * Adds a faceted project listener that will be notified when the selected events in the faceted
     * project life cycle occur. The listener will apply to all faceted projects that exist in the
     * workspace now and in the future (until the listener is removed or the workspace is closed).
     * 
     * @param listener the faceted project listener
     * @param types the types of the events to listen for
     * @throws IllegalArgumentException if <code>listener</code> parameter is <code>null</code> or
     *   the <code>types</code> parameter is <code>null</code> or empty.
     * @see removeListener(IFacetedProjectListener)
     * @see IFacetedProject.addListener(IFacetedProjectListener,IFacetedProjectEvent.Type[])
     * @see IFacetedProject.removeListener(IFacetedProjectListener)
     */
    
    public static void addListener( final IFacetedProjectListener listener,
                                    final IFacetedProjectEvent.Type... types )
    {
        initialize();
        impl.addListener( listener, types );
    }
    
    public static void addListener( final IFacetedProjectFrameworkListener listener,
                                    final IFacetedProjectFrameworkEvent.Type... types )
    {
        initialize();
        impl.addListener( listener, types );
    }
    
    /**
     * Removes the faceted project listener that was previously registered using the
     * {@see addListener(IFacetedProjectListener,IFacetedProjectEvent.Type[])} method. If the
     * specified listener is not present in the listener registry, this call will be ignored.
     * 
     * @param listener the faceted project listener
     * @throws IllegalArgumentException if <code>listener</code> parameter is <code>null</code>
     * @see addListener(IFacetedProjectListener,IFacetedProjectEvent.Type[])
     * @see IFacetedProject.addListener(IFacetedProjectListener,IFacetedProjectEvent.Type[])
     * @see IFacetedProject.removeListener(IFacetedProjectListener)
     */
    
    public static void removeListener( final IFacetedProjectListener listener )
    {
        initialize();
        impl.removeListener( listener );
    }
    
    public static void removeListener( final IFacetedProjectFrameworkListener listener )
    {
        initialize();
        impl.removeListener( listener );
    }
    
    private static synchronized void initialize()
    {
        if( impl == null )
        {
            impl = FacetedProjectFrameworkImpl.getInstance();
        }
    }
    
}
