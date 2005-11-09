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
package org.eclipse.wst.common.snippets.internal.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;

import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.wst.common.snippets.core.ISnippetCategory;
import org.eclipse.wst.common.snippets.core.ISnippetsEntry;
import org.eclipse.wst.common.snippets.internal.IEntryChangeListener;
import org.eclipse.wst.common.snippets.internal.ISnippetManager;
import org.eclipse.wst.common.snippets.internal.Logger;
import org.eclipse.wst.common.snippets.internal.SnippetDefinitions;
import org.eclipse.wst.common.snippets.internal.SnippetsPlugin;
import org.eclipse.wst.common.snippets.internal.palette.ModelFactoryForPlugins;
import org.eclipse.wst.common.snippets.internal.palette.ModelFactoryForUser;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteDrawer;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteItem;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteRoot;
import org.eclipse.wst.common.snippets.internal.palette.UserModelDumper;
import org.eclipse.wst.common.snippets.internal.palette.WorkspaceModelDumper;
import org.eclipse.wst.common.snippets.internal.team.CategoryFileInfo;
import org.eclipse.wst.common.snippets.internal.util.CommonXML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * 
 * SnippetManager
 * 
 * Manages the Library model across multiple views
 */
public class SnippetManager implements ISnippetManager, PropertyChangeListener {

	protected static String hiddenStateFilename = "hidden.xml"; //$NON-NLS-1$

	private static SnippetManager instance = null;

	public synchronized static SnippetManager getInstance() {
		if (instance == null) {
			instance = new SnippetManager();
			try {
				hiddenStateFilename = SnippetsPlugin.getDefault().getStateLocation().toString() + "/hidden.xml"; //$NON-NLS-1$
			}
			catch (Exception e) {
				hiddenStateFilename = "/hidden.xml"; //$NON-NLS-1$
			}
			// hook resource listener and load categories from workspace
			// nsd_TODO: disable in-workspace support until fully stabilized
			// SnippetsPlugin.getDefault().initializeResourceChangeListener();
		}
		return instance;
	}

	private SnippetDefinitions definitions = null;

	protected IEntryChangeListener[] fListeners;

	private SnippetPaletteRoot fRoot;

	public SnippetManager() {
		super();
	}

	public void addEntryChangeListener(IEntryChangeListener listener) {
		if (fListeners == null) {
			fListeners = new IEntryChangeListener[]{listener};
		}
		else {
			IEntryChangeListener[] newListeners = new IEntryChangeListener[fListeners.length + 1];
			newListeners[0] = listener;
			System.arraycopy(fListeners, 0, newListeners, 1, fListeners.length);
			fListeners = newListeners;
		}
	}

	protected ISnippetsEntry findEntry(SnippetDefinitions defs, String id) {
		List categories = defs.getCategories();
		ISnippetsEntry match = null;
		for (int i = 0; match == null && i < categories.size(); i++) {
			SnippetPaletteDrawer category = (SnippetPaletteDrawer) categories.get(i);
			if (category.getId().equals(id)) {
				match = category;
			}
			else {
				for (int j = 0; match == null && j < category.getChildren().size(); j++) {
					SnippetPaletteItem item = (SnippetPaletteItem) category.getChildren().get(j);
					if (item.getId().equals(id)) {
						match = item;
					}
				}
			}
		}
		return match;
	}

	/**
	 * Find a ISnippetsEntry by it's ID field. Not at all efficient.
	 * 
	 * @param id
	 * @return
	 */
	public ISnippetsEntry findEntry(String id) {
		return findEntry(getDefinitions(), id);
	}

	/**
	 * @param oldDefinitions
	 * @param newDefinitions
	 */
	private void fireModelChanged(SnippetDefinitions oldDefinitions, SnippetDefinitions newDefinitions) {
		if (fListeners == null)
			return;
		for (int i = 0; i < fListeners.length; i++)
			fListeners[i].modelChanged(oldDefinitions, newDefinitions);
	}

	/**
	 * @return
	 */
	public SnippetDefinitions getDefinitions() {
		if (definitions == null) {
			synchronized (getInstance()) {
				// load definitions
				definitions = loadDefinitions();
				if (fRoot == null) {
					fRoot = new SnippetPaletteRoot(definitions);
				}
				// hide categories so marked
				String[] hiddenIDs = loadHiddenState();
				for (int i = 0; i < hiddenIDs.length; i++) {
					ISnippetsEntry entry = findEntry(definitions, hiddenIDs[i]);
					if (entry != null) {
						if (entry instanceof SnippetPaletteItem) {
							((SnippetPaletteItem) entry).setVisible(false);
						}
						if (entry instanceof SnippetPaletteDrawer) {
							((SnippetPaletteDrawer) entry).setVisible(false);
						}
					}
				}
			}
		}
		return definitions;
	}

