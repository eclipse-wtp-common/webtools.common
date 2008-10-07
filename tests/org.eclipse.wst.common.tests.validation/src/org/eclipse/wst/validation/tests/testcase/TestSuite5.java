package org.eclipse.wst.validation.tests.testcase;

import java.io.UnsupportedEncodingException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.ValidationResults;
import org.eclipse.wst.validation.internal.Tracing;

/** Test the new pattern rule. */
public class TestSuite5 extends TestCase {
	
	private TestEnvironment _env;
	private IProject		_project;
	
	public static Test suite() {
		return new TestSuite(TestSuite5.class);
	} 
	
	public TestSuite5(String name){
		super(name);
	}
	

	protected void setUp() throws Exception {
		super.setUp();
		_env = new TestEnvironment();
		_project = _env.createProject("TestSuite5");
		
		IPath first = _env.addFolder(_project.getFullPath(), "first");
		IPath second = _env.addFolder(first, "second");
		IPath third = _env.addFolder(second, "third");
		_env.addFile(third, "sample.test1", 
			"info - information\n" +
			"warning - warning\n" +
			"error - error\n\n" +
			"t1error - extra error\n" +
			"t1warning - extra warning");
		
		TestEnvironment.enableOnlyTheseValidators("T5");
	}
	
	protected void tearDown() throws Exception {
		_project.delete(true, null);
		_env.dispose();
		super.tearDown();
	}
	
	/**
	 * Ensure that the pattern filters are working.
	 */
	public void testPatterns() throws CoreException, UnsupportedEncodingException, InterruptedException {
		Tracing.log("TestSuite5-01: testFacetVersions starting");
		IProgressMonitor monitor = new NullProgressMonitor();		
		ValidationFramework vf = ValidationFramework.getDefault();
		IProject[] projects = new IProject[1];
		projects[0] = _project;
		ValidationResults vr = vf.validate(projects, true, false, monitor);
		int errors = vr.getSeverityError();
		assertEquals("Number of errors", 1, errors);
				
		Tracing.log("TestSuite5-02:testFacetVersions finished");
	}
	


}
