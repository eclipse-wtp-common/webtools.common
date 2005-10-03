/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.frameworks.internal.operation.extensionui;

import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizardPage;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.IDMExtendedPageGroupHandler;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.IDMExtendedPageHandler;

/**
 * This interface is EXPERIMENTAL and is subject to substantial changes.
 */
public abstract class DMWizardExtensionFactory {

	public DMWizardExtensionFactory() {
		super();
	}

	public abstract DataModelWizardPage[] createPageGroup(IDataModel dataModel, String pageGroupID);

	/*
	 * this is optional
	 */
	public IDMExtendedPageHandler createPageHandler(IDataModel dataModel, String pageGroupID) 
	{
	  return null;
	}
		
	/**
	 * This page group handler can be optionally overriden.
	 */
	public IDMExtendedPageGroupHandler createPageGroupHandler( IDataModel dataModel, String pageGroupID )
	{
	  return null;
	}
}