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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.componentcore.internal.impl.ComponentCoreURIConverter;
import org.eclipse.wst.common.frameworks.componentcore.virtualpath.tests.TestWorkspace;

public class ModuleCoreURIConverterUnitTest  extends TestCase {

	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(ModuleCoreURIConverterUnitTest.class);
		return suite;
	}

	public ModuleCoreURIConverterUnitTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();  
		TestWorkspace.init();
	}
	
	public void testNormalizeDDURI() throws Exception { 
		
		ComponentCoreURIConverter converter = new ComponentCoreURIConverter(TestWorkspace.getTargetProject());
		
		URI inputURI = URI.createURI("module:/resource/TestVirtualAPI/WebModule2/WEB-INF/web.xml"); //$NON-NLS-1$
		
		URI resultURI = converter.normalize(inputURI);
		
		URI expectedURI = URI.createURI("platform:/resource/TestVirtualAPI/WebModule2/WebContent/WEB-INF/web.xml"); //$NON-NLS-1$
		// TODO
		//assertEquals("The resultant URI must match the expected URI", expectedURI, resultURI); //$NON-NLS-1$
	}
}