	/**
	 * @return
	 */
	public IEntryChangeListener[] getListeners() {
		return fListeners;
	}

	public SnippetPaletteRoot getPaletteRoot() {
		if (fRoot == null) {
			fRoot = new SnippetPaletteRoot(getDefinitions());
		}
		return fRoot;
	}

	/**
	 * @return
	 */
	protected SnippetDefinitions loadDefinitions() {
		// nsd_TODO: Ensure selection is constant across views
		// (LibrarySelectionManager?)
		SnippetDefinitions defs = new SnippetDefinitions();

		// Load all of the definitions from their sources (probably could
		// break
		// this into extensions with providers)
		SnippetDefinitions pluginDefs = ModelFactoryForPlugins.getInstance().loadCurrent();
		if (Logger.DEBUG_DEFINITION_PERSISTENCE)
			System.out.println("SnippetManager loaded " + pluginDefs.getCategories().size() + " categories from plug-ins"); //$NON-NLS-1$ //$NON-NLS-2$
		SnippetDefinitions userDefs = ModelFactoryForUser.getInstance().loadCurrent();
		if (Logger.DEBUG_DEFINITION_PERSISTENCE)
			System.out.println("SnippetManager loaded " + userDefs.getCategories().size() + " records for categories from user.xml"); //$NON-NLS-1$ //$NON-NLS-2$
		// SnippetDefinitions sharedDefs =
		// ModelFactoryForWorkspace.getInstance().loadCurrent();
		// if (Logger.debugDefinitionPersistence)
		// System.out.println("SnippetManager loaded " +
		// userDefs.getCategories().size() + " records for categories from
		// workspace"); //$NON-NLS-1$ //$NON-NLS-2$

		// the user categories also store placeholders for the non-user
		// categories
		defs.getCategories().addAll(userDefs.getCategories());

		// Merge plugin drawers by replacing the placeholder drawers with the
		// ones generated from the plugins
		for (int i = 0; i < pluginDefs.getCategories().size(); i++) {
			SnippetPaletteDrawer category = (SnippetPaletteDrawer) pluginDefs.getCategories().get(i);
			String id = category.getId();
			ISnippetsEntry existingEntry = findEntry(defs, id);
			int existingIndex = defs.getCategories().indexOf(existingEntry);
			if (existingEntry != null && existingIndex > -1) {
				// copy filters from existingEntry to pluginDefs category
				category.setFilters(existingEntry.getFilters());
				defs.getCategories().set(existingIndex, category);
			}
			else {
				defs.getCategories().add(category);
			}
		}

		// TODO: Merge workspace drawers

		// Remove stale plugin/workspace contributed drawers
		// Place-holders are stored without children so any that are not
		// refilled
		// should be removed. Empty contributed drawers will be removed as
		// well.
		Iterator it = defs.getCategories().iterator();
		while (it.hasNext()) {
			SnippetPaletteDrawer category = (SnippetPaletteDrawer) it.next();
			if (category.getSourceType() != ISnippetsEntry.SNIPPET_SOURCE_USER && category.getChildren().isEmpty())
				it.remove();
		}

		return defs;
	}

	/**
	 * Load the list of categories that aren't to be visible
	 */
	protected String[] loadHiddenState() {
		Document document = null;
		String[] results = new String[0];
		try {
			DocumentBuilder builder = CommonXML.getDocumentBuilder();
			if (builder != null) {
				document = builder.parse(hiddenStateFilename);
			}
			else {
				Logger.log(Logger.ERROR, "SnippetManager couldn't obtain a DocumentBuilder"); //$NON-NLS-1$
			}
		}
		catch (FileNotFoundException e) {
			// typical of new workspace, don't log it
			document = null;
		}
		catch (IOException e) {
			Logger.logException("SnippetManager could not load hidden state", e); //$NON-NLS-1$
		}
		catch (SAXException e) {
			Logger.logException("SnippetManager could not load hidden state", e); //$NON-NLS-1$
		}
		if (document != null) {
			List names = new ArrayList(0);
			Element hidden = document.getDocumentElement();
			if (hidden != null) {
				NodeList list = hidden.getChildNodes();
				for (int i = 0; i < list.getLength(); i++) {
					Node childNode = list.item(i);
					if (childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getNodeName().equals(SnippetsPlugin.NAMES.HIDE)) {
						Element entry = (Element) childNode;
						String id = null;
						if (entry.hasAttribute(SnippetsPlugin.NAMES.CATEGORY)) {
							id = entry.getAttribute(SnippetsPlugin.NAMES.CATEGORY);
						}
						else if (entry.hasAttribute(SnippetsPlugin.NAMES.ITEM)) {
							id = entry.getAttribute(SnippetsPlugin.NAMES.ITEM);
						}
						if (id != null && id.length() > 0) {
							names.add(id);
						}
					}
				}
				results = (String[]) names.toArray(results);
			}
		}
		if (Logger.DEBUG_DEFINITION_PERSISTENCE)
			System.out.println("SnippetManager remembered " + results.length + " hidden categories"); //$NON-NLS-1$ //$NON-NLS-2$
		return results;
	}

