/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.datamodel;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;

/**
 * <p>
 * IDataModelManagerOperation is an IDataModelOperation returned from
 * {@linkorg.eclipse.wst.common.frameworks.datamodel.IDataModel#getDefaultOperation()}. This Operation wraps the IDataModelOperation returned
 * from the IDatModelProvider backing the IDataModel that created it.
 * </p>
 * <p>
 * This operation supports pre and post operation execution. Pre and post operations are
 * IDataModelOperations registered through the OperationExtension extension point which are executed
 * before and after the primary operation.
 * </p>
 * <p>
 * <b>Example 1</b> <br>
 * Suppose an IDataModelProvider returns IDataModelOperation X as its default operation, and this
 * IDataModelManagerOperation is wrapping X. Also suppose operation A is registered as a pre
 * operation to X and operation B is registered as a post operation to X. When this
 * IDataModelManagerOperation is executed the following operations will be executed in this order:
 * A, X, B which may be thought as a preorder execution of the following tree:
 * </p>
 * 
 * <pre>
 *           X
 *          / \
 *         A   B
 *         
 *   execution = A.execute(),  X.execute(), B.execute()
 *   undo = B.undo(), X.undo(), A.undo()
 *   redo = A.redo(), X.redo(), B.redo()
 * </pre>
 * 
 * <p>
 * <b>Example 2</b> <br>
 * If A had a pre operation C and post operation D, and B had a pre operation E and post operation F
 * then the tree would look like:
 * </p>
 * 
 * <pre>
 *            X
 *           / \
 *          /   \
 *         A     B
 *        / \   / \
 *       C   D E   F
 *   
 *   execution = C.execute(), A.execute(),  D.execute(), X.execute(), E.execute(), B.execute(), F.execute()
 *   undo = F.undo(), B.undo(), E.undo(), X.undo(), D.undo(), A.undo(), C.undo()
 *   redo = C.redo(), A.redo(),  D.redo(), X.redo(), E.redo(), B.redo(), F.redo()
 * </pre>
 * 
 * </p>
 * <b>Example 3</b> <br>
 * If there is more than one pre operation or post operation for a particular operation, then there
 * is no defined ordering of those pre and post operation with respect to each other, other than
 * that it will be consistent. For example suppose operation X had only two pre operations, A and B,
 * and two post operations, C and D. The execution tree would be one of the following:
 * </p>
 * 
 * <pre>
 *         X              X              X              X
 *        / \            / \            / \            / \
 *      AB   CD        BA   CD        AB   DC        BA   DC
 *   
 *    exec = ABXCD   exec = BAXCD   exec = ABXDC   exec = BAXDC
 *    undo = DCXBA   undo = DCXAB   undo = CDXBA   undo = CDXAB 
 *    redo = ABXCD   redo = BAXCD   redo = ABXDC   redo = BAXDC
 * </pre>
 * 
 * <p>
 * During execution, rollback, undo, and redo, {@link IDataModelPausibleOperationEvent}s are fired to all
 * registered {@link IDataModelPausibleOperationListener}s giving each of them an opportunity to pause the
 * operation by returning {@link IDataModelPausibleOperationListener#PAUSE}.
 * {@link IDataModelPausibleOperationEvent} has more details on when these events are fired.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @seeorg.eclipse.wst.common.frameworks.datamodel.IDataModel#getDefaultOperation()
 * 
 * @since 1.5
 */
public interface IDataModelPausibleOperation extends IDataModelOperation {

