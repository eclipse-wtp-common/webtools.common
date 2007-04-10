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

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.wst.common.snippets.core.ISnippetCategory;
import org.eclipse.wst.common.snippets.internal.SnippetDefinitions;
import org.eclipse.wst.common.snippets.internal.SnippetsMessages;
import org.eclipse.wst.common.snippets.internal.SnippetsPlugin;
import org.eclipse.wst.common.snippets.internal.ui.SnippetsView;

public class DeleteCategoryAction extends AbstractCategoryAction {


	public DeleteCategoryAction(SnippetsView viewer, PaletteContainer container) {
		super(SnippetsMessages.Delete_1, viewer, container); //$NON-NLS-1$
	}

	public void run() {
		super.run();
		ISnippetCategory category = (ISnippetCategory) getContainer();
		EditPart categoryPart = (EditPart) getViewer().getViewer().getEditPartRegistry().get(category);
		if (categoryPart != null) {
			SnippetDefinitions defs = SnippetsPlugin.getSnippetManager().getDefinitions();
			defs.getCategories().remove(category);
			EditPart parent = categoryPart.getParent();
			parent.refresh();
		}
	}
}
