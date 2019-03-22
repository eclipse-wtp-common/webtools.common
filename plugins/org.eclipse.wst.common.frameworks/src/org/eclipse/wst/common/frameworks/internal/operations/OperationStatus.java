/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.operations;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonPlugin;

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

	/**
	 * Overridden to expose as public instead of protected
	 */
	@Override
	public void setCode(int code) {
		super.setCode(code);
	}

	/**
	 * Overridden to expose as public instead of protected
	 */
	@Override
	public void setException(Throwable exception) {
		super.setException(exception);
	}

	/**
	 * Overridden to expose as public instead of protected
	 */
	@Override
	public void setMessage(String message) {
		super.setMessage(message);
	}

	/**
	 * Overridden to expose as public instead of protected
	 */
	@Override
	public void setSeverity(int severity) {
		super.setSeverity(severity);
	}

	@Override
	public void add(IStatus status) {
		int newSev = status.getSeverity();
		if (newSev > getSeverity()) {
			setMessage(status.getMessage());
			setException(status.getException());
		}
		super.add(status);
	}
}
