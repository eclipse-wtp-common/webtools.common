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
 * Created on May 10, 2004
 *
 */
package org.eclipse.wst.common.frameworks.internal.operation.extensionui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel;



/**
 * @author mdelder
 *  
 */
public class IActionWTPOperationDataModel extends WTPOperationDataModel implements IPropertyChangeListener {

	public static final String IACTION = "IActionWTPOperationDataModel.IACTION"; //$NON-NLS-1$
	public static final String ISTRUCTURED_SELECTION = "IActionWTPOperationDataModel.ISTRUCTURED_SELECTION"; //$NON-NLS-1$
	public static final String ISELECTION_PROVIDER = "IActionWTPOperationDataModel.ISELECTION_PROVIDER"; //$NON-NLS-1$
	public static final String SHELL = "IActionWTPOperationDataModel.SHELL"; //$NON-NLS-1$
	public static final String ACTION_RAN_SUCCESSFULLY = "IActionWTPOperationDataModel.ACTION_RAN_SUCCESSFULLY"; //$NON-NLS-1$


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.operation.WTPOperationDataModel#initValidBaseProperties()
	 */
	protected void initValidBaseProperties() {
		super.initValidBaseProperties();
		addValidBaseProperty(IACTION);
		addValidBaseProperty(ISTRUCTURED_SELECTION);
		addValidBaseProperty(ISELECTION_PROVIDER);
		addValidBaseProperty(SHELL);
		addValidBaseProperty(ACTION_RAN_SUCCESSFULLY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.operation.WTPOperationDataModel#getDefaultOperation()
	 */
	public WTPOperation getDefaultOperation() {
		return new IActionWTPOperation(this);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.operation.WTPOperationDataModel#isResultProperty(java.lang.String)
	 */
	protected boolean isResultProperty(String propertyName) {
		if (ACTION_RAN_SUCCESSFULLY.equals(propertyName))
			return true;
		return super.isResultProperty(propertyName);
	}

	/**
	 * @param action
	 * @param selection
	 * @param provider
	 * @return
	 */
	public static WTPOperationDataModel createDataModel(IActionDelegate action, IStructuredSelection selection, ISelectionProvider provider, Shell shell) {
		WTPOperationDataModel dataModel = new IActionWTPOperationDataModel();
		dataModel.setProperty(IACTION, action);
		dataModel.setProperty(ISTRUCTURED_SELECTION, selection);
		dataModel.setProperty(ISELECTION_PROVIDER, provider);
		dataModel.setProperty(SHELL, shell);
		return dataModel;
	}

	/**
	 * @param action
	 * @param selection
	 * @param provider
	 * @return
	 */
	public static WTPOperationDataModel createDataModel(Action action, IStructuredSelection selection, ISelectionProvider provider, Shell shell) {
		WTPOperationDataModel dataModel = new IActionWTPOperationDataModel();
		dataModel.setProperty(IACTION, action);
		dataModel.setProperty(ISTRUCTURED_SELECTION, selection);
		dataModel.setProperty(ISELECTION_PROVIDER, provider);
		dataModel.setProperty(SHELL, shell);
		return dataModel;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.operation.WTPOperationDataModel#doSetProperty(java.lang.String,
	 *      java.lang.Object)
	 */
	protected boolean doSetProperty(String propertyName, Object propertyValue) {


		if (IACTION.equals(propertyName)) {

			IAction oldAction = (IAction) getProperty(IACTION);
			if (oldAction != null)
				oldAction.removePropertyChangeListener(this);

			boolean result = super.doSetProperty(propertyName, propertyValue);

			IAction newAction = (IAction) getProperty(IACTION);
			if (newAction != null)
				newAction.addPropertyChangeListener(this);

			return result;
		}
		return super.doSetProperty(propertyName, propertyValue);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.operation.WTPOperationDataModel#getDefaultProperty(java.lang.String)
	 */
	protected Object getDefaultProperty(String propertyName) {
		if (ACTION_RAN_SUCCESSFULLY.equals(propertyName))
			return Boolean.TRUE;
		return super.getDefaultProperty(propertyName);
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.operation.WTPOperationDataModel#dispose()
	 */
	public void dispose() {
		super.dispose();
		IAction action = (IAction) getProperty(IACTION);
		if (action != null)
			action.removePropertyChangeListener(this);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		IAction action = (IAction) getProperty(IACTION);
		if (action == null || action != event.getSource())
			return;
		// TODO MDE Implement this method
		//notifyListeners(event.getProperty(), 0, event.getOldValue(), event.getNewValue());
		if (IAction.RESULT.equals(event.getProperty())) {
			setProperty(ACTION_RAN_SUCCESSFULLY, event.getNewValue());
		}

	}

}