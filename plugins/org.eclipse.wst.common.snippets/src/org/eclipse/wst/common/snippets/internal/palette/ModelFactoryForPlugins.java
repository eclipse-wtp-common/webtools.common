/*******************************************************************************
 * Copyright (c) 2004, 2018 IBM Corporation and others.
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

import java.util.Iterator;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PluginVersionIdentifier;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.wst.common.snippets.core.ISnippetCategory;
import org.eclipse.wst.common.snippets.core.ISnippetVariable;
import org.eclipse.wst.common.snippets.core.ISnippetsEntry;
import org.eclipse.wst.common.snippets.internal.Logger;
import org.eclipse.wst.common.snippets.internal.PluginRecord;
import org.eclipse.wst.common.snippets.internal.SnippetDefinitions;
import org.eclipse.wst.common.snippets.internal.SnippetsPlugin;
import org.eclipse.wst.common.snippets.internal.util.StringUtils;
import org.osgi.framework.Bundle;

public class ModelFactoryForPlugins extends AbstractModelFactory {
	private static ModelFactoryForPlugins instance = null;

	public synchronized static ModelFactoryForPlugins getInstance() {
		if (instance == null)
			instance = new ModelFactoryForPlugins();
		return instance;
	}

	public ModelFactoryForPlugins() {
		super();
	}

	protected void addDefinitionFromExtension(SnippetDefinitions definitions, IConfigurationElement element) {
		String name = element.getName();
		if (name.equals(SnippetsPlugin.NAMES.CATEGORY)) {
			ISnippetCategory category = createCategory(element);
			if (category != null) {
				assignSource(category, definitions, element);
				definitions.getCategories().add(category);
				if (Logger.DEBUG_DEFINITION_PERSISTENCE) {
					System.out.println("Plugin reader creating category " + ((SnippetPaletteDrawer) category).getId()); //$NON-NLS-1$
				}

				// add items for category
				IConfigurationElement[] children = element.getChildren(SnippetsPlugin.NAMES.ITEM);
				for (int i = 0; i < children.length; i++) {
					IConfigurationElement child = children[i];
					SnippetPaletteItem item = createItem(child);
					if (item != null) {
						// add item's associated category
						setProperty(item, SnippetsPlugin.NAMES.CATEGORY, element.getAttribute(SnippetsPlugin.NAMES.ID));
						assignSource(item, definitions, element);
						definitions.getItems().add(item);
						if (Logger.DEBUG_DEFINITION_PERSISTENCE)
							System.out.println("Plugin reader creating item " + item.getId()); //$NON-NLS-1$
					}
				}
			}
		}
	}

	protected void assignSource(ISnippetsEntry entry, SnippetDefinitions definitions, IConfigurationElement element) {
		PluginRecord record = getPluginRecordFor(definitions, element);
		if (entry instanceof SnippetPaletteItem) {
			((SnippetPaletteItem) entry).setSourceType(ISnippetsEntry.SNIPPET_SOURCE_PLUGINS);
			((SnippetPaletteItem) entry).setSourceDescriptor(record);
		}
		if (entry instanceof SnippetPaletteDrawer) {
			((SnippetPaletteDrawer) entry).setSourceType(ISnippetsEntry.SNIPPET_SOURCE_PLUGINS);
			((SnippetPaletteDrawer) entry).setSourceDescriptor(record);
		}
		((PaletteEntry) entry).setUserModificationPermission(PaletteEntry.PERMISSION_HIDE_ONLY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.snippets.internal.palette.AbstractModelFactory#createCategory(java.lang.Object)
	 */
	public SnippetPaletteDrawer createCategory(Object source) {
		SnippetPaletteDrawer result = super.createCategory(source);
		if (result != null)
			result.setInitialState(PaletteDrawer.INITIAL_STATE_CLOSED);
		return result;
	}

	protected String[] getDefaultFilters() {
		return new String[]{"*"}; //$NON-NLS-1$
	}

	protected String getID(Object source) {
		if (source instanceof IConfigurationElement)
			return ((IConfigurationElement) source).getAttribute(SnippetsPlugin.NAMES.ID);
		return null;
	}

	protected PluginRecord getPluginRecordFor(SnippetDefinitions definitions, IConfigurationElement element) {
		String id = element.getDeclaringExtension().getNamespace();
		Bundle bundle = Platform.getBundle(id);
		String version = bundle.getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION);
		PluginVersionIdentifier identifier = new PluginVersionIdentifier(version);

		PluginRecord record = new PluginRecord();
		record.setPluginName(id);
		record.setPluginVersion(identifier.toString());
		if (Logger.DEBUG_DEFINITION_PERSISTENCE)
			System.out.println("Plugin reader creating plugin record for " + record.getPluginName() + "/" + record.getPluginVersion()); //$NON-NLS-1$ //$NON-NLS-2$
		return record;
	}

	public SnippetDefinitions loadCurrent() {
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		IExtensionPoint point = extensionRegistry.getExtensionPoint(SnippetsPlugin.BUNDLE_ID, SnippetsPlugin.NAMES.EXTENSION_POINT_ID);
		SnippetDefinitions definitions = new SnippetDefinitions();
		if (point != null) {
			IConfigurationElement[] elements = point.getConfigurationElements();
			for (int i = 0; i < elements.length; i++) {
				addDefinitionFromExtension(definitions, elements[i]);
			}
		}

		connectItemsAndCategories(definitions);

		return definitions;
	}

	protected void setProperties(SnippetPaletteDrawer category, Object source) {
		if (!(source instanceof IConfigurationElement))
			return;
		IConfigurationElement element = ((IConfigurationElement) source);

		setProperty(category, SnippetsPlugin.NAMES.DESCRIPTION, element.getAttribute(SnippetsPlugin.NAMES.DESCRIPTION));
		setProperty(category, SnippetsPlugin.NAMES.SMALLICON, element.getAttribute(SnippetsPlugin.NAMES.SMALLICON));
		setProperty(category, SnippetsPlugin.NAMES.ID, element.getAttribute(SnippetsPlugin.NAMES.ID));
		setProperty(category, SnippetsPlugin.NAMES.LABEL, element.getAttribute(SnippetsPlugin.NAMES.LABEL));
		setProperty(category, SnippetsPlugin.NAMES.LARGEICON, element.getAttribute(SnippetsPlugin.NAMES.LARGEICON));

		String filtersAttr = element.getAttribute("filters"); //$NON-NLS-1$
		String[] filters = null;
		if (filtersAttr != null)
			filters = StringUtils.asArray(element.getAttribute("filters")); //$NON-NLS-1$
		else
			filters = getDefaultFilters();
		setProperty(category, "filters", filters); //$NON-NLS-1$
	}

	protected void setProperties(SnippetPaletteItem item, Object source) {
		if (!(source instanceof IConfigurationElement))
			return;
		IConfigurationElement element = ((IConfigurationElement) source);

		setProperty(item, SnippetsPlugin.NAMES.CATEGORY, element.getAttribute(SnippetsPlugin.NAMES.CATEGORY));
		setProperty(item, SnippetsPlugin.NAMES.CLASSNAME, element.getAttribute(SnippetsPlugin.NAMES.CLASSNAME));
		setProperty(item, SnippetsPlugin.NAMES.DESCRIPTION, element.getAttribute(SnippetsPlugin.NAMES.DESCRIPTION));
		setProperty(item, SnippetsPlugin.NAMES.EDITORCLASSNAME, element.getAttribute(SnippetsPlugin.NAMES.EDITORCLASSNAME));
		setProperty(item, SnippetsPlugin.NAMES.SMALLICON, element.getAttribute(SnippetsPlugin.NAMES.SMALLICON));
		setProperty(item, SnippetsPlugin.NAMES.ID, element.getAttribute(SnippetsPlugin.NAMES.ID));
		setProperty(item, SnippetsPlugin.NAMES.LABEL, element.getAttribute(SnippetsPlugin.NAMES.LABEL));
		setProperty(item, SnippetsPlugin.NAMES.LARGEICON, element.getAttribute(SnippetsPlugin.NAMES.LARGEICON));

		IConfigurationElement[] children = element.getChildren();
		for (int i = 0; i < children.length; i++) {
			if (children[i].getName().equals(SnippetsPlugin.NAMES.CONTENT))
				setProperty(item, SnippetsPlugin.NAMES.CONTENT, children[i].getValue());
			else if (children[i].getName().equals(SnippetsPlugin.NAMES.VARIABLES)) {
				Iterator iterator = createVariables(children[i].getChildren()).iterator();
				while (iterator.hasNext()) {
					item.addVariable((ISnippetVariable) iterator.next());
				}
			}
			else if (children[i].getName().equals(SnippetsPlugin.NAMES.VARIABLE)) {
				ISnippetVariable var = createVariable(children[i]);
				if (var != null)
					item.addVariable(var);
			}
		}
	}

	protected void setProperties(SnippetVariable variable, Object source) {
		if (!(source instanceof IConfigurationElement))
			return;
		IConfigurationElement element = ((IConfigurationElement) source);
		setProperty(variable, SnippetsPlugin.NAMES.DEFAULT, element.getAttribute(SnippetsPlugin.NAMES.DEFAULT));
		setProperty(variable, SnippetsPlugin.NAMES.DESCRIPTION, element.getAttribute(SnippetsPlugin.NAMES.DESCRIPTION));
		setProperty(variable, SnippetsPlugin.NAMES.ID, element.getAttribute(SnippetsPlugin.NAMES.ID));
		setProperty(variable, SnippetsPlugin.NAMES.NAME, element.getAttribute(SnippetsPlugin.NAMES.NAME));
	}

}
