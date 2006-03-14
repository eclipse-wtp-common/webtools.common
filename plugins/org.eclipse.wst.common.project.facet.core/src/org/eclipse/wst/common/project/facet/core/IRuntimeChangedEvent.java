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

import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

/**
 * Describes the runtime changed event to the RUNTIME_CHANGED event handlers.
 * 
 * <p><i>This class is part of an interim API that is still under development 
 * and expected to change significantly before reaching stability. It is being 
 * made available at this early stage to solicit feedback from pioneering 
 * adopters on the understanding that any code that uses this API will almost 
 * certainly be broken (repeatedly) as the API evolves.</i></p>
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public interface IRuntimeChangedEvent
{
    /**
     * Returns the runtime previously associated with the project.
     * 
     * @return the runtime previously associated with the project, or
     *   <code>null</code>
     */
    
    IRuntime getOldRuntime();
    
    /**
     * Returns the runtime that's now associated with the project.
     * 
     * @return the runtime that's now associated with the project, or
     *   <code>null</code>
     */
    
    IRuntime getNewRuntime();
    
}
