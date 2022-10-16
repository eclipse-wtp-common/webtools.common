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
package org.eclipse.wst.common.snippets.internal.palette;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.wst.common.snippets.core.ISnippetCategory;
import org.eclipse.wst.common.snippets.core.ISnippetItem;
import org.eclipse.wst.common.snippets.core.ISnippetVariable;
import org.eclipse.wst.common.snippets.core.ISnippetsEntry;
import org.eclipse.wst.common.snippets.internal.Logger;
import org.eclipse.wst.common.snippets.internal.SnippetDefinitions;
import org.eclipse.wst.common.snippets.internal.SnippetsPlugin;
import org.eclipse.wst.common.snippets.internal.util.SnippetProviderManager;

public abstract class AbstractModelFactory {

	public AbstractModelFactory() {
		super();
	}

	protected void connectItemsAndCategories(SnippetDefinitions definitions) {
		Iterator<ISnippetCategory> categories = definitions.getCategories().iterator();
		while (categories.hasNext()) {
			SnippetPaletteDrawer category = (SnippetPaletteDrawer) categories.next();
			category.getChildren().clear();
		}

		Iterator<ISnippetItem> items = definitions.getItems().iterator();
		while (items.hasNext()) {
			SnippetPaletteItem item = (SnippetPaletteItem) items.next();
			SnippetPaletteDrawer parentCategory = (SnippetPaletteDrawer) definitions.getCategory(item.getCategoryName());
			if (parentCategory != null) {
				parentCategory.add(item);
			}
			else {
				Logger.log(Logger.WARNING, "Rejecting item " + item.getId() + " in missing category " + item.getCategoryName()); //$NON-NLS-1$ //$NON-NLS-2$
				items.remove();
			}
		}

	}

	public SnippetPaletteDrawer createCategory(Object source) {
		String id = getID(source);
		if (id == null || id.length() == 0)
			return null;

		SnippetPaletteDrawer drawer = new SnippetPaletteDrawer(id);
		drawer.setType(PaletteDrawer.PALETTE_TYPE_DRAWER);
		setProperties(drawer, source);

		migrate50to51(drawer);

		return drawer;
	}

	public SnippetPaletteItem createItem(Object source) {
		String id = getID(source);
		if (id == null || id.length() == 0)
			return null;

		SnippetPaletteItem item = new SnippetPaletteItem(id);
		item.setDescription(""); //$NON-NLS-1$
		setProperties(item, source);

		migrate50to51(item);
		
		return item;
	}

	public ISnippetVariable createVariable(Object source) {
		if (source == null)
			return null;
		SnippetVariable var = new SnippetVariable();
		setProperties(var, source);
		return var;
	}

	protected List<ISnippetVariable> createVariables(Object[] sources) {
		if (sources == null || sources.length < 1)
			return null;
		List<ISnippetVariable> variables = new ArrayList<>(sources.length);
		for (int i = 0; i < sources.length; i++) {
			ISnippetVariable variable = createVariable(sources[i]);
			if (variable != null)
				variables.add(variable);
		}
		return variables;
	}

	protected abstract String getID(Object source);

	public abstract SnippetDefinitions loadCurrent();

	protected void migrate50to51(ISnippetsEntry entry) {
		if (entry.getDescription() != null && entry.getDescription().length() > 0 && (entry.getLabel() == null || entry.getLabel().length() == 0)) {
			if (entry instanceof SnippetPaletteItem) {
				((SnippetPaletteItem) entry).setLabel(entry.getDescription());
				((SnippetPaletteItem) entry).setDescription(""); //$NON-NLS-1$
			}
			if (entry instanceof SnippetPaletteDrawer) {
				((SnippetPaletteDrawer) entry).setLabel(entry.getDescription());
				((SnippetPaletteDrawer) entry).setDescription(""); //$NON-NLS-1$
			}
		}
	}

