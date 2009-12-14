/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.export;

/**
 * 
 * This interface is not intended to be implemented by clients
 *
 */
public interface IExportableFolder extends IExportableResource {
	/**
	 * Returns the members (contents) of this folder.
	 * 
	 * @return an array containing the module resources contained in this folder
	 */
	public IExportableResource[] members();
	/**
	 * Sets the members (contents) of this folder.
	 * 
	 * @param members the members
	 */
	public void setMembers(IExportableResource[] members);
}
