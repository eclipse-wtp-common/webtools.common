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
 * Created on Apr 29, 2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
package org.eclipse.wst.common.frameworks.ui;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.framework.operation.ComposedOperation;
import org.eclipse.wst.common.framework.operation.WTPOperation;
import org.eclipse.wst.common.frameworks.operation.extension.ui.MasterDescriptor;
import org.eclipse.wst.common.frameworks.operation.extension.ui.UIOperationExtensionRegistry;
import org.eclipse.wst.common.frameworks.operation.extension.ui.WTPOptionalOperationDataModel;

import com.ibm.wtp.common.logger.proxy.Logger;

/**
 * @author mdelder
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Generation - Code and Comments
 */
public class WTPOperationAction extends Action implements IActionDelegate, IViewActionDelegate {

	private IStructuredSelection selection;

	private IViewPart viewPart;

	private IWorkbenchSite workbenchSite;

	public WTPOperationAction() {
	}

	public WTPOperationAction(String id, String name) {
		super(name);
		init(id, null);
	}

	public WTPOperationAction(String id, String name, ImageDescriptor imgDescriptor) {
		super(name, imgDescriptor);
		init(id, null);
	}

	public WTPOperationAction(String id, String name, IWorkbenchSite site) {
		super(name);
		init(id, site);
	}

	public WTPOperationAction(String id, String name, ImageDescriptor imgDescriptor, IWorkbenchSite site) {
		super(name, imgDescriptor);
		init(id, site);
	}

	/**
	 * @param id
	 * @param site
	 */
	private void init(String id, IWorkbenchSite site) {
		this.setId(id);
		this.setWorkbenchSite(site);
	}

	/**
	 * @param selection
	 * @return
	 */
	protected boolean updateSelection(IAction action, IStructuredSelection selection1) {
		this.selection = selection1;
		MasterDescriptor[] masters = UIOperationExtensionRegistry.INSTANCE.getExtendedUIOperations(action.getId(), selection);
		action.setEnabled(masters.length > 0);
		return masters.length > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		run(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public final void run(IAction action) {

		if (action == null)
			return;

		WTPOptionalOperationDataModel operationDataModel = WTPOptionalOperationDataModel.createDataModel(action.getId(), this.selection);
		operationDataModel.setProperty(WTPOptionalOperationDataModel.IWORKBENCH_SITE, getWorkbenchSite());

		executeCompoundOperation(operationDataModel);
	}

	/**
	 * @return
	 */
	public IWorkbenchSite getWorkbenchSite() {
		if (workbenchSite != null)
			return workbenchSite;
		if (viewPart != null)
			return viewPart.getSite();
		return null;
	}

	/**
	 * @param shell
	 * @param operationDataModel
	 * @param exec
	 */
	protected void executeCompoundOperation(WTPOptionalOperationDataModel operationDataModel) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		try {
			WTPOperation operation = operationDataModel.getDefaultOperation();
			operationDataModel.setOperationValidationEnabled(true);
			final List runnables = ((ComposedOperation) operation).getRunnables();
			/*
			 * PlatformUI.getWorkbench().getProgressService().run(true, false, new
			 * IRunnableWithProgress() {
			 * 
			 * 
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
			 * 
			 * public void run(IProgressMonitor monitor) throws InvocationTargetException,
			 * InterruptedException {
			 */
			/* monitor.beginTask("Executing compound operation", runnables.size()); */
			SubProgressMonitor submonitor = null;
			for (int i = 0; i < runnables.size(); i++) {
				final WTPOperation op = (WTPOperation) runnables.get(i);
				if (op != null) {
					/* submonitor = new SubProgressMonitor(monitor, 3); */
					op.run(submonitor);
				}
				/* monitor.worked(1); */
			}
			/* monitor.done(); */
			/*
			 * } });
			 */
			/* operation.run(new NullProgressMonitor()); */
			status = operation.getStatus();
			if (!status.isOK()) {
				ErrorDialog.openError(shell, WTPCommonUIResourceHandler.getString("WTPOperationAction_UI_0"), WTPCommonUIResourceHandler.getString("WTPOperationAction_UI_1"), status); //$NON-NLS-1$ //$NON-NLS-2$
			}

		} catch (Exception e) {
			Logger.getLogger().logError(e);
			status = new Status(IStatus.ERROR, WTPUIPlugin.PLUGIN_ID, 0, e.toString(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection1) {
		if (selection1 instanceof IStructuredSelection)
			setEnabled(updateSelection(action, (IStructuredSelection) selection1));
		else {
			action.setEnabled(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) {
		this.viewPart = view;
	}

	/**
	 * @param workbenchSite
	 *            The workbenchSite to set.
	 */
	public void setWorkbenchSite(IWorkbenchSite workbenchSite) {
		this.workbenchSite = workbenchSite;
	}

	protected IStatus status;

	/**
	 * @return Returns the status.
	 */
	public IStatus getStatus() {
		return status;
	}
}