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

package org.eclipse.wst.common.project.facet.core;

import java.util.Map;

/**
 * A dynamic preset is type of preset that needs to be resolved before it can be used. The resolve
 * operation relies the {@see IPresetFactory} specified when the dynamic preset is registered via
 * the <code>presets</code> extension point and can take into account the context in which the 
 * preset will be used. 
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 * @since 2.0
 */

public interface IDynamicPreset

    extends IPreset
    
{
    /**
     * The key of the context map entry whose value is an {@see IFacetedProjectBase} object.
     * 
     * @since 3.0
     */
    
    static final String CONTEXT_KEY_FACETED_PROJECT = "CONTEXT_KEY_FACETED_PROJECT"; //$NON-NLS-1$
    
    /**
     * The key of the context map entry whose value is a set of {@see IProjectFacet} objects
     * representing the fixed facets.
     */
    
    static final String CONTEXT_KEY_FIXED_FACETS = "CONTEXT_KEY_FIXED_FACETS"; //$NON-NLS-1$
    
    /**
     * The key of the context map entry whose value is the primary runtime ({@see IRuntime}) or
     * <code>null</code> if no runtime has been selected.
     */
    
    static final String CONTEXT_KEY_PRIMARY_RUNTIME = "CONTEXT_KEY_PRIMARY_RUNTIME";  //$NON-NLS-1$
    
    /**
     * Resolves the dynamic preset using the provided context information. If this preset is not
     * applicable to the provided context, this method will return <code>null</code>.
     * 
     * @param context the information about context that this preset will be used in
     * @return the resolved preset or <code>null</code>
     */
   
    IPreset resolve( Map<String,Object> context );
    
}
