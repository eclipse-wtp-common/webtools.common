/*******************************************************************************
 * Copyright (c) 2003, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on May 4, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.eclipse.wst.common.frameworks.internal.activities;

import java.util.Set;

/**
 * @author jsholl
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Generation - Code and Comments
 */
public interface WTPActivityBridgeHelper {

	public void enableActivity(String activityID, boolean enabled);

	public Set getEnabledActivityIds();

	public void setEnabledActivityIds(Set activityIDs);

	public Set getActivityIDsFromContribution(String localID, String pluginID);
}