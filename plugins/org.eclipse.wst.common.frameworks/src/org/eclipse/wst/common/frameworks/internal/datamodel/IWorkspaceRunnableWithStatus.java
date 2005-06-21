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
