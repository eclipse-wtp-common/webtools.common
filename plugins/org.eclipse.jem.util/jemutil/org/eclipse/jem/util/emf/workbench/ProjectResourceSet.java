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
 *  $$Revision: 1.2 $$  $$Date: 2005/02/04 23:12:01 $$ 
 */
package org.eclipse.jem.util.emf.workbench;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * A ResourceSet for an entire project. It allows sharing of resources from multiple editors/viewers for a project.
 * <p>
 * An additional Notification type is sent out by ProjectResourceSet's of project resource set about to be released. A release is
 * called when projects are about to be closed. They release all of the resources and unload them. This notification can be used 
 * to know that this is about to happen and to do something before the resources become invalid. It will be sent out just before the
 * resource set will be released. 
 * 
 * @see ProjectResourceSet#SPECIAL_NOTIFICATION_TYPE
 * @see ProjectResourceSet#PROJECTRESOURCESET_ABOUT_TO_RELEASE_ID 
 * @since 1.0.0
 */

public interface ProjectResourceSet extends ResourceSet {

	IProject getProject();
	
	/**
	 * Notification type in notifications from the ProjectResourceSet for
	 * special notifications, and not the standard ones from ResourceSet.
	 * 
	 * @see org.eclipse.emf.common.notify.Notification#getEventType()
	 * @since 1.1.0
	 */
	static int SPECIAL_NOTIFICATION_TYPE = Notification.EVENT_TYPE_COUNT+4;
	
	/**
	 * Notification Feature ID for resource set about to be released.
	 * Use {@link org.eclipse.emf.common.notify.Notification#getFeatureID(java.lang.Class)} to
	 * get this id. The getFeature() on notification will return null.
	 * 
	 * @since 1.1.0
	 */
	static int PROJECTRESOURCESET_ABOUT_TO_RELEASE_ID = 1000;

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