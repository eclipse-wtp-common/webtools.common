/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on May 3, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.eclipse.wst.validation.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.validation.internal.operations.ReferencialFileValidator;
import org.eclipse.wst.validation.plugin.ValidationPlugin;

import org.eclipse.jem.util.RegistryReader;

/**
 * @author vijayb
 * 
 * To change the template for this generated type comment go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */
public class ReferencialFileValidatorRegistryReader extends RegistryReader {
	static ReferencialFileValidatorRegistryReader instance = null;
	protected List referencialFileValidationExtensions;

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public ReferencialFileValidatorRegistryReader() {
		super(ValidationPlugin.PLUGIN_ID, "referencialFileValidator"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.RegistryReader#readElement(org.eclipse.core.runtime.IConfigurationElement)
	 */
	public boolean readElement(IConfigurationElement element) {
		if (ReferencialFileValidatorExtension.REF_FILE_VALIDATOR_EXTENSION.equals(element.getName())) {
			addExtension(element);
			return true;
		}
		return false;
	}

	/**
	 * Sets the extension point.
	 * 
	 * @param extensions
	 *            The extensions to set
	 */
	protected void addExtension(IConfigurationElement newExtension) {
		getReferencialFileValidationExtensions().add(new ReferencialFileValidatorExtension(newExtension));
	}

	/**
	 * Sets the extension point.
	 * 
	 * @param extensions
	 *            The extensions to set
	 */
	protected void addExtensionPoint(ReferencialFileValidatorExtension newExtension) {
		if (referencialFileValidationExtensions == null)
			referencialFileValidationExtensions = new ArrayList();
		referencialFileValidationExtensions.add(newExtension);
	}

	/**
	 * @return the appropriate handler for the project based on priorities of those which are
	 *         available and enabled
	 */
	public ReferencialFileValidator getReferencialFileValidator() {
		ReferencialFileValidatorExtension refFileValExt;
		for (Iterator refFileValItr = getReferencialFileValidationExtensions().iterator(); refFileValItr.hasNext();) {
			refFileValExt = (ReferencialFileValidatorExtension) refFileValItr.next();
			return refFileValExt.getInstance();
		}
		return null;
	}

	/**
	 * Gets the instance.
	 * 
	 * @return Returns a EJBCodegenHandlerExtensionReader
	 */
	public static ReferencialFileValidatorRegistryReader getInstance() {
		if (instance == null) {
			instance = new ReferencialFileValidatorRegistryReader();
			instance.readRegistry();
		}
		return instance;
	}

	/**
	 * @return Returns the handlerExtensions.
	 */
	protected List getReferencialFileValidationExtensions() {
		if (referencialFileValidationExtensions == null)
			referencialFileValidationExtensions = new ArrayList();
		return referencialFileValidationExtensions;
	}

}