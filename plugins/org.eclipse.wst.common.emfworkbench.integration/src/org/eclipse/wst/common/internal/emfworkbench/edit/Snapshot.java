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
 * Created on Oct 3, 2003
 *
 */
package org.eclipse.wst.common.internal.emfworkbench.edit;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchEditResourceHandler;


/**
 * The Snapshot is used to remember the call stack trace of any method that accesses a Resource.
 * 
 * @author mdelder
 */
public class Snapshot extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6041488000642757347L;

	public Snapshot() {
		super(EMFWorkbenchEditResourceHandler.Snapshot_ERROR_0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#printStackTrace()
	 */
	public String getStackTraceString() {
		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		super.printStackTrace(printWriter);
		return writer.toString();
	}
}
