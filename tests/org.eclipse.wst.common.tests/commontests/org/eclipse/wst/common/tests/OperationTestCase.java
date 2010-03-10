/*
 * Created on Nov 6, 2003
 * 
 * To change the template for this generated file go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 * Comments
 */
package org.eclipse.wst.common.tests;

import java.util.List;

import junit.framework.Assert;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.datamodel.IWorkspaceRunnableWithStatus;
import org.eclipse.wst.common.internal.emf.resource.RendererFactory;
import org.eclipse.wst.validation.internal.operations.ValidationBuilder;


/**
 * @author jsholl
 * @author itewk
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class OperationTestCase extends BaseTestCase {

	public static final String VALIDATOR_JOB_FAMILY = "validators";
	public static String fileSep = System.getProperty("file.separator"); //$NON-NLS-1$
	public static IStatus OK_STATUS = new Status(IStatus.OK, "org.eclipse.jem.util", 0, "OK", null); //$NON-NLS-1$ //$NON-NLS-2$
	
	protected void setUp() throws Exception {
		super.setUp();
		try{ 
			deleteAllProjects();
		} finally {
			RendererFactory.getDefaultRendererFactory().setValidating(false);
		}
	}
	
	public static void deleteAllProjects() {
		try {
			waitOnJobs();
		} catch (InterruptedException e1) {
			
		}
		IWorkspaceRunnableWithStatus workspaceRunnable = new IWorkspaceRunnableWithStatus(null) {
			public void run(IProgressMonitor pm) throws CoreException {
				try {
					ProjectUtility.deleteAllProjects();
				} catch (Exception e) {
				}
			}
		};
		try {
			ResourcesPlugin.getWorkspace().run(workspaceRunnable, null);
		} catch (CoreException e) {
			
		}
	}

	public OperationTestCase() {
		super("OperationsTestCase"); //$NON-NLS-1$
	}

	public OperationTestCase(String name) {
		super(name);
	}

	public static void runAndVerify(IDataModel dataModel) throws Exception {
		OperationTestCase.runAndVerify(dataModel, true, true);
	}
	public static void runDataModel(IDataModel dataModel) throws Exception {
		OperationTestCase.runDataModel(dataModel, true, true);
	}

	public static void runDataModel(IDataModel dataModel, boolean checkTasks, boolean checkLog) throws Exception {
		OperationTestCase.runDataModel(dataModel, checkTasks, checkLog, null, true, false);
	}

	public static void runAndVerify(IDataModel dataModel, boolean checkTasks, boolean checkLog) throws Exception {
		runAndVerify(dataModel, checkTasks, checkLog, null, true, false);
	}

	public static void runAndVerify(IDataModel dataModel, boolean checkTasks, boolean checkLog, boolean waitForBuildToComplete) throws Exception {
		runAndVerify(dataModel, checkTasks, checkLog, null, true, waitForBuildToComplete);
	}

	public static void runAndVerify(IDataModel dataModel, boolean checkTasks, boolean checkLog, List errorOKList, boolean reportIfExpectedErrorNotFound) throws Exception {
		runAndVerify(dataModel, checkTasks, checkLog, errorOKList, reportIfExpectedErrorNotFound, false);
	}

	public static void runAndVerify(IDataModel dataModel, boolean checkTasks, boolean checkLog, List errorOKList, boolean reportIfExpectedErrorNotFound, boolean waitForBuildToComplete) throws Exception {
		runAndVerify(dataModel, checkTasks, checkLog, errorOKList, reportIfExpectedErrorNotFound, waitForBuildToComplete, false);
	}
	
	public static void runDataModel(IDataModel dataModel, boolean checkTasks, boolean checkLog, List errorOKList, boolean reportIfExpectedErrorNotFound, boolean waitForBuildToComplete) throws Exception {
		runDataModel(dataModel, checkTasks, checkLog, errorOKList, reportIfExpectedErrorNotFound, waitForBuildToComplete, false);
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
				ResourcesPlugin.getWorkspace().setDescription(desc);
				ResourcesPlugin.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_BUILD);
			}
			
			//deal with weather to fail on logged statuses or not
			BaseTestCase.failOnLoggedStatus(checkLog);
			
			IStatus operationStatus = dataModel.getDefaultOperation().execute(new NullProgressMonitor(), null);
			
			if (waitForBuildToComplete) {
				desc.setAutoBuilding(true);
				ResourcesPlugin.getWorkspace().setDescription(desc);
				while (!listener.isBuildComplete()) {
					Thread.sleep(3000);// do nothing till all the jobs are completed
				}
			}
			
			// bug 173933 - runAndVerify() fails to check return IStatus 
			if (operationStatus.getSeverity() == IStatus.ERROR) {
				Throwable throwable = operationStatus.getException();
				String throwableStr = null;
				if(throwable != null){
					throwable.printStackTrace();
					throwableStr = getStackTrace(throwable);
				}
				if(throwableStr == null){
					throwableStr = "no message";
				}
				Assert.fail(operationStatus.getMessage()+"\n    caused by: "+throwableStr);
			}
			
			//run data model verifications
			DataModelVerifierFactory verifierFactory = DataModelVerifierFactory.getInstance();
			DataModelVerifier verifier = verifierFactory.createVerifier(dataModel);
			verifier.verify(dataModel);

			if (checkTasks && (errorOKList == null || errorOKList.isEmpty())) {
				checkTasksList();
			} else if (checkTasks && errorOKList != null && !errorOKList.isEmpty()) {
				TaskViewUtility.verifyErrors(errorOKList, reportIfExpectedErrorNotFound, removeAllSameTypesOfErrors);
			}
			
		} finally {
			if (listener != null)
				ResourcesPlugin.getWorkspace().removeResourceChangeListener(listener);
			dataModel.dispose();
		}
	}
	
	public static String getStackTrace(Throwable throwable){
		StringBuffer buffer = new StringBuffer(throwable.toString()+"\n");
		StackTraceElement[] stack = throwable.getStackTrace();
		for (int i=0; i<stack.length; i++)
			buffer.append("\tat " + stack[i]+"\n");

		StackTraceElement[] parentStack = stack;
		throwable = throwable.getCause();
		while (throwable != null) {
			buffer.append("Caused by: ");
			buffer.append(throwable);
			buffer.append("\n");
			StackTraceElement[] currentStack = throwable.getStackTrace();
			parentStack = currentStack;
			throwable = throwable.getCause();
		}
		return buffer.toString();
	}
	/**
	 * Guaranteed to close the dataModel
	 * 
	 * @param dataModel
	 * @throws Exception
	 */
	public static void runDataModel(IDataModel dataModel, boolean checkTasks, boolean checkLog, List errorOKList, boolean reportIfExpectedErrorNotFound, boolean waitForBuildToComplete, boolean removeAllSameTypesOfErrors) throws Exception {
		PostBuildListener listener = null;
		IWorkspaceDescription desc = null;
		try {
			if (waitForBuildToComplete) {
				listener = new PostBuildListener();
				desc = ResourcesPlugin.getWorkspace().getDescription();
				desc.setAutoBuilding(false);
				ResourcesPlugin.getWorkspace().setDescription(desc);
				ResourcesPlugin.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_BUILD);
			}
			
			//deal with weather to fail on logged statuses or not
			BaseTestCase.failOnLoggedStatus(checkLog);
			
			dataModel.getDefaultOperation().execute(new NullProgressMonitor(), null);
			
			if (waitForBuildToComplete) {
				desc.setAutoBuilding(true);
				ResourcesPlugin.getWorkspace().setDescription(desc);
				while (!listener.isBuildComplete()) {
					Thread.sleep(3000);// do nothing till all the jobs are completeled
				}
			}
			if (checkTasks && (errorOKList == null || errorOKList.isEmpty())) {
				checkTasksList();
			} else if (checkTasks && errorOKList != null && !errorOKList.isEmpty()) {
				TaskViewUtility.verifyErrors(errorOKList, reportIfExpectedErrorNotFound, removeAllSameTypesOfErrors);
			}

		} finally {
			if (listener != null)
				ResourcesPlugin.getWorkspace().removeResourceChangeListener(listener);
			dataModel.dispose();
		}
	}


	protected static void checkTasksList() {
		//TaskViewUtility.verifyNoErrors();
	}

	public static void verifyValidDataModel(IDataModel dataModel) {
		IStatus status = dataModel.validate();

		if (!status.isOK() && status.getSeverity() == IStatus.ERROR) {
			Assert.assertTrue("DataModel is invalid operation will not run:" + status.toString(), false); //$NON-NLS-1$
		}
	}

	public static void verifyInvalidDataModel(IDataModel dataModel) {
		IStatus status = dataModel.validate();
		if (status.isOK()) {
			Assert.assertTrue("DataModel should be invalid:" + status.getMessage(), false); //$NON-NLS-1$
		}
	}
	protected void tearDown() throws Exception {
		super.tearDown();
		// Wait for all validation jobs to end before ending test....
		waitOnJobs();
		
	}

	public static void waitOnJobs() throws InterruptedException {
		IProject[] projects = ProjectUtility.getAllProjects();
		try {
			for (int i = 0; i < projects.length; i++) {
				IProject project = projects[i];
				Job.getJobManager().join(project.getName() + VALIDATOR_JOB_FAMILY,null);
			}
			Job.getJobManager().join(ResourcesPlugin.FAMILY_MANUAL_BUILD,null);
			Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD,null);
			Job.getJobManager().join(ValidationBuilder.FAMILY_VALIDATION_JOB,null);
		} catch (InterruptedException ex) {
			
		}
	}
}
