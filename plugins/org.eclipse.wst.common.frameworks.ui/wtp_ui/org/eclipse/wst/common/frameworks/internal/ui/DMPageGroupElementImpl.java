/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Kaloyan Raev, kaloyan.raev@sap.com - bug 213927
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
import org.eclipse.wst.common.frameworks.internal.operation.extensionui.DMWizardPageGroupElement;

public class DMPageGroupElementImpl implements IDMPageGroup {
	private DMWizardPageGroupElement pageGroupElement;

	private List pages;
	
	public DMPageGroupElementImpl(IConfigurationElement element) {
		pageGroupElement = new DMWizardPageGroupElement(element);
	}

	public boolean getAllowsExtendedPages() {
		return pageGroupElement.allowsExtendedPagesAfter();
	}

	public String getRequiredDataOperationToRun() {
		return pageGroupElement.getRequiresDataOperationId();
	}

	public Set getDataModelIDs() {
		return pageGroupElement.getDataModelIDs();
	}

	public IDMPageGroupHandler getPageGroupHandler(IDataModel dataModel) {
		return pageGroupElement.createPageGroupHandler(dataModel);
	}

	public List getPages(IDataModel dataModel){
		if (pages == null) {
			pages = Arrays.asList(pageGroupElement.createPageGroup(dataModel));
		}
		return pages;
	}
	
	public IDMPageHandler getPageHandler(IDataModel dataModel) {
		return pageGroupElement.createPageHandler(dataModel);
	}

	public String getPageGroupID() {
		return pageGroupElement.getPageID();
	}

	public String getPageGroupInsertionID() {
		return pageGroupElement.getPageInsertionID();
	}

	public String getWizardID() {
		return pageGroupElement.getWizardID();
	}

}
