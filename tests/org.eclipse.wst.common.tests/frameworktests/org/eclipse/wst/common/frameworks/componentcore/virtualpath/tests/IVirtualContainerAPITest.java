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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.resources.IVirtualContainer;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;

public abstract class IVirtualContainerAPITest extends IVirtualResourceAPITest {	

	protected IVirtualContainer targetVirtualContainer;
	protected IContainer targetPlatformContainer;
	protected IPath expectedPlatformContainerPath;
	
	/*
	 *  The following fields assume a minimum structure of:
	 *  
	 *   /						[Root]
	 *   	/jsps				[Folder]
	 *   		/TestJsp3.jsp	[File]
	 *   	/WEB-INF			[Folder]
	 *   		/lib			[Folder]
	 *   /TestFile1.txt			[File]
	 * 
	 */
	
	
	protected IPath expectedFileSingleDepthPath = new Path("/jsps/TestJsp3.jsp");
	protected IPath expectedFolderSingleDepthPath = new Path("WEB-INF/lib");
	
	protected String expectedFolderName = "jsps";
	protected IPath expectedFolderZerothDepthPath = new Path(expectedFolderName);
		
	protected String expectedFileName = "TestFile1.txt";
	protected IPath expectedFileZerothDepthPath = new Path(expectedFileName);
	
	public IVirtualContainerAPITest(String name) {
		super(name);
	}	
	

	protected void assertRequirements() {
		super.assertRequirements();
//		assertNotNull("The target virtual container must be specified.", targetVirtualContainer);
//		assertNotNull("The target platform container must be specified.", targetPlatformContainer);
//		assertNotNull("The expected platform container path must be specified.", expectedPlatformContainerPath);
	}
	

	public void testGetFileString() {
		IVirtualFile testFile1txt = targetVirtualContainer.getFile(expectedFileName); //$NON-NLS-1$
		assertEquals("The test file project relative path must match.", expectedPlatformContainerPath.append(expectedFileName), testFile1txt.getProjectRelativePath()); //$NON-NLS-1$
	}
	
	public void testGetFilePath() {
		IVirtualFile test3jsp = targetVirtualContainer.getFile(expectedFileSingleDepthPath);		
		IPath expectedPath = expectedPlatformContainerPath.append(expectedFileSingleDepthPath);
		assertEquals("The test file project relative path must match.", expectedPath, test3jsp.getProjectRelativePath()); //$NON-NLS-1$
	} 
	 
	/*
	 * Class under test for IVirtualFolder getFolder(String)
	 */
	public void testGetFolderString() {
		IVirtualFolder jspsFolder = targetVirtualContainer.getFolder(expectedFolderName);
		assertNotNull("The folder should not be null.", jspsFolder);
		assertEquals("The name of the folder returned shouled match the expected folder.", expectedFolderName, jspsFolder.getName());
	}

	/*
	 * Class under test for IVirtualFolder getFolder(IPath)
	 */
	public void testGetFolderIPath() { 
		IVirtualFolder testGetFolder = targetVirtualContainer.getFolder(expectedFolderZerothDepthPath);
		assertNotNull("The folder should not be null.", testGetFolder);
		assertEquals("The name of the folder returned shouled match the expected folder.", expectedFolderZerothDepthPath, testGetFolder.getRuntimePath());		
	}
 
	/*
	 * Class under test for boolean exists(IPath)
	 */
	public void testExistsIPath() {
		assertTrue("The expected file path should be found to exist.", targetVirtualContainer.exists(expectedFileSingleDepthPath));		
	}

	/*
	 * Class under test for IVirtualResource findMember(String)
	 */
	public void testFindMemberString() {
		IVirtualResource foundResource = targetVirtualContainer.findMember(expectedFolderName);
		assertEquals("The type found for the expected folder name should be IVirtualResource.FOLDER.", IVirtualResource.FOLDER, foundResource.getType());
		IVirtualFolder foundFolder = (IVirtualFolder) foundResource;
		assertEquals("The name should be correct.", expectedFolderName, foundResource.getName());
		

		foundResource = targetVirtualContainer.findMember(expectedFileName);
		assertEquals("The type found for the expected folder name should be IVirtualResource.FILE.", IVirtualResource.FILE, foundResource.getType());
		IVirtualFile foundFile = (IVirtualFile) foundResource;
		assertEquals("The name should be correct.", expectedFileName, foundResource.getName());
	}

	/*
	 * Class under test for IVirtualResource findMember(String, int)
	 */
	public void testFindMemberStringint() {

		IVirtualResource foundResource = targetVirtualContainer.findMember(expectedFolderName, IVirtualResource.NONE);
		assertEquals("The type found for the expected folder name should be IVirtualResource.FOLDER.", IVirtualResource.FOLDER, foundResource.getType());
		IVirtualFolder foundFolder = (IVirtualFolder) foundResource;
		assertEquals("The name should be correct.", expectedFolderName, foundResource.getName());
		

		foundResource = targetVirtualContainer.findMember(expectedFolderName, IVirtualResource.NONE);
		assertEquals("The type found for the expected folder name should be IVirtualResource.FILE.", IVirtualResource.FILE, foundResource.getType());
		IVirtualFile foundFile = (IVirtualFile) foundResource;
		assertEquals("The name should be correct.", expectedFileName, foundResource.getName()); 
	}

	/*
	 * Class under test for IVirtualResource findMember(IPath)
	 */
	public void testFindMemberIPath() {

		IVirtualResource foundResource = targetVirtualContainer.findMember(expectedFolderSingleDepthPath);
		assertEquals("The type found for the expected folder name should be IVirtualResource.FOLDER.", IVirtualResource.FOLDER, foundResource.getType());
		IVirtualFolder foundFolder = (IVirtualFolder) foundResource;
		assertEquals("The name should be correct.", expectedFolderSingleDepthPath.lastSegment(), foundResource.getName());
		

		foundResource = targetVirtualContainer.findMember(expectedFileSingleDepthPath);
		assertEquals("The type found for the expected folder name should be IVirtualResource.FILE.", IVirtualResource.FILE, foundResource.getType());
		IVirtualFile foundFile = (IVirtualFile) foundResource;
		assertEquals("The name should be correct.", expectedFileSingleDepthPath.lastSegment(), foundResource.getName());
	}

	/*
	 * Class under test for IVirtualResource findMember(IPath, int)
	 */
	public void testFindMemberIPathint() {
		IVirtualResource foundResource = targetVirtualContainer.findMember(expectedFolderSingleDepthPath, IVirtualResource.NONE);
		assertEquals("The type found for the expected folder name should be IVirtualResource.FOLDER.", IVirtualResource.FOLDER, foundResource.getType());
		IVirtualFolder foundFolder = (IVirtualFolder) foundResource;
		assertEquals("The name should be correct.", expectedFolderSingleDepthPath.lastSegment(), foundResource.getName());
		

		foundResource = targetVirtualContainer.findMember(expectedFileSingleDepthPath, IVirtualResource.NONE);
		assertEquals("The type found for the expected folder name should be IVirtualResource.FILE.", IVirtualResource.FILE, foundResource.getType());
		IVirtualFile foundFile = (IVirtualFile) foundResource;
		assertEquals("The name should be correct.", expectedFileSingleDepthPath.lastSegment(), foundResource.getName());
	}
 

	/*
	 * Class under test for IVirtualResource[] members()
	 */
	public void testMembers() {
	}

	/*
	 * Class under test for IVirtualResource[] members(int)
	 */
	public void testMembersint() {
	}
 

	public void testCreate() {
	}

}
