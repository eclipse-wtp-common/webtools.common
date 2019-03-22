/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.operations;


import org.eclipse.core.resources.IProject;
import org.eclipse.wst.validation.internal.FilterUtil;


/**
 * 
 * This operation is not intended to be subclassed outside of the validation framework.
 */
public class ManualIncrementalValidatorsOperation extends ManualValidatorsOperation {

	public ManualIncrementalValidatorsOperation(IProject project) {
		super( project );
	}	
	public ManualIncrementalValidatorsOperation(IProject project, Object[] changedResources) {
		super( project, changedResources );
		setFileDeltas( FilterUtil.getFileDeltas(getEnabledValidators(), changedResources, false) );
	}	
}
