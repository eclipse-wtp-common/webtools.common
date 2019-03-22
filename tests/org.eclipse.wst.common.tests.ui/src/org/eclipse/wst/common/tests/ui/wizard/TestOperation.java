/***************************************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.tests.ui.wizard;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;

public class TestOperation extends AbstractDataModelOperation {

	public TestOperation() {
		setID("TestOperation"); //$NON-NLS-1$
	}

	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		System.out.println("In execute: data model=" + getDataModel()); //$NON-NLS-1$

		try {
			monitor.beginTask("Test operation: ", 5); //$NON-NLS-1$
			for (int index = 1; index < 6; index++) {
				monitor.subTask("part " + index + " of 5 complete."); //$NON-NLS-1$ //$NON-NLS-2$
				Thread.sleep(1000);
			}
		} catch (Exception exc) {
			throw new ExecutionException("execute threw and exception ", exc); //$NON-NLS-1$
		}

		return Status.OK_STATUS;
	}

	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return Status.OK_STATUS;
	}

	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		System.out.println("Undo test operation"); //$NON-NLS-1$

		return Status.OK_STATUS;
	}
}
