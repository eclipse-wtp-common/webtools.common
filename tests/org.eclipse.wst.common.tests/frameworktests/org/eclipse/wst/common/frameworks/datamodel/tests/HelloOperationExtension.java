/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.datamodel.tests;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;

public class HelloOperationExtension extends AbstractDataModelOperation {

	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		System.out.println(getClass().getName() + " execute"); //$NON-NLS-1$
		return OK_STATUS;
	}

	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		System.out.println(getClass().getName() + " redo"); //$NON-NLS-1$
		return OK_STATUS;
	}

	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		System.out.println(getClass().getName() + " undo"); //$NON-NLS-1$
		return OK_STATUS;
	}

}
