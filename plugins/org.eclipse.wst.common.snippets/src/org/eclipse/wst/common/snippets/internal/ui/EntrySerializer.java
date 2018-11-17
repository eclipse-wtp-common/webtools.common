/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
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


package org.eclipse.wst.common.snippets.internal.ui;

import java.io.ByteArrayOutputStream;

import org.eclipse.wst.common.snippets.core.ISnippetItem;
import org.eclipse.wst.common.snippets.core.ISnippetVariable;
import org.eclipse.wst.common.snippets.core.ISnippetsEntry;
import org.eclipse.wst.common.snippets.internal.Logger;
import org.eclipse.wst.common.snippets.internal.SnippetsPlugin;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteDrawer;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteItem;
import org.eclipse.wst.common.snippets.internal.palette.SnippetVariable;
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

	private EntrySerializer() {
		super();
	}

	/**
	 * Save the properties known for ISnippetsEntry
	 */
	private void assignEntryProperties(ISnippetsEntry entry, Element owningElement) {
		if (entry instanceof SnippetPaletteItem) {
			SnippetPaletteItem item = (SnippetPaletteItem) entry;
			owningElement.setAttribute(SnippetsPlugin.NAMES.ID, item.getId());
			if (item.getSmallIconName() != null)
				owningElement.setAttribute(SnippetsPlugin.NAMES.SMALLICON, item.getSmallIconName());
		}
		if (entry instanceof SnippetPaletteDrawer) {
			SnippetPaletteDrawer drawer = (SnippetPaletteDrawer) entry;
			owningElement.setAttribute(SnippetsPlugin.NAMES.ID, drawer.getId());
			if (drawer.getSmallIconName() != null)
				owningElement.setAttribute(SnippetsPlugin.NAMES.SMALLICON, drawer.getSmallIconName());
		}
		if (entry.getDescription() != null)
			owningElement.appendChild(createDescription(owningElement.getOwnerDocument(), entry.getDescription()));
		if (entry.getLabel() != null)
			owningElement.setAttribute(SnippetsPlugin.NAMES.LABEL, entry.getLabel());
	}

	/**
	 * Create and save the properties known for Snippet Categories
	 */
	private Element createCategory(Document doc, SnippetPaletteDrawer category) {
		Element element = doc.createElement(SnippetsPlugin.NAMES.CATEGORY);
		assignEntryProperties(category, element);
		for (int i = 0; i < category.getChildren().size(); i++) {
			SnippetPaletteItem item = (SnippetPaletteItem) category.getChildren().get(i);
			Element child = createItem(doc, item);
			element.appendChild(child);
		}
		if (Logger.DEBUG_DEFINITION_PERSISTENCE)
			System.out.println("User item writer saving category " + category.getId()); //$NON-NLS-1$
		return element;
	}

	/**
	 * Create and save the content property of a ISnippetItem - always place
	 * it in a CDATA section for consistency
	 */
	private Element createContent(Document doc, ISnippetItem item) {
		Element element = doc.createElement(SnippetsPlugin.NAMES.CONTENT);
		element.appendChild(doc.createCDATASection(item.getContentString()));
		return element;
	}

	/**
	 * Create and save the content property of a ISnippetItem - always place
	 * it in a CDATA section for consistency
	 */
	private Element createDescription(Document doc, String description) {
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
	private Element createItem(Document doc, SnippetPaletteItem item) {
		Element element = doc.createElement(SnippetsPlugin.NAMES.ITEM);
		assignEntryProperties(item, element);
		// JAXP is very picky about null values
		if (item.getCategory() != null)
			element.setAttribute(SnippetsPlugin.NAMES.CATEGORY, ((SnippetPaletteDrawer) item.getCategory()).getId());
		if (item.getClassName() != null)
			element.setAttribute(SnippetsPlugin.NAMES.CLASSNAME, item.getClassName());
		if (item.getEditorClassName() != null)
			element.setAttribute(SnippetsPlugin.NAMES.EDITORCLASSNAME, item.getEditorClassName());
		if (item.getProvider() != null)
			element.setAttribute(SnippetsPlugin.NAMES.PROVIDER_ID, item.getProvider().getId());
		if (item.getContentString() != null)
			element.appendChild(createContent(doc, item));
		ISnippetVariable[] variables = item.getVariables();
		for (int i = 0; i < variables.length; i++) {
			Element variable = createVariable(doc, variables[i]);
			element.appendChild(variable);
		}
		if (Logger.DEBUG_DEFINITION_PERSISTENCE)
			System.out.println("User item writer saving item " + ((SnippetPaletteDrawer) item.getCategory()).getId() + ":" + item.getId()); //$NON-NLS-1$ //$NON-NLS-2$
		return element;
	}

	/**
	 * Create and save the values for a ISnippetVariable
	 */
	private Element createVariable(Document doc, ISnippetVariable variable) {
		Element element = doc.createElement(SnippetsPlugin.NAMES.VARIABLE);
		element.setAttribute(SnippetsPlugin.NAMES.ID, ((SnippetVariable) variable).getId());
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
			SnippetPaletteItem item = (SnippetPaletteItem) entry;
			Element itemElement = createItem(document, item);
			document.getDocumentElement().appendChild(itemElement);
		}
		else {
			SnippetPaletteDrawer category = (SnippetPaletteDrawer) entry;
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
