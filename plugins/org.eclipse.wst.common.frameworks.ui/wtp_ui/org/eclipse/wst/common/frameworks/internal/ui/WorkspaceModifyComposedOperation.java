/*******************************************************************************
 * Copyright (c) 2001, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.frameworks.internal.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * An operation which delegates its work to a runnable that modifies the workspace.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 */
public class WorkspaceModifyComposedOperation extends org.eclipse.ui.actions.WorkspaceModifyOperation {
	private List fRunnables;

	public WorkspaceModifyComposedOperation(ISchedulingRule rule) {
		super(rule);
	}

	/**
	 * Creates a new operation which will delegate its work to the given runnable.
	 */
	public WorkspaceModifyComposedOperation() {
		super();
	}

	public WorkspaceModifyComposedOperation(ISchedulingRule rule, List nestedRunnablesWithProgress) {
		super(rule);
		getRunnables().addAll(nestedRunnablesWithProgress);
	}

	public WorkspaceModifyComposedOperation(List nestedRunnablesWithProgress) {
		super();
		getRunnables().addAll(nestedRunnablesWithProgress);
	}

	/**
	 * Creates a new operation which will delegate its work to the given runnable.
	 * 
	 * @param content
	 *            the runnable to delegate to when this operation is executed
	 */
	public WorkspaceModifyComposedOperation(IRunnableWithProgress nestedOp) {
		super();
		getRunnables().add(nestedOp);
	}

	public boolean addRunnable(IRunnableWithProgress nestedOp) {
		return getRunnables().add(nestedOp);
	}

	@Override
	protected void execute(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		try{
			List runnables = getRunnables();
			if(runnables.size() == 0){
				return;
			}
			monitor.beginTask("", runnables.size());//$NON-NLS-1$
			for (int i = 0; i < runnables.size(); i++) {
				IRunnableWithProgress op = (IRunnableWithProgress) runnables.get(i);
				op.run(new SubProgressMonitor(monitor, 1, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));
			}
		} finally {
			monitor.done();
		}
	}

	protected List getRunnables() {
		if (fRunnables == null)
			fRunnables = new ArrayList(3);
		return fRunnables;
	}
}