/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Aug 27, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.wst.common.frameworks.internal.operation.extensionui;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;



/**
 * @author mdelder
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Style - Code Templates
 */
public class PostPasteActionOperation extends WTPOperation {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.operation.WTPOperation#execute(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
		PasteActionOperationDataModel dataMdl = (PasteActionOperationDataModel) getOperationDataModel();

		Map refactoredResourcesMap = (Map) dataMdl.getProperty(PasteActionOperationDataModel.REFACTORED_RESOURCES);
		System.out.println(getClass().getName());
		System.out.println("Map:" + refactoredResourcesMap); //$NON-NLS-1$

		Object destination = dataMdl.getProperty(PasteActionOperationDataModel.DESTINATION);
		System.out.println("Destination:" + destination); //$NON-NLS-1$



	}

}