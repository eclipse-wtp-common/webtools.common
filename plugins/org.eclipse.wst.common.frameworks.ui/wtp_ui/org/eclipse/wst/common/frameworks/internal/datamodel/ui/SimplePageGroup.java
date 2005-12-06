/***************************************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.frameworks.internal.datamodel.ui;

import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

public class SimplePageGroup implements AddablePageGroup {
	private String groupID;
	private String wizardID;
	private boolean allowExtendedPages;
	private String requiredDataOperation;
	private Set    dataModelIDs;

	private IDMPageHandler pageHandler;
	private IDMPageGroupHandler pageGroupHandler;
	private Vector pages;

	public SimplePageGroup(String groupID, String wizardID, boolean allowExtendedPages, String requireDataOperation) {
		this.groupID = groupID;
		this.wizardID = wizardID;
		this.allowExtendedPages = allowExtendedPages;
		this.requiredDataOperation = requireDataOperation;
		pages = new Vector();
	}

	public SimplePageGroup(String groupID, String wizardID) {
		this(groupID, wizardID, true, null);
	}

	public void addPage(IWizardPage page) {
		pages.add(page);
	}

	public void addPages(IWizardPage[] newPages) {
		for (int index = 0; index < newPages.length; index++) {
			pages.add(newPages[index]);
		}
	}

	public boolean getAllowsExtendedPages() {
		return allowExtendedPages;
	}

	public void setPageGroupHandler(IDMPageGroupHandler handler) {
		pageGroupHandler = handler;
	}

	public IDMPageGroupHandler getPageGroupHandler(IDataModel dataModel) {
		return pageGroupHandler;
	}

	public void setPageHandler(IDMPageHandler handler) {
		pageHandler = handler;
	}

	public IDMPageHandler getPageHandler(IDataModel dataModel) {
		return pageHandler;
	}

	public List getPages(IDataModel dataModel) {
		return pages;
	}
	
	public IWizardPage[] getExtendedPages(IDataModel dataModel) {
		return (IWizardPage[]) pages.toArray(new IWizardPage[0]);
	}

	public String getPageGroupID() {
		return groupID;
	}

	public String getPageGroupInsertionID() {
		return null;
	}

	public String getRequiredDataOperationToRun() {
		return requiredDataOperation;
	}

	public String getWizardID() {
		return wizardID;
	}

	public Set getDataModelIDs() {
		return dataModelIDs;
	}

	public void setDataModelIDs(Set dataModelIDs) {
		this.dataModelIDs = dataModelIDs;
	}
}
