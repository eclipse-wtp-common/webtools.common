/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.operation.extensionui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jem.util.RegistryReader;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.frameworks.internal.enablement.EnablementManager;
import org.eclipse.wst.common.frameworks.internal.ui.WTPCommonUIResourceHandler;

public class DMWizardPageExtensionManager {

	public static final String ORG_ECLIPSE_UI = "org.eclipse.ui"; //$NON-NLS-1$

	protected static DMWizardPageExtensionManager instance = null;

	HashMap wizardPluginIDMap = null;

	TreeMap wizardPageElements = null;

	// private TreeSet wizardExtPageElements = null;
	List nonSyncedPageElementList = null;

	private WizardPageExtensionReader reader = null;

	private DMWizardPageExtensionManager() {
		setupWizardPluginIDMap();
		readFromRegistry();
		postReadFromRegistry();
	}

	private void setupWizardPluginIDMap() {
		wizardPluginIDMap = new HashMap();
		// get editor plugin and save it to a hash map
		// Note: editors extension id is different from editor id

		IExtensionPoint[] point = new IExtensionPoint[]{Platform.getExtensionRegistry().getExtensionPoint(ORG_ECLIPSE_UI, "exportWizards"), //$NON-NLS-1$
					Platform.getExtensionRegistry().getExtensionPoint(ORG_ECLIPSE_UI, "importWizards"), //$NON-NLS-1$
					Platform.getExtensionRegistry().getExtensionPoint(ORG_ECLIPSE_UI, "newWizards"), //$NON-NLS-1$
					Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.wst.common.frameworks.ui", "extendableWizard")}; //$NON-NLS-1$ //$NON-NLS-2$

		for (int x = 0; x < point.length; x++) {
			IConfigurationElement[] elements = point[x].getConfigurationElements();
			for (int i = 0; i < elements.length; i++) {
				String wizardID = elements[i].getAttribute("id"); //$NON-NLS-1$
				String pluginID = elements[i].getDeclaringExtension().getNamespace();
				wizardPluginIDMap.put(wizardID, pluginID);
			}
		}
	}

	private void readFromRegistry() {
		wizardPageElements = new TreeMap();
		// wizardExtPageElements = new TreeSet();
		nonSyncedPageElementList = new ArrayList();
		// Read all page extensions into editorPageElements
		reader = new WizardPageExtensionReader();
		reader.readRegistry();

	}

	protected class WizardPageExtensionReader extends RegistryReader {

		public WizardPageExtensionReader() {
			super(CommonUIPluginConstants.PLUGIN_ID, DMWizardPageElement.ELEMENT_PAGE_GROUP);
		}

		public boolean readElement(IConfigurationElement element) {
			if (!DMWizardPageElement.ELEMENT_PAGE_GROUP.equals(element.getName()))
				return false;
			DMWizardPageElement newElement = new DMWizardPageElement(element);

			// put the element into a hashmap, wizardID as key,
			// list of page elements as object
			String wizardID = newElement.getWizardID();
			if (!wizardPageElements.containsKey(wizardID)) {
				wizardPageElements.put(wizardID, createPageMapEntry(newElement));
			} else {
				TreeMap pageMap = (TreeMap) wizardPageElements.get(wizardID);
				insertPageElement(pageMap, newElement);
			}

			return true;
		}

		public void insertPageElement(TreeMap pageMap, DMWizardPageElement newElement) {
			if (newElement.pageInsertionID == null) {
				pageMap.put(newElement, new TreeSet());
			} else {
				String elementName = newElement.pageInsertionID;
				DMWizardPageElement parentElement = getPageElement(elementName, pageMap);
				insertExtPageElement(pageMap, parentElement, newElement);
			}

			newElement.getPageInsertionID();

		}

		private void insertExtPageElement(TreeMap pageMap, DMWizardPageElement parentElement, DMWizardPageElement newElement) {
			if (parentElement == null) {
				nonSyncedPageElementList.add(newElement);
				return;
			}
			if (!parentElement.allowsExtendedPagesAfter()) {
				Logger.getLogger().logError(WTPCommonUIResourceHandler.getString(WTPCommonUIResourceHandler.WizardPageExtensionManager_UI_0, new Object[]{parentElement.getPageID()})); //$NON-NLS-1$
				Logger.getLogger().logError(WTPCommonUIResourceHandler.getString(WTPCommonUIResourceHandler.WizardPageExtensionManager_UI_1, new Object[]{newElement.getPageID()})); //$NON-NLS-1$
				return;
			}
			TreeSet set = (TreeSet) pageMap.get(parentElement);
			set.add(newElement);

			if (newElement.allowsExtendedPagesAfter)
				pageMap.put(newElement, new TreeSet());

			if (nonSyncedPageElementList.contains(newElement))
				nonSyncedPageElementList.remove(newElement);
		}

		private DMWizardPageElement getPageElement(String elementName, TreeMap map) {
			Set keySet = map.keySet();
			for (Iterator iter = keySet.iterator(); iter.hasNext();) {
				DMWizardPageElement element = (DMWizardPageElement) iter.next();
				if (element.getPageID().equals(elementName))
					return element;
			}
			return null;

		}

		private TreeMap createPageMapEntry(DMWizardPageElement newElement) {
			TreeMap pageMap = new TreeMap();
			TreeSet pageExtensionSet = new TreeSet();
			pageMap.put(newElement, pageExtensionSet);
			return pageMap;

		}

	}

	public boolean hasExtensionElements(String wizardID) {
		TreeMap treeMap = (TreeMap) wizardPageElements.get(wizardID);
		return treeMap.isEmpty();
	}

	public DMWizardPageElement[] getPageElements(String wizardID) {
		TreeMap elementMap = (TreeMap) wizardPageElements.get(wizardID);
		if (elementMap == null || elementMap.isEmpty()) {
			return new DMWizardPageElement[0];
		}
		ArrayList alreadyVistedList = new ArrayList(elementMap.size());
		Set allPageElementsList = elementMap.keySet();
		ArrayList orderedPageList = new ArrayList(elementMap.size());

		for (Iterator iter = allPageElementsList.iterator(); iter.hasNext();) {
			DMWizardPageElement element = (DMWizardPageElement) iter.next();
			if (alreadyVistedList.contains(element))
				continue;
			if (EnablementManager.INSTANCE.getIdentifier(element.getID(), null).isEnabled()) {
				orderedPageList.add(element);
				TreeSet treeSet = (TreeSet) elementMap.get(element);
				if (treeSet != null && !treeSet.isEmpty())
					flatenTreeSet(treeSet, allPageElementsList, alreadyVistedList, orderedPageList, elementMap);
			}
		}
		return getPageArray(orderedPageList);

	}

	private DMWizardPageElement[] getPageArray(ArrayList orderedPageList) {
		DMWizardPageElement[] pageElements = new DMWizardPageElement[orderedPageList.size()];
		for (int i = 0; i < orderedPageList.size(); i++) {
			pageElements[i] = (DMWizardPageElement) orderedPageList.get(i);
		}
		return pageElements;
	}

	private void flatenTreeSet(TreeSet treeSet, Set allPageElementsList, ArrayList alreadyVistedList, ArrayList orderedPageList, TreeMap elementMap) {
		for (Iterator iter = treeSet.iterator(); iter.hasNext();) {
			DMWizardPageElement element = (DMWizardPageElement) iter.next();
			if (alreadyVistedList.contains(element)) {
				Logger.getLogger().logError(WTPCommonUIResourceHandler.getString(WTPCommonUIResourceHandler.WizardPageExtensionManager_UI_2, new Object[]{element.getPageID(), DMWizardPageElement.ATT_PAGE_INSERTION_ID})); //$NON-NLS-1$
				return;
			}
			if (allPageElementsList.contains(element)) {
				TreeSet set = (TreeSet) elementMap.get(element);
				orderedPageList.add(element);
				alreadyVistedList.add(element);
				flatenTreeSet(set, allPageElementsList, alreadyVistedList, orderedPageList, elementMap);
			} else {
				orderedPageList.add(element);
				alreadyVistedList.add(element);
			}

		}

	}

	// if child elements are read in before parent element. Do post read.
	protected ArrayList listRemoveObjects;

	private void postReadFromRegistry() {
		listRemoveObjects = new ArrayList(nonSyncedPageElementList.size());
		for (int i = 0; i < nonSyncedPageElementList.size(); i++) {
			DMWizardPageElement element = (DMWizardPageElement) nonSyncedPageElementList.get(i);
			TreeMap pageMap = (TreeMap) wizardPageElements.get(element.wizardID);
			if (element.pageInsertionID == null) {
				addToFirstAvialiable(pageMap, element);
			} else if (reader != null && pageMap != null)
				reader.insertPageElement(pageMap, element);
		}
		nonSyncedPageElementList.removeAll(listRemoveObjects);
		if (!nonSyncedPageElementList.isEmpty())
			logMissingClassError();
		nonSyncedPageElementList.clear();

	}

	/**
	 * @param pageMap
	 * @param element
	 */
	private void addToFirstAvialiable(TreeMap pageMap, DMWizardPageElement newElement) {
		boolean insertNotFound = true;
		for (Iterator iter = pageMap.keySet().iterator(); iter.hasNext();) {
			DMWizardPageElement element = (DMWizardPageElement) iter.next();
			if (element.allowsExtendedPagesAfter) {
				TreeSet set = (TreeSet) pageMap.get(element);
				set.add(newElement);
				listRemoveObjects.add(newElement);
				return;
			}
		}
		if (insertNotFound) {
			Logger logger = Logger.getLogger();
			logger.logError(WTPCommonUIResourceHandler.WizardPageExtensionManager_UI_3); //$NON-NLS-1$
		}

	}

	private void logMissingClassError() {
		Logger logger = Logger.getLogger();
		for (int i = 0; i < nonSyncedPageElementList.size(); i++) {
			DMWizardPageElement element = (DMWizardPageElement) nonSyncedPageElementList.get(i);
			logger.logError(WTPCommonUIResourceHandler.getString(WTPCommonUIResourceHandler.WizardPageExtensionManager_UI_4, new Object[]{element.pageInsertionID, element.getPageID(), element.pluginID})); //$NON-NLS-1$
		}
		nonSyncedPageElementList.clear();

	}

	/**
	 * Gets the instance.
	 * 
	 * @return Returns a EjbPageExtensionRegistry
	 */
	public static DMWizardPageExtensionManager getInstance() {
		if (instance == null) {
			instance = new DMWizardPageExtensionManager();
		}
		return instance;
	}
}
