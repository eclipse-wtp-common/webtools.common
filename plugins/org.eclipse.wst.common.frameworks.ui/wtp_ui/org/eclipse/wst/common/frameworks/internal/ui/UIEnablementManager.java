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
 * Created on Feb 3, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.eclipse.wst.common.frameworks.internal.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.activities.IActivityManager;
import org.eclipse.ui.activities.IWorkbenchActivitySupport;
import org.eclipse.wst.common.frameworks.internal.enablement.EnablementIdentifier;
import org.eclipse.wst.common.frameworks.internal.enablement.EnablementManager;

/**
 * @author mdelder
 */
public class UIEnablementManager extends EnablementManager {

	private IWorkbenchActivitySupport activitySupport = null;

	public UIEnablementManager() {
		super();
	}

	protected IActivityManager getActivityManager() {
		if (getActivitySupport() != null)
			return getActivitySupport().getActivityManager();
		else
			return null;
	}

	/**
	 * @return Returns the activitySupport.
	 */
	protected IWorkbenchActivitySupport getActivitySupport() {
		if (activitySupport == null)
			activitySupport = initActivitySupport();
		return activitySupport;
	}

	private IWorkbenchActivitySupport initActivitySupport() {
		IWorkbench work = null;
		try {
			work = PlatformUI.getWorkbench();
		} catch (IllegalStateException ex) {
			//Not initialized yet
		}
		return (work != null) ? work.getActivitySupport() : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.enablement.EnablementManager#createIdentifier(java.lang.String,
	 *      org.eclipse.core.resources.IProject)
	 */
	protected EnablementIdentifier createIdentifier(String identifierId, IProject project) {
		return new UIEnablementIdentifier(identifierId, project);
	}
}
