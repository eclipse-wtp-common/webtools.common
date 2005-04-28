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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.UnresolveableURIException;
import org.eclipse.wst.common.componentcore.internal.ComponentResource;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;

public class StructureEditTest extends TestCase {

	private IResource aResource;

	public StructureEditTest(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		TestWorkspace.init();
		aResource= TestWorkspace.TEST_PROJECT.getFile(new Path("WebModule1/testdata/TestFile1.txt"));
	}
	
	public void testFindBySourcePath() { 
		IProject proj = aResource.getProject();
		StructureEdit se = null;
		List foundResources = new ArrayList();
		try {
			se = StructureEdit.getStructureEditForRead(proj);
			ComponentResource[] resources = se.findResourcesBySourcePath(aResource.getProjectRelativePath());
			assertEquals("There should be one resource found.", 1, resources.length);

			resources = se.findResourcesBySourcePath(aResource.getFullPath());
			assertEquals("There should be one resource found.", 1, resources.length);
		}
		catch (UnresolveableURIException e) {
			e.printStackTrace();
		}
		 finally {
			se.dispose();	
		} 
	}

}
