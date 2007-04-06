/******************************************************************************
 * Copyright (c) 2005-2007 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.internal.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

/**
 * Contains utility functions for dealing with files.
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class FileUtil
{
    public static final String FILE_DOT_PROJECT = ".project"; //$NON-NLS-1$
    
    public static void validateEdit( final IFile... files )
    
        throws CoreException
        
    {
        /*final IWorkspace ws = ResourcesPlugin.getWorkspace();
        final IStatus st = ws.validateEdit( files, IWorkspace.VALIDATE_PROMPT );
        
        if( st.getSeverity() == IStatus.ERROR )
        {
            throw new CoreException( st );
        }*/
    }

    private FileUtil() {}
    
}
