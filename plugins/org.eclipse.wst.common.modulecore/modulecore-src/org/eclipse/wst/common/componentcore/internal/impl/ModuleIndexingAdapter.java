/*******************************************************************************
 * Copyright (c) 2003, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.common.componentcore.internal.ComponentcorePackage;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;

public class ModuleIndexingAdapter extends AdapterImpl {

	private static final Class MODULE_INDEXING_ADAPTER_CLASS = ModuleIndexingAdapter.class;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
	 */
	public void notifyChanged(Notification aNotification) {
		if (aNotification.getNotifier() instanceof EObject) {
			EObject notifier = (EObject) aNotification.getNotifier();
			if (notifier.eClass().getClassifierID() == ComponentcorePackage.PROJECT_COMPONENTS) {
				ProjectComponentsImpl projectModules = (ProjectComponentsImpl) notifier;
				synchronized (projectModules.getModulesIndex()) {
					switch (aNotification.getEventType()) {
						case Notification.ADD :
							WorkbenchComponent module = (WorkbenchComponent) aNotification.getNewValue();
							projectModules.getModulesIndex().put(module.getName(), module);
					}
				} 
			}
		} 
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.notify.impl.AdapterImpl#isAdapterForType(java.lang.Object)
	 */
	public boolean isAdapterForType(Object type) {
		return type == MODULE_INDEXING_ADAPTER_CLASS;
	}
}
