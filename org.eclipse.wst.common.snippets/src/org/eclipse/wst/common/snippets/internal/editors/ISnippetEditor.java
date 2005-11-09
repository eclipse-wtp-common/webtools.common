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

package org.eclipse.wst.common.snippets.internal.editors;

import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.wst.common.snippets.core.ISnippetItem;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteItem;

/**
 * A snippet editor is responsible for creating the interface from which a
 * user modifies a snippet item or category.
 * 
 * This interface is not meant to be implemented by clients.
 */
public interface ISnippetEditor {

	/**
	 * Adds a modify listener to this editor. Typically the UI surrounding
	 * this editor will listen for modifications.
	 * 
	 * @param listener
	 *            the to be added
	 */
	void addModifyListener(ModifyListener listener);

	/**
	 * Fill-in the contents of an editing Dialog.
	 * 
	 * @param parent
	 *            the parent composite for the editor's control
	 * @return the main control provided by the editor
	 */
	Control createContents(Composite parent);

	/**
	 * Get the ISnippetItem being edited.
	 * 
	 * @return the item being edited
	 */
	ISnippetItem getItem();

	/**
	 * Remove a modify listener from this editor. Typically the UI surrounding
	 * this editor will listen for modifications.
	 * 
	 * @param listener
	 *            the to be added
	 */
	void removeModifyListener(ModifyListener listener);

	/**
	 * Set the ISnippetItem to edit.
	 * 
	 * @param item
	 *            the item to edit
	 */
	void setItem(SnippetPaletteItem item);

	/**
	 * Update the ISnippetItem being edited, usually because the values of the
	 * dialog's controls have been changed or it is being closed.
	 */
	void updateItem();
}