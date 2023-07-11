/*******************************************************************************
 * Copyright (c) 2003, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.datamodel;

/**
 * <p>
 * Used to listen for {@link IDataModelPausibleOperationEvent}s fired from {@link IDataModelPausibleOperation}s.
 * </p>
 * 
 * @since 1.5
 */
public interface IDataModelPausibleOperationListener {

	/**
	 * <p>
	 * Clients should return this constant from {@link #notify(IDataModelPausibleOperationEvent)} to specify that
	 * execution should continue.
	 * </p>
	 */
	public static final int CONTINUE = 0;

	/**
	 * <p>
	 * Clients should return this constant from {@link #notify(IDataModelPausibleOperationEvent)} to specify that
	 * execution should pause.
	 * </p>
	 */
	public static final int PAUSE = 1;


	/**
	 * <p>
	 * Listener interface by {@link IDataModelPausibleOperation} for operation execution
	 * notification. Prior to starting any operation's execution, notifications will be sent which
	 * allow clients to pause an operation's execution. Implementers should return {@link #CONTINUE}
	 * to allow the operation's execution to continue or {@link #PAUSE} to pause execution. If
	 * {@link #PAUSE} is returned, then the entire operation stack will be paused until it is
	 * resumed. The responsiblity for resuming a paused operation lies on the client that paused it.
	 * </p>
	 * 
	 * @param event
	 *            The operation event
	 * 
	 * @return return CONTINUE to continue, or PAUSE to pause.
	 * 
	 * @see IDataModelPausibleOperation#addOperationListener(IDataModelPausibleOperationListener)
	 * @see IDataModelPausibleOperation#resume(org.eclipse.core.runtime.IProgressMonitor,
	 *      org.eclipse.core.runtime.IAdaptable)
	 */
	public int notify(IDataModelPausibleOperationEvent event);

}
