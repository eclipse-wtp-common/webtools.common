/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 *  $$RCSfile: EMFNatureRegistry.java,v $$
 *  $$Revision: 1.1 $$  $$Date: 2005/01/07 20:19:23 $$ 
 */
package org.eclipse.jem.internal.util.emf.workbench.nature;

import java.util.HashSet;
import java.util.Set;


import org.eclipse.core.runtime.*;

import org.eclipse.jem.internal.util.emf.workbench.nls.EMFWorkbenchResourceHandler;
import org.eclipse.jem.util.logger.proxy.Logger;

public class EMFNatureRegistry {

	private static final String NATURE_REGISTRATION_POINT = "org.eclipse.jem.util.nature_registration"; //$NON-NLS-1$
	private static final String NATURE  = "nature"; //$NON-NLS-1$
	private static final String STATIC_ID = "id"; //$NON-NLS-1$

	/**
	 * Constructor
	 */
	private EMFNatureRegistry() {
		super();
		readRegistry();
	}
	
	private static EMFNatureRegistry singleton;
	
	public final Set REGISTERED_NATURE_IDS = new HashSet();
	
	public static EMFNatureRegistry singleton() {
		if (singleton == null)
			singleton = new EMFNatureRegistry();
		return singleton;
	}
	
	protected void readRegistry() {
	// register Nature IDs for the J2EENatures
		IExtensionRegistry r = Platform.getExtensionRegistry();
		IConfigurationElement[] ce = r.getConfigurationElementsFor(NATURE_REGISTRATION_POINT);
		String natureId;
		for (int i=0; i<ce.length; i++) {
			if (ce[i].getName().equals(NATURE)) {
				natureId = ce[i].getAttribute(STATIC_ID);
				if (natureId != null)
					registerNatureID(natureId);
			}
		}
	}

	/**
	 * @param natureId
	 */
	private void registerNatureID(String natureId) {
		if (!REGISTERED_NATURE_IDS.contains(natureId))
			REGISTERED_NATURE_IDS.add(natureId);
		else
			Logger.getLogger().logError(EMFWorkbenchResourceHandler.getString("EMFNatureRegistry_ERROR_0", new Object[] {natureId})); //$NON-NLS-1$
	}

}
