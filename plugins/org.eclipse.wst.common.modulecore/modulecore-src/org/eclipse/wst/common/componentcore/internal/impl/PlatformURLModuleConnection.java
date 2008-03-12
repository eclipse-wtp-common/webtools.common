/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Jan 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.wst.common.componentcore.internal.impl;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.internal.boot.PlatformURLConnection;
import org.eclipse.core.internal.boot.PlatformURLHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;

/**
 * @author mdelder
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Style - Code Templates
 */
public class PlatformURLModuleConnection extends PlatformURLConnection {

	public static final String MODULE = "module"; //$NON-NLS-1$
	public static final String MODULE_PROTOCOL = MODULE + PlatformURLHandler.PROTOCOL_SEPARATOR;
	
	public static final String CLASSPATH = "classpath"; //$NON-NLS-1$

	public static final String RESOURCE_MODULE = "resource"; //$NON-NLS-1$
	public static final String BINARY_MODULE = "binary"; //$NON-NLS-1$


	public PlatformURLModuleConnection(URL aURL) {
		super(aURL);
	}
 
	public static URI resolve(URI aModuleResourceRuntimePath) throws IOException {
		try {
			IProject componentProject = StructureEdit.getContainingProject(aModuleResourceRuntimePath);
			//String componentName = ModuleURIUtil.getDeployedName(aModuleResourceRuntimePath);
			URI runtimeURI = ModuleURIUtil.trimToDeployPathSegment(aModuleResourceRuntimePath);
			IPath runtimePath = new Path(runtimeURI.path());
			IVirtualComponent component = ComponentCore.createComponent(componentProject);
			//IVirtualFile vFile = component.getFile(runtimePath);
			IVirtualFolder rootFolder = component.getRootFolder();
			int matchingSegs = runtimePath.matchingFirstSegments(rootFolder.getProjectRelativePath());
			if(matchingSegs > 0)
				runtimePath = runtimePath.removeFirstSegments(matchingSegs);
			IVirtualFile vFile = rootFolder.getFile(runtimePath);
			return URI.createPlatformResourceURI(vFile.getWorkspaceRelativePath().toString());
		} catch (Exception e) {
		}
		return aModuleResourceRuntimePath;
		 
	}

//	private static URI normalizeToWorkspaceRelative(IProject project, IPath sourcePath, URI moduleResourceDeployPath) throws UnresolveableURIException {
//		String projectName = project.getName();
//		return URI.createURI(projectName + '/' + sourcePath.toString());
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.boot.PlatformURLConnection#resolve()
	 */
	protected URL resolve() throws IOException {
		System.out.println("URL: " + getURL());
		//IPath moduleRelativePath = new Path(getURL().toExternalForm());
		//String moduleName = moduleRelativePath.segment(1);

		IPath resolvedPath = null; // handle.getResolvedPath().append(moduleRelativePath.removeFirstSegments(2));

		int count = resolvedPath.segmentCount();
		// if there are two segments then the second is a project name.
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(resolvedPath.segment(0));
		if (!project.exists()) {
			// TODO Fix this string
			String message = "Could not resolve URL"; //Policy.bind("url.couldNotResolve", project.getName(), url.toExternalForm()); //$NON-NLS-1$
			throw new IOException(message);
		}
		IPath result = null;
		if (count == 2)
			result = project.getLocation();
		else {
			resolvedPath = resolvedPath.removeFirstSegments(2);
			result = project.getFile(resolvedPath).getLocation();
		}
		return new URL("file", "", result.toString()); //$NON-NLS-1$ //$NON-NLS-2$

	}

	public static void startup() {
		PlatformURLHandler.register(PlatformURLModuleConnection.MODULE, PlatformURLModuleConnection.class);
	}

	// private IProject getRelevantProject(URI aModuleURI) {
	// aModuleURI.segment()
	// }
}
