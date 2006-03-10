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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This interface is implemented in order to provide logic associated with
 * a particular event in project facet's life cycle, such as install or 
 * uninstall.
 * 
 * <p><i>This class is part of an interim API that is still under development 
 * and expected to change significantly before reaching stability. It is being 
 * made available at this early stage to solicit feedback from pioneering 
 * adopters on the understanding that any code that uses this API will almost 
 * certainly be broken (repeatedly) as the API evolves.</i></p>
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public interface IDelegate 
{
    /**
     * The method that's called to execute the delegate.
     * 
     * @param project the workspace project
     * @param fv the project facet version that this delegate is handling; this
     *   is useful when sharing the delegate among several versions of the same
     *   project facet or even different project facets
     * @param config the configuration object, or <code>null</code> if defaults
     *   should be used
     * @param monitor the progress monitor
     * @throws CoreException if the delegate fails for any reason
     */
    
    void execute( IProject project,
                  IProjectFacetVersion fv,
                  Object config,
                  IProgressMonitor monitor )
    
        throws CoreException;
    
}
