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

import org.eclipse.core.runtime.CoreException;

/**
 * This interface is implemented in order to provide a method for creating 
 * a config object that will be used for parameterizing the facet action
 * delegate.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public interface IActionConfigFactory
{
    /**
     * Creates a new facet action configuration object. The new configuration
     * object should ideally be populated with reasonable defaults.
     * 
     * @return a new facet action configuration object
     * @throws CoreException if failed while creating the configuration object
     */
    
    Object create()
    
        throws CoreException;
    
}
