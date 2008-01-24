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
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.validation.internal.operations.ReferencialFileValidator;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

/**
 * @author vijayb
 */
public class ReferencialFileValidatorRegistryReader {
	static ReferencialFileValidatorRegistryReader instance;
	
	private static final String Id = "id"; //$NON-NLS-1$
	private static final String ExtensionPoint = "referencialFileValidator"; //$NON-NLS-1$
	
	protected List<ReferencialFileValidatorExtension> referencialFileValidationExtensions;

	public ReferencialFileValidatorRegistryReader() {
	}
	
	/**
	 * Read the extension point and parse it.
	 */
	public void readRegistry() {
		IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(Id, ExtensionPoint);
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
	 * @param newExtension
	 *            The extensions to set
	 */
	protected void addExtension(IConfigurationElement newExtension) {

    	//add to the list of post validator extensions only if the extension is not added yet
    	boolean containsExt = true;
    	List extensions = getReferencialFileValidationExtensions();
    	Iterator it = extensions.iterator();
    	while(it.hasNext()) {
    		ReferencialFileValidatorExtension ext = (ReferencialFileValidatorExtension)it.next();
    		if(!ext.getElement().getAttribute(Id).equals(newExtension.getAttribute(Id)))
    			containsExt = false;
    	}
    	if(!containsExt ||getReferencialFileValidationExtensions().isEmpty())
    		getReferencialFileValidationExtensions().add(new ReferencialFileValidatorExtension(newExtension));
    
		getReferencialFileValidationExtensions().add(new ReferencialFileValidatorExtension(newExtension));
	}

	/**
	 * Sets the extension point.
	 * 
	 * @param newExtension
	 *            The extensions to set
	 */
	protected void addExtensionPoint(ReferencialFileValidatorExtension newExtension) {
		if (referencialFileValidationExtensions == null)
			referencialFileValidationExtensions = new ArrayList<ReferencialFileValidatorExtension>();
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
	 * Gets the singleton instance.
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
	protected List<ReferencialFileValidatorExtension> getReferencialFileValidationExtensions() {
		if (referencialFileValidationExtensions == null)
			referencialFileValidationExtensions = new ArrayList<ReferencialFileValidatorExtension>();
		return referencialFileValidationExtensions;
	}

}
