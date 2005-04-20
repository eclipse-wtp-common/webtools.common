/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.frameworks.internal.datamodel;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.internal.AdaptabilityUtility;
import org.eclipse.wst.common.frameworks.internal.WTPResourceHandler;
import org.eclipse.wst.common.frameworks.internal.enablement.IEnablementManager;
import org.eclipse.wst.common.frameworks.internal.operations.DMComposedExtendedOperationHolder;
import org.eclipse.wst.common.frameworks.internal.operations.DMOperationExtensionRegistry;
import org.eclipse.wst.common.frameworks.internal.operations.OperationStatus;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonPlugin;

public final class ExtendableOperationImpl implements IDataModelOperation {

	private IDataModelOperation rootOperation;
	private List appendedOperations;

	private OperationStatus opStatus;

	public ExtendableOperationImpl(IDataModelOperation rootOperation) {
		this.rootOperation = rootOperation;
	}

	private DMComposedExtendedOperationHolder initializeExtensionOperations() {
		return DMOperationExtensionRegistry.getExtensions(rootOperation);
	}

	public IStatus redo(IProgressMonitor monitor, IAdaptable info) {
		// TODO Auto-generated method stub
		return null;
	}

	public IStatus undo(IProgressMonitor monitor, IAdaptable info) {
		// TODO Auto-generated method stub
		return null;
	}

	public final void addStatus(IStatus aStatus) {
		if (opStatus == null) {
			opStatus = new OperationStatus(aStatus.getMessage(), aStatus.getException());
			opStatus.setSeverity(aStatus.getSeverity());
			opStatus.add(aStatus);
		} else {
			opStatus.add(aStatus);
		}
	}

	private void addExtendedStatus(IStatus aStatus) {
		if (opStatus == null) {
			opStatus = new OperationStatus(new IStatus[]{WTPCommonPlugin.OK_STATUS});
		}
		opStatus.addExtendedStatus(aStatus);
	}

	public void appendOperation(IDataModelOperation appendedOperation) {
		if (appendedOperations == null) {
			appendedOperations = new ArrayList(3);
		}
		appendedOperations.add(appendedOperation);
	}

	public IStatus execute(IProgressMonitor monitor, IAdaptable info) {
		DMComposedExtendedOperationHolder extOpHolder = initializeExtensionOperations();
		IStatus preOpStatus = runPreOps(monitor, extOpHolder, info);
		try {
			addStatus(rootOperation.execute(monitor, info));
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		IStatus postOpStatus = runPostOps(monitor, extOpHolder, info);
		if (null != preOpStatus) {
			addExtendedStatus(preOpStatus);
		}
		if (null != postOpStatus) {
			addExtendedStatus(postOpStatus);
		}
		if (appendedOperations != null) {
			OperationStatus composedStatus = null;
			for (int i = 0; i < appendedOperations.size(); i++) {
				try {
					ExtendableOperationImpl op = new ExtendableOperationImpl((IDataModelOperation) appendedOperations.get(i));
					IStatus status = op.execute(new SubProgressMonitor(monitor, 1, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK), info);
					if (composedStatus == null)
						composedStatus = new OperationStatus(new IStatus[]{status});
					else
						composedStatus.add(status);
				} catch (Exception e) {
					Logger.getLogger().logError(e);
				}
			}
			if (null != composedStatus) {
				addStatus(composedStatus);
			}
		}
		return opStatus;
	}

	private IStatus runPreOps(IProgressMonitor pm, DMComposedExtendedOperationHolder extOpHolder, IAdaptable info) {
		IStatus preOpStatus = null;
		if ((extOpHolder != null) && extOpHolder.hasPreOps()) {
			preOpStatus = runExtendedOps(extOpHolder.getPreOps(), pm, info);
		}
		return preOpStatus;
	}

	private IStatus runPostOps(IProgressMonitor pm, DMComposedExtendedOperationHolder extOpHolder, IAdaptable info) {
		IStatus postOpStatus = null;
		if ((extOpHolder != null) && extOpHolder.hasPostOps()) {
			postOpStatus = runExtendedOps(extOpHolder.getPostOps(), pm, info);
		}

		return postOpStatus;
	}

	private IStatus runExtendedOps(List opList, IProgressMonitor pm, IAdaptable info) {
		IDataModel rootDataModel = rootOperation.getDataModel();
		IDataModelOperation nestedOp = null;
		OperationStatus returnStatus = null;
		IStatus localStatus;
		String opId = null;
		for (int i = 0; i < opList.size(); i++) {
			nestedOp = (IDataModelOperation) opList.get(i);
			try {
				opId = nestedOp.getID();
				boolean shouldExtendedRun = true;

				List extendedContext = rootDataModel.getExtendedContext();
				for (int contextCount = 0; shouldExtendedRun && contextCount < extendedContext.size(); contextCount++) {
					IProject project = (IProject) AdaptabilityUtility.getAdapter(extendedContext.get(contextCount), IProject.class);
					if (null != project && !IEnablementManager.INSTANCE.getIdentifier(opId, project).isEnabled()) {
						shouldExtendedRun = false;
					}
				}
				if (shouldExtendedRun) {
					nestedOp.setDataModel(rootDataModel);
					ExtendableOperationImpl extendedOp = new ExtendableOperationImpl(nestedOp);
					localStatus = extendedOp.execute(new SubProgressMonitor(pm, IProgressMonitor.UNKNOWN), info);
				} else
					localStatus = null;
			} catch (Exception e) {
				localStatus = new Status(IStatus.ERROR, WTPCommonPlugin.PLUGIN_ID, 0, WTPResourceHandler.getString("25", new Object[]{nestedOp.getClass().getName()}), e); //$NON-NLS-1$
			}
			if (localStatus != null) {
				if (returnStatus == null) {
					returnStatus = new OperationStatus(new IStatus[]{localStatus});
				} else {
					returnStatus.add(localStatus);
				}
			}
		}
		return returnStatus;
	}

	public void dispose() {
		// TODO Auto-generated method stub
	}

	public boolean canExecute() {
		return rootOperation.canExecute();
	}

	public boolean canRedo() {
		return rootOperation.canRedo();
	}

	public boolean canUndo() {
		return rootOperation.canUndo();
	}

	public String getLabel() {
		return rootOperation.getLabel();
	}

	public IUndoContext[] getContexts() {
		return rootOperation.getContexts();
	}

	public boolean hasContext(IUndoContext context) {
		return rootOperation.hasContext(context);
	}

	public void addContext(IUndoContext context) {
		rootOperation.addContext(context);
	}

	public void removeContext(IUndoContext context) {
		rootOperation.removeContext(context);
	}

	public void setID(String id) {
		rootOperation.setID(id);
	}

	public String getID() {
		return rootOperation.getID();
	}

	public void setDataModel(IDataModel model) {
		rootOperation.setDataModel(model);
	}

	public IDataModel getDataModel() {
		return rootOperation.getDataModel();
	}
}