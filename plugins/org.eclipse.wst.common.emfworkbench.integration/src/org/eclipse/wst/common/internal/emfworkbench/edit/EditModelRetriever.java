/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.internal.emfworkbench.edit;

import java.util.Map;

import org.eclipse.wst.common.emfworkbench.EMFWorkbenchContext;
import org.eclipse.wst.common.emfworkbench.integration.EditModel;



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