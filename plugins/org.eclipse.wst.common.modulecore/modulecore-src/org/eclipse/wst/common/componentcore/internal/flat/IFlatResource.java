/*******************************************************************************
 * Copyright (c) 2009 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.flat;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;

/**
 * 
 * This interface is not intended to be implemented by clients
 *
 */
public interface IFlatResource extends IAdaptable {
	/**
	 * Returns the module relative path to this resource.
	 * 
	 * @return the module relative path to this resource
	 */
	public IPath getModuleRelativePath();

	/**
	 * Returns the name of this resource.
	 * 
	 * @return the name of this resource
	 */
	public String getName();
}