package org.eclipse.wst.common.framework;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/*
 * Licensed Material - Property of IBM 
 * (C) Copyright IBM Corp. 2001, 2002 - All Rights Reserved. 
 * US Government Users Restricted Rights - Use, duplication or disclosure 
 * restricted by GSA ADP Schedule Contract with IBM Corp. 
 */

/**
 * @author Administrator
 * 
 *  
 */
public class WTPResourceHandler {

	private static ResourceBundle fgResourceBundle;

	/**
	 * Returns the resource bundle used by all classes in this Project
	 */
	public static ResourceBundle getResourceBundle() {
		try {
			return ResourceBundle.getBundle("wtp_common"); //$NON-NLS-1$
		} catch (MissingResourceException e) {
			// does nothing - this method will return null and
			// getString(String) will return the key
			// it was called with
		}
		return null;
	}

	public static String getString(String key) {
		if (fgResourceBundle == null) {
			fgResourceBundle = getResourceBundle();
		}

		if (fgResourceBundle != null) {
			try {
				return fgResourceBundle.getString(key);
			} catch (MissingResourceException e) {
				return "!" + key + "!"; //$NON-NLS-2$//$NON-NLS-1$
			}
		}
		return "!" + key + "!"; //$NON-NLS-2$//$NON-NLS-1$
	}

	public static String getString(String key, Object[] args) {

		try {
			return MessageFormat.format(getString(key), args);
		} catch (IllegalArgumentException e) {
			return getString(key);
		}

	}

	public static String getString(String key, Object[] args, int x) {

		return getString(key);
	}

}