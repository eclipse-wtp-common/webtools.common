package org.eclipse.wst.validation.tests.testcase;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.validation.IMutableValidator;
import org.eclipse.wst.validation.MutableProjectSettings;
import org.eclipse.wst.validation.MutableWorkspaceSettings;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.ValidationResults;
import org.eclipse.wst.validation.internal.Tracing;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;
import org.osgi.framework.Bundle;

/** 
 * Test the transient settings support. That is the ability to change validator settings without changing
 * the property files.
 */
public class TestSuite8 extends TestCase {
	
	private TestEnvironment _env;
	private IProject		_project;

	
	public static Test suite() {
		return new TestSuite(TestSuite8.class);
	} 
	
	public TestSuite8(String name){
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		_env = new TestEnvironment();
		_env.turnoffAutoBuild();
		_project = _env.createProject("TestSuite8");
		IPath first = _env.addFolder(_project.getFullPath(), "some-folder");
		_env.addFile(first, "first.t6a", "error - one error line");		
	}
	
	protected void tearDown() throws Exception {
		_project.delete(true, null);
		_env.dispose();
		super.tearDown();
	}

	
	/**
	 * Test the order of a clean build.
	 */
	public void testPrefs() throws CoreException, UnsupportedEncodingException, InterruptedException, InvocationTargetException {
		Tracing.log("TestSuite8-01: testClean starting");
		
		Bundle bundle = Platform.getBundle("org.eclipse.core.runtime");
		IPath path = Platform.getStateLocation(bundle);
		path = path.append(".settings/"+ValidationPlugin.PLUGIN_ID+".prefs");
		File file = path.toFile();
		long lastModified = file.lastModified();
		
		ValidationFramework vf = ValidationFramework.getDefault();
		MutableWorkspaceSettings mws = vf.getWorkspaceSettings();
		IMutableValidator[] validators = mws.getValidators();
		assertTrue("There must be sime validators defined", validators.length > 0);
		for (IMutableValidator val : validators){
			val.setBuildValidation(false);
			val.setManualValidation(true);
		}
		vf.applyChanges(mws, false);
		assertEquals("The global preference file should not have changed", lastModified, file.lastModified());
		
		IProgressMonitor monitor = new NullProgressMonitor();
		ValidationResults vr = vf.validate(new IProject[]{_project}, true, false, monitor);
		assertEquals("We expect exactly one error", 1, vr.getSeverityError());
		
		mws = vf.getWorkspaceSettings();
		validators = mws.getValidators();
		for (IMutableValidator val : validators){
			assertEquals("We expected " + val.getName() + " to have build turned off", false, val.isBuildValidation());
			assertEquals("We expected " + val.getName() + " to have manual turned on", true, val.isManualValidation());
			
			val.setBuildValidation(true);
			val.setManualValidation(false);
		}
		vf.applyChanges(mws, true);
		assertTrue("The global preference file should have changed", lastModified < file.lastModified());
		
		vr = vf.validate(new IProject[]{_project}, true, false, monitor);
		assertEquals("The validator is off, there should be no errors", 0, vr.getSeverityError());
		
		projectTest();
				
		Tracing.log("TestSuite8-02:testClean finished");
	}

	private void projectTest() throws CoreException {
		long lastModified = lastModifiedForProject();
		
		ValidationFramework vf = ValidationFramework.getDefault();
		
		MutableProjectSettings mps = vf.getProjectSettings(_project);
		assertFalse("By default, projects can not override workspace settings", mps.getOverride());
		
		mps.setOverride(true);
		vf.applyChanges(mps, false);
		
		IMutableValidator[] validators = mps.getValidators();
		assertTrue("There must be sime validators defined", validators.length > 0);
		for (IMutableValidator val : validators){
			val.setBuildValidation(false);
			val.setManualValidation(true);
		}
		vf.applyChanges(mps, false);
		assertEquals("The project preference file should not have changed", lastModified, lastModifiedForProject());
		
		IProgressMonitor monitor = new NullProgressMonitor();
		ValidationResults vr = vf.validate(new IProject[]{_project}, true, false, monitor);
		assertEquals("We expect exactly one error", 1, vr.getSeverityError());
		
		mps = vf.getProjectSettings(_project);
		validators = mps.getValidators();
		for (IMutableValidator val : validators){
			assertEquals("We expected " + val.getName() + " to have build turned off", false, val.isBuildValidation());
			assertEquals("We expected " + val.getName() + " to have manual turned on", true, val.isManualValidation());
			
			val.setBuildValidation(true);
			val.setManualValidation(false);
		}
		vf.applyChanges(mps, true);
		assertTrue("The project preference file should have changed", lastModified < lastModifiedForProject());
		
		vr = vf.validate(new IProject[]{_project}, true, false, monitor);
		assertEquals("The validator is off, there should be no errors", 0, vr.getSeverityError());

	}
	
	private long lastModifiedForProject(){
		long lastModified = 0;
		IResource prefs = _project.findMember(".settings/" + ValidationPlugin.PLUGIN_ID + ".prefs");
		if (prefs != null){
			File pf = prefs.getLocation().toFile();
			lastModified = pf.lastModified();
		}
		return lastModified;
	}
	
}
