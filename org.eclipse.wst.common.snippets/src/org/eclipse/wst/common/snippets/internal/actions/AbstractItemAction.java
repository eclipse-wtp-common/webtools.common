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
import org.eclipse.jface.action.Action;
import org.eclipse.wst.common.snippets.core.ISnippetItem;
import org.eclipse.wst.common.snippets.internal.ISnippetCategory;


public abstract class AbstractItemAction extends Action {

	private PaletteEntry fEntry = null;
	private GraphicalViewer fViewer = null;

	public AbstractItemAction(GraphicalViewer viewer, PaletteEntry entry) {
		super();
		fEntry = entry;
		fViewer = viewer;
	}

	/**
	 * Constructor for AbstractItemAction.
	 * 
	 * @param text
	 */
	protected AbstractItemAction(String text) {
		super(text);
	}

	public AbstractItemAction(String text, GraphicalViewer viewer, PaletteEntry entry) {
		super(text);
		fEntry = entry;
		fViewer = viewer;
	}

	/**
	 * Gets the entry.
	 * 
	 * @return Returns a PaletteEntry
	 */
	public PaletteEntry getEntry() {
		return fEntry;
	}

	/**
	 * Returns the viewer.
	 * 
	 * @return GraphicalViewer
	 */
	public GraphicalViewer getViewer() {
		return fViewer;
	}

	protected void refresh(ISnippetCategory category) {
		EditPart part = (EditPart) fViewer.getEditPartRegistry().get(category);
		if (part != null) {
			part.refresh();
		}
	}

	protected void refresh(ISnippetItem item) {
		EditPart part = (EditPart) fViewer.getEditPartRegistry().get(item);
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

	/**
	 * Sets the entry.
	 * 
	 * @param entry
	 *            The entry to set
	 */
	public void setEntry(PaletteEntry entry) {
		fEntry = entry;
	}


	/**
	 * Sets the viewer.
	 * 
	 * @param viewer
	 *            The viewer to set
	 */
	public void setViewer(GraphicalViewer viewer) {
		fViewer = viewer;
	}


}