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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IVirtualReference {
	
	public void create(int updateFlags, IProgressMonitor aMonitor);
	
	public boolean exists();
	
	public void setRuntimePath(IPath aRuntimePath);
	
	public IPath getRuntimePath();
	
	public void setDependencyType(int aDependencyType);
	
	public int getDependencyType();
	
	public IVirtualComponent getEnclosingComponent();
	
	public IVirtualComponent getReferencedComponent();

}
