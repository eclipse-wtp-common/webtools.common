/*
 * Created on Nov 24, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.wst.common.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestResult;

import org.eclipse.core.runtime.IStatus;

public class BaseTestCase extends TestCase {
	
	private static boolean fFailOnLoggedStatus = true;
	
	/**
	 * 
	 */ 
	public BaseTestCase() {
		super();
	}

	/**
	 * @param name
	 */
	public BaseTestCase(String name) {
		super(name); 
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUpTest() throws Exception { 
		setUp();
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	public final void tearDownTest() throws Exception { 
		tearDown();
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#runBare()
	 */
	public final void runCoreTest() throws Throwable { 
		runTest();
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		//TODO register any plugins with the JUnitLogListener here that you want to fail on when their log is written too
		//EX: CommonTestsPlugin.instance.getLog().addLogListener(JUnitLogListener.INSTANCE);
	}
	
	/**
	 * Set weather to fail junit tests on status logged to plugins registered with the <code>JUnitLogListener</code>
	 * Default value is set to true.
	 * 
	 * @param failOnLoggedStatus
	 * 			if <code>true</code> then fail tests when statuses logged to plugins registered with <code>JUnitLogListener</code>,
	 * 			if <code>false</code> then dont fail on logged statuses.
	 */
	protected static void failOnLoggedStatus(boolean failOnLoggedStatus) {
		fFailOnLoggedStatus = failOnLoggedStatus;
	}

	@Override
	public void run(TestResult result) {
		try{
			AssertWarn.clearWarnings();
			super.run(result);
			
		} finally {
			List <StackTraceElement> stackTrace = new ArrayList<StackTraceElement>();
			
			//check to see if this test already failed with an error
			//if so add error to stack trace
			Enumeration errorsEnum = result.errors();
			TestFailure errorOrFailure = null;
			boolean hadError = false;
			for(int i = 0; errorsEnum.hasMoreElements() && !hadError; i++) {
				Object o = errorsEnum.nextElement();
				errorOrFailure = (TestFailure)o;
				if(errorOrFailure.failedTest() == this) {
					hadError = true;
					
					Throwable thrown = errorOrFailure.thrownException();
					List<StackTraceElement> errorTrace = Arrays.asList(thrown.getStackTrace());
					stackTrace.addAll(errorTrace);
				}
			}
			
			//check to see if this test already failed with a failure if it didn't
			// fail with an error. if so add failure to stack trace
			boolean hadFailure = false;
			if(!hadError) {
				Enumeration failuresEnum = result.failures();
				for(int i = 0; failuresEnum.hasMoreElements() && !hadFailure; i++) {
					Object o = failuresEnum.nextElement();
					errorOrFailure = (TestFailure)o;
					if(errorOrFailure.failedTest() == this) {
						hadFailure = true;
						
						Throwable thrown = errorOrFailure.thrownException();
						List<StackTraceElement> failureTrace = Arrays.asList(thrown.getStackTrace());
						stackTrace.addAll(failureTrace);
					}
				}
			}
			
			//add any warnings to the stack trace and deal with if this test already failed
			List <AssertionFailedError> warnings = AssertWarn.getWarnings();
			boolean hadWarnings = warnings.size() != 0;
			if(hadWarnings) {
				int warningNum = 1;
				List<StackTraceElement> warningTrace = null;
				for(AssertionFailedError e : warnings){
					stackTrace.add(new StackTraceElement("---------->","", "WARNING MESSAGE: " + e.getMessage() + ")  (WARNING NUMBER", warningNum++));
					
					warningTrace = Arrays.asList(e.getStackTrace());
					stackTrace.addAll(warningTrace);
				}
			}
			
			//add any logged statuses to the stack trace, as long as fFailOnLoggedStatus == true
			HashMap<IStatus,String> loggedStatuses = JUnitLogListener.INSTANCE.getLoggedStatuses();
			boolean hadLoggedStatuses = loggedStatuses.size() != 0;
			if(hadLoggedStatuses && fFailOnLoggedStatus) {
				int loggedMessageNum = 1;
				List<StackTraceElement> loggedMessageTrace = null;
				
				Set<IStatus> loggedStatusesKeys = loggedStatuses.keySet();
				String statusType;
				for(IStatus status : loggedStatusesKeys) {
					stackTrace.add(new StackTraceElement("---------->","", "LOGGED " + statusTypeToString(status.getSeverity()) + " STATUS: " + loggedStatuses.get(status) + ": " + status.getMessage() + ")  (LOGGED STATUS NUMBER", loggedMessageNum++));
					
					if(status.getException() != null) {
						loggedMessageTrace = Arrays.asList(status.getException().getStackTrace());
						stackTrace.addAll(loggedMessageTrace);
					}
				}
			}
			
			//deal with making a new formated failure and added it to the result
			String message = "";
			String warningsMessage = "Test had " + warnings.size() + " warnings occur";
			String loggedStatusesMessage = "Test logged " + loggedStatuses.size() + " statuses";
			if(hadError || hadFailure) {
				message = errorOrFailure.exceptionMessage();
				
				if(hadWarnings) {
					message += "  (" + warningsMessage + ")";
				}
				
				if(hadLoggedStatuses && fFailOnLoggedStatus) {
					message += "  (" + loggedStatusesMessage + ")";
				}
			} else if(hadWarnings) {
				message = warningsMessage;
				
				if(hadLoggedStatuses && fFailOnLoggedStatus) {
					message += "  (" + loggedStatusesMessage + ")";
				}
			} else if(hadLoggedStatuses && fFailOnLoggedStatus) {
				message = loggedStatusesMessage;
			}
			
			if(hadError || hadFailure || hadWarnings || (hadLoggedStatuses && fFailOnLoggedStatus)) {
				AssertionFailedError error = new AssertionFailedError(message);
				StackTraceElement[] stackTraceArray = stackTrace.toArray(new StackTraceElement[stackTrace.size()]);
				error.setStackTrace(stackTraceArray);

				if(hadError) {
					result.addError(this, error);
				} else {
					result.addFailure(this, error);
				}
			}


		}
	}
	
	private String statusTypeToString(int statusType) {
		String sStatusType = "";
		switch (statusType) {
			case IStatus.ERROR :
				sStatusType = "ERROR";
				break;
			case IStatus.CANCEL :
				sStatusType = "CANCEL";
				break;
			case IStatus.WARNING : 
				sStatusType = "WARNING";
				break;
		}
		
		return sStatusType;
	}
}
