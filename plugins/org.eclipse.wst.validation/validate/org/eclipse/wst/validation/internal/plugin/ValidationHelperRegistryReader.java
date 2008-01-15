/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
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
import org.eclipse.jem.util.RegistryReader;
import org.eclipse.wst.validation.internal.IProjectValidationHelper;

public class ValidationHelperRegistryReader extends RegistryReader {

	/** validationHelper - extension point name. */
	public static final String VALIDATION_HELPER = "validationHelper"; //$NON-NLS-1$
	
	static final String ATT_ID = "id"; //$NON-NLS-1$ 
	
	/** helperClass - class that implements the extension. */
	static final String ATT_HELPER_CLASS = "helperClass"; //$NON-NLS-1$
	
	private static ValidationHelperRegistryReader INSTANCE = null;
	private List<IProjectValidationHelper> _validationHelpers;
	
	public ValidationHelperRegistryReader() {
		super(ValidationPlugin.PLUGIN_ID, VALIDATION_HELPER);
	}
	
	public static ValidationHelperRegistryReader getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ValidationHelperRegistryReader();
			INSTANCE.readRegistry();
		}
		return INSTANCE;
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
