/*******************************************************************************
 * Copyright (c) 2004, 2024 IBM Corporation and others.
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
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.wst.common.snippets.core.ISnippetCategory;
import org.eclipse.wst.common.snippets.internal.SnippetDefinitions;
import org.eclipse.wst.common.snippets.internal.SnippetsMessages;
import org.eclipse.wst.common.snippets.internal.SnippetsPlugin;
import org.eclipse.wst.common.snippets.internal.ui.SnippetsView;

public class DeleteCategoryAction extends AbstractCategoryAction {


	public DeleteCategoryAction(SnippetsView viewer, PaletteContainer container) {
		super(SnippetsMessages.Delete_1, viewer, container);
	}

	public void run() {
		super.run();
		ISnippetCategory category = (ISnippetCategory) getContainer();
		EditPart categoryPart = getViewer().getViewer().getEditPartRegistry().get(category);
		if (categoryPart != null) {
			SnippetDefinitions defs = SnippetsPlugin.getSnippetManager().getDefinitions();
			defs.getCategories().remove(category);
			EditPart parent = categoryPart.getParent();
			parent.refresh();
		}
	}
}
