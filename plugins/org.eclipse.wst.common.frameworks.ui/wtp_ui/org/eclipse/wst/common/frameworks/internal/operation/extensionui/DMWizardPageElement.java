/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.frameworks.internal.operation.extensionui;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.AbstractRegistryDescriptor;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizardPage;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.IDMExtendedPageGroupHandler;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.IDMExtendedPageHandler;
import org.eclipse.wst.common.frameworks.internal.enablement.IdentifiableComparator;

public class DMWizardPageElement extends AbstractRegistryDescriptor implements Comparable {
	/** Type designation for an {@link ExtendedEditorPage} */
	public static final int EXTENDED_EDITOR_PAGE = 0;
	/** Type designation for a nested editor */
	public static final int NESTED_EDITOR = 1;
	static final String[] EMPTY_STRING_ARRAY = new String[0];
	static final String ELEMENT_PAGE_GROUP = "wizardPageGroup"; //$NON-NLS-1$
	static final String ATT_PAGE_ID = "pageGroupID"; //$NON-NLS-1$
	static final String ATT_WIZARD_ID = "wizardID"; //$NON-NLS-1$
	static final String ATT_REQUIRES_DATA_OPERATION_ID = "requiresDataOperationId"; //$NON-NLS-1$
	static final String ATT_DATA_MODEL_ID = "dataModelId"; //$NON-NLS-1$
	static final String ATT_ALLOWS_EXTENDED_PAGES_AFTER = "allowsExtendedPagesAfter"; //$NON-NLS-1$
	static final String ATT_PAGE_INSERTION_ID = "pageGroupInsertionID"; //$NON-NLS-1$
	static final String ELEMENT_FACTORY = "factory"; //$NON-NLS-1$

	protected DMWizardPageFactoryElement wizardPageFactoryElement;
	protected String pluginID;
	protected String wizardID;
	public String pageGroupID;
	protected String wizardFactoryElement;
	protected boolean allowsExtendedPagesAfter;
	protected String requiresDataOperationId;
	protected String dataModelID;
	protected String pageInsertionID;
	private int loadOrder;
	private static int loadOrderCounter;


	private int type;

	public DMWizardPageElement(IConfigurationElement element1) {
		super(element1);
		pluginID = element1.getDeclaringExtension().getNamespace();
		wizardID = element1.getAttribute(ATT_WIZARD_ID);
		pageGroupID = element1.getAttribute(ATT_PAGE_ID);
		requiresDataOperationId = element1.getAttribute(ATT_REQUIRES_DATA_OPERATION_ID);
		dataModelID = element1.getAttribute(ATT_DATA_MODEL_ID);
		readAllowsExtendedPageAfter(element1);
		pageInsertionID = element1.getAttribute(ATT_PAGE_INSERTION_ID);
		readFactory(element1);
		validateSettings();
		loadOrder = loadOrderCounter++;
	}

	private void validateSettings() {
		if (wizardID == null || wizardPageFactoryElement == null) {
			Logger.getLogger().logError("Incomplete page extension specification."); //$NON-NLS-1$
		}
	}


	private void readAllowsExtendedPageAfter(IConfigurationElement element1) {
		String allowsPageAfterValue = element1.getAttribute(ATT_ALLOWS_EXTENDED_PAGES_AFTER);
		allowsExtendedPagesAfter = allowsPageAfterValue == null ? false : Boolean.valueOf(allowsPageAfterValue).booleanValue();
	}

	private void readFactory(IConfigurationElement element1) {
		IConfigurationElement[] factories = element1.getChildren(ELEMENT_FACTORY);
		if (factories != null && factories.length > 0) {
			wizardPageFactoryElement = new DMWizardPageFactoryElement(factories[0], pageGroupID);
		}
	}

	public IDMExtendedPageHandler createPageHandler(IDataModel dataModel) {
		if (wizardPageFactoryElement != null)
			return wizardPageFactoryElement.createPageHandler(dataModel);
		return null;
	}

