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
 * Created on Apr 9, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.eclipse.wst.common.frameworks.internal.ui;

import org.eclipse.ui.IPluginContribution;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.activities.IIdentifier;
import org.eclipse.ui.activities.IWorkbenchActivitySupport;

/**
 * @author jsholl
 * 
 * To change the template for this generated type comment go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */
public class WTPActivityHelper {

	/**
	 * @return whether the UI is set up to filter contributions (has defined activity categories).
	 */
	public static final boolean isFiltering() {
		return !PlatformUI.getWorkbench().getActivitySupport().getActivityManager().getDefinedActivityIds().isEmpty();
	}

	public static boolean allowUseOf(Object object) {
		if (!isFiltering())
			return true;
		if (object instanceof IPluginContribution) {
			IPluginContribution contribution = (IPluginContribution) object;
			if (contribution.getPluginId() != null) {
				IWorkbenchActivitySupport workbenchActivitySupport = PlatformUI.getWorkbench().getActivitySupport();
				IIdentifier identifier = workbenchActivitySupport.getActivityManager().getIdentifier(createUnifiedId(contribution));
				return identifier.isEnabled();
			}
		}
		return true;
	}


	public static final String createUnifiedId(IPluginContribution contribution) {
		if (contribution.getPluginId() != null)
			return contribution.getPluginId() + '/' + contribution.getLocalId();
		return contribution.getLocalId();
	}

}