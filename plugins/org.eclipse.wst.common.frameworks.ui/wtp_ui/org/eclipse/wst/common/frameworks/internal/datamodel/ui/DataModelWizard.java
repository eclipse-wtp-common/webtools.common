/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.frameworks.internal.datamodel.ui;

import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;
import org.eclipse.wst.common.frameworks.internal.DataModelManager;
import org.eclipse.wst.common.frameworks.internal.OperationManager;
import org.eclipse.wst.common.frameworks.internal.ui.ErrorDialog;
import org.eclipse.wst.common.frameworks.internal.ui.PageGroupManager;
import org.eclipse.wst.common.frameworks.internal.ui.WTPCommonUIResourceHandler;


/**
 * This class is EXPERIMENTAL and is subject to substantial changes.
 */
public abstract class DataModelWizard extends Wizard {
	private DataModelManager dataModelManager;
	private OperationManager operationManager;
	private PageGroupManager pageGroupManager;
	private IDataModel dataModel;
	private AddablePageGroup rootPageGroup;
	private IWizardPage firstpage;

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
	protected IDataModelOperation getRootOperation() {
		return getDefaultProvider().getDefaultOperation();
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


	public IWizardPage getStartingPage() {
		if (firstpage == null) {
			firstpage = getNextPage(null);
		}

		return firstpage;
	}

	public IWizardPage getNextPage(IWizardPage page) {

		IWizardPage currentPage = pageGroupManager.getCurrentPage();

		pageGroupManager.moveForwardOnePage();

		IWizardPage nextPage = pageGroupManager.getCurrentPage();

		// If an error occured then the current page and the next page will be the same.
		if (currentPage != nextPage && nextPage != null) {
			nextPage.setWizard(this);
			nextPage.setPreviousPage(currentPage);
		}

		return nextPage;
	}

	public IWizardPage getPreviousPage(IWizardPage page) {
		return page != null ? page.getPreviousPage() : null;
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
		boolean wasSuccessful = false;

		try {
			if (prePerformFinish()) {
				storeDefaultSettings();

				wasSuccessful = pageGroupManager.runAllRemainingOperations();
				postPerformFinish();
			}
		} catch (Throwable exc) {
			wasSuccessful = false;
			// TODO log error
		}

		return wasSuccessful;
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
		if (page instanceof DataModelWizardPage)
			((DataModelWizardPage) page).storeDefaultSettings();
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
		return new SimplePageGroup(id, id);
	}

	public void addPage(IWizardPage page) {
		rootPageGroup.addPage((DataModelWizardPage) page);
	}

	private void init() {
		dataModelManager = new DataModelManager(getDataModel());
		operationManager = new WizardOperationManager(dataModelManager, getRootOperation());
		rootPageGroup = createRootPageGroup();
		pageGroupManager = new PageGroupManager(operationManager, dataModelManager, rootPageGroup);
	}

	private class WizardOperationManager extends OperationManager {
		public WizardOperationManager(DataModelManager dataModelManager, IDataModelOperation rootOperation) {
			super(dataModelManager, rootOperation);
		}

		public IStatus runOperations() {
			final IStatus[] status = new IStatus[1];
			IRunnableWithProgress runnable = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {
					setProgressMonitor(monitor);
					status[0] = WizardOperationManager.super.runOperations();
				}
			};
			try {
				getContainer().run(runForked(), isCancelable(), runnable);
			} catch (Throwable exc) {
				Logger.getLogger().logError(exc);
				ErrorDialog.openError(getShell(), WTPCommonUIResourceHandler.getString("WTPWizard_UI_0", new Object[]{getWindowTitle()}), WTPCommonUIResourceHandler.getString("WTPWizard_UI_1", new Object[]{getWindowTitle()}), exc, 0, false); //$NON-NLS-1$ //$NON-NLS-2$
				status[0] = new Status(Status.ERROR, "id", 0, exc.getMessage(), exc);
			}

			return status[0];
		}

		public void undoLastRun() {
			IRunnableWithProgress runnable = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {
					setProgressMonitor(monitor);
					WizardOperationManager.super.undoLastRun();
				}
			};
			try {
				getContainer().run(runForked(), isCancelable(), runnable);
			} catch (Throwable exc) {
				Logger.getLogger().logError(exc);
				ErrorDialog.openError(getShell(), WTPCommonUIResourceHandler.getString("WTPWizard_UI_0", new Object[]{getWindowTitle()}), WTPCommonUIResourceHandler.getString("WTPWizard_UI_1", new Object[]{getWindowTitle()}), exc, 0, false); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
}