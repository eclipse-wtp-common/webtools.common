/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.frameworks.internal.ui;

import org.eclipse.core.internal.runtime.Assert;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel;

/**
 * This class is EXPERIMENTAL and is subject to substantial changes.
 */
public abstract class ExtendedWizardPage extends WTPWizardPage implements IExtendedWizardPage {

	private String id;

	protected ExtendedWizardPage(WTPOperationDataModel model, String pageName) {
		super(model, pageName);
	}

	/**
	 * @return Returns the id.
	 */
	public final String getGroupID() {
		return id;
	}

	/**
	 * Will only set the id once. Further invocations will be ignored.
	 * 
	 * @param id
	 *            The id to set.
	 */
	public final void setGroupID(String id) {
		Assert.isTrue(this.id == null, WTPCommonUIResourceHandler.getString("ExtendedWizardPage_ERROR_0")); //$NON-NLS-1$
		Assert.isNotNull(id, WTPCommonUIResourceHandler.getString("ExtendedWizardPage_ERROR_1")); //$NON-NLS-1$
		this.id = id;
	}
}