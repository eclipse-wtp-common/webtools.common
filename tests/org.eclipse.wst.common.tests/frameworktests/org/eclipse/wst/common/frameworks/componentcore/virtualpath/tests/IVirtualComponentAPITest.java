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
package org.eclipse.wst.common.frameworks.componentcore.virtualpath.tests;

import java.util.List;
import java.util.Properties;

import junit.framework.TestSuite;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.UnresolveableURIException;
import org.eclipse.wst.common.componentcore.internal.ReferencedComponent;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.internal.impl.ModuleURIUtil;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;

public class IVirtualComponentAPITest extends IVirtualContainerAPITest {

	private IVirtualComponent virtualComponent;
	private WorkbenchComponent workbenchComponent;
	private StructureEdit structureEdit;
	
	public IVirtualComponentAPITest(String name) {
		super(name); 
	}
	
	public static TestSuite suite() {
		TestSuite suite = new TestSuite();
		suite.addTest(new IVirtualComponentAPITest("testGetReferences"));
		return suite;
	}
	
	protected void doSetup() throws Exception { 
		virtualComponent = ComponentCore.createComponent(TestWorkspace.TEST_PROJECT, TestWorkspace.WEB_MODULE_1_NAME);
		structureEdit = StructureEdit.getStructureEditForRead(TestWorkspace.TEST_PROJECT);
		workbenchComponent = structureEdit.getComponent();
	}
	
	protected void tearDown() throws Exception { 
		super.tearDown();
		if(structureEdit != null)
			structureEdit.dispose();
	}

	public void testGetName() {
		
		String name = virtualComponent.getName();
	}

	public void testGetMetaProperties() {
		Properties properties = virtualComponent.getMetaProperties() ;
	}

	public void testGetMetaResources() {
		IPath[] metaresources = virtualComponent.getMetaResources() ;

	}

	public void testSetMetaResources() {
		
		IPath[] metaresources = new IPath[1];
		metaresources[0] = new Path("/test");
		virtualComponent.setMetaResources(metaresources) ;

	}
	
	public void testGetResources() {
		String resource = "/test";
		IVirtualFolder rootFolder = virtualComponent.getRootFolder();
		IVirtualResource[] virtualResource = rootFolder.getResources(resource) ;

	}
	
	public void testGetReferences() {
		IVirtualReference[] references = virtualComponent.getReferences();
		
		for(int i=0; i<references.length;i++)
			assertReference(references[i]);
	}
	
	private void assertReference(IVirtualReference reference) { 
		List referencedComponents = workbenchComponent.getReferencedComponents();
		ReferencedComponent referencedComponent = null;
		String componentName = null;
		for(int i=0; i<referencedComponents.size(); i++) {
			referencedComponent = (ReferencedComponent) referencedComponents.get(i);
			try {
				componentName = ModuleURIUtil.getDeployedName(referencedComponent.getHandle());
			} catch (UnresolveableURIException e) {  
			}
			if(componentName != null && componentName.equals(reference.getReferencedComponent().getName())) {					
				assertEquals("The runtime paths must match.", referencedComponent.getRuntimePath(), reference.getRuntimePath());
				assertEquals("The workbench component should match the enclosing component.", virtualComponent, reference.getEnclosingComponent());
				assertEquals("The dependencyTypes should match.", referencedComponent.getDependencyType().getValue(), reference.getDependencyType());
				URI actualHandle = ModuleURIUtil.fullyQualifyURI(reference.getReferencedComponent().getProject());
				assertEquals("The handles should match.", referencedComponent.getHandle(), actualHandle); 
				return;
			}
		}
		fail("A matching reference was not found for "+reference.getRuntimePath()); 
	}

	public void testSetReferences() {
		
	}

}
