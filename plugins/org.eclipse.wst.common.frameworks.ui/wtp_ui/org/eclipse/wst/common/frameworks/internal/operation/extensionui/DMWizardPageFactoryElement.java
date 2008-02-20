/*******************************************************************************
 * Copyright (c) 2008 SAP AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Kaloyan Raev, kaloyan.raev@sap.com - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.operation.extensionui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.ConfigurationElementWrapper;

/**
 * This class provides convenient methods for accessing the semantics of the
 * given <code>IConfigurationElement</code> in the context of the
 * <cite>factory</cite> element as a child element of the <cite>wizardPage</cite>
 * element of the <cite>wizardPageGroup</cite> extension point.
 * 
 * @author kraev
 */
public class DMWizardPageFactoryElement extends ConfigurationElementWrapper {
	
	/**
	 * The name of the attribute that points to fully qualified name of the
	 * class that extends the <code>DMWizardPageExtensionFactory</code>
	 * abstract class. This class does the actual job on extending a wizard
	 * page.
	 */
	public static final String ATT_CLASS_NAME = "className"; //$NON-NLS-1$
	
	protected String className;
	protected DMWizardPageExtensionFactory wizardPageFactory;

	private boolean isPageFactoryInitialized;

	/**
	 * Constructs a new <code>DMWizardPageFactoryElement</code> from the given
	 * <code>IConfigurationElement</code>.
	 * 
	 * @param element -
	 *            the <code>IConfigurationElement</code> to wrap.
	 */
	public DMWizardPageFactoryElement(IConfigurationElement element) {
		super(element);
		className = element.getAttribute(ATT_CLASS_NAME);
		
		isPageFactoryInitialized = false;
	}
	
	/**
	 * Create additional controls for the specified wizard page.
	 * 
	 * <p>
	 * This method ensures that the implementation of the
	 * <code>DMWizardPageExtensionFactory</code> abstract class, that is
	 * defined in the <cite>className</cite> attribute, is initialized. Then
	 * the method forwards the call to the factory class.
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
		if (!isPageFactoryInitialized)
			initPageFactory();

		if (wizardPageFactory == null)
			return;
		
		wizardPageFactory.createAdditionalControls(parent, model, pageName);
	}
	
	private void initPageFactory() {
		try {
			wizardPageFactory = (DMWizardPageExtensionFactory) element.createExecutableExtension(ATT_CLASS_NAME);
		} catch (CoreException e) {
			Logger.getLogger().logError("Error getting page factory: " + className); //$NON-NLS-1$ 
			Logger.getLogger().logError(e);
		} finally {
			isPageFactoryInitialized = true;
		}
	}

}
