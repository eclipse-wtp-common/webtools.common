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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;

public class VirtualReference implements IVirtualReference {

	public void create(int updateFlags, IProgressMonitor aMonitor) {
		// TODO Auto-generated method stub

	}

	public void setRuntimePath(IPath aRuntimePath) {
		// TODO Auto-generated method stub

	}

	public IPath getRuntimePath() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDependencyType(int aDependencyType) {
		// TODO Auto-generated method stub

	}

	public int getDependencyType() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	public IVirtualComponent getEnclosingComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	public IVirtualComponent getReferencedComponent() {
		// TODO Auto-generated method stub
		return null;
	}

}
