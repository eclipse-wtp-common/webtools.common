/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.frameworks.internal.operations;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.internal.runtime.Assert;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.wst.common.frameworks.internal.AdaptabilityUtility;
import org.eclipse.wst.common.frameworks.internal.WTPResourceHandler;
import org.eclipse.wst.common.frameworks.internal.enablement.IEnablementManager;
import org.eclipse.wst.common.frameworks.internal.enablement.nonui.WFTWrappedException;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonPlugin;

// TODO perhaps this class should be subclassed from a new super class which more closely resembles
// org.eclipse.ui.actions.WorkspaceModifyOperation
/**
 * @deprecated replace with {@link org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation}
 * An operation which potentially makes changes to the workspace. All resource modification should
 * be performed using this operation. The primary consequence of using this operation is that events
 * which typically occur as a result of workspace changes (such as the firing of resource deltas,
 * performance of autobuilds, etc.) are deferred until the outermost operation has successfully
 * completed.
 * <p>
 * Subclasses must implement <code>execute</code> to do the work of the operation.
 * </p>
 * This class is EXPERIMENTAL and is subject to substantial changes.
 */
public abstract class WTPOperation implements IHeadlessRunnableWithProgress {

	private static Hashtable threadToExtendedOpControl;

	private class ExtendedOpControl {
		private boolean allowExtensions;
		List restrictedExtensions;

		public ExtendedOpControl(boolean allowExtensions, List restrictedExtensions) {
			this.allowExtensions = allowExtensions;
			this.restrictedExtensions = restrictedExtensions;
		}

		public boolean shouldExecute(String operationID) {
			return allowExtensions && !restrictedExtensions.contains(operationID);
		}
	}

	/**
	 * The dataModel used to execute this operation
	 */
	protected WTPOperationDataModel operationDataModel;

	private OperationStatus opStatus;

	private String id;

	/**
	 * Constructor for the operation. Clients should use this constructor instead of the no argument
	 * constructor.
	 * 
	 * @param operationDataModel
	 */
	public WTPOperation(WTPOperationDataModel operationDataModel) {
		setOperationDataModel(operationDataModel);
	}

	/**
	 * This no argument constructor should not be used by clients. This is for extended operations.
	 * 
	 * ExtendedOperations
	 */
	public WTPOperation() {
	}

	// TODO see if this can be made package visible only.
	/**
	 * Note: This method is for internal use only. Clients should not call this method.
	 * 
	 * ExtendedOperations
	 * 
	 * @param value
	 *            the operation's id
	 */
	public final void setID(String value) {
		Assert.isTrue(this.id == null, WTPResourceHandler.getString("22")); //$NON-NLS-1$
		Assert.isNotNull(value, WTPResourceHandler.getString("23")); //$NON-NLS-1$
		this.id = value;
	}

	// TODO see if this can be removed
	/**
	 * Note: This method is for internal use only. Clients should not call this method.
	 * 
	 * ExtendedOperations
	 * 
	 * @return the operation's id
	 */
	public final String getID() {
		return this.id;
	}

	// TODO see if this can be make package visible only.
	/**
	 * Note: This method is for internal use only. Clients should not call this method.
	 * 
	 * @param operationDataModel
	 */
	public final void setOperationDataModel(WTPOperationDataModel operationDataModel) {
		this.operationDataModel = operationDataModel;
	}

	/**
	 * Returns the dataModels used by this operation.
	 * 
	 * @return the dataModels used by this operation.
	 */
	public final WTPOperationDataModel getOperationDataModel() {
		return operationDataModel;
	}

	/**
	 * Returns the result status of this operation. If this operation net been executed, then it
	 * will return null.
	 * 
	 * @return he result status of this operation.
	 */
	public IStatus getStatus() {
		if (null == opStatus)
			return WTPCommonPlugin.OK_STATUS;
		return opStatus;
	}

