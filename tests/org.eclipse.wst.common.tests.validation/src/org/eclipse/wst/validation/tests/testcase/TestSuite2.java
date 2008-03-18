package org.eclipse.wst.validation.tests.testcase;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.ValConstants;
import org.eclipse.wst.validation.internal.ValManager;
import org.eclipse.wst.validation.internal.ValPrefManagerGlobal;

public class TestSuite2 extends TestCase {
	
	private TestEnvironment _env;
	private IProject		_testProject;
	
	private IFile			_firstTest1;
	private IFile			_secondTest1;
	
	private IFile			_firstTest2x;
	
	public static Test suite() {
		return new TestSuite(TestSuite2.class);
	} 
	
	public TestSuite2(String name){
		super(name);
	}
	

	protected void setUp() throws Exception {
		super.setUp();
		_env = new TestEnvironment();
		_testProject = _env.findProject("TestProject");
		if (_testProject != null)return;
		_env.turnoffAutoBuild();
		turnoffOtherValidators();
		_testProject = _env.createProject("TestProject");
		IPath folder = _env.addFolder(_testProject.getFullPath(), "source");
		_firstTest1 = _env.addFile(folder, "first.test1", "include map.test1\ninfo - information\nwarning - warning\nerror - error\n\n" +
		"t1error - extra error\nt1warning - extra warning");
		_secondTest1 = _env.addFile(folder, "second.test1", "info - information\nwarning - warning\nerror - error\n\n" +
			"t1error - extra error\nt1warning - extra warning");
		_env.addFile(folder, "map.test1", "# will hold future mappings");
		_env.addFile(folder, "first.test2", "# sample file");
		_firstTest2x = _env.addFile(folder, "first.test2x", "# a file that will be validated as a side effect");
		_env.addFile(folder, "third.test4", "# Doesn't really matter\nWe just want to make the build a bit slower.");
		_env.addFile(folder, "fourth.test4", "# Doesn't really matter");
		_env.addFile(folder, "fifth.test5", "# Doesn't really matter");
	}

	/**
	 * Since other plug-ins can add and remove validators, turn off all the ones that are not part of
	 * these tests.
	 */
	private static void turnoffOtherValidators() {
		Validator[] vals = ValManager.getDefault().getValidators();
		for (Validator v : vals){
			boolean validateIt = v.getValidatorClassname().startsWith("org.eclipse.wst.validation.tests.T1");
			v.setBuildValidation(validateIt);
			v.setManualValidation(validateIt);
		}
		ValPrefManagerGlobal gp = ValPrefManagerGlobal.getDefault();
		gp.saveAsPrefs(vals);		
	}

	protected void tearDown() throws Exception {
		_env.dispose();
		super.tearDown();
	}
	
	public void testFullBuild() throws CoreException, InterruptedException {
		ValidationFramework vf = ValidationFramework.getDefault();
//		Listener listener = new Listener(_firstTest2x);
//		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		try {
//			workspace.addResourceChangeListener(listener);
			_env.fullBuild();
			IProgressMonitor monitor = new NullProgressMonitor();
			
			vf.join(monitor);
			
			_env.turnOnAutoBuild();
			
			_firstTest1.touch(monitor);
			_secondTest1.touch(monitor);
			vf.join(monitor);
			
			checkClear();
		}
		finally {
//			workspace.removeResourceChangeListener(listener);
		}
	}
	
	/**
	 * Check if the clear function worked.
	 */
	private void checkClear() throws CoreException {
		IMarker[] markers = _firstTest2x.findMarkers(ValConstants.ProblemMarker, false, IResource.DEPTH_ZERO);
		assertEquals(1, markers.length);
	}
	
	public static class Listener implements IResourceChangeListener {
		
		private IResource _interested;
		
		public Listener(IResource resource){
			_interested = resource;
		}

		public void resourceChanged(IResourceChangeEvent event) {
			IMarkerDelta[] markers = event.findMarkerDeltas(ValConstants.ProblemMarker, false);
			for (IMarkerDelta marker : markers){
				IResource resource = marker.getResource();
				if (_interested.equals(resource)){
					// added=1, removed=2, changed=4
//					int kind = marker.getKind();
//					Map map = marker.getAttributes();
//					int len = map.size();
				}
			}
		}
		
	}

}
