package org.eclipse.wst.validation.tests.testcase;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.validation.IDependencyIndex;
import org.eclipse.wst.validation.MessageSeveritySetting;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.ValidationResults;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.Tracing;
import org.eclipse.wst.validation.internal.ValConstants;
import org.eclipse.wst.validation.internal.ValManager;
import org.eclipse.wst.validation.internal.ValPrefManagerGlobal;
import org.eclipse.wst.validation.tests.Misc;
import org.eclipse.wst.validation.tests.TestValidator;
import org.eclipse.wst.validation.tests.TestValidator2;
import org.eclipse.wst.validation.tests.TestValidator4;
import org.eclipse.wst.validation.tests.TestValidator5D;
import org.eclipse.wst.validation.tests.TestValidator6;
import org.eclipse.wst.validation.tests.TestValidator7;
import org.eclipse.wst.validation.tests.ValCounters;

public class TestSuite1 extends TestCase {
	
	private TestEnvironment _env;
	private IProject		_testProject;
	
	private IFile			_mapTest1;
	
	public static Test suite() {
		return new TestSuite(TestSuite1.class);
	} 
	
	public TestSuite1(String name){
		super(name);
	}
	

	protected void setUp() throws Exception {
		super.setUp();
		_env = new TestEnvironment();
		_testProject = _env.findProject("TestProject");
		if (_testProject != null)return;
		_env.turnoffAutoBuild();
		enableOnlyTestValidators();
		_testProject = _env.createProject("TestProject");
		IPath folder = _env.addFolder(_testProject.getFullPath(), "source");
		_env.addFile(folder, "first.test1", "include map.test1\n" +
			"info - information\n" +
			"warning - warning\n" +
			"error - error\n\n" +
			"t1error - extra error\n" +
			"t1warning - extra warning");
		_env.addFile(folder, "second.test1", "info - information\n" +
			"warning - warning\n" +
			"error - error\n\n" +
			"t1error - extra error\n" +
			"t1warning - extra warning");
		_mapTest1 = _env.addFile(folder, "map.test1", 
			"# will hold future mappings\n\n" +
			"# syntax: map target replacement\n" +
			"# for example map t1error error - would replace all t1error tokens with error");
		_env.addFile(folder, "first.test2", "# sample file");
		_env.addFile(folder, "third.test4", 
			"# Doesn't really matter\n" +
			"# We just want to make the build a bit slower.");
		_env.addFile(folder, "fourth.test4", "# Doesn't really matter");
		_env.addFile(folder, "fifth.test5", "# Doesn't really matter");
	}

	/**
	 * Since other plug-ins can add and remove validators, turn off all the ones that are not part of
	 * these tests.
	 */
	private static void enableOnlyTestValidators() {
		Validator[] vals = ValManager.getDefault().getValidators();
		for (Validator v : vals){
			boolean enable = v.getValidatorClassname().startsWith("org.eclipse.wst.validation.tests.Test");
			v.setBuildValidation(enable);
			v.setManualValidation(enable);
		}
		ValPrefManagerGlobal gp = ValPrefManagerGlobal.getDefault();
		gp.saveAsPrefs(vals);		
	}

	protected void tearDown() throws Exception {
		_env.dispose();
		super.tearDown();
	}
	
	public void testIndex(){
		ValidationFramework vf = ValidationFramework.getDefault();
		IDependencyIndex index = vf.getDependencyIndex();
		assertNotNull(index);
	}
	
	public void testIndex2() throws CoreException, InterruptedException {
		ValidationFramework vf = ValidationFramework.getDefault();
		IDependencyIndex index = vf.getDependencyIndex();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		IResource r = root.findMember("TestProject/source/map.test1");
		IProject p = r.getProject();
		p.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
		ValidationFramework.getDefault().join(null);

		r = root.findMember("TestProject/source/first.test1");
		assertFalse(index.isDependedOn(r));		
	}
	
	public void testGetValidators(){
		ValidationFramework vf = ValidationFramework.getDefault();
		IResource resource = _env.getWorkspace().getRoot().findMember("TestProject/source/first.test1");
		Validator[] validators = vf.getValidatorsFor(resource, false, false);
		assertTrue(validators.length > 0);
		
		String id = TestValidator.id();
		int count = 0;
		for (int i =0; i<validators.length; i++){
			if (validators[i].getId().equals(id))count++;
		}
		assertEquals(1, count);
	}
	
	public void testTest1() throws CoreException, UnsupportedEncodingException, InterruptedException {
		Tracing.log("testTest1 starting");
		IProgressMonitor monitor = new NullProgressMonitor();
		ValidationFramework vf = ValidationFramework.getDefault();
		IProject[] projects = {_testProject};
		/*
		 * After the validation we expect first.test1 to have 1 error, 1 warning and 1 info. And for
		 * second.test1 to have 1 error, 1 warning and 1 info. 
		 */
		ValidationResults vr = vf.validate(projects, true, false, monitor);
		
		IResource resource = _env.getWorkspace().getRoot().findMember("TestProject/source/first.test1");
		checkFirstPass(resource, vr);
		
		// add a first build so that we know that only the map file has changed
		_env.incrementalBuild();
		Thread.sleep(2000);
		vf.join(monitor);
		
		ByteArrayInputStream in = new ByteArrayInputStream("map t1error error\nmap t1warning warning".getBytes());
		_mapTest1.setContents(in, true, true, monitor);
		
		TestValidator4.getCounters().reset();
		TestValidator5D.getCounters().reset();
		_env.incrementalBuild();
		Thread.sleep(2000);
		vf.join(monitor);
		
		ValCounters vc = TestValidator4.getCounters();
		Tracing.log("testTest1: " + vc.toString());
		assertEquals(vc.startingCount, vc.finishedCount);
		assertEquals(vc.startingProjectCount, vc.finishedProjectCount);
		assertEquals(vc.startingCount, 1);
		assertEquals(vc.finishedCount, 1);
		
		vc = TestValidator5D.getCounters();
		assertEquals(vc.startingCount, vc.finishedCount);
		assertEquals(vc.startingProjectCount, vc.finishedProjectCount);
		assertEquals(vc.startingCount, 1);
		assertEquals(vc.finishedCount, 1);
				
		assertTrue("We expect the delegating validator Test5D to be called at least once", 
			TestValidator5D.getCalledCount()>0);
		
		checkSecondPass(resource);	
		Tracing.log("testTest1 finished");
	}
	
