/*******************************************************************************
 * Copyright (c) 2003, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emfworkbench.integration;



/**
 * @author Administrator
 * 
 *  
 */
public class ComposedAccessorKey {

	private Object accessorKey = null;
	private Object addonKey = null;


	public static ComposedAccessorKey getComposedAccessorKey(Object accessorKey, ComposedEditModel editModel) {
		ComposedAccessorKey newKey = new ComposedAccessorKey(accessorKey, editModel);
		editModel.cacheAccessorKey(newKey);
		return newKey;
	}


	private ComposedAccessorKey(Object accessorKey, Object addonKey) {
		this.accessorKey = accessorKey;
		this.addonKey = addonKey;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof ComposedAccessorKey))
			return false;
		ComposedAccessorKey otherKey = (ComposedAccessorKey) other;

		return accessorKey.equals(otherKey.accessorKey) && addonKey.equals(otherKey.addonKey);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return accessorKey.hashCode() ^ addonKey.hashCode();
	}


}