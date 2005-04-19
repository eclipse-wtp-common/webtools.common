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
package org.eclipse.wst.common.componentcore;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.internal.resources.FlexibleProject;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualComponent;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualFile;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualFolder;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualReference;
import org.eclipse.wst.common.componentcore.resources.IFlexibleProject;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualContainer;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;

public class ComponentCore {


	public static IFlexibleProject createFlexibleProject(IProject aProject) {
		return new FlexibleProject(aProject); 
	}
	
	public static IVirtualComponent createComponent(IProject aProject, String aComponentName) {
		return new VirtualComponent(aProject, aComponentName, new Path("/")); //$NON-NLS-1$
	}

	public static IVirtualFolder createFolder(IProject aProject, String aComponentName, IPath aRuntimePath) {
		return new VirtualFolder(aProject, aComponentName, aRuntimePath);	
	}

	public static IVirtualFile createFile(IProject aProject, String aComponentName, IPath aRuntimePath) {
		return new VirtualFile(aProject, aComponentName, aRuntimePath);	
	}

	public static IVirtualReference createReference(IVirtualComponent aComponent, IVirtualComponent aReferencedComponent) {
		return new VirtualReference(aComponent, aReferencedComponent);
	}

}
