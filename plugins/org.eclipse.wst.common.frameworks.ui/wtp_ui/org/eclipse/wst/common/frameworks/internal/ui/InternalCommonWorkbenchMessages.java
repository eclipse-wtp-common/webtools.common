/**********************************************************************
 * Copyright (c) 2006 IBM Corporation and others. All rights reserved.   This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.common.frameworks.internal.ui;

import org.eclipse.osgi.util.NLS;

public class InternalCommonWorkbenchMessages extends NLS {
	private static final String BUNDLE_NAME = "messages";//$NON-NLS-1$


	public static String WizardNewProjectCreationPage_projectContentsLabel;
	public static String WizardNewProjectCreationPage_useDefaultLabel;
	public static String WizardNewProjectCreationPage_locationLabel;



	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, InternalCommonWorkbenchMessages.class);
	}

}