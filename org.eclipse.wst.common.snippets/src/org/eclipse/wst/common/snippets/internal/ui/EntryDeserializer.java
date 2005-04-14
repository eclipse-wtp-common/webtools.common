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


package org.eclipse.wst.common.snippets.internal.ui;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.wst.common.snippets.internal.Debug;
import org.eclipse.wst.common.snippets.internal.ISnippetVariable;
import org.eclipse.wst.common.snippets.internal.SnippetDefinitions;
import org.eclipse.wst.common.snippets.internal.SnippetsPlugin;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteItem;
import org.eclipse.wst.common.snippets.internal.palette.SnippetVariable;
import org.eclipse.wst.common.snippets.internal.provisional.ISnippetItem;
import org.eclipse.wst.common.snippets.internal.provisional.ISnippetsEntry;
import org.eclipse.wst.common.snippets.internal.util.CommonXML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class EntryDeserializer {


	private static EntryDeserializer reader = null;

	public synchronized static EntryDeserializer getInstance() {
		// cached definitions will be out of sync once new definitions are
		// written to disk
		reader = new EntryDeserializer();
		return reader;
	}

	protected EntryDeserializer() {
		super();
	}

	protected void addItem(SnippetDefinitions definitions, Element element) {
		SnippetPaletteItem item = createItem(element);
		// item.setPluginRecord(getPluginRecord(definitions, element));
		definitions.getItems().add(item);
	}

	protected void assignEntryProperties(Element element, ISnippetsEntry entry) {
		entry.setId(element.getAttribute(SnippetsPlugin.NAMES.ID));
		entry.setIconName(element.getAttribute(SnippetsPlugin.NAMES.ICON));
		// new in V5.1
		String description = createDescription(element);
		String label = element.getAttribute(SnippetsPlugin.NAMES.LABEL);
		if ((label == null || label.length() == 0) && description != null) {
			label = description;
			description = ""; //$NON-NLS-1$
		}
		entry.setDescription(description);
		entry.setLabel(label);
	}

	protected String createContent(Node item) {
		return readCDATAofChild(item, SnippetsPlugin.NAMES.CONTENT);
	}

	protected String createDescription(Node entryElement) {
		return readCDATAofChild(entryElement, SnippetsPlugin.NAMES.DESCRIPTION);
	}

	protected SnippetPaletteItem createItem(Element element) {
		SnippetPaletteItem item = new SnippetPaletteItem(element.getAttribute(SnippetsPlugin.NAMES.ID));
		assignEntryProperties(element, item);
		item.setContentString(createContent(element));
		item.setCategoryName(element.getAttribute(SnippetsPlugin.NAMES.CATEGORY));
		item.setClassName(element.getAttribute(SnippetsPlugin.NAMES.CLASSNAME));
		item.setEditorClassName(element.getAttribute(SnippetsPlugin.NAMES.EDITORCLASSNAME));
		NodeList children = element.getChildNodes();
		int length = children.getLength();
		for (int i = 0; i < length; i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equals(SnippetsPlugin.NAMES.VARIABLE)) {
				ISnippetVariable var = createVariable((Element) child);
				if (var != null)
					item.addVariable(var);
			}
		}
		if (Debug.debugDefinitionPersistence)
			System.out.println("User item reader creating item " + item.getId()); //$NON-NLS-1$
		return item;
	}

	protected ISnippetVariable createVariable(Element element) {
		SnippetVariable var = new SnippetVariable();
		var.setId(element.getAttribute(SnippetsPlugin.NAMES.ID));
		var.setName(element.getAttribute(SnippetsPlugin.NAMES.NAME));
		var.setDescription(createDescription(element));
		var.setDefaultValue(element.getAttribute(SnippetsPlugin.NAMES.DEFAULT));
		return var;
	}

	public ISnippetsEntry fromXML(byte[] xml) {
		ISnippetsEntry entry = null;
		try {
			Document document = CommonXML.getDocumentBuilder().parse(new InputSource(new ByteArrayInputStream(xml)));
			Element el = document.getDocumentElement();
			if (el != null && el.hasChildNodes()) {
				Node child = el.getFirstChild();
				while (child.getNodeType() != Node.ELEMENT_NODE)
					child = child.getNextSibling();
				if (child.getNodeName().equals(SnippetsPlugin.NAMES.ITEM)) {
					ISnippetItem item = createItem((Element) child);
					item.setCategoryName(null);
					item.setCategory(null);
					item.setClassName(null);
					item.setEditorClassName(null);
					item.setIconName(null);
					entry = item;
				}
				else if (child.getNodeName().equals(SnippetsPlugin.NAMES.CATEGORY)) {
				}
			}
		}
		catch (SAXException e) {
		}
		catch (IOException e) {
		}
		return entry;
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
						String value = descriptionChild.getNodeValue();
						if (value != null)
							return value;
					}
				}
				return ""; //$NON-NLS-1$
			}
		}
		return ""; //$NON-NLS-1$
	}

	//
	// protected void assignPluginRecordFor(ISnippetsEntry entry, Element
	// owningElement) {
	// if (entry.getPluginRecord() != null) {
	// owningElement.setAttribute(SnippetsPlugin.NAMES.PLUGIN,
	// entry.getPluginRecord().getPluginName());
	// owningElement.setAttribute(SnippetsPlugin.NAMES.VERSION,
	// entry.getPluginRecord().getPluginVersion());
	// }
	// }
	//
	// protected Element createCategory(Document doc, ISnippetCategory
	// category) {
	// Element element = doc.createElement(SnippetsPlugin.NAMES.CATEGORY);
	// assignEntryProperties(category, element);
	// assignPluginRecordFor(category, element);
	// for (int i = 0; i < category.getChildren().size(); i++) {
	// ISnippetItem item = (ISnippetItem) category.getChildren().get(i);
	// Element child = createItem(doc, item);
	// element.appendChild(child);
	// }
	// return element;
	// }
	//
	// protected Element createContent(Document doc, ISnippetItem item) {
	// Element element = doc.createElement(SnippetsPlugin.NAMES.CONTENT);
	// element.appendChild(doc.createCDATASection(item.getContentString()));
	// return element;
	// }
	//	
	// protected String readDescription(Element parent) {
	// return "";
	// }
	//
	// protected Document createDocument(SnippetDefinitions defs) {
	// Document document =
	// CommonXML.getDocumentBuilder().getDOMImplementation().createDocument(null,
	// SnippetsPlugin.NAMES.SNIPPETS, null);
	// Element root = document.getDocumentElement();
	// for (int i = 0; i < defs.getKnownPlugins().size(); i++) {
	// PluginRecord record = (PluginRecord) defs.getKnownPlugins().get(i);
	// root.appendChild(createPluginRecord(document, record));
	// }
	// for (int i = 0; i < defs.getCategories().size(); i++) {
	// ISnippetCategory category = (ISnippetCategory)
	// defs.getCategories().get(i);
	// Element categoryElement = createCategory(document, category);
	// root.appendChild(categoryElement);
	// }
	// return document;
	// }
	//
	// protected Element createItem(Document doc, ISnippetItem item) {
	// Element element = doc.createElement(SnippetsPlugin.NAMES.ITEM);
	// assignEntryProperties(item, element);
	// assignPluginRecordFor(item, element);
	// element.setAttribute(SnippetsPlugin.NAMES.CATEGORY,
	// item.getCategory().getId());
	// element.setAttribute(SnippetsPlugin.NAMES.CLASSNAME,
	// item.getClassName());
	// element.setAttribute(SnippetsPlugin.NAMES.EDITORCLASSNAME,
	// item.getEditorClassName());
	// element.appendChild(createContent(doc, item));
	// for (int i = 0; i < item.getVariables().size(); i++) {
	// Element variable = createVariable(doc, (ISnippetVariable)
	// item.getVariables().get(i));
	// element.appendChild(variable);
	// }
	// return element;
	// }
	//
	// protected Element createPluginRecord(Document doc, PluginRecord record)
	// {
	// Element element = doc.createElement(SnippetsPlugin.NAMES.PLUGIN);
	// element.setAttribute(SnippetsPlugin.NAMES.NAME,
	// record.getPluginName());
	// element.setAttribute(SnippetsPlugin.NAMES.VERSION,
	// record.getPluginVersion());
	// return element;
	// }
	//
	// protected Element createVariable(Document doc, ISnippetVariable
	// variable) {
	// Element element = doc.createElement(SnippetsPlugin.NAMES.VARIABLE);
	// element.setAttribute(SnippetsPlugin.NAMES.ID, variable.getId());
	// element.setAttribute(SnippetsPlugin.NAMES.DEFAULT,
	// variable.getDefaultValue());
	// element.appendChild(createDescription(doc, variable.getDescription()));
	// return element;
	// }

}