/*******************************************************************************
 * Copyright (c) 2005, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
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

	@Override
	public boolean getAllowsExtendedPages() {
		return pageGroupElement.allowsExtendedPagesAfter();
	}

	@Override
	public String getRequiredDataOperationToRun() {
		return pageGroupElement.getRequiresDataOperationId();
	}

	public Set getDataModelIDs() {
		return pageGroupElement.getDataModelIDs();
	}

	@Override
	public IDMPageGroupHandler getPageGroupHandler(IDataModel dataModel) {
		return pageGroupElement.createPageGroupHandler(dataModel);
	}

	@Override
	public List getPages(IDataModel dataModel){
		if (pages == null) {
			pages = Arrays.asList(pageGroupElement.createPageGroup(dataModel));
		}
		return pages;
	}
	
	@Override
	public IDMPageHandler getPageHandler(IDataModel dataModel) {
		return pageGroupElement.createPageHandler(dataModel);
	}

	@Override
	public String getPageGroupID() {
		return pageGroupElement.getPageID();
	}

	@Override
	public String getPageGroupInsertionID() {
		return pageGroupElement.getPageInsertionID();
	}

	@Override
	public String getWizardID() {
		return pageGroupElement.getWizardID();
	}

}
