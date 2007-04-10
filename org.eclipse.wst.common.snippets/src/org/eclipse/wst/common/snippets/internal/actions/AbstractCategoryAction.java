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
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.wst.common.snippets.core.ISnippetCategory;
import org.eclipse.wst.common.snippets.core.ISnippetItem;
import org.eclipse.wst.common.snippets.internal.SnippetsMessages;
import org.eclipse.wst.common.snippets.internal.ui.SnippetsView;

public abstract class AbstractCategoryAction extends Action {

	protected static class CategoryNameValidator implements IInputValidator {

		public String isValid(String name) {
			// Don't allow blank names
			if (name == null || name.length() < 1) {
				return (SnippetsMessages.A_name_must_be_specified_1); //$NON-NLS-1$
			}
			return null;
		}
	}

	private PaletteContainer fContainer = null;
	private SnippetsView fViewer = null;

	/**
	 * Constructor for AbstractItemAction.
	 * 
	 * @param text
	 */
	protected AbstractCategoryAction(String text) {
		super(text);
	}

	public AbstractCategoryAction(String text, SnippetsView viewer, PaletteContainer entry) {
		super(text); //$NON-NLS-1$
		fContainer = entry;
		fViewer = viewer;
	}

	/**
	 * Gets the container.
	 * 
	 * @return Returns a PaletteContainer
	 */
	public PaletteContainer getContainer() {
		return fContainer;
	}


	/**
	 * Gets the viewer.
	 * 
	 * @return Returns a GraphicalViewer
	 */
	public SnippetsView getViewer() {
		return fViewer;
	}

	protected void refresh(ISnippetCategory category) {
		EditPart part = (EditPart) fViewer.getViewer().getEditPartRegistry().get(category);
		if (part != null) {
			part.refresh();
		}
	}

	protected void refresh(ISnippetItem item) {
		EditPart part = (EditPart) fViewer.getViewer().getEditPartRegistry().get(item);
		if (part != null) {
			part.refresh();
		}
	}

	protected void refresh(PaletteEntry entry) {
		if (entry instanceof ISnippetCategory)
			refresh((ISnippetCategory) entry);
		else if (entry instanceof ISnippetItem)
			refresh((ISnippetItem) entry);
	}


}
