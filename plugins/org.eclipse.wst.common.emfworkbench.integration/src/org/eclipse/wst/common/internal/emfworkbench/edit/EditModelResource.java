/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
/*
 * Created on Feb 5, 2004
 * 
 * To change the template for this generated file go to Window&gt;Preferences&gt;Java&gt;Code
 * Generation&gt;Code and Comments
 */
package org.eclipse.wst.common.internal.emfworkbench.edit;


import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.frameworks.internal.AbstractRegistryDescriptor;



public class EditModelResource extends AbstractRegistryDescriptor implements Comparable {
	public static final String EDIT_MODEL_URI_ATTR = "URI"; //$NON-NLS-1$
	public static final String AUTO_LOAD_ATTR = "autoload"; //$NON-NLS-1$
	public static final String EDIT_MODEL_RESOURCE_ELEMENT = "editModelResource"; //$NON-NLS-1$

	private static int loadOrderCounter = 1;
	private URI uri;
	private boolean autoload = false;
	//Indicates if this was defined as part of the edit model,
	//as opposed to an extension
	private boolean isCore = true;

	private String extensionID;

	private int loadOrder;

	public EditModelResource(IConfigurationElement element) {
		super(element);
		String strUri = element.getAttribute(EDIT_MODEL_URI_ATTR);
		if (strUri != null)
			EditModelResource.this.uri = URI.createURI(strUri);

		String strLoad = element.getAttribute(AUTO_LOAD_ATTR);
		if (strLoad != null)
			autoload = Boolean.valueOf(strLoad).booleanValue();
		loadOrder = loadOrderCounter++;
	}

	public EditModelResource(IConfigurationElement element, String extensionID) {
		this(element);
		this.extensionID = extensionID;
		isCore = false;
	}

	public URI getURI() {
		return uri;
	}

	public boolean isAutoLoad() {
		return autoload;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.AbstractRegistryDescriptor#getID()
	 */
	@Override
	public String getID() {
		return extensionID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.AbstractRegistryDescriptor#getPriority()
	 */
	@Override
	public int getPriority() {
		if (isCore)
			return 0;
		return super.getPriority();
	}

	/**
	 * return whether this resource is defined as part of the edit model definition as opposed to an
	 * extension
	 */
	public boolean isCore() {
		return isCore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		if (!(o instanceof EditModelResource))
			return 1;
		EditModelResource res = (EditModelResource) o;
		int value = getPriority() - res.getPriority();
		if (value == 0)
			return loadOrder - res.loadOrder;
		return value;
	}

	/**
	 * @return Returns the loadOrder.
	 */
	public int getLoadOrder() {
		return loadOrder;
	}
}