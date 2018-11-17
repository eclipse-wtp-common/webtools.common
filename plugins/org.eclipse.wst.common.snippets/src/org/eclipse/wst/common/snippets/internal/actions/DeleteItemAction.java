/*******************************************************************************
 * Copyright (c) 2004, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.snippets.internal.actions;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.wst.common.snippets.internal.SnippetsMessages;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteDrawer;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteItem;

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
		setText(SnippetsMessages.Delete_1);
		setId("delete"); //$NON-NLS-1$
	}

	public void run() {
		super.run();
		SnippetPaletteItem item = (SnippetPaletteItem) getEntry();
		EditPart itemPart = (EditPart) getViewer().getEditPartRegistry().get(item);
		if (itemPart != null) {
			SnippetPaletteDrawer category = (SnippetPaletteDrawer) item.getCategory();
			EditPart categoryPart = itemPart.getParent();
			category.remove(item);
			categoryPart.refresh();
		}
	}
}
