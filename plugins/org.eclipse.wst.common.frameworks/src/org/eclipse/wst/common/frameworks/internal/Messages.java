/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Nov 21, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.eclipse.wst.common.frameworks.internal;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author DABERG
 * 
 * To change the template for this generated type comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public abstract class Messages {
	//Resource bundle.
	protected ResourceBundle resourceBundle;

	/**
	 * The constructor.
	 */
	public Messages() {
		initializeBundle();
	}

	protected abstract void initializeBundle();

	/**
	 * Returns the string from the resource bundle, or 'key' if not found.
	 */
	protected String doGetResourceString(String key) {
		try {
			return (resourceBundle != null ? resourceBundle.getString(key) : key);
		} catch (MissingResourceException e) {
			return "!" + key + "!"; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	protected String doGetResourceString(String key, Object[] args) {
		String pattern = doGetResourceString(key);
		if (pattern != null)
			return MessageFormat.format(pattern, args);
		return null;
	}
}