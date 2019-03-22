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
package org.eclipse.wst.common.frameworks.datamodel.tests.extended;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProperties;
import org.eclipse.wst.common.frameworks.internal.datamodel.IDataModelPausibleOperation;
import org.eclipse.wst.common.frameworks.internal.datamodel.IDataModelPausibleOperationEvent;
import org.eclipse.wst.common.frameworks.internal.datamodel.IDataModelPausibleOperationListener;

public class ExtendedOperationTests extends TestCase {

	public static List executionList = new ArrayList();

	public static final String a = A.class.getName();
	public static final String b = B.class.getName();
	public static final String c = C.class.getName();
	public static final String d = D.class.getName();
	public static final String e = E.class.getName();
	public static final String f = F.class.getName();
	public static final String g = G.class.getName();
	public static final String h = H.class.getName();
	public static final String r = R.class.getName();

	protected class PauseListener implements IDataModelPausibleOperationListener {

		public List pausedOperations = new ArrayList();

		public int notify(IDataModelPausibleOperationEvent event) {
			String opID = event.getOperation().getID();
			if (pausedOperations.contains(opID)) {
				return CONTINUE;
			}
			pausedOperations.add(opID);
			return PAUSE;
		}
	}

	protected PauseListener pauseListener = new PauseListener();

	protected void setUp() throws Exception {
		super.setUp();
		executionList.clear();
		pauseListener.pausedOperations.clear();
		IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject("foo"); //$NON-NLS-1$
		if (!p.exists()) {
			p.create(null);
		}
	}

	public void testAllOn() throws Exception {
		IDataModel dm = DataModelFactory.createDataModel(new RootDMProvider());
		String[] forwardExpectedResults = new String[]{c, a, d, r, e, b, f, g, h};
		String[] forwardExpectedPauseOrder = new String[]{r, a, c, d, b, e, f, h, g};
		checkAll(dm, forwardExpectedResults, forwardExpectedPauseOrder);
	}

	public void testAllOff() throws Exception {
		IDataModel dm = DataModelFactory.createDataModel(new RootDMProvider());
		dm.setBooleanProperty(IDataModelProperties.ALLOW_EXTENSIONS, false);
		String[] forwardExpectedResults = new String[]{r};
		String[] forwardExpectedPauseOrder = new String[]{r};
		checkAll(dm, forwardExpectedResults, forwardExpectedPauseOrder);
	}

	public void testAOff() throws Exception {
		IDataModel dm = DataModelFactory.createDataModel(new RootDMProvider());
		List restrictedList = new ArrayList();
		restrictedList.add(a);
		dm.setProperty(IDataModelProperties.RESTRICT_EXTENSIONS, restrictedList);
		String[] forwardExpectedResults = new String[]{r, e, b, f, g, h};
		String[] forwardExpectedPauseOrder = new String[]{r, b, e, f, h, g};
		checkAll(dm, forwardExpectedResults, forwardExpectedPauseOrder);
	}

	public void testBOff() throws Exception {
		IDataModel dm = DataModelFactory.createDataModel(new RootDMProvider());
		List restrictedList = new ArrayList();
		restrictedList.add(b);
		dm.setProperty(IDataModelProperties.RESTRICT_EXTENSIONS, restrictedList);
		String[] forwardExpectedResults = new String[]{c, a, d, r};
		String[] forwardExpectedPauseOrder = new String[]{r, a, c, d};
		checkAll(dm, forwardExpectedResults, forwardExpectedPauseOrder);
	}

	public void testCOff() throws Exception {
		IDataModel dm = DataModelFactory.createDataModel(new RootDMProvider());
		List restrictedList = new ArrayList();
		restrictedList.add(c);
		dm.setProperty(IDataModelProperties.RESTRICT_EXTENSIONS, restrictedList);
		String[] forwardExpectedResults = new String[]{a, d, r, e, b, f, g, h};
		String[] forwardExpectedPauseOrder = new String[]{r, a, d, b, e, f, h, g};
		checkAll(dm, forwardExpectedResults, forwardExpectedPauseOrder);
	}

