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
import java.util.Stack;
import java.util.Vector;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.internal.DataModelManager;
import org.eclipse.wst.common.frameworks.internal.OperationListener;
import org.eclipse.wst.common.frameworks.internal.OperationManager;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizardPage;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.IDMExtendedPageGroup;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.IDMExtendedPageGroupHandler;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.IDMExtendedPageHandler;
import org.eclipse.wst.common.frameworks.internal.operation.extensionui.CommonUIPluginConstants;

public class PageGroupManager {
	private IDMExtendedPageGroup rootPageGroup;
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

	public PageGroupManager(OperationManager operationManager, DataModelManager dataModelManager, IDMExtendedPageGroup rootPageGroup) {
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

	public void addGroupAfter(String pageGroupID, IDMExtendedPageGroup pageInsertGroup) {
		PageGroupEntry pageGroupEntry = (PageGroupEntry) groupTable.get(pageGroupID);

		if (pageGroupEntry.pageGroup.getAllowsExtendedPages()) {
			addPageGroup(pageGroupEntry.pageGroup, pageInsertGroup);
		}
	}

	public void moveForwardOnePage() {
		boolean pageFound = false;

		if (pageGroupStack.empty()) {
			PageGroupEntry rootEntry = (PageGroupEntry) groupTable.get(rootPageGroup.getPageGroupID());
			String dataModelID = rootEntry.pageGroup.getDataModelID();
			pageGroupStack.push(new StackEntry(rootEntry, -1));

			if (dataModelID != null)
				dataModelManager.addNestedDataModel(dataModelID);
		}

		saveStackInfo();

		try {
			pageFound = findNextPage(true);
		} catch (Throwable exc) {
			// TODO display some error.
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

			String dataModelID = topEntry.pageGroupEntry.pageGroup.getDataModelID();

			if (dataModelID != null)
				dataModelManager.removeNestedDataModel(dataModelID);

			pageGroupStack.pop();

			if (!pageGroupStack.empty()) {
				foundPreviousPage = findPreviousPageInGroup();
				topEntry = (StackEntry) pageGroupStack.peek();
			}
		}
	}

	public DataModelWizardPage getCurrentPage() {
		DataModelWizardPage page = null;

		if (!pageGroupStack.empty()) {
			StackEntry topEntry = (StackEntry) pageGroupStack.peek();
			int pageIndex = topEntry.getTopPageIndex();

			page = pageIndex == -1 ? null : topEntry.pageGroupEntry.getPages()[pageIndex];
		}

		return page;
	}

	public boolean hasNextPage() {
		boolean pageFound = false;

		saveStackInfo();

		if (pageGroupStack.empty()) {
			PageGroupEntry rootEntry = (PageGroupEntry) groupTable.get(rootPageGroup.getPageGroupID());
			String dataModelID = rootEntry.pageGroup.getDataModelID();

			pageGroupStack.push(new StackEntry(rootEntry, -1));

			if (dataModelID != null)
				dataModelManager.addNestedDataModel(dataModelID);
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
				IDMExtendedPageGroup pageGroup = nextStackEntry.pageGroupEntry.pageGroup;
				String requiresOperationsId = pageGroup.getRequiredDataOperationToRun();
				String dataModelID = pageGroup.getDataModelID();

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

				if (dataModelID != null)
					dataModelManager.addNestedDataModel(dataModelID);

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

	private void loadExtendedPages(IDMExtendedPageGroup pageGroup) {
		String wizardId = rootPageGroup.getWizardID();
		String pageGroupId = pageGroup.getPageGroupID();
		int length = elements.length;

		for (int index = 0; index < length; index++) {
			DMPageGroupElementImpl pageInsertGroup = new DMPageGroupElementImpl(elements[index]);

			if (pageInsertGroup.getWizardID().equals(wizardId) && pageInsertGroup.getPageGroupInsertionID().equals(pageGroupId)) {
				addPageGroup(pageGroup, pageInsertGroup);

				// If this page group has page then add them
				if (pageInsertGroup.getAllowsExtendedPages()) {
					loadExtendedPages(pageInsertGroup);
				}
			}
		}
	}

	private void addPageGroup(IDMExtendedPageGroup pageGroup, IDMExtendedPageGroup insertedPageGroup) {
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

	public StackEntry findNextPageGroup(Stack pageGroupStack) {
		StackEntry topEntry = (StackEntry) pageGroupStack.peek();
		PageGroupEntry nextPageGroup = topEntry.getNextPageGroup(null);
		int parentIndex = topEntry.parentGroupIndex;
		int prevParentIndex = pageGroupStack.size() - 1;

		// Recurse up through the parents to find the next group if needed.
		while (parentIndex != -1 && nextPageGroup == null) {
			StackEntry parentStackEntry = (StackEntry) pageGroupStack.elementAt(parentIndex);

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
				DataModelWizardPage[] pages = pageGroupEntry.getPages();

				int pageIndex = getTopPageIndex();
				String pageId = pageIndex == -1 ? null : pages[pageIndex].getName();
				String expectedId = pageIndex + 1 >= pages.length ? null : pages[pageIndex + 1].getName();
				String newPageId = null;

				try {
					newPageId = pageGroupEntry.getPageHandler().getNextPage(pageId, expectedId);
				} catch (Throwable exc) {
					// TODO Log an error here.
				}

				if (newPageId != null && newPageId.equals(IDMExtendedPageHandler.SKIP_PAGE) && pageIndex >= 0 && pageIndex < pages.length - 2) {
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
				// TODO log error here.
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
		public IDMExtendedPageGroup pageGroup;
		public Vector groupsThatFollow;
		private IDMExtendedPageHandler pageHandler;
		private IDMExtendedPageGroupHandler pageGroupHandler;
		private DataModelWizardPage[] pages;
		private boolean initialized;

		public PageGroupEntry(IDMExtendedPageGroup newPageGroup) {
			pageGroup = newPageGroup;
			groupsThatFollow = new Vector();
			initialized = false;
		}

		public IDMExtendedPageHandler getPageHandler() {
			if (!initialized)
				init();

			return pageHandler;
		}

		public IDMExtendedPageGroupHandler getPageGroupHandler() {
			if (!initialized)
				init();

			return pageGroupHandler;
		}

		public DataModelWizardPage[] getPages() {
			if (!initialized)
				init();

			return pages;
		}

		public PageGroupEntry(PageGroupEntry originalEntry) {
			pageGroup = originalEntry.pageGroup;
			groupsThatFollow = originalEntry.groupsThatFollow;
			pageHandler = originalEntry.pageHandler;
			pageGroupHandler = originalEntry.pageGroupHandler;
			pages = originalEntry.pages;
		}

		private void init() {
			try {
				pageHandler = pageGroup.getExtendedPageHandler(dataModel);
				pageGroupHandler = pageGroup.getExtendedPageGroupHandler(dataModel);
				pages = pageGroup.getExtendedPages(dataModel);
			} catch (Throwable exc) {
				// TODO need to log this exception.
			}

			if (pageHandler == null)
				pageHandler = new SimplePageHandler();

			if (pageGroupHandler == null)
				pageGroupHandler = new SimplePageGroupHandler();

			if (pages == null)
				pages = new DataModelWizardPage[0];

			initialized = true;
		}

		private int checkForSpecialIds(String pageId) {
			int result = -1;

			if (pages.length == 0 || pageId == null)
				return -1;

			if (pageId.startsWith(IDMExtendedPageHandler.PAGE_AFTER)) {
				String afterID = pageId.substring(IDMExtendedPageHandler.PAGE_AFTER.length(), pageId.length());
				result = getIndexOf(afterID);
				result = result >= 0 && result < pages.length - 1 ? result + 1 : -1;
			} else if (pageId.startsWith(IDMExtendedPageHandler.PAGE_BEFORE)) {
				String beforeID = pageId.substring(IDMExtendedPageHandler.PAGE_BEFORE.length(), pageId.length());
				result = getIndexOf(beforeID);
				result = result >= 1 && result < pages.length ? result - 1 : -1;
			} else {
				result = getIndexOf(pageId);
			}

			return result;
		}

		private int getIndexOf(String pageId) {
			int result = -1;

			for (int index = 0; index < pages.length; index++) {
				if (pages[index].getName().equals(pageId)) {
					result = index;
					break;
				}
			}

			return result;
		}
	}
}
