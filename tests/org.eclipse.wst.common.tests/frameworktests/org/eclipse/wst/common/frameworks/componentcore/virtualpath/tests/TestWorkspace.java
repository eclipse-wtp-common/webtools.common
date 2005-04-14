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
package org.eclipse.wst.common.frameworks.componentcore.virtualpath.tests;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.etools.common.test.apitools.ProjectUnzipUtil;
import org.eclipse.wst.common.tests.CommonTestsPlugin;

public class TestWorkspace {

	public static final String PROJECT_NAME = "TestVirtualAPI"; //$NON-NLS-1$
	public static final String WEB_MODULE_1_NAME = "WebModule1"; //$NON-NLS-1$
	public static final String WEB_MODULE_2_NAME = "WebModule2"; //$NON-NLS-1$
	
	public static final String NEW_WEB_MODULE_NAME = "NewWebModule"; //$NON-NLS-1$
	

	public static final String META_INF = "META-INF"; //$NON-NLS-1$
	public static final String WEB_INF = "WEB-INF"; //$NON-NLS-1$
	private static Path zipFilePath = new Path("testData/TestVirtualAPI.zip");
	
	public static final IProject TEST_PROJECT = ResourcesPlugin.getWorkspace().getRoot().getProject(TestWorkspace.PROJECT_NAME);
	  	
	public static final String[] MODULE_NAMES = new String[]{WEB_MODULE_1_NAME, WEB_MODULE_2_NAME};  
	
	
	public static IProject getTargetProject() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
	}

	public static void init() {
		
		try {
			IProject project = getTargetProject();
			if (!project.exists())
				createProject();
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) { 
			e.printStackTrace();
		}
	}
	
	public static boolean createProject() {
		IPath localZipPath = getLocalPath();
		ProjectUnzipUtil util = new ProjectUnzipUtil(localZipPath, new String[]{PROJECT_NAME});
		return util.createProjects();
	}

	private static IPath getLocalPath() {
		URL url = CommonTestsPlugin.instance.find(zipFilePath);
		try {
			url = Platform.asLocalURL(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Path(url.getPath());
	}

}
