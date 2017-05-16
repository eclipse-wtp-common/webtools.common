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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;

public class IVirtualFileAPITest extends IVirtualResourceAPITest {
	
	public IVirtualFileAPITest(String name) {
		super(name);
	}
	
	protected void doSetup() throws Exception { 
		
		
	}

	/*
	 * Class under test for void VirtualFile(IProject, String, IPath)
	 */
	public void testVirtualFileIProjectStringIPath() {
		
	}
 

	public void testGetUnderlyingFile() { 

		IFile platformFileToCreate = ((IVirtualFile)targetVirtualResourceToCreate).getUnderlyingFile(); 
		assertTrue("The underyling resource should not exist.", !platformFileToCreate.exists() );  

		IFile existingPlatformFile = ((IVirtualFile)targetExistingVirtualResource).getUnderlyingFile(); 
		assertTrue("The underyling resource should not exist.", !existingPlatformFile.exists() );

	}

	public void testGetUnderlyingFiles() {  
		
		IResource[] platformFileToCreate = ((IVirtualFile)targetVirtualResourceToCreate).getUnderlyingFiles();
		assertEquals("There should only be one resource in the result array.", 1, platformFileToCreate.length);
		assertEquals("The type of the underlying resource should match IResource.FILE.", IResource.FILE, platformFileToCreate[0].getType());
		assertTrue("The underyling resource should not exist.", !platformFileToCreate[0].exists() ); 


		IResource[] existingPlatformFile = ((IVirtualFile)targetExistingVirtualResource).getUnderlyingFiles();
		assertEquals("There should only be one resource in the result array.", 1, existingPlatformFile.length);
		assertEquals("The type of the underlying resource should match IResource.FILE.", IResource.FILE, existingPlatformFile[0].getType());
		assertTrue("The underyling resource should not exist.", !existingPlatformFile[0].exists() );  

		IFile multiPlatformFile = ((IVirtualFile)targetMultiVirtualResource).getUnderlyingFile(); 
		assertTrue("The underyling resource should not exist.", !multiPlatformFile.exists() );
	}

}