	public void testCFOff() throws Exception {
		IDataModel dm = DataModelFactory.createDataModel(new RootDMProvider());
		List restrictedList = new ArrayList();
		restrictedList.add(c);
		restrictedList.add(f);
		dm.setProperty(IDataModelProperties.RESTRICT_EXTENSIONS, restrictedList);
		String[] forwardExpectedResults = new String[]{a, d, r, e, b};
		String[] forwardExpectedPauseOrder = new String[]{r, a, d, b, e};
		checkAll(dm, forwardExpectedResults, forwardExpectedPauseOrder);
	}

	public void testCBOff() throws Exception {
		IDataModel dm = DataModelFactory.createDataModel(new RootDMProvider());
		List restrictedList = new ArrayList();
		restrictedList.add(c);
		restrictedList.add(b);
		dm.setProperty(IDataModelProperties.RESTRICT_EXTENSIONS, restrictedList);
		String[] forwardExpectedResults = new String[]{a, d, r};
		String[] forwardExpectedPauseOrder = new String[]{r, a, d};
		checkAll(dm, forwardExpectedResults, forwardExpectedPauseOrder);
	}

	public void testAEFOff() throws Exception {
		IDataModel dm = DataModelFactory.createDataModel(new RootDMProvider());
		List restrictedList = new ArrayList();
		restrictedList.add(a);
		restrictedList.add(e);
		restrictedList.add(f);
		dm.setProperty(IDataModelProperties.RESTRICT_EXTENSIONS, restrictedList);
		String[] forwardExpectedResults = new String[]{r, b};
		String[] forwardExpectedPauseOrder = new String[]{r, b};
		checkAll(dm, forwardExpectedResults, forwardExpectedPauseOrder);
	}

	public void testABOff() throws Exception {
		IDataModel dm = DataModelFactory.createDataModel(new RootDMProvider());
		List restrictedList = new ArrayList();
		restrictedList.add(a);
		restrictedList.add(b);
		dm.setProperty(IDataModelProperties.RESTRICT_EXTENSIONS, restrictedList);
		String[] forwardExpectedResults = new String[]{r};
		String[] forwardExpectedPauseOrder = new String[]{r};
		checkAll(dm, forwardExpectedResults, forwardExpectedPauseOrder);
	}

	private void checkAll(IDataModel dm, String[] forwardExpectedResults, String[] expectedPauseOrder) throws Exception {
		String[] undoExpectedResults = reverseString(forwardExpectedResults);
		boolean shouldRollBack = false;

		// Check regular executions
		IDataModelPausibleOperation op = (IDataModelPausibleOperation)dm.getDefaultOperation();
		checkExecution(op, forwardExpectedResults, null, shouldRollBack);
		checkUndo(op, undoExpectedResults, null);
		checkRedo(op, forwardExpectedResults, null);
		checkUndo(op, undoExpectedResults, null);

		// Check executions with pausing & resuming
		op = (IDataModelPausibleOperation)dm.getDefaultOperation();
		op.addOperationListener(pauseListener);
		checkExecution(op, forwardExpectedResults, expectedPauseOrder, shouldRollBack);
		checkUndo(op, undoExpectedResults, undoExpectedResults);
		checkRedo(op, forwardExpectedResults, forwardExpectedResults);
		checkUndo(op, undoExpectedResults, undoExpectedResults);

		// Check executions with pausing, resuming & rolling back.
		op = (IDataModelPausibleOperation)dm.getDefaultOperation();
		op.addOperationListener(pauseListener);
		shouldRollBack = true;
		checkExecution(op, forwardExpectedResults, expectedPauseOrder, shouldRollBack);
		checkUndo(op, undoExpectedResults, undoExpectedResults);
		checkRedo(op, forwardExpectedResults, forwardExpectedResults);
		checkUndo(op, undoExpectedResults, undoExpectedResults);
	}

	private String[] reverseString(String[] forwardString) {
		String[] reverseString = new String[forwardString.length];
		for (int i = 0; i < reverseString.length; i++) {
			reverseString[i] = forwardString[forwardString.length - i - 1];
		}
		return reverseString;
	}