	/**
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(PaletteContainer.PROPERTY_CHILDREN)) {
			SnippetDefinitions oldDefinitions = getDefinitions();
			definitions = new SnippetDefinitions();
			definitions.setCategories((List) evt.getNewValue());
			fireModelChanged(oldDefinitions, definitions);
		}
	}

	public void removeEntryChangeListener(IEntryChangeListener listener) {
		if (fListeners.length == 1) {
			fListeners = null;
		}
		else {
			List newListenersList = new ArrayList(Arrays.asList(fListeners));
			newListenersList.remove(listener);
			IEntryChangeListener[] newListeners = new IEntryChangeListener[newListenersList.size() - 1];
			newListeners = (IEntryChangeListener[]) newListenersList.toArray(newListeners);
			fListeners = newListeners;
		}
	}

	public void resetDefinitions() {
		SnippetDefinitions oldDefinitions = definitions;
		definitions = null;
		if (fRoot != null)
			fRoot.setDefinitions(getDefinitions());
		fireModelChanged(oldDefinitions, getDefinitions());
	}

	public synchronized void saveDefinitions() {
		if (definitions == null)
			return;

		// save the list of categories to not see
		saveHiddenState();
		if (Logger.DEBUG_DEFINITION_PERSISTENCE)
			System.out.println("SnippetManager saved hidden state"); //$NON-NLS-1$

		// save the model
		UserModelDumper.getInstance().write(getDefinitions());
		if (Logger.DEBUG_DEFINITION_PERSISTENCE)
			System.out.println("SnippetManager wrote user records"); //$NON-NLS-1$
		List categories = getDefinitions().getCategories();
		final List workspaceCategories = new ArrayList();
		for (int i = 0; i < categories.size(); i++) {
			ISnippetCategory category = (ISnippetCategory) categories.get(i);
			if (category.getSourceType() == ISnippetsEntry.SNIPPET_SOURCE_WORKSPACE) {
				CategoryFileInfo info = (CategoryFileInfo) category.getSourceDescriptor();
				if (info != null && info.getFile() != null) {
					workspaceCategories.add(info);
				}
			}
		}
		for (int i = 0; i < workspaceCategories.size(); i++) {
			CategoryFileInfo info = (CategoryFileInfo) workspaceCategories.get(i);
			try {
				if (Logger.DEBUG_DEFINITION_PERSISTENCE)
					System.out.println("save workspace category: " + info.getCategory().getLabel()); //$NON-NLS-1$
				WorkspaceModelDumper.getInstance().write(info.getCategory(), info.getFile());
			}
			catch (Exception e) {
				Logger.logException(e);
			}
		}
	}

	/**
	 * Save the list of categories that aren't visible
	 */
	protected void saveHiddenState() {
		Document document = CommonXML.getDocumentBuilder().getDOMImplementation().createDocument(null, "hidden", null); //$NON-NLS-1$
		try {
			FileOutputStream ostream = new FileOutputStream(hiddenStateFilename);
			List categoryIDs = new ArrayList(0);
			List itemIDs = new ArrayList(0);
			// collect all of the hidden entry IDs
			for (int i = 0; i < getDefinitions().getCategories().size(); i++) {
				SnippetPaletteDrawer category = (SnippetPaletteDrawer) getDefinitions().getCategories().get(i);
				if (!category.isVisible() && !categoryIDs.contains(category.getId())) {
					categoryIDs.add(category.getId());
				}
				for (int j = 0; j < category.getChildren().size(); j++) {
					SnippetPaletteItem entry = (SnippetPaletteItem) category.getChildren().get(j);
					if (!entry.isVisible() && !itemIDs.contains(entry.getId())) {
						itemIDs.add(entry.getId());
					}
				}
			}
			// save those IDs in <hide category="id"/> tags
			for (int j = 0; j < categoryIDs.size(); j++) {
				Element hidden = document.createElement(SnippetsPlugin.NAMES.HIDE);
				hidden.setAttribute(SnippetsPlugin.NAMES.CATEGORY, categoryIDs.get(j).toString());
				document.getDocumentElement().appendChild(hidden);
			}
			for (int j = 0; j < itemIDs.size(); j++) {
				Element hidden = document.createElement(SnippetsPlugin.NAMES.HIDE);
				hidden.setAttribute(SnippetsPlugin.NAMES.ITEM, itemIDs.get(j).toString());
				document.getDocumentElement().appendChild(hidden);
			}
			CommonXML.serialize(document, ostream);
			ostream.close();
		}
		catch (IOException e) {
			Logger.logException(e);
		}
	}

}