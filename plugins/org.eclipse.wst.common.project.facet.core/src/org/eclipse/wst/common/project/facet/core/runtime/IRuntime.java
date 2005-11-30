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

package org.eclipse.wst.common.project.facet.core.runtime;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * Represents a configured instance of a runtime. A runtime instance is composed
 * of multiple runtime components.
 * 
 * <p><i>This class is part of an interim API that is still under development 
 * and expected to change significantly before reaching stability. It is being 
 * made available at this early stage to solicit feedback from pioneering 
 * adopters on the understanding that any code that uses this API will almost 
 * certainly be broken (repeatedly) as the API evolves.</i></p>
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public interface IRuntime

    extends IAdaptable
    
{
    /**
     * Returns the name of this runtime. The runtime name is unique within the
     * workspace.
     * 
     * @return the name of this runtime
     */
    
    String getName();
    
    /**
     * Returns the runtime components that comprise this runtime. Note that the
     * order is important since for some operations components are consoluted
     * in order and the first one capable of performing the opeation wins.
     *  
     * @return the runtime components that comprise this runtime (element type: 
     *   {@see IRuntimeComponent})
     */
    
    List getRuntimeComponents();
    
    /**
     * Returns the properties associated with this runtime component. The
     * contents will vary dependending on how the runtime was created and what
     * component types/versions it's comprised of.
     * 
     * @return the properties associated with this runtime (key type: 
     *   {@see String}, value type: {@see String})
     */
    
    Map getProperties();
    
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
    
}
