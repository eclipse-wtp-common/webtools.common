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
 * Created on Oct 29, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.eclipse.wst.common.framework.operation;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclispe.wst.common.framework.plugin.WTPCommonPlugin;

/**
 * @author jsholl
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class OperationStatus extends MultiStatus {


	public OperationStatus(String message, Throwable e) {
		super(WTPCommonPlugin.PLUGIN_ID, 0, new IStatus[]{}, message, e);
	}

	public OperationStatus(IStatus[] children) {
		this(WTPCommonPlugin.PLUGIN_ID, 0, children, "", null); //$NON-NLS-1$
	}

	/**
	 * @param pluginId
	 * @param code
	 * @param children
	 * @param message
	 * @param exception
	 */
	public OperationStatus(String pluginId, int code, IStatus[] children, String message, Throwable exception) {
		super(pluginId, code, children, message, exception);
	}

	public void addExtendedStatus(IStatus status) {
		int oldSeverity = getSeverity();
		super.add(status);
		if (oldSeverity != IStatus.ERROR && getSeverity() == IStatus.ERROR) {
			setSeverity(IStatus.WARNING);
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Status#setCode(int)
	 */
	public void setCode(int code) {
		super.setCode(code);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Status#setException(java.lang.Throwable)
	 */
	public void setException(Throwable exception) {
		super.setException(exception);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Status#setMessage(java.lang.String)
	 */
	public void setMessage(String message) {
		super.setMessage(message);
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Status#setSeverity(int)
	 */
	public void setSeverity(int severity) {
		super.setSeverity(severity);
	}

	public void add(IStatus status) {
		int newSev = status.getSeverity();
		if (newSev > getSeverity()) {
			setMessage(status.getMessage());
			setException(status.getException());
		}
		super.add(status);
	}
}