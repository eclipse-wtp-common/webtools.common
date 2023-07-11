/*******************************************************************************
 * Copyright (c) 2003, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.operation.extensionui;

import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizardPage;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.IDMPageGroupHandler;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.IDMPageHandler;

public abstract class DMWizardExtensionFactory {

	public DMWizardExtensionFactory() {
		super();
	}

	public abstract DataModelWizardPage[] createPageGroup(IDataModel dataModel, String pageGroupID);

	/*
	 * this is optional
	 */
	public IDMPageHandler createPageHandler(IDataModel dataModel, String pageGroupID) 
	{
	  return null;
	}
		
	/**
	 * This page group handler can be optionally overriden.
	 */
	public IDMPageGroupHandler createPageGroupHandler( IDataModel dataModel, String pageGroupID )
	{
	  return null;
	}
}
