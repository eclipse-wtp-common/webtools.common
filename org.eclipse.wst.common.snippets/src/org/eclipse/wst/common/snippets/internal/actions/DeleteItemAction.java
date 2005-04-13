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
package org.eclipse.wst.common.snippets.internal.actions;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.wst.common.snippets.core.ISnippetItem;
import org.eclipse.wst.common.snippets.internal.ISnippetCategory;
import org.eclipse.wst.common.snippets.internal.SnippetsMessages;

public class DeleteItemAction extends AbstractItemAction {

	/*
	 * @see IAction#run()
	 */
	/**
	 * Constructor for DeleteItemAction.
	 * 
	 * @param viewer
	 * @param entry
	 */
	public DeleteItemAction(GraphicalViewer viewer, PaletteEntry entry) {
		super(viewer, entry);
		setText(SnippetsMessages.Delete_1); //$NON-NLS-1$
		setId("delete"); //$NON-NLS-1$
	}

	public void run() {
		super.run();
		ISnippetItem item = (ISnippetItem) getEntry();
		EditPart itemPart = (EditPart) getViewer().getEditPartRegistry().get(item);
		if (itemPart != null) {
			ISnippetCategory category = item.getCategory();
			EditPart categoryPart = itemPart.getParent();
			category.remove(item);
			categoryPart.refresh();
		}
	}
}