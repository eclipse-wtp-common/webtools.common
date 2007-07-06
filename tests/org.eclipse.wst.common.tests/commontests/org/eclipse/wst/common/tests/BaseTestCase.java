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
import java.util.List;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestResult;

public class BaseTestCase extends TestCase {

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
	public void run(TestResult result) {
		try{
			AssertWarn.clearWarnings();
			super.run(result);
			
		} finally {
			List <StackTraceElement> stackTrace = new ArrayList<StackTraceElement>();
			List <AssertionFailedError> warnings = null;
			
			//check to see if this test already failed with an error
			//if so add error to stack trace
			Enumeration errorsEnum = result.errors();
			TestFailure errorOrFailure = null;
			boolean errorForTestExists = false;
			for(int i = 0; errorsEnum.hasMoreElements() && !errorForTestExists; i++) {
				Object o = errorsEnum.nextElement();
				errorOrFailure = (TestFailure)o;
				if(errorOrFailure.failedTest() == this) {
					errorForTestExists = true;
					
					Throwable thrown = errorOrFailure.thrownException();
					List<StackTraceElement> errorTrace = Arrays.asList(thrown.getStackTrace());
					stackTrace.addAll(errorTrace);
				}
			}
			
			//check to see if this test already failed with a failure if it didn't
			// fail with an error. if so add failure to stack trace
			boolean failureForTestExists = false;
			if(!errorForTestExists) {
				Enumeration failuresEnum = result.failures();
				for(int i = 0; failuresEnum.hasMoreElements() && !failureForTestExists; i++) {
					Object o = failuresEnum.nextElement();
					errorOrFailure = (TestFailure)o;
					if(errorOrFailure.failedTest() == this) {
						failureForTestExists = true;
						
						Throwable thrown = errorOrFailure.thrownException();
						List<StackTraceElement> failureTrace = Arrays.asList(thrown.getStackTrace());
						stackTrace.addAll(failureTrace);
					}
				}
			}
			
			//add any warnings to the stack trace and deal with if this test already failed
			warnings = AssertWarn.getWarnings();
			if(warnings.size() != 0) {
				int warningNum = 1;
				List<StackTraceElement> warningTrace = null;
				for(AssertionFailedError e : warnings){
					stackTrace.add(new StackTraceElement("---------->","", "WARNING MESSAGE: " + e.getMessage() + ")  (WARNING NUMBER", warningNum++));
					
					warningTrace = Arrays.asList(e.getStackTrace());
					stackTrace.addAll(warningTrace);
				}
				
				//deal with making a new formated failure and added it to the result
				String message = "";
				if((errorForTestExists || failureForTestExists)) {
					message = errorOrFailure.exceptionMessage();
					message += "  (Test also had " + warnings.size() + " warnings occure.)";
				} else {
					message = "Test had " + warnings.size() + " warnings occure.";
				}

				AssertionFailedError error = new AssertionFailedError(message);
				StackTraceElement[] stackTraceArray = stackTrace.toArray(new StackTraceElement[stackTrace.size()]);
				error.setStackTrace(stackTraceArray);
				
				if(errorForTestExists) {
					result.addError(this, error);
				} else {
					result.addFailure(this, error);
				}
				
			}
		}
	}
	
}
