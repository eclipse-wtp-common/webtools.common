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
package org.eclipse.wst.common.frameworks.internal.datamodel.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.wst.common.environment.IEnvironment;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelPausibleOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;
import org.eclipse.wst.common.frameworks.internal.eclipse.ui.EclipseEnvironment;
import org.eclipse.wst.common.frameworks.internal.ui.ErrorDialog;
import org.eclipse.wst.common.frameworks.internal.ui.PageGroupManager;
import org.eclipse.wst.common.frameworks.internal.ui.WTPCommonUIResourceHandler;
import org.eclipse.wst.common.frameworks.internal.ui.WTPUIPlugin;


/**
 * This class is EXPERIMENTAL and is subject to substantial changes.
 */
public abstract class DataModelWizard extends Wizard implements IDMPageHandler {
	private PageGroupManager pageGroupManager;
	private IDataModel dataModel;
	private AddablePageGroup rootPageGroup;
	private IDataModelPausibleOperation rootOperation;

	// private IWizardPage firstpage;

	public DataModelWizard(IDataModel dataModel) {
		this.dataModel = dataModel;
	}

	public DataModelWizard() {
	}

	protected abstract IDataModelProvider getDefaultProvider();

	/**
	 * @return the wizard ID that clients should extend to add to this wizard
	 */
	public final String getWizardID() {
		return getDataModel().getID();
	}

	/**
	 * 
	 * @return returns the root operation for this wizard.
	 */
	protected IDataModelPausibleOperation getRootOperation() {
		return getDataModel().getDefaultOperation();
	}

	/**
	 * This is finalized to handle the adding of extended pages. Clients should override
	 * doAddPages() to add their pages.
	 */
	public final void addPages() {
		init();
		doAddPages();
	}

	/**
	 * Subclasses should override this method to add pages.
	 */
	protected void doAddPages() {
	}

	// TODO make this final
	public IWizardPage getStartingPage() {
		pageGroupManager.reset();
		return getNextPage(null);
	}

	/**
	 * Subclasses wishing to control the page ordering should do so by overriding
	 * getNextPage(String, String) and getPreviousPage(String, String)
	 * 
	 * @link #getNextPage(String, String)
	 * @link #getPreviousPage(String, String)
	 */
	// TODO make this final
	public IWizardPage getNextPage(IWizardPage page) {

		IWizardPage currentPage = pageGroupManager.getCurrentPage();

		pageGroupManager.moveForwardOnePage();

		IWizardPage nextPage = pageGroupManager.getCurrentPage();

		// If an error occured then the current page and the next page will be the same.
		if (currentPage != nextPage && nextPage != null) {
			nextPage.setWizard(this);
			nextPage.setPreviousPage(currentPage);
		}

		return currentPage == nextPage ? null : nextPage;
	}

	public String getNextPage(String currentPageName, String expectedNextPageName) {
		return expectedNextPageName;
	}

	/**
	 * Subclasses wishing to control the page ordering should do so by overriding
	 * getNextPage(String, String) and getPreviousPage(String, String)
	 * 
	 * @link #getNextPage(String, String)
	 * @link #getPreviousPage(String, String)
	 */
	// TODO make this final
	public IWizardPage getPreviousPage(IWizardPage page) {
		return page != null ? page.getPreviousPage() : null;
	}

	public String getPreviousPage(String currentPageName, String expectedPreviousPageName) {
		return expectedPreviousPageName;
	}

	public boolean canFinish() {
		if (!super.canFinish() || !getDataModel().isValid()) {
			return false;
		}

		return true;
	}

	public PageGroupManager getPageGroupManager() {
		return pageGroupManager;
	}

