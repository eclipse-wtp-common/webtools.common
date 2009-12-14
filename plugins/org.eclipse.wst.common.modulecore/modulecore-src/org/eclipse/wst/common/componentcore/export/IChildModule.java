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
package org.eclipse.wst.common.componentcore.export;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;

/**
 * A child module may be based on a file (if binary), 
 * or on a reference / component if not. 
 * 
 * This interface is not intended to be implemented by clients
 */
public interface IChildModule {
	/**
	 * Will return a file if this can be tracked to one
	 * @return
	 */
	public File getFile();
	
	
	/**
	 * Return the reference if it exists
	 * @return
	 */
	public IVirtualReference getReference();
	
	/**
	 * Return the component if it exists
	 * @return
	 */
	public IVirtualComponent getComponent();
	
	/**
	 * If this is a single file, return true.
	 * If a more complex component, return false
	 * @return
	 */
	public boolean isBinary();
	
	/**
	 * Get the relative URI to it's direct parent, 
	 * including path + filename + extension
	 * @return
	 */
	public IPath getRelativeURI();
}
