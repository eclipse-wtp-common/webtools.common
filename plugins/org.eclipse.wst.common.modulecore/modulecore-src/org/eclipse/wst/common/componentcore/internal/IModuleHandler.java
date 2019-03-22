/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/


package org.eclipse.wst.common.componentcore.internal;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

/**
 * Interface intended to reflect behavior of customized components
 *
 */
public interface IModuleHandler {
	
	
	/**
	 * This is used to return a proper archive name based on the component type
	 * @param proj 
	 * @param comp
	 * @return String archive name
	 */
	public String getArchiveName(IProject proj, IVirtualComponent comp);
	
	/**
	 * Used to filter items displayed in Add dialog
	 * @param sourceComponent
	 * @param availableComponents
	 * @return List of items that will not be shown in the add ref dialogs
	 */
	public List<IProject> getFilteredProjectListForAdd(IVirtualComponent sourceComponent, List<IProject> availableComponents);

	/**
	 * This call is meant to add IVirtualCompoonent infrastructure to any project passed if needed
	 * Meant be overridden to include technology specific attributes
	 * @param proj
	 * @return boolean indicating operation success
	 */
	public boolean setComponentAttributes(IProject proj);
	

}
