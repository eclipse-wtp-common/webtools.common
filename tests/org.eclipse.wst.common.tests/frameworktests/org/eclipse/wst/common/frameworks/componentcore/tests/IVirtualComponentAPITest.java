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
package org.eclipse.wst.common.frameworks.componentcore.tests;

import java.util.Properties;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;

public class IVirtualComponentAPITest extends BaseVirtualTest {

	public IVirtualComponentAPITest(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public void testGetName() {
		
		String name = component.getName();
	}

	public void testGetComponentTypeId() {
		String id = component.getComponentTypeId() ;
	}

	public void testSetComponentTypeId() {
		String id = "jst.ejb";
		component.setComponentTypeId(id) ;
	}

	public void testGetMetaProperties() {
		Properties properties = component.getMetaProperties() ;
	}

	public void testGetMetaResources() {
		IPath[] metaresources = component.getMetaResources() ;

	}

	public void testSetMetaResources() {
		
		IPath[] metaresources = new IPath[1];
		metaresources[0] = new Path("/test");
		component.setMetaResources(metaresources) ;

	}
	
	public void testGetResources() {
		String resource = "/test";
		IVirtualResource[] virtualResource = component.getResources(resource) ;

	}

}
