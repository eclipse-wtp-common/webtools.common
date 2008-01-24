/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;


/**
 * This class retrieves the Strings from the .properties file appropriate for the machine's Locale.
 */
public class ResourceHandler {
	//TODO Make this class final once the public ResourceHandler has been deleted.
	private static ResourceBundle _bundle = null;

	protected ResourceHandler() {
		//TODO Make this method private once the public ResourceHandler has been deleted.
		super();
	}

	/**
	 * Return the resource bundle which contains the messages, as identified by
	 */
	public static ResourceBundle getBundle() {
		if (_bundle == null) {
			try {
				_bundle = ResourceBundle.getBundle(ValidationPlugin.getBundlePropertyFileName());
			} catch (MissingResourceException exc) {
				_bundle = null;
				if (Misc.isLogging()) {
					Misc.log("Cannot find bundle " + ValidationPlugin.getBundlePropertyFileName()); //$NON-NLS-1$
				}
			}
		}
		return _bundle;
	}

	public static String getExternalizedMessage(String key) {
		try {
			ResourceBundle bundle = getBundle();
			if (bundle == null) {
				if (Misc.isLogging()) {
					Misc.log("Resource bundle is null"); //$NON-NLS-1$
				}
				return key;
			}

			return bundle.getString(key);
		} catch (NullPointerException exc) {
			if (Misc.isLogging()) {
				Misc.log("Cannot find message id " + key); //$NON-NLS-1$
			}
		}
		return key;
	}

	public static String getExternalizedMessage(String key, String[] parms) {
		String res = ""; //$NON-NLS-1$
		try {
			res = java.text.MessageFormat.format(getExternalizedMessage(key), (Object[])parms);
		} catch (MissingResourceException exc) {
			if (Misc.isLogging()) {
				Misc.log("Cannot find message id " + key); //$NON-NLS-1$
			}
		} catch (NullPointerException exc) {
			if (Misc.isLogging()) {
				Misc.log("Cannot format message id " + key + " with " + parms.length + " parameters."); //$NON-NLS-1$  //$NON-NLS-2$  //$NON-NLS-3$
			}
		}
		return res;
	}
}
