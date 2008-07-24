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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This interface is implemented in order to provide logic associated with
 * a particular event in project facet's life cycle, such as install or 
 * uninstall.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
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
