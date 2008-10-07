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

package org.eclipse.jst.common.project.facet.core.libprov.user;

import static org.eclipse.wst.common.project.facet.core.util.internal.ProgressMonitorUtil.beginTask;
import static org.eclipse.wst.common.project.facet.core.util.internal.ProgressMonitorUtil.done;
import static org.eclipse.wst.common.project.facet.core.util.internal.ProgressMonitorUtil.worked;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jst.common.project.facet.core.internal.ClasspathUtil;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryProviderOperation;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryProviderOperationConfig;

/**
 * The uninstall operation corresponding to the user-library-provider that uses JDT user library facility
 * for managing libraries. This class can be subclassed by those wishing to extend the base implementation
 * supplied by the framework.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @since WTP 3.1
 */

public class UserLibraryProviderUninstallOperation

    extends LibraryProviderOperation
    
{
    /**
     * Runs the library provider operation. Subclasses can override.
     * 
     * @param config the library provider operation config; will never be null
     * @param monitor the progress monitor for status reporting and cancellation
     * @throws CoreException if failed while executing the operation
     */
    
    public void execute( final LibraryProviderOperationConfig config,
                         final IProgressMonitor monitor )
    
        throws CoreException
        
    {
        beginTask( monitor, "", 1 ); //$NON-NLS-1$
        
        try
        {
            final IProject project = config.getFacetedProject().getProject();
            ClasspathUtil.removeClasspathEntries( project, config.getProjectFacet() );
            
            worked( monitor, 1 );
        }
        finally
        {
            done( monitor );
        }
    }
    
}
