/******************************************************************************
 * Copyright (c) 2005-2007 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.runtime;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * Represents a configured instance of a runtime. A runtime instance is composed of multiple 
 * runtime components. 
 * 
 * <p>This interface is not intended to be implemented outside of this framework. Client code can 
 * get access to <code>IRuntime</code> objects by using methods on the {@see RuntimeManager} 
 * class.</p>  
 * 
 * @see RuntimeManager.getRuntimes()
 * @see RuntimeManager.getRuntime(String)
 * @see RuntiemManager.isRuntimeDefined(String)
 * @see RuntimeManager.defineRuntime(String,List<IRuntimeComponent>,Map<String,String>)
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public interface IRuntime

    extends IAdaptable
    
{
    /**
     * Returns the name of this runtime. The runtime name is unique within the workspace.
     * 
     * @return the name of this runtime
     */
    
    String getName();
    
    /**
     * Returns the localized name of this runtime. If not specified, this will default to the name
     * of the runtime as returned by the {@see getName()} method. 
     * 
     * <p>The localized name should be used in all communications with the user while all metadata 
     * references to the runtime should use the unlocalized name. Note that there is an inherent 
     * danger in this. If a runtime is deleted or renamed, existing projects that use that runtime 
     * might become invalid. In that case the system will need to communicate that problem to the 
     * user using the unlocalized name for the runtime. Since the user is never exposed to runtime's 
     * unlocalized name, the user can have trouble understanding and correcting the problem. A
     * similar situation can arise when a project is imported into another user's workspace.</p>
     * 
     * <p>To mitigate the above risks, the name localization feature is expected to be used 
     * sparingly and only in contexts where the runtime provider can take steps to guarantee that
     * the above situation is not likely to occur. One scenario where name localization is
     * acceptable is when a runtime is auto-created and the user is not given ability to delete or
     * rename it.</p>
     * 
     * @return the localized name of this runtime
     */
    
    String getLocalizedName();
    
    /**
     * Returns the set of other names (if any) that this runtime may be known by. The localized
     * name (if specified) will be present in the alternate names set. 
     * 
     * @return the set of alternate names for this runtime or an empty set
     * @since 3.0
     */
    
    Set<String> getAlternateNames();
    
    /**
     * Returns the runtime components that comprise this runtime. Note that the
     * order is important since for some operations components are consoluted
     * in order and the first one capable of performing the opeation wins.
     *  
     * @return the runtime components that comprise this runtime
     */
    
    List<IRuntimeComponent> getRuntimeComponents();
    
    /**
     * Returns the properties associated with this runtime component. The
     * contents will vary dependending on how the runtime was created and what
     * component types/versions it's comprised of.
     * 
     * @return the properties associated with this runtime
     */
    
    Map<String,String> getProperties();
    
    /**
     * Returns the value of the specified property.
     * 
     * @param name the property name
     * @return the property value, or <code>null</code>
     */
    
    String getProperty( String name );
    
    /**
     * Determines whether this runtime supports the specified project facet.
     * The runtime supports a project facet if any of it's components support
     * the project facet. The support mappings are specified using the
     * <code>org.eclipse.wst.common.project.facet.core.runtime</code> extension
     * point.
     * 
     * @param fv the project facet version
     * @return <code>true</code> if this runtime supports the specified facet,
     *   <code>false</code> otherwise
     */
    
    boolean supports( IProjectFacetVersion fv );
    
    boolean supports( IProjectFacet f );
    
    /**
     * Returns the facets (and the versions) that should be selected by default 
     * when this runtime is selected. This information is drawn from what's
     * specified through the <code>org.eclipse.wst.common.project.facet.core.defaultFacets</code>
     * extension point. The returned list is filtered by removing facets that
     * would conflict with the specified fixed facets. Note that the returned 
     * set will always include the fixed facets. If the default version for any 
     * fixed facet is not explicitly specified through the above extension 
     * point, the latest version will be used.
     * 
     * @param fixed the fixed facets
     * @return the default facets
     * @throws CoreException if failed for any reason
     */
    
    Set<IProjectFacetVersion> getDefaultFacets( final Set<IProjectFacet> fixed )
    
        throws CoreException;
    
    /**
     * Performs a number of runtime-specific checks to determine whether this runtime
     * instance is valid or not.
     * 
     * @param monitor used for getting progress information and canceling validation
     * @return the result of validation
     * @since 3.0
     */
    
    IStatus validate( IProgressMonitor monitor );
    
}
