/***************************************************************************************************
 * Copyright (c) 2003, 2019 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.internal.emfworkbench.edit;

import java.util.Map;

import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModel;



public class EditModelRetriever {
	private EMFWorkbenchContext context;
	private String editModelID;
	private Map editModelParms;

	/**
	 * EditModelRetriever constructor comment.
	 */
	public EditModelRetriever(EMFWorkbenchContext context, String editModelKey, Map parms) {
		super();
		this.context = context;
		editModelID = editModelKey;
		editModelParms = parms;
	}

	public EditModel getEditModelForRead(Object accessorKey) {
		return context.getEditModelForRead(getEditModelID(), accessorKey, editModelParms);
	}

	public EditModel getEditModelForWrite(Object accessorKey) {
		return context.getEditModelForWrite(getEditModelID(), accessorKey, editModelParms);
	}

	public String getEditModelID() {
		return editModelID;
	}

	public EMFWorkbenchContext context() {
		return context;
	}
}