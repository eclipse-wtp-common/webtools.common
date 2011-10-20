/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.validation.ui.internal;

import org.eclipse.osgi.util.NLS;

public class ValUIMessages extends NLS {
	
	private static final String BUNDLE_NAME = "org.eclipse.wst.validation.ui.internal.messages"; //$NON-NLS-1$
	   
	public static String ADD_VALIDATION_BUILDER;
	
	public static String PREF_BUTTON_OVERRIDE;
	public static String PREF_VALLIST_TITLE;
	public static String PREF_BUTTON_ENABLEALL;
	public static String PREF_BUTTON_DISABLEALL;
	public static String PREF_MNU_MANUAL;
	public static String PREF_MNU_BUILD;
	public static String PREF_MNU_SETTINGS;
	
	public static String ConfigWsSettings;
	public static String DoNotShowResults;
	public static String DelegatesDialogTitle;
	public static String DelegatesComboLabel;
	
	public static String ErrorsWarnings;
		
	public static String VBF_EXC_INTERNAL_TITLE;
	public static String VBF_EXC_INTERNAL_PAGE;
	public static String VBF_EXC_INVALID_REGISTER;
	public static String DISABLE_VALIDATION;
	
	public static String MANUAL;
	public static String BUILD;
	public static String SETTINGS;
	public static String VALIDATOR;
	public static String VBF_UI_NO_VALIDATORS_INSTALLED;
	
	public static String SaveFilesDialog_saving;
	public static String SaveFilesDialog_always_save;
	public static String SaveFilesDialog_save_all_resources;
	public static String SaveFilesDialog_must_save;
	public static String PrefPage_always_save;
	public static String PrefPageConfirmDialog;
	public static String ProjectOverridesNotAllowed;
	public static String RunValidationDialogTitle;
	
	/* Validator Filters */
	public static String fdTitle;
	public static String fdNoFilters;

	public static String ErrConfig;
	
	public static String FilterHelp;
	
	public static String ButtonAddGroupInclude;	
	public static String ButtonAddGroupExclude;	
	public static String ButtonAddRule;	
	public static String ButtonRemove;
	
	public static String LabelExtension;
	public static String LabelFile;
	public static String LabelProjects;
	public static String LabelFacets;
	public static String LabelContentType;
	public static String LabelEnableProjectSpecific;
	
	public static String DescExtension;
	public static String DescFile;
	public static String DescProjects;
	public static String DescFacets;
	public static String DescContentType;
	
	public static String Validation;
	public static String ValidationStatus;
	
	public static String ErrSummary;
	public static String ValidationSuccessful;
	
	public static String FrWizard;
	public static String FrSelectFilterType;
	public static String FrFileExtension;
	public static String FrFileExtensionLabel;
	public static String FrCaseSensitive;
	public static String FrExactMatch;
	public static String FrFolderOrFile;
	public static String FrFolderOrFileLabel;
	public static String FrBrowseFile;
	public static String FrFileFilter;
	public static String FrBrowseFolder;
	public static String FrFolderFilter;
	public static String FrProjectNature;
	public static String FrProjectNatureLabel;
	public static String FrSimpleFileName;
	public static String FrFacit;
	public static String FrFacitLabel;
	public static String FrContentType;
	public static String FrNewFacitLabel;
	public static String FrContentTypeLabel;
	public static String FrMsgSev;
	
	public static String RebuildTitle;
	public static String RebuildMsg;
	
		
	static {
		NLS.initializeMessages(BUNDLE_NAME, ValUIMessages.class);
	  }

}
