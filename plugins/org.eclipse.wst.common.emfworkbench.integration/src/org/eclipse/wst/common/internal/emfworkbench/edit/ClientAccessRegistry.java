/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Oct 6, 2003
 */
package org.eclipse.wst.common.internal.emfworkbench.edit;

import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

import org.eclipse.jem.internal.util.emf.workbench.nls.EMFWorkbenchResourceHandler;
import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchEditResourceHandler;

/**
 * @author mdelder
 */
public class ClientAccessRegistry {

	private final WeakHashMap registry = new WeakHashMap();
	private final Set baseSet = new HashSet();

	public void access(Object accessorKey) {
		if (isStable()) {
			if (!registry.containsKey(accessorKey)) {
				Snapshot snapshot = new Snapshot();
				this.registry.put(accessorKey, snapshot);
				this.baseSet.add(snapshot);

			} else
				throw new ClientAccessRegistryException(EMFWorkbenchEditResourceHandler.getString("ClientAccessRegistry_ERROR_0"), accessorKey); //$NON-NLS-1$

		} else
			complain();
	}

	public void release(Object accessorKey) {

		/*
		 * Error condition: Some one has been naughty and not released the resource
		 */
		if (this.registry.containsKey(accessorKey) && isStable()) {
			Snapshot snapshot = (Snapshot) this.registry.remove(accessorKey);
			this.baseSet.remove(snapshot);
		} else
			complain(accessorKey);
	}

	public void assertAccess(Object accessorKey) {
		if (!isClientAccessing(accessorKey))
			throw new ClientAccessRegistryException(EMFWorkbenchResourceHandler.getString("ClientAccessRegistry_ERROR_1"), accessorKey); //$NON-NLS-1$
	}

	public boolean isClientAccessing(Object client) {
		boolean result = this.registry.containsKey(client);
		if (!isStable())
			complain();
		return result;
	}

	public boolean isAnyClientAccessing() {
		boolean result = this.registry.size() > 0;
		if (!isStable())
			complain();
		return result;
	}

	public boolean isStable() {
		return this.baseSet.size() == this.registry.size();
	}

	public void complain() {
		complain(null);
	}

	public void complain(Object accessorKey) {
		if (!isStable())
			throw new ClientAccessRegistryException(this.registry, this.baseSet);
		throw new ClientAccessRegistryException(EMFWorkbenchResourceHandler.getString("ClientAccessRegistry_ERROR_1"), accessorKey); //$NON-NLS-1$
	}

	public String toString() {
		StringBuffer result = new StringBuffer("ClientAccessRegistry: ["); //$NON-NLS-1$
		result.append((isStable()) ? "STABLE" : "OUT OF SYNC"); //$NON-NLS-1$ //$NON-NLS-2$
		result.append("]: Reference Count = "); //$NON-NLS-1$
		result.append(this.size());
		return result.toString();
	}

	public int size() {
		return this.registry.size();
	}

}