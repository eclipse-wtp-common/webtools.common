/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.datamodel.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.wst.common.tests.SimpleTestSuite;

/**
 * @author jsholl
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Style - Code Templates
 */
public class DataModelAPITests extends TestSuite {

	public static Test suite() {
		return new DataModelAPITests();
	}

	public DataModelAPITests() {
		super();
		addTest(new SimpleTestSuite(EventTest.class));
		addTest(new SimpleTestSuite(NestingTest.class));
		addTest(new SimpleTestSuite(NestedListeningTest.class));
		addTest(new SimpleTestSuite(SimpleDataModelTest.class));
		addTest(new SimpleTestSuite(DataModelFactoryTest.class));
        addTest(new SimpleTestSuite(DataModelEnablementTest.class));
	}
}
