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
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.validation.internal.operations.PostValidator;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

import com.ibm.wtp.common.RegistryReader;

/**
 * @author vijayb
 * 
 * To change the template for this generated type comment go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */
public class PostValidatorRegistryReader extends RegistryReader {
	static PostValidatorRegistryReader instance = null;
	protected List postValidationExtensions;

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public PostValidatorRegistryReader() {
		super(Platform.getPluginRegistry(), ValidationPlugin.PLUGIN_ID, "postValidator"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.framework.RegistryReader#readElement(org.eclipse.core.runtime.IConfigurationElement)
	 */
	public boolean readElement(IConfigurationElement element) {
		if (PostValidatorExtension.POST_VALIDATOR_EXTENSION.equals(element.getName())) {
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
		getPostValidatorExtensions().add(new PostValidatorExtension(newExtension));
	}

	/**
	 * Sets the extension point.
	 * 
	 * @param extensions
	 *            The extensions to set
	 */
	protected void addExtensionPoint(PostValidatorExtension newExtension) {
		if (postValidationExtensions == null)
			postValidationExtensions = new ArrayList();
		postValidationExtensions.add(newExtension);
	}

	/**
	 * @return the appropriate handler for the project based on priorities of those which are
	 *         available and enabled
	 */
	public PostValidator getPostValidator() {
		PostValidatorExtension postValExt;
		for (Iterator postValItr = getPostValidatorExtensions().iterator(); postValItr.hasNext();) {
			postValExt = (PostValidatorExtension) postValItr.next();
			return postValExt.getInstance();
		}
		return null;
	}

	/**
	 * Gets the instance.
	 * 
	 * @return Returns a EJBCodegenHandlerExtensionReader
	 */
	public static PostValidatorRegistryReader getInstance() {
		if (instance == null) {
			instance = new PostValidatorRegistryReader();
			instance.readRegistry();
		}
		return instance;
	}

	/**
	 * @return Returns the handlerExtensions.
	 */
	protected List getPostValidatorExtensions() {
		if (postValidationExtensions == null)
			postValidationExtensions = new ArrayList();
		return postValidationExtensions;
	}

}