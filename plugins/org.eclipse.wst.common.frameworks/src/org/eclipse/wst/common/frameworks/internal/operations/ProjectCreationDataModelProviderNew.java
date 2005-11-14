/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.frameworks.internal.operations;

import java.io.File;
import java.util.Set;

import org.eclipse.core.internal.resources.Resource;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.internal.WTPPlugin;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonMessages;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonPlugin;

public class ProjectCreationDataModelProviderNew extends AbstractDataModelProvider implements IProjectCreationPropertiesNew {

	public IDataModelOperation getDefaultOperation() {
		return new ProjectCreationOperation(model);
	}

	public void init() {
		super.init();
	}

	public Set getPropertyNames() {
		Set propertyNames = super.getPropertyNames();
		propertyNames.add(PROJECT);
		propertyNames.add(PROJECT_NAME);
		propertyNames.add(PROJECT_LOCATION);
		propertyNames.add(USE_DEFAULT_LOCATION);
		propertyNames.add(DEFAULT_LOCATION);
		propertyNames.add(USER_DEFINED_LOCATION);
		propertyNames.add(PROJECT_NATURES);
		propertyNames.add(PROJECT_DESCRIPTION);
		return propertyNames;
	}

	public Object getDefaultProperty(String propertyName) {
		if (propertyName.equals(PROJECT_LOCATION)) {
			if (getBooleanProperty(USE_DEFAULT_LOCATION)) {
				return null;
			}
			return getProperty(USER_DEFINED_LOCATION);
		} else if (DEFAULT_LOCATION.equals(propertyName)) {
			IPath path = ResourcesPlugin.getWorkspace().getRoot().getLocation();
			path = path.append(getStringProperty(PROJECT_NAME));
			return path.toOSString();
		} else if (USE_DEFAULT_LOCATION.equals(propertyName)) {
			return Boolean.TRUE;
		} else if (USER_DEFINED_LOCATION.equals(propertyName)) {
			return "";
		} else if (propertyName.equals(PROJECT_DESCRIPTION))
			return getProjectDescription();
		return super.getDefaultProperty(propertyName);
	}

	public boolean propertySet(String propertyName, Object propertyValue) {
		if (propertyName.equals(PROJECT_LOCATION) || propertyName.equals(DEFAULT_LOCATION) || propertyName.equals(PROJECT_DESCRIPTION)) {
			throw new RuntimeException();
		} else if (propertyName.equals(PROJECT_NAME)) {
			IStatus stat = model.validateProperty(PROJECT_NAME);
			if (stat != OK_STATUS)
				return true;
			model.setProperty(PROJECT, getProject());
			model.notifyPropertyChange(DEFAULT_LOCATION, IDataModel.VALUE_CHG);
			if (getBooleanProperty(USE_DEFAULT_LOCATION)) {
				model.notifyPropertyChange(PROJECT_LOCATION, IDataModel.VALUE_CHG);
			}
		} else if (propertyName.equals(USE_DEFAULT_LOCATION)) {
			model.notifyPropertyChange(PROJECT_LOCATION, IDataModel.VALUE_CHG);
		} else if (propertyName.equals(USER_DEFINED_LOCATION) && !getBooleanProperty(USE_DEFAULT_LOCATION)) {
			model.notifyPropertyChange(PROJECT_LOCATION, IDataModel.VALUE_CHG);
		}
		return true;
	}

	private IProjectDescription getProjectDescription() {
		String projectName = (String) getProperty(PROJECT_NAME);
		IProjectDescription desc = ResourcesPlugin.getWorkspace().newProjectDescription(projectName);
		String projectLocation = (String) getProperty(PROJECT_LOCATION);
		if (projectLocation != null)
			desc.setLocation(new Path(projectLocation));
		else
			desc.setLocation(null);
		return desc;
	}

	protected IProject getProject() {
		String projectName = (String) getProperty(PROJECT_NAME);
		return (null != projectName && projectName.length() > 0) ? ResourcesPlugin.getWorkspace().getRoot().getProject(projectName) : null;
	}

	public IStatus validate(String propertyName) {
		if (propertyName.equals(PROJECT_NAME)) {
			String name = model.getStringProperty(PROJECT_NAME);
			IStatus status = validateName( name );
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

	public static IStatus validateName(String name) {
		IStatus status = validateProjectName(name);
		if (!status.isOK())
			return status;
		IProject project = ProjectUtilities.getProject( name );
		if (project.exists())
			return WTPCommonPlugin.createErrorStatus(WTPCommonPlugin.getResourceString(WTPCommonMessages.PROJECT_EXISTS_ERROR, new Object[]{name}));

		if (!WTPPlugin.isPlatformCaseSensitive()) {
			// now look for a matching case variant in the tree
			IResource variant = ((Resource) project).findExistingResourceVariant(project.getFullPath());
			if (variant != null) {
				// TODO Fix this string
				return WTPCommonPlugin.createErrorStatus("Resource already exists with a different case."); //$NON-NLS-1$
			}
		}
		return OK_STATUS;
	}
	

	private IStatus validateLocation() {
		String loc = (String) getProperty(PROJECT_LOCATION);
		if (null != loc) {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IPath path = new Path(loc);
			return workspace.validateProjectLocation(getProject(), path);
		}
		return OK_STATUS;
	}
}