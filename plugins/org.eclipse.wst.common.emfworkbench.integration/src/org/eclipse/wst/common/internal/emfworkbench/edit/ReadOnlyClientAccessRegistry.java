/*******************************************************************************
 * Copyright (c) 2005, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emfworkbench.edit;

import org.eclipse.jem.internal.util.emf.workbench.nls.EMFWorkbenchResourceHandler;
import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchEditResourceHandler;

/**
 * This is a readonly version of the editmodel access registry, only uses a weak hash dictionary,
 * and because the access is read only, is less concerned with timing of access/release, and will be
 * cleaned up on garbage collection if needed.
 * 
 */
public class ReadOnlyClientAccessRegistry extends ClientAccessRegistry {

	public ReadOnlyClientAccessRegistry() {
		super();
	}

	@Override
	public synchronized void access(Object accessorKey) {
		if (!registry.containsKey(accessorKey)) {
			this.registry.put(accessorKey, null);
		} else
			throw new ClientAccessRegistryException(EMFWorkbenchEditResourceHandler.ClientAccessRegistry_ERROR_0, accessorKey);
	}

	@Override
	public synchronized void release(Object accessorKey) {

		/*
		 * Error condition: Some one has been naughty and not released the resource
		 */
		if (this.registry.containsKey(accessorKey)) {
			this.registry.remove(accessorKey);
		} else
			complain(accessorKey);
	}

	@Override
	public void complain(Object accessorKey) {

		throw new ClientAccessRegistryException(EMFWorkbenchResourceHandler.getString("ClientAccessRegistry_ERROR_1"), accessorKey); //$NON-NLS-1$
	}

}
