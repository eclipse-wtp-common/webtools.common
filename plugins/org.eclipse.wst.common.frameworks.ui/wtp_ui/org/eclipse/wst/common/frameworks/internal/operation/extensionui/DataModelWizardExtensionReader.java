/*******************************************************************************
 * Copyright (c) 2003, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.operation.extensionui;

import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.common.core.util.RegistryReader;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizard;
import org.eclipse.wst.common.frameworks.internal.ui.WTPUIPlugin;

public class DataModelWizardExtensionReader extends RegistryReader {

	private static final String EXTENSION = "DataModelWizardExtension"; //$NON-NLS-1$
	private static final String ELEMENT = "DataModelWizard"; //$NON-NLS-1$
	private static final String ATTRIBUTE_ID = "id"; //$NON-NLS-1$
	private static final String ATTRIBUTE_CLASS = "class"; //$NON-NLS-1$

	private HashMap extensions;

	public DataModelWizardExtensionReader() {
		super(WTPUIPlugin.PLUGIN_ID, EXTENSION);
	}

	@Override
	public boolean readElement(IConfigurationElement element) {
		if (!element.getName().equals(ELEMENT))
			return false;
		String id = element.getAttribute(ATTRIBUTE_ID);
		if (null == id || id.trim().length() == 0) {
			WTPUIPlugin.logError(new RuntimeException("Extension:" + EXTENSION + " Element:" + ELEMENT + " is missing " + ATTRIBUTE_ID)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		String className = element.getAttribute(ATTRIBUTE_CLASS);
		if (null == className || className.trim().length() == 0) {
			WTPUIPlugin.logError(new RuntimeException("Extension:" + EXTENSION + " Element:" + ELEMENT + " is missing " + ATTRIBUTE_CLASS)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		addExtension(id, element);
		return true;
	}

	private void addExtension(String id, IConfigurationElement element) {
		if (extensions.containsKey(id)) {
			WTPUIPlugin.logError(new RuntimeException("Duplicate " + ELEMENT + " " + ATTRIBUTE_ID + " " + id)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
			throw new RuntimeException("Extension:" + EXTENSION + " Element:" + ELEMENT + " not found for " + ATTRIBUTE_ID + ": " + id); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
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
			WTPUIPlugin.logError(e);
		}

		return wizard;
	}
}
