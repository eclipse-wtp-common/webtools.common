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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;

public class IVirtualFolderAPITest extends IVirtualContainerAPITest {

	public static final String TEST_FOLDER_NAME = "WEB-INF"; //$NON-NLS-1$
	
	public static final IPath WEBCONTENT_FOLDER_REAL_PATH = new Path("/WebModule1/WebContent/");
	public static final IPath WEBINF_FOLDER_REAL_PATH = WEBCONTENT_FOLDER_REAL_PATH.append(TEST_FOLDER_NAME); //$NON-NLS-1$ //$NON-NLS-2$
	public static final IPath WEBINF_FOLDER_RUNTIME_PATH = new Path("/"+TEST_FOLDER_NAME); //$NON-NLS-1$
	
	public static final IPath TESTDATA_FOLDER_REAL_PATH = new Path("WebModule1/testdata"); //$NON-NLS-1$ //$NON-NLS-2$
	public static final IPath TESTDATA_FOLDER_RUNTIME_PATH = new Path("/"); //$NON-NLS-1$
	
	private static final IPath DELETEME_PATH = new Path("/deleteme"); //$NON-NLS-1$ 

	;

	public IVirtualFolderAPITest(String name) {
		super(name);
	} 

	protected void doSetup() throws Exception { 
		
		expectedPlatformContainerPath = TESTDATA_FOLDER_REAL_PATH;
		
		expectedRuntimePath = WEBINF_FOLDER_RUNTIME_PATH;
		expectedName = TEST_FOLDER_NAME;
		expectedProject = TestWorkspace.TEST_PROJECT;
		
		targetExistingPlatformResource = TestWorkspace.TEST_PROJECT.getFolder(WEBINF_FOLDER_REAL_PATH);
		
		//virtualParent = ComponentCore.createComponent(TestWorkspace.TEST_PROJECT, TestWorkspace.WEB_MODULE_1_NAME);
		
		IVirtualComponent component = ComponentCore.createComponent(TestWorkspace.TEST_PROJECT, TestWorkspace.WEB_MODULE_1_NAME);
		IVirtualFolder rootFolder = component.getRootFolder();
		virtualParent = rootFolder;
		
		targetExistingVirtualResource = virtualParent.getFolder(WEBINF_FOLDER_RUNTIME_PATH); 		

		targetVirtualContainer = virtualParent.getFolder(TESTDATA_FOLDER_RUNTIME_PATH); 
		targetPlatformContainer = TestWorkspace.TEST_PROJECT.getFolder(TESTDATA_FOLDER_REAL_PATH);
		
		targetVirtualResourceToDelete = virtualParent.getFolder(DELETEME_PATH);
		targetVirtualResourceToDelete.create(IVirtualResource.FORCE, null);
		
		targetPlatformResourceToDelete = expectedProject.getFolder(targetVirtualResourceToDelete.getProjectRelativePath());			
		
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		
		if(targetPlatformResourceToDelete.exists())
			targetPlatformResourceToDelete.delete(IVirtualResource.FORCE, null); 
	}  
	

	public void testGetUnderlyingFolder() { 
		IFolder underlyingResource  = ((IVirtualFolder)targetExistingVirtualResource).getUnderlyingFolder();		
		IFolder expectedPlatformResource = TestWorkspace.TEST_PROJECT.getFolder(WEBINF_FOLDER_RUNTIME_PATH);		
		assertEquals("The underlying resource should be " +expectedPlatformResource.getProjectRelativePath(), expectedPlatformResource, underlyingResource);		
	}

	public void testGetUnderlyingFolders() { 

		IFolder[] underlyingResources  = ((IVirtualFolder)targetVirtualContainer).getUnderlyingFolders();
		assertEquals("There should be two folders mapped to root", 2, underlyingResources.length); 
		
		Set underlyingResourcesSet = new HashSet(Arrays.asList(underlyingResources));
		Set expectedUnderlyingResourcesSet = new HashSet();
		expectedUnderlyingResourcesSet.add(TestWorkspace.TEST_PROJECT.getFolder(TESTDATA_FOLDER_REAL_PATH));
		expectedUnderlyingResourcesSet.add(TestWorkspace.TEST_PROJECT.getFolder(WEBCONTENT_FOLDER_REAL_PATH));
		assertEquals("Expecting two folders mapped to root." +expectedUnderlyingResourcesSet, underlyingResourcesSet);		

	}
	 

}
