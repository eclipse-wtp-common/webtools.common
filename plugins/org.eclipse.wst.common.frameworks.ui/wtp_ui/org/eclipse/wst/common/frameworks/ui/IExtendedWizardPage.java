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
package org.eclipse.wst.common.frameworks.ui;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.wst.common.frameworks.operations.WTPOperation;

/**
 * This interface is EXPERIMENTAL and is subject to substantial changes.
 */
public interface IExtendedWizardPage extends IWizardPage {

	WTPOperation createOperation();

	boolean canPageFinish();

	String getGroupID();

	void setGroupID(String id);

}