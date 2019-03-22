/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.flat;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;

public abstract class FlatResource implements IFlatResource, IAdaptable {
	/**
	 * Returns the module relative path to this resource.
	 * 
	 * @return the module relative path to this resource
	 */
	public abstract IPath getModuleRelativePath();

	/**
	 * Returns the name of this resource.
	 * 
	 * @return the name of this resource
	 */
	public abstract String getName();
}
