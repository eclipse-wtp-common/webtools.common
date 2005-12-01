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
package org.eclipse.wst.common.componentcore.internal.impl;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.internal.util.DeployedPathProvider;
import org.eclipse.wst.common.componentcore.internal.util.IPathProvider;
import org.eclipse.wst.common.componentcore.internal.util.SourcePathProvider;

/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public class ResourceTreeRootAdapter extends AdapterImpl implements Adapter {
	
	public static final Object SOURCE_ADAPTER_TYPE = new Object();
	public static final Object DEPLOY_ADAPTER_TYPE = new Object();
	public static final int SOURCE_TREE = 0;
	public static final int DEPLOY_TREE = 1;
	
	private ResourceTreeRoot resourceTreeRoot;
	private int type;
	
	public ResourceTreeRootAdapter(int aType) {
		type = aType;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
	 */
	public void notifyChanged(Notification msg) {
		super.notifyChanged(msg);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.emf.common.notify.impl.AdapterImpl#isAdapterForType(java.lang.Object)
	 */
	public boolean isAdapterForType(Object anAdapterType) {
		return (SOURCE_ADAPTER_TYPE == anAdapterType && type == SOURCE_TREE) || (DEPLOY_ADAPTER_TYPE == anAdapterType && type == DEPLOY_TREE);
	}
	
	public ResourceTreeRoot getResourceTreeRoot() {		
		if(resourceTreeRoot != null)
			return resourceTreeRoot;
		synchronized(this) {
			if(resourceTreeRoot == null) {
				IPathProvider pathProvider= (type == DEPLOY_TREE) ? DeployedPathProvider.INSTANCE : SourcePathProvider.INSTANCE;
				resourceTreeRoot = new ResourceTreeRoot((WorkbenchComponent)getTarget(), pathProvider);
			}
		}
		return resourceTreeRoot;
	}

}
