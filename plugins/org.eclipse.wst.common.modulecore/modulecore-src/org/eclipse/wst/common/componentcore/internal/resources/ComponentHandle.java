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
package org.eclipse.wst.common.componentcore.internal.resources;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.componentcore.StructureEdit;
import org.eclipse.wst.common.componentcore.UnresolveableURIException;

public class ComponentHandle {
	
	private final IProject project;
	private final String name;
	private String toString;
	private int hashCode;
	
	private ComponentHandle(IProject aProject, String aName) {
		project = aProject;
		name = aName;
	}

	public String getName() {
		return name;
	}
	

	public IProject getProject() {
		return project;
	}

	public static ComponentHandle create(IProject aProject, String aComponentName) {
		return new ComponentHandle(aProject, aComponentName);
	}
	
	public static ComponentHandle create(IProject aContext, URI aComponentURI) {
		IProject componentProject = null;
		String componentName = null;
		if(aComponentURI == null)
			return null;
		if(aComponentURI.segmentCount() == 1) {
			componentProject = aContext;
			componentName = aComponentURI.segment(0);
		} else {
			try {
				componentProject  = StructureEdit.getContainingProject(aComponentURI);
				componentName = StructureEdit.getDeployedName(aComponentURI);
			} catch (UnresolveableURIException e) {
				return null;
			}
		}
		
		return new ComponentHandle(componentProject, componentName);
	}
	
	public String toString() {
		if(toString == null)
			toString = "["+project.getFullPath()+"]:"+name; //$NON-NLS-1$ //$NON-NLS-2$   
		return toString;
	}
	
	public int hashCode() {
		if(hashCode == 0) 
			hashCode = toString().hashCode();
		return hashCode;
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof ComponentHandle) {
			ComponentHandle other = (ComponentHandle) obj;
			return getProject().equals(other.getProject()) && 
					( (getName() == null && other.getName() == null) || 
						getName().equals(other.getName())
					);
		}
		return false;
	}
	

}
