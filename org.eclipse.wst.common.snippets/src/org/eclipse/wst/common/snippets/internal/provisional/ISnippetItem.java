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

package org.eclipse.wst.common.snippets.internal.provisional;

import org.eclipse.wst.common.snippets.internal.ISnippetCategory;
import org.eclipse.wst.common.snippets.internal.ISnippetVariable;

/**
 * A snippet item is the actual object draggable from the Snippets view.
 * 
 * This interface is not meant to be implemented by clients.
 */
public interface ISnippetItem extends ISnippetsEntry {

	/**
	 * @param the variable to add
	 * @deprecated assumes that all implementions support this style of
	 *             variables
	 */
	void addVariable(ISnippetVariable variable);

	/**
	 * @return the category holding this item
	 * @deprecated
	 */
	ISnippetCategory getCategory();

	/**
	 * @return the name of the category holding this item. This is a handle
	 *         only method; the category need not exist yet.
	 * @deprecated assumes that all implementions support this style of
	 *             variables
	 */
	String getCategoryName();

	/**
	 * @return the ISnippetInsertion class to be used with this item
	 * @deprecated
	 */
	String getClassName();

	/**
	 * @return the content string for this item; the text inserted may not be
	 *         identical
	 */
	String getContentString();

	/**
	 * @return the ISnippetEditor class to be used with this item
	 * @deprecated
	 */
	String getEditorClassName();

	/**
	 * @return an array containing all of the valid variables for this item
	 * @deprecated assumes that all implementions support this style of
	 *             variables
	 */
	ISnippetVariable[] getVariables();

	/**
	 * @param the variable to remove. If the variable is not a know variable
	 *            of this item, it is ignored.
	 * 
	 * @deprecated assumes that all implementions support this style of
	 *             variables
	 */
	void removeVariable(ISnippetVariable variable);

	/**
	 * @param the category for this item
	 * @deprecated - should not be changeable
	 */
	void setCategory(ISnippetCategory category);

	/**
	 * @param the name of the category holding this item. This is a handle
	 *            only method; the category need not exist yet.
	 * @deprecated - should not be changeable
	 */
	void setCategoryName(String name);

	/**
	 * @param the name of the insertion class for this item
	 * 
	 * @deprecated - should not be changeable
	 */
	void setClassName(String className);

	/**
	 * @param the new content string for this item
	 * @deprecated - should not be changeable
	 */
	void setContentString(String content);

	/**
	 * @param the name of the editor class for this item
	 * 
	 * @deprecated - should not be changeable
	 */
	void setEditorClassName(String editorClassName);
}