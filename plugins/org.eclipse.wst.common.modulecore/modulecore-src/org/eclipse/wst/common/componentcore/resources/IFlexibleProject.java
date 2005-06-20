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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Represents a project that supports a flexible project model.
 * <p>
 * Flexible projects contain logical "workbench components" which allow clients
 * to group project resources to satisfy certain expected structures.
 * </p>
 * 
 * @since 1.0
 */
public interface IFlexibleProject {
	/**
	 * Finds and returns the components within this project, defined by the
	 * component model.
	 * 
	 * @return the array of components within this project
	 */
	IVirtualComponent[] getComponents();

	/**
	 * Finds and returns the component within this project, defined by the
	 * component model, specified by the component name. Returns
	 * <code>null</code> if no such component exists.
	 * 
	 * @param aComponentName
	 *            the string name of the component
	 * @return the component within this project with the given name or
	 *         <code>null</code> if no such component exists
	 */
	IVirtualComponent getComponent(String aComponentName);
	
	/**
	 * Finds and returns the components within this project of the specified
	 * component type.
	 * 
	 * @param type
	 * @return the array of components within this project for this componentType
	 */
	public IVirtualComponent[] getComponentsOfType(String type);

	/**
	 * Returns the underlying IProject
	 * 
	 * @return the underlying IProject
	 */
	IProject getProject();

	/**
	 * @return true if the underlying IProject is flexible
	 */
	boolean isFlexible();

	/**
	 * Creates an IFlexible Project model for the underlying IProject. Assumes a
	 * valid IProject has already been created.	
	 */
	void create(int theFlags, IProgressMonitor aMonitor);

}
