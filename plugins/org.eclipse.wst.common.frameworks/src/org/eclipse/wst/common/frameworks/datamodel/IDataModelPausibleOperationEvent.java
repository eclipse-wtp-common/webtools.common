/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.datamodel;

/**
 * <p>
 * These events are fired to {@link IDataModelPausibleOperationListener}s from {@link IDataModelPausibleOperation}s during execution, rollback, undo,
 * and redo.
 * </p>
 * 
 * <p>
 * <b>Example 2</b> from {@link IDataModelPausibleOperation}<br>
 * <pre>
 *          X
 *         / \
 *        /   \
 *       A     B
 *      / \   / \
 *     C   D E   F
 * </pre>
 * In the example above, the events will be fired in the following order during an execution:
 * <table border=1>
 * <tr><th>operation</th><th>operation type</th><th>execution type</th></tr>
 * <tr><td>X</td><td>NODE_STARTING</td><td>EXECUTE</td></tr>
 * <tr><td>A</td><td>NODE_STARTING</td><td>EXECUTE</td></tr>
 * <tr><td>C</td><td>NODE_STARTING</td><td>EXECUTE</td></tr>
 * <tr><td>C</td><td>MAIN_STARTING</td><td>EXECUTE</td></tr>
 * <tr><td>C</td><td>MAIN_FINISHED</td><td>EXECUTE</td></tr>
 * <tr><td>C</td><td>NODE_FINISHED</td><td>EXECUTE</td></tr>
 * <tr><td>A</td><td>MAIN_STARTING</td><td>EXECUTE</td></tr>
 * <tr><td>A</td><td>MAIN_FINISHED</td><td>EXECUTE</td></tr>
 * <tr><td>D</td><td>NODE_STARTING</td><td>EXECUTE</td></tr>
 * <tr><td>D</td><td>MAIN_STARTING</td><td>EXECUTE</td></tr>
 * <tr><td>D</td><td>MAIN_FINISHED</td><td>EXECUTE</td></tr>
 * <tr><td>D</td><td>NODE_FINISHED</td><td>EXECUTE</td></tr>
 * <tr><td>A</td><td>NODE_FINISHED</td><td>EXECUTE</td></tr>
 * <tr><td>X</td><td>MAIN_STARTING</td><td>EXECUTE</td></tr>
 * <tr><td>X</td><td>MAIN_FINISHED</td><td>EXECUTE</td></tr>
 * <tr><td>B</td><td>NODE_STARTING</td><td>EXECUTE</td></tr>
 * <tr><td>E</td><td>NODE_STARTING</td><td>EXECUTE</td></tr>
 * <tr><td>E</td><td>MAIN_STARTING</td><td>EXECUTE</td></tr>
 * <tr><td>E</td><td>MAIN_FINISHED</td><td>EXECUTE</td></tr>
 * <tr><td>E</td><td>NODE_FINISHED</td><td>EXECUTE</td></tr>
 * <tr><td>B</td><td>MAIN_STARTING</td><td>EXECUTE</td></tr>
 * <tr><td>B</td><td>MAIN_FINISHED</td><td>EXECUTE</td></tr>
 * <tr><td>F</td><td>NODE_STARTING</td><td>EXECUTE</td></tr>
 * <tr><td>F</td><td>MAIN_STARTING</td><td>EXECUTE</td></tr>
 * <tr><td>F</td><td>MAIN_FINISHED</td><td>EXECUTE</td></tr>
 * <tr><td>F</td><td>NODE_FINISHED</td><td>EXECUTE</td></tr>
 * <tr><td>B</td><td>NODE_FINISHED</td><td>EXECUTE</td></tr>
 * <tr><td>X</td><td>NODE_FINISHED</td><td>EXECUTE</td></tr>
 * </table>
 * </p>
 * <p>
 * <b>Example 2</b> from {@link IDataModelPausibleOperation}<br>
 * <pre>
 *          X
 *         / \
 *        /   \
 *       A     B
 *      / \   / \
 *     C   D E   F
 * </pre>
 * In the example above, the events will be fired in the following order during a rollback:
 * <table border=1>
 * <tr><th>operation</th><th>operation type</th><th>execution type</th></tr>
 * <tr><td>F</td><td>MAIN_STARTING</td><td>ROLLBACK</td></tr>
 * <tr><td>F</td><td>MAIN_FINISHED</td><td>ROLLBACK</td></tr>
 * <tr><td>B</td><td>MAIN_STARTING</td><td>ROLLBACK</td></tr>
 * <tr><td>B</td><td>MAIN_FINISHED</td><td>ROLLBACK</td></tr>
 * <tr><td>E</td><td>MAIN_STARTING</td><td>ROLLBACK</td></tr>
 * <tr><td>E</td><td>MAIN_FINISHED</td><td>ROLLBACK</td></tr>
 * <tr><td>X</td><td>MAIN_STARTING</td><td>ROLLBACK</td></tr>
 * <tr><td>X</td><td>MAIN_FINISHED</td><td>ROLLBACK</td></tr>
 * <tr><td>D</td><td>MAIN_STARTING</td><td>ROLLBACK</td></tr>
 * <tr><td>D</td><td>MAIN_FINISHED</td><td>ROLLBACK</td></tr>
 * <tr><td>A</td><td>MAIN_STARTING</td><td>ROLLBACK</td></tr>
 * <tr><td>A</td><td>MAIN_FINISHED</td><td>ROLLBACK</td></tr>
 * <tr><td>C</td><td>MAIN_STARTING</td><td>ROLLBACK</td></tr>
 * <tr><td>C</td><td>MAIN_FINISHED</td><td>ROLLBACK</td></tr>
 * </table>
 * </p>
 * <p>
 * <b>Example 2</b> from {@link IDataModelPausibleOperation}<br>
 * <pre>
 *          X
 *         / \
 *        /   \
 *       A     B
 *      / \   / \
 *     C   D E   F
 * </pre>
 * In the example above, the events will be fired in the following order during an undo:
 * <table border=1>
 * <tr><th>operation</th><th>operation type</th><th>execution type</th></tr>
 * <tr><td>F</td><td>MAIN_STARTING</td><td>UNDO</td></tr>
 * <tr><td>F</td><td>MAIN_FINISHED</td><td>UNDO</td></tr>
 * <tr><td>B</td><td>MAIN_STARTING</td><td>UNDO</td></tr>
 * <tr><td>B</td><td>MAIN_FINISHED</td><td>UNDO</td></tr>
 * <tr><td>E</td><td>MAIN_STARTING</td><td>UNDO</td></tr>
 * <tr><td>E</td><td>MAIN_FINISHED</td><td>UNDO</td></tr>
 * <tr><td>X</td><td>MAIN_STARTING</td><td>UNDO</td></tr>
 * <tr><td>X</td><td>MAIN_FINISHED</td><td>UNDO</td></tr>
 * <tr><td>D</td><td>MAIN_STARTING</td><td>UNDO</td></tr>
 * <tr><td>D</td><td>MAIN_FINISHED</td><td>UNDO</td></tr>
 * <tr><td>A</td><td>MAIN_STARTING</td><td>UNDO</td></tr>
 * <tr><td>A</td><td>MAIN_FINISHED</td><td>UNDO</td></tr>
 * <tr><td>C</td><td>MAIN_STARTING</td><td>UNDO</td></tr>
 * <tr><td>C</td><td>MAIN_FINISHED</td><td>UNDO</td></tr>
 * </table>
 * </p>
 * <p>
 * <b>Example 2</b> from {@link IDataModelPausibleOperation}<br>
 * <pre>
 *          X
 *         / \
 *        /   \
 *       A     B
 *      / \   / \
 *     C   D E   F
 * </pre>
 * In the example above, the events will be fired in the following order during a redo:
 * <table border=1>
 * <tr><th>operation</th><th>operation type</th><th>execution type</th></tr>
 * <tr><td>C</td><td>MAIN_STARTING</td><td>UNDO</td></tr>
 * <tr><td>C</td><td>MAIN_FINISHED</td><td>UNDO</td></tr>
 * <tr><td>A</td><td>MAIN_STARTING</td><td>UNDO</td></tr>
 * <tr><td>A</td><td>MAIN_FINISHED</td><td>UNDO</td></tr>
 * <tr><td>D</td><td>MAIN_STARTING</td><td>UNDO</td></tr>
 * <tr><td>D</td><td>MAIN_FINISHED</td><td>UNDO</td></tr>
 * <tr><td>X</td><td>MAIN_STARTING</td><td>UNDO</td></tr>
 * <tr><td>X</td><td>MAIN_FINISHED</td><td>UNDO</td></tr>
 * <tr><td>E</td><td>MAIN_STARTING</td><td>UNDO</td></tr>
 * <tr><td>E</td><td>MAIN_FINISHED</td><td>UNDO</td></tr>
 * <tr><td>B</td><td>MAIN_STARTING</td><td>UNDO</td></tr>
 * <tr><td>B</td><td>MAIN_FINISHED</td><td>UNDO</td></tr>
 * <tr><td>F</td><td>MAIN_STARTING</td><td>UNDO</td></tr>
 * <tr><td>F</td><td>MAIN_FINISHED</td><td>UNDO</td></tr>
 * </table>
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @since 1.5
 */
