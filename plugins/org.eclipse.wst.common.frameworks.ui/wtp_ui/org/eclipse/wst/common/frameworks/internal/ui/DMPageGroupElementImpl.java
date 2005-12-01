/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.ui;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.IDMPageGroup;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.IDMPageGroupHandler;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.IDMPageHandler;
import org.eclipse.wst.common.frameworks.internal.operation.extensionui.DMWizardPageElement;

public class DMPageGroupElementImpl implements IDMPageGroup {
	private DMWizardPageElement pageElement;

	public DMPageGroupElementImpl(IConfigurationElement element) {
		pageElement = new DMWizardPageElement(element);
	}

	public boolean getAllowsExtendedPages() {
		return pageElement.allowsExtendedPagesAfter();
	}

	public String getRequiredDataOperationToRun() {
		return pageElement.getRequiresDataOperationId();
	}

	public Set getDataModelIDs() {
		return pageElement.getDataModelIDs();
	}

	public IDMPageGroupHandler getPageGroupHandler(IDataModel dataModel) {
		return pageElement.createPageGroupHandler(dataModel);
	}

	public List getPages(IDataModel dataModel){
		return Arrays.asList(pageElement.createPageGroup(dataModel));
	}
	
	public IDMPageHandler getPageHandler(IDataModel dataModel) {
		return pageElement.createPageHandler(dataModel);
	}

	public String getPageGroupID() {
		return pageElement.getPageID();
	}

	public String getPageGroupInsertionID() {
		return pageElement.getPageInsertionID();
	}

	public String getWizardID() {
		return pageElement.getWizardID();
	}

}
