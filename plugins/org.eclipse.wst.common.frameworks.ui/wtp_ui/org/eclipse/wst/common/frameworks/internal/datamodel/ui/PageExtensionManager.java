/*******************************************************************************
 * Copyright (c) 2008, 2019 SAP AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * Kaloyan Raev, kaloyan.raev@sap.com - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.datamodel.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.operation.extensionui.CommonUIPluginConstants;
import org.eclipse.wst.common.frameworks.internal.operation.extensionui.DMWizardPageElement;

/**
 * This class organizes the wizard page extensions for a particular wizard. 
 * 
 * @author kraev
 */
public class PageExtensionManager {
	
	/**
	 * The name of the extension point where the wizard page extensions are
	 * contributed.
	 */
	public static final String POINT_WIZARD_PAGE_GROUP = "wizardPageGroup"; //$NON-NLS-1$
	
	/**
	 * The name of the extension point's element where the wizard page
	 * extensions are contributed.
	 */
	public static final String ELEMENT_WIZARD_PAGE = "wizardPage"; //$NON-NLS-1$
	
	private String wizardID;
	private DMWizardPageElement[] elements;
	
	/**
	 * Constructs page extension manager for the specified wizard.
	 * 
	 * @param wizard -
	 *            the wizard to construct the manager for.
	 */
	public PageExtensionManager(DataModelWizard wizard) {
		this.wizardID = wizard.getWizardID();
		loadElements();
	}
	
	/**
	 * Create additional controls for the specified wizard page.
	 * 
	 * <p>
	 * The additional controls are contributed by the wizard page extensions
	 * that are registered in the <cite>wizardPage</cite> element of the
	 * <cite>wizardPageGroup</cite> extension point and the <cite>wizardID</cite>
	 * attribute points to the current wizard.
	 * </p>
	 * 
	 * @param parent -
	 *            the parent composite where the additional controls will be
	 *            added to.
	 * @param model -
	 *            the data model of the wizard.
	 * @param pageName -
	 *            the name of the extended wizard page.
	 */
	public void createAdditionalControls(Composite parent, IDataModel model, String pageName) {
		for (int i = 0; i < elements.length; i++) {
			DMWizardPageElement element = elements[i];
			if (wizardID.equals(element.getWizardID())) {
				element.createAdditionalControls(parent, model, pageName);
			}
		}
	}
	
	private void loadElements() { 
		List result = new ArrayList();
		
		IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(CommonUIPluginConstants.PLUGIN_ID, POINT_WIZARD_PAGE_GROUP);
		IConfigurationElement[] allElements = point.getConfigurationElements();
		for (int i = 0; i < allElements.length; i++) {
			IConfigurationElement element = allElements[i];
			if (ELEMENT_WIZARD_PAGE.equals(element.getName())) {
				result.add(new DMWizardPageElement(element));
			}
		}
		
		elements = (DMWizardPageElement[]) result.toArray(new DMWizardPageElement[] { });
	}

}
