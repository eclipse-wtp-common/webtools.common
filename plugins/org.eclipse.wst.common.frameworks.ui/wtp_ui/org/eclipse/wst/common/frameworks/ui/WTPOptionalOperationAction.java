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

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.frameworks.operation.extension.ui.IOperationNode;
import org.eclipse.wst.common.frameworks.operation.extension.ui.WTPActionDialog;
import org.eclipse.wst.common.frameworks.operation.extension.ui.WTPOptionalOperationDataModel;


/**
 * @author mdelder
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Generation - Code and Comments
 */
public class WTPOptionalOperationAction extends WTPOperationAction {

	public WTPOptionalOperationAction() {
	}


	public WTPOptionalOperationAction(String id, String name) {
		super(id, name);
	}

	public WTPOptionalOperationAction(String id, String name, ImageDescriptor imgDescriptor) {
		super(id, name, imgDescriptor);
	}


	public WTPOptionalOperationAction(String id, String name, IWorkbenchSite site) {
		super(id, name, site);
	}

	public WTPOptionalOperationAction(String id, String name, ImageDescriptor imgDescriptor, IWorkbenchSite site) {
		super(id, name, imgDescriptor, site);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.framework.ui.WTPOperationAction#executeCompoundOperation(org.eclipse.wst.common.framework.operation.extension.ui.WTPOptionalOperationDataModel)
	 */
	protected void executeCompoundOperation(WTPOptionalOperationDataModel operationDataModel) {
		if (operationDataModel != null) {
			IOperationNode root = (IOperationNode) operationDataModel.getProperty(WTPOptionalOperationDataModel.OPERATION_TREE);
			if (WTPOptionalOperationDataModel.getOptionalChildren(root).length > 0) {
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				WTPActionDialog optionalDialog = new WTPActionDialog(shell, operationDataModel);
				optionalDialog.setId(getId());
				if (optionalDialog.open() == Window.OK)
					super.executeCompoundOperation(operationDataModel);
				else
					status = Status.CANCEL_STATUS;
			} else
				super.executeCompoundOperation(operationDataModel);
		}
	}
}