	private void checkExecution(IDataModelPausibleOperation op, String[] expectedResults, String[] expectedPauseOrder, boolean rollback) throws Exception {
		assertEquals(IDataModelPausibleOperation.NOT_STARTED, op.getExecutionState());
		op.execute(null, null);
		if (null != expectedPauseOrder) {
			if (rollback) {
				List cachedExecutionList = new ArrayList();
				for (int i = 0; i < expectedResults.length; i++) {
					cachedExecutionList.clear();
					cachedExecutionList.addAll(executionList);
					assertEquals(IDataModelPausibleOperation.PAUSED_EXECUTE, op.getExecutionState());
					op.rollBack(null, null);
					assertEquals(IDataModelPausibleOperation.COMPLETE_ROLLBACK, op.getExecutionState());
					checkRollBackResults(cachedExecutionList, expectedResults);
					op.resume(null, null);
				}
				assertEquals(IDataModelPausibleOperation.COMPLETE_EXECUTE, op.getExecutionState());
				checkResults(expectedResults, expectedPauseOrder);	
				
				pauseListener.pausedOperations.clear();
				executionList.clear();
				op.execute(null, null);
			}
			for (int i = 0; i < expectedResults.length; i++) {
				assertEquals(IDataModelPausibleOperation.PAUSED_EXECUTE, op.getExecutionState());
				op.resume(null, null);
			}
		}
		assertEquals(IDataModelPausibleOperation.COMPLETE_EXECUTE, op.getExecutionState());
		checkResults(expectedResults, expectedPauseOrder);
		executionList.clear();
		pauseListener.pausedOperations.clear();
	}

	private void checkUndo(IDataModelPausibleOperation op, String[] expectedResults, String[] expectedPauseOrder) throws Exception {
		if (op.getExecutionState() != IDataModelPausibleOperation.COMPLETE_EXECUTE && op.getExecutionState() != IDataModelPausibleOperation.COMPLETE_REDO) {
			fail("Operation execution state invalid " + op.getExecutionState()); //$NON-NLS-1$
		}
		op.undo(null, null);
		if (null != expectedPauseOrder) {
			for (int i = 0; i < expectedResults.length; i++) {
				assertEquals(IDataModelPausibleOperation.PAUSED_UNDO, op.getExecutionState());
				op.resume(null, null);
			}
		}
		assertEquals(IDataModelPausibleOperation.COMPLETE_UNDO, op.getExecutionState());
		checkResults(expectedResults, expectedPauseOrder);
		executionList.clear();
		pauseListener.pausedOperations.clear();
	}

	private void checkRedo(IDataModelPausibleOperation op, String[] expectedResults, String[] expectedPauseOrder) throws Exception {
		assertEquals(IDataModelPausibleOperation.COMPLETE_UNDO, op.getExecutionState());
		op.redo(null, null);
		if (null != expectedPauseOrder) {
			for (int i = 0; i < expectedResults.length; i++) {
				assertEquals(IDataModelPausibleOperation.PAUSED_REDO, op.getExecutionState());
				op.resume(null, null);
			}
		}
		assertEquals(IDataModelPausibleOperation.COMPLETE_REDO, op.getExecutionState());
		checkResults(expectedResults, expectedPauseOrder);
		executionList.clear();
		pauseListener.pausedOperations.clear();
	}

	private void checkRollBackResults(List cachedExecutionList, String [] expectedResults) {
		assertEquals(cachedExecutionList.size() * 2, executionList.size());
		for (int i = 0; i < cachedExecutionList.size(); i++) {
			assertEquals(expectedResults[i], (String)executionList.get(i));
			assertEquals((String)cachedExecutionList.get(i), (String) executionList.get(executionList.size() - 1 - i));
		}
		executionList.clear();
	}

	private void checkResults(String[] expectedResults, String[] expectedPauseOrder) {
		assertEquals(expectedResults.length, executionList.size());
		for (int i = 0; i < expectedResults.length; i++) {
			assertEquals(expectedResults[i], (String) executionList.get(i));
		}
		if (null == expectedPauseOrder) {
			assertEquals(0, pauseListener.pausedOperations.size());
		} else {
			assertEquals(expectedPauseOrder.length, pauseListener.pausedOperations.size());
			for (int i = 0; i < expectedPauseOrder.length; i++) {
				assertEquals(expectedPauseOrder[i], (String) pauseListener.pausedOperations.get(i));
			}
		}
	}

}
