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
 * Created on Feb 12, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.eclipse.wst.internal.common.frameworks.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.activities.IActivityManager;
import org.eclipse.ui.activities.IIdentifier;
import org.eclipse.ui.activities.IIdentifierListener;
import org.eclipse.ui.activities.IdentifierEvent;
import org.eclispe.wst.common.framework.enablement.EnablementIdentifierEvent;
import org.eclispe.wst.common.framework.enablement.IEnablementManager;
import org.eclispe.wst.common.internal.framework.enablement.EnablementIdentifier;


/**
 * @author schacher
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
class UIEnablementIdentifier extends EnablementIdentifier implements IIdentifierListener {

	private IIdentifier activityIdentifier;

	/**
	 * @param id
	 * @param project
	 */
	public UIEnablementIdentifier(String id, IProject project) {
		super(id, project);
		activityIdentifier = getActivityManager().getIdentifier(id);
		activityIdentifier.addIdentifierListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.activities.IIdentifierListener#identifierChanged(org.eclipse.ui.activities.IdentifierEvent)
	 */
	public void identifierChanged(IdentifierEvent identifierEvent) {
		boolean enabledChanged = resetEnabled();
		EnablementIdentifierEvent evt = new EnablementIdentifierEvent(this, false, enabledChanged);
		fireIdentifierChanged(evt);
	}

	protected IActivityManager getActivityManager() {
		return ((UIEnablementManager) IEnablementManager.INSTANCE).getActivityManager();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclispe.wst.common.internal.framework.enablement.EnablementIdentifier#getNewEnabled()
	 */
	protected boolean getNewEnabled() {
		return activityIdentifier.isEnabled() && super.getNewEnabled();
	}



}