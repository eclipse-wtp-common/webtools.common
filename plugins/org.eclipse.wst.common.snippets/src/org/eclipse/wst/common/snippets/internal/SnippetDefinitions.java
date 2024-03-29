/*******************************************************************************
 * Copyright (c) 2004, 2022 IBM Corporation and others.
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
package org.eclipse.wst.common.snippets.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.common.snippets.core.ISnippetCategory;
import org.eclipse.wst.common.snippets.core.ISnippetItem;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteDrawer;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteItem;


/**
 * A collection of all the Snippets Categories and Items from one source.
 */
public class SnippetDefinitions {

	/**
	 * The full list of category model objects
	 */
	protected List<ISnippetCategory> fCategories = null;
	/**
	 * The full list of item model objects
	 */
	protected List<ISnippetItem> fItems = null;

	/**
	 * Gets the categories.
	 * 
	 * @return Returns a List
	 */
	public List<ISnippetCategory> getCategories() {
		if (fCategories == null)
			fCategories = new ArrayList<>();
		return fCategories;
	}

	/**
	 * Locates a unique ISnippetCategory by its ID
	 * 
	 * @param id -
	 *            the ID of the category to locate
	 * @return the ISnippetCategory if found, null if not
	 */
	public ISnippetCategory getCategory(String id) {
		Iterator<ISnippetCategory> iterator = getCategories().iterator();
		while (iterator.hasNext()) {
			SnippetPaletteDrawer category = (SnippetPaletteDrawer) iterator.next();
			if (category.getId().equals(id))
				return category;
		}
		return null;
	}

	/**
	 * Locates a unique ISnippetItem by its ID
	 * 
	 * @param id
	 * @return the ISnippetItem if found, null if not
	 */
	public ISnippetItem getItem(String id) {
		Iterator<ISnippetItem> iterator = getItems().iterator();
		while (iterator.hasNext()) {
			SnippetPaletteItem item = (SnippetPaletteItem) iterator.next();
			if (item.getId().equals(id))
				return item;
		}
		for (int i = 0; i < fCategories.size(); i++) {
			ISnippetItem[] items = fCategories.get(i).getItems();
			for (int j = 0; j < items.length; j++) {
				SnippetPaletteItem item = (SnippetPaletteItem) items[j];
				if (item.getId().equals(id)) {
					return item;
				}
			}
		}
		return null;
	}

	/**
	 * Gets the items that were found by all of the model factories. This may
	 * be different from all of the items within the categories.
	 * 
	 * @return a List of all the ISnippetItems loaded
	 */
	public List<ISnippetItem> getItems() {
		if (fItems == null)
			fItems = new ArrayList<>();
		return fItems;
	}

	/**
	 * Sets the entire list of known ISnippetCategorys.
	 * 
	 * @param categories -
	 *            the categories to set
	 */
	public void setCategories(List<ISnippetCategory> categories) {
		fCategories = categories;
	}

	/**
	 * Sets the entire list of known ISnippetItems.
	 * 
	 * @param items
	 *            The items to set
	 */
	public void setItems(List<ISnippetItem> items) {
		fItems = items;
	}
}
