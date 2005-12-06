/***************************************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.frameworks.internal.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.internal.DataModelManager;
import org.eclipse.wst.common.frameworks.internal.OperationListener;
import org.eclipse.wst.common.frameworks.internal.OperationManager;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.IDMPageGroup;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.IDMPageGroupHandler;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.IDMPageHandler;
import org.eclipse.wst.common.frameworks.internal.operation.extensionui.CommonUIPluginConstants;

public class PageGroupManager {
	private IDMPageGroup rootPageGroup;
	private OperationManager operationManager;
	private DataModelManager dataModelManager;
	private HashMap groupTable;
	private HashSet operationsRun;
	private Stack pageGroupStack;
	private IDataModel dataModel;
	private StackEntry savedTopEntry;
	private int savedStackSize;

	private IConfigurationElement[] elements;

	private final String ELEMENT_PAGE_GROUP = "wizardPageGroup"; //$NON-NLS-1$

	public PageGroupManager(OperationManager operationManager, DataModelManager dataModelManager, IDMPageGroup rootPageGroup) {
		this.operationManager = operationManager;
		this.dataModelManager = dataModelManager;
		dataModel = this.dataModelManager.getDataModel();
		groupTable = new HashMap();
		operationsRun = new HashSet();
		pageGroupStack = new Stack();
		this.rootPageGroup = rootPageGroup;


		PageGroupEntry rootPageGroupEntry = new PageGroupEntry(rootPageGroup);
		IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(CommonUIPluginConstants.PLUGIN_ID, ELEMENT_PAGE_GROUP);
		elements = point.getConfigurationElements();
		groupTable.put(this.rootPageGroup.getPageGroupID(), rootPageGroupEntry);

		if (this.rootPageGroup.getAllowsExtendedPages()) {
			// Find all the page groups that follow this root page group.
			loadExtendedPages(rootPageGroup);
		}

		this.operationManager.setUndoExecuteListener(new OperationListener() {
			public boolean notify(IDataModelOperation operation) {
				operationsRun.remove(operation.getID());

				return true;
			}
		});

	}

	public void addGroupAfter(String pageGroupID, IDMPageGroup pageInsertGroup) {
		PageGroupEntry pageGroupEntry = (PageGroupEntry) groupTable.get(pageGroupID);

		if (pageGroupEntry.pageGroup.getAllowsExtendedPages()) {
			addPageGroup(pageGroupEntry.pageGroup, pageInsertGroup);
		}
	}

	public void moveForwardOnePage() {
		boolean pageFound = false;

		if (pageGroupStack.empty()) {
			PageGroupEntry rootEntry = (PageGroupEntry) groupTable.get(rootPageGroup.getPageGroupID());
			Set dataModelIDs = rootEntry.pageGroup.getDataModelIDs();
			pageGroupStack.push(new StackEntry(rootEntry, -1));

			if (dataModelIDs != null && dataModelIDs.size() > 0) {
				Iterator ids = dataModelIDs.iterator();

				while (ids.hasNext()) {
					String dataModelID = (String) ids.next();
					dataModelManager.addNestedDataModel(dataModelID);
				}
			}
		}

		saveStackInfo();

		try {
			pageFound = findNextPage(true);
		} catch (Throwable exc) {
			Logger.getLogger().logError(exc);
			operationManager.undoLastRun();
			pageFound = false;
		}

		if (pageFound == false) {
			// If we moved forward and there wasn't a page then we will restore the stack.
			// Normally, this wouldn't happen since a call to hasNextPage would have indicated
			// that there wasn't a page.
			restoreStackInfo();
		}
	}

	public void moveBackOnePage() {
		if (pageGroupStack.empty())
			return;

		StackEntry topEntry = (StackEntry) pageGroupStack.peek();

		// Pop the last page.
		if (!topEntry.pagesReturned.empty())
			topEntry.pagesReturned.pop();

		// Now find the previous page.
		boolean foundPreviousPage = findPreviousPageInGroup();

		while (!foundPreviousPage && !pageGroupStack.empty()) {
			if (topEntry.ranOperations) {
				operationManager.undoLastRun();
			}

			Set dataModelIDs = topEntry.pageGroupEntry.pageGroup.getDataModelIDs();

			if (dataModelIDs != null && dataModelIDs.size() > 0) {
				Iterator ids = dataModelIDs.iterator();

				while (ids.hasNext()) {
					String dataModelID = (String) ids.next();
					dataModelManager.removeNestedDataModel(dataModelID);
				}
			}

			pageGroupStack.pop();

			if (!pageGroupStack.empty()) {
				foundPreviousPage = findPreviousPageInGroup();
				topEntry = (StackEntry) pageGroupStack.peek();
			}
		}
	}

	public void reset(){
		pageGroupStack.clear();
	}
	
	public IWizardPage getCurrentPage() {
		IWizardPage page = null;

		if (!pageGroupStack.empty()) {
			StackEntry topEntry = (StackEntry) pageGroupStack.peek();
			int pageIndex = topEntry.getTopPageIndex();

			page = pageIndex == -1 ? null : (IWizardPage)topEntry.pageGroupEntry.getPages().get(pageIndex);
		}

		return page;
	}

	private Boolean hasMulitplePages;

	public boolean hasMultiplePages() {
		if (null == hasMulitplePages) {
			int pageCount = 0;
			PageGroupEntry rootEntry = (PageGroupEntry) groupTable.get(rootPageGroup.getPageGroupID());
			pageCount += rootEntry.getPages().size();
			for (int i = 0; pageCount < 2 && i < rootEntry.groupsThatFollow.size(); i++) {
				pageCount += ((PageGroupEntry) rootEntry.groupsThatFollow.get(i)).getPages().size();
			}
			hasMulitplePages = pageCount > 1 ? Boolean.TRUE : Boolean.FALSE;
		}
		return hasMulitplePages.booleanValue();
	}

	public boolean hasNextPage() {
		boolean pageFound = false;

		saveStackInfo();

		if (pageGroupStack.empty()) {
			PageGroupEntry rootEntry = (PageGroupEntry) groupTable.get(rootPageGroup.getPageGroupID());
			Set dataModelIDs = rootEntry.pageGroup.getDataModelIDs();

			pageGroupStack.push(new StackEntry(rootEntry, -1));

			if (dataModelIDs != null && dataModelIDs.size() > 0) {
				Iterator ids = dataModelIDs.iterator();

				while (ids.hasNext()) {
					String dataModelID = (String) ids.next();
					dataModelManager.addNestedDataModel(dataModelID);
				}
			}
		}

		pageFound = findNextPage(false);
		restoreStackInfo();

		return pageFound;
	}

	public boolean runAllRemainingOperations() {
		setPostListener(null);

		IStatus status = operationManager.runOperations();

		return status.getSeverity() != IStatus.ERROR;
	}

	public void undoAllCurrentOperations() {
		while (!pageGroupStack.empty()) {
			moveBackOnePage();
		}
	}

	private boolean findPreviousPageInGroup() {
		StackEntry topEntry = (StackEntry) pageGroupStack.peek();
		boolean pageFound = false;

		if (!topEntry.pagesReturned.empty()) {
			topEntry.pagesComplete = false;
			pageFound = true;
		}

		return pageFound;
	}

	private boolean findNextPage(boolean runOperations) {
		StackEntry topEntry = (StackEntry) pageGroupStack.peek();
		int newPageIndex = topEntry.findNextPageIndex();
		boolean pageFound = false;

		if (newPageIndex == -1) {
			// Our page handler didn't find a page so we will see if there is a page group that
			// follows this page group that can find a page.
			topEntry.pagesComplete = true;

			StackEntry nextStackEntry = findNextPageGroup(pageGroupStack);

			if (nextStackEntry != null) {
				IDMPageGroup pageGroup = nextStackEntry.pageGroupEntry.pageGroup;
				String requiresOperationsId = pageGroup.getRequiredDataOperationToRun();
				Set dataModelIDs = pageGroup.getDataModelIDs();

				// If this group requires an operation and it has not already been run
				// then we need to run it.
				if (runOperations && requiresOperationsId != null && !operationsRun.contains(requiresOperationsId)) {
					setPostListener(requiresOperationsId);
					IStatus status = operationManager.runOperations();

					nextStackEntry.ranOperations = true;
					if (status.getSeverity() == IStatus.ERROR) {
						// TODO need a better error feedback mechanism here.
						throw new IllegalArgumentException(status.getMessage());
					}
				}

				if (dataModelIDs != null && dataModelIDs.size() > 0) {
					Iterator ids = dataModelIDs.iterator();

					while (ids.hasNext()) {
						String dataModelID = (String) ids.next();
						dataModelManager.addNestedDataModel(dataModelID);
					}
				}

				pageGroupStack.push(nextStackEntry);
				pageFound = findNextPage(runOperations);
			}
		} else {
			// We found a new page in the page handler.
			pageFound = true;
			topEntry.pagesReturned.push(new Integer(newPageIndex));
		}

		return pageFound;
	}

	private void setPostListener(final String operationId) {
		if (operationId != null) {
			// Listener for a particular operation and stop after we are notified of it.
			operationManager.setPostExecuteListener(new OperationListener() {
				public boolean notify(IDataModelOperation operation) {
					String id = operation.getID();

					operationsRun.add(id);

					return !id.equals(operationId);
				}
			});
		} else {
			// Set the post execution listener to doing nothing so that all operations
			// will execute.
			operationManager.setPostExecuteListener(new OperationListener() {
				public boolean notify(IDataModelOperation operation) {
					return true;
				}
			});
		}
	}

	private void saveStackInfo() {
		if (!pageGroupStack.empty()) {
			savedTopEntry = new StackEntry((StackEntry) pageGroupStack.peek());
		}

		savedStackSize = pageGroupStack.size();
	}

	private void restoreStackInfo() {
		if (savedStackSize == 0) {
			pageGroupStack.removeAllElements();
		} else {
			pageGroupStack.setSize(savedStackSize - 1);
			pageGroupStack.push(savedTopEntry);
		}
	}

	private void loadExtendedPages(IDMPageGroup pageGroup) {
		String wizardId = rootPageGroup.getWizardID();
		String pageGroupId = pageGroup.getPageGroupID();
		int length = elements.length;

		for (int index = 0; index < length; index++) {
			DMPageGroupElementImpl pageInsertGroup = new DMPageGroupElementImpl(elements[index]);
			String pageInsertGroupId = pageInsertGroup.getPageGroupInsertionID();
			if (pageInsertGroup.getWizardID().equals(wizardId) && (null == pageInsertGroupId || pageInsertGroupId.equals(pageGroupId))) {
				addPageGroup(pageGroup, pageInsertGroup);

				// If this page group has page then add them
				if (pageInsertGroup.getAllowsExtendedPages()) {
					loadExtendedPages(pageInsertGroup);
				}
			}
		}
	}

	private void addPageGroup(IDMPageGroup pageGroup, IDMPageGroup insertedPageGroup) {
		PageGroupEntry pageGroupEntry = (PageGroupEntry) groupTable.get(pageGroup.getPageGroupID());
		PageGroupEntry nextGroupEntry = (PageGroupEntry) groupTable.get(insertedPageGroup.getPageGroupID());

		if (pageGroupEntry == null) {
			pageGroupEntry = new PageGroupEntry(pageGroup);
			groupTable.put(pageGroup.getPageGroupID(), pageGroupEntry);
		}

		if (nextGroupEntry == null) {
			nextGroupEntry = new PageGroupEntry(insertedPageGroup);
			groupTable.put(insertedPageGroup.getPageGroupID(), nextGroupEntry);
		}

		pageGroupEntry.groupsThatFollow.add(nextGroupEntry);
	}

	public StackEntry findNextPageGroup(Stack stack) {
		StackEntry topEntry = (StackEntry) stack.peek();
		PageGroupEntry nextPageGroup = topEntry.getNextPageGroup(null);
		int parentIndex = topEntry.parentGroupIndex;
		int prevParentIndex = stack.size() - 1;

		// Recurse up through the parents to find the next group if needed.
		while (parentIndex != -1 && nextPageGroup == null) {
			StackEntry parentStackEntry = (StackEntry) stack.elementAt(parentIndex);

			nextPageGroup = parentStackEntry.getNextPageGroup(topEntry.getId());
			prevParentIndex = parentIndex;
			parentIndex = parentStackEntry.parentGroupIndex;
			topEntry = parentStackEntry;
		}

		return nextPageGroup == null ? null : new StackEntry(nextPageGroup, prevParentIndex);
	}

	private class StackEntry {
		public PageGroupEntry pageGroupEntry;
		public Stack pagesReturned; // Element = Interger of page indexes.
		public boolean pagesComplete;
		public int parentGroupIndex;
		public boolean ranOperations;

		public StackEntry(PageGroupEntry newPageGroupEntry, int parentIndex) {
			pageGroupEntry = newPageGroupEntry;
			pagesReturned = new Stack();
			pagesComplete = false;
			parentGroupIndex = parentIndex;
			ranOperations = false;
		}

		public StackEntry(StackEntry stackEntry) {
			pageGroupEntry = stackEntry.pageGroupEntry;
			pagesReturned = new Stack();
			pagesComplete = stackEntry.pagesComplete;
			parentGroupIndex = stackEntry.parentGroupIndex;
			ranOperations = stackEntry.ranOperations;
			pagesReturned.addAll(stackEntry.pagesReturned);
		}

		public String getId() {
			return pageGroupEntry.pageGroup.getPageGroupID();
		}

		public int findNextPageIndex() {
			int result = -1;

			if (!pagesComplete) {
				List pages = pageGroupEntry.getPages();

				int pageIndex = getTopPageIndex();
				String pageId = pageIndex == -1 ? null : ((IWizardPage)pages.get(pageIndex)).getName();
				String expectedId = pageIndex + 1 >= pages.size() ? null : ((IWizardPage)pages.get(pageIndex + 1)).getName();
				String newPageId = null;

				try {
					newPageId = pageGroupEntry.getPageHandler().getNextPage(pageId, expectedId);
				} catch (Throwable exc) {
					Logger.getLogger().logError(exc);
				}

				if (newPageId != null && newPageId.equals(IDMPageHandler.SKIP_PAGE) && pageIndex >= 0 && pageIndex < pages.size() - 2) {
					result = pageIndex + 2;
				} else {
					result = pageGroupEntry.checkForSpecialIds(newPageId);
				}
			}

			return result;
		}

		public PageGroupEntry getNextPageGroup(String afterId) {
			PageGroupEntry result = null;
			String nextGroupID = null;
			String[] groupIDList = getGroupIDList();

			try {
				nextGroupID = pageGroupEntry.getPageGroupHandler().getNextPageGroup(afterId, groupIDList);
			} catch (Throwable exc) {
				Logger.getLogger().logError(exc);
			}

			if (nextGroupID != null) {
				// Find this string in the list.
				for (int index = 0; index < groupIDList.length; index++) {
					if (groupIDList[index].equals(nextGroupID)) {
						result = (PageGroupEntry) pageGroupEntry.groupsThatFollow.elementAt(index);
						break;
					}
				}
			}

			return result;
		}

		private String[] getGroupIDList() {
			String[] result = new String[pageGroupEntry.groupsThatFollow.size()];

			for (int index = 0; index < pageGroupEntry.groupsThatFollow.size(); index++) {
				PageGroupEntry entry = (PageGroupEntry) pageGroupEntry.groupsThatFollow.elementAt(index);

				result[index] = entry.pageGroup.getPageGroupID();
			}

			return result;
		}

		private int getTopPageIndex() {
			return pagesReturned.empty() ? -1 : ((Integer) pagesReturned.peek()).intValue();
		}
	}

	private class PageGroupEntry {
		public IDMPageGroup pageGroup;
		public Vector groupsThatFollow;
		private IDMPageHandler pageHandler;
		private IDMPageGroupHandler pageGroupHandler;
		private boolean initialized;

		public PageGroupEntry(IDMPageGroup newPageGroup) {
			pageGroup = newPageGroup;
			groupsThatFollow = new Vector();
			initialized = false;
		}

		public IDMPageHandler getPageHandler() {
			if (!initialized)
				init();

			return pageHandler;
		}

		public IDMPageGroupHandler getPageGroupHandler() {
			if (!initialized)
				init();

			return pageGroupHandler;
		}

		public List getPages() {
			if (!initialized)
				init();

			return pageGroup.getPages(dataModel);
		}

		public PageGroupEntry(PageGroupEntry originalEntry) {
			pageGroup = originalEntry.pageGroup;
			groupsThatFollow = originalEntry.groupsThatFollow;
			pageHandler = originalEntry.pageHandler;
			pageGroupHandler = originalEntry.pageGroupHandler;
		}

		private void init() {
			try {
				pageHandler = pageGroup.getPageHandler(dataModel);
				pageGroupHandler = pageGroup.getPageGroupHandler(dataModel);
			} catch (Throwable exc) {
				Logger.getLogger().logError(exc);
			}

			if (pageHandler == null)
				pageHandler = new SimplePageHandler();

			if (pageGroupHandler == null)
				pageGroupHandler = new SimplePageGroupHandler();

			initialized = true;
		}

		private int checkForSpecialIds(String pageId) {
			int result = -1;

			List pages = getPages();
			
			if (pages.isEmpty() || pageId == null)
				return -1;

			if (pageId.startsWith(IDMPageHandler.PAGE_AFTER)) {
				String afterID = pageId.substring(IDMPageHandler.PAGE_AFTER.length(), pageId.length());
				result = getIndexOf(afterID);
				result = result >= 0 && result < pages.size() - 1 ? result + 1 : -1;
			} else if (pageId.startsWith(IDMPageHandler.PAGE_BEFORE)) {
				String beforeID = pageId.substring(IDMPageHandler.PAGE_BEFORE.length(), pageId.length());
				result = getIndexOf(beforeID);
				result = result >= 1 && result < pages.size() ? result - 1 : -1;
			} else {
				result = getIndexOf(pageId);
			}

			return result;
		}

		private int getIndexOf(String pageId) {
			int result = -1;

			List pages = getPages();
			for (int index = 0; index < pages.size(); index++) {
				if (((IWizardPage)pages.get(index)).getName().equals(pageId)) {
					result = index;
					break;
				}
			}

			return result;
		}
	}
}
