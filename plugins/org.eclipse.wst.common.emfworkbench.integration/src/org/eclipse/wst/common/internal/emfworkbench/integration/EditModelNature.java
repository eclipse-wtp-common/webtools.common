/*******************************************************************************
 * Copyright (c) 2003, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Mar 4, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.eclipse.wst.common.internal.emfworkbench.integration;

import java.util.Map;

import org.eclipse.jem.util.emf.workbench.nature.EMFNature;
import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext;

/**
 * @author schacher
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class EditModelNature extends EMFNature {
	/**
	 *  
	 */
	public EditModelNature() {
		super();
	}

	public EditModel getEditModelForRead(String editModelKey, Object accessorKey) {
		return getEditModelForRead(editModelKey, accessorKey, null);
	}

	public EditModel getEditModelForWrite(String editModelKey, Object accessorKey) {
		return getEditModelForWrite(editModelKey, accessorKey, null);
	}

	public EditModel getEditModelForRead(String editModelKey, Object accessorKey, Map params) {
		EditModel result = null;
		if (getEmfContext() != null)
			result = getEmfContext().getEditModelForRead(editModelKey, accessorKey, params);
		return result;
	}

	public EditModel getEditModelForWrite(String editModelKey, Object accessorKey, Map params) {
		EditModel result = null;
		if (getEmfContext() != null)
			result = getEmfContext().getEditModelForWrite(editModelKey, accessorKey, params);
		return result;
	}

	protected EMFWorkbenchContext getEmfContext() {
		return (EMFWorkbenchContext) getEmfContextBase();
	}

}
