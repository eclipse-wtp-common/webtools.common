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
package org.eclipse.wst.common.frameworks.modulecore.tests;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.modulecore.ComponentResource;
import org.eclipse.wst.common.modulecore.ModuleCore;
import org.eclipse.wst.common.modulecore.WorkbenchComponent;
import org.eclipse.wst.common.modulecore.resources.IVirtualContainer;
import org.eclipse.wst.common.modulecore.resources.IVirtualFile;
import org.eclipse.wst.common.modulecore.resources.IVirtualFolder;
import org.eclipse.wst.common.modulecore.resources.IVirtualResource;

public class IVirtualFolderAPITest extends TestCase {
	
	public static final IProject TEST_PROJECT = ResourcesPlugin.getWorkspace().getRoot().getProject(TestWorkspace.PROJECT_NAME);

	public static final String TEST_FOLDER_NAME = "WEB-INF"; //$NON-NLS-1$
	
	public static final Path WEBINF_FOLDER_REAL_PATH = new Path("/WebModule1/WebContent/"+TEST_FOLDER_NAME); //$NON-NLS-1$ //$NON-NLS-2$
	public static final Path WEBINF_FOLDER_RUNTIME_PATH = new Path("/"+TEST_FOLDER_NAME); //$NON-NLS-1$
	
	public static final Path TESTDATA_FOLDER_REAL_PATH = new Path("/WebModule1/testdata"); //$NON-NLS-1$ //$NON-NLS-2$
	public static final Path TESTDATA_FOLDER_RUNTIME_PATH = new Path("/"); //$NON-NLS-1$
	
	private static final IPath DELETEME_PATH = new Path("/deleteme"); //$NON-NLS-1$
	
	private IVirtualContainer component;
	
	private IVirtualFolder webInfFolder;
	private IFolder realWebInfFolder;
	
	private IVirtualFolder deletemeVirtualFolder;
	private IFolder deletemeFolder;	

	private IVirtualFolder testdataFolder;
	private IFolder realTestdataFolder;

	public IVirtualFolderAPITest(String name) {
		super(name);
	} 

	protected void setUp() throws Exception {
		super.setUp();
		TestWorkspace.init();		
		
		realWebInfFolder = TEST_PROJECT.getFolder(WEBINF_FOLDER_REAL_PATH);
		
		component = ModuleCore.create(TEST_PROJECT, TestWorkspace.WEB_MODULE_1_NAME);
		webInfFolder = component.getFolder(WEBINF_FOLDER_RUNTIME_PATH); 		

		testdataFolder = component.getFolder(TESTDATA_FOLDER_RUNTIME_PATH); 
		realTestdataFolder = TEST_PROJECT.getFolder(TESTDATA_FOLDER_REAL_PATH);
		
		deletemeVirtualFolder = component.getFolder(DELETEME_PATH);
		deletemeVirtualFolder.create(IVirtualResource.FORCE, null);
		
		deletemeFolder = deletemeVirtualFolder.getRealFolder();		
		
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		
		if(deletemeFolder.exists())
			deletemeFolder.delete(IVirtualResource.FORCE, null);
		
	}

	public void testGetFileExtension() {
		assertTrue("The /WEB-INF folder should have no file extension.", webInfFolder.getFileExtension() == null); //$NON-NLS-1$
	}

	public void testGetWorkspaceRelativePath() {
		IPath realPath = realWebInfFolder.getFullPath();
		IPath virtualPath = webInfFolder.getWorkspaceRelativePath();
		assertEquals("The workspace relative path of the virtual resource must match the real resource", realPath, virtualPath); //$NON-NLS-1$

	}

	public void testGetProjectRelativePath() {
		IPath realPath = realWebInfFolder.getProjectRelativePath();
		IPath virtualPath = webInfFolder.getProjectRelativePath();
		assertEquals("The project relative path of the virtual resource must match the real resource", realPath, virtualPath); //$NON-NLS-1$
	}

	public void testGetRuntimePath() { 
		IPath virtualPath = webInfFolder.getRuntimePath();
		assertEquals("The runtime path of the virtual resource must match the real resource", WEBINF_FOLDER_RUNTIME_PATH, virtualPath); //$NON-NLS-1$
	
	}
	
	public void testGetName() {
		assertEquals("The name of the virtual resource must match the expected name.", TEST_FOLDER_NAME, webInfFolder.getName()); //$NON-NLS-1$
	}

	public void testGetParent() {
		assertEquals("The parent of the virtual resource must match the component.", component, webInfFolder.getParent()); //$NON-NLS-1$
	}

	public void testGetProject() {
		assertEquals("The project of the virtual resource must match the test project.", TEST_PROJECT, webInfFolder.getProject()); //$NON-NLS-1$
	}  

	public void testGetType() {
		assertEquals("The type of the virtual resource must match the test project.", IResource.FOLDER, webInfFolder.getType()); //$NON-NLS-1$
	}
	