	/**
	 * <p>
	 * The exeuction state when the operation never been executed. When the operation is in this
	 * state it may only be executed.
	 * </p>
	 * 
	 * @see org.eclipse.core.commands.operations.IUndoableOperation#execute(IProgressMonitor, IAdaptable)
	 */
	public static final int NOT_STARTED = 0;
	/**
	 * <p>
	 * The exeuction state while the operation is executing. When the operation is in this state it
	 * may only be paused by an {@link IDataModelPausibleOperationListener}.
	 * </p>
	 * 
	 * @see org.eclipse.core.commands.operations.IUndoableOperation#execute(IProgressMonitor, IAdaptable)
	 * @see IDataModelPausibleOperationListener
	 */
	public static final int RUNNING_EXECUTE = 1;
	/**
	 * <p>
	 * The exeuction state while the operation is rolling back. When the operation is in this state
	 * it may only be paused by an {@link IDataModelPausibleOperationListener}.
	 * </p>
	 * 
	 * @see #rollBack(IProgressMonitor, IAdaptable)
	 * @see IDataModelPausibleOperationListener
	 */
	public static final int RUNNING_ROLLBACK = 2;
	/**
	 * <p>
	 * The exeuction state while the operation is redoing. When the operation is in this state it
	 * may only be paused by an {@link IDataModelPausibleOperationListener}.
	 * </p>
	 * 
	 * @see org.eclipse.core.commands.operations.IUndoableOperation#undo(IProgressMonitor, IAdaptable)
	 * @see IDataModelPausibleOperationListener
	 */
	public static final int RUNNING_UNDO = 3;
	/**
	 * <p>
	 * The exeuction state while the operaiton is redoing. When the operation is in this state it
	 * may only be paused by an {@link IDataModelPausibleOperationListener}.
	 * </p>
	 * 
	 * @see org.eclipse.core.commands.operations.IUndoableOperation#redo(IProgressMonitor, IAdaptable)
	 * @see IDataModelPausibleOperationListener
	 */
	public static final int RUNNING_REDO = 4;
	/**
	 * <p>
	 * The exeuction state when the operation has been paused while it was executing. When the
	 * operation is in this state it may either be resumed to continue execution or rolled back to
	 * undo the partial execution.
	 * </p>
	 * 
	 * @see #resume(IProgressMonitor, IAdaptable)
	 * @see #rollBack(IProgressMonitor, IAdaptable)
	 */
	public static final int PAUSED_EXECUTE = 5;
	/**
	 * <p>
	 * The exeuction state when the operation has been paused while it was rolling back. When the
	 * operation is in this state it may either be resumed to continue rolling back or executed
	 * which continue the foward execution from the point where the rollback was paused
	 * </p>
	 * 
	 * @see #resume(IProgressMonitor, IAdaptable)
	 * @see org.eclipse.core.commands.operations.IUndoableOperation#execute(IProgressMonitor, IAdaptable)
	 */
	public static final int PAUSED_ROLLBACK = 6;
	/**
	 * <p>
	 * The exeuction state when the operation has been paused while it was undoing. When the
	 * operation is in this state it may be only be resumed to continue undoing.
	 * </p>
	 * 
	 * @see #resume(IProgressMonitor, IAdaptable)
	 */
	public static final int PAUSED_UNDO = 7;
	/**
	 * <p>
	 * The exeuction state when the operation has been paused while it was redoing. When the
	 * operation is in this state it may only be resumed to continue redoing.
	 * </p>
	 * 
	 * @see #resume(IProgressMonitor, IAdaptable)
	 */
	public static final int PAUSED_REDO = 8;
	/**
	 * <p>
	 * The exeuction state when the operation finished execution. When the operation is in this
	 * state it may only be undone.
	 * </p>
	 * 
	 * @see org.eclipse.core.commands.operations.IUndoableOperation#undo(IProgressMonitor, IAdaptable)
	 */
	public static final int COMPLETE_EXECUTE = 9;
	/**
	 * <p>
	 * The exeuction state when the operation finished rolling back. When the operation is in this
	 * state it may only be executed.
	 * </p>
	 * 
	 * @see org.eclipse.core.commands.operations.IUndoableOperation#execute(IProgressMonitor, IAdaptable)
	 */
	public static final int COMPLETE_ROLLBACK = 10;
	/**
	 * <p>
	 * The exeuction state when the operation finished undoing. When the operation is in this state
	 * it may only be redone.
	 * </p>
	 * 
	 * @see org.eclipse.core.commands.operations.IUndoableOperation#redo(IProgressMonitor, IAdaptable)
	 */
	public static final int COMPLETE_UNDO = 11;
	/**
	 * <p>
	 * The exeuction state when the operation finished redoing. When the operation is in this state
	 * it may only be udone.
	 * </p>
	 * 
	 * @see org.eclipse.core.commands.operations.IUndoableOperation#undo(IProgressMonitor, IAdaptable)
	 */
	public static final int COMPLETE_REDO = 12;


