/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.resources;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

public interface ITaggedVirtualResource {
	
	/**
	 * Sets the given tag to the path linked to this resource 
	 * @param aProjectRelativeLocation - path linked to the resource
	 * @param tag - The tag to be set
	 * @param monitor - can be null
	 * @return true if the path was tagged correctly, false otherwise (e.g. the path is not linked to the resource)  
	 */
	
	boolean tagResource(IPath aProjectRelativeLocation, String tag, IProgressMonitor monitor);
	
	/**
	 * Returns the path of the first link to this resource tagged with the given tag
	 * @param tag
	 * @return the path linked to this resource tagged with the given tag, or null if no path can be found.
	 */
	IPath getFirstTaggedResource(String tag);
	
	/**
	 * Returns the paths of the links to this resource tagged with the given tag
	 * @param tag
	 * @return the paths linked to this resource tagged with the given tag, or empty array if no paths can be found.
	 */
	IPath[] getTaggedResources(String tag);

}
