/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.validation.tests.testcase;

import java.io.UnsupportedEncodingException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.ValidationResults;
import org.eclipse.wst.validation.internal.Tracing;

/** Tests for derived resources. */
public class TestSuite9 extends TestCase {
	
	private TestEnvironment _env;
	private IProject		_project;
	
	public static Test suite() {
		return new TestSuite(TestSuite9.class);
	} 
	
	public TestSuite9(String name){
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		_env = new TestEnvironment();
		_project = _env.createProject("TestSuite9");
		
		IPath first = _env.addFolder(_project.getFullPath(), "first");
		IFile file = _env.addFile(first, "test.xml", 
			"<?xml version='1.0' encoding='UTF-8'?>" +
			"<root>");
		file.setDerived(true);
		
		TestEnvironment.enableOnlyThisValidator("org.eclipse.wst.xml.core.internal.validation.eclipse.Validator");
	}
	
	protected void tearDown() throws Exception {
		_project.delete(true, null);
		_env.dispose();
		super.tearDown();
	}
	
	/**
	 * Ensure that the framework ignores derived resources.
	 */
	public void testIgnoresDerivedResources() throws CoreException, UnsupportedEncodingException, InterruptedException {
		Tracing.log("TestSuite9-01: testIgnoresDerivedResources starting");
		IProgressMonitor monitor = new NullProgressMonitor();		
		ValidationFramework vf = ValidationFramework.getDefault();
		IProject[] projects = new IProject[1];
		projects[0] = _project;
		ValidationResults vr = vf.validate(projects, true, false, monitor);
		int errors = vr.getSeverityError();
		assertEquals("Number of errors", 0, errors);
				
		Tracing.log("TestSuite9-02: testIgnoresDerivedResources finished");
	}
}