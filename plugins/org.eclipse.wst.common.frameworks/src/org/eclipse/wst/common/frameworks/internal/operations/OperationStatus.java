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
package org.eclipse.wst.common.frameworks.internal.operations;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclispe.wst.common.frameworks.internal.plugin.WTPCommonPlugin;

public class OperationStatus extends MultiStatus {

	public OperationStatus(String message, Throwable e) {
		super(WTPCommonPlugin.PLUGIN_ID, 0, new IStatus[]{}, message, e);
	}

	public OperationStatus(IStatus[] children) {
		this(WTPCommonPlugin.PLUGIN_ID, 0, children, "", null); //$NON-NLS-1$
	}

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

	public void setCode(int code) {
		super.setCode(code);
	}

	public void setException(Throwable exception) {
		super.setException(exception);
	}

	public void setMessage(String message) {
		super.setMessage(message);
	}

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