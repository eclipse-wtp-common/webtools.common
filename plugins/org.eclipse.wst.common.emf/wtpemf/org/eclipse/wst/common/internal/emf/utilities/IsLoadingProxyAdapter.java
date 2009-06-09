/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Apr 1, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.eclipse.wst.common.internal.emf.utilities;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * @author DABERG
 * 
 * To change the template for this generated type comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public class IsLoadingProxyAdapter extends AdapterImpl {
	private Resource resource;
	private Adapter targetAdapter;
	private EObject targetObject;

	public IsLoadingProxyAdapter(Adapter aTargetAdapter, EObject aTargetObject) {
		targetAdapter = aTargetAdapter;
		targetObject = aTargetObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
	 */
	@Override
	public void notifyChanged(Notification msg) {
		if (resource != null) {
			//listen for the remove of the loading adapter
			if (msg.getFeatureID(null) == Resource.RESOURCE__IS_LOADED && msg.getEventType() == Notification.SET) {
				removeProxySupport();
				reset();
			}
		} else if (cacheResource()) {
			targetAdapter.notifyChanged(msg);
			reset();
		}
	}

	/**
	 * Cache the resource variable and return true if we removed the proxy support.
	 */
	private boolean cacheResource() {
		if (getTarget() != null) {
			EObject eObj = (EObject) getTarget();
			resource = eObj.eResource();
			if (resource != null) {
				eObj.eAdapters().remove(this);
				if (ExtendedEcoreUtil.isLoading(resource))
					resource.eAdapters().add(this);
				else {
					targetObject.eAdapters().add(targetAdapter);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 *  
	 */
	private void removeProxySupport() {
		getTarget().eAdapters().remove(this);
		targetObject.eAdapters().add(targetAdapter);
	}

	private void reset() {
		resource = null;
		targetAdapter = null;
		targetObject = null;
	}

}
