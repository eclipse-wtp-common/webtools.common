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
 * Created on May 4, 2004
 * 
 */
package org.eclipse.wst.internal.common.frameworks.ui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.ui.IPluginContribution;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.activities.IActivityManager;
import org.eclipse.ui.activities.IIdentifier;
import org.eclipse.ui.activities.IWorkbenchActivitySupport;
import org.eclipse.ui.activities.WorkbenchActivityHelper;
import org.eclipse.wst.common.framework.activities.WTPActivityBridgeHelper;


/**
 * @author jsholl
 *  
 */
public class WTPActivityBridgeHelperImpl implements WTPActivityBridgeHelper {

	private IWorkbenchActivitySupport workbenchActivitySupport = null;
	private IActivityManager activityManager = null;

	public WTPActivityBridgeHelperImpl() {
		workbenchActivitySupport = PlatformUI.getWorkbench().getActivitySupport();
		activityManager = workbenchActivitySupport.getActivityManager();
	}

	public void enableActivity(String activityID, boolean enabled) {
		Set enabledActivities = activityManager.getEnabledActivityIds();
		Set newEnabledActivities = null;
		if (enabled && !enabledActivities.contains(activityID)) {
			newEnabledActivities = new HashSet();
			newEnabledActivities.addAll(enabledActivities);
			newEnabledActivities.add(activityID);
		}
		if (!enabled && enabledActivities.contains(activityID)) {
			newEnabledActivities = new HashSet();
			newEnabledActivities.addAll(enabledActivities);
			newEnabledActivities.remove(activityID);
		}
		if (null != newEnabledActivities) {
			workbenchActivitySupport.setEnabledActivityIds(newEnabledActivities);
		}
	}

	public Set getEnabledActivityIds() {
		return activityManager.getEnabledActivityIds();
	}

	public void setEnabledActivityIds(Set activityIDs) {
		workbenchActivitySupport.setEnabledActivityIds(activityIDs);
	}

	public Set getActivityIDsFromContribution(final String localID, final String pluginID) {
		IIdentifier identifier = activityManager.getIdentifier(WorkbenchActivityHelper.createUnifiedId(new IPluginContribution() {
			public String getLocalId() {
				return localID;
			}

			public String getPluginId() {
				return pluginID;
			}
		}));
		return identifier.getActivityIds();
	}
}