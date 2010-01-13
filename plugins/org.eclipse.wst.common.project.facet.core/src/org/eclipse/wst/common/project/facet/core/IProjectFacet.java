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

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

/**
 * Contains metadata that describes a project facet. This interface is not 
 * intended to be implemented by clients.
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public interface IProjectFacet

    extends IAdaptable
    
{
    /**
     * The name of the property that's used for suggesting to the framework that this facet's
     * version does not convey a special meaning and should be hidden where possible.
     * 
     * @since 3.0
     */
    
    static final String PROP_HIDE_VERSION = "hide.version"; //$NON-NLS-1$
    
    /**
     * Returns the project facet identifier. 
     * 
     * @return the project facet identifier
     */

    String getId();
    
    /**
     * Returns the alternate identifiers that are associated with this facet. Aliases are specified
     * via the <code>aliases</code> extension point.
     * 
     * @return the alternate identifiers that are associated with this facet, if any
     */
    
    Set<String> getAliases();
    
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
     * @return the descriptors of all versions of this project facet
     */
    
    Set<IProjectFacetVersion> getVersions();
    
    Set<IProjectFacetVersion> getVersions( String expr )
    
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
     * Returns the latest version of the project facet that exists.
     * 
     * @return returns the latest version of the project facet
     */
    
    IProjectFacetVersion getLatestVersion()
    
    	throws VersionFormatException, CoreException;

    /**
     * Returns the latest version of the project facet that is supported by the
     * given runtime.
     * 
     * @param runtime the runtime
     * @return returns the latest version of the project facet that is supported
     *   by the given runtime
     */
    
    IProjectFacetVersion getLatestSupportedVersion( IRuntime runtime )
    
        throws CoreException;
    
    /**
     * Returns the facet version that should be selected by default. If the
     * default version is not explicitly specified in the facet definition, the
     * latest version (as specified by {@link #getLatestVersion()} method) will
     * be returned. 
     * 
     * @return the facet version that should be selected by default
     */
    
    IProjectFacetVersion getDefaultVersion();

    /**
     * Returns a sorted list containing the descriptors of all versions of this 
     * project facet. 
     * 
     * @param ascending whether version descriptors should be sorted in 
     *   ascending order
     * @return a sorted list containing the descriptors of all versions of this 
     *   project facet
     */
    
    List<IProjectFacetVersion> getSortedVersions( boolean ascending )
    
        throws VersionFormatException, CoreException;
    
    /**
     * Returns the version comparator specified for this project facet. If no 
     * version comparator is specified, this method will return an instance of 
     * the {@link DefaultVersionComparator}.
     * 
     * @return the version comparator specified for this project facet
     */
    
    Comparator<String> getVersionComparator()
    
        throws CoreException;
    
    /**
     * Returns the properties that specify additional information regarding this facet. Some of
     * the properties are recognized and processed by the faceted project framework, while others
     * are there for the benefit of framework's users.
     * 
     * @return the properties of this project facet
     * @since 3.0
     */
    
    Map<String,Object> getProperties();
    
    /**
     * Returns the property value corresponding to the provided name. Properties specify additional
     * information regarding this facet. Some of the properties are recognized and processed by the
     * faceted project framework, while others are there for the benefit of framework's users.
     * 
     * @param name the name of the property
     * @return the value of the property
     * @since 3.0
     */
    
    Object getProperty( String name );
    
}
