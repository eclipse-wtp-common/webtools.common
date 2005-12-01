/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.datamodel;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

public class IWorkspaceRunnableWithStatus implements IWorkspaceRunnable {
	private IAdaptable info;
	private IStatus status;

public IWorkspaceRunnableWithStatus(IAdaptable info) {
		super();
		this.info = info;
	}
public IAdaptable getInfo() {
	// TODO Auto-generated method stub
	return info;
}
public void setStatus(IStatus aStatus) {
	status = aStatus;
}

public void run(IProgressMonitor monitor) throws CoreException {
		
	}
public IStatus getStatus() {
	return status;
}

}
