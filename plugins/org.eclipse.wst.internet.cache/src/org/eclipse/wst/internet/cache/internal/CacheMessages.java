/**
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *   IBM - Initial API and implementation
 */
package org.eclipse.wst.internet.cache.internal;

import org.eclipse.osgi.util.NLS;

/**
 * Strings used by the cache.
 */
public class CacheMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.wst.internet.cache.internal.CachePluginResources";//$NON-NLS-1$

	public static String _UI_CONFIRM_CLEAR_CACHE_DIALOG_TITLE;
	public static String _UI_CONFIRM_CLEAR_CACHE_DIALOG_MESSAGE;
	public static String _UI_CONFIRM_DELETE_CACHE_ENTRY_DIALOG_TITLE;
	public static String _UI_CONFIRM_DELETE_CACHE_ENTRY_DIALOG_MESSAGE;
	public static String _UI_BUTTON_CLEAR_CACHE;
	public static String _UI_BUTTON_DELETE_ENTRY;
	public static String _UI_PREF_CACHE_ENTRIES_TITLE;
	public static String _UI_PREF_CACHE_CACHE_OPTION;
	public static String _UI_PREF_CACHE_IGNORE_NO_CACHE_HEADER;
	public static String _UI_PREF_CACHE_CACHE_DURATION_LABEL;
	public static String _UI_PREF_CACHE_ABOUT;
	public static String _UI_PREF_PROMPT_FOR_DISAGREED_LICENSES;
	public static String _UI_CACHE_MONITOR_NAME;
	public static String _UI_CACHE_MONITOR_CACHING;

	// Cache license dialog
	public static String _UI_CACHE_DIALOG_LICENSE_STATEMENT1;
	public static String _UI_CACHE_DIALOG_LICENSE_STATEMENT2;
	public static String _UI_CACHE_DIALOG_LICENSE_STATEMENT2_NO_INTERNAL;
	public static String _UI_CACHE_DIALOG_LICENSE_STATEMENT2_NO_BROWSER;
	public static String _UI_CACHE_DIALOG_AGREE_BUTTON;
	public static String _UI_CACHE_DIALOG_DISAGREE_BUTTON;
	public static String _UI_CACHE_DIALOG_TITLE;
	public static String _UI_LOADING_LICENSE;

	// Cache logging messages
	public static String _LOG_INFO_WTP_NO_USER_INTERACTION;

	// WTP test no user interaction system property
	public static String WTP_NO_USER_INTERACTION_SYSTEM_PROP;   

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, CacheMessages.class);
	}

	private CacheMessages() {
		// cannot create new instance
	}
}
