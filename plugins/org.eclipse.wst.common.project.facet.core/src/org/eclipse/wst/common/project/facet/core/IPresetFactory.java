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

import org.eclipse.core.runtime.CoreException;

/**
 * A preset factory is used for creating a dynamic preset. Unlike a static preset, which is fully
 * specified from the start, a dynamic preset uses a factory to synthesize the preset definition on 
 * the fly based on the context in which it will be used. 
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @since 2.0
 */

public interface IPresetFactory
{
    /**
     * The factory method for creating a preset definition. The definition contains all of the 
     * information necessary for the system to create a preset. The factory can take into account 
     * the context that the preset will be used in when creating a preset definition. See
     * {@link IDynamicPreset} for documentation on what type of information the context can contain.
     *
     * @param presetId the id of the preset
     * @param context the information about context that this preset will be used in
     * @return the created preset definition or <code>null</code> if this factory is not applicable 
     *   to the provided context
     * @throws CoreException if failed while creating the preset definition
     */
    
    PresetDefinition createPreset( String presetId,
                                   Map<String,Object> context )
    
        throws CoreException;
    
}
