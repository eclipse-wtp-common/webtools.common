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

/**
 * 
 * This interface is not intended to be implemented by clients
 *
 */
public interface IFlatFolder extends IFlatResource {
	/**
	 * Returns the members (contents) of this folder.
	 * 
	 * @return an array containing the module resources contained in this folder
	 */
	public IFlatResource[] members();
	/**
	 * Sets the members (contents) of this folder.
	 * 
	 * @param members the members
	 */
	public void setMembers(IFlatResource[] members);
}
