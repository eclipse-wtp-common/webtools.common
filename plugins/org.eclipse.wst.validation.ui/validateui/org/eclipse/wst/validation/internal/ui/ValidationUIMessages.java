/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 * /
 *******************************************************************************/
package org.eclipse.wst.validation.internal.ui;

import org.eclipse.osgi.util.NLS;

/**
 * Strings used by Validation UI.
 */
public class ValidationUIMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.wst.validation.internal.ui.validationui";//$NON-NLS-1$

	public static String SaveFilesDialog_saving;
	public static String SaveFilesDialog_always_save;
	public static String SaveFilesDialog_save_all_resources;
	public static String SaveFilesDialog_must_save;
	public static String PrefPage_always_save;
	public static String RunValidationDialogTitle;

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, ValidationUIMessages.class);
	}

	private ValidationUIMessages() {
		// cannot create new instance
	}
}
