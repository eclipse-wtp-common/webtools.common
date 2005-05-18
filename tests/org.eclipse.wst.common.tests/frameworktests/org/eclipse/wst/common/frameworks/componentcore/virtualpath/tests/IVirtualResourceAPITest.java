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

import junit.framework.TestCase;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.ComponentResource;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.internal.impl.ResourceTreeNode;
import org.eclipse.wst.common.componentcore.internal.impl.ResourceTreeRoot;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualContainer;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;

public abstract class IVirtualResourceAPITest extends TestCase {
	
	protected IVirtualContainer virtualParent;	
	protected IVirtualResource targetExistingVirtualResource;
	protected IResource targetExistingPlatformResource;
	  
	protected IVirtualFolder targetVirtualResourceToDelete;
	protected IResource targetPlatformResourceToDelete;	 
		
	protected IVirtualResource targetVirtualResourceToCreate = null;
	protected IVirtualResource targetMultiVirtualResource = null; 
	
	protected IProject expectedProject;	
	protected IPath expectedRuntimePath;
	protected String expectedName;

	private static final String PROJECT_NAME = null;

	public IVirtualResourceAPITest(String name) {
		super(name);
	}  
	
	protected final void setUp() throws Exception {	
		TestWorkspace.init();
		doSetup();
		assertRequirements();
	}
	
	protected void assertRequirements() { 
		
	}

	protected abstract void doSetup() throws Exception ;

