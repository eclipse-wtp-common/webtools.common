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
import org.eclipse.wst.validation.internal.Tracing;
import org.eclipse.wst.validation.tests.T7A;
import org.eclipse.wst.validation.tests.T7A.ValEntryPoint;

/** Test the order of validation events. */
public class TestSuite7 extends TestCase {
	
	private TestEnvironment _env;
	private IProject		_projectA;
	private IProject		_projectB;
	
	public static Test suite() {
		return new TestSuite(TestSuite7.class);
	} 
	
	public TestSuite7(String name){
		super(name);
	}
	

	protected void setUp() throws Exception {
		super.setUp();
		TestEnvironment.enableOnlyThisValidator("org.eclipse.wst.validation.tests.T7A");
		_env = new TestEnvironment();
		_projectA = _env.createProject("TestSuite7a");
		_projectB = _env.createProject("TestSuite7b");
		makeFiles(_projectA);
		makeFiles(_projectB);
		
	}
	
	private void makeFiles(IProject project) throws Exception{
		IPath first = _env.addFolder(project.getFullPath(), "some-folder");
		_env.addFile(first, "first.t7a",	"# a dummy file");		
		_env.addFile(first, "second.t7a", "# a dummy file");
		
	}
	
	protected void tearDown() throws Exception {
		_projectA.delete(true, null);
		_projectB.delete(true, null);
		_env.dispose();
		super.tearDown();
	}
	
	/**
	 * Test the order of a clean build.
	 */
	public void testClean() throws CoreException, UnsupportedEncodingException, InterruptedException {
		Tracing.log("TestSuite7-01: testClean starting");
		IProgressMonitor monitor = new NullProgressMonitor();
		_env.turnOnAutoBuild();
		_env.cleanBuild(monitor);

		T7A.resetList();
		
		_env.cleanBuild(monitor);
		ValEntryPoint[] array = T7A.getArray();
		int start = 0;
		int finish = 0;
		for (ValEntryPoint vep : array){
			switch (vep.getType()){
			case Starting:
				if (start == 0)assertNull("First starting entry must be null", vep.getResource());
				start++;
				break;
			case Finishing:
				finish++;
				break;		
			case Normal:
				assertEquals("All normal validation events must be two levels deep", 2, start-finish);
			}
		}
		assertEquals("Starting must equal finishing", start, finish);
		assertNull("Last entry must be null", array[array.length-1].getResource());
		
		Tracing.log("TestSuite7-02:testClean finished");
	}
	


}
