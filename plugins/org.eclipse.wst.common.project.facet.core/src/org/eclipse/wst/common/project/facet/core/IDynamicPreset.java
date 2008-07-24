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

import java.util.Map;

import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

/**
 * A dynamic preset is type of preset that needs to be resolved before it can be used. The resolve
 * operation relies the {@link IPresetFactory} specified when the dynamic preset is registered via
 * the <code>presets</code> extension point and can take into account the context in which the 
 * preset will be used. 
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @since 2.0
 */

public interface IDynamicPreset

    extends IPreset
    
{
    /**
     * The key of the context map entry whose value is an {@link IFacetedProjectBase} object.
     * 
     * @since 3.0
     */
    
    static final String CONTEXT_KEY_FACETED_PROJECT = "CONTEXT_KEY_FACETED_PROJECT"; //$NON-NLS-1$
    
    /**
     * The key of the context map entry whose value is a set of {@link IProjectFacet} objects
     * representing the fixed facets.
     */
    
    static final String CONTEXT_KEY_FIXED_FACETS = "CONTEXT_KEY_FIXED_FACETS"; //$NON-NLS-1$
    
    /**
     * The key of the context map entry whose value is the primary runtime ({@link IRuntime}) or
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
