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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.common.emfworkbench.integration.EditModel;


/**
 * @author jlanuti This is the abstraction layer for validate edit
 */
public interface IValidateEditContext extends ResourceStateValidatorPresenter {

	public static final String CLASS_KEY = "ValidateEditContext"; //$NON-NLS-1$

	public void setEditModel(EditModel fValidator);

	public IStatus validateState(EditModel fValidator);
}