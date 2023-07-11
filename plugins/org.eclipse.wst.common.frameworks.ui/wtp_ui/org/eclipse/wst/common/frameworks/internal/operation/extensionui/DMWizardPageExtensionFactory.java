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
package org.eclipse.wst.common.frameworks.internal.operation.extensionui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * An abstract class where the actual extending of the wizard pages happen.
 * 
 * <p>
 * Implementation of this abstract class should be registered in the
 * <cite>org.eclipse.wst.common.frameworks.ui.wizardPageGruop</cite> extension
 * point > <cite>wizardPage</cite> > <cite>factory</cite> > <cite>className</cite>.
 * </p>
 * 
 * @author kraev
 */
public abstract class DMWizardPageExtensionFactory {
	
	/**
	 * Create additional controls for the specified wizard page.
	 * 
	 * @param parent -
	 *            the parent composite where the additional controls will be
	 *            added to.
	 * @param model -
	 *            the data model of the wizard.
	 * @param pageName -
	 *            the name of the extended wizard page.
	 */
	public abstract void createAdditionalControls(Composite parent, IDataModel model, String pageName);

}