public interface IDataModelPausibleOperationEvent {

	/**
	 * <p>
	 * The operation type set when an operation execution traversal is about to start within the
	 * tree of operations handled by the owning {@link IDataModelPausibleOperation}. This operation
	 * type is only set when the execution type is set to {@link #EXECUTE}. Using the example
	 * above, events with this operation type will be fired according to an in order traversal of
	 * the tree; i.e. XACDBEF
	 * </p>
	 */
	public static final int NODE_STARTING = 1;
	/**
	 * <p>
	 * The operation type set when an operation execution traversal finishes within the tree of
	 * operations handled by the owning {@link IDataModelPausibleOperation}. This operation type is
	 * only set when the execution type is set to {@link #EXECUTE}. Using the example above, events
	 * with this operation type will be fired according to a post order traversal of the tree; i.e.
	 * CDAEFBX
	 * </p>
	 */
	public static final int NODE_FINISHED = 2;
	/**
	 * <p>
	 * The operation type set right before an operation is going to do its work. This operation type
	 * may be used with all exection types. Using the example above, events with this operiton type
	 * will be fired according to the following tree traversal: CADXBEF
	 * <p>
	 */
	public static final int MAIN_STARTING = 3;
	/**
	 * <p>
	 * The operation type set immediatly after an operation finshed doing its work. This operation
	 * type may be used with all exection types. Using the example above, events with this operiton
	 * type will be fired according to the following tree traversal: CADXBEF
	 * <p>
	 */
	public static final int MAIN_FINISHED = 4;

