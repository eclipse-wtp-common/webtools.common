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
 * Created on May 10, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.eclipse.wst.common.frameworks.internal.operation.extensionui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.wst.common.frameworks.internal.operations.NonConflictingRule;
import org.eclipse.wst.common.frameworks.internal.ui.WTPCommonUIResourceHandler;
import org.eclipse.wst.common.frameworks.internal.ui.WTPUIPlugin;
import org.eclipse.wst.common.frameworks.operations.WTPOperation;
import org.eclipse.wst.common.frameworks.operations.WTPOperationDataModel;


/**
 * @author mdelder
 *  
 */
public class IActionWTPOperation extends WTPOperation {

	private static final ISchedulingRule nonconflictingRule = new NonConflictingRule();

	boolean done;

	/**
	 *  
	 */
	public IActionWTPOperation() {
		super();
	}

	/**
	 * @param operationDataModel
	 */
	public IActionWTPOperation(WTPOperationDataModel operationDataModel) {
		super(operationDataModel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.operation.WTPOperation#execute(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {

		final IAction action = (IAction) getOperationDataModel().getProperty(IActionWTPOperationDataModel.IACTION);
		if (action == null)
			throw new CoreException(new Status(IStatus.WARNING, WTPUIPlugin.PLUGIN_ID, 0, WTPCommonUIResourceHandler.getString("IActionWTPOperation_UI_0"), null)); //$NON-NLS-1$

		final ISelection selection = (IStructuredSelection) getOperationDataModel().getProperty(IActionWTPOperationDataModel.ISTRUCTURED_SELECTION);
		final ISelectionProvider selectionProvider = (ISelectionProvider) getOperationDataModel().getProperty(IActionWTPOperationDataModel.ISELECTION_PROVIDER);

		/*
		 * if(selectionProvider != null) selection = (selection != null) ? selection :
		 * selectionProvider.getSelection();
		 */
		Shell shell = (Shell) getOperationDataModel().getProperty(IActionWTPOperationDataModel.SHELL);
		done = false;

		Runnable executeAction = new Runnable() {

			public void run() {
				try {
					if (action instanceof IActionDelegate) {
						((IActionDelegate) action).selectionChanged(action, selection);
						((IActionDelegate) action).run(action);
					} else if (action instanceof IViewActionDelegate) {
						((IViewActionDelegate) action).selectionChanged(action, selection);
						((IActionDelegate) action).run(action);
					} /*else if (action instanceof SelectionDispatchAction) {
						((SelectionDispatchAction) action).update(selection);
						((SelectionDispatchAction) action).run();
					} */
					  else if (action instanceof ISelectionChangedListener) {
					   	((ISelectionChangedListener)action).selectionChanged(new SelectionChangedEvent(selectionProvider, selection)); 
					   	action.run(); 
					}


				} catch (RuntimeException e) {
					e.printStackTrace();
				} finally {
					getOperationDataModel().dispose();
					done = true;
				}
			}
		};

		Display current = Display.getCurrent();
		if (current != null) {
			executeAction.run();
		} else {
			shell.getDisplay().asyncExec(executeAction);
		}

		while (!done) {
			Thread.sleep(100);
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.operation.WTPOperation#getSchedulingRule()
	 */
	protected ISchedulingRule getSchedulingRule() {
		return nonconflictingRule;
	}

}