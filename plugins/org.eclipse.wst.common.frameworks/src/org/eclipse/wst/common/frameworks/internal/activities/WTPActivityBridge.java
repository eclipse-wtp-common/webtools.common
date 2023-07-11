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

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonPlugin;

/**
 * @author jsholl
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Generation - Code and Comments
 */
public class WTPActivityBridge {
	private static final String PLUGIN_ID = "org.eclipse.wst.common.frameworks"; //$NON-NLS-1$
	private static final String EXTENSION_POINT = "WTPActivityBridgeHelper"; //$NON-NLS-1$
	private static final String LISTENER_CLASS = "class"; //$NON-NLS-1$

	private static WTPActivityBridge INSTANCE = null;
	private WTPActivityBridgeHelper[] listeners;

	public static WTPActivityBridge getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new WTPActivityBridge();
		}
		return INSTANCE;
	}

	private WTPActivityBridge() {
		loadExtensionPoints();
	}

	public void enableActivity(String activityID, boolean enabled) {
		for (int i = 0; i < listeners.length; i++) {
			listeners[i].enableActivity(activityID, enabled);
		}
	}

	public Set getEnabledActivityIds() {
		if(listeners.length > 0){
			return listeners[0].getEnabledActivityIds();
		}
		return Collections.EMPTY_SET;
	}

	public void setEnabledActivityIds(Set activityIDs) {
		for (int i = 0; i < listeners.length; i++) {
			listeners[i].setEnabledActivityIds(activityIDs);
		}
	}

	public Set getActivityIDsFromContribution(String localID, String pluginID) {
		if(listeners.length > 0) {
			return listeners[0].getActivityIDsFromContribution(localID, pluginID);
		}
		return Collections.EMPTY_SET;
	}

	private void loadExtensionPoints() {
		IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(PLUGIN_ID, EXTENSION_POINT);
		if (point == null)
			return;
		IConfigurationElement[] elements = point.getConfigurationElements();
		listeners = new WTPActivityBridgeHelper[elements.length];
		for (int i = 0; i < elements.length; i++) {
			if (null == elements[i].getAttribute(LISTENER_CLASS)) {
				logError(elements[i], "No " + LISTENER_CLASS + " defined."); //$NON-NLS-1$ //$NON-NLS-2$
			}
			try {
				listeners[i] = (WTPActivityBridgeHelper) elements[i].createExecutableExtension(LISTENER_CLASS);
			} catch (CoreException e) {
				logError(elements[i], "Error loading " + LISTENER_CLASS + ":" + elements[i].getAttribute(LISTENER_CLASS)); //$NON-NLS-1$ //$NON-NLS-2$
				e.printStackTrace();
			}
		}
	}

	public static void logError(IConfigurationElement element, String text) {
		IExtension extension = element.getDeclaringExtension();
		StringBuffer buf = new StringBuffer();
		buf.append("Plugin " + extension.getNamespace() + ", extension " + extension.getExtensionPointUniqueIdentifier()); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("\n" + text); //$NON-NLS-1$
		WTPCommonPlugin.logError(buf.toString());
	}
}
