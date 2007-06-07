/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.ui;

import org.eclipse.wst.common.frameworks.internal.ui.WTPUIPlugin;

public interface ContextIds {
	static final String PREFIX = WTPUIPlugin.PLUGIN_ID;

	// context ids for the Validation Properties Page
	public static final String VALIDATION_PROPERTIES_PAGE = PREFIX + ".jvpp0000"; //$NON-NLS-1$
	public static final String VALIDATION_PROPERTIES_PAGE_DISABLED_OVERRIDE = PREFIX + ".jvpp0001"; //$NON-NLS-1$
	public static final String VALIDATION_PROPERTIES_PAGE_DISABLED_BUILD_NOVALSELECTED = PREFIX + ".jvpp0003"; //$NON-NLS-1$
	public static final String VALIDATION_PROPERTIES_PAGE_DISABLED_AUTO_AUTOBUILD = PREFIX + ".jvpp0004"; //$NON-NLS-1$
	public static final String VALIDATION_PROPERTIES_PAGE_DISABLED_AUTO_NOINCVALSELECTED = PREFIX + ".jvpp0005"; //$NON-NLS-1$
	public static final String VALIDATION_PROPERTIES_PAGE_DISABLED_AUTO_NOINCVALCONFIG = PREFIX + ".jvpp0006"; //$NON-NLS-1$
	public static final String VALIDATION_PROPERTIES_PAGE_AUTO_ENABLED = PREFIX + ".jvpp0020"; //$NON-NLS-1$
	public static final String VALIDATION_PROPERTIES_PAGE_OVERRIDE_ENABLED = PREFIX + ".jvpp0030"; //$NON-NLS-1$
	public static final String VALIDATION_PROPERTIES_PAGE_OVERRIDE_ENABLED_CANNOT_HONOUR_BOTH = PREFIX + ".jvpp0031"; //$NON-NLS-1$
	public static final String VALIDATION_PROPERTIES_PAGE_OVERRIDE_ENABLED_CANNOT_HONOUR_AUTO = PREFIX + ".jvpp0032"; //$NON-NLS-1$
	public static final String VALIDATION_PROPERTIES_PAGE_OVERRIDE_ENABLED_CANNOT_HONOUR_MANUAL = PREFIX + ".jvpp0033"; //$NON-NLS-1$
	public static final String VALIDATION_PROPERTIES_PAGE_REBUILD_ENABLED = PREFIX + ".jvpp0040"; //$NON-NLS-1$
	public static final String VALIDATION_PROPERTIES_PAGE_MAX_MESSAGES = PREFIX + ".jvpp0050"; //$NON-NLS-1$

	public static final String VALIDATION_PREFERENCE_PAGE = PREFIX + ".jvgp0000"; //$NON-NLS-1$
	public static final String VALIDATION_PREFERENCE_PAGE_OVERRIDE = PREFIX + ".jvgp0005"; //$NON-NLS-1$
	public static final String VALIDATION_PREFERENCE_PAGE_DISABLE_ALL_ENABLED = PREFIX + ".jvgp0010"; //$NON-NLS-1$
	//	public static final String VALIDATION_PREFERENCE_PAGE_REBUILD_DISABLED = PREFIX +
	// ".jvgp0011"; //$NON-NLS-1$
	public static final String VALIDATION_PREFERENCE_PAGE_AUTO_ENABLED = PREFIX + ".jvgp0020"; //$NON-NLS-1$
	//	public static final String VALIDATION_PREFERENCE_PAGE_AUTO_DISABLED_AUTOBUILD = PREFIX +
	// ".jvgp0021"; //$NON-NLS-1$
	//	public static final String VALIDATION_PREFERENCE_PAGE_AUTO_DISABLED_NOINCVALSELECTED = PREFIX
	// + ".jvgp0022"; //$NON-NLS-1$
	//	public static final String VALIDATION_PREFERENCE_PAGE_AUTO_DISABLED_NOINCVALCONFIG = PREFIX +
	// ".jvgp0023"; //$NON-NLS-1$
	public static final String VALIDATION_PREFERENCE_PAGE_MAX_MESSAGES = PREFIX + ".jvgp0030"; //$NON-NLS-1$
}
