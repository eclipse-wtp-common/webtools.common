/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 *  $$RCSfile: ResourceSetWorkbenchSynchronizer.java,v $$
 *  $$Revision: 1.9 $$  $$Date: 2010/05/12 22:47:45 $$ 
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
	protected QueuingHashSet <ISynchronizerExtender> extenders;

	/**
	 * This HashSet is similar to a regular HashSet except it can be put in
	 * queuing mode with a call to the {@link #startQueuing()} method.
	 * While in queuing mode any adds or removes will not be committed to 
	 * the set until a call to {@link #stopQueuing()} is made. This allows
	 * the QueuingHasSet to be put in queuing mode prior to iterating over
	 * the contents without needing to worry about changes coming in and
	 * throwing ConcurrentModificationExceptions.
	 * 
	 * @author jsholl
	 *
	 * @param <E>
	 */
	protected class QueuingHashSet <E> extends HashSet <E> {

		private static final long serialVersionUID = 6959354060950816784L;
	
		private Object lock = new Object();
		
		private boolean queuing = false;
		private Set <E> addQueue = null;
		private Set removeQueue = null;
		private int initialCapacity = 3;
		
		public QueuingHashSet(int capacity) {
			super(capacity);
			addQueue = new HashSet<E>(capacity);
			removeQueue = new HashSet(capacity);
			initialCapacity = capacity;
		}

		public void startQueuing() {
			synchronized(lock){
				if(queuing){
					throw new UnsupportedOperationException("startQueuing may only be called while not already queuing");
				}
				this.queuing = true;
				addQueue.clear();
				removeQueue.clear();
			}
		}

		/**
		 * Returns the set of adds which occurred while in queuing mode.
		 * @return
		 */
		public Set <E> stopQueuing() {
			synchronized(lock){
				if(!queuing){
					throw new UnsupportedOperationException("stopQueuing may only be called while queuing");
				}
				queuing = false;
				removeAll(removeQueue);
				addAll(addQueue);
				if(!addQueue.isEmpty()){
					Set <E> queue = addQueue;
					addQueue = new HashSet<E>(initialCapacity);
					return queue;
				}
				return Collections.emptySet();
			}
		}
		
		@Override
		public boolean add(E object) {
			synchronized (lock) {
				if(queuing){
					if(contains(object)){
						return false;
					}
					return addQueue.add(object);
				} else{
					return super.add(object);
				}
			}
		}
		
		@Override
		public boolean remove(Object object) {
			synchronized (lock) {
				if(queuing){
					if(contains(object)){
						return removeQueue.add(object);
					} else {
						return false;
					}
				} else {
					return super.remove(object);
				}
			}
		}
		
		@Override
		public boolean isEmpty() {
			synchronized (lock) {
				return super.isEmpty();	
			}
		}
		
	};
	

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


	protected void notifyExtendersIfNecessary() {
		if (currentEventType != IResourceChangeEvent.POST_CHANGE || currentProjectDelta == null)
			return;
		if(extenders != null){
			Set <ISynchronizerExtender> extendersToNotify = extenders;
			while(!extendersToNotify.isEmpty()){
				try{
					extenders.startQueuing();
					for (Iterator <ISynchronizerExtender> iterator = extendersToNotify.iterator(); iterator.hasNext();) {
						ISynchronizerExtender extender = iterator.next();
						extender.projectChanged(currentProjectDelta);
					}
				} finally {
					extendersToNotify = extenders.stopQueuing();
				}
			}
		}
	}

	protected void notifyExtendersOfClose() {
		if(extenders != null){
			Set <ISynchronizerExtender> extendersToNotify = extenders;
			while(!extendersToNotify.isEmpty()){
				try{
					extenders.startQueuing();
					for (Iterator <ISynchronizerExtender> iterator = extendersToNotify.iterator(); iterator.hasNext();) {
						ISynchronizerExtender extender = iterator.next();
						extender.projectClosed();
					}
				} finally {
					extendersToNotify = extenders.stopQueuing();
				}
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
	public void addExtender(ISynchronizerExtender extender) {
		if (extenders == null){
			extenders = new QueuingHashSet <ISynchronizerExtender>(3);
		}
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
