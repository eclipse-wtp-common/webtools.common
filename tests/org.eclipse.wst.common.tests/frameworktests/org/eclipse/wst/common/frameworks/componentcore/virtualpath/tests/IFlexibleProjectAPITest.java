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

import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.resources.FlexibleProject;
import org.eclipse.wst.common.componentcore.resources.IFlexibleProject;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

public class IFlexibleProjectAPITest extends TestCase {
	
	private IFlexibleProject flexibleProject;
	
	public IFlexibleProjectAPITest (String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();		
		
		TestWorkspace.init();
		flexibleProject = ComponentCore.createFlexibleProject(TestWorkspace.getTargetProject());
	}

	public void testFlexibleProjectIProject() {
		
		IFlexibleProject localFlexiProject = new FlexibleProject(TestWorkspace.getTargetProject());
		// should be created without exception 
		
	}

	public void testGetComponents() {
		
		IVirtualComponent[] components = flexibleProject.getComponents();
		assertEquals("Verify the number of modules defined in the test project.", TestWorkspace.MODULE_NAMES.length, components.length);
		
		boolean found = false;
		for (int componentIndex = 0; componentIndex < components.length; componentIndex++) {
			found = false;
			for (int moduleNamesIndex = 0; moduleNamesIndex < TestWorkspace.MODULE_NAMES.length; moduleNamesIndex++) {
				if(TestWorkspace.MODULE_NAMES[moduleNamesIndex].equals(components[componentIndex].getName())) { 
					found = true;
					break;
				} 
			}
			assertTrue("A component with the following name must be found in the project: " + components[componentIndex].getName(), found);
		}
		
	}

	public void testGetComponent() {
		IVirtualComponent component = flexibleProject.getComponent(TestWorkspace.WEB_MODULE_1_NAME);
		assertEquals("The component must match the expected.", TestWorkspace.WEB_MODULE_1_NAME, component.getName());
	}

	public void testGetProject() {
		
		assertEquals("The project associated with the flexible project must match the expected.", TestWorkspace.getTargetProject(), flexibleProject.getProject());
	}

}
