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
package org.eclipse.wst.common.internal.emfworkbench.edit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jem.util.RegistryReader;
import org.eclipse.wst.common.internal.emfworkbench.integration.EMFWorkbenchEditPlugin;


/**
 * @author mdelder
 */
class EditModelExtensionRegistry extends RegistryReader {

	private static EditModelExtensionRegistry INSTANCE = null;

	public static final String EDIT_MODEL_EXT_ELEMENT = "editModelExtension"; //$NON-NLS-1$
	public static final String EDIT_MODEL_ID_ATTR = "editModelID"; //$NON-NLS-1$
	public static final String GROUP_ID_ATTR = "functionGroupID"; //$NON-NLS-1$


	private Map extensions = null;

	protected EditModelExtensionRegistry() {
		super(EMFWorkbenchEditPlugin.ID, EMFWorkbenchEditPlugin.EDIT_MODEL_EXTENSION_REGISTRY_EXTENSION_POINT);
	}

	public static EditModelExtensionRegistry getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new EditModelExtensionRegistry();
			INSTANCE.readRegistry();
		}
		return INSTANCE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.RegistryReader#readElement(org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	public boolean readElement(IConfigurationElement element) {

		boolean result = false;
		Collection extensionsByID = null;
		EditModelExtension editModelExtension = null;
		if (element.getName().equals(EDIT_MODEL_EXT_ELEMENT)) {
			editModelExtension = new EditModelExtension(element);
			extensionsByID = (Collection) getExtensions().get(editModelExtension.getEditModelID());
			if (extensionsByID == null) {
				extensionsByID = new ArrayList();
				getExtensions().put(editModelExtension.getEditModelID(), extensionsByID);
			}
			extensionsByID.add(editModelExtension);
			result = true;
		}
		return result;
	}

	protected Map getExtensions() {
		if (extensions == null)
			extensions = new HashMap();
		return extensions;
	}

	/**
	 * @return
	 */
	public Collection getEditModelResources(Object editModelID) {
		//TODO - Cache the resources
		Collection editModelResources = new ArrayList();
		Collection editModelExtensions = (Collection) getExtensions().get(editModelID);

		if (editModelExtensions == null || editModelExtensions.size() == 0)
			return Collections.EMPTY_LIST;
		Iterator itr = editModelExtensions.iterator();
		while (itr.hasNext()) {
			EditModelExtension ext = (EditModelExtension) itr.next();
			editModelResources.addAll(ext.getResources());
		}
		return (!editModelExtensions.isEmpty()) ? editModelResources : Collections.EMPTY_LIST;
	}

}
