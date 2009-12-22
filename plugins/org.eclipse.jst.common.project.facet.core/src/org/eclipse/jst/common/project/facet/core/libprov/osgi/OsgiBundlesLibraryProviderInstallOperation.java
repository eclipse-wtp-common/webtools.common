/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.jst.common.project.facet.core.libprov.osgi;

import static org.eclipse.jst.common.project.facet.core.libprov.osgi.OsgiBundlesContainer.addToClasspath;
import static org.eclipse.jst.common.project.facet.core.libprov.osgi.OsgiBundlesContainer.isOnClasspath;
import static org.eclipse.jst.common.project.facet.core.libprov.osgi.OsgiBundlesContainer.*;
import static org.eclipse.wst.common.project.facet.core.util.internal.ProgressMonitorUtil.beginTask;
import static org.eclipse.wst.common.project.facet.core.util.internal.ProgressMonitorUtil.done;
import static org.eclipse.wst.common.project.facet.core.util.internal.ProgressMonitorUtil.worked;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryProviderOperation;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryProviderOperationConfig;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;


/**
 * @since 1.4
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class OsgiBundlesLibraryProviderInstallOperation

    extends LibraryProviderOperation

{
    public void execute( final LibraryProviderOperationConfig config,
                         final IProgressMonitor monitor )

        throws CoreException

    {
        beginTask( monitor, "", 1 ); //$NON-NLS-1$

        try
        {
            final OsgiBundlesLibraryProviderInstallOperationConfig cfg
                = (OsgiBundlesLibraryProviderInstallOperationConfig) config;
            
            IProject project = cfg.getFacetedProject().getProject();
            
            if( project == null ) // TODO: Galileo - Remove this workaround.
            {
                final String name = ( (IFacetedProjectWorkingCopy) config.getFacetedProject() ).getProjectName();
                project = ResourcesPlugin.getWorkspace().getRoot().getProject( name );
            }
            
            final IProjectFacet facet = cfg.getProjectFacet();
            
            setBundleReferences( project, facet, cfg.getBundleReferences() );
            setContainerLabel( project, facet, cfg.getContainerLabel() );
            
            if( ! isOnClasspath( project, facet ) )
            {
                addToClasspath( project, facet, cfg.getClasspathAttributes() );
            }

        	worked( monitor, 1 );
        }
        finally
        {
            done( monitor );
        }
    }
    
}
