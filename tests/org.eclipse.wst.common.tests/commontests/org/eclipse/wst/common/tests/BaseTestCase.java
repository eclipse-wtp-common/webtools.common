package org.eclipse.wst.common.tests;

import junit.framework.TestCase;

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

		//list of plugins whos logs if written to will cause JUnits to fail
//		ArrayList<String> pluginIDs = new ArrayList<String>();
		
		//TODO uncomment these to make tests start failing on logged errors
//		pluginIDs.add(JEMUtilPlugin.ID);
//		pluginIDs.add("org.eclipse.jst.common.annotations.controller");
//		pluginIDs.add("org.eclipse.jst.common.annotations.core");
//		pluginIDs.add("org.eclipse.jst.common.annotations.ui");
//		pluginIDs.add(CommonFrameworksPlugin.PLUGIN_ID);
//		pluginIDs.add(EJBUIPlugin.PLUGIN_ID);
//		pluginIDs.add(J2EEPlugin.PLUGIN_ID);
//		pluginIDs.add(J2EECorePlugin.PLUGIN_ID);
//		pluginIDs.add(EjbPlugin.PLUGIN_ID);
//		pluginIDs.add(ModelPlugin.PLUGINID);
//		pluginIDs.add("org.eclipse.jst.j2ee.ejb.annotations.emitter");
//		pluginIDs.add(XDocletAnnotationPlugin.PLUGINID);
//		pluginIDs.add(JcaPlugin.PLUGIN_ID);
//		pluginIDs.add(JCAUIPlugin.PLUGIN_ID);
//		pluginIDs.add(J2EENavigatorPlugin.PLUGIN_ID);
//		pluginIDs.add(J2EEUIPlugin.PLUGIN_ID);
//		pluginIDs.add(WebPlugin.PLUGIN_ID);
//		pluginIDs.add(WebServicePlugin.PLUGIN_ID);
//		pluginIDs.add(WebServiceUIPlugin.PLUGIN_ID);
//		pluginIDs.add(JEEPlugin.PLUGIN_ID);
//		pluginIDs.add(Activator.PLUGIN_ID);
//		pluginIDs.add(J2EEUIPlugin.PLUGIN_ID);
//		pluginIDs.add(org.eclipse.jst.jee.web.Activator.PLUGIN_ID);
//		pluginIDs.add(ServletUIPlugin.PLUGIN_ID);
//		pluginIDs.add(PropertiesValidatorPlugin.PLUGIN_ID);
//		pluginIDs.add(BVTValidationPlugin.PLUGIN_ID);
//		pluginIDs.add(EcoreUtilitiesPlugin.ID);
//		pluginIDs.add(EMFWorkbenchEditPlugin.ID);
//		pluginIDs.add(WTPCommonPlugin.PLUGIN_ID);
//		pluginIDs.add(WTPUIPlugin.PLUGIN_ID);
//		pluginIDs.add(ModulecorePlugin.PLUGIN_ID);
//		pluginIDs.add(ValidationPlugin.PLUGIN_ID);
//		pluginIDs.add(ValidationUIPlugin.PLUGIN_ID);
//		pluginIDs.add(WSTWebPlugin.PLUGIN_ID);
//		pluginIDs.add(WSTWebUIPlugin.PLUGIN_ID);
//		pluginIDs.add("org.eclipse.wst.xml.core");
//		pluginIDs.add("org.eclipse.jem");
//		pluginIDs.add(BeaninfoPlugin.PI_BEANINFO_PLUGINID);
//		pluginIDs.add("org.eclipse.jem.proxy");
//		pluginIDs.add("org.eclipse.jem.ui");
//		pluginIDs.add("org.eclipse.jem.workbench");
//		pluginIDs.add("org.eclipse.jem.tests");
//		pluginIDs.add("org.eclipse.jst.standard.schemas");
//		pluginIDs.add(CommonTestsPlugin.PLUGIN_ID);
//		pluginIDs.add("org.eclipse.wst.common.tests.collector");
//		pluginIDs.add("org.eclipse.wst.common.tests.ui");
//		pluginIDs.add("org.eclipse.jst.j2ee.core.tests");
//		pluginIDs.add("org.eclipse.jst.j2ee.core.tests.performance");
//		pluginIDs.add("org.eclipse.jst.j2ee.tests");
//		pluginIDs.add("org.eclipse.jst.j2ee.tests.performance");
//		pluginIDs.add("org.eclipse.jst.servlet.tests");	

//		for(String pluginID : pluginIDs) {
//			InternalPlatform.getDefault().getLog(InternalPlatform.getDefault().getBundle(pluginID)).addLogListener(JUnitLogListener.INSTANCE);
//		}
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

