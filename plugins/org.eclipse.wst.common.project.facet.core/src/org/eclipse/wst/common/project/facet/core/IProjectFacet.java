/******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

/**
 * Contains metadata that describes a project facet. This interface is not 
 * intended to be implemented by clients.
 * 
 * <p><i>This class is part of an interim API that is still under development 
 * and expected to change significantly before reaching stability. It is being 
 * made available at this early stage to solicit feedback from pioneering 
 * adopters on the understanding that any code that uses this API will almost 
 * certainly be broken (repeatedly) as the API evolves.</i></p>
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public interface IProjectFacet

    extends IAdaptable
    
{
    /**
     * Returns the project facet identifier. 
     * 
     * @return the project facet identifier
     */

    String getId();
    
    /**
     * Returns the id of the plugin that defines this project facet. This method
     * will return <code>null</code> if this facet is not defined. 
     * 
     * @return the id of the plugin that defines this project facet, or
     *   <code>null</code>
     */
    
    String getPluginId();
    
    /**
     * Returns the project facet label. The label should be used when presenting
     * the project facet to the user.
     * 
     * @return the project facet label
     */

    String getLabel();
    
    /**
     * Returns the project facet description.
     * 
     * @return the project facet description
     */

    String getDescription();
    
    /**
     * Returns the category, if any, that this project facet belongs to.
     * 
     * @return the category that this project facet belongs to, or 
     *   <code>null</code>
     */
    
    ICategory getCategory();
    
    /**
     * Returns the descriptors of all versions of this project facet.
     * 
     * @return the descriptors of all versions of this project facet (element
     *   type: {@see IProjectFacetVersion})
     */
    
    Set getVersions();
    
    Set getVersions( String expr )
    
        throws CoreException;
    
    /**
     * Determines whether the specified project facet version exists.
     * 
     * @param version the verson string
     * @return <code>true</code> if the specified project facet version exists,
     *   <code>false</code> otherwise
     */
    
    boolean hasVersion( String version );
    
    /**
     * Returns the descriptor of the given project facet version.
     * 
     * @param version the version string
     * @return the descriptor of the given project facet version, or 
     *   <code>null</code>
     */
        
    IProjectFacetVersion getVersion( String version );
    
    /**
     * Returns the latest version of the project facet as specified by the 
     * version comparator.
     * 
     * @return returns the latest version of the project facet
     */
    
    IProjectFacetVersion getLatestVersion()
    
        throws VersionFormatException, CoreException;
    
    IProjectFacetVersion getLatestSupportedVersion( IRuntime runtime )
    
        throws CoreException;

    /**
     * Returns a sorted list containing the descriptors of all versions of this 
     * project facet. 
     * 
     * @param ascending whether version descriptors should be sorted in 
     *   ascending order
     * @return a sorted list containing the descriptors of all versions of this 
     *   project facet (element type: {@see IProjectFacetVersion})
     * @throws VersionFormatException if failed while parsing a version string
     */
    
    List getSortedVersions( boolean ascending )
    
        throws VersionFormatException, CoreException;
    
    /**
     * Returns the version comparator specified for this project facet. If no 
     * version comparator is specified, this method will return an instance of 
     * the {@see DefaultVersionComparator}.
     * 
     * @return the version comparator specified for this project facet
     */
    
    Comparator getVersionComparator()
    
        throws CoreException;
    
}