	public void testTest2() {
		ValidationFramework vf = ValidationFramework.getDefault();
		IResource test2 = _testProject.findMember("source/first.test2");
		assertNotNull(test2);
		Validator[] vals = vf.getValidatorsFor(test2, true, true);
		for (Validator v : vals){
			String id = v.getId();
			if (id.equals(TestValidator.id()))fail("first.test2 should not be validated by the test1 validator");
		}
		
		IResource test1 = _testProject.findMember("source/first.test1");
		assertNotNull(test1);
		vals = vf.getValidatorsFor(test1, true, true);
		boolean found = false;
		for (Validator v : vals){
			String id = v.getId();
			if (id.equals(TestValidator.id()))found = true;
		}
		assertTrue(found);
	}
	
	/**
	 * Test if we can get a message that was defined through the extension point.
	 */
	public void testMessages() {
		ValidationFramework vf = ValidationFramework.getDefault();
		Validator v = vf.getValidator(TestValidator2.id(), null);
		assertNotNull("We expected to find TestValidator2", v);
		MessageSeveritySetting ms = v.getMessage("bad");
		assertNotNull("We expected to find a message for 'bad'", ms);
		assertEquals(MessageSeveritySetting.Severity.Error, ms.getCurrent());
		assertEquals(4, v.getMessageSettings().size());
	}
	
	public void testSuspend() throws CoreException, InterruptedException {
		ValidationFramework vf = ValidationFramework.getDefault();
		long start = System.currentTimeMillis();
		_env.fullBuild();
		
		vf.join(null);
		long first = System.currentTimeMillis();
		long valBuild = first-start;
		assertTrue("We expect the build to take longer than 3s, but it completed in " + valBuild + "ms", valBuild > 3000);
		
		Validator v = vf.getValidator(TestValidator6.id(), null);
		TestValidator6 t6 = (TestValidator6)v.asV2Validator().getValidator();
		IResource projectFile = _testProject.findMember(".project");
		assertFalse("We should not be validating the .product file", t6.getSet().contains(projectFile));
		
		v = vf.getValidator(TestValidator7.id(), null);
		TestValidator7 t7 = (TestValidator7)v.asV2Validator().getValidator();
		assertEquals("We expected the validation to be suspended after the first call", 1, t7.getSet().size());
		
		vf.suspendAllValidation(true);
		_env.fullBuild();
		vf.join(null);
		long second = System.currentTimeMillis();
		vf.suspendAllValidation(false);
		long novalBuild = second - first;
		assertTrue("We except the build to go faster with validation turned off, but it was " + (novalBuild-valBuild) +
				" ms faster" , novalBuild < valBuild);

	}
	
	private void checkFirstPass(IResource resource, ValidationResults vr) throws CoreException {
		assertTrue("We expect there to be exactly two error messages, but errors=" + vr.getSeverityError(), vr.getSeverityError() == 2);
		assertTrue("We expect there to be exactly two warning messages, but warnings=" + vr.getSeverityWarning(), vr.getSeverityWarning() == 2);
		assertTrue("We expect there to be exactly two info messages, but info=" + vr.getSeverityInfo(), vr.getSeverityInfo() == 2);
		
		assertTrue("We expect six messages, but got back: "+vr.getMessages().length , vr.getMessages().length == 6);
		
		IMarker[] markers = resource.findMarkers(ValConstants.ProblemMarker, false, IResource.DEPTH_ZERO);
		int errors =0, warnings=0, info=0;
		for (IMarker marker : markers){
			int severity = marker.getAttribute(IMarker.SEVERITY, -1);
			switch (severity){
				case IMarker.SEVERITY_ERROR: errors++;
				break;
				case IMarker.SEVERITY_WARNING: warnings++;
				break;
				case IMarker.SEVERITY_INFO: info++;
				break;
			}
		}
		assertTrue("We expect there to be exactly one error message, but errors=" + errors, errors == 1);
		assertTrue("We expect there to be exactly one warning message, but warnings="+warnings, warnings == 1);
		assertTrue("We expect there to be exactly one info message, but info="+info, info == 1);
	}

	private void checkSecondPass(IResource resource) throws CoreException {
		IMarker[] markers = resource.findMarkers(ValConstants.ProblemMarker, false, IResource.DEPTH_ZERO);
		int errors =0, warnings=0, info=0;
		for (int i=0; i<markers.length; i++){
			int severity = markers[i].getAttribute(IMarker.SEVERITY, -1);
			switch (severity){
				case IMarker.SEVERITY_ERROR: errors++;
				break;
				case IMarker.SEVERITY_WARNING: warnings++;
				break;
				case IMarker.SEVERITY_INFO: info++;
				break;
			}
		}
		if (Tracing.isLogging()){
			Tracing.log("checkSecondPass: " + Misc.listMarkers(resource));
		}
		assertTrue("We expect there to be exactly two error messages, but errors=" + errors, errors == 2);
		assertTrue("We expect there to be exactly two warning messages, but warnings="+warnings, warnings == 2);
		assertTrue("We expect there to be exactly one info message, but info="+info, info == 1);
	}

}
