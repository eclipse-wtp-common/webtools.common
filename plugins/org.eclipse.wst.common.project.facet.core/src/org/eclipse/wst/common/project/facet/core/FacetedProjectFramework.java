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

package org.eclipse.wst.common.project.facet.core;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectFrameworkEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectFrameworkListener;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.internal.FacetedProjectFrameworkImpl;
import org.eclipse.wst.common.project.facet.core.internal.FacetedProjectNature;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.util.internal.VersionExpr2;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
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
     *     {@link IRuntime#getDefaultFacets(Set)}.</li>
     *   <li>If no runtime is selected, this preset will contain default versions for all of the 
     *     fixed facets as specified by {@link IProjectFacet#getDefaultVersion()}.
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
     * Determines whether the specified project is faceted. This method will return
     * <code>false</code> if the project is not accessible.
     * 
     * @param project the project to check
     * @return <code>true</code> if the project is faceted
     * @throws CoreException if failed while reading project metadata
     * @since 1.4
     */
    
    public static boolean isFacetedProject( final IProject project )
    
        throws CoreException
        
    {
        return ( project.isAccessible() && project.isNatureEnabled( FacetedProjectNature.NATURE_ID ) );
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
     * {@link IProjectFacet} and {@link IProjectFacetVersion} instances using the
     * singleton pattern and using the
     * {@link IFacetedProject#hasProjectFacet(IProjectFacet)} or
     * {@link IFacetedProject#hasProjectFacet(IProjectFacetVersion)} methods.</p>
     * 
     * <p>This method is equivalent to calling 
     * {@link #hasProjectFacet(IProject,String,String)} with <code>null</code>
     * version expression parameter.</p>
     * 
     * @param project the project to check for the facet presence
     * @param fid the project facet id
     * @return <code>true</code> if specified project has the given facet
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
     * accessible or the project is not faceted. Works even if the facet or
     * the version that is being checked is not defined.</p>
     * 
     * <p>This method is explicitly designed to avoid activation of the Faceted
     * Project Framework if the project is not faceted. For the code that
     * operates in the context where it can be assumed that the framework has
     * started already, better performance can be achieved by storing 
     * {@link IProjectFacet} and {@link IProjectFacetVersion} instances using the
     * singleton pattern and using the
     * {@link IFacetedProject#hasProjectFacet(IProjectFacet)} or
     * {@link IFacetedProject#hasProjectFacet(IProjectFacetVersion)} methods.</p>
     * 
     * @param project the project to check for the facet presence
     * @param fid the project facet id
     * @param vexpr the version match expression, or <code>null</code> to
     *   match any version
     * @return <code>true</code> if specified project has the given facet
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
                            final VersionExpr2 expr = new VersionExpr2( vexpr );
                            return expr.check( fv.getVersionString() );
                        }
                    }
                }
                else
                {
                    for( IProjectFacetVersion fv : fproj.getProjectFacets() )
                    {
                        final IProjectFacet f = fv.getProjectFacet();
                        
                        if( f.getId().equals( fid ) )
                        {
                            if( vexpr == null )
                            {
                                return true;
                            }
                            else
                            {
                                final VersionExpr2 expr = new VersionExpr2( vexpr );
                                return expr.check( fv.getVersionString() );
                            }
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
     * @see #removeListener(IFacetedProjectListener)
     * @see IFacetedProject#addListener(IFacetedProjectListener,IFacetedProjectEvent.Type[])
     * @see IFacetedProject#removeListener(IFacetedProjectListener)
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
     * {@link #addListener(IFacetedProjectListener,IFacetedProjectEvent.Type[])} method. If the
     * specified listener is not present in the listener registry, this call will be ignored.
     * 
     * @param listener the faceted project listener
     * @throws IllegalArgumentException if <code>listener</code> parameter is <code>null</code>
     * @see #addListener(IFacetedProjectListener,IFacetedProjectEvent.Type[])
     * @see IFacetedProject#addListener(IFacetedProjectListener,IFacetedProjectEvent.Type[])
     * @see IFacetedProject#removeListener(IFacetedProjectListener)
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
    
    /**
     * Returns the global preferences store for the specified facet. These preferences are stored
     * in workspace metadata.
     * 
     * @param facet project facet
     * @return the global preferences store for the specified facet
     * @throws BackingStoreException if failed while reading from the backing store
     * @since WTP 1.4
     */
    
    public static Preferences getPreferences( final IProjectFacet facet )
    
        throws BackingStoreException
        
    {
        initialize();
        return impl.getPreferences( facet );
    }
    
    private static synchronized void initialize()
    {
        if( impl == null )
        {
            impl = FacetedProjectFrameworkImpl.getInstance();
        }
    }
    
}
