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

package org.eclipse.wst.common.project.facet.core;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

/**
 * This interface is used for manipulating the set of facets installed on a
 * project. Use {@link ProjectFacetsManager#create(IProject)} to get an instance 
 * of this interface.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public interface IFacetedProjectBase
{
    static final int PROBLEM_PROJECT_NAME = 1;
    static final int PROBLEM_OTHER = -1;
    
    IProject getProject();
    
    /**
     * Returns the set of fixed project facets for this project. Fixed facets 
     * cannot be uninstalled, but the installed version can be changed.
     * 
     * @return the set of fixed project facets for this project
     */
    
    Set<IProjectFacet> getFixedProjectFacets();
    
    boolean isFixedProjectFacet( IProjectFacet facet );
    
    /**
     * Returns the set of project facets currently installed on this project.
     * 
     * @return the set of project facets currently installed on this project 
     */
    
    Set<IProjectFacetVersion> getProjectFacets();
    
    IProjectFacetVersion getProjectFacetVersion( IProjectFacet f );
    
    /**
     * Determines whether any version of the specified project facet is 
     * installed on this project.
     * 
     * @param f the project facet descriptor
     * @return <code>true</code> if any version of the specified project facet 
     *   is installed on this project, <code>false</code> otherwise
     */
    
    boolean hasProjectFacet( IProjectFacet f );
    
    /**
     * Determines whether the specfied project facet version is installed on 
     * this project.
     * 
     * @param fv the project facet version descriptor
     * @return <code>true</code> if the specified project facet version is 
     *   installed on this project, <code>false</code> otherwise
     */
    
    boolean hasProjectFacet( IProjectFacetVersion fv );
    
    /**
     * Determines whether this project (in it's current state) can be targeted to the provided
     * runtime. This determination is made by looking at the facets that are currently installed
     * and checking them against the set of facets known to be supported by the provided runtime.
     * 
     * @param runtime the runtime to check
     * @return <code>true</code> if this project can target the provided runtime and
     *   <code>false</code> otherwise
     * @since WTP 2.0
     */
    
    boolean isTargetable( IRuntime runtime );
    
    /**
     * Checks whether this project currently targets the specified runtime. 
     * 
     * @param runtime the runtime to check
     * @return <code>true</code> if this project currently targets the specified runtime and
     *   <code>false</code> otherwise
     * @since WTP 2.0
     */
    
    boolean isTargeted( IRuntime runtime );
    
    /**
     * <p>Returns the set of all runtimes that this project is targeting. When a
     * project targets a runtime, the set of facets that can be installed is
     * limited to those supported by that runtime. When a project targets
     * multiple runtimes, the set of applicable facets is limited to those
     * supported by all targeted runtimes.</p>
     * 
     * @return the set of targeted runtimes
     */
    
    Set<IRuntime> getTargetedRuntimes();
    
    /**
     * <p>Returns the primary target runtime for this project. There is always
     * a primary runtime unless the list of target runtimes is empty.</p>
     * 
     * @return the primary runtime, or <code>null</code>
     */
    
    IRuntime getPrimaryRuntime();

    /**
     * Performs a variety of consistency checks over the faceted project. The
     * result of the validation is returned as a status object. 
     *
     * @param monitor a progress monitor, or <code>null</code> if progress
     *    reporting and cancellation are not desired
     * @return a status object with code <code>IStatus.OK</code> if this
     *   faceted project is valid, otherwise a status object indicating what is 
     *   wrong with it
     */
    
    IStatus validate( IProgressMonitor monitor );

    /**
     * Performs a variety of consistency checks over the faceted project. The
     * result of the validation is returned as a status object. This method is
     * equivalent to calling validate with a <code>null</code> progress monitor. 
     *
     * @return a status object with code <code>IStatus.OK</code> if this
     *   faceted project is valid, otherwise a status object indicating what is 
     *   wrong with it
     */
    
    IStatus validate();
    
    /**
     * Adds a faceted project listener that will be notified when the selected events in the faceted
     * project life cycle occur. The listener will apply only to this project.
     * 
     * @param listener the faceted project listener
     * @param types the types of the events to listen for
     * @throws IllegalArgumentException if <code>listener</code> parameter is <code>null</code> or
     *   the <code>types</code> parameter is <code>null</code> or empty.
     * @see #removeListener(IFacetedProjectListener)
     * @see FacetedProjectFramework#addListener(IFacetedProjectListener,IFacetedProjectEvent.Type[])
     * @see FacetedProjectFramework#removeListener(IFacetedProjectListener)
     */
    
    void addListener( IFacetedProjectListener listener,
                      IFacetedProjectEvent.Type... types );
    
    /**
     * Removes the faceted project listener that was previously registered using the
     * {@link #addListener(IFacetedProjectListener,IFacetedProjectEvent.Type[])} method. If the
     * specified listener is not present in the listener registry, this call will be ignored.
     * 
     * @param listener the faceted project listener
     * @throws IllegalArgumentException if <code>listener</code> parameter is <code>null</code>
     * @see #addListener(IFacetedProjectListener,IFacetedProjectEvent.Type[])
     * @see FacetedProjectFramework#addListener(IFacetedProjectListener,IFacetedProjectEvent.Type[])
     * @see FacetedProjectFramework#removeListener(IFacetedProjectListener)
     */
    
    void removeListener( IFacetedProjectListener listener );
    
}