	/**
	 * <p>
	 * Returns the current execution state of the operation. Returns one of:
	 * <ul>
	 * <li>{@link #NOT_STARTED}</li>
	 * <li>{@link #RUNNING_EXECUTE}</li>
	 * <li>{@link #RUNNING_ROLLBACK}</li>
	 * <li>{@link #RUNNING_UNDO}</li>
	 * <li>{@link #RUNNING_REDO}</li>
	 * <li>{@link #PAUSED_EXECUTE}</li>
	 * <li>{@link #PAUSED_ROLLBACK}</li>
	 * <li>{@link #PAUSED_UNDO}</li>
	 * <li>{@link #PAUSED_REDO}</li>
	 * <li>{@link #COMPLETE_EXECUTE}</li>
	 * <li>{@link #COMPLETE_ROLLBACK}</li>
	 * <li>{@link #COMPLETE_UNDO}</li>
	 * <li>{@link #COMPLETE_REDO}</li>
	 * </ul>
	 * <p>
	 * 
	 * @return The current execution state.
	 */
	public int getExecutionState();

	/**
	 * <p>
	 * Resumes execution if the operation is curently paused. An operation is paused if
	 * {@link #getExecutionState()} returns any of {@link #PAUSED_EXECUTE},
	 * {@link #PAUSED_ROLLBACK}, {@link #PAUSED_UNDO}, or {@link #PAUSED_REDO}.
	 * </p>
	 * 
	 * @param monitor
	 *            the progress monitor (or <code>null</code>) to use for reporting progress to
	 *            the user.
	 * @param info
	 *            the IAdaptable (or <code>null</code>) provided by the caller in order to supply
	 *            UI information for prompting the user if necessary. When this parameter is not
	 *            <code>null</code>, it should minimally contain an adapter for the
	 *            org.eclipse.swt.widgets.Shell.class.
	 * 
	 * @return the IStatus of the resume. The status severity should be set to <code>OK</code> if
	 *         the operation was successful, and <code>ERROR</code> if it was not. Any other
	 *         status is assumed to represent an incompletion of the resume.
	 */
	public IStatus resume(IProgressMonitor monitor, IAdaptable info) throws ExecutionException;

	/**
	 * <p>
	 * Rolls back the partial execution if the operation is currently in
	 * {@link #getExecutionState()} returns eiter {@link #PAUSED_EXECUTE} or
	 * {@link #PAUSED_ROLLBACK}.
	 * </p>
	 * 
	 * @param monitor
	 *            the progress monitor (or <code>null</code>) to use for reporting progress to
	 *            the user.
	 * @param info
	 *            the IAdaptable (or <code>null</code>) provided by the caller in order to supply
	 *            UI information for prompting the user if necessary. When this parameter is not
	 *            <code>null</code>, it should minimally contain an adapter for the
	 *            org.eclipse.swt.widgets.Shell.class.
	 * 
	 * @return the IStatus of the resume. The status severity should be set to <code>OK</code> if
	 *         the operation was successful, and <code>ERROR</code> if it was not. Any other
	 *         status is assumed to represent an incompletion of the resume.
	 * 
	 */
	public IStatus rollBack(IProgressMonitor monitor, IAdaptable info) throws ExecutionException;

	/**
	 * <p>
	 * Adds an operation listener to listen for operation events.
	 * </p>
	 * 
	 * @param operationListener
	 *            the IOperationListener to add.
	 */
	public void addOperationListener(IDataModelPausibleOperationListener operationListener);

	/**
	 * <p>
	 * Removes the specified IOperationLinstener.
	 * <p>
	 * 
	 * @param operationListener
	 *            The IOperatoinListener to remove.
	 */
	public void removeOperationListener(IDataModelPausibleOperationListener operationListener);

}
