/*******************************************************************************
 * Copyright (c) 2003, 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.modulecore.internal.operation;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.common.frameworks.internal.operations.ProjectCreationDataModel;
import org.eclipse.wst.common.frameworks.operations.WTPOperationDataModel;
/**
 * This dataModel is a common super class used for create in a module context.
 * 
 * This class (and all its fields and methods) is likely to change during the WTP 1.0 milestones as
 * the new project structures are adopted. Use at your own risk.
 * 
 * @since WTP 1.0
 */
public abstract class ArtifactEditOperationDataModel extends WTPOperationDataModel {
	/**
	 * Required
	 */
	public static final String PROJECT_NAME = "ArtifactEditOperationDataModel.PROJECT_NAME"; //$NON-NLS-1$
	/**
	 * Required
	 */
	public static final String MODULE_NAME = "ArtifactEditOperationDataModel.MODULE_NAME"; //$NON-NLS-1$
	/**
	 * Optional, should save with prompt...defaults to false
	 */
	public static final String PROMPT_ON_SAVE = "ArtifactEditOperationDataModel.PROMPT_ON_SAVE"; //$NON-NLS-1$
	
	protected void initValidBaseProperties() {
		super.initValidBaseProperties();
		addValidBaseProperty(PROJECT_NAME);
		addValidBaseProperty(MODULE_NAME);
		addValidBaseProperty(PROMPT_ON_SAVE);
	}

	public IProject getTargetProject() {
		return ProjectCreationDataModel.getProjectHandleFromProjectName(getStringProperty(PROJECT_NAME));
	}

	protected Object getDefaultProperty(String propertyName) {
		if (propertyName.equals(PROMPT_ON_SAVE))
			return Boolean.FALSE;
		return super.getDefaultProperty(propertyName);
	}
}
