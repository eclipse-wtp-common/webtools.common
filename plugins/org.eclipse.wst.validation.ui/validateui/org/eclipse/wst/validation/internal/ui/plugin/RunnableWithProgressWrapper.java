/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.ui.plugin;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.wst.common.frameworks.internal.operations.IHeadlessRunnableWithProgress;
import org.eclipse.wst.validation.internal.operations.ValidationOperation;


/**
 * This is a wrapper for an IWorkspaceRunnable to the IRunnableWithProgress. This class needs to be
 * used when running the operation from a IRunnableContext.
 * 
 * @see IRunnableContext
 * @see ValidationUIPlugin#getRunnableWithProgress Creation date: (5/8/2001 1:28:45 PM)
 * @author: Administrator
 */
public class RunnableWithProgressWrapper implements IRunnableWithProgress {
	private IHeadlessRunnableWithProgress headlessRunnable = null;
	private IWorkspaceRunnable workspaceRunnable = null;
	private ValidationOperation validationOperation = null;

	/**
	 * @deprecated Will be removed in Milestone 3. Use
	 *             RunnableWithProgressWrapper(IWorkspaceRunnable)
	 */
	public RunnableWithProgressWrapper(IHeadlessRunnableWithProgress aHeadlessRunnableWithProgress) {
		setHeadlessRunnable(aHeadlessRunnableWithProgress);
	}

	public RunnableWithProgressWrapper(IWorkspaceRunnable aHeadlessRunnableWithProgress) {
		setWorkspaceRunnable(aHeadlessRunnableWithProgress);
	}

	public RunnableWithProgressWrapper(ValidationOperation op) {
		setValidationOperation(op);
	}

	/**
	 * @deprecated Will be removed in Milestone 3. Use getWorkspaceRunnable()
	 */
	protected IHeadlessRunnableWithProgress getHeadlessRunnable() {
		return headlessRunnable;
	}

	protected IWorkspaceRunnable getWorkspaceRunnable() {
		return workspaceRunnable;
	}

	/**
	 * @see IRunnableWithProgress
	 */
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		try {
			if (getHeadlessRunnable() != null) {
				getHeadlessRunnable().run(monitor);
			} else {
				IWorkspaceRunnable runnable = (getWorkspaceRunnable() == null) ? validationOperation : getWorkspaceRunnable();
				if (runnable == null) {
					return;
				}
				ResourcesPlugin.getWorkspace().run(runnable, monitor);
			}
		} catch (CoreException exc) {
			throw new InvocationTargetException(exc);
		}
	}

	/**
	 * @deprecated Will be removed in Milestone 3. Use setWorkspaceRunnable(IWorkspaceRunnable)
	 */
	protected void setHeadlessRunnable(IHeadlessRunnableWithProgress newHeadlessRunnable) {
		headlessRunnable = newHeadlessRunnable;
	}

	protected void setWorkspaceRunnable(IWorkspaceRunnable newWorkspaceRunnable) {
		workspaceRunnable = newWorkspaceRunnable;
	}

	protected void setValidationOperation(ValidationOperation op) {
		validationOperation = op;
	}
}