//	@Override
//	public void run(TestResult result) {
//		try{
//			AssertWarn.clearWarnings();
//			super.run(result);
//			
//		} finally {
//			List <StackTraceElement> stackTrace = new ArrayList<StackTraceElement>();
//			
//			//check to see if this test already failed with an error
//			//if so add error to stack trace
//			Enumeration errorsEnum = result.errors();
//			TestFailure errorOrFailure = null;
//			boolean hadError = false;
//			for(int i = 0; errorsEnum.hasMoreElements() && !hadError; i++) {
//				Object o = errorsEnum.nextElement();
//				errorOrFailure = (TestFailure)o;
//				if(errorOrFailure.failedTest() == this) {
//					hadError = true;
//					
//					Throwable thrown = errorOrFailure.thrownException();
//					List<StackTraceElement> errorTrace = Arrays.asList(thrown.getStackTrace());
//					stackTrace.addAll(errorTrace);
//				}
//			}
//			
//			//check to see if this test already failed with a failure if it didn't
//			// fail with an error. if so add failure to stack trace
//			boolean hadFailure = false;
//			if(!hadError) {
//				Enumeration failuresEnum = result.failures();
//				for(int i = 0; failuresEnum.hasMoreElements() && !hadFailure; i++) {
//					Object o = failuresEnum.nextElement();
//					errorOrFailure = (TestFailure)o;
//					if(errorOrFailure.failedTest() == this) {
//						hadFailure = true;
//						
//						Throwable thrown = errorOrFailure.thrownException();
//						List<StackTraceElement> failureTrace = Arrays.asList(thrown.getStackTrace());
//						stackTrace.addAll(failureTrace);
//					}
//				}
//			}
//			
//			//add any warnings to the stack trace and deal with if this test already failed
//			List <AssertionFailedError> warnings = AssertWarn.getWarnings();
//			boolean hadWarnings = warnings.size() != 0;
//			if(hadWarnings) {
//				int warningNum = 1;
//				List<StackTraceElement> warningTrace = null;
//				for(AssertionFailedError e : warnings){
//					stackTrace.add(new StackTraceElement("---------->","", "WARNING MESSAGE: " + e.getMessage() + ")  (WARNING NUMBER", warningNum++));
//					
//					warningTrace = Arrays.asList(e.getStackTrace());
//					stackTrace.addAll(warningTrace);
//				}
//			}
//			
//			//add any logged statuses to the stack trace, as long as fFailOnLoggedStatus == true
//			HashMap<IStatus,String> loggedStatuses = JUnitLogListener.INSTANCE.getLoggedStatuses();
//			boolean hadLoggedStatuses = loggedStatuses.size() != 0;
//			if(hadLoggedStatuses && fFailOnLoggedStatus) {
//				int loggedMessageNum = 1;
//				List<StackTraceElement> loggedMessageTrace = null;
//				
//				Set<IStatus> loggedStatusesKeys = loggedStatuses.keySet();
//				String statusType;
//				for(IStatus status : loggedStatusesKeys) {
//					stackTrace.add(new StackTraceElement("---------->","", "LOGGED " + statusTypeToString(status.getSeverity()) + " STATUS: " + loggedStatuses.get(status) + ": " + status.getMessage() + ")  (LOGGED STATUS NUMBER", loggedMessageNum++));
//					
//					if(status.getException() != null) {
//						loggedMessageTrace = Arrays.asList(status.getException().getStackTrace());
//						stackTrace.addAll(loggedMessageTrace);
//					}
//				}
//			}
//			
//			//deal with making a new formated failure and added it to the result
//			String message = "";
//			String warningsMessage = "Test had " + warnings.size() + " warnings occur";
//			String loggedStatusesMessage = "Test logged " + loggedStatuses.size() + " statuses";
//			if(hadError || hadFailure) {
//				message = errorOrFailure.exceptionMessage();
//				
//				if(hadWarnings) {
//					message += "  (" + warningsMessage + ")";
//				}
//				
//				if(hadLoggedStatuses && fFailOnLoggedStatus) {
//					message += "  (" + loggedStatusesMessage + ")";
//				}
//			} else if(hadWarnings) {
//				message = warningsMessage;
//				
//				if(hadLoggedStatuses && fFailOnLoggedStatus) {
//					message += "  (" + loggedStatusesMessage + ")";
//				}
//			} else if(hadLoggedStatuses && fFailOnLoggedStatus) {
//				message = loggedStatusesMessage;
//			}
//			
//			if(hadError || hadFailure || hadWarnings || (hadLoggedStatuses && fFailOnLoggedStatus)) {
//				AssertionFailedError error = new AssertionFailedError(message);
//				StackTraceElement[] stackTraceArray = stackTrace.toArray(new StackTraceElement[stackTrace.size()]);
//				error.setStackTrace(stackTraceArray);
//
//				if(hadError) {
//					result.addError(this, error);
//				} else {
//					result.addFailure(this, error);
//				}
//			}
//
//
//		}
//	}
	
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
