package org.eclipse.wst.common.frameworks.internal.operations;

/*
 * Licensed Material - Property of IBM (C) Copyright IBM Corp. 2001, 2002 - All
 * Rights Reserved. US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.internal.runtime.Assert;
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
import org.eclipse.wst.common.frameworks.internal.WTPResourceHandler;
import org.eclipse.wst.common.frameworks.internal.enablement.nonui.WFTWrappedException;
import org.eclispe.wst.common.frameworks.internal.enablement.IEnablementIdentifier;
import org.eclispe.wst.common.frameworks.internal.enablement.IEnablementManager;
import org.eclispe.wst.common.frameworks.internal.plugin.WTPCommonPlugin;


/**
 * An operation which potentially makes changes to the workspace. All resource modification should
 * be performed using this operation. The primary consequence of using this operation is that events
 * which typically occur as a result of workspace changes (such as the firing of resource deltas,
 * performance of autobuilds, etc.) are deferred until the outermost operation has successfully
 * completed.
 * <p>
 * Subclasses must implement <code>execute</code> to do the work of the operation.
 * </p>
 */
public abstract class WTPOperation implements IHeadlessRunnableWithProgress {

	protected WTPOperationDataModel operationDataModel;

	/**
	 * @deprecated instead of accessing this variable, call addStatus(IStatus status)
	 */
	protected IStatus status;

	OperationStatus opStatus;

	private String id;

	public WTPOperation(WTPOperationDataModel operationDataModel) {
		setOperationDataModel(operationDataModel);
	}

	public WTPOperation() {
	}

	/**
	 * If the operationId is already set, setOperationId() will do nothing
	 * 
	 * @param value
	 */
	public final void setID(String value) {
		Assert.isTrue(this.id == null, WTPResourceHandler.getString("22")); //$NON-NLS-1$
		Assert.isNotNull(value, WTPResourceHandler.getString("23")); //$NON-NLS-1$
		this.id = value;
	}

	public final String getID() {
		return this.id;
	}

	public final void setOperationDataModel(WTPOperationDataModel operationDataModel) {
		this.operationDataModel = operationDataModel;
	}

	public WTPOperationDataModel getOperationDataModel() {
		return this.operationDataModel;
	}

	/**
	 * @deprecated use addStatus(IStatus status)
	 */
	protected void setStatus(IStatus status) {
		addStatus(status);
	}

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

	protected void initilize(IProgressMonitor monitor) {
		//Making sure the status objects are initialized
		status = null;
		opStatus = null;
	}

	private ComposedExtendedOperationHolder initializeExtensionOperations() {
		return OperationExtensionRegistry.getExtensions(this);
	}

	protected void dispose(IProgressMonitor pm) {
	}

	protected IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	public WTPOperation append(WTPOperation op) {
		ComposedOperation composedOp = new ComposedOperation();
		composedOp.addRunnable(this);
		composedOp.addRunnable(op);
		return composedOp;
	}

	/**
	 * Initiates a batch of changes, by invoking the execute() method as a workspace runnable.
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
							//TODO do something with the status
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

	public final void doRun(IProgressMonitor pm) throws CoreException, InvocationTargetException, InterruptedException {

		boolean alreadyLocked = operationDataModel == null ? true : operationDataModel.isLocked();
		boolean operationValidationEnabled = operationDataModel == null ? false : operationDataModel.isOperationValidationEnabled();
		try {
			if (!alreadyLocked) {
				operationDataModel.setLocked(true);
			}
			if (operationValidationEnabled) {
				operationDataModel.setOperationValidationEnabled(false);
				status = operationDataModel.validateDataModel();
				if (!status.isOK()) {
					//TODO display something to user and remove System.out
					System.out.println(WTPResourceHandler.getString("24", new Object[]{status.getMessage()})); //$NON-NLS-1$
					Thread.dumpStack();
					return;
				}
			}
			initilize(pm);

			if (!validateEdit())
				return;
			//if (getStatus().isOK()) {
			ComposedExtendedOperationHolder extOpHolder = initializeExtensionOperations();
			IStatus preOpStatus = runPreOps(pm, extOpHolder);
			execute(pm);
			IStatus postOpStatus = runPostOps(pm, extOpHolder);
			if (null != preOpStatus)
				addExtendedStatus(preOpStatus);
			if (null != postOpStatus)
				addExtendedStatus(postOpStatus);
			//}
		} finally {
			dispose(pm);

			if (!alreadyLocked) {
				operationDataModel.setLocked(false);
			}
			if (operationValidationEnabled) {
				operationDataModel.setOperationValidationEnabled(true);
			}

		}
		// CoreException and OperationCanceledException are propagated
	}

	// Subclasses should override as they need to
	protected boolean validateEdit() {
		return true;
	}

	/**
	 * This is to track the status of the main flow of operations (extended operation stati are
	 * added through addExtendedStatus) execute() should call this method as necessary.
	 * 
	 * @param aStatus
	 */
	protected void addStatus(IStatus aStatus) {
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
		IEnablementIdentifier enablementIdentifer = null;
		for (int i = 0; i < opList.size(); i++) {
			op = (WTPOperation) opList.get(i);
			try {
				opId = op.getID();
				enablementIdentifer = IEnablementManager.INSTANCE.getIdentifier(opId, operationDataModel.getTargetProject());
				if (enablementIdentifer.isEnabled()) {
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
		return returnStatus;
	}

	protected WTPOperation getExtendedOperation() {
		return this;
	}

	protected WTPOperation getRootExtendedOperation() {
		if (this.getRootExtendedOperation() == null)
			return this;
		return this.getRootExtendedOperation();
	}

	protected void runNestedDefaultOperation(WTPOperationDataModel model, IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		WTPOperation op = model.getDefaultOperation();
		if (op != null)
			op.run(monitor);
	}
}