	/**
	 * Performs the steps that are to be treated as a single logical workspace change.
	 * <p>
	 * Subclasses must implement this method.
	 * </p>
	 * 
	 * @param monitor
	 *            the progress monitor to use to display progress and field user requests to cancel
	 * @exception CoreException
	 *                if the operation fails due to a CoreException
	 * @exception InvocationTargetException
	 *                if the operation fails due to an exception other than CoreException
	 * @exception InterruptedException
	 *                if the operation detects a request to cancel, using
	 *                <code>IProgressMonitor.isCanceled()</code>, it should exit by throwing
	 *                <code>InterruptedException</code>
	 */
	protected abstract void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException;

	/**
	 * Subclasses should override as necessary to perform any additional initialization before
	 * executing.
	 * 
	 * @param monitor
	 *            the progress monitor
	 */
	protected void initilize(IProgressMonitor monitor) {
		// Making sure the status objects are initialized
		// status = null;
		opStatus = null;
	}

	private ComposedExtendedOperationHolder initializeExtensionOperations() {
		return OperationExtensionRegistry.getExtensions(this);
	}

	/**
	 * <p>
	 * Subclasses should override as necessary to perform any post executiong cleanup. Subclasses,
	 * should not initialize any resources anywhere other than within either the
	 * initialize(IProjgressMonitor) or the execute(IProgressMonitor) methods. This method will
	 * always be called when an operation is run irregardless of whether the execution is
	 * successful.
	 * </p>
	 * <p>
	 * Clients should never need to call this method
	 * </p>
	 * 
	 * @param monitor
	 *            the progress monitor
	 */
	protected void dispose(IProgressMonitor monitor) {
	}

	// TODO see if this can be removed
	/**
	 * Note: This method is for internal use only. Clients should not call this method.
	 * 
	 * @return the workspace
	 */
	protected IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	// TODO see if this can be removed
	/**
	 * Note: This method is for internal use only. Clients should not call this method.
	 * 
	 * @param op
	 *            the to append
	 * @return A new operation
	 */
	public WTPOperation append(WTPOperation op) {
		ComposedOperation composedOp = new ComposedOperation();
		composedOp.addRunnable(this);
		composedOp.addRunnable(op);
		return composedOp;
	}

