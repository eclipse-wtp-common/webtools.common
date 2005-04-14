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


public interface IFlexibleProject {
	/**
	 * Finds and returns the components within this project, defined by the component model.
	 * 
	 * @return the array of components within this project
	 */
	IVirtualComponent[] getComponents();
	/**
	 * Finds and returns the component within this project, defined by the component model,
	 * specified by the component name. Returns <code>null</code> if no such
	 * component exists.
	 * 
	 * @param aComponentName the string name of the component
	 * @return the component within this project with the given name or <code>null</code> if no such
	 * 		component exists
	 */
	IVirtualComponent getComponent(String aComponentName);
	/**
	 * returns the underlying IProject
	 * 
	 * @return the underlying IProject
	 */
	IProject getProject();

}
