/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emf.utilities;


import org.eclipse.osgi.util.NLS;

public class WFTUtilsResourceHandler extends NLS {
	private static final String BUNDLE_NAME = "wftutils";//$NON-NLS-1$

	private WFTUtilsResourceHandler() {
		// Do not instantiate
	}

	public static String DANGLING_HREF_ERROR_;
	public static String Integer_UI_;
	public static String Failed_to_convert__0__to___ERROR_;
	public static String Enumeration_UI_;
	public static String Short_UI_;
	public static String Character_UI_;
	public static String Long_UI_;
	public static String Double_UI_;
	public static String ResourceDependencyRegister_ERROR_0;
	public static String Float_UI_;
	public static String Byte_UI_;
	public static String Warning__Could_not_write_b_WARN_;
	public static String Boolean_UI_;
	public static String Stack_trace_of_nested_exce_ERROR_;
	public static String MofObject_UI_;
	public static String PleaseMigrateYourCodeError_ERROR_0;
	public static String EMF2DOMAdapterImpl_ERROR_0;

	static {
		NLS.initializeMessages(BUNDLE_NAME, WFTUtilsResourceHandler.class);
	}

	public static String getString(String key, Object[] args) {
		return NLS.bind(key, args);
	}
}