	/**
	 * <p>
	 * The execution type set when during execution.
	 * </p>
	 */
	public static final int EXECUTE = 0;
	/**
	 * <p>
	 * The execution type set when during rollback.
	 * </p>
	 */
	public static final int ROLLBACK = 1;
	/**
	 * <p>
	 * The execution type set when during undo.
	 * </p>
	 */
	public static final int UNDO = 2;
	/**
	 * <p>
	 * The execution type set when during redo.
	 * </p>
	 */
	public static final int REDO = 3;

	/**
	 * <p>
	 * Returns the operation in question. This operation will be one of the operations within the
	 * tree of operations handled by the owning {@link IDataModelPausibleOperation}.
	 * </p>
	 * 
	 * @return the operation.
	 */
	public IDataModelOperation getOperation();

	/**
	 * <p>
	 * Returns the type of operation with respect to the {@link IDataModelPausibleOperation}
	 * executing it. This will be one of {@link #NODE_STARTING}, {@link #NODE_FINISHED},
	 * {@link #MAIN_STARTING}, or {@link #MAIN_FINISHED}.
	 * </p>
	 * 
	 * @return one of {@link #NODE_STARTING}, {@link #NODE_FINISHED}, {@link #MAIN_STARTING}, or
	 *         {@link #MAIN_FINISHED}.
	 */
	public int getOperationType();

	/**
	 * <p>
	 * Returns the type of execution the operation is about to begin. This will be one of
	 * {@link #EXECUTE}, {@link #ROLLBACK}, {@link #UNDO}, or {@link #REDO}.
	 * </p>
	 * 
	 * @return one of {@link #EXECUTE}, {@link #ROLLBACK}, {@link #UNDO}, or {@link #REDO}.
	 */
	public int getExecutionType();

}
