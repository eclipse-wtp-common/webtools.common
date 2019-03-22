/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/ 
package org.eclipse.wst.common.frameworks.componentcore.tests;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;


public class IVirtualFileAPITest extends BaseVirtualTest {

	protected IVirtualFile testFile1;
	protected IFile realTestFile1;
	public static final Path TEST_FILE_REAL_PATH = new Path("WebModule1/testdata/TestFile1.txt"); //$NON-NLS-1$ //$NON-NLS-2$
	public static final Path TEST_FILE_RUNTIME_PATH = new Path("/"); //$NON-NLS-1$


	public IVirtualFileAPITest(String name) {
		super(name);
		
	}
	protected void setUp() throws Exception {
		super.setUp();
		IVirtualFolder rootFolder = component.getRootFolder();
		testFile1 = rootFolder.getFile(TESTDATA_FOLDER_RUNTIME_PATH); 
		realTestFile1 = TEST_PROJECT.getFile(TESTDATA_FOLDER_REAL_PATH);
		
	}

	public void testGetUnderlyingFile() {
		IFile file = testFile1.getUnderlyingFile();
	}

	public void testGetUnderlyingFiles() {
		IFile[] file = testFile1.getUnderlyingFiles();
	}
	
	public void testIsAccessible() throws CoreException {
		assertEquals(((IVirtualResource)deletemeVirtualFolder).isAccessible(),true);
		((IVirtualResource)deletemeVirtualFolder).delete(IVirtualResource.FORCE, null);
		//assertEquals(deletemeVirtualFolder.isAccessible(),false);
	}

}
