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

package org.eclipse.jst.common.project.facet.core.libprov.internal;

import static org.eclipse.wst.common.project.facet.core.util.internal.ProgressMonitorUtil.beginTask;
import static org.eclipse.wst.common.project.facet.core.util.internal.ProgressMonitorUtil.done;
import static org.eclipse.wst.common.project.facet.core.util.internal.ProgressMonitorUtil.worked;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jst.common.project.facet.core.ClasspathHelper;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryProviderOperation;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryProviderOperationConfig;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class RuntimeLibraryProviderInstallOperation

    extends LibraryProviderOperation
    
{
    public void execute( final LibraryProviderOperationConfig config,
                         final IProgressMonitor monitor )
    
        throws CoreException
        
    {
        beginTask( monitor, "", 1 ); //$NON-NLS-1$
        
        try
        {
            final IProjectFacetVersion fv = config.getProjectFacetVersion();
            
            ClasspathHelper.addClasspathEntries( config.getFacetedProject().getProject(), fv );
            
            worked( monitor, 1 );
        }
        finally
        {
            done( monitor );
        }
    }
    
}
