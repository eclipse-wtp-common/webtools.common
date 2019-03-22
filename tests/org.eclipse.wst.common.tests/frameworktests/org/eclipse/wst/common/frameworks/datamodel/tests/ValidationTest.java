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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;
import org.eclipse.wst.common.tests.CommonTestsPlugin;

public class ValidationTest extends TestCase {

	private static final String A = "A";
	private static final String B = "B";
	private static final String C = "C";

	private static final String[] allProperties = new String[]{A, B, C};


	private class P extends AbstractDataModelProvider {


		public Set getPropertyNames(){
			Set propertyNames = super.getPropertyNames();
			propertyNames.add(A);
			propertyNames.add(B);
			propertyNames.add(C);
			return propertyNames;
		}

		public IStatus validate(String propertyName) {
			validationList.add(propertyName);
			return status;
		}
	}

	private IStatus errorStatus = new Status(IStatus.ERROR, CommonTestsPlugin.PLUGIN_ID, 0, "error", null);
	private IStatus okStatus = IDataModelProvider.OK_STATUS;

	private IStatus status;

	private List validationList;

	private IDataModel dm;

	protected void setUp() throws Exception {
		super.setUp();
		dm = DataModelFactory.createDataModel(new P());
		status = okStatus;
		validationList = new ArrayList();
	}

	public void _testBasicValidation() {
		for (int i = 0; i < 2; i++) {
			boolean ok = i == 0;
			status = ok ? okStatus : errorStatus;

			validationList.clear();
			assertTrue(dm.isValid() == ok);
			assertEquals(ok ? 5 : 1, validationList.size());
			assertTrue(validationList.contains(A) || validationList.contains(IDataModelProperties.ALLOW_EXTENSIONS) || validationList.contains(IDataModelProperties.RESTRICT_EXTENSIONS));
			if (ok) {
				assertTrue(validationList.contains(B));
				assertTrue(validationList.contains(C));
			}
			validationList.clear();

			assertTrue(dm.validate().isOK() == ok);
			// TODO
			//assertEquals(ok ? 3 : 1, validationList.size());
			assertTrue(validationList.contains(A) || validationList.contains(IDataModelProperties.ALLOW_EXTENSIONS) || validationList.contains(IDataModelProperties.RESTRICT_EXTENSIONS));
			if (ok) {
				assertTrue(validationList.contains(B));
				assertTrue(validationList.contains(C));
			}
			validationList.clear();

			assertTrue(dm.validate(true).isOK() == ok);
			assertEquals(ok ? 5 : 1, validationList.size());
			validationList.clear();

			assertTrue(dm.validate(false).isOK() == ok);
			assertEquals(5, validationList.size());
			assertTrue(validationList.contains(A));
			assertTrue(validationList.contains(B));
			assertTrue(validationList.contains(C));
			validationList.clear();

			for (int j = 0; j < allProperties.length; j++) {
				assertTrue(dm.isPropertyValid(allProperties[j]) == ok);
				assertEquals(1, validationList.size());
				assertTrue(validationList.contains(allProperties[j]));
				validationList.clear();
				assertTrue(dm.validateProperty(allProperties[j]).isOK() == ok);
				assertEquals(1, validationList.size());
				assertTrue(validationList.contains(allProperties[j]));
				validationList.clear();
			}
		}
	}

	public void testNestedValidation() {
		dm.addNestedModel("a", DataModelFactory.createDataModel(new A()));
		dm.addNestedModel("b", DataModelFactory.createDataModel(new B()));
		dm.addNestedModel("c", DataModelFactory.createDataModel(new C()));

		for (int i = 0; i < 4; i++) {
			validationList.clear();
			switch (i) {
				case 0 :
					assertTrue(dm.isValid());
					break;
				case 1 :
					assertTrue(dm.validate().isOK());
					break;
				case 2 :
					assertTrue(dm.validate(true).isOK());
					break;
				case 3 :
					assertTrue(dm.validate(false).isOK());
					break;
			}
			assertEquals(8, validationList.size());
			assertTrue(validationList.contains(A));
			assertTrue(validationList.contains(B));
			assertTrue(validationList.contains(C));
			assertTrue(validationList.contains("a"));
			assertTrue(validationList.contains("b"));
			assertTrue(validationList.contains("c"));
			validationList.clear();
		}
		status = errorStatus;
		for (int i = 0; i < 3; i++) {
			validationList.clear();
			switch (i) {
				case 0 :
					assertTrue(!dm.isValid());
					break;
				case 1 :
					assertTrue(!dm.validate().isOK());
					break;
				case 2 :
					assertTrue(!dm.validate(true).isOK());
					break;
			}
			// TODO
			//assertEquals(1, validationList.size());
			//assertTrue(!validationList.contains("a"));
			//assertTrue(!validationList.contains("b"));
			//assertTrue(!validationList.contains("c"));
			validationList.clear();
		}

		assertTrue(!dm.validate(false).isOK());
		assertEquals(8, validationList.size());
		assertTrue(validationList.contains(A));
		assertTrue(validationList.contains(B));
		assertTrue(validationList.contains(C));
		assertTrue(validationList.contains("a"));
		assertTrue(validationList.contains("b"));
		assertTrue(validationList.contains("c"));
		validationList.clear();
	}

}
