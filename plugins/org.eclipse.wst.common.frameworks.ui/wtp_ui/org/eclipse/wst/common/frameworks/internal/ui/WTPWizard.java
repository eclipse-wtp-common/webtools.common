/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.frameworks.internal.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.frameworks.internal.enablement.EnablementManager;
import org.eclipse.wst.common.frameworks.internal.operation.extensionui.WizardPageElement;
import org.eclipse.wst.common.frameworks.internal.operation.extensionui.WizardPageExtensionManager;
import org.eclipse.wst.common.frameworks.internal.operations.FailSafeComposedOperation;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel;

import org.eclipse.jem.util.logger.proxy.Logger;


/**
 * This class is EXPERIMENTAL and is subject to substantial changes.
 */
public abstract class WTPWizard extends Wizard {

	private IExtendedWizardPage[] extendedPages = null;
	private IExtendedPageHandler[] extendedPageHandlers = null;


	protected WTPOperationDataModel model;

	public WTPWizard(WTPOperationDataModel model) {
		this.model = model;
	}

	public WTPWizard() {
		this.model = createDefaultModel();
	}

	/**
	 * Return a new default WTPOperationDataModel.
	 * 
	 * @return
	 */
	protected abstract WTPOperationDataModel createDefaultModel();

	/**
	 * @return the wizard ID that clients should extend to add to this wizard
	 */
	public String getWizardID() {
		return this.getClass().getName();
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


	/**
	 * This is finalized to handle the adding of extended pages. Clients should override
	 * doAddPages() to add their pages.
	 */
	public final void addPages() {
		doAddPages();
		addExtensionPages();
	}

	/**
	 * Subclasses should override this method to add pages.
	 */
	protected void doAddPages() {

	}

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

	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage expectedPage = super.getNextPage(page);
		if (expectedPage instanceof IExtendedWizardPage) {
			IExtendedWizardPage extendedWizardPage = (IExtendedWizardPage) expectedPage;
			if (!EnablementManager.INSTANCE.getIdentifier(extendedWizardPage.getGroupID(), getModel().getTargetProject()).isEnabled())
				return getNextPage(expectedPage);
		}
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

	public boolean canFinish() {
		if (!super.canFinish() || !model.isValid()) {
			return false;
		}
		for (int i = 0; null != extendedPages && i < extendedPages.length; i++) {
			if (!extendedPages[i].canPageFinish()) {
				return false;
			}
		}
		return true;
	}

	protected void resetAfterFinishError() {
		IWizardPage[] pages = getPages();
		for (int i = 0; i < pages.length; i++) {
			WTPWizardPage wtpPage = (WTPWizardPage) pages[i];
			wtpPage.validatePage(true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public final boolean performFinish() {
		WTPOperation op = null;
		boolean wasSuccessful = false;
		try {
			model.setProperty(WTPOperationDataModel.UI_OPERATION_HANLDER, new UIOperationHandler(getShell()));
			if (prePerformFinish()) {
				storeDefaultSettings();
				op = createOperation();
				if (!model.getBooleanProperty(WTPOperationDataModel.RUN_OPERATION)) {
					model.setProperty(WTPOperationDataModel.CACHED_DELAYED_OPERATION, op);
					wasSuccessful = isSuccessfulFinish(op);
					return wasSuccessful;
				}
				if (op != null) {
					IRunnableWithProgress runnable = WTPUIPlugin.getRunnableWithProgress(op);
					try {
						getContainer().run(runForked(), isCancelable(), runnable);
						postPerformFinish();
					} catch (InvocationTargetException e) {
						Logger.getLogger().logError(e);
						ErrorDialog.openError(getShell(), WTPCommonUIResourceHandler.getString("WTPWizard_UI_0", new Object[]{getWindowTitle()}), WTPCommonUIResourceHandler.getString("WTPWizard_UI_1", new Object[]{getWindowTitle()}), e, 0, false); //$NON-NLS-1$ //$NON-NLS-2$
						wasSuccessful = false;
						return wasSuccessful;
					} catch (InterruptedException e) {
						Logger.getLogger().logError(e);
						wasSuccessful = false;
						return wasSuccessful;
					}
				}
			}
			wasSuccessful = isSuccessfulFinish(op);
			return wasSuccessful;
		} finally {
			if (!wasSuccessful) {
				resetAfterFinishError();
			}
		}
	}

	/**
	 * @param op
	 * @return
	 */
	protected boolean isSuccessfulFinish(WTPOperation op) {
		return op != null;
	}

	/**
	 * Subclass can override to perform any tasks prior to running the operation. Return true to
	 * have the operation run and false to stop the execution of the operation.
	 * 
	 * @return
	 */
	protected boolean prePerformFinish() {
		return true;
	}

	/**
	 * Subclasses should override to perform any actions necessary after performing Finish.
	 */
	protected void postPerformFinish() throws InvocationTargetException {
	}

	protected void storeDefaultSettings() {
		IWizardPage[] pages = getPages();
		for (int i = 0; i < pages.length; i++)
			storeDefaultSettings(pages[i], i);
	}

	/**
	 * Subclasses may override if they need to do something special when storing the default
	 * settings for a particular page.
	 * 
	 * @param page
	 * @param pageIndex
	 */
	protected void storeDefaultSettings(IWizardPage page, int pageIndex) {
		if (page instanceof WTPWizardPage)
			((WTPWizardPage) page).storeDefaultSettings();
	}

	/**
	 * Subclasses should override if the running operation is allowed to be cancelled. The default
	 * is false.
	 * 
	 * @return
	 */
	protected boolean isCancelable() {
		return false;
	}

	/**
	 * Subclasses should override to return false if the running operation cannot be run forked.
	 * 
	 * @return
	 */
	protected boolean runForked() {
		return false;
	}


	/**
	 * This is the base operation the wizard will run when finished. If the wizard is extended, then
	 * this operation will run first followed by any extensions.
	 * 
	 * @return
	 */
	protected WTPOperation createBaseOperation() {
		return model.getDefaultOperation();
	}

	/**
	 * Returs the operation this wizard is going to run. This is final to handle extended pages;
	 * subclasses should override createBaseOperation.
	 * 
	 * @return
	 */
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
	 * @return Returns the model.
	 */
	public WTPOperationDataModel getModel() {
		return model;
	}

	public void dispose() {
		super.dispose();
		if (null != model) {
			model.dispose();
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
	 */


	public void addPage(IWizardPage page) {
		if (model.isProperty(WTPWizardSkipPageDataModel.SKIP_PAGES) && null != page.getName()) {
			List pagesToSkip = (List) model.getProperty(WTPWizardSkipPageDataModel.SKIP_PAGES);
			if (null != pagesToSkip && pagesToSkip.contains(page.getName())) {
				return;
			}
		}
		super.addPage(page);
	}
}