	public void testCreateLinkIPathintIProgressMonitor() throws Exception {
		
		IVirtualComponent component = ComponentCore.createComponent(TestWorkspace.getTargetProject(), TestWorkspace.WEB_MODULE_2_NAME);
		IVirtualFolder images = component.getFolder(new Path("/images")); //$NON-NLS-1$		
		images.createLink(new Path("/WebModule2/images"), 0, null); //$NON-NLS-1$

		IFolder realImages = TestWorkspace.getTargetProject().getFolder(new Path("/WebModule2/images")); //$NON-NLS-1$
		assertTrue("The /WebContent2/images directory must exist.", realImages.exists()); //$NON-NLS-1$

		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForRead(TestWorkspace.getTargetProject());
			WorkbenchComponent wbComponent = moduleCore.findComponentByName(TestWorkspace.WEB_MODULE_2_NAME);

			ComponentResource[] componentResources = wbComponent.findResourcesByRuntimePath(new Path("/images")); //$NON-NLS-1$

			assertTrue("There should be at least one mapping for virtual path \"/images\".", componentResources.length > 0); //$NON-NLS-1$

			ResourceTreeRoot resourceTreeRoot = ResourceTreeRoot.getSourceResourceTreeRoot(wbComponent);
			componentResources = resourceTreeRoot.findModuleResources(realImages.getFullPath(), ResourceTreeNode.CREATE_NONE);

			assertTrue("There should be exactly one Component resource with the source path \"" + realImages.getProjectRelativePath() + "\".", componentResources.length == 1); //$NON-NLS-1$ //$NON-NLS-2$

			assertTrue("The runtime path should match \"/images\".", componentResources[0].getRuntimePath().toString().equals("/images")); //$NON-NLS-1$ //$NON-NLS-2$

			// make sure that only one component resource is created

			images.createLink(new Path("/WebModule2/images"), 0, null); //$NON-NLS-1$

			componentResources = resourceTreeRoot.findModuleResources(realImages.getFullPath(), ResourceTreeNode.CREATE_NONE);

			assertTrue("There should be exactly one Component resource with the source path \"" + realImages.getProjectRelativePath() + "\".", componentResources.length == 1); //$NON-NLS-1$ //$NON-NLS-2$

			assertTrue("The runtime path should match \"/images\".", componentResources[0].getRuntimePath().toString().equals("/images")); //$NON-NLS-1$ //$NON-NLS-2$
		} finally {
			if (moduleCore != null)
				moduleCore.dispose();
		}
		
	}
	
	public void testEquals() throws Exception {
		
	}
	
	public void testExists() throws Exception {
		
	}

	public void testGetFileExtension() {
		assertTrue("The existing virtual resource should have no file extension.", targetExistingVirtualResource.getFileExtension() == null); //$NON-NLS-1$
	}

	public void testGetWorkspaceRelativePath() {
		IPath realPath = targetExistingPlatformResource.getFullPath();
		IPath virtualPath = targetExistingVirtualResource.getWorkspaceRelativePath();
		assertEquals("The workspace relative path of the virtual resource must match the real resource", realPath, virtualPath); //$NON-NLS-1$

	}

	public void testGetProjectRelativePath() {
		IPath realPath = targetExistingPlatformResource.getProjectRelativePath();
		IPath virtualPath = targetExistingVirtualResource.getProjectRelativePath();
		assertEquals("The project relative path of the virtual resource must match the real resource", realPath, virtualPath); //$NON-NLS-1$
	}

	public void testGetRuntimePath() { 
		IPath virtualPath = targetExistingVirtualResource.getRuntimePath();
		assertEquals("The runtime path of the virtual resource must match the real resource", expectedRuntimePath, virtualPath); //$NON-NLS-1$
	}
	

	public void testGetName() {
		assertEquals("The name of the virtual resource must match the expected name.", expectedName, targetExistingVirtualResource.getName()); //$NON-NLS-1$
	}

	public void testGetParent() {
		assertEquals("The parent of the virtual resource must match the component.", virtualParent, targetExistingVirtualResource.getParent()); //$NON-NLS-1$
	}

	public void testGetProject() {
		assertEquals("The project of the virtual resource must match the test project.", expectedProject, targetExistingVirtualResource.getProject()); //$NON-NLS-1$
	}  

	public void testGetType() {
		assertEquals("The type of the virtual resource must match the test project.", IResource.FOLDER, targetExistingVirtualResource.getType()); //$NON-NLS-1$
	}
	
	public void testGetComponent() { 
		assertEquals("The component name of the virtual resource must match the test project.", TestWorkspace.WEB_MODULE_1_NAME, targetExistingVirtualResource.getComponent().getName()); //$NON-NLS-1$
	}

	public void testIsAccessible() {
		assertEquals("The platform resource should be accessible only if the virtual resource is accessible.", targetExistingPlatformResource.isAccessible(), targetExistingPlatformResource.isAccessible()); //$NON-NLS-1$
	} 

	/*
	 * Class under test for void delete(int, IProgressMonitor)
	 */
	public void testDeleteintIProgressMonitor() throws Exception {
		targetVirtualResourceToDelete.delete(0, null);
		
		assertTrue("The real folder should be deleted when IVirtualResource.DELETE_METAMODEL_ONLY is NOT supplied.", !targetPlatformResourceToDelete.exists()); //$NON-NLS-1$
				
		IVirtualResource[] members = virtualParent.members();
		
		for (int i = 0; i < members.length; i++) {
			if(members[i].getRuntimePath().equals(targetVirtualResourceToDelete.getRuntimePath())) {
				fail("Found deleted folder in members()"); //$NON-NLS-1$
			}
		}		
	}
	
	/*
	 * Class under test for void delete(int, IProgressMonitor)
	 */
	public void testDeleteintIProgressMonitor2() throws Exception {
		targetVirtualResourceToDelete.delete(IVirtualResource.IGNORE_UNDERLYING_RESOURCE, null);
		
		assertTrue("The real resource should not be deleted when IVirtualResource.IGNORE_UNDERLYING_RESOURCE is supplied.", targetPlatformResourceToDelete.exists()); //$NON-NLS-1$
				
		// only handles explicit mappings
		StructureEdit moduleCore = null;
		try { 
			moduleCore = StructureEdit.getStructureEditForWrite(expectedProject);
			WorkbenchComponent wbComponent = moduleCore.findComponentByName(TestWorkspace.WEB_MODULE_1_NAME);
			ComponentResource[] resources = wbComponent.findResourcesByRuntimePath(targetVirtualResourceToDelete.getRuntimePath());
			assertTrue("There should be no matching resources found in the model.", resources.length == 0); //$NON-NLS-1$
			
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
	public void testDeleteintIProgressMonitor3()  throws Exception  {
		targetVirtualResourceToDelete.delete(IVirtualResource.FORCE, null);
		
		assertTrue("The real resource should be deleted when IVirtualResource.IGNORE_UNDERLYING_RESOURCE is NOT supplied.", !targetPlatformResourceToDelete.exists()); //$NON-NLS-1$
				
		IVirtualResource[] members = virtualParent.members();
		
		for (int i = 0; i < members.length; i++) {
			if(members[i].getRuntimePath().equals(targetVirtualResourceToDelete.getRuntimePath())) {
				fail("Found deleted folder in members()"); //$NON-NLS-1$
			}
		}	
	}	
	 

	public void testGetUnderlyingResource() {
		IResource platformResourceToCreate = targetVirtualResourceToCreate.getUnderlyingResource();
		int expectedType = determineExpectedType(targetVirtualResourceToCreate);
		assertEquals("The type of the underlying resource should match the expected type.", expectedType, platformResourceToCreate.getType());
		assertTrue("The underyling resource should not exist.", !platformResourceToCreate.exists() );  
		
		expectedType = determineExpectedType(targetExistingVirtualResource);
		IResource exitingPlatformResource = targetExistingVirtualResource.getUnderlyingResource();
		assertEquals("The type of the underlying resource should match the expected type.", expectedType, exitingPlatformResource.getType());
		assertTrue("The underyling resource should exist.", exitingPlatformResource.exists() );
		
		
		expectedType = determineExpectedType(targetMultiVirtualResource);
		IResource multiPlatformResource = targetMultiVirtualResource.getUnderlyingResource();
		assertEquals("The type of the underlying resource should match the expected type.", expectedType, multiPlatformResource.getType());
		assertTrue("The underyling resource should exist.", multiPlatformResource.exists() ); 
		
	}

	public void testGetUnderlyingResources() { 

		int expectedType = determineExpectedType(targetVirtualResourceToCreate);
		IResource[] platformResourcesToCreate = targetVirtualResourceToCreate.getUnderlyingResources();
		assertEquals("There should only be one resource in the result array.", 1, platformResourcesToCreate.length);
		assertEquals("The type of the underlying resource should match the expected type.", expectedType, platformResourcesToCreate[0].getType());
		assertTrue("The underyling resource should not exist.", !platformResourcesToCreate[0].exists() ); 

		expectedType = determineExpectedType(targetExistingVirtualResource);
		IResource[] existingPlatformResource = targetExistingVirtualResource.getUnderlyingResources();
		assertEquals("There should only be one resource in the result array.", 1, existingPlatformResource.length);
		assertEquals("The type of the underlying resource should match the expected type.", expectedType, existingPlatformResource[0].getType());
		assertTrue("The underyling resource should not exist.", existingPlatformResource[0].exists() );  

		
		expectedType = determineExpectedType(targetMultiVirtualResource);
		IResource[] multiPlatformResources = targetMultiVirtualResource.getUnderlyingResources();
		assertEquals("The type of the underlying resource should match the expected type.", expectedType, multiPlatformResources[0].getType());
		assertTrue("The underyling resource should not exist.", multiPlatformResources[0].exists() ); 
	}

	private int determineExpectedType(IVirtualResource aVirtualResource) { 
		switch(targetVirtualResourceToCreate.getType()) {
			case IVirtualResource.FILE:
				return IResource.FILE; 
			case IVirtualResource.FOLDER:
			case IVirtualResource.COMPONENT:
				return IResource.FOLDER; 
		}
		return 0;
	}

}
