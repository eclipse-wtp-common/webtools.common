/******************************************************************************
 * Copyright (c) 2008 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.jst.common.project.facet.core.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jst.common.project.facet.core.ClasspathHelper;
import org.eclipse.wst.common.project.facet.core.FacetedProjectFrameworkException;
import org.eclipse.wst.common.project.facet.core.IDelegate;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public abstract class JavaFacetDelegate 

    implements IDelegate 
    
{
    protected static final class RelevantFiles
    {
        public final IFile dotProjectFile;
        public final IFile dotClasspathFile;
        public final IFile jdtCorePrefsFile;
        public final IFile jstFacetCorePrefsFile;
        
        public RelevantFiles( final IProject project )
        {
            this.dotProjectFile = project.getFile( IProjectDescription.DESCRIPTION_FILE_NAME );
            this.dotClasspathFile = project.getFile( JavaFacetUtil.FILE_CLASSPATH );
            this.jdtCorePrefsFile = project.getFile( JavaFacetUtil.FILE_JDT_CORE_PREFS );
            this.jstFacetCorePrefsFile = project.getFile( ClasspathHelper.METADATA_FILE_NAME );
        }
    }
    
    protected static void validateEdit( final IProject project )
    
        throws CoreException
        
    {
        validateEdit( new RelevantFiles( project ) );
    }
    
    protected static void validateEdit( final RelevantFiles files )
    
        throws CoreException
        
    {
        final List<IFile> list = new ArrayList<IFile>();
        
        list.add( files.dotProjectFile );
        
        if( files.dotClasspathFile.exists() )
        {
            list.add( files.dotClasspathFile );
        }
        
        if( files.jdtCorePrefsFile.exists() )
        {
            list.add( files.jdtCorePrefsFile );
        }
        
        if( files.jstFacetCorePrefsFile.exists() )
        {
            list.add( files.jstFacetCorePrefsFile );
        }
        
        final IWorkspace ws = ResourcesPlugin.getWorkspace();
        
        final IStatus validateEditStatus 
            = ws.validateEdit( list.toArray( new IFile[ list.size() ] ), IWorkspace.VALIDATE_PROMPT );
        
        if( validateEditStatus.getSeverity() == IStatus.ERROR )
        {
            final FacetedProjectFrameworkException e 
                = new FacetedProjectFrameworkException( validateEditStatus );
            
            e.setExpected( true );
            
            throw e;
        }
    }

}
