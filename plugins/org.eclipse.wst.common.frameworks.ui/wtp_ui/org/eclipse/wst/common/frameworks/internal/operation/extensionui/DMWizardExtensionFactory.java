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
import org.eclipse.wst.common.frameworks.datamodel.ui.IDMExtendedPageHandler;
import org.eclipse.wst.common.frameworks.datamodel.ui.IDMExtendedWizardPage;

/**
 * This interface is EXPERIMENTAL and is subject to substantial changes.
 */
public abstract class DMWizardExtensionFactory {

	public DMWizardExtensionFactory() {
		super();
	}

	public abstract IDMExtendedWizardPage[] createPageGroup(IDataModel dataModel, String pageGroupID);

	/*
	 * this is optional
	 */
	public IDMExtendedPageHandler createPageHandler(IDataModel dataModel, String pageGroupID) {
		return null;
	}
}