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

}
