/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.snippets.internal;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.common.snippets.internal.model.SnippetManager;


/**
 * The main plugin class to be used in the desktop.
 */
public class SnippetsPlugin extends AbstractUIPlugin {
	public static interface NAMES {
		String CATEGORY = "category"; //$NON-NLS-1$
		String CLASSNAME = "class"; //$NON-NLS-1$
		String CONTENT = "content"; //$NON-NLS-1$
		String DEFAULT = "default"; //$NON-NLS-1$
		String DESCRIPTION = "description"; //$NON-NLS-1$
		String EDITORCLASSNAME = "editorclass"; //$NON-NLS-1$
		String EXTENSION_POINT_ID = "SnippetContributions"; //$NON-NLS-1$
		String HIDE = "hide"; //$NON-NLS-1$
		String ICON = "icon"; //$NON-NLS-1$
		String ID = "id"; //$NON-NLS-1$
		String INITIAL_STATE = "initial_state"; //$NON-NLS-1$
		String ITEM = "item"; //$NON-NLS-1$
		String LABEL = "label"; //$NON-NLS-1$
		String LARGEICON = "largeicon"; //$NON-NLS-1$
		String NAME = "name"; //$NON-NLS-1$
		String PLUGIN = "plugin"; //$NON-NLS-1$
		String SHARED = "shared"; //$NON-NLS-1$
		String SHOW = "show"; //$NON-NLS-1$

		String SNIPPETS = "snippets"; //$NON-NLS-1$
		String VARIABLE = "variable"; //$NON-NLS-1$
		String VARIABLES = "variables"; //$NON-NLS-1$
		String VERSION = "version"; //$NON-NLS-1$
		String VIEW_ID = "org.eclipse.wst.common.snippets.internal.ui.SnippetsView"; //$NON-NLS-1$
	}
	
	public static final String BUNDLE_ID = "org.eclipse.wst.common.snippets";  //$NON-NLS-1$

	/**
	 * The shared instance
	 */
	private static SnippetsPlugin fInstance;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	private static final String KEY_PREFIX = "%"; //$NON-NLS-1$
	private static final String KEY_DOUBLE_PREFIX = "%%"; //$NON-NLS-1$	

	/**
	 * Returns the shared instance.
	 */
	public static SnippetsPlugin getDefault() {
		return fInstance;
	}

	/**
	 * @return the ISnippetManager exposing the Snippets view model
	 */
	public static ISnippetManager getSnippetManager() {
		return SnippetManager.getInstance();
	}

	/**
	 * 
	 */
	public SnippetsPlugin() {
		super();
		fInstance = this;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String value) {
		String s = value.trim();
		if (!s.startsWith(KEY_PREFIX, 0))
			return s;
		if (s.startsWith(KEY_DOUBLE_PREFIX, 0))
			return s.substring(1);

		int ix = s.indexOf(' ');
		String key = ix == -1 ? s : s.substring(0, ix);

		ResourceBundle bundle = getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key.substring(1)) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	public static String getResourceString(String key, Object[] args) {

		try {
			return MessageFormat.format(getResourceString(key), args);
		} catch (IllegalArgumentException e) {
			return getResourceString(key);
		}

	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		try {
			if (resourceBundle == null)
				resourceBundle = ResourceBundle.getBundle("org.eclipse.wst.common.snippets.internal.SnippetsPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}
}
