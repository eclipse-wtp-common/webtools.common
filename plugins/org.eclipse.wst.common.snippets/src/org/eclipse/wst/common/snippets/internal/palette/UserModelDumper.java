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

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.wst.common.snippets.core.ISnippetCategory;
import org.eclipse.wst.common.snippets.core.ISnippetItem;
import org.eclipse.wst.common.snippets.core.ISnippetVariable;
import org.eclipse.wst.common.snippets.core.ISnippetsEntry;
import org.eclipse.wst.common.snippets.internal.Logger;
import org.eclipse.wst.common.snippets.internal.PluginRecord;
import org.eclipse.wst.common.snippets.internal.SnippetDefinitions;
import org.eclipse.wst.common.snippets.internal.SnippetsPlugin;
import org.eclipse.wst.common.snippets.internal.util.CommonXML;
import org.eclipse.wst.common.snippets.internal.util.StringUtils;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class UserModelDumper {

	private static UserModelDumper dumper = null;

	public synchronized static UserModelDumper getInstance() {
		if (dumper == null) {
			dumper = new UserModelDumper();
		}
		return dumper;
	}

	protected UserModelDumper() {
		super();
	}

	/**
	 * Save the properties known for ISnippetsEntry
	 */
	protected void assignEntryProperties(ISnippetsEntry entry, Element owningElement) {
		if (entry instanceof SnippetPaletteDrawer) {
			owningElement.setAttribute(SnippetsPlugin.NAMES.ID, ((SnippetPaletteDrawer) entry).getId());
			if (((SnippetPaletteDrawer) entry).getSmallIconName() != null)
				owningElement.setAttribute(SnippetsPlugin.NAMES.SMALLICON, ((SnippetPaletteDrawer) entry).getSmallIconName());
			if (entry.getLabel() != null)
				owningElement.setAttribute(SnippetsPlugin.NAMES.LABEL, entry.getLabel());
			if (((SnippetPaletteDrawer) entry).getLargeIconName() != null)
				owningElement.setAttribute(SnippetsPlugin.NAMES.LARGEICON, ((SnippetPaletteDrawer) entry).getLargeIconName());
		}
		if (entry instanceof SnippetPaletteItem) {
			owningElement.setAttribute(SnippetsPlugin.NAMES.ID, ((SnippetPaletteItem) entry).getId());
			if (((SnippetPaletteItem) entry).getSmallIconName() != null)
				owningElement.setAttribute(SnippetsPlugin.NAMES.SMALLICON, ((SnippetPaletteItem) entry).getSmallIconName());
			if (entry.getLabel() != null)
				owningElement.setAttribute(SnippetsPlugin.NAMES.LABEL, entry.getLabel());
			if (((SnippetPaletteItem) entry).getLargeIconName() != null)
				owningElement.setAttribute(SnippetsPlugin.NAMES.LARGEICON, ((SnippetPaletteItem) entry).getLargeIconName());
		}
		owningElement.appendChild(createDescription(owningElement.getOwnerDocument(), entry.getDescription()));
	}

	/**
	 * If the ISnippetsEntry has a PluginRecord, save the plugin information
	 */
	protected void assignSourceFor(ISnippetsEntry entry, Element owningElement) {
		if (entry.getSourceType() == ISnippetsEntry.SNIPPET_SOURCE_PLUGINS) {
			PluginRecord record = (PluginRecord) entry.getSourceDescriptor();
			owningElement.setAttribute(SnippetsPlugin.NAMES.PLUGIN, record.getPluginName());
			owningElement.setAttribute(SnippetsPlugin.NAMES.VERSION, record.getPluginVersion());
		}
		else if (entry.getSourceType() == ISnippetsEntry.SNIPPET_SOURCE_WORKSPACE) {
			owningElement.setAttribute(SnippetsPlugin.NAMES.SHARED, SnippetsPlugin.NAMES.SHARED);
		}
	}

	/**
	 * Create and save the properties known for LibraryCategories
	 */
	protected Element createCategory(Document doc, ISnippetCategory category) {
		Element element = doc.createElement(SnippetsPlugin.NAMES.CATEGORY);
		assignSourceFor(category, element);
		// if the category came from a plugin, only store a placeholder
		// to maintain the ordering [it will be reloaded in subsequent
		// sessions directly from the plugin definitions]
		element.setAttribute(SnippetsPlugin.NAMES.ID, ((SnippetPaletteDrawer) category).getId());
		if (category instanceof PaletteDrawer) {
			element.setAttribute(SnippetsPlugin.NAMES.INITIAL_STATE, Integer.toString(((PaletteDrawer) category).getInitialState()));
		}
		String[] filters = category.getFilters();
		if (filters.length > 0) {
			String filtersAttr = filters[0];
			if (filters.length > 1) {
				for (int i = 1; i < filters.length; i++) {
					filtersAttr += " " + filters[i]; //$NON-NLS-1$
				}
			}
			element.setAttribute("filters", filtersAttr); //$NON-NLS-1$
		}
		if (category.getSourceType() == ISnippetsEntry.SNIPPET_SOURCE_USER) {
			assignEntryProperties(category, element);

			for (int i = 0; i < category.getItems().length; i++) {
				ISnippetItem item = category.getItems()[i];
				Element child = createItem(doc, item);
				element.appendChild(child);
			}
		}

		if (Logger.DEBUG_DEFINITION_PERSISTENCE)
			System.out.println("User item writer saving category " + ((SnippetPaletteDrawer) category).getId()); //$NON-NLS-1$
		return element;
	}

	/**
	 * Create and save the content property of a ISnippetItem - always place
	 * it in a CDATA section for consistency
	 */
	protected Element createContent(Document doc, ISnippetItem item) {
		Element element = doc.createElement(SnippetsPlugin.NAMES.CONTENT);
		String contents = item.getContentString();
		if (contents != null && contents.length() > 0) {
			/*
			 * EOL translation
			 * (https://bugs.eclipse.org/bugs/show_bug.cgi?id=102941).
			 * Normalize to '\n'.
			 */
			contents = StringUtils.replace(contents, "\r\n", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
			contents = StringUtils.replace(contents, "\r", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
			element.appendChild(doc.createCDATASection(contents));
		}
		return element;
	}

	/**
	 * Create and save the content property of a ISnippetItem - always place
	 * it in a CDATA section for consistency
	 */
	protected Element createDescription(Document doc, String description) {
		Element element = doc.createElement(SnippetsPlugin.NAMES.DESCRIPTION);
		if (description != null && description.length() > 0)
			element.appendChild(doc.createCDATASection(description));
		return element;
	}

	protected Document createDocument(SnippetDefinitions defs) {
		Document document = CommonXML.getDocumentBuilder().getDOMImplementation().createDocument(null, SnippetsPlugin.NAMES.SNIPPETS, null);
		Element root = document.getDocumentElement();
		for (int i = 0; i < defs.getCategories().size(); i++) {
			ISnippetCategory category = defs.getCategories().get(i);
			Element categoryElement = createCategory(document, category);
			root.appendChild(categoryElement);
		}
		return document;
	}

	/**
	 * Create and save the properties known for LibraryItems
	 */
	protected Element createItem(Document doc, ISnippetItem item) {
		Element element = doc.createElement(SnippetsPlugin.NAMES.ITEM);
		assignEntryProperties(item, element);
		assignSourceFor(item, element);
		element.setAttribute(SnippetsPlugin.NAMES.CATEGORY, ((SnippetPaletteDrawer) item.getCategory()).getId());
		if (((SnippetPaletteItem) item).getClassName() != null)
			element.setAttribute(SnippetsPlugin.NAMES.CLASSNAME, ((SnippetPaletteItem) item).getClassName());
		if (((SnippetPaletteItem) item).getEditorClassName() != null)
			element.setAttribute(SnippetsPlugin.NAMES.EDITORCLASSNAME, ((SnippetPaletteItem) item).getEditorClassName());
		if (((SnippetPaletteItem) item).getProvider() != null)
			element.setAttribute(SnippetsPlugin.NAMES.PROVIDER_ID, ((SnippetPaletteItem) item).getProvider().getId());
		element.appendChild(createContent(doc, item));
		ISnippetVariable[] variables = item.getVariables();
		for (int i = 0; i < variables.length; i++) {
			Element variable = createVariable(doc, variables[i]);
			element.appendChild(variable);
		}
		if (Logger.DEBUG_DEFINITION_PERSISTENCE)
			System.out.println("User item writer saving item " + ((SnippetPaletteDrawer) item.getCategory()).getId() + ":" + ((SnippetPaletteItem) item).getId()); //$NON-NLS-1$ //$NON-NLS-2$
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
		if (Logger.DEBUG_DEFINITION_PERSISTENCE)
			System.out.println("User item writer saving plugin record " + record.getPluginName() + "/" + record.getPluginVersion()); //$NON-NLS-1$ //$NON-NLS-2$
		return element;
	}

	/**
	 * Create and save the values for a ISnippetVariable
	 */
	protected Element createVariable(Document doc, ISnippetVariable variable) {
		Element element = doc.createElement(SnippetsPlugin.NAMES.VARIABLE);
		element.setAttribute(SnippetsPlugin.NAMES.ID, ((SnippetVariable) variable).getId());
		if (variable.getName() != null)
			element.setAttribute(SnippetsPlugin.NAMES.NAME, variable.getName());
		if (variable.getDefaultValue() != null)
			element.setAttribute(SnippetsPlugin.NAMES.DEFAULT, variable.getDefaultValue());
		element.appendChild(createDescription(doc, variable.getDescription()));
		return element;
	}

	protected String getFilename() {
		String name = null;
		try {
			Bundle bundle = Platform.getBundle(SnippetsPlugin.BUNDLE_ID);
			name = Platform.getStateLocation(bundle).toOSString() + "/user.xml"; //$NON-NLS-1$
		}
		catch (Exception e) {
			name = "/user.xml"; //$NON-NLS-1$
		}
		return name;
	}

	public String toXML(ISnippetsEntry entry) {
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
		catch (IOException e) {
			return e.getMessage();
		}
		String retVal = null;
		try {
			retVal = new String(output.toByteArray(), "utf16"); //$NON-NLS-1$
		}
		catch (UnsupportedEncodingException e1) {
			retVal = new String(output.toByteArray());
		}
		return retVal;
	}

	public void write(SnippetDefinitions definitions) {
		try {
			FileOutputStream ostream = new FileOutputStream(getFilename());
			write(definitions, ostream);
		}
		catch (IOException e) {
			Logger.logException("could not save " + getFilename(), e); //$NON-NLS-1$
		}
	}

	public void write(SnippetDefinitions definitions, OutputStream stream) {
		Document document = createDocument(definitions);
		try {
			CommonXML.serialize(document, stream);
			stream.flush();
		}
		catch (IOException e) {
			Logger.log(Logger.ERROR, "could not save " + stream, e); //$NON-NLS-1$
		}
		finally {
			//				stream.close();
			document = null;
		}
	}

}
