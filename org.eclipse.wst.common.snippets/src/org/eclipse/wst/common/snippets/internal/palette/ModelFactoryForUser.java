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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;

import org.eclipse.core.runtime.Platform;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.wst.common.snippets.core.ISnippetItem;
import org.eclipse.wst.common.snippets.core.ISnippetsEntry;
import org.eclipse.wst.common.snippets.internal.Debug;
import org.eclipse.wst.common.snippets.internal.ISnippetCategory;
import org.eclipse.wst.common.snippets.internal.ISnippetVariable;
import org.eclipse.wst.common.snippets.internal.Logger;
import org.eclipse.wst.common.snippets.internal.PluginRecord;
import org.eclipse.wst.common.snippets.internal.SnippetDefinitions;
import org.eclipse.wst.common.snippets.internal.SnippetsPlugin;
import org.eclipse.wst.common.snippets.internal.util.CommonXML;
import org.eclipse.wst.sse.core.util.StringUtils;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ModelFactoryForUser extends AbstractModelFactory {
	private static ModelFactoryForUser instance = null;

	public synchronized static ModelFactoryForUser getInstance() {
		if (instance == null)
			instance = new ModelFactoryForUser();
		return instance;
	}

	public ModelFactoryForUser() {
		super();
	}

	protected void addCategory(SnippetDefinitions definitions, Element categoryElement) {
		ISnippetCategory category = createCategory(categoryElement);
		if (category != null) {
			assignSource(category, definitions, categoryElement);
			if (category instanceof PaletteDrawer) {
				String stateString = categoryElement.getAttribute(SnippetsPlugin.NAMES.INITIAL_STATE);
				int state = PaletteDrawer.INITIAL_STATE_CLOSED;
				if (stateString != null && stateString.length() > 0) {
					try {
						state = Integer.parseInt(stateString);
					}
					catch (NumberFormatException e) {
						// leave unchanged
					}
				}
				((PaletteDrawer) category).setInitialState(state);
			}
			definitions.getCategories().add(category);
			if (Debug.debugDefinitionPersistence)
				System.out.println("Plugin reader creating category " + category.getId()); //$NON-NLS-1$
			NodeList children = categoryElement.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child != null && child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equalsIgnoreCase(SnippetsPlugin.NAMES.ITEM))
					addChild(definitions, (Element) child);
			}
		}
	}

	protected void addChild(SnippetDefinitions definitions, Element child) {
		ISnippetItem item = createItem(child);
		if (item != null) {
			assignSource(item, definitions, child);
			definitions.getItems().add(item);
			if (Debug.debugDefinitionPersistence)
				System.out.println("Plugin reader creating item " + item.getId()); //$NON-NLS-1$
		}
	}

	protected void assignSource(ISnippetsEntry entry, SnippetDefinitions definitions, Element element) {
		entry.setSourceType(ISnippetsEntry.SNIPPET_SOURCE_USER);
		((PaletteEntry) entry).setUserModificationPermission(PaletteEntry.PERMISSION_FULL_MODIFICATION);
		Object pluginRecord = createPluginRecord(definitions, element);
		if (pluginRecord != null) {
			entry.setSourceDescriptor(pluginRecord);
			((PaletteEntry) entry).setUserModificationPermission(PaletteEntry.PERMISSION_HIDE_ONLY);
			entry.setSourceType(ISnippetsEntry.SNIPPET_SOURCE_PLUGINS);
		}
		else if (element.getAttribute(SnippetsPlugin.NAMES.SHARED).equals(SnippetsPlugin.NAMES.SHARED)) {
			entry.setSourceType(ISnippetsEntry.SNIPPET_SOURCE_WORKSPACE);
		}
	}

	/**
	 * @see org.eclipse.wst.common.snippets.internal.palette.AbstractModelFactory#createCategory(java.lang.Object)
	 */
	public SnippetPaletteDrawer createCategory(Object source) {
		SnippetPaletteDrawer drawer = super.createCategory(source);
		/**
		 * MIGRATION: V5 to V5.1, com.ibm.sed.jseditor.js0 categor was
		 * removed.
		 */
		if (drawer.getId().equals("com.ibm.sed.jseditor.js0")) //$NON-NLS-1$
			return null;
		return drawer;
	}

	protected String createContent(Node item) {
		return readCDATAofChild(item, SnippetsPlugin.NAMES.CONTENT);
	}

	protected String createDescription(Node entryElement) {
		return readCDATAofChild(entryElement, SnippetsPlugin.NAMES.DESCRIPTION);
	}

	protected PluginRecord createPluginRecord(SnippetDefinitions definitions, Element element) {
		String pluginName = element.getAttribute(SnippetsPlugin.NAMES.PLUGIN);
		String pluginVersion = element.getAttribute(SnippetsPlugin.NAMES.VERSION);
		PluginRecord record = null;

		if (pluginName.length() > 0 && pluginVersion.length() > 0) {
			record = new PluginRecord();
			record.setPluginName(pluginName);
			record.setPluginVersion(pluginVersion);
		}
		return record;
	}

	protected String[] getDefaultFilters() {
		return new String[]{"*"}; //$NON-NLS-1$
	}

	public String getFilename() {
		String name = null;
		try {
			Bundle bundle = Platform.getBundle(SnippetsPlugin.BUNDLE_ID);
			name = Platform.getStateLocation(bundle).toString() + "/user.xml"; //$NON-NLS-1$ 
		}
		catch (Exception e) {
			name = "/user.xml"; //$NON-NLS-1$
		}
		return name;
	}

	protected String getID(Object source) {
		if (source instanceof Element)
			return ((Element) source).getAttribute(SnippetsPlugin.NAMES.ID);
		return null;
	}

	public SnippetDefinitions load(String filename) {
		SnippetDefinitions definitions = new SnippetDefinitions();
		Document document = null;
		try {
			DocumentBuilder builder = CommonXML.getDocumentBuilder();
			if (builder != null) {
				InputStream fis = new FileInputStream(filename);
				document = builder.parse(new InputSource(fis));
			}
			else {
				Logger.log(Logger.ERROR, "Couldn't obtain a DocumentBuilder"); //$NON-NLS-1$
			}
		}
		catch (FileNotFoundException e) {
			// typical of new workspace, don't log it
			document = null;
		}
		catch (IOException e) {
			Logger.logException("Could not load user items", e); //$NON-NLS-1$
			return definitions;
		}
		catch (SAXException e) {
			Logger.logException("Could not load user items", e); //$NON-NLS-1$
			return definitions;
		}
		if (document == null)
			return definitions;
		Element library = document.getDocumentElement();
		if (library == null || !library.getNodeName().equals(SnippetsPlugin.NAMES.SNIPPETS))
			return definitions;
		loadDefinitions(definitions, library);

		connectItemsAndCategories(definitions);

		return definitions;
	}

	public SnippetDefinitions loadCurrent() {
		return load(getFilename());
	}

	protected void loadDefinitions(SnippetDefinitions definitions, Node library) {
		NodeList children = library.getChildNodes();
		int length = children.getLength();
		Node child = null;
		for (int i = 0; i < length; i++) {
			child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				if (child.getNodeName().equals(SnippetsPlugin.NAMES.ITEM)) {
					addChild(definitions, (Element) child);
				}
				else if (child.getNodeName().equals(SnippetsPlugin.NAMES.CATEGORY)) {
					addCategory(definitions, (Element) child);
				}
			}
		}
	}

	/**
	 * @param list
	 * @return
	 */
	private Object[] nodesToArray(NodeList list) {
		Object[] objects = new Object[list.getLength()];
		for (int i = 0; i < list.getLength(); i++)
			objects[i] = list.item(i);
		return objects;
	}

	/**
	 * Extracts the contents of a CDATA section arranged like this:
	 * 
	 * <node><otherChild/><childName><wrong element/> <![CDATA[ RETURNED
	 * TEXT]]> </childName> </node>
	 */
	protected String readCDATAofChild(Node node, String childName) {
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equals(childName)) {
				NodeList descriptionChildren = child.getChildNodes();
				for (int j = 0; j < descriptionChildren.getLength(); j++) {
					Node descriptionChild = descriptionChildren.item(j);
					if (descriptionChild.getNodeType() == Node.CDATA_SECTION_NODE) {
						return descriptionChild.getNodeValue();
					}
				}
				return ""; //$NON-NLS-1$
			}
		}
		return ""; //$NON-NLS-1$
	}

	protected void setProperties(SnippetPaletteDrawer category, Object source) {
		if (!(source instanceof Element))
			return;
		Element element = ((Element) source);

		setProperty(category, SnippetsPlugin.NAMES.ICON, element.getAttribute(SnippetsPlugin.NAMES.ICON));
		setProperty(category, SnippetsPlugin.NAMES.ID, element.getAttribute(SnippetsPlugin.NAMES.ID));
		setProperty(category, SnippetsPlugin.NAMES.LABEL, element.getAttribute(SnippetsPlugin.NAMES.LABEL));
		setProperty(category, SnippetsPlugin.NAMES.LARGEICON, element.getAttribute(SnippetsPlugin.NAMES.LARGEICON));
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeName().equals(SnippetsPlugin.NAMES.DESCRIPTION))
				setProperty(category, SnippetsPlugin.NAMES.DESCRIPTION, createDescription(element));
		}

		String filtersAttr = element.getAttribute("filters"); //$NON-NLS-1$
		String[] filters = null;
		if (filtersAttr != null)
			filters = StringUtils.asArray(element.getAttribute("filters")); //$NON-NLS-1$
		else
			filters = getDefaultFilters();
		setProperty(category, "filters", filters); //$NON-NLS-1$
	}

	protected void setProperties(SnippetPaletteItem item, Object source) {
		if (!(source instanceof Element))
			return;
		Element element = ((Element) source);

		setProperty(item, SnippetsPlugin.NAMES.CATEGORY, element.getAttribute(SnippetsPlugin.NAMES.CATEGORY));
		setProperty(item, SnippetsPlugin.NAMES.CLASSNAME, element.getAttribute(SnippetsPlugin.NAMES.CLASSNAME));
		setProperty(item, SnippetsPlugin.NAMES.EDITORCLASSNAME, element.getAttribute(SnippetsPlugin.NAMES.EDITORCLASSNAME));
		setProperty(item, SnippetsPlugin.NAMES.ICON, element.getAttribute(SnippetsPlugin.NAMES.ICON));
		setProperty(item, SnippetsPlugin.NAMES.ID, element.getAttribute(SnippetsPlugin.NAMES.ID));
		setProperty(item, SnippetsPlugin.NAMES.LABEL, element.getAttribute(SnippetsPlugin.NAMES.LABEL));
		setProperty(item, SnippetsPlugin.NAMES.LARGEICON, element.getAttribute(SnippetsPlugin.NAMES.LARGEICON));

		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeName().equals(SnippetsPlugin.NAMES.DESCRIPTION))
				setProperty(item, SnippetsPlugin.NAMES.DESCRIPTION, createDescription(element));
			else if (children.item(i).getNodeName().equals(SnippetsPlugin.NAMES.CONTENT))
				setProperty(item, SnippetsPlugin.NAMES.CONTENT, createContent(element));
			else if (children.item(i).getNodeName().equals(SnippetsPlugin.NAMES.VARIABLES)) {
				Iterator variables = createVariables(nodesToArray(children.item(i).getChildNodes())).iterator();
				while (variables.hasNext()) {
					item.addVariable((ISnippetVariable) variables.next());
				}
			}
			else if (children.item(i).getNodeName().equals(SnippetsPlugin.NAMES.VARIABLE)) {
				ISnippetVariable var = createVariable(children.item(i));
				if (var != null)
					item.addVariable(var);
			}
		}
	}

	protected void setProperties(SnippetVariable variable, Object source) {
		if (!(source instanceof Element))
			return;
		Element element = ((Element) source);

		setProperty(variable, SnippetsPlugin.NAMES.DEFAULT, element.getAttribute(SnippetsPlugin.NAMES.DEFAULT));
		setProperty(variable, SnippetsPlugin.NAMES.DESCRIPTION, element.getAttribute(SnippetsPlugin.NAMES.DESCRIPTION));
		if (variable.getDescription() == null || variable.getDescription().length() == 0)
			variable.setDescription(createDescription(element));
		setProperty(variable, SnippetsPlugin.NAMES.ID, element.getAttribute(SnippetsPlugin.NAMES.ID));
		setProperty(variable, SnippetsPlugin.NAMES.NAME, element.getAttribute(SnippetsPlugin.NAMES.NAME));
	}
}