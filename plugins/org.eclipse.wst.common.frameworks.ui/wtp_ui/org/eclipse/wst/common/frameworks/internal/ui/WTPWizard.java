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
import java.util.List;

import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.wst.common.frameworks.internal.operation.extensionui.IExtendedWizardPage;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel;
import org.eclispe.wst.common.frameworks.internal.enablement.EnablementManager;

import com.ibm.wtp.common.logger.proxy.Logger;


public abstract class WTPWizard extends Wizard {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#canFinish()
	 */
	public boolean canFinish() {
		if (super.canFinish())
			return model.isValid();
		return false;
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
	 * Return the operation that will perform the task suported by this wizard.
	 * 
	 * @return IHeadlessRunnableWithProgress
	 */
	protected abstract WTPOperation createOperation();

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
	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage nextPage = super.getNextPage(page);

		if (nextPage instanceof IExtendedWizardPage) {
			IExtendedWizardPage extendedWizardPage = (IExtendedWizardPage) nextPage;
			if (!EnablementManager.INSTANCE.getIdentifier(extendedWizardPage.getGroupID(), getModel().getTargetProject()).isEnabled())
				return getNextPage(nextPage);
		}
		return nextPage;
	}

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