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
 * Created on Jan 19, 2004
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package org.eclipse.wst.common.frameworks.operation.extension.ui;


import org.eclipse.core.internal.runtime.Assert;
import org.eclipse.wst.common.framework.operation.WTPOperationDataModel;
import org.eclipse.wst.common.frameworks.ui.WTPCommonUIResourceHandler;
import org.eclipse.wst.internal.common.frameworks.ui.WTPWizardPage;


/**
 * @author jsholl
 * 
 * To change the template for this generated type comment go to Window - Preferences - Java - Code
 * Generation - Code and Comments
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