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
 * Created on Jan 21, 2004
 * 
 * To change the template for this generated file go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
package org.eclipse.wst.common.frameworks.internal.operations;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.wst.common.frameworks.operations.WTPOperation;

import org.eclipse.jem.util.logger.proxy.Logger;

/**
 * @author jsholl
 * 
 * Same as ComposedOperation except the first Operation must run; all others can fail without
 * effecting the ComposedOperation or any other operation following them in the Composed Operation's
 * list.
 */
public class FailSafeComposedOperation extends ComposedOperation {

	public FailSafeComposedOperation() {
		super();
	}

	public FailSafeComposedOperation(List nestedRunnablesWithProgress) {
		super(nestedRunnablesWithProgress);
	}

	public void execute(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		int size = fRunnables.size();
		monitor.beginTask("", size); //$NON-NLS-1$
		for (int i = 0; i < fRunnables.size(); i++) {
			WTPOperation op = (WTPOperation) fRunnables.get(i);
			if (i == 0) { //The first one must pass; all others can fail
				op.run(new SubProgressMonitor(monitor, 1, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));
			} else {
				try {
					op.run(new SubProgressMonitor(monitor, 1, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));
				} catch (InterruptedException e) {
					throw e;
				} catch (Exception e) {
					Logger.getLogger().logError(e);
				}
			}
		}
	}

}