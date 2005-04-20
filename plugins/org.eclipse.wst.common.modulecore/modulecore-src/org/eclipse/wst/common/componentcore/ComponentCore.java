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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.internal.ComponentResource;
import org.eclipse.wst.common.componentcore.internal.ModulecorePlugin;
import org.eclipse.wst.common.componentcore.internal.resources.FlexibleProject;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualComponent;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualFile;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualFolder;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualReference;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualResource;
import org.eclipse.wst.common.componentcore.resources.IFlexibleProject;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualContainer;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;
import org.eclipse.wst.common.frameworks.internal.enablement.nonui.WorkbenchUtil;

public class ComponentCore {
	
	private static final IVirtualResource[] NO_RESOURCES = new VirtualResource[0];


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
	public static IVirtualResource[] createResources(IResource aResource) {
		IProject proj = aResource.getProject();
		StructureEdit se = null;
		List foundResources = new ArrayList();
		try {
			se = StructureEdit.getStructureEditForRead(proj);
			ComponentResource[] resources = se.findResourcesBySourcePath(aResource.getFullPath());
			for (int i = 0; i < resources.length; i++) {
				if (aResource.getType() == IResource.FILE)
					foundResources.add(new VirtualFile(proj,resources[i].getComponent().getName(), resources[i].getRuntimePath()));
				else
					foundResources.add(new VirtualFolder(proj,resources[i].getComponent().getName(), resources[i].getRuntimePath()));
			}
		}
		catch (UnresolveableURIException e) {
			e.printStackTrace();
		}
		 finally {
			se.dispose();	
		}
		 if (foundResources.size() > 0)
				return (IVirtualResource[]) foundResources.toArray(new VirtualResource[foundResources.size()]);
			return NO_RESOURCES;
	}
}
