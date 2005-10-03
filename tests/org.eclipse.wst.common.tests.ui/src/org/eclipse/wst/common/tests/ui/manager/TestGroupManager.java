/***************************************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.tests.ui.manager;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import junit.framework.TestCase;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.internal.DataModelManager;
import org.eclipse.wst.common.frameworks.internal.OperationManager;
import org.eclipse.wst.common.frameworks.internal.datamodel.DataModelImpl;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizardPage;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.IDMExtendedPageGroupHandler;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.IDMExtendedPageHandler;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.SimplePageGroup;
import org.eclipse.wst.common.frameworks.internal.ui.PageGroupManager;
import org.eclipse.wst.common.frameworks.operations.tests.manager.BaseOperation;

public class TestGroupManager extends TestCase {
	private OperationManager operationManager;
	private PageGroupManager pageGroupManager;
	private BaseOperation opA;
	private BaseOperation opB;
	private BaseOperation opC;
	private BaseOperation opD;
	private BaseOperation opE;
	private BaseOperation opF;
	private BaseOperation opG;
	private SimplePageGroup pgA;
	private SimplePageGroup pgB;
	private SimplePageGroup pgC;
	private SimplePageGroup pgD;
	private SimplePageGroup pgE;
	private SimplePageGroup pgF;
	private SimplePageGroup pgG;
	private SimplePageGroup pgH;
	private SimplePageGroup pgRoot;
	private WizardPage r1;
	private WizardPage b1;
	private WizardPage b2;
	private WizardPage c1;
	private WizardPage d1;
	private WizardPage d2;
	private WizardPage d3;
	private WizardPage f1;
	private WizardPage f2;
	private WizardPage f3;
	private WizardPage f4;
	private WizardPage f5;
	private WizardPage f6;
	private WizardPage g1;

	private AGroupHandler aGroupHandler;
	private FGroupHandler fGroupHandler;
	// private Status error_ = new Status( IStatus.ERROR, "id", 0, "mess", null );
	private Vector executedOps;
	private Vector executedUndoOps;
	private Vector expectedOps;
	private Vector expectedUndoOps;
	private IDataModel dataModel;

	protected void setUp() throws Exception {
		super.setUp();

		executedOps = new Vector();
		executedUndoOps = new Vector();
		expectedOps = new Vector();
		expectedUndoOps = new Vector();
		dataModel = new DataModelImpl(new DataModelProvider());

		DataModelManager dataModelManager = new DataModelManager(dataModel);

		opA = new BaseOperation("A", executedOps, executedUndoOps);
		opB = new BaseOperation("B", executedOps, executedUndoOps);
		opC = new BaseOperation("C", executedOps, executedUndoOps);
		opD = new BaseOperation("D", executedOps, executedUndoOps);
		opE = new BaseOperation("E", executedOps, executedUndoOps);
		opF = new BaseOperation("F", executedOps, executedUndoOps);
		opG = new BaseOperation("G", executedOps, executedUndoOps);

		// Operations are organized as follows:
		//
		// D
		// / \
		// B F
		// / \ / \
		// A C E G
		operationManager = new OperationManager(dataModelManager, opD);
		operationManager.addPreOperation(opD.getID(), opB);
		operationManager.addPostOperation(opD.getID(), opF);
		operationManager.addPreOperation(opB.getID(), opA);
		operationManager.addPostOperation(opB.getID(), opC);
		operationManager.addPreOperation(opF.getID(), opE);
		operationManager.addPostOperation(opF.getID(), opG);

		// Page groups are organized as follows:
		//
		// B - C
		// / \
		// Root - A - D \ G
		// \ \ /
		// ------ E - F - H
		// \ null
		//                     
		// The page group handler for A will return either B and then E or D and
		// then E. The group handler for F will return either G or H and then null or
		// just null.
		// 
		// Some of these group require operations to run first:
		//
		// Page group B requires operation C.
		// Page group D requires operation C.
		// Page group E requires operation E.
		// Page group F requires operation C.( C will already have been run and should not be
		// rerun.)
		//
		// Each page group has some pages associated with it as follows:
		//
		// Root has page r1.
		// A has no pages.
		// B has b1 and b2.
		// C has c1
		// D has d1, d2, and d3
		// E has no pages.
		// F has pages f1, f2, f3, f4, f5, f6
		// the page handler for F will return the following:
		// expected = f1 returns f1
		// expected = f2 returns skip
		// expected = f3 returns before f6
		// expected = f4 returns null
		// expected = f5 returns after f5
		// expected = f6 returns f4
		// G has pages g1
		// H has no pages.
		r1 = new WizardPage("r1");
		b1 = new WizardPage("b1");
		b2 = new WizardPage("b2");
		c1 = new WizardPage("c1");
		d1 = new WizardPage("d1");
		d2 = new WizardPage("d2");
		d3 = new WizardPage("d3");
		f1 = new WizardPage("f1");
		f2 = new WizardPage("f2");
		f3 = new WizardPage("f3");
		f4 = new WizardPage("f4");
		f5 = new WizardPage("f5");
		f6 = new WizardPage("f6");
		g1 = new WizardPage("g1");

		String wizardID = "testWizard";

		pgRoot = new SimplePageGroup("Root", wizardID);
		pgA = new SimplePageGroup("A", wizardID);
		pgB = new SimplePageGroup("B", wizardID, true, "C");
		pgC = new SimplePageGroup("C", wizardID);
		pgD = new SimplePageGroup("D", wizardID, true, "C");
		pgE = new SimplePageGroup("E", wizardID, true, "E");
		pgF = new SimplePageGroup("F", wizardID, true, "C");
		pgG = new SimplePageGroup("G", wizardID);
		pgH = new SimplePageGroup("H", wizardID);

		pgRoot.addPages(new WizardPage[]{r1});
		pgB.addPages(new WizardPage[]{b1, b2});
		pgC.addPages(new WizardPage[]{c1});
		pgD.addPages(new WizardPage[]{d1, d2, d3});
		pgF.addPages(new WizardPage[]{f1, f2, f3, f4, f5, f6});
		pgG.addPages(new WizardPage[]{g1});

		pgF.setExtendedPageHandler(new FPageHandler());

		aGroupHandler = new AGroupHandler();
		fGroupHandler = new FGroupHandler();

		pgA.setExtendedPageGroupHandler(aGroupHandler);
		pgF.setExtendedPageGroupHandler(fGroupHandler);

		pageGroupManager = new PageGroupManager(operationManager, dataModelManager, pgRoot);
		pageGroupManager.addGroupAfter("Root", pgA);
		pageGroupManager.addGroupAfter("A", pgB);
		pageGroupManager.addGroupAfter("A", pgD);
		pageGroupManager.addGroupAfter("A", pgE);
		pageGroupManager.addGroupAfter("B", pgC);
		pageGroupManager.addGroupAfter("E", pgF);
		pageGroupManager.addGroupAfter("F", pgG);
		pageGroupManager.addGroupAfter("F", pgH);
	}

	public void testSimpleRun() throws Exception {
		assertTrue("There should be a next page", pageGroupManager.hasNextPage());
		assertTrue("The root page should be null", pageGroupManager.getCurrentPage() == null);
		pageGroupManager.moveBackOnePage(); // Should do nothing.
		checkResults();

		pageGroupManager.moveForwardOnePage();
		aGroupHandler.setGroupIDToSelect("B");
		assertTrue("There should be a next page", pageGroupManager.hasNextPage());
		assertTrue("The page should be r1", pageGroupManager.getCurrentPage() == r1);
		checkResults();

		pageGroupManager.moveForwardOnePage();
		assertTrue("There should be a next page", pageGroupManager.hasNextPage());
		assertTrue("The page should be b1", pageGroupManager.getCurrentPage() == b1);
		expectedOps.add(opA);
		expectedOps.add(opB);
		expectedOps.add(opC);
		checkResults();

		pageGroupManager.moveForwardOnePage();
		assertTrue("There should be a next page", pageGroupManager.hasNextPage());
		assertTrue("The page should be b2", pageGroupManager.getCurrentPage() == b2);
		checkResults();

		pageGroupManager.moveForwardOnePage();
		assertTrue("There should be a next page", pageGroupManager.hasNextPage());
		assertTrue("The page should be c1", pageGroupManager.getCurrentPage() == c1);
		checkResults();

		pageGroupManager.moveForwardOnePage();
		assertTrue("There should be a next page", pageGroupManager.hasNextPage());
		assertTrue("The page should be f1", pageGroupManager.getCurrentPage() == f1);
		expectedOps.add(opD);
		expectedOps.add(opE);
		checkResults();

		pageGroupManager.moveForwardOnePage();
		assertTrue("There should be a next page", pageGroupManager.hasNextPage());
		assertTrue("The page should be f3", pageGroupManager.getCurrentPage() == f3);
		checkResults();

		pageGroupManager.moveForwardOnePage();
		assertTrue("There should be a next page", pageGroupManager.hasNextPage());
		assertTrue("The page should be f5", pageGroupManager.getCurrentPage() == f5);
		checkResults();

		pageGroupManager.moveForwardOnePage();
		assertTrue("There should be a next page", pageGroupManager.hasNextPage());
		assertTrue("The page should be f6", pageGroupManager.getCurrentPage() == f6);
		checkResults();

		pageGroupManager.moveForwardOnePage();
		fGroupHandler.setGroupIDToSelect("G");
		assertTrue("There should be a next page", pageGroupManager.hasNextPage());
		assertTrue("The page should be f4", pageGroupManager.getCurrentPage() == f4);
		checkResults();

		pageGroupManager.moveForwardOnePage();
		assertFalse("There should not be a next page", pageGroupManager.hasNextPage());
		assertTrue("The page should be g1", pageGroupManager.getCurrentPage() == g1);
		checkResults();

		pageGroupManager.moveBackOnePage();
		assertTrue("There should be a next page", pageGroupManager.hasNextPage());
		assertTrue("The page should be f4", pageGroupManager.getCurrentPage() == f4);
		checkResults();

		pageGroupManager.moveBackOnePage();
		assertTrue("There should be a next page", pageGroupManager.hasNextPage());
		assertTrue("The page should be f4", pageGroupManager.getCurrentPage() == f6);
		checkResults();

		pageGroupManager.moveBackOnePage();
		assertTrue("There should be a next page", pageGroupManager.hasNextPage());
		assertTrue("The page should be f4", pageGroupManager.getCurrentPage() == f5);
		checkResults();

		pageGroupManager.moveBackOnePage();
		assertTrue("There should be a next page", pageGroupManager.hasNextPage());
		assertTrue("The page should be f4", pageGroupManager.getCurrentPage() == f3);
		checkResults();

		pageGroupManager.moveBackOnePage();
		assertTrue("There should be a next page", pageGroupManager.hasNextPage());
		assertTrue("The page should be f4", pageGroupManager.getCurrentPage() == f1);
		checkResults();

		pageGroupManager.moveBackOnePage();
		assertTrue("There should be a next page", pageGroupManager.hasNextPage());
		assertTrue("The page should be f4", pageGroupManager.getCurrentPage() == c1);
		expectedOps.setSize(3);
		expectedUndoOps.add(opE);
		expectedUndoOps.add(opD);
		checkResults();

		pageGroupManager.moveBackOnePage();
		assertTrue("There should be a next page", pageGroupManager.hasNextPage());
		assertTrue("The page should be b2", pageGroupManager.getCurrentPage() == b2);
		checkResults();

		pageGroupManager.moveBackOnePage();
		assertTrue("There should be a next page", pageGroupManager.hasNextPage());
		assertTrue("The page should be b1", pageGroupManager.getCurrentPage() == b1);
		checkResults();

		pageGroupManager.moveBackOnePage();
		assertTrue("There should be a next page", pageGroupManager.hasNextPage());
		assertTrue("The root page should be r1", pageGroupManager.getCurrentPage() == r1);
		expectedOps = new Vector();
		expectedUndoOps.add(opC);
		expectedUndoOps.add(opB);
		expectedUndoOps.add(opA);
		checkResults();

		reset();
		aGroupHandler.setGroupIDToSelect("D");
		pageGroupManager.moveForwardOnePage();
		assertTrue("There should be a next page", pageGroupManager.hasNextPage());
		assertTrue("The page should be d1", pageGroupManager.getCurrentPage() == d1);
		expectedOps.add(opA);
		expectedOps.add(opB);
		expectedOps.add(opC);
		checkResults();

		pageGroupManager.moveForwardOnePage();
		assertTrue("There should be a next page", pageGroupManager.hasNextPage());
		assertTrue("The page should be d2", pageGroupManager.getCurrentPage() == d2);
		checkResults();

		pageGroupManager.moveForwardOnePage();
		assertTrue("There should be a next page", pageGroupManager.hasNextPage());
		assertTrue("The page should be d3", pageGroupManager.getCurrentPage() == d3);
		checkResults();

		pageGroupManager.moveForwardOnePage();
		assertTrue("There should be a next page", pageGroupManager.hasNextPage());
		assertTrue("The page should be f1", pageGroupManager.getCurrentPage() == f1);
		expectedOps.add(opD);
		expectedOps.add(opE);
		checkResults();

		pageGroupManager.moveForwardOnePage();
		assertTrue("There should be a next page", pageGroupManager.hasNextPage());
		assertTrue("The page should be f3", pageGroupManager.getCurrentPage() == f3);
		checkResults();

		pageGroupManager.moveForwardOnePage();
		assertTrue("There should be a next page", pageGroupManager.hasNextPage());
		assertTrue("The page should be f5", pageGroupManager.getCurrentPage() == f5);
		checkResults();

		pageGroupManager.moveForwardOnePage();
		assertTrue("There should be a next page", pageGroupManager.hasNextPage());
		assertTrue("The page should be f6", pageGroupManager.getCurrentPage() == f6);
		checkResults();

		pageGroupManager.moveForwardOnePage();
		fGroupHandler.setGroupIDToSelect(null);
		assertFalse("There should not be a next page", pageGroupManager.hasNextPage());
		assertTrue("The page should be f4", pageGroupManager.getCurrentPage() == f4);
		checkResults();
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

	private void reset() {
		executedOps.removeAllElements();
		expectedOps.removeAllElements();
		executedUndoOps.removeAllElements();
		expectedUndoOps.removeAllElements();
	}

	private class AGroupHandler implements IDMExtendedPageGroupHandler {
		private String groupID_;

		public String getNextPageGroup(String currentPageGroupID, String[] pageGroupIDs) {
			String result = null;

			if (currentPageGroupID == null) {
				result = groupID_;
			} else if (currentPageGroupID.equals("E")) {
				result = null;
			} else {
				result = "E";
			}

			return result;
		}

		public void setGroupIDToSelect(String id) {
			groupID_ = id;
		}
	}

	private class FGroupHandler implements IDMExtendedPageGroupHandler {
		private String groupID_;

		public String getNextPageGroup(String currentPageGroupID, String[] pageGroupIDs) {
			if (currentPageGroupID == null)
				return groupID_;

			return null;
		}

		public void setGroupIDToSelect(String id) {
			groupID_ = id;
		}
	}

	//
	// F has pages f1, f2, f3, f4, f5, f6
	// the page handler for F will return the following:
	// expected = f1 returns f1
	// expected = f2 returns skip
	// expected = f3 returns before f6
	// expected = f4 returns null
	// expected = f5 returns after f5
	// expected = f6 returns f4
	//
	// This handle should cause the following pages to be used.
	// f1, f3, f5, f6, f4, null
	//
	private class FPageHandler implements IDMExtendedPageHandler {
		public String getNextPage(String currentPageName, String expectedNextPageName) {
			String result = null;

			if (currentPageName == null) {
				result = "f1";
			} else if (currentPageName.equals("f1")) {
				result = IDMExtendedPageHandler.SKIP_PAGE;
			} else if (currentPageName.equals("f3")) {
				result = IDMExtendedPageHandler.PAGE_BEFORE + "f6";
			} else if (currentPageName.equals("f4")) {
				result = null;
			} else if (currentPageName.equals("f5")) {
				result = IDMExtendedPageHandler.PAGE_AFTER + "f5";
			} else if (currentPageName.equals("f6")) {
				result = "f4";
			}

			return result;
		}

		public String getPreviousPage(String currentPageName, String expectedPreviousPageName) {
			return expectedPreviousPageName;
		}

	}

	private class DataModelProvider extends AbstractDataModelProvider {

		public Set getPropertyNames() {
			return new HashSet();
		}
	}

	private class WizardPage extends DataModelWizardPage {
		private boolean canFinish_ = true;

		public WizardPage(String id) {
			super(dataModel, id);
		}

		protected Composite createTopLevelComposite(Composite parent) {
			return null;
		}

		protected String[] getValidationPropertyNames() {
			return new String[0];
		}

		public void setCanFinish(boolean canFinish) {
			canFinish_ = canFinish;
		}

		public boolean canPageFinish() {
			return canFinish_;
		}

		public IDataModelOperation createOperation() {
			return null;
		}
	}
}
