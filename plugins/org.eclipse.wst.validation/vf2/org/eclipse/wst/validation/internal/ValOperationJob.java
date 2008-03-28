/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.model.IValidatorVisitor;

/**
 * This is used to signal when the entire validation operation is complete. This needs to be done in a job
 * because the operation isn't done, until all the validation jobs have finished.  
 * @author karasiuk
 *
 */
public class ValOperationJob extends Job {
	
	private ValOperation _operation;
	
	public ValOperationJob(ValOperation operation){
		super(ValMessages.JobNameMonitor);
		_operation = operation;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		boolean ok = true;
		try {
			ValidationFramework.getDefault().join(monitor);
		}
		catch (InterruptedException e){
			ok = false;
		}
		finished(monitor);
		return ok ? Status.OK_STATUS : Status.CANCEL_STATUS;
	}
	
	private void finished(IProgressMonitor monitor){
		IValidatorVisitor visitor = new IValidatorVisitor(){

			public void visit(Validator validator, IProject project, ValType valType, 
				ValOperation operation, IProgressMonitor monitor) {
				
				validator.validationFinishing(project, operation.getState(), monitor);					
			}
			
		};
		ValManager.getDefault().accept(visitor, null, ValType.Build, _operation, monitor);
	}

}
