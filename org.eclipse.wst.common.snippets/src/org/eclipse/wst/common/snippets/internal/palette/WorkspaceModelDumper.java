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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
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


public class WorkspaceModelDumper {

	private static WorkspaceModelDumper dumper = null;

	public synchronized static WorkspaceModelDumper getInstance() {
		if (dumper == null) {
			dumper = new WorkspaceModelDumper();
		}
		return dumper;
	}

	protected WorkspaceModelDumper() {
		super();
	}

	/**
	 * Save the properties known for ISnippetsEntry
	 */
	protected void assignEntryProperties(ISnippetsEntry entry, Element owningElement) {
		owningElement.setAttribute(SnippetsPlugin.NAMES.ID, entry.getId());
		owningElement.setAttribute(SnippetsPlugin.NAMES.ICON, entry.getIconName());
		owningElement.appendChild(createDescription(owningElement.getOwnerDocument(), entry.getDescription()));
		// new in V5.1
		owningElement.setAttribute(SnippetsPlugin.NAMES.LABEL, entry.getLabel());
		owningElement.setAttribute(SnippetsPlugin.NAMES.LARGEICON, entry.getLargeIconName());
	}

	/**
	 * If the ISnippetsEntry has a PluginRecord, save the plugin information
	 */
	protected void assignSourceFor(ISnippetsEntry entry, Element owningElement) {
		if (entry.getSourceType() == ISnippetsEntry.SNIPPET_SOURCE_WORKSPACE) {
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
		element.setAttribute(SnippetsPlugin.NAMES.ID, category.getId());
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
		if (category.getSourceType() == ISnippetsEntry.SNIPPET_SOURCE_WORKSPACE) {
			assignEntryProperties(category, element);

			for (int i = 0; i < category.getChildren().size(); i++) {
				ISnippetItem item = (ISnippetItem) category.getChildren().get(i);
				Element child = createItem(doc, item);
				element.appendChild(child);
			}
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

	protected Document createDocument(ISnippetCategory category) {
		Document document = CommonXML.getDocumentBuilder().getDOMImplementation().createDocument(null, SnippetsPlugin.NAMES.SNIPPETS, null);
		Element root = document.getDocumentElement();
		Element categoryElement = createCategory(document, category);
		root.appendChild(categoryElement);
		return document;
	}

	/**
	 * Create and save the properties known for LibraryItems
	 */
	protected Element createItem(Document doc, ISnippetItem item) {
		Element element = doc.createElement(SnippetsPlugin.NAMES.ITEM);
		assignEntryProperties(item, element);
		assignSourceFor(item, element);
		element.setAttribute(SnippetsPlugin.NAMES.CATEGORY, item.getCategory().getId());
		element.setAttribute(SnippetsPlugin.NAMES.CLASSNAME, item.getClassName());
		element.setAttribute(SnippetsPlugin.NAMES.EDITORCLASSNAME, item.getEditorClassName());
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
		element.setAttribute(SnippetsPlugin.NAMES.NAME, variable.getName());
		element.setAttribute(SnippetsPlugin.NAMES.DEFAULT, variable.getDefaultValue());
		element.appendChild(createDescription(doc, variable.getDescription()));
		return element;
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

	// nsd_TODO: set a valid encoding
	public void write(ISnippetCategory category, IFile file) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		write(category, output);
		byte[] array = output.toByteArray();
		try {
			file.setContents(new ByteArrayInputStream(array), false, false, new NullProgressMonitor());
		}
		catch (CoreException e) {
			Logger.logException(e);
		}
	}

	public void write(ISnippetCategory category, OutputStream ostream) {
		Document document = createDocument(category);
		try {
			CommonXML.serialize(document, ostream); //$NON-NLS-1$
			ostream.flush();
		}
		catch (IOException e) {
			Logger.log(Logger.ERROR, "could not save " + ostream, e); //$NON-NLS-1$
		}
		finally {
			try {
				ostream.close();
				document = null;
			}
			catch (IOException e) {
				// do nothing
			}
		}
	}

}