/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
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
	String VBF_EXC_INTERNAL = "VBF_EXC_INTERNAL"; //$NON-NLS-1$

	// Internal error occurred while constructing or running the Properties or Preference page.
	String VBF_EXC_INTERNAL_PAGE = "VBF_EXC_INTERNAL_PAGE"; //$NON-NLS-1$
	String VBF_EXC_INTERNAL_TITLE = "VBF_EXC_INTERNAL_TITLE"; //$NON-NLS-1$

	// Should never happen, since plugin.xml specifies an IProject objectClass filter, but if the
	// ValidationPropertiesPage is shown on a non-IProject resource, this message will be what's
	// shown.
	String VBF_EXC_INVALID_REGISTER = "VBF_EXC_INVALID_REGISTER"; //$NON-NLS-1$

	String VBF_UI_AUTO_NOTE = "VBF_UI_AUTO_NOTE"; //$NON-NLS-1$
	String VBF_UI_AUTO_NOTE_TEXT = "VBF_UI_AUTO_NOTE_TEXT"; //$NON-NLS-1$
	String VBF_UI_MENUITEM_TEXT = "VBF_UI_MENUITEM_TEXT"; //$NON-NLS-1$
	String VBF_UI_MENUITEM_TEXT_DEFAULT = "VBF_UI_MENUITEM_TEXT_DEFAULT"; //$NON-NLS-1$
	String VBF_UI_NO_VALIDATORS_INSTALLED = "VBF_UI_NO_VALIDATORS_INSTALLED"; //$NON-NLS-1$

	String VBF_UI_POPUP_RUNVALIDATION = "%VBF_UI_POPUP_RUNVALIDATION"; //$NON-NLS-1$

	String PREF_BUTTON_ENABLEALL = "PREF_BUTTON_ENABLEALL"; //$NON-NLS-1$
	String PREF_BUTTON_DISABLEALL = "PREF_BUTTON_DISABLEALL"; //$NON-NLS-1$
	String PREF_VALLIST_TITLE = "PREF_VALLIST_TITLE"; //$NON-NLS-1$
	String PREF_MNU_MANUAL = "PREF_MNU_MANUAL"; //$NON-NLS-1$
	String PREF_MNU_BUILD = "PREF_MNU_BUILD"; //$NON-NLS-1$
	String PREF_MNU_SETTINGS = "PREF_MNU_SETTINGS"; //$NON-NLS-1$
	
	String DISABLE_VALIDATION = "DISABLE_VALIDATION"; //$NON-NLS-1$
	String ADD_VALIDATION_BUILDER = "ADD_VALIDATION_BUILDER"; //$NON-NLS-1$
  
  	String VALIDATOR = "VALIDATOR"; //$NON-NLS-1$
  	String MANUAL = "MANUAL"; //$NON-NLS-1$
  	String BUILD = "BUILD"; //$NON-NLS-1$
  	String SETTINGS = "SETTINGS"; //$NON-NLS-1$
  	
 }
