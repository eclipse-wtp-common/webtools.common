/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.jst.common.project.facet.core.libprov;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This class should be subclassed in order to provider the logic that should execute
 * when a library provider action (such as install and uninstall) is triggered.
 *
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @since 1.4
 */

public abstract class LibraryProviderOperation
{
    /**
     * Runs the library provider operation.
     * 
     * @param config the library provider operation config; will never be null
     * @param monitor the progress monitor for status reporting and cancellation
     * @throws CoreException if failed while executing the operation
     */
    
    public abstract void execute( final LibraryProviderOperationConfig config,
                                  final IProgressMonitor monitor )
    
        throws CoreException;
    
}