	// TODO need to implement this. Perhaps in the PageGroupManager
	//
	protected void resetAfterFinishError() {
		// IWizardPage[] pages = getPages();
		// for (int i = 0; i < pages.length; i++) {
		// DataModelWizardPage wtpPage = (DataModelWizardPage) pages[i];
		// wtpPage.validatePage(true);
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public final boolean performFinish() {
		try {
			if (prePerformFinish()) {
				storeDefaultSettings();

				final IStatus st = runOperations();

				if (st.getSeverity() == IStatus.ERROR) {
					WTPUIPlugin.log(st);
					ErrorDialog.openError(getShell(), WTPCommonUIResourceHandler.getString(WTPCommonUIResourceHandler.WTPWizard_UI_0, new Object[]{getWindowTitle()}), WTPCommonUIResourceHandler.getString(WTPCommonUIResourceHandler.WTPWizard_UI_1, new Object[]{getWindowTitle()}), new CoreException(st), 0, false); //$NON-NLS-1$ //$NON-NLS-2$
				}

				postPerformFinish();
			}
		} catch (Throwable exc) {
			WTPUIPlugin.log(exc);
			ErrorDialog.openError(getShell(), WTPCommonUIResourceHandler.getString(WTPCommonUIResourceHandler.WTPWizard_UI_0, new Object[]{getWindowTitle()}), WTPCommonUIResourceHandler.getString(WTPCommonUIResourceHandler.WTPWizard_UI_1, new Object[]{getWindowTitle()}), exc, 0, false); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return true;
	}

	private IStatus runOperations() {
		final IStatus[] status = new IStatus[1];
		class CatchThrowableRunnableWithProgress implements IRunnableWithProgress {
			public Throwable caught = null;

			public void run(IProgressMonitor pm) {
				try {
					if (rootOperation == null) {
						//This will be the typical case because most wizards will
						//not initialize the root operation during init.
						rootOperation = getRootOperation();
					}
					status[0] = rootOperation.execute(pm, null);
				} catch (Throwable e) {
					caught = e;
				}
			}
		}
		CatchThrowableRunnableWithProgress runnable = new CatchThrowableRunnableWithProgress();

		try {
			getContainer().run(runForked(), isCancelable(), runnable);
		} catch (Throwable e) {
			runnable.caught = e;
		}
		if (runnable.caught != null) {
			Logger.getLogger().logError(runnable.caught);
			status[0] = new Status(IStatus.ERROR, "id", 0, runnable.caught.getMessage(), runnable.caught); //$NON-NLS-1$
			ErrorDialog.openError(getShell(), WTPCommonUIResourceHandler.getString(WTPCommonUIResourceHandler.WTPWizard_UI_0, new Object[]{getWindowTitle()}), WTPCommonUIResourceHandler.getString(WTPCommonUIResourceHandler.WTPWizard_UI_1, new Object[]{getWindowTitle()}), runnable.caught, 0, false);
		}
		return status[0];
	}

	public boolean performCancel() {
		pageGroupManager.undoAllCurrentOperations();

		return true;
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
		pageGroupManager.storeDefaultSettings(this);
	}

	public void storeDefaultSettings(IWizardPage page) {
		if (page instanceof DataModelWizardPage)
			((DataModelWizardPage) page).storeDefaultSettings();
	}


	/**
	 * Subclasses may override if they need to do something special when storing the default
	 * settings for a particular page.
	 * 
	 * @param page
	 * @param pageIndex
	 */
	protected void storeDefaultSettings(IWizardPage page, int pageIndex) {
		storeDefaultSettings(page);
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

	public void setDataModel(IDataModel model) {
		this.dataModel = model;
	}

	/**
	 * @return Returns the model.
	 */
	public IDataModel getDataModel() {
		if (null == dataModel) {
			dataModel = DataModelFactory.createDataModel(getDefaultProvider());
		}

		return dataModel;
	}

	public void dispose() {
		super.dispose();
		if (null != dataModel) {
			dataModel.dispose();
		}
	}

	/**
	 * The default is to return a SimplePageGroup. Subclasses may want to overrided this method to
	 * return a different root page group for the wizard.
	 * 
	 * @return
	 */
	protected AddablePageGroup createRootPageGroup() {
		String id = getWizardID();
		// For the root page group the wizard id and the group id are the same.
		SimplePageGroup pageGroup = new SimplePageGroup(id, id);
		pageGroup.setPageHandler(this);
		return pageGroup;
	}

	/**
	 * Creates the default environment for this wizard.
	 * 
	 * @return
	 */
	protected IEnvironment createEnvironment() {
		return new EclipseEnvironment();
	}

	public void addPage(IWizardPage page) {
		rootPageGroup.addPage(page);
	}

	private void init() {
		rootPageGroup = createRootPageGroup();
		if (needsToRunOperationsBeforeFinish()) {
			rootOperation = getRootOperation();
			pageGroupManager = new PageGroupManager(rootOperation, rootPageGroup);
		} else {
			pageGroupManager = new PageGroupManager(getDataModel(), rootPageGroup);
		}
	}
	
	/**
	 * Subclasses should override to return true if they require the running
	 * of the operation during page turning.
	 * 
	 * @return A boolean defaulted to false.
	 */
	protected boolean needsToRunOperationsBeforeFinish() {
		return false;
	}

	public boolean needsPreviousAndNextButtons() {
		return super.needsPreviousAndNextButtons() || getPageGroupManager().hasMultiplePages();
	}

}
