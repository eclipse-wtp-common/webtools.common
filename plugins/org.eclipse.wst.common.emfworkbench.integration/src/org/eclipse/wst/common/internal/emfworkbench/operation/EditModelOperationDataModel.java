/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
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
package org.eclipse.wst.common.internal.emfworkbench.operation;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel;
import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModel;

import com.ibm.wtp.emf.workbench.WorkbenchResourceHelperBase;

public abstract class EditModelOperationDataModel extends WTPOperationDataModel {

	/**
	 * Required
	 */
	public static final String PROJECT_NAME = "EditModelOperationDataModel.PROJECT_NAME"; //$NON-NLS-1$
	/**
	 * Required
	 */
	public static final String EDIT_MODEL_ID = "EditModelOperationDataModel.EDIT_MODEL_ID"; //$NON-NLS-1$
	/**
	 * Optional, should save with prompt...defaults to false
	 */
	public static final String PROMPT_ON_SAVE = "EditModelOperationDataModel.PROMPT_ON_SAVE"; //$NON-NLS-1$

	protected void initValidBaseProperties() {
		super.initValidBaseProperties();
		addValidBaseProperty(PROJECT_NAME);
		addValidBaseProperty(EDIT_MODEL_ID);
		addValidBaseProperty(PROMPT_ON_SAVE);
	}

	public IProject getTargetProject() {
		return getProjectHandle(PROJECT_NAME);
	}

	protected Object getDefaultProperty(String propertyName) {
		if (propertyName.equals(PROMPT_ON_SAVE))
			return Boolean.FALSE;
		return super.getDefaultProperty(propertyName);
	}

	public final EditModel getEditModelForRead(Object key) {
		EMFWorkbenchContext emfWorkbenchContext = (EMFWorkbenchContext) WorkbenchResourceHelperBase.createEMFContext(getTargetProject(), null);
		return emfWorkbenchContext.getEditModelForRead(getStringProperty(EDIT_MODEL_ID), key);
	}

}