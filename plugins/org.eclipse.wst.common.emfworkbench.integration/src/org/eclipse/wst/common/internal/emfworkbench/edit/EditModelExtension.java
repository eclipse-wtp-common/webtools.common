/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
/*
 * Created on Feb 18, 2004
 * 
 * To change the template for this generated file go to Window&gt;Preferences&gt;Java&gt;Code
 * Generation&gt;Code and Comments
 */
package org.eclipse.wst.common.internal.emfworkbench.edit;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.common.frameworks.internal.ConfigurationElementWrapper;

import org.eclipse.jem.util.logger.proxy.Logger;


public class EditModelExtension extends ConfigurationElementWrapper {
	public static final String ID_ATTR = "id"; //$NON-NLS-1$

	private String editModelID = null;
	private Collection resources = null;
	private String id = null;


	public EditModelExtension(IConfigurationElement element) {
		super(element);
		init();
	}

	private void init() {
		id = element.getAttribute(ID_ATTR);
		if (id == null) {
			Logger.getLogger().logError("Incorrect usage of editModelExtension extension point.  Element must contain id attribute.  Plugin: " + getPluginId()); //$NON-NLS-1$
			return;
		}

		editModelID = element.getAttribute(EditModelExtensionRegistry.EDIT_MODEL_ID_ATTR);
		resources = new ArrayList();
		IConfigurationElement[] editModelResources = element.getChildren(EditModelResource.EDIT_MODEL_RESOURCE_ELEMENT);
		for (int j = 0; j < editModelResources.length; j++)
			resources.add(new EditModelResource(editModelResources[j], id));
	}

	/**
	 * @return
	 */
	public String getEditModelID() {
		return editModelID;
	}


	/**
	 * @return
	 */
	public Collection getResources() {
		return resources;
	}

}