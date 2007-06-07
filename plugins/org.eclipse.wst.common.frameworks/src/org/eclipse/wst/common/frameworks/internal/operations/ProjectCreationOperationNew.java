/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Oct 27, 2003
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.eclipse.wst.common.frameworks.internal.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

public class ProjectCreationOperationNew extends AbstractDataModelOperation implements IProjectCreationPropertiesNew {

	public ProjectCreationOperationNew(IDataModel dataModel) {
		super(dataModel);
	}

	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		try {
			IProgressMonitor subMonitor = new SubProgressMonitor(monitor, IProgressMonitor.UNKNOWN);
			IProjectDescription desc = (IProjectDescription) model.getProperty(PROJECT_DESCRIPTION);
			IProject project = (IProject) model.getProperty(PROJECT);
			if (!project.exists()) {
				project.create(desc, subMonitor);
			}
			if (monitor.isCanceled())
				throw new OperationCanceledException();
			subMonitor = new SubProgressMonitor(monitor, IProgressMonitor.UNKNOWN);

			project.open(subMonitor);

			String[] natureIds = (String[]) model.getProperty(PROJECT_NATURES);
			if (null != natureIds) {
				desc = project.getDescription();
				desc.setNatureIds(natureIds);
				project.setDescription(desc, monitor);
			}
		} catch (CoreException e) {
			Logger.getLogger().logError(e);
		} finally {
			monitor.done();
		}
		if (monitor.isCanceled())
			throw new OperationCanceledException();
		return OK_STATUS;
	}

	public boolean canUndo() {
		return false;
	}

	public boolean canRedo() {
		return false;
	}

}
