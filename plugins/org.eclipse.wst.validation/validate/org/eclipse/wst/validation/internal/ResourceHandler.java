/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
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
				if (Tracing.isLogging()) {
					Tracing.log("ResourceHandler-01", "Cannot find bundle " + ValidationPlugin.getBundlePropertyFileName()); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
		return _bundle;
	}

	public static String getExternalizedMessage(String key) {
		try {
			ResourceBundle bundle = getBundle();
			if (bundle == null) {
				Tracing.log("ResourceHandler-02: ", "Resource bundle is null"); //$NON-NLS-1$ //$NON-NLS-2$
				return key;
			}

			return bundle.getString(key);
		} catch (NullPointerException exc) {
			Tracing.log("ResourceHandler-03: ", "Cannot find message id ", key); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return key;
	}

	public static String getExternalizedMessage(String key, String[] parms) {
		String res = ""; //$NON-NLS-1$
		try {
			res = java.text.MessageFormat.format(getExternalizedMessage(key), (Object[])parms);
		} catch (MissingResourceException exc) {
			Tracing.log("ResourceHandler-04: ", "Cannot find message id ", key); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (NullPointerException exc) {
			if (Tracing.isLogging()) {
				Tracing.log("ResourceHandler-05: Cannot format message id " + key + " with " + parms.length + " parameters."); //$NON-NLS-1$  //$NON-NLS-2$  //$NON-NLS-3$
			}
		}
		return res;
	}
}
