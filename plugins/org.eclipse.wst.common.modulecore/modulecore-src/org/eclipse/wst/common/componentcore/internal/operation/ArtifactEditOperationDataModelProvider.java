/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.operation;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.wst.common.componentcore.ArtifactEdit;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonMessages;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonPlugin;

public class ArtifactEditOperationDataModelProvider extends AbstractDataModelProvider implements IArtifactEditOperationDataModelProperties {

	public ArtifactEditOperationDataModelProvider() {
		super();
	}

	public Set getPropertyNames() {
		Set propertyNames = super.getPropertyNames();
		propertyNames.add(TYPE_ID);
		propertyNames.add(PROJECT_NAME);
		propertyNames.add(COMPONENT_NAME);
		propertyNames.add(PROMPT_ON_SAVE);
		propertyNames.add(TARGET_PROJECT);
		propertyNames.add(TARGET_COMPONENT);
		return propertyNames;
	}

	public IProject getTargetProject() {
		String projectName = (String) model.getProperty(IArtifactEditOperationDataModelProperties.PROJECT_NAME);
		if (projectName != null)
			return ProjectUtilities.getProject(projectName);
		return null;
	}

	public Object getDefaultProperty(String propertyName) {
		if (propertyName.equals(PROMPT_ON_SAVE))
			return Boolean.FALSE;
		else if (propertyName.equals(TARGET_PROJECT))
			return getTargetProject();
		else if (propertyName.equals(TARGET_COMPONENT))
			return getTargetComponent();
		else if (propertyName.equals(COMPONENT_NAME))
			return getStringProperty(PROJECT_NAME);
		return super.getDefaultProperty(propertyName);
	}

	/**
	 * @return
	 */
	public WorkbenchComponent getWorkbenchModule() {
		StructureEdit moduleCore = null;
		WorkbenchComponent module = null;
		try {
			moduleCore = StructureEdit.getStructureEditForRead(getTargetProject());
			module = moduleCore.getComponent();
		} finally {
			if (null != moduleCore) {
				moduleCore.dispose();
			}
		}
		return module;
	}
	public boolean propertySet(String propertyName, Object propertyValue) {
		boolean notify = super.propertySet(propertyName, propertyValue);
		if (COMPONENT_NAME.equals(propertyName))
			setProperty(PROJECT_NAME, propertyValue);
		return notify;
	}

	public ArtifactEdit getArtifactEditForRead() {
		WorkbenchComponent module = getWorkbenchModule();
		IProject proj = StructureEdit.getContainingProject(module);
		return ArtifactEdit.getArtifactEditForRead(proj);
	}

	public IStatus validate(String propertyName) {
		IStatus result = super.validate(propertyName);
		if (result != null && !result.isOK())
			return result;
		else if (propertyName.equals(PROJECT_NAME))
			return validateModuleName();
		return result;
	}

	protected IStatus validateModuleName() {
		String moduleName = getStringProperty(PROJECT_NAME);
		if (moduleName == null || moduleName.length() == 0)
			return WTPCommonPlugin.createErrorStatus(WTPCommonPlugin.getResourceString(WTPCommonMessages.PROJECT_NAME_EMPTY));
		return WTPCommonPlugin.OK_STATUS;
	}

	public IVirtualComponent getTargetComponent() {
		String moduleName = getStringProperty(COMPONENT_NAME);
		if (moduleName != null && moduleName.length() > 0)
			return ComponentCore.createComponent(getTargetProject());
		return null;


	}

}
