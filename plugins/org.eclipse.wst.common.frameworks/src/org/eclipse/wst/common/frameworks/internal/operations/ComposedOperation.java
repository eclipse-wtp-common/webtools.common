/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.frameworks.internal.operations;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubProgressMonitor;

public class ComposedOperation extends WTPOperation {

	protected List fRunnables;

	public ComposedOperation() {
		super();
	}

	public ComposedOperation(List nestedRunnablesWithProgress) {
		super();
		fRunnables = nestedRunnablesWithProgress;
	}

	public boolean addRunnable(WTPOperation nestedOp) {
		return getRunnables().add(nestedOp);
	}

	public WTPOperation append(WTPOperation op) {
		this.addRunnable(op);
		return this;
	}

	// TODO MDE Make protected
	public List getRunnables() {
		if (fRunnables == null)
			fRunnables = new ArrayList(3);
		return fRunnables;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.j2ee.operations.IHeadlessRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void execute(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		if (fRunnables == null || fRunnables.isEmpty())
			return;
		int size = fRunnables.size();
		monitor.beginTask("", size);//$NON-NLS-1$

		OperationStatus composedStatus = null;
		try {
			for (int i = 0; i < fRunnables.size(); i++) {
				WTPOperation op = (WTPOperation) fRunnables.get(i);
				op.run(new SubProgressMonitor(monitor, 1, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));
				if (composedStatus == null)
					composedStatus = new OperationStatus(new IStatus[]{op.getStatus()});
				else
					composedStatus.add(op.getStatus());
			}
		} finally {
			if (composedStatus != null)
				addStatus(composedStatus);
		}
	}
}