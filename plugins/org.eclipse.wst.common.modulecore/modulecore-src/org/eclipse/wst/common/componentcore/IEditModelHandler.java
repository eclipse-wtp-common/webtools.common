/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.componentcore;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * <p>
 * Provides a standard interface for managing the lifecycle of an Edit Model. Clients which use
 * instances of this interface are <b>required to invoke {@see #dispose()}</b> when they have
 * completed their usage. Once clients have disposed that instance, they will not be able to invoke
 * {@see #save(IProgressMonitor)}or {@see #saveIfNecessary(IProgressMonitor)}&nbsp;and should be wary of
 * using any model objects acquired from the handler, as they may be or become stale.
 * </p>
 * @see org.eclipse.wst.common.componentcore.StructureEdit
 * @see org.eclipse.wst.common.componentcore.ArtifactEdit
 */
public interface IEditModelHandler {

	/**
	 * <p>
	 * Force a save of the underlying edit model and keep track of progress using the supplied
	 * progress monitor. Clients should avoid calling this version of save unless they are certain
	 * they require the model to be saved. Clients are encouraged to use
	 * {@see #saveIfNecessary(IProgressMonitor)}&nbsp;instead.
	 * </p>
	 * 
	 * @param aMonitor
	 *            A valid progress monitor or null
	 */
	void save(IProgressMonitor aMonitor);

	/**
	 * <p>
	 * Save the underlying edit model, if no other consumers are using the edit model, and keep
	 * track of progress using the supplied progress monitor. This version of save will only save if
	 * the underlying edit model is not shared with other consumers.
	 * </p>
	 * 
	 * @param aMonitor
	 *            A valid progress monitor or null
	 */
	void saveIfNecessary(IProgressMonitor aMonitor);

	/**
	 * <p>
	 * Clients must invoke this method when they have finished using the handler.
	 * </p>
	 */
	void dispose();
}