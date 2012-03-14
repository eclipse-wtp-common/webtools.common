/*******************************************************************************
 * Copyright (c) 2003, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.wst.common.componentcore.internal.ComponentResource;
import org.eclipse.wst.common.componentcore.internal.ComponentcorePackage;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;

public class EclipseResourceAdapter extends AdapterImpl implements Adapter {

	public static final Class ADAPTER_TYPE = EclipseResourceAdapter.class;
	private IResource resource; 

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.notify.Adapter#notifyChanged(org.eclipse.emf.common.notify.Notification)
	 */
	public void notifyChanged(Notification aNotification) {
		if (aNotification.getFeature() == ComponentcorePackage.eINSTANCE.getComponentResource_SourcePath()) {
			if (aNotification.getEventType() == Notification.SET) {
				resource = null;
				
			}
		}
	}

	public IResource getEclipseResource() { 
		synchronized (this) {
			if (resource == null) {

				IProject container = null;
				ComponentResource moduleResource = (ComponentResource) getTarget();
				if (moduleResource != null) {
					IPath sourcePath = moduleResource.getSourcePath();
					if (moduleResource.getOwningProject() != null)
						container = moduleResource.getOwningProject();
					else
						container = StructureEdit.getContainingProject(moduleResource.getComponent());
					if (container != null)
						resource = container.findMember(sourcePath); 
					if(resource == null)
						resource = ResourcesPlugin.getWorkspace().getRoot().findMember(sourcePath);
				}
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
