package org.eclipse.wst.validation.internal.ui;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

public class ValidationJob extends Job {


	private Map projects = null;
	public ValidationJob(String name) {
		super(name);
	}


	public void setProjectsMap(Map projects){
		this.projects = projects;
	}


	protected IStatus run(IProgressMonitor monitor) {

		return null;
	}
}
