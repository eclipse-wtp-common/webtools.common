/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 *  $$RCSfile: ProjectResourceSet.java,v $$
 *  $$Revision: 1.1 $$  $$Date: 2005/01/07 20:19:23 $$ 
 */
package org.eclipse.jem.util.emf.workbench;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * A ResourceSet for an entire project. It allows sharing of resources from multiple editors/viewers for a project.
 * 
 * @since 1.0.0
 */

public interface ProjectResourceSet extends ResourceSet {

	IProject getProject();

	/**
	 * Call when the ResourceSet is no longer to be used.
	 * 
	 * 
	 * @since 1.0.0
	 */
	void release();

	/**
	 * Add the <code>resourceHandler</code> to the end of the list of resourceHandlers.
	 * 
	 * @param resourceHandler
	 *            IResourceHandler
	 * @return boolean Return <code>true</code> if it was added.
	 * @since 1.0.0
	 */
	boolean add(ResourceHandler resourceHandler);

	/**
	 * Add the <code>resourceHandler</code> to the front of the list of resourceHandlers.
	 * 
	 * @param resourceHandler
	 *            IResourceHandler
	 * @since 1.0.0
	 */
	void addFirst(ResourceHandler resourceHandler);

	/**
	 * Remove the <code>resourceHandler</code> from the list of resourceHandlers.
	 * 
	 * @param resourceHandler
	 *            IResourceHandler
	 * @return boolean Return true if it was removed.
	 * @since 1.0.0
	 */
	boolean remove(ResourceHandler resourceHandler);

	/**
	 * Return the ResourceSet synchronizer that will synchronize the ResourceSet with changes from the Workbench.
	 * 
	 * @return ResourceSetWorkbenchSynchronizer
	 * @since 1.0.0
	 */
	ResourceSetWorkbenchSynchronizer getSynchronizer();

	/**
	 * Set the ResourceSet synchronizer that will synchronize the ResourceSet with changes from the Workbench.
	 * 
	 * @param aSynchronizer
	 * @return ResourceSetWorkbenchSynchronizer
	 * @since 1.0.0
	 */
	void setSynchronizer(ResourceSetWorkbenchSynchronizer aSynchronizer);

	/**
	 * This should be called by clients whenever the structure of the project changes such that any cached URIs will be invalid. For example, if the
	 * source folders within the URIConverter change.
	 * 
	 * @since 1.0.0
	 */
	void resetNormalizedURICache();
}