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

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {

	
	public static TestSuite suite() {
		
		TestWorkspace.init();
		
		TestSuite suite = new TestSuite(); 
		
		suite.addTestSuite(IVirtualFolderAPITest.class);
		suite.addTestSuite(ModuleCoreAPITest.class);
		suite.addTestSuite(ModuleCoreURIConverterUnitTest.class);
		
		return suite;
	}
}
