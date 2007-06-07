/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on May 23, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.wst.common.frameworks.internal.ui;

import org.eclipse.osgi.util.NLS;

/**
 * @author vijayb
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Style - Code Templates
 */
public class WTPCommonUIResourceHandler extends NLS {
	private static final String BUNDLE_NAME = "wtpcommonui";//$NON-NLS-1$

	private WTPCommonUIResourceHandler() {
		// Do not instantiate
	}

	public static String Project_location_;
	public static String WTPOperationAction_UI_1;
	public static String MasterDescriptor_UI_1;
	public static String WTPOptionalOperationDataModel_UI_1;
	public static String WTPOperationAction_UI_0;
	public static String WTPOptionalOperationDataModel_UI_0;
	public static String MULTIPLE_MODULE_PREF_TEXT;
	public static String IActionWTPOperation_UI_0;
	public static String MasterDescriptor_ERROR_2;
	public static String Name_;
	public static String WTPWizard_UI_0;
	public static String WTPWizard_UI_1;
	public static String WTPWizard_UI_2;
	public static String Browse_;
	public static String ExtendableWizard_UI_0;
	public static String ExtendedWizardPage_ERROR_1;
	public static String ExtendedWizardPage_ERROR_0;
	public static String WTPActionDialog_UI_0;
	public static String TimerQueue_ERROR_0;
	public static String Timer_UI_1;
	public static String Timer_UI_0;
	public static String WizardPageExtensionManager_UI_4;
	public static String WizardPageExtensionManager_UI_3;
	public static String WizardPageExtensionManager_UI_2;
	public static String WizardPageExtensionManager_UI_1;
	public static String WizardPageExtensionManager_UI_0;
	public static String Delete_UI_0;

	static {
		NLS.initializeMessages(BUNDLE_NAME, WTPCommonUIResourceHandler.class);
	}

	public static String getString(String key, Object[] args) {
		return NLS.bind(key, args);
	}
}
