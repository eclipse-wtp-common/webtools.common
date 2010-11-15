/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.validation.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

public class ProjectReValidationJob extends WorkspaceJob {
    
    private final IProject project;

    public ProjectReValidationJob(IProject project){
        super(ValMessages.JobName);
        this.project = project;
    }

    @Override
    public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
        try {
            project.build(IncrementalProjectBuilder.FULL_BUILD, ValidationPlugin.VALIDATION_BUILDER_ID, null, monitor);
        }
        catch (Exception e){
            return new Status(IStatus.ERROR,ValidationPlugin.PLUGIN_ID, e.toString(), e);
        }
        return Status.OK_STATUS;
    }

}
