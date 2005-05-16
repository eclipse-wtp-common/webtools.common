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
package org.eclipse.wst.common.componentcore.resources;

import org.eclipse.core.resources.IFolder;
/**
 * Represents a folder that can be navigated through 
 * an abstract ("virtual") path.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * @since 1.0
 */
public interface IVirtualFolder extends IVirtualContainer { 
	/**
	 * returns the underlying IFolder mapped to the runtime path, 
	 * returns first IFolder if multiple exist.
	 * 
	 * @return the underlying IFolder
	 */
	public IFolder getUnderlyingFolder();
	/**
	 * returns the underlying IFolders mapped to the runtime path. 
	 * Multiple IFolders can be mapped to the same runtime path.
	 * 
	 * @return the array of underlying IFolders
	 */
	public IFolder[] getUnderlyingFolders();
}
