/*******************************************************************************
 * Copyright (c) 2008 SAP AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * Kaloyan Raev, kaloyan.raev@sap.com - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.operation.extensionui;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.AbstractRegistryDescriptor;
import org.eclipse.wst.common.frameworks.internal.enablement.Identifiable;

/**
 * This class provides convenient methods for accessing the semantics of the
 * given <code>IConfigurationElement</code> in the context of the the
 * <cite>wizardPage</cite> element of the <cite>wizardPageGroup</cite>
 * extension point.
 * 
 * @author kraev
 */
public class DMWizardPageElement extends AbstractRegistryDescriptor {
	
	/**
	 * The name of the attribute that points to the wizard, which pages are
	 * extended by the current contribution.
	 */
	public static final String ATT_WIZARD_ID = "wizardID"; //$NON-NLS-1$
	
	/**
	 * The name of the element that contains the factory class name, where is
	 * the Java code that contributes the new controls to the wizard pages.
	 */
	public static final String ELEMENT_FACTORY = "factory"; //$NON-NLS-1$
	
	protected String wizardID;
	protected DMWizardPageFactoryElement factoryElement;
	
	private int loadOrder;
	private static int loadOrderCounter;
	
	/**
	 * Constructs a new <code>DMWizardPageElement</code> from the given
	 * <code>IConfigurationElement</code>.
	 * 
	 * @param element -
	 *            the <code>IConfigurationElement</code> to wrap.
	 */
	public DMWizardPageElement(IConfigurationElement element) {
		super(element);
		
		wizardID = element.getAttribute(ATT_WIZARD_ID);
		readFactory(element);
		
		loadOrder = loadOrderCounter++;
	}

	/**
	 * @see Identifiable#getID()
	 */
	@Override
	public String getID() {
		String id = wizardID;
		if (factoryElement != null) {
			id = id + "@" + factoryElement.className; //$NON-NLS-1$
		}
		return id; 
	}

	/**
	 * @see Identifiable#getLoadOrder()
	 */
	public int getLoadOrder() {
		return loadOrder;
	}
	
	/**
	 * Returns the ID of the wizard which pages the current extension will
	 * contribute to.
	 * 
	 * @return a String representation of the wizard ID.
	 */
	public String getWizardID() {
		return wizardID;
	}
	
	/**
	 * Create additional controls for the specified wizard page.
	 * 
	 * <p>
	 * The current extension contributes additional controls to the specified
	 * parent composite that is part of the wizard page.
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
		if (factoryElement != null)
			factoryElement.createAdditionalControls(parent, model, pageName);
	}
	
	private void readFactory(IConfigurationElement element) {
		IConfigurationElement[] factories = element.getChildren(ELEMENT_FACTORY);
		if (factories != null && factories.length > 0) {
			factoryElement = new DMWizardPageFactoryElement(factories[0]);
		}
	}

}
