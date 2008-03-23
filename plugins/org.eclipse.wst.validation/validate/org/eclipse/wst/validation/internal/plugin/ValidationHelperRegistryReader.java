/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.plugin;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.validation.internal.IProjectValidationHelper;

public class ValidationHelperRegistryReader {

	/** validationHelper - extension point name. */
	public static final String VALIDATION_HELPER = "validationHelper"; //$NON-NLS-1$
	
	static final String ATT_ID = "id"; //$NON-NLS-1$ 
	
	/** helperClass - class that implements the extension. */
	static final String ATT_HELPER_CLASS = "helperClass"; //$NON-NLS-1$
	
	private static ValidationHelperRegistryReader INSTANCE = null;
	private List<IProjectValidationHelper> _validationHelpers;
	
	public ValidationHelperRegistryReader() {
	}
	
	public static ValidationHelperRegistryReader getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ValidationHelperRegistryReader();
			INSTANCE.readRegistry();
		}
		return INSTANCE;
	}

	/**
	 * Read the extension point and parse it.
	 */
	public void readRegistry() {
		IExtensionPoint point = Platform.getExtensionRegistry()
			.getExtensionPoint(ValidationPlugin.PLUGIN_ID, VALIDATION_HELPER);
		if (point == null)return;
		IConfigurationElement[] elements = point.getConfigurationElements();
		for (int i = 0; i < elements.length; i++) {
			internalReadElement(elements[i]);
		}
	}
	
	private void internalReadElement(IConfigurationElement element) {
		boolean recognized = this.readElement(element);
		if (!recognized) {
			logError(element, "Error processing extension: " + element); //$NON-NLS-1$
		}
	}

	/*
	 * Logs the error in the desktop log using the provided text and the information in the configuration element.
	 */
	protected void logError(IConfigurationElement element, String text) {
		IExtension extension = element.getDeclaringExtension();
		StringBuffer buf = new StringBuffer();
		buf.append("Plugin " + extension.getContributor().getName() + ", extension " + extension.getExtensionPointUniqueIdentifier()); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("\n" + text); //$NON-NLS-1$
		ValidationPlugin.getPlugin().logMessage(IStatus.ERROR, buf.toString());
	}

	private List<IProjectValidationHelper> getValidationHelpers() {
		if (_validationHelpers == null)
			_validationHelpers = new ArrayList<IProjectValidationHelper>();
		return _validationHelpers;
	}

	public boolean readElement(IConfigurationElement element) {
		if (!element.getName().equals(VALIDATION_HELPER))
			return false;
		IProjectValidationHelper helper = null;
		try {
			helper = (IProjectValidationHelper) element.createExecutableExtension(ATT_HELPER_CLASS);
			getValidationHelpers().add(helper);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public IProjectValidationHelper getValidationHelper() {
		if (getValidationHelpers().isEmpty())return null;
		return getValidationHelpers().get(0);
	}

}
