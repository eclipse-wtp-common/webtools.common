/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.resources;

import org.eclipse.core.resources.IFile;
/**
 * Represents a file that can be navigated through 
 * an abstract ("virtual") path. 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * @plannedfor 1.0
 */
public interface IVirtualFile extends IVirtualResource {  
	/**
	 * returns the underlying IFile, returns first IFile if multiple exist.
	 * 
	 * @return the underlying IFile
	 */
	public IFile getUnderlyingFile();
	/**
	 * returns the array of underlying IFiles that are mapped to the same runtime path.
	 * 
	 * @return the array of underlying IFiles mapped to the runtime path
	 */
	public IFile[] getUnderlyingFiles();
	
}
