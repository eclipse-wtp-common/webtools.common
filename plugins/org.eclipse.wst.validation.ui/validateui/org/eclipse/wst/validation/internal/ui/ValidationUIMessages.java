/*******************************************************************************
 * Copyright (c) 2006, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
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
	public static String UnableToSave;
	
	public static String Validate;
	
	public static String ValResults;

	// results validating one resource
	public static String ValError1Resource1;
	public static String ValErrorsResource1;
	public static String ValWarn1Resource1;
	public static String ValWarnResource1;
	public static String ValInfo1Resource1;
	public static String ValInfoResource1;

	// results validating multiple resources
	public static String ValError1Resources;
	public static String ValErrorsResources;
	public static String ValWarn1Resources;
	public static String ValWarnResources;
	public static String ValInfo1Resources;
	public static String ValInfoResources;

	public static String ValSuccess;
	

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, ValidationUIMessages.class);
	}

	private ValidationUIMessages() {
		// cannot create new instance
	}
}
