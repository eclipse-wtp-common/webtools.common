/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.ui.internal;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.validation.internal.ValOperation;
import org.eclipse.wst.validation.internal.ValType;
import org.eclipse.wst.validation.internal.ValidationResultSummary;
import org.eclipse.wst.validation.internal.ValidationRunner;
import org.eclipse.wst.validation.ui.internal.dialog.ResultsDialog;

/**
 * Run a manual validation. 
 * @author karasiuk
 *
 */
public class ManualValidationRunner extends WorkspaceJob {
	
	private Map<IProject, Set<IResource>> 	_projects;
	private ValType _valType;
	private boolean	_showResults;
	
	/**
	 * Validate the selected projects and/or resources.
	 * 
	 * @param projects
	 *            The selected projects. The key is an IProject and the value is
	 *            the Set of IResources that were selected. Often this will be
	 *            every resource in the project.
	 * 
	 * @param isManual
	 *            Is this a manual validation?
	 * 
	 * @param isBuild
	 *            Is this a build based validation?
	 * 
	 * @param showResults
	 *            When the validation is finished, show the results in a dialog box.
	 */
	public static void validate(Map<IProject, Set<IResource>> projects, ValType valType, boolean showResults){
		ManualValidationRunner me = new ManualValidationRunner(projects, valType, showResults);
		
		//TODO optimize this, I don't like the idea of validators having to lock the entire project
		Set<IProject> keys = projects.keySet();
		IProject[] projectArray = new IProject[keys.size()];
		keys.toArray(projectArray);
		if (projectArray.length == 1)me.setRule(projectArray[0]);
		else {
			me.setRule(MultiRule.combine(projectArray));
		}
		me.schedule();
	}
	
	private ManualValidationRunner(Map<IProject, Set<IResource>> projects, ValType valType, boolean showResults){
		super(ValUIMessages.Validation);
		_projects = projects;
		_valType = valType;
		_showResults = showResults;
	}

	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		
		long start = System.currentTimeMillis();
		final ValOperation vo = ValidationRunner.validate(_projects, _valType, monitor, false);
		final long time = System.currentTimeMillis() - start;
		int resourceCount = 0;
		for (Set s : _projects.values())resourceCount += s.size();
		final int finalResourceCount = resourceCount;
		if (vo.isCanceled())return Status.CANCEL_STATUS;
		
		if (_showResults){
			Display display = Display.getDefault();
			Runnable run = new Runnable(){

				public void run() {
					ValidationResultSummary vr = vo.getResult();
					ResultsDialog rd = new ResultsDialog(null, vr, time, finalResourceCount);
					rd.open();
				}
				
			};
			display.asyncExec(run);			
		}
		return Status.OK_STATUS;
	}
}
