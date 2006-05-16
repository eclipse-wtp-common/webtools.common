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


/**
 * This interface contains the constants used as keys into the ResourceBundle.
 */
public interface ResourceConstants {
	// Internal error has occurred.
	public static final String VBF_EXC_INTERNAL = "VBF_EXC_INTERNAL"; //$NON-NLS-1$

	// Internal error occurred while constructing or running the Properties or Preference page.
	public static final String VBF_EXC_INTERNAL_PAGE = "VBF_EXC_INTERNAL_PAGE"; //$NON-NLS-1$

	// Internal error has occurred.
	public static final String VBF_EXC_INTERNAL_PROJECT = "VBF_EXC_INTERNAL_PROJECT"; //$NON-NLS-1$
	public static final String VBF_EXC_INTERNAL_TITLE = "VBF_EXC_INTERNAL_TITLE"; //$NON-NLS-1$

	// Should never happen, since plugin.xml specifies an IProject objectClass filter, but if the
	// ValidationPropertiesPage is shown on a non-IProject resource, this message will be what's
	// shown.
	public static final String VBF_EXC_INVALID_REGISTER = "VBF_EXC_INVALID_REGISTER"; //$NON-NLS-1$

	// On the Properties Page, this is the string which explains to the user what to do. The string
	// is
	// displayed in a label.
	public static final String VBF_UI_LBL_DESC = "VBF_UI_LBL_DESC"; //$NON-NLS-1$

	// On the Properties Page, this is the string which explains to the user what to do. The string
	// is
	// displayed in a label.
	public static final String VBF_UI_LBL_NOVALIDATORS_DESC = "VBF_UI_LBL_NOVALIDATORS_DESC"; //$NON-NLS-1$

	// Label on the checkbox button of the validator page
	public static final String VBF_UI_LBL_AUTO_VALIDATE = "VBF_UI_LBL_AUTO_VALIDATE"; //$NON-NLS-1$

	// If the user has selected "Validate this project" from the popupMenu, but has no validators
	// enabled, tell them that nothing will run.
	public static final String VBF_UI_NO_VALIDATORS_ENABLED = "VBF_UI_NO_VALIDATORS_ENABLED"; //$NON-NLS-1$

	// On the Properties Page, this is the title of the group which encloses the CheckboxTableViewer
	// (the list of configured validators).
	public static final String VBF_UI_CHECKBOXGROUP_TITLE = "VBF_UI_CHECKBOXGROUP_TITLE"; //$NON-NLS-1$
	public static final String VBF_UI_AUTO_ON_NONINC = "VBF_UI_AUTO_ON_NONINC"; //$NON-NLS-1$
	public static final String VBF_UI_MSSGBOX_TITLE_NONINC = "VBF_UI_MSSGBOX_TITLE_NONINC"; //$NON-NLS-1$
	public static final String VBF_UI_AUTO_NOTE = "VBF_UI_AUTO_NOTE"; //$NON-NLS-1$
	public static final String VBF_UI_AUTO_NOTE_TEXT = "VBF_UI_AUTO_NOTE_TEXT"; //$NON-NLS-1$
	public static final String VBF_UI_MENUITEM_TEXT = "VBF_UI_MENUITEM_TEXT"; //$NON-NLS-1$
	public static final String VBF_UI_MENUITEM_TEXT_DEFAULT = "VBF_UI_MENUITEM_TEXT_DEFAULT"; //$NON-NLS-1$
	public static final String VBF_UI_CLOSED_PROJECT = "VBF_UI_CLOSED_PROJECT"; //$NON-NLS-1$
	public static final String VBF_UI_NO_VALIDATORS_INSTALLED = "VBF_UI_NO_VALIDATORS_INSTALLED"; //$NON-NLS-1$

	public static final String VBF_UI_POPUP_RUNVALIDATION = "%VBF_UI_POPUP_RUNVALIDATION"; //$NON-NLS-1$

