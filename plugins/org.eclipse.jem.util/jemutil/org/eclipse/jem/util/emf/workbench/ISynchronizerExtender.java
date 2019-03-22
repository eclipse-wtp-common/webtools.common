/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 *  $$RCSfile: ISynchronizerExtender.java,v $$
 *  $$Revision: 1.2 $$  $$Date: 2005/02/15 23:04:14 $$ 
 */
package org.eclipse.jem.util.emf.workbench;

import org.eclipse.core.resources.IResourceDelta;

/**
 * Implimenters allows clients, eg {@link org.eclipse.jem.util.emf.workbench.EMFNatureContributor}, to extend the behavior of the
 * ResourceSetWorkbenchSynchronizer.
 * 
 * @see org.eclipse.jem.util.emf.workbench.ResourceSetWorkbenchSynchronizer#addExtender(ISynchronizerExtender)
 * @since 1.0.0
 */
public interface ISynchronizerExtender {

	/**
	 * Notification that project has changed.
	 * 
	 * @param delta
	 * 
	 * @since 1.0.0
	 */
	void projectChanged(IResourceDelta delta);

	/**
	 * Notification that project has been closed.
	 * 
	 * 
	 * @since 1.0.0
	 */
	void projectClosed();
}