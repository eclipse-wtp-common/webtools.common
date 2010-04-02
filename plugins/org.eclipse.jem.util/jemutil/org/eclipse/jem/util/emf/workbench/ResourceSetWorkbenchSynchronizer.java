/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 *  $$RCSfile: ResourceSetWorkbenchSynchronizer.java,v $$
 *  $$Revision: 1.8 $$  $$Date: 2010/04/02 14:57:14 $$ 
 */

package org.eclipse.jem.util.emf.workbench;

import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.emf.ecore.resource.ResourceSet;

import org.eclipse.jem.internal.util.emf.workbench.EMFWorkbenchContextFactory;
import org.eclipse.jem.util.plugin.JEMUtilPlugin;


/**
 * Synchronizer on the workbench. It listens for the project to see if it is closed or deleted. If it does it notifies this out.
 * 
 * @since 1.0.0
 */
public class ResourceSetWorkbenchSynchronizer implements IResourceChangeListener {

	protected IProject project;

	protected ResourceSet resourceSet;

	/** Extenders that will be notified after a pre build resource change */
	protected Set extenders;

	/** The delta for this project that will be broadcast to the extenders */
	protected IResourceDelta currentProjectDelta;

	private int currentEventType = -1;

	/**
	 * Constructor taking a resource set and project.
	 * 
	 * @param aResourceSet
	 * @param aProject
	 * 
	 * @since 1.0.0
	 */
	public ResourceSetWorkbenchSynchronizer(ResourceSet aResourceSet, IProject aProject) {
		resourceSet = aResourceSet;
		project = aProject;
		if (aResourceSet != null && aResourceSet instanceof ProjectResourceSet)
			((ProjectResourceSet) aResourceSet).setSynchronizer(this);
		initialize();
	}

	/**
	 * Get the project for this synchronizer
	 * 
	 * @return project
	 * 
	 * @since 1.0.0
	 */
	public IProject getProject() {
		return project;
	}

	/*
	 * @see IResourceChangeListener#resourceChanged(IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		currentEventType = event.getType();
		currentProjectDelta = null;
		if ((currentEventType == IResourceChangeEvent.PRE_CLOSE || currentEventType == IResourceChangeEvent.PRE_DELETE)
				&& event.getResource().equals(getProject())) {
			notifyExtendersOfClose();
			release();
		}
	}


	protected synchronized void notifyExtendersIfNecessary() {
		if (currentEventType != IResourceChangeEvent.POST_CHANGE || extenders == null || currentProjectDelta == null)
			return;
		for (Iterator iterator = Collections.unmodifiableSet(extenders).iterator(); iterator.hasNext();) {
			ISynchronizerExtender extender = (ISynchronizerExtender) iterator.next();
			extender.projectChanged(currentProjectDelta);	
		}
	}

	protected synchronized void notifyExtendersOfClose() {
		if (extenders != null && !extenders.isEmpty()) {
			for (Iterator iterator = Collections.unmodifiableSet(extenders).iterator(); iterator.hasNext();) {
				ISynchronizerExtender extender = (ISynchronizerExtender) iterator.next();
				extender.projectClosed();
			}
		}
	}

	protected IWorkspace getWorkspace() {
		if (getProject() == null)
			return ResourcesPlugin.getWorkspace();
		return getProject().getWorkspace();
	}

	protected void initialize() {
		getWorkspace().addResourceChangeListener(this,
				IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE | IResourceChangeEvent.POST_CHANGE | IResourceChangeEvent.PRE_BUILD);
	}

	/**
	 * Dispose of the synchronizer. Called when no longer needed.
	 * 
	 * 
	 * @since 1.0.0
	 */
	public void dispose() {
		getWorkspace().removeResourceChangeListener(this);
	}

	/**
	 * The project is going away so we need to cleanup ourself and the ResourceSet.
	 */
	protected void release() {
		if (JEMUtilPlugin.isActivated()) {
			try {
				if (resourceSet instanceof ProjectResourceSet)
					((ProjectResourceSet) resourceSet).release();
			} finally {
				EMFWorkbenchContextFactory.INSTANCE.removeCachedProject(getProject());
				dispose();
			}
		}
	}

	/**
	 * Add an extender to be notified of events.
	 * 
	 * @param extender
	 * 
	 * @since 1.0.0
	 */
	public synchronized void addExtender(ISynchronizerExtender extender) {
		if (extenders == null)
			extenders = new HashSet(3);
		extenders.add(extender);
	}

	/**
	 * Remove extender from notification of events.
	 * 
	 * @param extender
	 * 
	 * @since 1.0.0
	 */
	public void removeExtender(ISynchronizerExtender extender) {
		if (extenders == null)
			return;
		extenders.remove(extender);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getClass().getName() + '(' + ((getProject() != null) ? getProject().getName() : "null") + ')'; //$NON-NLS-1$
	}

	/**
	 * Tell Synchronizer that a file is about to be saved. This method should be called prior to writing to an IFile from an EMF resource.
	 * <p>
	 * Default does nothing, but subclasses can do something.
	 * </p>
	 * 
	 * @param aFile
	 *            file about to be saved.
	 * 
	 * @since 1.0.0
	 */
	public void preSave(IFile aFile) {
		//Default is do nothing
	}

}
