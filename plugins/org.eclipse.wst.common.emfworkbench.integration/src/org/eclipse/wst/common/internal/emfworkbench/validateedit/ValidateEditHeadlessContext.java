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
 * Created on May 18, 2004
 */
package org.eclipse.wst.common.internal.emfworkbench.validateedit;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModel;


/**
 * @author jlanuti Headless Validate Edit Context Implementation
 */
public class ValidateEditHeadlessContext implements IValidateEditContext {

	protected boolean fNeedsStateValidation = true;
	protected boolean fMessageUp = false;
	protected EditModel fValidator = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.internal.emfworkbench.validateedit.IValidateEditContext#validateState()
	 */
	public IStatus validateState() {
		// For now do nothing in headless state
		return Status.OK_STATUS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.internal.emfworkbench.validateedit.ResourceStateValidatorPresenter#getValidateEditContext()
	 */
	public Object getValidateEditContext() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.internal.emfworkbench.validateedit.ResourceStateValidatorPresenter#promptForInconsistentFileOverwrite(java.util.List)
	 */
	public boolean promptForInconsistentFileOverwrite(List inconsistentFiles) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.internal.emfworkbench.validateedit.ResourceStateValidatorPresenter#promptForInconsistentFileRefresh(java.util.List)
	 */
	public boolean promptForInconsistentFileRefresh(List inconsistentFiles) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see IValidateEditListener#setNeedsStateValidation(boolean)
	 */
	public void setNeedsStateValidation(boolean needsStateValidation) {
		fNeedsStateValidation = needsStateValidation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.internal.emfworkbench.validateedit.IValidateEditContext#setEditModel(org.eclipse.wst.common.internal.emfworkbench.integration.EditModel)
	 */
	public void setEditModel(EditModel fValidator) {
		this.fValidator = fValidator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.internal.emfworkbench.validateedit.IValidateEditContext#validateState(org.eclipse.wst.common.internal.emfworkbench.integration.EditModel)
	 */
	public IStatus validateState(EditModel editModel) {
		setEditModel(editModel);
		return validateState();
	}
}