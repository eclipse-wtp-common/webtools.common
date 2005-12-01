/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
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
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.ConfigurationElementWrapper;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizardPage;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.IDMPageGroupHandler;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.IDMPageHandler;

/**
 * @author schacher
 * 
 * To change the template for this generated type comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public class DMWizardPageFactoryElement extends ConfigurationElementWrapper {

	static final String ATT_CLASS_NAME = "className"; //$NON-NLS-1$

	protected String className;
	protected DMWizardExtensionFactory wizardPageFactory;
	protected boolean isPageFactoryInitialized;
	protected String pageGroupID;

	/**
	 * @param element
	 */
	public DMWizardPageFactoryElement(IConfigurationElement element, String pageGroupID) {
		super(element);
		className = element.getAttribute(ATT_CLASS_NAME);
		this.pageGroupID = pageGroupID;
	}

	public IDMPageHandler createPageHandler(IDataModel dataModel) {
		if (!isPageFactoryInitialized)
			initPageFactory();
		if (wizardPageFactory == null)
			return null;

		IDMPageHandler handler = wizardPageFactory.createPageHandler(dataModel, pageGroupID);
		return handler;
	}

	public DataModelWizardPage[] createPageGroup(IDataModel dataModel) {
		if (!isPageFactoryInitialized)
			initPageFactory();

		if (wizardPageFactory == null)
			return null;

		DataModelWizardPage[] pages = wizardPageFactory.createPageGroup(dataModel, pageGroupID);
		
		return pages;
	}

	public IDMPageGroupHandler createPageGroupHandler( IDataModel dataModel )
	{
	  if (!isPageFactoryInitialized) initPageFactory();
		
	  if( wizardPageFactory == null ) return null;
	  
	  return wizardPageFactory.createPageGroupHandler( dataModel, pageGroupID );
	}
		
	private void initPageFactory() {
		try {
			wizardPageFactory = (DMWizardExtensionFactory) element.createExecutableExtension(ATT_CLASS_NAME);
		} catch (CoreException e) {
			Logger.getLogger().logError("Error getting page factory: " + className); //$NON-NLS-1$ 
			Logger.getLogger().logError(e);
		} finally {
			isPageFactoryInitialized = true;
		}
	}


}
