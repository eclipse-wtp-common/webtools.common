/***************************************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.frameworks.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.wst.common.environment.IEnvironment;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.internal.datamodel.IWorkspaceRunnableWithStatus;
import org.eclipse.wst.common.frameworks.internal.operations.ComposedExtendedOperationHolder;
import org.eclipse.wst.common.frameworks.internal.operations.OperationStatus;

public class OperationManager {
	private IDataModel dataModel;
	private DataModelManager dataModelManager;
	private TableEntry rootOperation;
	private HashMap operationTable;
	private Stack runStopList;
	private IProgressMonitor monitor;
	private IAdaptable adaptable;
	private OperationStatus status;
	private OperationListener preExecuteListener;
	private OperationListener postExecuteListener;
	private OperationListener undoExecuteListener;
	private IEnvironment environment;

	public OperationManager(DataModelManager aDataModelManager, IDataModelOperation aRootOperation, IEnvironment aEnvironment) {
		if (aRootOperation == null)
			aRootOperation = new NullOperation();

		TableEntry entry = new TableEntry(aRootOperation);

		dataModelManager = aDataModelManager;
		dataModel = dataModelManager.getDataModel();
		rootOperation = entry;
		operationTable = new HashMap();
		runStopList = new Stack();
		operationTable.put(aRootOperation.getID(), entry);
		environment = aEnvironment;
		addExtendedOperations(aRootOperation);

		OperationListener defaultListener = new OperationListener() {
			public boolean notify(IDataModelOperation operation) {
				return true;
			}
		};

		preExecuteListener = defaultListener;
		postExecuteListener = defaultListener;
		undoExecuteListener = defaultListener;
	}

	public void setProgressMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	public void setAdaptable(IAdaptable adaptable) {
		this.adaptable = adaptable;
	}

	public void addExtendedPreOperation(String operationId, IDataModelOperation insertOperation) {
		TableEntry entry = (TableEntry) operationTable.get(operationId);

		if (entry != null) {
			TableEntry newEntry = new TableEntry(insertOperation);

			entry.preOperations.add(newEntry);
			operationTable.put(insertOperation.getID(), newEntry);
		}
	}

	public void addExtendedPostOperation(String operationId, IDataModelOperation insertOperation) {
		TableEntry entry = (TableEntry) operationTable.get(operationId);

		if (entry != null) {
			TableEntry newEntry = new TableEntry(insertOperation);

			entry.postOperations.add(newEntry);
			operationTable.put(insertOperation.getID(), newEntry);
		}
	}

	public void setPreExecuteListener(OperationListener listener) {
		if (listener != null)
			preExecuteListener = listener;
	}

	public void setPostExecuteListener(OperationListener listener) {
		if (listener != null)
			postExecuteListener = listener;
	}

	public void setUndoExecuteListener(OperationListener listener) {
		if (listener != null)
			undoExecuteListener = listener;
	}

	public IStatus runOperations() {
		boolean continueRun = true;
		RunListEntry runEntry = startNewRun();

		status = null;

		// All operations have already been run so just return OK.
		if (runEntry.stackEntries.empty())
			return Status.OK_STATUS;

		while (continueRun) {
			continueRun = runOperationsUntilStopped(runEntry) && !runEntry.stackEntries.empty();
		}

		if (status != null && status.getSeverity() == IStatus.ERROR) {
			undoLastRun();
		}
		return status;
	}

	public void undoLastRun() {
		if (!runStopList.empty()) {
			RunListEntry runListEntry = (RunListEntry) runStopList.pop();

			for (int index = runListEntry.executedOperations.size() - 1; index >= 0; index--) {
				IDataModelOperation operation = (IDataModelOperation) runListEntry.executedOperations.elementAt(index);
				Set dataModelIDs = operation.getDataModelIDs();

				if (dataModelIDs != null && dataModelIDs.size() > 0) {
					Iterator ids = dataModelIDs.iterator();

					while (ids.hasNext()) {
						String dataModelID = (String) ids.next();
						dataModelManager.removeNestedDataModel(dataModelID);
					}
				}

				try {
					undoExecuteListener.notify(operation);
				} catch (Throwable exc) {
					// TODO report undo notify exception.
				}

				if (operation.canUndo()) {
					try {
						runOperation(operation, true);
					} catch (Throwable exc) {
						// TODO report an undo exception here.
					}
				}
			}
		}
	}

	private RunListEntry startNewRun() {
		RunListEntry newEntry = null;

		if (runStopList.empty()) {
			newEntry = new RunListEntry(rootOperation);
		} else {
			RunListEntry topRunList = (RunListEntry) runStopList.peek();
			newEntry = new RunListEntry(topRunList);
		}

		runStopList.push(newEntry);

		return newEntry;
	}

	private boolean runOperationsUntilStopped(RunListEntry runListEntry) {
		StackEntry stackEntry = (StackEntry) runListEntry.stackEntries.peek();
		boolean continueRun = true;

		// Run extended pre operations.
		for (int index = stackEntry.preOperationIndex + 1; continueRun && index < stackEntry.tableEntry.preOperations.size(); index++) {
			TableEntry tableEntry = (TableEntry) stackEntry.tableEntry.preOperations.elementAt(index);

			runListEntry.stackEntries.push(new StackEntry(tableEntry));
			stackEntry.preOperationIndex = index;
			continueRun = runOperationsUntilStopped(runListEntry);
		}

		if (continueRun && stackEntry.preOpChildIndex == -1)
			stackEntry.addPreOpChildren();

		// Run child pre operations.
		for (int index = stackEntry.preOpChildIndex + 1; continueRun && index < stackEntry.preOpChildren.size(); index++) {
			TableEntry tableEntry = (TableEntry) stackEntry.preOpChildren.elementAt(index);

			runListEntry.stackEntries.push(new StackEntry(tableEntry));
			stackEntry.preOpChildIndex = index;
			continueRun = runOperationsUntilStopped(runListEntry);
		}

		if (continueRun && !stackEntry.operationExecuted) {
			IDataModelOperation operation = stackEntry.tableEntry.operation;

			try {
				continueRun = preExecuteListener.notify(operation);

				if (continueRun) {
					Set dataModelIDs = operation.getDataModelIDs();

					if (dataModelIDs != null && dataModelIDs.size() > 0) {
						Iterator ids = dataModelIDs.iterator();

						while (ids.hasNext()) {
							String dataModelID = (String) ids.next();
							dataModelManager.addNestedDataModel(dataModelID);
						}
					}

					operation.setDataModel(dataModel);
					operation.setEnvironment(environment);
					setStatus(runOperation(operation, false));
					runListEntry.executedOperations.add(operation);
					stackEntry.operationExecuted = true;
					continueRun = postExecuteListener.notify(operation);
				}
			} catch (Throwable exc) {
				setStatus(new Status(IStatus.ERROR, "id", 0, exc.getMessage() == null ? exc.toString() : exc.getMessage(), exc));
			}

			if (status != null && status.getSeverity() == IStatus.ERROR) {
				// This isn't really true, but it will cause the run operations to stop.
				continueRun = false;
			}
		}

		if (continueRun && stackEntry.postOpChildIndex == -1)
			stackEntry.addPostOpChildren();

		// Run child post operations.
		for (int index = stackEntry.postOpChildIndex + 1; continueRun && index < stackEntry.postOpChildren.size(); index++) {
			TableEntry tableEntry = (TableEntry) stackEntry.postOpChildren.elementAt(index);

			stackEntry.postOpChildIndex = index;
			runListEntry.stackEntries.push(new StackEntry(tableEntry));
			continueRun = runOperationsUntilStopped(runListEntry);
		}

		// Run extended post operations.
		for (int index = stackEntry.postOperationIndex + 1; continueRun && index < stackEntry.tableEntry.postOperations.size(); index++) {
			TableEntry tableEntry = (TableEntry) stackEntry.tableEntry.postOperations.elementAt(index);

			stackEntry.postOperationIndex = index;
			runListEntry.stackEntries.push(new StackEntry(tableEntry));
			continueRun = runOperationsUntilStopped(runListEntry);
		}

		// If we are have run the pre ops, this operation, and
		// the post ops, we should pop this entry off the stack.
		// Also, if continueRun is false we don't want to pop the stack since we will want to come
		// back to this entry later.
		if (continueRun) {
			runListEntry.stackEntries.pop();
		}

		return continueRun;
	}

	private IStatus runOperation(final IDataModelOperation operation, final boolean isUndo) {
		IWorkspaceRunnableWithStatus workspaceRunnable = new IWorkspaceRunnableWithStatus(adaptable) {
			public void run(IProgressMonitor pm) throws CoreException {
				try {
					if (isUndo) {
						this.setStatus(operation.undo(monitor, getInfo()));
					} else {
						this.setStatus(operation.execute(monitor, getInfo()));
					}
				} catch (Throwable exc) {
					exc.printStackTrace();
				}
			}
		};

		ISchedulingRule rule = operation.getSchedulingRule();

		try {
			if (rule == null) {
				ResourcesPlugin.getWorkspace().run(workspaceRunnable, monitor);
			} else {
				ResourcesPlugin.getWorkspace().run(workspaceRunnable, rule, operation.getOperationExecutionFlags(), monitor);
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return workspaceRunnable.getStatus();
	}

	private void setStatus(IStatus newStatus) {
		if (status == null) {
			status = new OperationStatus(newStatus.getMessage(), newStatus.getException());
			status.setSeverity(newStatus.getSeverity());
			status.add(newStatus);
		} else {
			status.add(newStatus);
		}
	}

	private void addExtendedOperations(IDataModelOperation operation) {
		ComposedExtendedOperationHolder extendedOps = ComposedExtendedOperationHolder.createExtendedOperationHolder(operation.getID());

		ArrayList preOps = null;
		ArrayList postOps = null;

		if (extendedOps != null) {
			preOps = extendedOps.getPreOps();
			postOps = extendedOps.getPostOps();
		}

		if (preOps == null)
			preOps = new ArrayList();
		if (postOps == null)
			postOps = new ArrayList();

		for (int index = 0; index < preOps.size(); index++) {
			IDataModelOperation newOperation = (IDataModelOperation) preOps.get(index);

			addExtendedPreOperation(operation.getID(), newOperation);
			addExtendedOperations(newOperation);
		}

		for (int index = 0; index < postOps.size(); index++) {
			IDataModelOperation newOperation = (IDataModelOperation) postOps.get(index);

			addExtendedPostOperation(operation.getID(), newOperation);
			addExtendedOperations(newOperation);
		}
	}

	private class RunListEntry {
		public Stack stackEntries;
		public Vector executedOperations;

		public RunListEntry(TableEntry newEntry) {
			stackEntries = new Stack();
			executedOperations = new Vector();
			stackEntries.push(new StackEntry(newEntry));
		}

		public RunListEntry(RunListEntry oldList) {
			stackEntries = new Stack();
			executedOperations = new Vector();

			for (int index = 0; index < oldList.stackEntries.size(); index++) {
				StackEntry oldEntry = (StackEntry) oldList.stackEntries.elementAt(index);
				stackEntries.add(new StackEntry(oldEntry));
			}
		}
	}

	private class StackEntry {
		public int preOperationIndex;
		public int postOperationIndex;
		public int preOpChildIndex;
		public int postOpChildIndex;
		public boolean operationExecuted;
		public Vector preOpChildren;
		public Vector postOpChildren;
		public TableEntry tableEntry;

		public StackEntry(TableEntry newTableEntry) {
			preOperationIndex = -1;
			postOperationIndex = -1;
			operationExecuted = false;
			preOpChildIndex = -1;
			postOpChildIndex = -1;
			tableEntry = newTableEntry;
		}

		public StackEntry(StackEntry newStackEntry) {
			preOperationIndex = newStackEntry.preOperationIndex;
			postOperationIndex = newStackEntry.postOperationIndex;
			operationExecuted = newStackEntry.operationExecuted;
			tableEntry = newStackEntry.tableEntry;
			preOpChildIndex = newStackEntry.preOpChildIndex;
			postOpChildIndex = newStackEntry.postOpChildIndex;
			preOpChildren = newStackEntry.preOpChildren == null ? null : new Vector(newStackEntry.preOpChildren);
			postOpChildren = newStackEntry.postOpChildren == null ? null : new Vector(newStackEntry.postOpChildren);
		}

		public void addPreOpChildren() {
			List preOps = tableEntry.operation.getPreOperations();

			preOpChildren = new Vector();

			if (preOps != null) {
				for (int index = 0; index < preOps.size(); index++) {
					IDataModelOperation op = (IDataModelOperation) preOps.get(index);
					TableEntry newEntry = new TableEntry(op);

					preOpChildren.add(newEntry);
					operationTable.put(op.getID(), newEntry);
					addExtendedOperations(op);
				}
			}
		}

		public void addPostOpChildren() {
			List postOps = tableEntry.operation.getPostOperations();

			postOpChildren = new Vector();

			if (postOps != null) {
				for (int index = 0; index < postOps.size(); index++) {
					IDataModelOperation op = (IDataModelOperation) postOps.get(index);
					TableEntry newEntry = new TableEntry(op);

					postOpChildren.add(newEntry);
					operationTable.put(op.getID(), newEntry);
					addExtendedOperations(op);
				}
			}
		}
	}

	private class TableEntry {
		public IDataModelOperation operation;
		public Vector preOperations;
		public Vector postOperations;

		public TableEntry(IDataModelOperation newOperation) {
			operation = newOperation;
			preOperations = new Vector(3);
			postOperations = new Vector(3);
		}
	}

	private class NullOperation extends AbstractDataModelOperation {
		public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return Status.OK_STATUS;
		}

		public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return Status.OK_STATUS;
		}

		public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return Status.OK_STATUS;
		}

	}
}
