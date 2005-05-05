/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
/*
 * Created on Oct 27, 2003
 * 
 * To change the template for this generated file go to Window&gt;Preferences&gt;Java&gt;Code
 * Generation&gt;Code and Comments
 */
package org.eclipse.wst.common.frameworks.internal.operations;

import java.io.File;

import org.eclipse.core.internal.localstore.CoreFileSystemLibrary;
import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonMessages;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonPlugin;

public class ProjectCreationDataModelProvider extends AbstractDataModelProvider implements IProjectCreationProperties {

	public IDataModelOperation getDefaultOperation() {
		return new ProjectCreationOp(model);
	}
    public void init() {
        super.init();
        model.setProperty(PROJECT_LOCATION, getDefaultLocation());
        model.setProperty(PROJECT_DESCRIPTION, getProjectDescription());    
    }
	public String[] getPropertyNames() {
		return new String[]{PROJECT, PROJECT_NAME, PROJECT_LOCATION, PROJECT_NATURES, PROJECT_DESCRIPTION};
	}

	public Object getDefaultProperty(String propertyName) {
		return super.getDefaultProperty(propertyName);
	}

	public boolean propertySet(String propertyName, Object propertyValue) {
		if (propertyValue != null && propertyName.equals(PROJECT_LOCATION)) {
			IPath path = getRootLocation();
			if (path.equals(new Path((String) propertyValue))) {
				setProperty(propertyName, null);
				return false;
			}
		}
		if (propertyName.equals(PROJECT_NAME) && !getDataModel().isPropertySet(PROJECT_LOCATION)) {
			model.notifyPropertyChange(PROJECT_NAME, IDataModel.VALUE_CHG);
			model.notifyPropertyChange(PROJECT_LOCATION, IDataModel.VALUE_CHG);
            model.setProperty(PROJECT, getProject());
			return false;
		}
		return true;
	}

	private String getDefaultLocation() {
		IPath path = getRootLocation();
		String projectName = (String) getProperty(PROJECT_NAME);
		if (projectName != null)
			path = path.append(projectName);
		return path.toOSString();
	}

	private IPath getRootLocation() {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation();
	}

	private IProjectDescription getProjectDescription() {
		String projectName = (String) getProperty(PROJECT_NAME);
		IProjectDescription desc = ResourcesPlugin.getWorkspace().newProjectDescription(projectName);
		if (getDataModel().isPropertySet(PROJECT_LOCATION)) {
			String projectLocation = (String) getProperty(ProjectCreationDataModelProvider.PROJECT_LOCATION);
			desc.setLocation(new Path(projectLocation));
		}
		return desc;
	}

	protected IProject getProject() {
		String projectName = (String) getProperty(PROJECT_NAME);
		return (null != projectName && projectName.length() > 0) ? ResourcesPlugin.getWorkspace().getRoot().getProject(projectName) : null;
	}

	public IStatus validate(String propertyName) {
		if (propertyName.equals(PROJECT_NAME)) {
			IStatus status = validateName();
			if (!status.isOK())
				return status;
		}
		if (propertyName.equals(PROJECT_LOCATION)) {
			IStatus status = validateLocation();
			if (!status.isOK())
				return status;
		}
		if (propertyName.equals(PROJECT_LOCATION) || propertyName.equals(PROJECT_NAME)) {
			String projectName = getStringProperty(PROJECT_NAME);
			String projectLoc = getStringProperty(PROJECT_LOCATION);
			return validateExisting(projectName, projectLoc);
		}
		return OK_STATUS;
	}

	/**
	 * @param projectName
	 * @param projectLoc
	 * @todo Generated comment
	 */
	private IStatus validateExisting(String projectName, String projectLoc) {
		if (projectName != null && !projectName.equals("")) {//$NON-NLS-1$
			File file = new File(projectLoc);
			if (file.exists()) {
				if (file.isDirectory()) {
					File dotProject = new File(file, ".project");//$NON-NLS-1$
					if (dotProject.exists()) {
						return WTPCommonPlugin.createErrorStatus(WTPCommonPlugin.getResourceString(WTPCommonMessages.PROJECT_EXISTS_AT_LOCATION_ERROR, new Object[]{file.toString()}));
					}
				}
			}
		}
		return OK_STATUS;
	}

	public static IProject getProjectHandleFromProjectName(String projectName) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IStatus status = workspace.validateName(projectName, IResource.PROJECT);
		return (null != projectName && projectName.length() > 0 && status.isOK()) ? ResourcesPlugin.getWorkspace().getRoot().getProject(projectName) : null;
	}

	public static IStatus validateProjectName(String projectName) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IStatus status = workspace.validateName(projectName, IResource.PROJECT);
		if (!status.isOK())
			return status;
		return OK_STATUS;
	}

	private IStatus validateName() {
		String name = getStringProperty(PROJECT_NAME);
		IStatus status = validateProjectName(name);
		if (!status.isOK())
			return status;
		if (getProject().exists())
			return WTPCommonPlugin.createErrorStatus(WTPCommonPlugin.getResourceString(WTPCommonMessages.PROJECT_EXISTS_ERROR, new Object[]{name}));

		if (!CoreFileSystemLibrary.isCaseSensitive()) {
			// now look for a matching case variant in the tree
			IResource variant = ((Project) getProject()).findExistingResourceVariant(getProject().getFullPath());
			if (variant != null) {
				// TODO Fix this string
				return WTPCommonPlugin.createErrorStatus("Resource already exists with a different case.");
			}
		}
		return OK_STATUS;
	}

	private IStatus validateLocation() {
		if (getDataModel().isPropertySet(PROJECT_LOCATION)) {
			String loc = (String) getProperty(PROJECT_LOCATION);
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IPath path = new Path(loc);
			return workspace.validateProjectLocation(getProject(), path);
		}
		return OK_STATUS;
	}
}