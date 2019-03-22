/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.datamodel.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.wst.common.frameworks.datamodel.tests.extended.ExtendedOperationTests;
import org.eclipse.wst.common.tests.SimpleTestSuite;

/**
 * @author jsholl
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
		addTest(new SimpleTestSuite(TestAbstractDMProvider.class));
		addTest(new SimpleTestSuite(ValidationTest.class));
		addTest(new SimpleTestSuite(ExtendedOperationTests.class));
	}
}
