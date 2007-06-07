/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.ui;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

public class ValidationJob extends Job {


	private Map projects;
	public ValidationJob(String name) {
		super(name);
	}


	public void setProjectsMap(Map projects){
		this.projects = projects;
	}


	protected IStatus run(IProgressMonitor monitor) {

		return null;
	}
	
	protected Map getProjects() {
		return projects;
	}
}
