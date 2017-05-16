/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/


package org.eclipse.wst.common.snippets.internal.actions;

import org.eclipse.wst.common.snippets.core.ISnippetItem;
import org.eclipse.wst.common.snippets.core.ISnippetVariable;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteItem;


public class LibraryItemUtility {

	public static void copyAttributes(ISnippetItem sourceItem, ISnippetItem targetItem) {
		SnippetPaletteItem source = (SnippetPaletteItem) sourceItem;
		SnippetPaletteItem target = (SnippetPaletteItem) targetItem;

		target.setCategoryName(source.getCategoryName());
		target.setClassName(source.getClassName());
		target.setContentString(source.getContentString());
		target.setDescription(source.getDescription());
		target.setEditorClassName(source.getEditorClassName());
		target.setIconName(source.getSmallIconName());
		target.setId(source.getId());
		target.setLabel(source.getLabel());
		target.setLargeIconName(source.getLargeIconName());
		target.setSourceDescriptor(source.getSourceDescriptor());
		target.setSourceType(source.getSourceType());
		target.setVisible(source.isVisible());
		ISnippetVariable[] variables = target.getVariables();
		for (int i = 0; i < variables.length; i++) {
			target.removeVariable(variables[i]);
		}
		variables = source.getVariables();
		for (int i = 0; i < variables.length; i++) {
			target.addVariable(variables[i]);
		}
	}

}
