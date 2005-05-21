/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
/*
 * Created on May 5, 2004
 * 
 * TODO To change the template for this generated file go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */
package org.eclipse.wst.common.frameworks.internal.operation.extensionui;

import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.internal.ActionExpression;
import org.eclipse.wst.common.frameworks.internal.ui.WTPCommonUIResourceHandler;

public class MasterDescriptor extends SlaveDescriptor {

	public static final String MASTER_OPERATION = "masterOperation"; //$NON-NLS-1$

	public static final String ATT_EXTENDED_OPERATION_ID = "extendedGenericId"; //$NON-NLS-1$

	public static final String SELECTION_ENABLEMENT = "selectionEnablement"; //$NON-NLS-1$

	private static final String ATT_POPULATOR_CLASS = "populatorClass"; //$NON-NLS-1$

	private static final String ATT_ALWAYS_EXECUTE = "alwaysExecute"; //$NON-NLS-1$

	private ActionExpression enablement;

	private String extendedOperationId;

	private WTPOperationDataModelUICreator creator;

	private boolean alwaysExecute;


	public MasterDescriptor(IConfigurationElement element) {
		super(element);
		init();
	}

	private void init() {
		this.extendedOperationId = getElement().getAttribute(ATT_EXTENDED_OPERATION_ID);
		if (null == extendedOperationId)
			Logger.getLogger().log(WTPCommonUIResourceHandler.getString("MasterDescriptor_UI_0", new Object[]{ATT_EXTENDED_OPERATION_ID})); //$NON-NLS-1$


		if (Boolean.valueOf(getElement().getAttribute(ATT_ALWAYS_EXECUTE)).booleanValue())
			alwaysExecute = true;

		IConfigurationElement[] elements = getElement().getChildren(SELECTION_ENABLEMENT);
		if (elements.length == 1)
			this.enablement = new ActionExpression(elements[0]);
		else
			Logger.getLogger().log(WTPCommonUIResourceHandler.getString("MasterDescriptor_ERROR_2")); //$NON-NLS-1$

	}

	/**
	 * @return Returns the extendedOperationId.
	 */
	public String getExtendedOperationId() {
		return extendedOperationId;
	}


	public boolean isEnabledFor(IStructuredSelection selection) {
		if (getEnablement() == null)
			return true;
		boolean result = false;
		for (Iterator itr = selection.iterator(); itr.hasNext();) {
			result = getEnablement().isEnabledFor(itr.next());
			if (result)
				break;
		}
		return result;
	}

	public WTPOperationDataModelUICreator getCreator() {
		if (creator == null) {
			try {
				creator = (WTPOperationDataModelUICreator) getElement().createExecutableExtension(ATT_POPULATOR_CLASS);
			} catch (CoreException e) {
				Logger.getLogger().logError(e);
			}
		}
		return creator;
	}

	protected ActionExpression getEnablement() {
		return enablement;
	}


	/**
	 * @return Returns the alwaysExecute.
	 */
	public boolean isAlwaysExecute() {
		return alwaysExecute;
	}
}