	public DataModelWizardPage[] createPageGroup(IDataModel dataModel) {
		if (wizardPageFactoryElement != null)
			return wizardPageFactoryElement.createPageGroup(dataModel);
		return null;
	}

	public IDMExtendedPageGroupHandler createPageGroupHandler(IDataModel dataModel) {
		return wizardPageFactoryElement == null ? null : wizardPageFactoryElement.createPageGroupHandler(dataModel);
	}

	public int compareTo(Object o) {
		return IdentifiableComparator.getInstance().compare(this, o);
		/*
		 * if (o == null) return GREATER_THAN; WizardPageElement element = (WizardPageElement) o; if
		 * (getID() == null && element.getID() == null) return compareLoadOrder(element); if
		 * (getID() == null) return GREATER_THAN; else if (element.getID() == null) return
		 * LESS_THAN;
		 * 
		 * int priority = getPriority(); int elementPriority =element.getPriority();
		 * 
		 * if (priority == elementPriority) return compareLoadOrder(element); if (priority <
		 * elementPriority) return GREATER_THAN; if (priority > elementPriority) return LESS_THAN;
		 * return EQUAL;
		 */
	}

	/**
	 * @return
	 */
	public boolean allowsExtendedPagesAfter() {
		return allowsExtendedPagesAfter;
	}

	/**
	 * @return
	 */
	public String getPluginID() {
		return pluginID;
	}

	/**
	 * @return
	 */
	public String getPageID() {
		return pageGroupID;
	}

	/**
	 * @return
	 */
	public String getPageInsertionID() {
		return pageInsertionID;
	}

	/**
	 * @return
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return
	 */
	public int getLoadOrder() {
		return loadOrder;
	}


	/**
	 * @return Returns the allowsExtendedPagesAfter.
	 */
	public boolean isAllowsExtendedPagesAfter() {
		return allowsExtendedPagesAfter;
	}

	/**
	 * @param allowsExtendedPagesAfter
	 *            The allowsExtendedPagesAfter to set.
	 */
	public void setAllowsExtendedPagesAfter(boolean allowsExtendedPagesAfter) {
		this.allowsExtendedPagesAfter = allowsExtendedPagesAfter;
	}

	public String getRequiresDataOperationId() {
		return requiresDataOperationId;
	}


	public void setRequiresDataOperationId(String dataOperationId) {
		requiresDataOperationId = dataOperationId;
	}

	public String getDataModelID() {
		return dataModelID;
	}

	public void setDataModelID(String newDataModelID) {
		dataModelID = newDataModelID;
	}

	/**
	 * @return Returns the wizardFactoryElement.
	 */
	public String getWizardFactoryElement() {
		return wizardFactoryElement;
	}

	/**
	 * @param wizardFactoryElement
	 *            The wizardFactoryElement to set.
	 */
	public void setWizardFactoryElement(String wizardFactoryElement) {
		this.wizardFactoryElement = wizardFactoryElement;
	}

	/**
	 * @return Returns the wizardID.
	 */
	public String getWizardID() {
		return wizardID;
	}

	/**
	 * @param wizardID
	 *            The wizardID to set.
	 */
	public void setWizardID(String wizardID) {
		this.wizardID = wizardID;
	}

	/**
	 * @return Returns the wizardPageFactoryElement.
	 */
	public DMWizardPageFactoryElement getWizardPageFactoryElement() {
		return wizardPageFactoryElement;
	}

	/**
	 * @param wizardPageFactoryElement
	 *            The wizardPageFactoryElement to set.
	 */
	public void setWizardPageFactoryElement(DMWizardPageFactoryElement wizardPageFactoryElement) {
		this.wizardPageFactoryElement = wizardPageFactoryElement;
	}


	/**
	 * @param pageID
	 *            The pageID to set.
	 */
	public void setPageID(String pageID) {
		this.pageGroupID = pageID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.AbstractRegistryDescriptor#getID()
	 */
	public String getID() {
		return getPageID();
	}

}