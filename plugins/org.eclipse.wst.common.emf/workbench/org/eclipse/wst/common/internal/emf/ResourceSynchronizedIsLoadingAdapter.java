/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Oct 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.wst.common.internal.emf;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.wst.common.internal.emf.utilities.ResourceIsLoadingAdapter;



/**
 * The ResourceSynchronizedIsLoadingAdapter is used to synchronize the loading
 * of EMF resources. This is the Eclipse version of ResourceIsLoadingAdapter,
 * and uses the Eclipse ILock technology to acquire a semaphore until the
 * Resource is loaded. the waitForResourceToLoad() method will pause until
 * either (a) the Resource has loaded, the Adapter is notified, and the
 * semaphore is released or (b) the DELAY timeout is exceeded, which prevents
 * full deadlock.
 * 
 * @author mdelder
 */
public class ResourceSynchronizedIsLoadingAdapter extends ResourceIsLoadingAdapter {

	private final ILock loadingLock;

	/**
	 * The delay is default to 5 minutes. This is the upward threshhold. The
	 * lock will wait up to DELAY milliseconds to acquire the lock, and bail
	 * if not. It does not mean that it will wait DELAY milliseconds always.
	 * In general, the wait should be almost instanteous -- just as long as
	 * document loading remains speedy.
	 */
	private static final long DELAY = 300000;

	public ResourceSynchronizedIsLoadingAdapter() {
		loadingLock = Platform.getJobManager().newLock();
		if (loadingLock != null)
			loadingLock.acquire();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.internal.emf.utilities.ResourceIsLoadingAdapter#waitForResourceToLoad()
	 */
	public void waitForResourceToLoad() {

		if (loadingLock == null)
			return;

		boolean lockAcquired = false;
		try {
			if (loadingLock != null)
				if (!(lockAcquired = loadingLock.acquire(DELAY)))
					logWarning();
		}
		catch (InterruptedException e) {
			// ignore, just continue
		}
		finally {
			if (lockAcquired)
				loadingLock.release();
		}

	}


	/**
	 * 
	 */
	private void logWarning() {
		Notifier target = getTarget();
		if (target == null || !(target instanceof Resource)) {
			Resource resource = (Resource) target;
			System.err.println("[WARNING] Could not acquire Semaphore Lock for Resource: \"" + resource.getURI() + "\" in " + getClass());
		}

	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.internal.emf.utilities.ResourceIsLoadingAdapter#notifyChanged(org.eclipse.emf.common.notify.Notification)
	 */
	public void notifyChanged(Notification notification) {

		if (notification.getNotifier() != null) {
			// listen for the remove of the loading adapter
			if (isSetLoadedResourceNotification(notification)) {
				if (loadingLock != null)
					loadingLock.release();
				removeIsLoadingSupport();
			}
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.internal.emf.utilities.ResourceIsLoadingAdapter#forceRelease()
	 */
	public void forceRelease() {
		if (loadingLock != null && loadingLock.getDepth() > 0)
			loadingLock.release();
	}

}
