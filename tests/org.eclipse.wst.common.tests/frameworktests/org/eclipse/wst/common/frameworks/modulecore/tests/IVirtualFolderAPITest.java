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
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.modulecore.ModuleCore;
import org.eclipse.wst.common.modulecore.resources.IVirtualContainer;
import org.eclipse.wst.common.modulecore.resources.IVirtualFolder;

public class IVirtualFolderAPITest extends TestCase {
	
	private IVirtualFolder webInfFolder;
	private IFolder realWebInfFolder;

	public IVirtualFolderAPITest(String name) {
		super(name);
	}
	 

	protected void setUp() throws Exception {
		super.setUp();
		TestWorkspace.init();
		IProject targetProject = ResourcesPlugin.getWorkspace().getRoot().getProject(TestWorkspace.PROJECT_NAME);
		
		realWebInfFolder = targetProject.getFolder(new Path("/"+TestWorkspace.WEB_MODULE_1_NAME+"/webContent/WEB-INF"));
		
		IVirtualContainer component = ModuleCore.create(targetProject, TestWorkspace.WEB_MODULE_1_NAME);
		webInfFolder = component.getFolder(new Path("/WEB-INF")); 		
	}


	/*
	 * Class under test for void copy(IPath, boolean, IProgressMonitor)
	 */
	public void testCopyIPathbooleanIProgressMonitor() {
	}

	/*
	 * Class under test for void copy(IPath, int, IProgressMonitor)
	 */
	public void testCopyIPathintIProgressMonitor() {
	}

	/*
	 * Class under test for void delete(boolean, IProgressMonitor)
	 */
	public void testDeletebooleanIProgressMonitor() {
	}

	/*
	 * Class under test for void delete(boolean, boolean, IProgressMonitor)
	 */
	public void testDeletebooleanbooleanIProgressMonitor() {
	}

	/*
	 * Class under test for void delete(int, IProgressMonitor)
	 */
	public void testDeleteintIProgressMonitor() {
	}

	public void testGetFileExtension() {
		assertTrue("The /WEB-INF folder should have no file extension.", webInfFolder.getFileExtension() == null); //$NON-NLS-1$
	}

	public void testGetWorkspaceRelativePath() {
		
	}

	public void testGetProjectRelativePath() {
		IPath realPath = realWebInfFolder.getProjectRelativePath();
		IPath virtualPath = webInfFolder.getProjectRelativePath();
		assertEquals("The project relative path of the virtual resource must match the real resource", realPath, virtualPath);
	}

	public void testGetRuntimePath() {
	}
	
	public void testGetName() {
	}

	public void testGetParent() {
	}

	public void testGetProject() {
	}  

	public void testGetType() {
	}

	public void testIsAccessible() {
	}

	public void testIsReadOnly() {
	}

	/*
	 * Class under test for void move(IPath, boolean, IProgressMonitor)
	 */
	public void testMoveIPathbooleanIProgressMonitor() {
	}

	/*
	 * Class under test for void move(IPath, int, IProgressMonitor)
	 */
	public void testMoveIPathintIProgressMonitor() {
	}

	public void testRefreshLocal() {
	}

	public void testSetReadOnly() {
	}

	public void testContains() {
	}

	public void testIsConflicting() {
	}

}
