/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 *  $$RCSfile: WorkbenchURIConverter.java,v $$
 *  $$Revision: 1.2 $$  $$Date: 2005/02/15 23:04:14 $$ 
 */
package org.eclipse.jem.util.emf.workbench;

import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.resource.URIConverter;

/**
 * Implementers of this interface are WorkbenchURI converters. Workbench URI converters handle references to files in the project's containers.
 * This converter is only necessary to resolve old ambiguous workbench URIs.
 * @since 1.0.0
 */
public interface WorkbenchURIConverter extends URIConverter {

	/**
	 * Add input container to to the converter.
	 * 
	 * @param aContainer
	 * 
	 * @since 1.0.0
	 */
	void addInputContainer(IContainer aContainer);

	/**
	 * Add list of containers to the converter.
	 * 
	 * @param containers
	 *            list of <code>IContainer</code>
	 * 
	 * @since 1.0.0
	 */
	void addInputContainers(List containers);

	/**
	 * Get the file relative to a container.
	 * 
	 * @param uri
	 * @return file relative to a container or <code>null</code> if not.
	 * 
	 * @since 1.0.0
	 */
	IFile getFile(String uri);

	/**
	 * Get first input container
	 * 
	 * @return first input container or <code>null</code> if none set.
	 * 
	 * @since 1.0.0
	 */
	IContainer getInputContainer();

	/**
	 * Get all input containers.
	 * 
	 * @return all input containers.
	 * 
	 * @since 1.0.0
	 */
	List getInputContainers();

	/**
	 * Get the output container if set.
	 * 
	 * @return output container or <code>null</code> if not set.
	 * 
	 * @since 1.0.0
	 */
	IContainer getOutputContainer();

	/**
	 * Set the output container.
	 * 
	 * @param container
	 * 
	 * @since 1.0.0
	 */
	void setOutputContainer(IContainer container);

	/**
	 * Return an IFile for
	 * 
	 * @aPath. If we have a parent and we do not contain the first segment of the aPath, forward to the parent to retrieve the file.
	 * @param aPath
	 * @return
	 * 
	 * @since 1.0.0
	 */
	IFile getOutputFile(IPath aPath);

	/**
	 * Get output file with mapping applied.
	 * 
	 * @param uri
	 * @return
	 * 
	 * @since 1.0.0
	 */
	IFile getOutputFileWithMappingApplied(String uri);

	/**
	 * Remove input container from list.
	 * 
	 * @param aContainer
	 * @return <code>true</code> if removed.
	 * 
	 * @since 1.0.0
	 */
	boolean removeInputContainer(IContainer aContainer);

	/**
	 * Return true if we can retrieve the resource used to open an input stream on.
	 * 
	 * @param aFileName
	 * @return <code>true</code> if filename is valid for file stream access.
	 * @since 1.0.0
	 *  
	 */
	boolean canGetUnderlyingResource(String aFileName);

	/**
	 * Is force save relative flag turned on.
	 * 
	 * @return <code>true</code> if force save relative is turned on.
	 * 
	 * @since 1.0.0
	 */
	boolean isForceSaveRelative();

	/**
	 * Set to true if you do not want any path manipulation when creating the output stream..
	 * 
	 * @param forceSaveRelative
	 *            <code>true</code> to force saves as relative.
	 */
	void setForceSaveRelative(boolean forceSaveRelative);

}