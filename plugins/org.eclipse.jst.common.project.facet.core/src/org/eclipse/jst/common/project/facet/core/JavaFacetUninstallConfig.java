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

package org.eclipse.jst.common.project.facet.core;

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.jst.common.project.facet.core.internal.JavaFacetUtil;
import org.eclipse.wst.common.project.facet.core.ActionConfig;

/**
 * @since 3.1
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public class JavaFacetUninstallConfig

    extends ActionConfig
    
{
    @Override
    public Set<IFile> getValidateEditFiles()
    {
        final Set<IFile> files = super.getValidateEditFiles();
        final IProject project = getFacetedProjectWorkingCopy().getProject();
        
        files.add( project.getFile( IProjectDescription.DESCRIPTION_FILE_NAME ) );
        files.add( project.getFile( JavaFacetUtil.FILE_CLASSPATH ) );
        files.add( project.getFile( JavaFacetUtil.FILE_JDT_CORE_PREFS ) );
        files.add( project.getFile( ClasspathHelper.LEGACY_METADATA_FILE_NAME ) );
        
        return files;
    }
    
}
