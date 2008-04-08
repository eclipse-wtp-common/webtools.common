package org.eclipse.wst.validation.internal;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

public class FullBuildJob extends WorkspaceJob {
	
	public FullBuildJob(){
		super(ValMessages.JobName);
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		try {
			ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, monitor);
		}
		catch (Exception e){
			return new Status(IStatus.ERROR,ValidationPlugin.PLUGIN_ID, e.toString(), e);
		}
		return Status.OK_STATUS;
	}

}
