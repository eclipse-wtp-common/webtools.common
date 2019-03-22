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

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {

	
	public static TestSuite suite() {
		
		TestSuite suite = new TestSuite(); 
		
		suite.addTestSuite(IVirtualFolderAPITest.class);
		suite.addTestSuite(ModuleCoreAPIFVTTest.class);
		suite.addTestSuite(ModuleCoreURIConverterUnitTest.class);
		//suite.addTestSuite(StructureEditAPITest.class);
		suite.addTestSuite(StructureEditStressTest.class);
		
		
		return suite;
	}
}