	protected void setEntryProperty(ISnippetsEntry entry, String property, Object value) {
		if (property == null || value == null)
			return;
		if (entry instanceof SnippetPaletteItem) {
			if (property.equals(SnippetsPlugin.NAMES.DESCRIPTION))
				((SnippetPaletteItem) entry).setDescription(value.toString());
			else if (property.equals(SnippetsPlugin.NAMES.SMALLICON))
				((SnippetPaletteItem) entry).setIconName(value.toString());
			else if (property.equals(SnippetsPlugin.NAMES.ID))
				((SnippetPaletteItem) entry).setId(value.toString());
			else if (property.equals(SnippetsPlugin.NAMES.LABEL))
				((SnippetPaletteItem) entry).setLabel(value.toString());
			else if (property.equals(SnippetsPlugin.NAMES.LARGEICON))
				((SnippetPaletteItem) entry).setLargeIconName(value.toString());
			else if (property.equals("filters")) //$NON-NLS-1$
				((SnippetPaletteItem) entry).setFilters((String[]) value);
		}
		if (entry instanceof SnippetPaletteDrawer) {
			if (property.equals(SnippetsPlugin.NAMES.DESCRIPTION))
				((SnippetPaletteDrawer) entry).setDescription(value.toString());
			else if (property.equals(SnippetsPlugin.NAMES.SMALLICON))
				((SnippetPaletteDrawer) entry).setIconName(value.toString());
			else if (property.equals(SnippetsPlugin.NAMES.ID))
				((SnippetPaletteDrawer) entry).setId(value.toString());
			else if (property.equals(SnippetsPlugin.NAMES.LABEL))
				((SnippetPaletteDrawer) entry).setLabel(value.toString());
			else if (property.equals(SnippetsPlugin.NAMES.LARGEICON))
				((SnippetPaletteDrawer) entry).setLargeIconName(value.toString());
			else if (property.equals("filters")) //$NON-NLS-1$
				((SnippetPaletteDrawer) entry).setFilters((String[]) value);
		}
	}

	protected abstract void setProperties(SnippetPaletteDrawer category, Object source);

	protected abstract void setProperties(SnippetPaletteItem item, Object source);

	protected abstract void setProperties(SnippetVariable variable, Object source);

	protected void setProperty(SnippetPaletteDrawer category, String property, Object value) {
		if (property == null || value == null)
			return;
		else if (property.equals(SnippetsPlugin.NAMES.SHOW))
			category.setVisible(Boolean.valueOf(value.toString()).booleanValue());
		else
			setEntryProperty(category, property, value);
	}

	protected void setProperty(SnippetPaletteItem item, String property, Object value) {
		if (property == null)
			return;
		Object propertyValue = value;
		if (propertyValue == null) {
			propertyValue = ""; //$NON-NLS-1$
		}
		if (property.equals(SnippetsPlugin.NAMES.CATEGORY))
			item.setCategoryName(propertyValue.toString());
		else if (property.equals(SnippetsPlugin.NAMES.CLASSNAME))
			item.setClassName(propertyValue.toString());
		else if (property.equals(SnippetsPlugin.NAMES.CONTENT))
			item.setContentString(propertyValue.toString());
		else if (property.equals(SnippetsPlugin.NAMES.EDITORCLASSNAME))
			item.setEditorClassName(propertyValue.toString());
		else if (property.equals(SnippetsPlugin.NAMES.PROVIDER_ID))
			item.setProvider(SnippetProviderManager.findProvider(propertyValue.toString()));
		else
			setEntryProperty(item, property, propertyValue);
	}

	protected void setProperty(SnippetVariable variable, String property, Object value) {
		if (property == null)
			return;
		Object propertyValue = value;
		if (propertyValue == null) {
			propertyValue = ""; //$NON-NLS-1$
		}
		if (property.equals(SnippetsPlugin.NAMES.DEFAULT))
			variable.setDefaultValue(propertyValue.toString());
		else if (property.equals(SnippetsPlugin.NAMES.DESCRIPTION))
			variable.setDescription(propertyValue.toString());
		else if (property.equals(SnippetsPlugin.NAMES.NAME))
			variable.setName(propertyValue.toString());
		else if (property.equals(SnippetsPlugin.NAMES.ID))
			variable.setId(propertyValue.toString());
	}
}
