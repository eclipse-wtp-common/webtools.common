/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Jan 16, 2004
 * 
 * To change the template for this generated file go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
package org.eclipse.wst.common.frameworks.operation.extension.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.util.Assert;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.framework.operation.FailSafeComposedOperation;
import org.eclipse.wst.common.framework.operation.WTPOperation;
import org.eclipse.wst.common.framework.operation.WTPOperationDataModel;
import org.eclipse.wst.common.frameworks.ui.WTPCommonUIResourceHandler;
import org.eclipse.wst.internal.common.frameworks.ui.WTPWizard;

import com.ibm.wtp.common.logger.proxy.Logger;

/**
 * @author blancett
 * 
 * To change the template for this generated type comment go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */
public abstract class ExtendableWizard extends WTPWizard {

	private IExtendedWizardPage[] extendedPages = null;
	private IExtendedPageHandler[] extendedPageHandlers = null;

	public ExtendableWizard(WTPOperationDataModel model) {
		super(model);
	}

	public ExtendableWizard() {
		super();
	}

	/**
	 * 
	 * @return the wizard ID that clients should extend to add to this wizard
	 */
	public abstract String getWizardID();

	/**
	 * Add all wizard pages that come from this wizard
	 */
	protected abstract void doAddPages();

	/**
	 * Create the operation that should be used by this wizard
	 * 
	 * @return
	 */
	protected abstract WTPOperation createBaseOperation();

	private void addExtensionPages() {
		String wizardID = getWizardID();
		WizardPageElement wizElement = null;
		if (wizardID == null)
			return;
		WizardPageElement[] elements = WizardPageExtensionManager.getInstance().getPageElements(getWizardID());
		IExtendedWizardPage[] extendedPagesLocal = null;
		IExtendedPageHandler extendedPageHandler = null;
		List extendedPagesList = new ArrayList();
		List extendedPageHandlerList = new ArrayList();
		for (int i = 0; i < elements.length; i++) {
			wizElement = elements[i];
			try {
				extendedPagesLocal = wizElement.createPageGroup(model);
				if (null != extendedPagesLocal) {
					for (int j = 0; j < extendedPagesLocal.length; j++) {
						addPage(extendedPagesLocal[j]);
						extendedPagesList.add(extendedPagesLocal[j]);
					}
				}
				extendedPageHandler = wizElement.createPageHandler(model);
				if (null != extendedPageHandler && !extendedPageHandlerList.contains(extendedPageHandler)) {
					extendedPageHandlerList.add(extendedPageHandler);
				}
			} catch (RuntimeException runtime) {
				Logger.getLogger().logError(WTPCommonUIResourceHandler.getString("ExtendableWizard_UI_0", new Object[]{wizElement.getPluginID(), wizElement.pageGroupID})); //$NON-NLS-1$
				Logger.getLogger().logError(runtime);
			}
		}
		extendedPages = new IExtendedWizardPage[extendedPagesList.size()];
		for (int i = 0; i < extendedPages.length; i++) {
			extendedPages[i] = (IExtendedWizardPage) extendedPagesList.get(i);
		}
		extendedPageHandlers = new IExtendedPageHandler[extendedPageHandlerList.size()];
		for (int i = 0; i < extendedPageHandlers.length; i++) {
			extendedPageHandlers[i] = (IExtendedPageHandler) extendedPageHandlerList.get(i);
		}

	}

	public boolean canFinish() {
		if (!super.canFinish()) {
			return false;
		}
		for (int i = 0; null != extendedPages && i < extendedPages.length; i++) {
			if (!extendedPages[i].canPageFinish()) {
				return false;
			}
		}
		return true;
	}

	protected final WTPOperation createOperation() {
		WTPOperation baseOperation = createBaseOperation();
		FailSafeComposedOperation composedOperation = null;
		for (int i = 0; null != extendedPages && i < extendedPages.length; i++) {
			WTPOperation op = extendedPages[i].createOperation();
			if (op != null) {
				if (composedOperation == null) {
					composedOperation = new FailSafeComposedOperation();
					composedOperation.append(baseOperation);
				}
				composedOperation.append(op);
			}
		}
		return composedOperation != null ? composedOperation : baseOperation;
	}

	/**
	 * The <code>Wizard</code> implementation of this <code>IWizard</code> method creates all
	 * the pages controls using <code>IDialogPage.createControl</code>. Subclasses should
	 * reimplement this method if they want to delay creating one or more of the pages lazily. The
	 * framework ensures that the contents of a page will be created before attempting to show it.
	 */
	public void createPageControls(Composite pageContainer) {
		IWizardPage[] pages = getPages();
		// the default behavior is to create all the pages controls
		for (int i = 0; i < pages.length; i++) {
			if (isExtendedPage(pages[i])) {
				try {
					pages[i].createControl(pageContainer);
				} catch (Exception e) {
					Logger.getLogger().logError(e);
					continue;
				}
			} else {
				pages[i].createControl(pageContainer);
			}
			// page is responsible for ensuring the created control is
			// accessable
			// via getControl.
			Assert.isNotNull(pages[i].getControl());
		}
	}

	protected boolean isExtendedPage(IWizardPage page) {
		for (int i = 0; null != extendedPages && i < extendedPages.length; i++) {
			if (page == extendedPages[i]) {
				return true;
			}
		}
		return false;
	}


	public final void addPages() {
		doAddPages();
		addExtensionPages();
	}

	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage expectedPage = super.getNextPage(page);
		String expectedPageName = (null == expectedPage) ? null : expectedPage.getName();
		String nextPageName = null;
		for (int i = 0; null != extendedPageHandlers && i < extendedPageHandlers.length; i++) {
			nextPageName = extendedPageHandlers[i].getNextPage(page.getName(), expectedPageName);
			if (null != nextPageName) {
				if (nextPageName.equals(IExtendedPageHandler.SKIP_PAGE)) {
					return getNextPage(expectedPage);
				} else if (nextPageName.startsWith(IExtendedPageHandler.PAGE_AFTER)) {
					String tempNextPageName = nextPageName.substring(IExtendedPageHandler.PAGE_AFTER.length());
					IWizardPage tempNextPage = getPage(tempNextPageName);
					return null == tempNextPage ? null : super.getNextPage(tempNextPage);
				}
				return getPage(nextPageName);
			}
		}
		return expectedPage;
	}

	public IWizardPage getPreviousPage(IWizardPage page) {
		IWizardPage expectedPage = super.getPreviousPage(page);
		String expectedPageName = (null == expectedPage) ? null : expectedPage.getName();
		String previousPageName = null;
		for (int i = 0; null != extendedPageHandlers && i < extendedPageHandlers.length; i++) {
			previousPageName = extendedPageHandlers[i].getPreviousPage(page.getName(), expectedPageName);
			if (null != previousPageName) {
				if (previousPageName.equals(IExtendedPageHandler.SKIP_PAGE)) {
					return getPreviousPage(expectedPage);
				} else if (previousPageName.startsWith(IExtendedPageHandler.PAGE_AFTER)) {
					String tempPreviousPageName = previousPageName.substring(IExtendedPageHandler.PAGE_AFTER.length());
					IWizardPage tempPreviousPage = getPage(tempPreviousPageName);
					return null == tempPreviousPage ? null : super.getPreviousPage(tempPreviousPage);
				}
				return getPage(previousPageName);
			}
		}
		return expectedPage;
	}


}