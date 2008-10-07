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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.common.project.facet.core.internal.ClasspathUtil;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryProviderOperation;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryProviderOperationConfig;

/**
 * The install operation corresponding to the user-library-provider that uses JDT user library facility
 * for managing libraries. This class can be subclassed by those wishing to extend the base implementation
 * supplied by the framework.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @since WTP 3.1
 */

public class UserLibraryProviderInstallOperation

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
            final UserLibraryProviderInstallOperationConfig cfg
                = (UserLibraryProviderInstallOperationConfig) config;
            
            final List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
            
            for( String libraryName : cfg.getLibraryNames() )
            {
                entries.add( createClasspathEntry( cfg, libraryName ) );
            }
            
            final IProject project = config.getFacetedProject().getProject();
            ClasspathUtil.addClasspathEntries( project, config.getProjectFacet(), entries );
            
            worked( monitor, 1 );
        }
        finally
        {
            done( monitor );
        }
    }
    
    /**
     * Constructs a classpath entry for the provided user library. Subclasses can override.
     * 
     * @param config the user library provider install operation config
     * @param libraryName the name of a JDT user library
     * @return the classpath entry for the provided user library
     */
    
    protected IClasspathEntry createClasspathEntry( final UserLibraryProviderInstallOperationConfig config,
                                                    final String libraryName )
    {
        final IPath containerPath = new Path( JavaCore.USER_LIBRARY_CONTAINER_ID ).append( libraryName );
        return JavaCore.newContainerEntry( containerPath );
    }
    
}
