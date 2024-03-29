/*******************************************************************************
 * Copyright (c) 2003, 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.datamodel;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProperties;
import org.eclipse.wst.common.frameworks.internal.AdaptabilityUtility;
import org.eclipse.wst.common.frameworks.internal.WTPResourceHandler;
import org.eclipse.wst.common.frameworks.internal.enablement.IEnablementManager;
import org.eclipse.wst.common.frameworks.internal.operations.ComposedExtendedOperationHolder;
import org.eclipse.wst.common.frameworks.internal.operations.OperationStatus;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonPlugin;

public class DataModelPausibleOperationImpl extends WrappedOperation implements IDataModelPausibleOperation {

	// Stack of StackEntries to be executed
	protected Stack<OperationStackEntry> operationStackToExecute = null;

	// Stack of StackEntries already executed
	protected Stack<OperationStackEntry> undoStack = null;
	protected Stack<OperationStackEntry> redoStack = null;

	protected OperationStackEntry rootStackEntry = null;

	protected List<IDataModelPausibleOperationListener> operationListeners;

	protected int executionState = NOT_STARTED;

	public DataModelPausibleOperationImpl(IDataModelOperation operation) {
		super(operation);
	}

	@Override
	public void addOperationListener(IDataModelPausibleOperationListener operationListener) {
		if (null == operationListeners) {
			operationListeners = new ArrayList<IDataModelPausibleOperationListener>();
		}
		operationListeners.add(operationListener);
	}

	@Override
	public void removeOperationListener(IDataModelPausibleOperationListener operationListener) {
		if (null != operationListeners) {
			operationListeners.remove(operationListener);
		}
	}

	@Override
	public int getExecutionState() {
		return executionState;
	}

	protected static final int EXECUTE_IMPL = 0;
	protected static final int UNDO_IMPL = 1;
	protected static final int REDO_IMPL = 2;
	protected static final int ROLLBACK_IMPL = 3;
	protected static final int RESUME_IMPL = 4;

	private static Hashtable<Thread, IDataModelPausibleOperation> threadToExtendedOpControl = new Hashtable<Thread, IDataModelPausibleOperation>();

	protected IStatus cacheThreadAndContinue(IProgressMonitor monitor, IAdaptable info, int runType) throws ExecutionException {
		final Thread currentThread = Thread.currentThread();
		final boolean isTopLevelOperation = !threadToExtendedOpControl.containsKey(currentThread);
		try {
			if (isTopLevelOperation) {
				threadToExtendedOpControl.put(currentThread, this);
			}
			switch (runType) {
				case EXECUTE_IMPL :
					return executeImpl(monitor, info);
				case UNDO_IMPL :
					return undoImpl(monitor, info);
				case REDO_IMPL :
					return redoImpl(monitor, info);
				case ROLLBACK_IMPL :
					return rollBackImpl(monitor, info);
				case RESUME_IMPL :
					return resumeImpl(monitor, info);
			}
		} finally {
			if (isTopLevelOperation) {
				threadToExtendedOpControl.remove(currentThread);
			}
		}
		throw new RuntimeException();
	}

	@Override
	public IStatus resume(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return cacheThreadAndContinue(monitor, info, RESUME_IMPL);
	}

	protected IStatus resumeImpl(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		switch (executionState) {
			case NOT_STARTED :
				return executeImpl(monitor, info);
			case PAUSED_EXECUTE :
			case COMPLETE_ROLLBACK :
				return doExecute(monitor, info);
			case PAUSED_UNDO :
				return doUndo(monitor, info);
			case PAUSED_REDO :
				return redoImpl(monitor, info);
		}
		throw new RuntimeException();
	}

	@Override
	public boolean canRedo() {
		return (COMPLETE_UNDO == executionState) && super.canRedo();
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return cacheThreadAndContinue(monitor, info, REDO_IMPL);
	}

	protected IStatus redoImpl(IProgressMonitor monitor, IAdaptable info) {
		return doRedo(monitor, info);
	}

	protected IStatus doRedo(IProgressMonitor monitor, IAdaptable info) {
		try {
			executionState = RUNNING_REDO;
			OperationStatus returnStatus = null;
			int shouldContinue = IDataModelPausibleOperationListener.CONTINUE;
			while (IDataModelPausibleOperationListener.CONTINUE == shouldContinue && !redoStack.isEmpty()) {
				OperationStackEntry stackEntry = redoStack.peek();
				IDataModelOperation operation = stackEntry.getOperation();
				DataModelPausibleOperationEventImpl event = new DataModelPausibleOperationEventImpl(operation, IDataModelPausibleOperationEvent.MAIN_STARTING, IDataModelPausibleOperationEvent.REDO);
				shouldContinue = notifyOperationListeners(event);
				if (IDataModelPausibleOperationListener.PAUSE == shouldContinue) {
					continue;
				}
				undoStack.push(redoStack.pop());
				returnStatus = runOperation(operation, monitor, info, IDataModelPausibleOperationEvent.REDO, returnStatus);
				event = new DataModelPausibleOperationEventImpl(operation, IDataModelPausibleOperationEvent.MAIN_FINISHED, IDataModelPausibleOperationEvent.REDO);
				shouldContinue = notifyOperationListeners(event);

			}
			return returnStatus == null ? Status.OK_STATUS : returnStatus;
		} finally {
			executionState = redoStack.isEmpty() ? COMPLETE_REDO : PAUSED_REDO;
		}
	}

	@Override
	public boolean canUndo() {
		return (executionState == COMPLETE_EXECUTE || executionState == COMPLETE_REDO) && super.canUndo();
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return cacheThreadAndContinue(monitor, info, UNDO_IMPL);
	}

	protected IStatus undoImpl(IProgressMonitor monitor, IAdaptable info) {
		redoStack = new Stack<OperationStackEntry>();
		return doUndo(monitor, info);
	}

	protected IStatus doUndo(IProgressMonitor monitor, IAdaptable info) {
		try {
			executionState = RUNNING_UNDO;
			OperationStatus returnStatus = null;
			int shouldContinue = IDataModelPausibleOperationListener.CONTINUE;
			while (IDataModelPausibleOperationListener.CONTINUE == shouldContinue && !undoStack.isEmpty()) {
				OperationStackEntry stackEntry = undoStack.peek();
				IDataModelOperation operation = stackEntry.getOperation();
				DataModelPausibleOperationEventImpl event = new DataModelPausibleOperationEventImpl(operation, IDataModelPausibleOperationEvent.MAIN_STARTING, IDataModelPausibleOperationEvent.UNDO);
				shouldContinue = notifyOperationListeners(event);
				if (IDataModelPausibleOperationListener.PAUSE == shouldContinue) {
					continue;
				}
				redoStack.push(undoStack.pop());
				returnStatus = runOperation(operation, monitor, info, IDataModelPausibleOperationEvent.UNDO, returnStatus);
				event = new DataModelPausibleOperationEventImpl(operation, IDataModelPausibleOperationEvent.MAIN_FINISHED, IDataModelPausibleOperationEvent.UNDO);
				shouldContinue = notifyOperationListeners(event);

			}
			return returnStatus == null ? Status.OK_STATUS : returnStatus;
		} finally {
			executionState = undoStack.isEmpty() ? COMPLETE_UNDO : PAUSED_UNDO;
		}
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return cacheThreadAndContinue(monitor, info, EXECUTE_IMPL);
	}

	protected IStatus executeImpl(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		try {
			executionState = RUNNING_EXECUTE;
			undoStack = new Stack();
			operationStackToExecute = new Stack<OperationStackEntry>();
			rootStackEntry = new OperationStackEntry(null, rootOperation);
			operationStackToExecute.push(rootStackEntry);
			DataModelPausibleOperationEventImpl event = new DataModelPausibleOperationEventImpl(rootOperation, IDataModelPausibleOperationEvent.NODE_STARTING, IDataModelPausibleOperationEvent.EXECUTE);
			if (IDataModelPausibleOperationListener.CONTINUE == notifyOperationListeners(event)) {
				return doExecute(monitor, info);
			}
			return Status.OK_STATUS;
		} finally {
			executionState = operationStackToExecute.isEmpty() ? COMPLETE_EXECUTE : PAUSED_EXECUTE;
		}
	}

	protected IStatus doExecute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		try {
			OperationStatus returnStatus = null;
			int shouldContinue = IDataModelPausibleOperationListener.CONTINUE;
			while (IDataModelPausibleOperationListener.CONTINUE == shouldContinue && !operationStackToExecute.isEmpty()) {
				OperationStackEntry stackEntry = operationStackToExecute.peek();
				OperationStackEntry preStackEntry = stackEntry.getNextPreOperation();
				if (null != preStackEntry) {
					operationStackToExecute.push(preStackEntry);
					DataModelPausibleOperationEventImpl event = new DataModelPausibleOperationEventImpl(preStackEntry.getOperation(), IDataModelPausibleOperationEvent.NODE_STARTING, IDataModelPausibleOperationEvent.EXECUTE);
					shouldContinue = notifyOperationListeners(event);
					continue;
				}
				IDataModelOperation stackEntryOperation = stackEntry.getOperationForExecution();
				if (null != stackEntryOperation) {
					DataModelPausibleOperationEventImpl event = new DataModelPausibleOperationEventImpl(stackEntryOperation, IDataModelPausibleOperationEvent.MAIN_STARTING, IDataModelPausibleOperationEvent.EXECUTE);
					shouldContinue = notifyOperationListeners(event);
					if (IDataModelPausibleOperationListener.PAUSE == shouldContinue) {
						continue;
					}
					returnStatus = runOperation(stackEntryOperation, monitor, info, IDataModelPausibleOperationEvent.EXECUTE, returnStatus);
					undoStack.push(stackEntry);
					event = new DataModelPausibleOperationEventImpl(stackEntryOperation, IDataModelPausibleOperationEvent.MAIN_FINISHED, IDataModelPausibleOperationEvent.EXECUTE);
					shouldContinue = notifyOperationListeners(event);
					if (IDataModelPausibleOperationListener.PAUSE == shouldContinue) {
						continue;
					}
				}
				OperationStackEntry postStackEntry = stackEntry.getNextPostOperation();
				if (null != postStackEntry) {
					operationStackToExecute.push(postStackEntry);
					DataModelPausibleOperationEventImpl event = new DataModelPausibleOperationEventImpl(postStackEntry.getOperation(), IDataModelPausibleOperationEvent.NODE_STARTING, IDataModelPausibleOperationEvent.EXECUTE);
					shouldContinue = notifyOperationListeners(event);
					continue;
				}
				operationStackToExecute.pop();
				DataModelPausibleOperationEventImpl event = new DataModelPausibleOperationEventImpl(stackEntry.getOperation(), IDataModelPausibleOperationEvent.NODE_FINISHED, IDataModelPausibleOperationEvent.EXECUTE);
				shouldContinue = notifyOperationListeners(event);
			}
			return returnStatus == null ? Status.OK_STATUS : returnStatus;
		} finally {
			executionState = operationStackToExecute.isEmpty() ? COMPLETE_EXECUTE : PAUSED_EXECUTE;
		}
	}

	@Override
	public IStatus rollBack(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return cacheThreadAndContinue(monitor, info, ROLLBACK_IMPL);
	}

	protected IStatus rollBackImpl(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		if (PAUSED_EXECUTE == executionState || PAUSED_ROLLBACK == executionState) {
			try {
				executionState = RUNNING_ROLLBACK;
				OperationStatus returnStatus = null;
				int shouldContinue = IDataModelPausibleOperationListener.CONTINUE;
				while (IDataModelPausibleOperationListener.CONTINUE == shouldContinue && !undoStack.isEmpty()) {
					OperationStackEntry stackEntry = undoStack.peek();
					IDataModelOperation operation = stackEntry.getOperation();
					DataModelPausibleOperationEventImpl event = new DataModelPausibleOperationEventImpl(operation, IDataModelPausibleOperationEvent.MAIN_STARTING, IDataModelPausibleOperationEvent.ROLLBACK);
					shouldContinue = notifyOperationListeners(event);
					if (IDataModelPausibleOperationListener.PAUSE == shouldContinue) {
						continue;
					}
					undoStack.pop();
					OperationStackEntry executionTopStackEntry = stackEntry.rollBackOneOperation();
					if (operationStackToExecute.contains(executionTopStackEntry)) {
						while (operationStackToExecute.peek() != executionTopStackEntry) {
							operationStackToExecute.pop();
						}
					} else {
						Stack<OperationStackEntry> parentStack = new Stack<OperationStackEntry>();
						parentStack.push(executionTopStackEntry);
						OperationStackEntry entry = executionTopStackEntry.parent;
						while (!operationStackToExecute.contains(entry)) {
							parentStack.push(entry);
							entry = entry.parent;
						}
						while (operationStackToExecute.peek() != entry) {
							operationStackToExecute.pop();
						}
						while (!parentStack.isEmpty()) {
							operationStackToExecute.push(parentStack.pop());
						}
					}
					returnStatus = runOperation(operation, monitor, info, IDataModelPausibleOperationEvent.UNDO, returnStatus);
					event = new DataModelPausibleOperationEventImpl(operation, IDataModelPausibleOperationEvent.MAIN_FINISHED, IDataModelPausibleOperationEvent.ROLLBACK);
					shouldContinue = notifyOperationListeners(event);
				}
				return returnStatus == null ? Status.OK_STATUS : returnStatus;
			} finally {
				executionState = undoStack.isEmpty() ? COMPLETE_ROLLBACK : PAUSED_ROLLBACK;
			}
		}
		throw new RuntimeException();
	}

	private int notifyOperationListeners(DataModelPausibleOperationEventImpl event) {
		if (null != operationListeners) {
			IDataModelPausibleOperationListener listener = null;
			for (int i = 0; i < operationListeners.size(); i++) {
				listener = operationListeners.get(i);
				if (IDataModelPausibleOperationListener.PAUSE == listener.notify(event)) {
					return IDataModelPausibleOperationListener.PAUSE;
				}
			}
		}
		return IDataModelPausibleOperationListener.CONTINUE;
	}

	protected OperationStatus addStatus(OperationStatus returnStatus, IStatus status) {
		OperationStatus innerReturnStatus = returnStatus;
		if (innerReturnStatus == null) {
			innerReturnStatus = new OperationStatus(status.getMessage(), status.getException());
			innerReturnStatus.setSeverity(status.getSeverity());
			innerReturnStatus.add(status);
		} else {
			innerReturnStatus.add(status);
		}
		return innerReturnStatus;
	}

	private OperationStatus addExtendedStatus(OperationStatus returnStatus, IStatus aStatus) {
		OperationStatus innerReturnStatus = returnStatus;
		if (innerReturnStatus == null) {
			innerReturnStatus = new OperationStatus(new IStatus[]{WTPCommonPlugin.OK_STATUS});
		}
		innerReturnStatus.addExtendedStatus(aStatus);
		return innerReturnStatus;
	}

	private OperationStatus runOperation(final IDataModelOperation operation, final IProgressMonitor monitor, final IAdaptable info, final int executionType, OperationStatus returnStatus) {
		OperationStatus innerReturnStatus = returnStatus;
		if (rootOperation == operation) {
			IStatus status = runOperation(operation, monitor, info, IDataModelPausibleOperationEvent.EXECUTE);
			if (!status.isOK()) {
				innerReturnStatus = addStatus(innerReturnStatus, status);
			}
		} else {
			try {
				IStatus status = runOperation(operation, monitor, info, IDataModelPausibleOperationEvent.EXECUTE);
				if (!status.isOK()) {
					innerReturnStatus = addExtendedStatus(innerReturnStatus, status);
				}
			} catch (Exception e) {
				IStatus status = new Status(IStatus.ERROR, WTPCommonPlugin.PLUGIN_ID, 0, WTPResourceHandler.getString("25", new Object[]{operation.getClass().getName()}), e); //$NON-NLS-1$
				innerReturnStatus = addExtendedStatus(innerReturnStatus, status);
			}
		}
		return innerReturnStatus;
	}

	private IStatus runOperation(final IDataModelOperation operation, final IProgressMonitor monitor, final IAdaptable info, final int executionType) {
		IWorkspaceRunnableWithStatus workspaceRunnable = new IWorkspaceRunnableWithStatus(info) {
			@Override
			public void run(IProgressMonitor pm) throws CoreException {
				try {
					switch (executionType) {
						case IDataModelPausibleOperationEvent.EXECUTE :
							this.setStatus(operation.execute(pm, info));
							break;
						case IDataModelPausibleOperationEvent.UNDO :
							this.setStatus(operation.undo(pm, info));
							break;
						case IDataModelPausibleOperationEvent.REDO :
							this.setStatus(operation.redo(pm, info));
							break;
					}
					if (null == this.getStatus()) {
						this.setStatus(Status.OK_STATUS);
					}
				} catch (Exception e) {
					this.setStatus(new Status(IStatus.ERROR, WTPCommonPlugin.PLUGIN_ID, 0, WTPResourceHandler.getString("25", new Object[]{operation.getClass().getName()}), e)); //$NON-NLS-1$
					WTPCommonPlugin.logError(e);
				}
			}
		};

		ISchedulingRule rule = operation.getSchedulingRule();
		if (null == rule) {
			rule = ResourcesPlugin.getWorkspace().getRoot();
		}

		try {
			ResourcesPlugin.getWorkspace().run(workspaceRunnable, rule, operation.getOperationExecutionFlags(), monitor);
		} catch (CoreException e) {
			workspaceRunnable.setStatus(new Status(IStatus.ERROR, WTPCommonPlugin.PLUGIN_ID, 0, WTPResourceHandler.getString("25", new Object[]{operation.getClass().getName()}), e)); //$NON-NLS-1$
		}
		return workspaceRunnable.getStatus();
	}

	protected class OperationStackEntry {

		private IDataModelOperation operation;
		private IDataModelOperation operationForExecution;

		private boolean extendedOpsInitialized = false;

		private ComposedExtendedOperationHolder extOpHolder = null;

		private int preOpIndex = 0;
		private int postOpIndex = 0;

		private OperationStackEntry[] preOpStackEntries = null;
		private OperationStackEntry[] postOpStackEntries = null;

		public OperationStackEntry parent = null;

		public OperationStackEntry(OperationStackEntry parentEntry, IDataModelOperation dataModelOperation) {
			this.parent = parentEntry;
			this.operation = dataModelOperation;
			this.operationForExecution = dataModelOperation;
		}

		public OperationStackEntry rollBackOneOperation() {
			postOpIndex = 0;
			operationForExecution = operation;
			if (preOpIndex == 0 && parent != null) {
				return parent.rollBackExtended(this);
			}
			return this;
		}

		private OperationStackEntry rollBackExtended(OperationStackEntry extendedEntry) {
			while (postOpIndex > 0) {
				int index = --postOpIndex;
				if (postOpStackEntries[index] == extendedEntry) {
					postOpStackEntries[index] = null;
					return this;
				}
			}
			while (preOpIndex > 0) {
				int index = --preOpIndex;
				if (preOpStackEntries[index] == extendedEntry) {
					preOpStackEntries[index] = null;
					if (index != 0) {
						return this;
					}
				}
			}
			if (null != parent) {
				return parent.rollBackExtended(this);
			}
			return this;
		}

		public OperationStackEntry getNextPreOperation() {
			if (!extendedOpsInitialized) {
				initExtendedOps();
			}
			if (extOpHolder == null) {
				return null;
			}
			List ops = extOpHolder.getPreOps();
			if (ops == null || ops.size() <= preOpIndex) {
				return null;
			}

			while (preOpIndex < ops.size()) {
				int index = preOpIndex++;
				IDataModelOperation op = (IDataModelOperation) ops.get(index);
				if (shouldExecuteExtended(op.getID())) {
					op.setDataModel(getDataModel());
					op.setEnvironment(getEnvironment());
					preOpStackEntries[index] = new OperationStackEntry(this, op);
					return preOpStackEntries[index];
				}
			}
			return null;
		}

		@Override
		public String toString() {
			return getOperationID();
		}

		public String getOperationID() {
			return operation.getID();
		}

		public IDataModelOperation getOperation() {
			return operation;
		}

		public boolean executed() {
			return null != operationForExecution;
		}

		/**
		 * The root operation is only returned once; this way it is known whether it has been
		 * executed yet or not.
		 * 
		 * @return
		 */
		public IDataModelOperation getOperationForExecution() {
			try {
				return operationForExecution;
			} finally {
				operationForExecution = null;
			}
		}

		public OperationStackEntry getNextPostOperation() {
			if (!extendedOpsInitialized) {
				initExtendedOps();
			}
			if (extOpHolder == null) {
				return null;
			}
			List ops = extOpHolder.getPostOps();
			if (ops == null || ops.size() <= postOpIndex) {
				return null;
			}

			while (postOpIndex < ops.size()) {
				int index = postOpIndex++;
				IDataModelOperation op = (IDataModelOperation) ops.get(index);
				if (shouldExecuteExtended(op.getID())) {
					op.setDataModel(getDataModel());
					op.setEnvironment(getEnvironment());
					postOpStackEntries[index] = new OperationStackEntry(this, op);
					return postOpStackEntries[index];
				}
			}
			return null;

		}

		private void initExtendedOps() {
			if (shouldExecuteExtended(operation.getID())) {
				extOpHolder = ComposedExtendedOperationHolder.createExtendedOperationHolder(operation.getID());
				if (null != extOpHolder) {
					if (extOpHolder.hasPreOps()) {
						preOpStackEntries = new OperationStackEntry[extOpHolder.getPreOps().size()];
					}
					if (extOpHolder.hasPostOps()) {
						postOpStackEntries = new OperationStackEntry[extOpHolder.getPostOps().size()];
					}
				}
			}
			extendedOpsInitialized = true;
		}
	}

	protected boolean shouldExecuteExtended(String operationID) {
		// Check the top most level operation first
		DataModelPausibleOperationImpl threadRootOperation = (DataModelPausibleOperationImpl) threadToExtendedOpControl.get(Thread.currentThread());
		if (threadRootOperation != this && threadRootOperation.getDataModel() != getDataModel() && !threadRootOperation.shouldExecuteExtended(operationID)) {
			return false;
		}
		// No extended operations are being executed
		boolean allowExtensions = getDataModel().getBooleanProperty(IDataModelProperties.ALLOW_EXTENSIONS);
		if (!allowExtensions) {
			return false;
		}
		// This specific operation should not be executed
		List restrictedExtensions = (List) getDataModel().getProperty(IDataModelProperties.RESTRICT_EXTENSIONS);
		if (restrictedExtensions.contains(operationID)) {
			return false;
		}
		// This specific function group should not be executed
		List extendedContext = getDataModel().getExtendedContext();
		for (int contextCount = 0; contextCount < extendedContext.size(); contextCount++) {
			IProject project = AdaptabilityUtility.getAdapter(extendedContext.get(contextCount), IProject.class);
			if (null != project && !IEnablementManager.INSTANCE.getIdentifier(operationID, project).isEnabled()) {
				return false;
			}
		}
		return true;
	}

}
