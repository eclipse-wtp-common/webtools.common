/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.snippets.internal;


/**
 * Help context ids for the Snippets View
 * <p>
 * This interface contains constants only; it is not intended to be
 * implemented or extended.
 * </p>
 * 
 */
public interface IHelpContextIds {

	// not in use yet, so keep commented out
	// // Import button in Customize dialog
	// public static final String CUSTOMIZE_IMPORT_BUTTON = PREFIX +
	// "snip0010"; //$NON-NLS-1$
	// // Export button in Customize dialog
	// public static final String CUSTOMIZE_EXPORT_BUTTON = PREFIX +
	// "snip0020"; //$NON-NLS-1$

	// org.eclipse.wst.common.snippets.
	public static final String PREFIX = SnippetsPlugin.BUNDLE_ID + "."; //$NON-NLS-1$

	// Content type selection dialog
	public static final String DIALOG_CONTENT_TYPE_SELECTION = PREFIX + "snip0030"; //$NON-NLS-1$

	// New/Edit Category (dialog)
	public static final String DIALOG_EDIT_CATEGORY = PREFIX + "libv1300"; //$NON-NLS-1$

	// New/Edit Item (dialog)
	public static final String DIALOG_EDIT_VARITEM = PREFIX + "libv1200"; //$NON-NLS-1$

	// Selection Needed [for Filter Shown Categories] (dialog) (no longer
	// needed??)
	public static final String DIALOG_FILTER_CATEGORY = PREFIX + "libv1400"; //$NON-NLS-1$

	// Insert Template (dialog)
	public static final String DIALOG_INSERT_VARITEM = PREFIX + "libv1100"; //$NON-NLS-1$

	// Snippets view (general)
	public static final String MAIN_VIEW_GENERAL = PREFIX + "libv1000"; //$NON-NLS-1$

	// Add to snippets menu item
	public static final String MENU_ADD_TO_SNIPPETS = PREFIX + "snip0040"; //$NON-NLS-1$

	// Copy snippet menu item
	public static final String MENU_COPY_SNIPPET = PREFIX + "snip0060"; //$NON-NLS-1$

	// Cut snippet menu item
	public static final String MENU_CUT_SNIPPET = PREFIX + "snip0050"; //$NON-NLS-1$

	// Paste snippet menu item
	public static final String MENU_PASTE_SNIPPET = PREFIX + "snip0070"; //$NON-NLS-1$
}