	/* package */static final String PREF_BUTTON_ENABLEALL = "PREF_BUTTON_ENABLEALL"; //$NON-NLS-1$
	/* package */static final String PREF_BUTTON_DISABLEALL = "PREF_BUTTON_DISABLEALL"; //$NON-NLS-1$
	/* package */static final String PREF_VALLIST_TITLE = "PREF_VALLIST_TITLE"; //$NON-NLS-1$
	/* package */static final String PREF_BUTTON_AUTO = "PREF_BUTTON_AUTO"; //$NON-NLS-1$
	/* package */static final String PREF_BUTTON_OVERRIDE = "PREF_BUTTON_OVERRIDE"; //$NON-NLS-1$
	/* package */static final String PREF_BUTTON_FULL = "PREF_BUTTON_FULL"; //$NON-NLS-1$
	/* package */static final String PREF_LBL_MAXMSGS = "PREF_LBL_MAXMSGS"; //$NON-NLS-1$
  /* package */static final String PREF_MNU_MANUAL = "PREF_MNU_MANUAL"; //$NON-NLS-1$
  /* package */static final String PREF_MNU_BUILD = "PREF_MNU_BUILD"; //$NON-NLS-1$
  /* package */static final String PREF_MNU_SETTINGS = "PREF_MNU_SETTINGS"; //$NON-NLS-1$
  /* package */static final String PROP_BUTTON_OVERRIDE = "PROP_BUTTON_OVERRIDE"; //$NON-NLS-1$
	/* package */static final String PROP_BUTTON_FULL = "PROP_BUTTON_FULL"; //$NON-NLS-1$
	/* package */static final String PROP_BUTTON_SELECTALL = "PROP_BUTTON_SELECTALL"; //$NON-NLS-1$
	/* package */static final String PROP_BUTTON_DESELECTALL = "PROP_BUTTON_DESELECTALL"; //$NON-NLS-1$
	/* package */static final String PROP_LBL_MAXMSSGS = "PROP_LBL_MAXMSSGS"; //$NON-NLS-1$
	/* package */static final String PREF_ERROR_INT = "PREF_ERROR_INT"; //$NON-NLS-1$
	/* package */static final String PROP_ERROR_INT = "PROP_ERROR_INT"; //$NON-NLS-1$

	/* package */static final String VBF_UI_PRJNEEDINPUT = "VBF_UI_PRJNEEDINPUT"; //$NON-NLS-1$
	/* package */static final String VBF_UI_RESNEEDINPUT = "VBF_UI_RESNEEDINPUT"; //$NON-NLS-1$
	/* package */static final String VBF_UI_MAX_REPORTED = "VBF_UI_MAX_REPORTED"; //$NON-NLS-1$
	/* package */static final String VBF_UI_PRJVALIDATED = "VBF_UI_PRJVALIDATED"; //$NON-NLS-1$
	/* package */static final String VBF_UI_RESVALIDATED = "VBF_UI_RESVALIDATED"; //$NON-NLS-1$
	/* package */static final String VBF_UI_CANCELLED = "VBF_UI_CANCELLED"; //$NON-NLS-1$
	/* package */static final String VBF_UI_COMPLETE = "VBF_UI_COMPLETE"; //$NON-NLS-1$
	/* package */static final String VBF_UI_CLOSE = "VBF_UI_CLOSE"; //$NON-NLS-1$
	/* package */static final String VBF_UI_STATUS = "VBF_UI_STATUS"; //$NON-NLS-1$
	/* package */static final String VBF_UI_RESCANCELLED = "VBF_UI_RESCANCELLED"; //$NON-NLS-1$
	
	/* package */static final String DISABLE_VALIDATION = "DISABLE_VALIDATION"; //$NON-NLS-1$
	/* package */static final String ADD_VALIDATION_BUILDER = "ADD_VALIDATION_BUILDER"; //$NON-NLS-1$
	static final String INFO = "INFO"; //$NON-NLS-1$
  
  String DELEGATES_DIALOG_TITLE = "DELEGATES_DIALOG_TITLE"; //$NON-NLS-1$
  String DELEGATES_COMBO_LABEL = "DELEGATES_COMBO_LABEL"; //$NON-NLS-1$
  
  	public static final String CONFIG_WS_SETTINGS="CONFIG_WS_SETTINGS";//$NON-NLS-1$
  	public static final String VALIDATOR = "VALIDATOR"; //$NON-NLS-1$
  	public static final String MANUAL = "MANUAL"; //$NON-NLS-1$
  	public static final String BUILD = "BUILD"; //$NON-NLS-1$
  	public static final String SETTINGS = "SETTINGS"; //$NON-NLS-1$
  	
 }