	public void testGetComponentName() { 
		assertEquals("The component name of the virtual resource must match the test project.", TestWorkspace.WEB_MODULE_1_NAME, webInfFolder.getComponentName()); //$NON-NLS-1$
	}
	
	public void testGetFileString() {
		IVirtualFile testFile1txt = testdataFolder.getFile("TestFile1.txt"); //$NON-NLS-1$
		assertEquals("The test file project relative path must match.", TESTDATA_FOLDER_REAL_PATH.append("TestFile1.txt"), testFile1txt.getProjectRelativePath()); //$NON-NLS-1$
	}
	
	public void testGetFilePath() {
		IVirtualFile test3jsp = testdataFolder.getFile(new Path("/jsps/TestJsp3.jsp"));
		
		IPath expectedPath = TESTDATA_FOLDER_REAL_PATH.append(new Path("/jsps/TestJsp3.jsp"));
		assertEquals("The test file project relative path must match.", expectedPath, test3jsp.getProjectRelativePath()); //$NON-NLS-1$
	}
	
	public void testIsAccessible() {
	}

	/*
	 * Class under test for void delete(int, IProgressMonitor)
	 */
	public void testDeleteintIProgressMonitor() throws Exception {
		deletemeVirtualFolder.delete(0, null);
		
		assertTrue("The real folder should be deleted when IVirtualResource.DELETE_METAMODEL_ONLY is NOT supplied.", !deletemeFolder.exists()); //$NON-NLS-1$
				
		IVirtualResource[] members = component.members();
		
		for (int i = 0; i < members.length; i++) {
			if(members[i].getRuntimePath().equals(deletemeVirtualFolder.getRuntimePath())) {
				fail("Found deleted folder in members()"); //$NON-NLS-1$
			}
		}		
	}
	
	/*
	 * Class under test for void delete(int, IProgressMonitor)
	 */
	public void testDeleteintIProgressMonitor2() throws Exception {
		deletemeVirtualFolder.delete(IVirtualResource.DELETE_METAMODEL_ONLY, null);
		
		assertTrue("The real folder should not be deleted when IVirtualResource.DELETE_METAMODEL_ONLY is supplied.", deletemeFolder.exists()); //$NON-NLS-1$
				
		// only handles explicit mappings
		ModuleCore moduleCore = null;
		try {
			URI runtimeURI = URI.createURI(deletemeVirtualFolder.getRuntimePath().toString());
			moduleCore = ModuleCore.getModuleCoreForWrite(TEST_PROJECT);
			WorkbenchComponent wbComponent = moduleCore.findWorkbenchModuleByDeployName(TestWorkspace.WEB_MODULE_1_NAME);
			ComponentResource[] resources = wbComponent.findWorkbenchModuleResourceByDeployPath(runtimeURI);
			assertTrue("There should be no matching components found in the model.", resources.length == 0); //$NON-NLS-1$
			
		} finally {
			if (moduleCore != null) {
				moduleCore.saveIfNecessary(null);
				moduleCore.dispose();
			}
		}
	}
	
	/*
	 * Class under test for void delete(boolean, IProgressMonitor)
	 */
	public void testDeletebooleanIProgressMonitor()  throws Exception  {
		deletemeVirtualFolder.delete(IVirtualResource.FORCE, null);
		
		assertTrue("The real folder should be deleted when IVirtualResource.DELETE_METAMODEL_ONLY is NOT supplied.", !deletemeFolder.exists()); //$NON-NLS-1$
				
		IVirtualResource[] members = component.members();
		
		for (int i = 0; i < members.length; i++) {
			if(members[i].getRuntimePath().equals(deletemeVirtualFolder.getRuntimePath())) {
				fail("Found deleted folder in members()"); //$NON-NLS-1$
			}
		}	
	}	
	
	/*
	 * Class under test for void delete(boolean, IProgressMonitor)
	 */
	public void testDeletebooleanbooleanIProgressMonitor()  throws Exception  {
		deletemeVirtualFolder.delete(IVirtualResource.FORCE | IVirtualResource.KEEP_HISTORY, null);
		
		assertTrue("The real folder should be deleted when IVirtualResource.DELETE_METAMODEL_ONLY is NOT supplied.", !deletemeFolder.exists()); //$NON-NLS-1$
				
		IVirtualResource[] members = component.members();
		
		for (int i = 0; i < members.length; i++) {
			if(members[i].getRuntimePath().equals(deletemeVirtualFolder.getRuntimePath())) {
				fail("Found deleted folder in members()"); //$NON-NLS-1$
			}
		}	
	}	
	 	
	public void testFindMemberString() { 
	}
	
	public void testFindMemberStringBoolean() { 
	}
	
	public void testFindMemberIPath() {
	}

	public void testFindMemberIPathBoolean() { 
	}

	public void testContains() {
	}

	public void testIsConflicting() {
	}

}
