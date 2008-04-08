package org.eclipse.wst.validation.tests.testcase;

import java.io.UnsupportedEncodingException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.Tracing;
import org.eclipse.wst.validation.internal.ValManager;
import org.eclipse.wst.validation.internal.ValPrefManagerGlobal;

public class TestSuite3 extends TestCase {
	
	private TestEnvironment _env;
	private IProject		_testProject;
	
	private IResource	_folder;
	private IFile		_firstTest1;
	
	public static Test suite() {
		return new TestSuite(TestSuite3.class);
	} 
	
	public TestSuite3(String name){
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
		ResourcesPlugin.getWorkspace().getRoot().findMember(folder);
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
		_env.addFile(folder, "map.test1", 
			"# will hold future mappings\n\n" +
			"# syntax: map target replacement\n" +
			"# for example map t1error error - would replace all t1error tokens with error");
		_env.addFile(folder, "first.test2", "# sample file");
		_env.addFile(folder, "third.test4", 
			"# Doesn't really matter\n" +
			"# We just want to make the build a bit slower.");
		_env.addFile(folder, "fourth.test4", "# Doesn't really matter");
		_env.addFile(folder, "fifth.test5", "# Doesn't really matter");
		
		folder = _env.addFolder(_testProject.getFullPath(), FileNames.disabled);
		_folder = ResourcesPlugin.getWorkspace().getRoot().findMember(folder);
		_firstTest1 = _env.addFile(folder, "first.test1", "include map.test1\n" +
				"info - information\n" +
				"warning - warning\n" +
				"error - error\n\n" +
				"t1error - extra error\n" +
				"t1warning - extra warning");

	}
	
	/**
	 * Since other plug-ins can add and remove validators, turn off all the ones that are not part of
	 * these tests.
	 */
	private static void enableOnlyTestValidators() {
		Validator[] vals = ValManager.getDefault().getValidatorsCopy();
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
	
	public void testTest1() throws CoreException, UnsupportedEncodingException, InterruptedException {
		Tracing.log("TestSuite3-01: testTest1 starting");
		IProgressMonitor monitor = new NullProgressMonitor();		
		ValidationFramework vf = ValidationFramework.getDefault();
		
		vf.disableValidation(_folder);		
		_env.fullBuild(monitor);
		IMarker[] markers = _firstTest1.findMarkers(null, true, IResource.DEPTH_ZERO);
		assertEquals("The file should not have been validated.", 0, markers.length);
		
		vf.enableValidation(_folder);
		_env.fullBuild(monitor);		
		markers = _firstTest1.findMarkers(null, true, IResource.DEPTH_ZERO);
		assertTrue("The file should have some markers", markers.length > 0);
		
		vf.disableValidation(_folder);				
		_env.fullBuild(monitor);
		markers = _firstTest1.findMarkers(null, true, IResource.DEPTH_ZERO);
		assertEquals("The file should not have been validated.", 0, markers.length);
		
		Tracing.log("TestSuite3-02:testTest1 finished");
	}
	


}
