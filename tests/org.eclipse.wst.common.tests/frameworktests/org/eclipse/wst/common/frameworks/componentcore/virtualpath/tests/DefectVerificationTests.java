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

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;
import org.eclipse.wst.common.tests.CommonTestsPlugin;

public class DefectVerificationTests extends TestCase {
	

	private static Path zipFilePath = new Path("testData/DefectVerificationTests.zip");

	protected void setUp() throws Exception {
//		IPath localZipPath = getLocalPath();
//		ProjectUnzipUtil util = new ProjectUnzipUtil(localZipPath, new String[]{"DefectVerificationProject"});
//		util.createProjects();
	}
	
	public void test96862() { 
		
		IPath filePath = new Path("/WEB-INF/web.xml");
		IPath folderPath = new Path("/WEB-INF");
		
		IVirtualComponent component = ComponentCore.createComponent(getProject(), "DefectVerificationProject");
		
		IVirtualFolder rootFolder = component.getRootFolder();
		IVirtualResource fileResource = rootFolder.findMember(filePath);		
		assertEquals("The returned type should be a file.", IVirtualResource.FILE, fileResource.getType());

		IVirtualResource folderResource = rootFolder.findMember(folderPath);		
		assertEquals("The returned type should be a folder.", IVirtualResource.FOLDER, folderResource.getType());

	}

	private IProject getProject() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject("DefectVerificationProject");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
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
