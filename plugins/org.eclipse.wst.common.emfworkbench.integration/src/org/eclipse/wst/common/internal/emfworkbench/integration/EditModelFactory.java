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
package org.eclipse.wst.common.internal.emfworkbench.integration;


import java.util.Map;

import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext;


/**
 * @author mdelder
 */
public class EditModelFactory implements IEditModelFactory {
	protected boolean loadKnownResourcesAsReadOnly = true;

	public EditModel createEditModelForRead(String editModelID, EMFWorkbenchContext context) {
		return createEditModelForRead(editModelID, context, null);
	}

	public EditModel createEditModelForWrite(String editModelID, EMFWorkbenchContext context) {
		return createEditModelForWrite(editModelID, context, null);
	}

	public EditModel createEditModelForRead(String editModelID, EMFWorkbenchContext context, Map params) {
		EditModel editModel = new EditModel(editModelID, context, true);
		editModel.setAccessAsReadForUnKnownURIs(loadKnownResourcesAsReadOnly);
		return editModel;
	}

	public EditModel createEditModelForWrite(String editModelID, EMFWorkbenchContext context, Map params) {
		EditModel editModel = new EditModel(editModelID, context, false);
		editModel.setAccessAsReadForUnKnownURIs(loadKnownResourcesAsReadOnly);
		return editModel;
	}

	public String getCacheID(String editModelID, Map params) {
		return editModelID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.internal.emfworkbench.integration.IEditModelFactory#setLoadKnownResourcesAsReadOnly(boolean)
	 */
	public void setLoadKnownResourcesAsReadOnly(boolean value) {
		this.loadKnownResourcesAsReadOnly = value;
	}

	/**
	 * @return Returns the loadKnownResourcesAsReadOnly.
	 */
	protected boolean isLoadKnownResourcesAsReadOnly() {
		return loadKnownResourcesAsReadOnly;
	}

}