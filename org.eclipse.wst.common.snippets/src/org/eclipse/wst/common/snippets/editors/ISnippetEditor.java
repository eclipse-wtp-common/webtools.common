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

package org.eclipse.wst.common.snippets.editors;

import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.wst.common.snippets.core.ISnippetItem;

/**
 * A snippet editor is responsible for creating the interface from which a
 * user modifies a snippet item or category.
 */
public interface ISnippetEditor {

	/**
	 * Adds a modify listener to this editor. Typically the UI surrounding
	 * this editor will listen for modifications.
	 * @param listener the to be added
	 */
	void addModifyListener(ModifyListener listener);

	/**
	 * Fill-in the contents of an editing Dialog.
	 */
	Control createContents(Composite parent);

	/**
	 * Get the ISnippetItem being edited.
	 */
	ISnippetItem getItem();

	/**
	 * Remove a modify listener from this editor. Typically the UI surrounding
	 * this editor will listen for modifications.
	 * @param listener the to be added
	 */
	void removeModifyListener(ModifyListener listener);

	/**
	 * Set the ISnippetItem to edit.
	 */
	void setItem(ISnippetItem item);

	/**
	 * Update the ISnippetItem being edited, usually because the values of the
	 * dialog's controls have been changed or it is being closed.
	 */
	void updateItem();
}