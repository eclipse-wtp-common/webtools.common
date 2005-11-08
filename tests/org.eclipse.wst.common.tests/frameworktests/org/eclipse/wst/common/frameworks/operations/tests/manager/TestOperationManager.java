/***************************************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.frameworks.operations.tests.manager;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.environment.EnvironmentService;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.internal.DataModelManager;
import org.eclipse.wst.common.frameworks.internal.OperationListener;
import org.eclipse.wst.common.frameworks.internal.OperationManager;
import org.eclipse.wst.common.frameworks.internal.datamodel.DataModelImpl;


public class TestOperationManager extends TestCase {
	private Vector executedOps;
	private Vector executedUndoOps;
	private Vector expectedOps;
	private Vector expectedUndoOps;
	private OperationManager manager;
	private BaseOperation opA;
	private BaseOperation opB;
	private BaseOperation opC;
	private BaseOperation opD;
	private BaseOperation opE;
	private BaseOperation opF;
	private BaseOperation opG;
	private Status error = new Status(IStatus.ERROR, "id", 0, "mess", null);
  
  public static Test suite()
  {
    return new TestSuite( TestOperationManager.class );
  }

	protected void setUp() throws Exception {
		super.setUp();

		DataModelManager dataModelManager = new DataModelManager(new DataModelImpl(new DataModelProvider()));

		executedOps = new Vector();
		executedUndoOps = new Vector();
		expectedOps = new Vector();
		expectedUndoOps = new Vector();

		opA = new BaseOperation("A", executedOps, executedUndoOps);
		opB = new BaseOperation("B", executedOps, executedUndoOps);
		opC = new BaseOperation("C", executedOps, executedUndoOps);
		opD = new BaseOperation("D", executedOps, executedUndoOps);
		opE = new BaseOperation("E", executedOps, executedUndoOps);
		opF = new BaseOperation("F", executedOps, executedUndoOps);
		opG = new BaseOperation("G", executedOps, executedUndoOps);

		// Operations are organized as follows:
		//
		//     D
		//    / \
		//   B   F
		//  / \ / \
		// A  C E G
		manager = new OperationManager(dataModelManager, opD, EnvironmentService.getEclipseConsoleEnvironment() );
		manager.addExtendedPreOperation(opD.getID(), opB);
		manager.addExtendedPostOperation(opD.getID(), opF);
		manager.addExtendedPreOperation(opB.getID(), opA);
		manager.addExtendedPostOperation(opB.getID(), opC);
		manager.addExtendedPreOperation(opF.getID(), opE);
		manager.addExtendedPostOperation(opF.getID(), opG);
	}

	public void testRunAll() throws Exception {
		expectedOps.add(opA);
		expectedOps.add(opB);
		expectedOps.add(opC);
		expectedOps.add(opD);
		expectedOps.add(opE);
		expectedOps.add(opF);
		expectedOps.add(opG);
		manager.runOperations();
		checkResults();
	}

  public void testDataModelsAreSet() throws Exception {
    expectedOps.add(opA);
    expectedOps.add(opB);
    expectedOps.add(opC);
    expectedOps.add(opD);
    expectedOps.add(opE);
    expectedOps.add(opF);
    expectedOps.add(opG);
    
    opA.setCheckModels( true );
    opG.setCheckModels( true );
    manager.runOperations();
    checkResults();
    
    assertTrue("Expected opA models to be OK", opA.getModelsOK() );
    assertTrue("Expected opG models to be OK", opG.getModelsOK() );
      
  }
  
	public void testRunWithErrors() throws Exception {
		BaseOperation[] operations = new BaseOperation[]{opA, opB, opC, opD, opE, opF, opG};

		for (int index = 0; index < operations.length; index++) {
			operations[index].setStatus(error);

			for (int innerIndex = index; innerIndex >= 0; innerIndex--) {
				expectedUndoOps.add(operations[innerIndex]);
			}

			manager.runOperations();
			checkResults();

			operations[index].setStatus(Status.OK_STATUS);
		}
	}

	public void testRunWithPreExecuteStops() throws Exception {
		StopListener stopListener = new StopListener(new String[]{"A", "D", "G"});

		manager.setPreExecuteListener(stopListener);

		manager.runOperations();
		checkResults();

		expectedOps.add(opA);
		expectedOps.add(opB);
		expectedOps.add(opC);
		manager.runOperations();
		checkResults();

		expectedOps.add(opD);
		expectedOps.add(opE);
		expectedOps.add(opF);
		manager.runOperations();
		checkResults();

		expectedOps.add(opG);
		manager.runOperations();
		checkResults();

		manager.runOperations();
		checkResults();
	}

	public void testRunWithPostExecuteStops() throws Exception {
		StopListener stopListener = new StopListener(new String[]{"A", "D", "G"});

		manager.setPostExecuteListener(stopListener);

		expectedOps.add(opA);
		manager.runOperations();
		checkResults();

		expectedOps.add(opB);
		expectedOps.add(opC);
		expectedOps.add(opD);
		manager.runOperations();
		checkResults();

		expectedOps.add(opE);
		expectedOps.add(opF);
		expectedOps.add(opG);
		manager.runOperations();
		checkResults();

		manager.runOperations();
		checkResults();

		manager.runOperations();
		checkResults();
	}

	public void testRunWithPostExecuteStopsAndErrors1() throws Exception {
		StopListener stopListener = new StopListener(new String[]{"A", "D", "G"});

		manager.setPostExecuteListener(stopListener);

		opA.setStatus(error);
		expectedUndoOps.add(opA);
		manager.runOperations();
		checkResults();
	}

	public void testRunWithPostExecuteStopsAndErrors2() throws Exception {
		StopListener stopListener = new StopListener(new String[]{"A", "D", "G"});

		manager.setPostExecuteListener(stopListener);

		expectedOps.add(opA);
		manager.runOperations();
		checkResults();

		opB.setStatus(error);
		expectedUndoOps.add(opB);
		manager.runOperations();
		checkResults();
	}

	public void testRunWithPostExecuteStopsAndErrors3() throws Exception {
		StopListener stopListener = new StopListener(new String[]{"A", "D", "G"});

		manager.setPostExecuteListener(stopListener);

		expectedOps.add(opA);
		manager.runOperations();
		checkResults();

		opD.setStatus(error);
		expectedUndoOps.add(opD);
		expectedUndoOps.add(opC);
		expectedUndoOps.add(opB);
		manager.runOperations();
		checkResults();
	}

	public void testRunWithPostExecuteStopsAndErrors4() throws Exception {
		StopListener stopListener = new StopListener(new String[]{"A", "D", "G"});

		manager.setPostExecuteListener(stopListener);

		expectedOps.add(opA);
		manager.runOperations();
		checkResults();

		expectedOps.add(opB);
		expectedOps.add(opC);
		expectedOps.add(opD);
		manager.runOperations();
		checkResults();

		opF.setStatus(error);
		expectedUndoOps.add(opF);
		expectedUndoOps.add(opE);
		manager.runOperations();
		checkResults();
	}

	public void testRunWithPostExecuteStopsAndErrors5() throws Exception {
		StopListener stopListener = new StopListener(new String[]{"A", "D", "G"});

		manager.setPostExecuteListener(stopListener);

		expectedOps.add(opA);
		manager.runOperations();
		checkResults();

		expectedOps.add(opB);
		expectedOps.add(opC);
		expectedOps.add(opD);
		manager.runOperations();
		checkResults();

		opG.setStatus(error);
		expectedUndoOps.add(opG);
		expectedUndoOps.add(opF);
		expectedUndoOps.add(opE);
		manager.runOperations();
		checkResults();
	}

	public void testRunWithPostExecuteStopsAndUndo1() throws Exception {
		StopListener stopListener = new StopListener(new String[]{"B", "D", "F"});

		manager.setPostExecuteListener(stopListener);

		expectedOps.add(opA);
		expectedOps.add(opB);
		manager.runOperations();
		checkResults();

		expectedOps.removeAllElements();
		expectedUndoOps.add(opB);
		expectedUndoOps.add(opA);
		manager.undoLastRun();
		checkResults();

		manager.setPostExecuteListener(new StopListener(new String[]{"B", "D", "F"}));
		reset();
		expectedOps.add(opA);
		expectedOps.add(opB);
		manager.runOperations();
		checkResults();

		expectedOps.add(opC);
		expectedOps.add(opD);
		manager.runOperations();
		checkResults();

		expectedOps.add(opE);
		expectedOps.add(opF);
		manager.runOperations();
		checkResults();

		expectedOps.add(opG);
		manager.runOperations();
		checkResults();

		expectedUndoOps.add(opG);
		expectedOps.removeAllElements();
		expectedOps.add(opA);
		expectedOps.add(opB);
		expectedOps.add(opC);
		expectedOps.add(opD);
		expectedOps.add(opE);
		expectedOps.add(opF);
		manager.undoLastRun();
		checkResults();

		expectedUndoOps.add(opF);
		expectedUndoOps.add(opE);
		expectedOps.removeAllElements();
		expectedOps.add(opA);
		expectedOps.add(opB);
		expectedOps.add(opC);
		expectedOps.add(opD);
		manager.undoLastRun();
		checkResults();

		expectedUndoOps.add(opD);
		expectedUndoOps.add(opC);
		expectedOps.removeAllElements();
		expectedOps.add(opA);
		expectedOps.add(opB);
		manager.undoLastRun();
		checkResults();

		expectedUndoOps.add(opB);
		expectedUndoOps.add(opA);
		expectedOps.removeAllElements();
		manager.undoLastRun();
		checkResults();
	}
  
  public void testRunWithChildOperations() throws Exception 
  {
    StopListener stopListener1 = new StopListener(new String[]{"B"});
    StopListener stopListener2 = new StopListener( new String[]{ "APre1Pre1", "APost1", "B" } );
    
    BaseOperation opAPre1 = new BaseOperation("APre1", executedOps, executedUndoOps);
    BaseOperation opAPre2 = new BaseOperation("APre2", executedOps, executedUndoOps);
    BaseOperation opAPost1 = new BaseOperation("APost1", executedOps, executedUndoOps);
    BaseOperation opAPre1Pre1 = new BaseOperation("APre1Pre1", executedOps, executedUndoOps);
    BaseOperation opAPre2Post1 = new BaseOperation("APre2Post2", executedOps, executedUndoOps);
    
    opA.addPreOp( opAPre1 );
    opA.addPreOp( opAPre2 );
    opA.addPostOp( opAPost1 );
    opAPre1.addPreOp( opAPre1Pre1 );
    opAPre2.addPostOp( opAPre2Post1 );
    
    manager.setPostExecuteListener(stopListener1);
    
    expectedOps.add(opAPre1Pre1);
    expectedOps.add(opAPre1);
    expectedOps.add( new TestExtendedOperation() );
    expectedOps.add(opAPre2);
    expectedOps.add(opAPre2Post1);
    expectedOps.add(opA);
    expectedOps.add(opAPost1);
    expectedOps.add(opB);
    manager.runOperations();
    checkResults();

    expectedOps.removeAllElements();
    expectedUndoOps.add(opB);
    expectedUndoOps.add(opAPost1);
    expectedUndoOps.add(opA);
    expectedUndoOps.add(opAPre2Post1);
    expectedUndoOps.add(opAPre2);
    expectedUndoOps.add( new TestExtendedOperation() );
    expectedUndoOps.add(opAPre1);
    expectedUndoOps.add(opAPre1Pre1);
    manager.undoLastRun();
    checkResults();
    
    reset();
    manager.setPostExecuteListener(stopListener2);
    
    expectedOps.add(opAPre1Pre1);
    manager.runOperations();
    checkResults();
    
    expectedOps.add(opAPre1);
    expectedOps.add( new TestExtendedOperation() );
    expectedOps.add(opAPre2);
    expectedOps.add(opAPre2Post1);
    expectedOps.add(opA);
    expectedOps.add(opAPost1);
    manager.runOperations();
    checkResults();
    
    expectedOps.add(opB);
    manager.runOperations();
    checkResults();
    
  }
  
	private void reset() {
		executedOps.removeAllElements();
		expectedOps.removeAllElements();
		executedUndoOps.removeAllElements();
		expectedUndoOps.removeAllElements();
	}

	private void checkResults() {
		assertTrue("Expected=" + expectedOps.size() + " executed=" + executedOps.size(), executedOps.size() == expectedOps.size());

		for (int index = 0; index < executedOps.size(); index++) {
			assertEquals(((BaseOperation) expectedOps.elementAt(index)).getID(), ((BaseOperation) executedOps.elementAt(index)).getID());
		}

		assertTrue("Expected undo=" + expectedUndoOps.size() + " executed=" + executedUndoOps.size(), executedUndoOps.size() == expectedUndoOps.size());

		for (int index = 0; index < executedUndoOps.size(); index++) {
			assertEquals(((BaseOperation) expectedUndoOps.elementAt(index)).getID(), ((BaseOperation) executedUndoOps.elementAt(index)).getID());
		}
	}

	private class DataModelProvider extends AbstractDataModelProvider {

		public Set getPropertyNames() {
			return new HashSet();
		}
	}

	private class StopListener implements OperationListener {
		private String[] stopIds_;
		private int idIndex_;

		public StopListener(String[] ids) {
			stopIds_ = ids;
			idIndex_ = 0;
		}

		public boolean notify(IDataModelOperation operation) {
			boolean continueRun = true;

			if (idIndex_ < stopIds_.length && operation.getID().equals(stopIds_[idIndex_])) {
				continueRun = false;
				idIndex_++;
			}

			return continueRun;
		}

	}
}