	/**
	 * Initiates a batch of changes, by invoking the execute() method as a workspace runnable.
	 * Client should always call this method and never call the execute() method.
	 * 
	 * @param monitor
	 *            the progress monitor to use to display progress
	 * @exception InvocationTargetException
	 *                wraps any CoreException, runtime exception or error thrown by the execute()
	 *                method
	 * @see WorkspaceModifyOperation - this class was directly copied from it
	 */
	public synchronized final void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		final InvocationTargetException[] iteHolder = new InvocationTargetException[1];
		try {

			IWorkspaceRunnable workspaceRunnable = new IWorkspaceRunnable() {

				public void run(IProgressMonitor pm) throws CoreException {
					try {
						doRun(pm);
						if (opStatus != null && !opStatus.isOK()) {
							// TODO do something with the status
							System.out.println(opStatus.getMessage());
						}
					} catch (InvocationTargetException e) {
						// Pass it outside the workspace runnable
						iteHolder[0] = e;
					} catch (InterruptedException e) {
						// Re-throw as OperationCanceledException, which will
						// be
						// caught and re-thrown as InterruptedException below.
						throw new OperationCanceledException(e.getMessage());
					}
				}
			};
			ISchedulingRule rule = getSchedulingRule();
			if (rule == null)
				ResourcesPlugin.getWorkspace().run(workspaceRunnable, monitor);
			else
				ResourcesPlugin.getWorkspace().run(workspaceRunnable, rule, 0, monitor);
		} catch (CoreException e) {
			if (e.getStatus().getCode() == IResourceStatus.OPERATION_FAILED)
				throw new WFTWrappedException(e.getStatus().getException(), e.getMessage());
			throw new WFTWrappedException(e);
		} catch (OperationCanceledException e) {
			throw new InterruptedException(e.getMessage());
		}
		// Re-throw the InvocationTargetException, if any occurred
		if (iteHolder[0] != null) {
			throw new WFTWrappedException(iteHolder[0].getTargetException(), iteHolder[0].getMessage());
		}
	}

	/**
	 * @return
	 */
	protected ISchedulingRule getSchedulingRule() {
		return null;
	}

	/**
	 * Typically clients should call the run(IProgressMonitor) method instead of this method. This
	 * may be used by subclassed operations during execution to invoke other operations. This method
	 * runs within the same the same WorkspaceRunnable as the calling operation.
	 * 
	 * @param monitor
	 *            the progress monitor to use to display progress
	 * @throws CoreException
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */
	public final void doRun(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
		if (null == threadToExtendedOpControl) {
			threadToExtendedOpControl = new Hashtable();
		}
		final Thread currentThread = Thread.currentThread();
		final boolean rootOperation = !threadToExtendedOpControl.containsKey(currentThread);

		boolean alreadyLocked = operationDataModel == null ? true : operationDataModel.isLocked();
		boolean operationValidationEnabled = operationDataModel == null ? false : operationDataModel.isOperationValidationEnabled();
		try {
			if (rootOperation) {
				boolean allowExtensions = operationDataModel == null ? true : operationDataModel.getBooleanProperty(WTPOperationDataModel.ALLOW_EXTENSIONS);
				List restrictedExtensions = operationDataModel == null ? Collections.EMPTY_LIST : (List) operationDataModel.getProperty(WTPOperationDataModel.RESTRICT_EXTENSIONS);
				ExtendedOpControl extendedOpControl = new ExtendedOpControl(allowExtensions, restrictedExtensions);
				threadToExtendedOpControl.put(currentThread, extendedOpControl);
			}
			if (!alreadyLocked) {
				operationDataModel.setLocked(true);
			}
			if (operationValidationEnabled) {
				operationDataModel.setOperationValidationEnabled(false);
				IStatus status = operationDataModel.validateDataModel();
				if (!status.isOK()) {
					// TODO display something to user and remove System.out
					System.out.println(WTPResourceHandler.getString("24", new Object[]{status.getMessage()})); //$NON-NLS-1$
					Thread.dumpStack();
					return;
				}
			}
			initilize(monitor);

			if (!validateEdit())
				return;
			// if (getStatus().isOK()) {
			ComposedExtendedOperationHolder extOpHolder = initializeExtensionOperations();
			IStatus preOpStatus = runPreOps(monitor, extOpHolder);
			execute(monitor);
			IStatus postOpStatus = runPostOps(monitor, extOpHolder);
			if (null != preOpStatus)
				addExtendedStatus(preOpStatus);
			if (null != postOpStatus)
				addExtendedStatus(postOpStatus);
			// }
		} finally {
			dispose(monitor);
			if (rootOperation) {
				threadToExtendedOpControl.remove(currentThread);
			}
			if (!alreadyLocked) {
				operationDataModel.setLocked(false);
			}
			if (operationValidationEnabled) {
				operationDataModel.setOperationValidationEnabled(true);
			}

		}
		// CoreException and OperationCanceledException are propagated
	}

	/**
	 * This method is called when an operation is run after initialize() is called, but before
	 * execute(). If this method returns <code>true</code> then operation's execution continues,
	 * otherwise, execution is aborted. Subclasses should override this method to provide any final
	 * initialization and validation required before execution.
	 * 
	 * @return
	 */
	protected boolean validateEdit() {
		return true;
	}

	// TODO lock down addStatus so it throws runtime exceptions if not called during execute.
	// TODO make this protected and create a package level accessor for WTPOperationJobAdapter
	/**
	 * <p>
	 * Adds a status to this opererations status. If this operation currently has no status, then
	 * the specified status becomes the operation's status. If the operation already has a status,
	 * then that status is converted to a multistatus and the specified status is appendend to it.
	 * </p>
	 * <p>
	 * This method should only be called from the execute() method of subclasses.
	 * </p>
	 * 
	 * @param aStatus
	 *            the status to add.
	 */
	public final void addStatus(IStatus aStatus) {
		if (opStatus == null) {
			opStatus = new OperationStatus(aStatus.getMessage(), aStatus.getException());
			opStatus.setSeverity(aStatus.getSeverity());
			opStatus.add(aStatus);
		} else {
			opStatus.add(aStatus);
		}
	}

	/**
	 * This is to keep track of extended operation stati. If the main status is WARNING, an extended
	 * status of ERROR will not make the main status ERROR. If the main status is OK, an extended
	 * status of ERROR or WARNING will make the main status WARNING.
	 * 
	 * @param aStatus
	 */
	private void addExtendedStatus(IStatus aStatus) {
		if (opStatus == null) {
			opStatus = new OperationStatus(new IStatus[]{WTPCommonPlugin.OK_STATUS});
		}
		opStatus.addExtendedStatus(aStatus);
	}

	private IStatus runPostOps(IProgressMonitor pm, ComposedExtendedOperationHolder extOpHolder) {
		IStatus postOpStatus = null;
		if ((extOpHolder != null) && extOpHolder.hasPostOps()) {
			postOpStatus = runExtendedOps(extOpHolder.getPostOps(), pm);
		}

		return postOpStatus;
	}

	private IStatus runPreOps(IProgressMonitor pm, ComposedExtendedOperationHolder extOpHolder) {
		IStatus preOpStatus = null;
		if ((extOpHolder != null) && extOpHolder.hasPreOps()) {
			preOpStatus = runExtendedOps(extOpHolder.getPreOps(), pm);
		}
		return preOpStatus;
	}

	private IStatus runExtendedOps(List opList, IProgressMonitor pm) {
		WTPOperation op = null;
		OperationStatus returnStatus = null;
		IStatus localStatus;
		String opId = null;
		ExtendedOpControl opControl = (ExtendedOpControl) threadToExtendedOpControl.get(Thread.currentThread());
		for (int i = 0; i < opList.size(); i++) {
			op = (WTPOperation) opList.get(i);
			opId = op.getID();
			if (opControl.shouldExecute(op.getClass().getName()) && opControl.shouldExecute(opId)) {
				try {
					boolean shouldExtendedRun = true;
					List extendedContext = (List) operationDataModel.getProperty(WTPOperationDataModel.EXTENDED_CONTEXT);
					for (int contextCount = 0; shouldExtendedRun && contextCount < extendedContext.size(); contextCount++) {
						IProject project = (IProject) AdaptabilityUtility.getAdapter(extendedContext.get(contextCount), IProject.class);
						if (null != project && !IEnablementManager.INSTANCE.getIdentifier(opId, project).isEnabled()) {
							shouldExtendedRun = false;
						}
					}
					if (shouldExtendedRun) {
						op.setOperationDataModel(operationDataModel);
						op.doRun(new SubProgressMonitor(pm, IProgressMonitor.UNKNOWN));
						localStatus = op.getStatus();
					} else
						localStatus = null;
				} catch (Exception e) {
					localStatus = new Status(IStatus.ERROR, WTPCommonPlugin.PLUGIN_ID, 0, WTPResourceHandler.getString("25", new Object[]{op.getClass().getName()}), e); //$NON-NLS-1$
				}
				if (localStatus != null) {
					if (returnStatus == null) {
						returnStatus = new OperationStatus(new IStatus[]{localStatus});
					} else {
						returnStatus.add(localStatus);
					}
				}
			}
		}
		return returnStatus;
	}

	// protected WTPOperation getRootExtendedOperation() {
	// if (this.getRootExtendedOperation() == null)
	// return this;
	// return this.getRootExtendedOperation();
	// }

	// TODO this should be deleted.
	/**
	 * Note: This method is for internal use only. Clients should not call this method.
	 */
	protected static final void runNestedDefaultOperation(WTPOperationDataModel model, IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		WTPOperation op = model.getDefaultOperation();
		if (op != null)
			op.run(monitor);
	}
}