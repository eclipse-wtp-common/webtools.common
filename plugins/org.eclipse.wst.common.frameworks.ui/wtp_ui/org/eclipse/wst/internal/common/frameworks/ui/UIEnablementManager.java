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
/*
 * Created on Feb 3, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.eclipse.wst.internal.common.frameworks.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.activities.IActivityManager;
import org.eclipse.ui.activities.IWorkbenchActivitySupport;
import org.eclispe.wst.common.internal.framework.enablement.EnablementIdentifier;
import org.eclispe.wst.common.internal.framework.enablement.EnablementManager;

/**
 * @author mdelder
 */
public class UIEnablementManager extends EnablementManager {

	private IWorkbenchActivitySupport activitySupport = null;

	public UIEnablementManager() {
		super();
	}

	protected IActivityManager getActivityManager() {
		return getActivitySupport().getActivityManager();
	}

	/**
	 * @return Returns the activitySupport.
	 */
	protected IWorkbenchActivitySupport getActivitySupport() {
		if (activitySupport == null)
			activitySupport = PlatformUI.getWorkbench().getActivitySupport();
		return activitySupport;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.framework.enablement.EnablementManager#createIdentifier(java.lang.String,
	 *      org.eclipse.core.resources.IProject)
	 */
	protected EnablementIdentifier createIdentifier(String identifierId, IProject project) {
		return new UIEnablementIdentifier(identifierId, project);
	}
}