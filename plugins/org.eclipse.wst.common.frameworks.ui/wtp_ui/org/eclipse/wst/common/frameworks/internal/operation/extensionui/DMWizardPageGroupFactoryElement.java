/*******************************************************************************
 * Copyright (c) 2003, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Kaloyan Raev, kaloyan.raev@sap.com - bug 213927
 *******************************************************************************/
/*
 * Created on Oct 20, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.eclipse.wst.common.frameworks.internal.operation.extensionui;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.ConfigurationElementWrapper;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizardPage;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.IDMPageGroupHandler;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.IDMPageHandler;
import org.eclipse.wst.common.frameworks.internal.ui.WTPUIPlugin;

/**
 * @author schacher
 * 
 * To change the template for this generated type comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public class DMWizardPageGroupFactoryElement extends ConfigurationElementWrapper {

	static final String ATT_CLASS_NAME = "className"; //$NON-NLS-1$

	protected String className;
	protected DMWizardExtensionFactory wizardPageGroupFactory;
	protected boolean isPageGroupFactoryInitialized;
	protected String pageGroupID;

	/**
	 * @param element
	 */
	public DMWizardPageGroupFactoryElement(IConfigurationElement element, String pageGroupID) {
		super(element);
		className = element.getAttribute(ATT_CLASS_NAME);
		this.pageGroupID = pageGroupID;
	}

	public IDMPageHandler createPageHandler(IDataModel dataModel) {
		if (!isPageGroupFactoryInitialized)
			initPageFactory();
		if (wizardPageGroupFactory == null)
			return null;

		IDMPageHandler handler = wizardPageGroupFactory.createPageHandler(dataModel, pageGroupID);
		return handler;
	}

	public DataModelWizardPage[] createPageGroup(IDataModel dataModel) {
		if (!isPageGroupFactoryInitialized)
			initPageFactory();

		if (wizardPageGroupFactory == null)
			return null;

		DataModelWizardPage[] pages = wizardPageGroupFactory.createPageGroup(dataModel, pageGroupID);
		
		return pages;
	}

	public IDMPageGroupHandler createPageGroupHandler( IDataModel dataModel )
	{
	  if (!isPageGroupFactoryInitialized) initPageFactory();
		
	  if( wizardPageGroupFactory == null ) return null;
	  
	  return wizardPageGroupFactory.createPageGroupHandler( dataModel, pageGroupID );
	}
		
	private void initPageFactory() {
		try {
			wizardPageGroupFactory = (DMWizardExtensionFactory) element.createExecutableExtension(ATT_CLASS_NAME);
		} catch (CoreException e) {
			WTPUIPlugin.logError("Error getting page factory: " + className); //$NON-NLS-1$ 
			WTPUIPlugin.logError(e);
		} finally {
			isPageGroupFactoryInitialized = true;
		}
	}


}
