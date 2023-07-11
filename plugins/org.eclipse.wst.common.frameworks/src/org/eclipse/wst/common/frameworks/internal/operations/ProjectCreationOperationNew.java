/*******************************************************************************
 * Copyright (c) 2003, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
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
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonPlugin;

public class ProjectCreationOperationNew extends AbstractDataModelOperation implements IProjectCreationPropertiesNew {

	public ProjectCreationOperationNew(IDataModel dataModel) {
		super(dataModel);
	}

	@Override
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
			WTPCommonPlugin.logError(e);
		} finally {
			monitor.done();
		}
		if (monitor.isCanceled())
			throw new OperationCanceledException();
		return OK_STATUS;
	}

	@Override
	public boolean canUndo() {
		return false;
	}

	@Override
	public boolean canRedo() {
		return false;
	}

}
