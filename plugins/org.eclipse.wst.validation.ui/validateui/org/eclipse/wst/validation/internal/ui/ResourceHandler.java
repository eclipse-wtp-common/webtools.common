/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;

import org.eclipse.jem.util.logger.LogEntry;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.frameworks.internal.ui.WTPUIPlugin;
import org.eclipse.wst.validation.internal.ui.plugin.ValidationUIPlugin;


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
				_bundle = ResourceBundle.getBundle(ValidationUIPlugin.getBundleName());
			} catch (MissingResourceException exc) {
				_bundle = null;
				Logger logger = WTPUIPlugin.getLogger();
				if (logger.isLoggingLevel(Level.FINE)) {
					LogEntry entry = ValidationUIPlugin.getLogEntry();
					entry.setSourceID("org.eclipse.wst.validation.internal.operations.ui.ResourceHandler.getBundle()"); //$NON-NLS-1$
					entry.setText("Cannot find bundle " + ValidationUIPlugin.getBundleName()); //$NON-NLS-1$
					entry.setTargetException(exc);
					logger.write(Level.FINE, entry);
				}
			}
		}
		return _bundle;
	}

	public static String getExternalizedMessage(String key) {
		try {
			ResourceBundle bundle = getBundle();
			if (bundle == null) {
				Logger logger = WTPUIPlugin.getLogger();
				if (logger.isLoggingLevel(Level.FINE)) {
					LogEntry entry = ValidationUIPlugin.getLogEntry();
					entry.setSourceID("org.eclipse.wst.validation.internal.operations.ui.ResourceHandler.getExternalizedMessage(String)"); //$NON-NLS-1$
					entry.setText("Resource bundle is null"); //$NON-NLS-1$
					logger.write(Level.FINE, entry);
				}
				return key;
			}

			return bundle.getString(key);
		} catch (NullPointerException exc) {
			Logger logger = WTPUIPlugin.getLogger();
			if (logger.isLoggingLevel(Level.FINE)) {
				LogEntry entry = ValidationUIPlugin.getLogEntry();
				entry.setSourceID("org.eclipse.wst.validation.internal.operations.ui.ResourceHandler.getExternalizedMessage(String)"); //$NON-NLS-1$
				entry.setText("Cannot find message id " + key); //$NON-NLS-1$
				entry.setTargetException(exc);
				logger.write(Level.FINE, entry);
			}
		}
		return key;
	}

	public static String getExternalizedMessage(String key, String[] parms) {
		String res = ""; //$NON-NLS-1$
		try {
			res = java.text.MessageFormat.format(getExternalizedMessage(key), parms);
		} catch (MissingResourceException exc) {
			Logger logger = WTPUIPlugin.getLogger();
			if (logger.isLoggingLevel(Level.FINE)) {
				LogEntry entry = ValidationUIPlugin.getLogEntry();
				entry.setSourceID("org.eclipse.wst.validation.internal.operations.ui.ResourceHandler.getExternalizedMessage(String, String[])"); //$NON-NLS-1$
				entry.setText("Cannot find message id " + key); //$NON-NLS-1$
				entry.setTargetException(exc);
				logger.write(Level.FINE, entry);
			}
		} catch (NullPointerException exc) {
			Logger logger = WTPUIPlugin.getLogger();
			if (logger.isLoggingLevel(Level.FINE)) {
				LogEntry entry = ValidationUIPlugin.getLogEntry();
				entry.setSourceID("org.eclipse.wst.validation.internal.operations.ui.ResourceHandler.getExternalizedMessage(String, String[])"); //$NON-NLS-1$
				entry.setText("Cannot format message id " + key + " with " + parms.length + " parameters."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				entry.setTargetException(exc);
				logger.write(Level.FINE, entry);
			}
		}
		return res;
	}
}