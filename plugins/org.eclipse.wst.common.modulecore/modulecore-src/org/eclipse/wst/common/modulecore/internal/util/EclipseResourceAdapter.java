/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.modulecore.internal.util;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.wst.common.modulecore.ModuleCorePackage;
import org.eclipse.wst.common.modulecore.ComponentResource;

/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public class EclipseResourceAdapter extends AdapterImpl implements Adapter {

	public static final Class ADAPTER_TYPE = EclipseResourceAdapter.class;
	private IResource resource;
	private boolean hasSearchFailed = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.notify.Adapter#notifyChanged(org.eclipse.emf.common.notify.Notification)
	 */
	public void notifyChanged(Notification aNotification) {
		if (aNotification.getFeature() == ModuleCorePackage.eINSTANCE.getComponentResource_SourcePath()) {
			if (aNotification.getEventType() == Notification.SET) {
				resource = null;
				hasSearchFailed = false;
			}
		}
	}

	public IResource getEclipseResource() {
		if (resource != null || hasSearchFailed)
			return resource;
		synchronized (this) {
			if (resource == null) {
				ComponentResource moduleResource = (ComponentResource) getTarget();
				IPath workspacePath = new Path(moduleResource.getSourcePath().path())/*.removeFirstSegments(1)*/; // we already have a workspace-relative path
				resource = ResourcesPlugin.getWorkspace().getRoot().findMember(workspacePath);
				hasSearchFailed = resource == null;
			}
		}
		return resource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.notify.Adapter#isAdapterForType(java.lang.Object)
	 */
	public boolean isAdapterForType(Object aType) {
		return ADAPTER_TYPE == aType;
	}
}
