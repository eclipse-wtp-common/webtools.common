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
package org.eclipse.wst.common.frameworks.internal.operation.extensionui;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jem.util.RegistryReader;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.ui.DataModelWizard;
import org.eclipse.wst.common.frameworks.internal.ui.WTPUIPlugin;

public class DataModelWizardExtensionReader extends RegistryReader {

	private static final String EXTENSION = "DataModelWizardExtension";
	private static final String ELEMENT = "DataModelWizard";
	private static final String ATTRIBUTE_ID = "id";
	private static final String ATTRIBUTE_CLASS = "class";

	private HashMap extensions;

	public DataModelWizardExtensionReader() {
		super(WTPUIPlugin.PLUGIN_ID, EXTENSION);
	}

	public boolean readElement(IConfigurationElement element) {
		if (!element.getName().equals(ELEMENT))
			return false;
		String id = element.getAttribute(ATTRIBUTE_ID);
		if (null == id || id.trim().length() == 0) {
			Logger.getLogger().logError(new RuntimeException("Extension:" + EXTENSION + " Element:" + ELEMENT + " is missing " + ATTRIBUTE_ID));
		}
		String className = element.getAttribute(ATTRIBUTE_CLASS);
		if (null == className || className.trim().length() == 0) {
			Logger.getLogger().logError(new RuntimeException("Extension:" + EXTENSION + " Element:" + ELEMENT + " is missing " + ATTRIBUTE_CLASS));
		}
		addExtension(id, element);
		return true;
	}

	private void addExtension(String id, IConfigurationElement element) {
		if (extensions.containsKey(id)) {
			Logger.getLogger().logError(new RuntimeException("Duplicate " + ELEMENT + " " + ATTRIBUTE_ID + " " + id));
		}
		extensions.put(id, element);
	}

	protected IConfigurationElement getExtension(String id) {
		if (extensions == null) {
			extensions = new HashMap();
			readRegistry();
		}
		IConfigurationElement element = (IConfigurationElement) extensions.get(id);
		if (null == element) {
			throw new RuntimeException("Extension:" + EXTENSION + " Element:" + ELEMENT + " not found for " + ATTRIBUTE_ID + ": " + id);
		}
		return element;
	}

	public DataModelWizard getWizard(IDataModel dataModel) {
		DataModelWizard wizard = null;
		IConfigurationElement element = getExtension(dataModel.getID());

		try {
			wizard = (DataModelWizard) element.createExecutableExtension(ATTRIBUTE_CLASS);
			wizard.setDataModel(dataModel);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return wizard;
	}
}
