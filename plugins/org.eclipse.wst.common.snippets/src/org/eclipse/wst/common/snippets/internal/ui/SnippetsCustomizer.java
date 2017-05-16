/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.snippets.internal.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteTemplateEntry;
import org.eclipse.gef.ui.palette.PaletteCustomizer;
import org.eclipse.gef.ui.palette.customize.EntryPage;
import org.eclipse.wst.common.snippets.core.ISnippetCategory;
import org.eclipse.wst.common.snippets.core.ISnippetsEntry;
import org.eclipse.wst.common.snippets.internal.Logger;
import org.eclipse.wst.common.snippets.internal.editors.ISnippetEditor;
import org.eclipse.wst.common.snippets.internal.model.SnippetManager;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteDrawer;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteDrawerFactory;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteItem;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteItemFactory;


public class SnippetsCustomizer extends PaletteCustomizer {
	
	protected List activeEditors = new ArrayList();
	protected List factories = null;
	private List deletedIds = new ArrayList();

	public SnippetsCustomizer() {
		super();
		factories = new ArrayList(2);
		factories.add(new SnippetPaletteDrawerFactory());
		factories.add(new SnippetPaletteItemFactory());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.palette.PaletteCustomizer#canAdd(org.eclipse.gef.palette.PaletteContainer,
	 *      org.eclipse.gef.palette.PaletteEntry)
	 */
	protected boolean canAdd(PaletteContainer container, PaletteEntry entry) {
		if (entry instanceof ISnippetCategory) {
			return super.canAdd(container, entry);
		}
		else if (entry.getType().equals(PaletteTemplateEntry.PALETTE_TYPE_TEMPLATE)) {
			return container.getType().equals(PaletteDrawer.PALETTE_TYPE_DRAWER) && ((ISnippetsEntry) entry).getSourceType() != ISnippetsEntry.SNIPPET_SOURCE_PLUGINS && super.canAdd(container, entry);
		}
		return ((ISnippetsEntry) entry).getSourceType() != ISnippetsEntry.SNIPPET_SOURCE_PLUGINS && super.canAdd(container, entry);
	}

	public boolean canDelete(PaletteEntry entry) {
		return ((ISnippetsEntry) entry).getSourceType() != ISnippetsEntry.SNIPPET_SOURCE_PLUGINS && super.canDelete(entry);
	}

	public boolean canExport(PaletteEntry entry) {
		return ((ISnippetsEntry) entry).getSourceType() != ISnippetsEntry.SNIPPET_SOURCE_PLUGINS && entry.getType() == PaletteDrawer.PALETTE_TYPE_DRAWER;
	}

	public boolean canImport(PaletteEntry entry) {
		return true;
	}

	public boolean canMoveDown(PaletteEntry entry) {
		if (entry instanceof ISnippetCategory)
			return super.canMoveDown(entry);
		return ((ISnippetsEntry) entry).getSourceType() != ISnippetsEntry.SNIPPET_SOURCE_PLUGINS && super.canMoveDown(entry);
	}

	public boolean canMoveUp(PaletteEntry entry) {
		if (entry instanceof ISnippetCategory)
			return super.canMoveUp(entry);
		return ((ISnippetsEntry) entry).getSourceType() != ISnippetsEntry.SNIPPET_SOURCE_PLUGINS && super.canMoveUp(entry);
	}

	/**
	 * Find and instantiate the editor class
	 */

	public List getNewEntryFactories() {
		return factories;
	}

	/**
	 * @see org.eclipse.gef.ui.palette.PaletteCustomizer#getPropertiesPage(org.eclipse.gef.palette.PaletteEntry)
	 */
	public EntryPage getPropertiesPage(PaletteEntry entry) {
		if (entry instanceof SnippetPaletteDrawer) {
			return new SnippetDrawerEntryPage();
		}
		else if (entry instanceof SnippetPaletteItem && entry.getUserModificationPermission() == PaletteEntry.PERMISSION_FULL_MODIFICATION) {
			return new SnippetTemplateEntryPage(this);
		}
		return super.getPropertiesPage(entry);
	}

	public void revertToSaved() {
		activeEditors = new ArrayList(0);
		SnippetManager.getInstance().resetDefinitions();
	}

	public void save() {
		// The EntryPage's apply() is final, so we have to force updates now
		for (int i = 0; i < activeEditors.size(); i++) {
			try {
				((ISnippetEditor) activeEditors.get(i)).updateItem();
			}
			catch (Exception e) {
				Logger.logException(e);
			}
		}
		
		for (int i = 0; i < deletedIds.size(); i++) {
			IPath path = SnippetManager.getInstance().getStorageLocation(deletedIds.get(i).toString());
			File folder = new File(path.toOSString());
			deleteFolders(folder);
		}
		deletedIds.clear();

		activeEditors = new ArrayList(0);

		try {
			SnippetManager.getInstance().saveDefinitions();
		}
		catch (Exception e) {
			Logger.logException(e);
		}
	}

	private void deleteFolders(File folder) {
		if (!folder.exists()) {
			return;
		}
		File[] listFiles = folder.listFiles();
		for (int i = 0; i < listFiles.length; i++) {
			if (listFiles[i].isDirectory()) {
				deleteFolders(listFiles[i]);
			}
			else {
				listFiles[i].delete();
			}
		}
		folder.delete();
	}

	public void performDelete(PaletteEntry entry) {
		deletedIds.add(entry.getId());
		super.performDelete(entry);
	}

}
