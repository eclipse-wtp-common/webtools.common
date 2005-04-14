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
package org.eclipse.wst.common.snippets.internal.palette;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.wst.common.snippets.internal.ISnippetCategory;
import org.eclipse.wst.common.snippets.internal.ISnippetVariable;
import org.eclipse.wst.common.snippets.internal.Logger;
import org.eclipse.wst.common.snippets.internal.SnippetDefinitions;
import org.eclipse.wst.common.snippets.internal.SnippetsPlugin;
import org.eclipse.wst.common.snippets.internal.provisional.ISnippetItem;
import org.eclipse.wst.common.snippets.internal.provisional.ISnippetsEntry;


public abstract class AbstractModelFactory {

	public AbstractModelFactory() {
		super();
	}

	protected void connectItemsAndCategories(SnippetDefinitions definitions) {
		Iterator iterator = definitions.getCategories().iterator();
		while (iterator.hasNext()) {
			ISnippetCategory category = (ISnippetCategory) iterator.next();
			category.getChildren().clear();
		}

		iterator = definitions.getItems().iterator();
		while (iterator.hasNext()) {
			ISnippetItem item = (ISnippetItem) iterator.next();
			ISnippetCategory parentCategory = definitions.getCategory(item.getCategoryName());
			if (parentCategory != null) {
				parentCategory.add(item);
			}
			else {
				Logger.log(Logger.WARNING, "Rejecting item " + item.getId() + " in missing category " + item.getCategoryName()); //$NON-NLS-1$ //$NON-NLS-2$
				iterator.remove();
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

	protected List createVariables(Object[] sources) {
		if (sources == null || sources.length < 1)
			return null;
		List variables = new ArrayList(sources.length);
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
		if (entry.getDescription() != null && entry.getDescription().length() > 0 && (entry.getLabel() == null || entry.getLabel().length() == 0 || entry.getId().equals(entry.getLabel()))) {
			entry.setLabel(entry.getDescription());
			entry.setDescription(""); //$NON-NLS-1$
		}
	}

	protected void setEntryProperty(ISnippetsEntry entry, String property, Object value) {
		if (property == null || value == null)
			return;
		else if (property.equals(SnippetsPlugin.NAMES.DESCRIPTION))
			entry.setDescription(value.toString());
		else if (property.equals(SnippetsPlugin.NAMES.ICON))
			entry.setIconName(value.toString());
		else if (property.equals(SnippetsPlugin.NAMES.ID))
			entry.setId(value.toString());
		else if (property.equals(SnippetsPlugin.NAMES.LABEL))
			entry.setLabel(value.toString());
		else if (property.equals(SnippetsPlugin.NAMES.LARGEICON))
			entry.setLargeIconName(value.toString());
		else if (property.equals("filters")) //$NON-NLS-1$
			entry.setFilters((String[]) value);
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
		if (property == null || value == null)
			return;
		else if (property.equals(SnippetsPlugin.NAMES.CATEGORY))
			item.setCategoryName(value.toString());
		else if (property.equals(SnippetsPlugin.NAMES.CLASSNAME))
			item.setClassName(value.toString());
		else if (property.equals(SnippetsPlugin.NAMES.CONTENT))
			item.setContentString(value.toString());
		else if (property.equals(SnippetsPlugin.NAMES.EDITORCLASSNAME))
			item.setEditorClassName(value.toString());
		else
			setEntryProperty(item, property, value);
	}

	protected void setProperty(SnippetVariable variable, String property, Object value) {
		if (property == null || value == null)
			return;
		else if (property.equals(SnippetsPlugin.NAMES.DEFAULT))
			variable.setDefaultValue(value.toString());
		else if (property.equals(SnippetsPlugin.NAMES.DESCRIPTION))
			variable.setDescription(value.toString());
		else if (property.equals(SnippetsPlugin.NAMES.NAME))
			variable.setName(value.toString());
		else if (property.equals(SnippetsPlugin.NAMES.ID))
			variable.setId(value.toString());
	}
}