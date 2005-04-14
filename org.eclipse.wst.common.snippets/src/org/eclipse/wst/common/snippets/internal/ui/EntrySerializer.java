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

import java.io.ByteArrayOutputStream;

import org.eclipse.wst.common.snippets.internal.Debug;
import org.eclipse.wst.common.snippets.internal.ISnippetCategory;
import org.eclipse.wst.common.snippets.internal.ISnippetVariable;
import org.eclipse.wst.common.snippets.internal.Logger;
import org.eclipse.wst.common.snippets.internal.PluginRecord;
import org.eclipse.wst.common.snippets.internal.SnippetsPlugin;
import org.eclipse.wst.common.snippets.internal.provisional.ISnippetItem;
import org.eclipse.wst.common.snippets.internal.provisional.ISnippetsEntry;
import org.eclipse.wst.common.snippets.internal.util.CommonXML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class EntrySerializer {

	private static EntrySerializer writer = null;

	public synchronized static EntrySerializer getInstance() {
		if (writer == null) {
			writer = new EntrySerializer();
		}
		return writer;
	}

	protected EntrySerializer() {
		super();
	}

	/**
	 * Save the properties known for ISnippetsEntry
	 */
	protected void assignEntryProperties(ISnippetsEntry entry, Element owningElement) {
		owningElement.setAttribute(SnippetsPlugin.NAMES.ID, entry.getId());
		if (entry.getIconName() != null)
			owningElement.setAttribute(SnippetsPlugin.NAMES.ICON, entry.getIconName());
		if (entry.getDescription() != null)
			owningElement.appendChild(createDescription(owningElement.getOwnerDocument(), entry.getDescription()));
		// new in V5.1
		if (entry.getLabel() != null)
			owningElement.setAttribute(SnippetsPlugin.NAMES.LABEL, entry.getLabel());
	}

	/**
	 * Create and save the properties known for LibraryCategories
	 */
	protected Element createCategory(Document doc, ISnippetCategory category) {
		Element element = doc.createElement(SnippetsPlugin.NAMES.CATEGORY);
		assignEntryProperties(category, element);
		for (int i = 0; i < category.getChildren().size(); i++) {
			ISnippetItem item = (ISnippetItem) category.getChildren().get(i);
			Element child = createItem(doc, item);
			element.appendChild(child);
		}
		if (Debug.debugDefinitionPersistence)
			System.out.println("User item writer saving category " + category.getId()); //$NON-NLS-1$
		return element;
	}

	/**
	 * Create and save the content property of a ISnippetItem - always place
	 * it in a CDATA section for consistency
	 */
	protected Element createContent(Document doc, ISnippetItem item) {
		Element element = doc.createElement(SnippetsPlugin.NAMES.CONTENT);
		element.appendChild(doc.createCDATASection(item.getContentString()));
		return element;
	}

	/**
	 * Create and save the content property of a ISnippetItem - always place
	 * it in a CDATA section for consistency
	 */
	protected Element createDescription(Document doc, String description) {
		Element element = doc.createElement(SnippetsPlugin.NAMES.DESCRIPTION);
		if (description != null)
			element.appendChild(doc.createCDATASection(description));
		else
			element.appendChild(doc.createCDATASection("")); //$NON-NLS-1$
		return element;
	}

	/**
	 * Create and save the properties known for LibraryItems
	 */
	protected Element createItem(Document doc, ISnippetItem item) {
		Element element = doc.createElement(SnippetsPlugin.NAMES.ITEM);
		assignEntryProperties(item, element);
		// JAXP is very picky about null values
		if (item.getCategory() != null)
			element.setAttribute(SnippetsPlugin.NAMES.CATEGORY, item.getCategory().getId());
		if (item.getClassName() != null)
			element.setAttribute(SnippetsPlugin.NAMES.CLASSNAME, item.getClassName());
		if (item.getEditorClassName() != null)
			element.setAttribute(SnippetsPlugin.NAMES.EDITORCLASSNAME, item.getEditorClassName());
		if (item.getContentString() != null)
			element.appendChild(createContent(doc, item));
		ISnippetVariable[] variables = item.getVariables();
		for (int i = 0; i < variables.length; i++) {
			Element variable = createVariable(doc, variables[i]);
			element.appendChild(variable);
		}
		if (Debug.debugDefinitionPersistence)
			System.out.println("User item writer saving item " + item.getCategory().getId() + ":" + item.getId()); //$NON-NLS-1$ //$NON-NLS-2$
		return element;
	}

	/**
	 * Save the list of plugins already seen as there may no longer be
	 * references to them in the remaining categories and items
	 */
	protected Element createPluginRecord(Document doc, PluginRecord record) {
		Element element = doc.createElement(SnippetsPlugin.NAMES.PLUGIN);
		element.setAttribute(SnippetsPlugin.NAMES.NAME, record.getPluginName());
		element.setAttribute(SnippetsPlugin.NAMES.VERSION, record.getPluginVersion());
		if (Debug.debugDefinitionPersistence)
			System.out.println("User item writer saving plugin record " + record.getPluginName() + "/" + record.getPluginVersion()); //$NON-NLS-1$ //$NON-NLS-2$
		return element;
	}

	/**
	 * Create and save the values for a ISnippetVariable
	 */
	protected Element createVariable(Document doc, ISnippetVariable variable) {
		Element element = doc.createElement(SnippetsPlugin.NAMES.VARIABLE);
		element.setAttribute(SnippetsPlugin.NAMES.ID, variable.getId());
		if (variable.getName() != null)
			element.setAttribute(SnippetsPlugin.NAMES.NAME, variable.getName());
		if (variable.getDefaultValue() != null)
			element.setAttribute(SnippetsPlugin.NAMES.DEFAULT, variable.getDefaultValue());
		if (variable.getDescription() != null)
			element.appendChild(createDescription(doc, variable.getDescription()));
		return element;
	}

	public byte[] toXML(ISnippetsEntry entry) {
		Document document = CommonXML.getDocumentBuilder().getDOMImplementation().createDocument(null, SnippetsPlugin.NAMES.SNIPPETS, null);
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		if (entry instanceof ISnippetItem) {
			ISnippetItem item = (ISnippetItem) entry;
			Element itemElement = createItem(document, item);
			document.getDocumentElement().appendChild(itemElement);
		}
		else {
			ISnippetCategory category = (ISnippetCategory) entry;
			Element categoryElement = createCategory(document, category);
			document.getDocumentElement().appendChild(categoryElement);
		}

		try {
			CommonXML.serialize(document, output);
		}
		catch (Exception e) {
			Logger.logException(e);
		}
		return output.toByteArray();
	}

}