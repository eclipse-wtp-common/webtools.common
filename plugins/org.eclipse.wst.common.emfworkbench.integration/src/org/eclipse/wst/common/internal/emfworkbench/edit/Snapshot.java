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

	public Snapshot() {
		super(EMFWorkbenchEditResourceHandler.getString("Snapshot_ERROR_0")); //$NON-NLS-1$
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