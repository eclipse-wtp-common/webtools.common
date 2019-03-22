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

package org.eclipse.wst.common.project.facet.ui.internal;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ConvertProjectToFacetedFormRunnable

    implements IRunnableWithProgress
    
{
    private final IProject project;
    
    public static void runInProgressDialog( final Shell shell,
                                            final IProject project )
    {
        final ConvertProjectToFacetedFormRunnable runnable = new ConvertProjectToFacetedFormRunnable( project );
        
        try
        {
            new ProgressMonitorDialog( shell ).run( true, true, runnable );
        }
        catch( InvocationTargetException e )
        {
            FacetUiPlugin.log( e );
        }
        catch( InterruptedException e ) {}
    }
    
    public ConvertProjectToFacetedFormRunnable( final IProject project )
    {
        this.project = project;
    }
    
    public void run( final IProgressMonitor monitor )
    
        throws InvocationTargetException, InterruptedException
        
    {
        monitor.beginTask( Resources.taskConvertingProject, 1000 );
        
        try
        {
            final IProgressMonitor createProgressMonitor = new SubProgressMonitor( monitor, 100 );
            final IFacetedProject fpj = ProjectFacetsManager.create( this.project, true, createProgressMonitor );
            
            if( monitor.isCanceled() )
            {
                throw new InterruptedException();
            }
            
            monitor.setTaskName( Resources.taskDetectingTechnologies );
            
            final IProgressMonitor detectProgressMonitor = new SubProgressMonitor( monitor, 800 );
            final IFacetedProjectWorkingCopy fpjwc = fpj.createWorkingCopy();
            fpjwc.detect( detectProgressMonitor );
            
            monitor.setTaskName( Resources.taskInstallingFacets );
            
            final IProgressMonitor commitChangesProgressMonitor = new SubProgressMonitor( monitor, 100 );
            fpjwc.commitChanges( commitChangesProgressMonitor );
        }
        catch( CoreException e )
        {
            throw new InvocationTargetException( e );
        }
        finally
        {
            monitor.done();
        }
    }
    
    private static final class Resources 
    
        extends NLS
        
    {
        public static String taskConvertingProject;
        public static String taskDetectingTechnologies;
        public static String taskInstallingFacets;
        
        static
        {
            initializeMessages( ConvertProjectToFacetedFormRunnable.class.getName(), Resources.class );
        }
    }
    
}
