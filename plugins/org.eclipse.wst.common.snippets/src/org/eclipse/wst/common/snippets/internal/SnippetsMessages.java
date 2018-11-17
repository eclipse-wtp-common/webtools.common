/**********************************************************************
 * Copyright (c) 2005, 2016 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License 2.0 which accompanies this distribution, and is
 * available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: 
 * IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.common.snippets.internal;

import org.eclipse.osgi.util.NLS;

/**
 * Strings used by Snippets
 * 
 * @plannedfor 1.0
 */
public class SnippetsMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.wst.common.snippets.internal.SnippetsPluginResources";//$NON-NLS-1$

	public static String A_name_must_be_specified_1;
	public static String New_Category_Title;
	public static String New_Item_Title;
	public static String Delete_1;
	public static String Unnamed_Template_1;
	public static String Insert_Template___1;
	public static String Insert_Template_2;
	public static String Edit_Instruction;
	public static String Variables__4;
	public static String Description_of_variable__5;
	public static String Variable_Name_6;
	public static String Value_7;
	public static String Preview__9;
	public static String Insert_14;
	public static String Variable_Name_3;
	public static String Value_4;
	public static String Name_5;

	public static String NameCannotBeEmpty;
	public static String Description_6;
	public static String Default_Value_7;
	public static String Remove_15;
	public static String Template_Pattern__16;
	public static String Insert_Variable_17;
	public static String Insert___;
	public static String New_1;
	public static String Cut_2;
	public static String Copy_2;
	public static String Paste_4;
	public static String new_category_name;
	public static String category;
	public static String item;
	public static String Unnamed_Category;
	public static String Add_to_Snippets____3;
	public static String Cant_add_to_this;
	public static String choose_or_create;
	public static String force_create;
	public static String SnippetCustomizerDialog_0;
	public static String SnippetCustomizerDialog_1;
	public static String SnippetCustomizerDialog_2;
	public static String SnippetCustomizerDialog_3;
	public static String SnippetCustomizerDialog_4;
	public static String SnippetDrawerEntryPage_0;
	public static String SnippetDrawerEntryPage_1;
	public static String SnippetDrawerEntryPage_2;
	public static String SnippetDrawerEntryPage_3;
	public static String SnippetDrawerEntryPage_4;
	public static String SnippetDrawerEntryPage_5;
	public static String SnippetDrawerEntryPage_6;
	public static String Paste_as_Snippet;
	public static String Import_Snippets;
	public static String Export_Snippets;

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, SnippetsMessages.class);
	}

	private SnippetsMessages() {
		// cannot create new instance
	}
}
