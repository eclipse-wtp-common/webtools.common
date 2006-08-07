/******************************************************************************
 * Copyright (c) 2006 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core;

/**
 * This interface can be implemented in order to provide the facet version that
 * is selected by default. If a version provider is not specified, the latest
 * version will be used. Note that if a runtime is selected, the runtime can 
 * override this default with a version best suited for that runtime. 
 *  
 * <p><i>This class is part of an interim API that is still under development 
 * and expected to change significantly before reaching stability. It is being 
 * made available at this early stage to solicit feedback from pioneering 
 * adopters on the understanding that any code that uses this API will almost 
 * certainly be broken (repeatedly) as the API evolves.</i></p>
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public interface IDefaultVersionProvider
{
    /**
     * Returns the facet version that should be selected by default.
     * 
     * @return the facet version that should be selected by default
     */
    
    IProjectFacetVersion getDefaultVersion();

}
