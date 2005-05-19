/*
 * Created on Nov 6, 2003
 * 
 * To change the template for this generated file go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 * Comments
 */
package org.eclipse.wst.common.tests;

import java.util.List;

import junit.framework.Assert;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel;


/**
 * @author jsholl
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class OperationTestCase extends BaseTestCase {

	public static String fileSep = System.getProperty("file.separator"); //$NON-NLS-1$

	public static IStatus OK_STATUS = new Status(IStatus.OK, "org.eclipse.jem.util", 0, "OK", null); //$NON-NLS-1$ //$NON-NLS-2$

	// public abstract void testBVT() throws Exception;

	protected void setUp() throws Exception {
		super.setUp();
		ProjectUtility.deleteAllProjects();
		// LogUtility.getInstance().resetLogging();
	}

	public OperationTestCase() {
		super("OperationsTestCase"); //$NON-NLS-1$
	}

	public OperationTestCase(String name) {
		super(name);
	}

	/**
	 * @deprecated
	 */
	public static void runAndVerify(WTPOperationDataModel dataModel) throws Exception {
		OperationTestCase.runAndVerify(dataModel, true, true);
	}

	public static void runAndVerify(IDataModel dataModel) throws Exception {
		OperationTestCase.runAndVerify(dataModel, true, true);
	}

	/**
	 * @deprecated
	 */
	public static void runAndVerify(WTPOperationDataModel dataModel, boolean checkTasks, boolean checkLog) throws Exception {
		runAndVerify(dataModel, checkTasks, checkLog, null, true, false);
	}

	public static void runAndVerify(IDataModel dataModel, boolean checkTasks, boolean checkLog) throws Exception {
		runAndVerify(dataModel, checkTasks, checkLog, null, true, false);
	}

	/**
	 * @deprecated
	 */
	public static void runAndVerify(WTPOperationDataModel dataModel, boolean checkTasks, boolean checkLog, boolean waitForBuildToComplete) throws Exception {
		runAndVerify(dataModel, checkTasks, checkLog, null, true, waitForBuildToComplete);
	}

	public static void runAndVerify(IDataModel dataModel, boolean checkTasks, boolean checkLog, boolean waitForBuildToComplete) throws Exception {
		runAndVerify(dataModel, checkTasks, checkLog, null, true, waitForBuildToComplete);
	}

	/**
	 * @deprecated
	 */
	public static void runAndVerify(WTPOperationDataModel dataModel, boolean checkTasks, boolean checkLog, List errorOKList, boolean reportIfExpectedErrorNotFound) throws Exception {
		runAndVerify(dataModel, checkTasks, checkLog, errorOKList, reportIfExpectedErrorNotFound, false);
	}

	public static void runAndVerify(IDataModel dataModel, boolean checkTasks, boolean checkLog, List errorOKList, boolean reportIfExpectedErrorNotFound) throws Exception {
		runAndVerify(dataModel, checkTasks, checkLog, errorOKList, reportIfExpectedErrorNotFound, false);
	}

	/**
	 * @deprecated
	 */
	public static void runAndVerify(WTPOperationDataModel dataModel, boolean checkTasks, boolean checkLog, List errorOKList, boolean reportIfExpectedErrorNotFound, boolean waitForBuildToComplete) throws Exception {
		runAndVerify(dataModel, checkTasks, checkLog, errorOKList, reportIfExpectedErrorNotFound, false, false);
	}

	public static void runAndVerify(IDataModel dataModel, boolean checkTasks, boolean checkLog, List errorOKList, boolean reportIfExpectedErrorNotFound, boolean waitForBuildToComplete) throws Exception {
		runAndVerify(dataModel, checkTasks, checkLog, errorOKList, reportIfExpectedErrorNotFound, false, false);
	}

	/**
	 * @deprecated
	 * 
	 * Guaranteed to close the dataModel
	 * 
	 * @param dataModel
	 * @throws Exception
	 */
	public static void runAndVerify(WTPOperationDataModel dataModel, boolean checkTasks, boolean checkLog, List errorOKList, boolean reportIfExpectedErrorNotFound, boolean waitForBuildToComplete, boolean removeAllSameTypesOfErrors) throws Exception {
		PostBuildListener listener = null;
		IWorkspaceDescription desc = null;
		try {
			if (waitForBuildToComplete) {
				listener = new PostBuildListener();
				desc = ResourcesPlugin.getWorkspace().getDescription();
				desc.setAutoBuilding(false);
				ResourcesPlugin.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_BUILD);
			}
			if (checkLog)
				LogUtility.getInstance().resetLogging();
			verifyValidDataModel(dataModel);
			dataModel.getDefaultOperation().run(null);
			verifyDataModel(dataModel);
			if (waitForBuildToComplete) {
				desc.setAutoBuilding(true);
				while (!listener.isBuildComplete()) {
					Thread.sleep(3000);// do nothing till all the jobs are completeled
				}
			}
			if (checkTasks && (errorOKList == null || errorOKList.isEmpty())) {
				checkTasksList();
			} else if (checkTasks && errorOKList != null && !errorOKList.isEmpty()) {
				TaskViewUtility.verifyErrors(errorOKList, reportIfExpectedErrorNotFound, removeAllSameTypesOfErrors);
			}
			if (checkLog) {
				checkLogUtility();
			}
		} finally {
			if (listener != null)
				ResourcesPlugin.getWorkspace().removeResourceChangeListener(listener);
			dataModel.dispose();
		}
	}

	/**
	 * Guaranteed to close the dataModel
	 * 
	 * @param dataModel
	 * @throws Exception
	 */
	public static void runAndVerify(IDataModel dataModel, boolean checkTasks, boolean checkLog, List errorOKList, boolean reportIfExpectedErrorNotFound, boolean waitForBuildToComplete, boolean removeAllSameTypesOfErrors) throws Exception {
		PostBuildListener listener = null;
		IWorkspaceDescription desc = null;
		try {
			if (waitForBuildToComplete) {
				listener = new PostBuildListener();
				desc = ResourcesPlugin.getWorkspace().getDescription();
				desc.setAutoBuilding(false);
				ResourcesPlugin.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_BUILD);
			}
			if (checkLog)
				LogUtility.getInstance().resetLogging();
			// TODO Verification to be fixed to use IDataModel
			// verifyValidDataModel(dataModel);
			dataModel.getDefaultOperation().execute(new NullProgressMonitor(), null);
			// TODO Verification to be fixed to use IDataModel
			// verifyDataModel(dataModel);
			if (waitForBuildToComplete) {
				desc.setAutoBuilding(true);
				while (!listener.isBuildComplete()) {
					Thread.sleep(3000);// do nothing till all the jobs are completeled
				}
			}
			if (checkTasks && (errorOKList == null || errorOKList.isEmpty())) {
				checkTasksList();
			} else if (checkTasks && errorOKList != null && !errorOKList.isEmpty()) {
				TaskViewUtility.verifyErrors(errorOKList, reportIfExpectedErrorNotFound, removeAllSameTypesOfErrors);
			}
			if (checkLog) {
				checkLogUtility();
			}
		} finally {
			if (listener != null)
				ResourcesPlugin.getWorkspace().removeResourceChangeListener(listener);
			dataModel.dispose();
		}
	}


	/**
	 * @deprecated
	 */
	public static void verifyDataModel(WTPOperationDataModel dataModel) throws Exception {
		DataModelVerifier verifier = DataModelVerifierFactory.getInstance().createVerifier(dataModel);
		verifier.verify(dataModel);
	}

	protected static void checkLogUtility() {
		LogUtility.getInstance().verifyNoWarnings();
	}

	protected static void checkTasksList() {
		TaskViewUtility.verifyNoErrors();
	}

	/**
	 * @deprecated
	 */
	public static void verifyValidDataModel(WTPOperationDataModel dataModel) {
		IStatus status = dataModel.validateDataModel();

		if (!status.isOK() && status.getSeverity() == IStatus.ERROR) {
			Assert.assertTrue("DataModel is invalid operation will not run:" + status.toString(), false); //$NON-NLS-1$
		}
	}

	public static void verifyValidDataModel(IDataModel dataModel) {
		IStatus status = dataModel.validate();

		if (!status.isOK() && status.getSeverity() == IStatus.ERROR) {
			Assert.assertTrue("DataModel is invalid operation will not run:" + status.toString(), false); //$NON-NLS-1$
		}
	}

	/**
	 * @deprecated
	 */
	public static void verifyInvalidDataModel(WTPOperationDataModel dataModel) {
		IStatus status = dataModel.validateDataModel();
		if (status.isOK()) {
			Assert.assertTrue("DataModel should be invalid:" + status.getMessage(), false); //$NON-NLS-1$
		}
	}

	public static void verifyInvalidDataModel(IDataModel dataModel) {
		IStatus status = dataModel.validate();
		if (status.isOK()) {
			Assert.assertTrue("DataModel should be invalid:" + status.getMessage(), false); //$NON-NLS-1$
		}
	}
}
