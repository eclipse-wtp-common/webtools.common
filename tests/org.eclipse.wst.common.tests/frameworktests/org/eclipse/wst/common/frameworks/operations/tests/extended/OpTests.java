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
package org.eclipse.wst.common.frameworks.operations.tests.extended;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel;

public class OpTests extends TestCase {

	public static List executionList = new ArrayList();

	public static final String a = A.class.getName();
	public static final String b = B.class.getName();
	public static final String c = C.class.getName();
	public static final String d = D.class.getName();
	public static final String e = E.class.getName();
	public static final String f = F.class.getName();
	public static final String r = R.class.getName();


	private String[] expectedExecution;

	protected void setUp() throws Exception {
		super.setUp();
		executionList.clear();
		expectedExecution = null;
		IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject("foo");
		if(!p.exists()){
			p.create(null);
		}
	}

	public void testAllOn() throws Exception {
		RootDM dm = new RootDM();
		dm.getDefaultOperation().run(null);
		expectedExecution = new String[]{c, a, d, r, e, b, f};
		checkResults();
	}

	public void testAllOff() throws Exception {
		RootDM dm = new RootDM();
		dm.setBooleanProperty(WTPOperationDataModel.ALLOW_EXTENSIONS, false);
		dm.getDefaultOperation().run(null);
		expectedExecution = new String[]{r};
		checkResults();
	}

	public void testAOff() throws Exception {
		RootDM dm = new RootDM();
		List restrictedList = new ArrayList();
		restrictedList.add(a);
		dm.setProperty(WTPOperationDataModel.RESTRICT_EXTENSIONS, restrictedList);
		dm.getDefaultOperation().run(null);
		expectedExecution = new String[]{r, e, b, f};
		checkResults();
	}

	public void testBOff() throws Exception {
		RootDM dm = new RootDM();
		List restrictedList = new ArrayList();
		restrictedList.add(b);
		dm.setProperty(WTPOperationDataModel.RESTRICT_EXTENSIONS, restrictedList);
		dm.getDefaultOperation().run(null);
		expectedExecution = new String[]{c, a, d, r};
		checkResults();
	}

	public void testCOff() throws Exception {
		RootDM dm = new RootDM();
		List restrictedList = new ArrayList();
		restrictedList.add(c);
		dm.setProperty(WTPOperationDataModel.RESTRICT_EXTENSIONS, restrictedList);
		dm.getDefaultOperation().run(null);
		expectedExecution = new String[]{a, d, r, e, b, f};
		checkResults();
	}

	public void testCFOff() throws Exception {
		RootDM dm = new RootDM();
		List restrictedList = new ArrayList();
		restrictedList.add(c);
		restrictedList.add(f);
		dm.setProperty(WTPOperationDataModel.RESTRICT_EXTENSIONS, restrictedList);
		dm.getDefaultOperation().run(null);
		expectedExecution = new String[]{a, d, r, e, b};
		checkResults();
	}

	public void testCBOff() throws Exception {
		RootDM dm = new RootDM();
		List restrictedList = new ArrayList();
		restrictedList.add(c);
		restrictedList.add(b);
		dm.setProperty(WTPOperationDataModel.RESTRICT_EXTENSIONS, restrictedList);
		dm.getDefaultOperation().run(null);
		expectedExecution = new String[]{a, d, r};
		checkResults();
	}

	public void testAEFOff() throws Exception {
		RootDM dm = new RootDM();
		List restrictedList = new ArrayList();
		restrictedList.add(a);
		restrictedList.add(e);
		restrictedList.add(f);
		dm.setProperty(WTPOperationDataModel.RESTRICT_EXTENSIONS, restrictedList);
		dm.getDefaultOperation().run(null);
		expectedExecution = new String[]{r, b};
		checkResults();
	}

	public void testABOff() throws Exception {
		RootDM dm = new RootDM();
		List restrictedList = new ArrayList();
		restrictedList.add(a);
		restrictedList.add(b);
		dm.setProperty(WTPOperationDataModel.RESTRICT_EXTENSIONS, restrictedList);
		dm.getDefaultOperation().run(null);
		expectedExecution = new String[]{r};
		checkResults();
	}


	private void checkResults() {
		assertEquals(expectedExecution.length, executionList.size());
		for (int i = 0; i < expectedExecution.length; i++) {
			assertEquals(expectedExecution[i], (String) executionList.get(i));
		}

	}

}
