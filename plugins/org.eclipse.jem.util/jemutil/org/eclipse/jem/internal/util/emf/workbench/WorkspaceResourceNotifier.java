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
 *  $$RCSfile: WorkspaceResourceNotifier.java,v $$
 *  $$Revision: 1.1 $$  $$Date: 2005/01/07 20:19:23 $$ 
 */
package org.eclipse.jem.internal.util.emf.workbench;


import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.notify.impl.NotifierImpl;

import org.eclipse.jem.util.emf.workbench.ProjectResourceSet;

/**
 * This class is used to capture all ADD and REMOVE notifications from each ProjectResourceSet
 * and forward it on to any interrested listeners.  This is to allow you to listen to one object
 * to gain all ADD and REMOVE notifications for each ResourceSet within the system.
 */
public class WorkspaceResourceNotifier extends NotifierImpl {
	protected Adapter projectAdapter = new WorkspaceResourceCacheAdapter();

	class WorkspaceResourceCacheAdapter extends AdapterImpl {
		/**
		 * Forward ADD and REMOVE notification.
		 */
		public void notifyChanged(Notification msg) {
			switch (msg.getEventType()) {
				case Notification.ADD :
				case Notification.ADD_MANY :
				case Notification.REMOVE :
				case Notification.REMOVE_MANY :
					eNotify(msg);
					break;
			}
		}
	}

	/**
	 * Constructor for WorkspaceResourceCache.
	 */
	public WorkspaceResourceNotifier() {
		super();
	}

	/**
	 * Begin listening to a ProjectResourceSet.
	 */
	public void beginListening(ProjectResourceSet aResourceSet) {
		if (aResourceSet != null) { 
			if (aResourceSet.eAdapters() == null ||  
			!aResourceSet.eAdapters().contains(projectAdapter))
			aResourceSet.eAdapters().add(projectAdapter);
		}
	}
	/**
	 * Stop listening to a ProjectResourceSet.
	 */
	public void stopListening(ProjectResourceSet aResourceSet) {
		if (aResourceSet != null)
			aResourceSet.eAdapters().remove(projectAdapter);
	}
}
