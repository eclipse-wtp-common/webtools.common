package org.eclipse.wst.validation.tests.testcase;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.common.project.facet.core.FacetedProjectFramework;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.validation.IMutableValidator;
import org.eclipse.wst.validation.MutableWorkspaceSettings;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.ValidationResults;
import org.eclipse.wst.validation.internal.Tracing;

public class TestSuite4 extends TestCase {
	
	private TestEnvironment _env;
	private IProject		_project;
	
	public static Test suite() {
		return new TestSuite(TestSuite4.class);
	} 
	
	public TestSuite4(String name){
		super(name);
	}
	

	protected void setUp() throws Exception {
		super.setUp();
		_env = new TestEnvironment();
		IFacetedProjectWorkingCopy project = FacetedProjectFramework.createNewProject();
		project.setProjectName("FacetProject");
		IProjectFacet pf = ProjectFacetsManager.getProjectFacet("java");
		IProjectFacetVersion ipv = pf.getVersion("1.5");
		Set<IProjectFacetVersion> set = new HashSet<IProjectFacetVersion>(1);
		set.add(ipv);
		project.setProjectFacets(set);
		project.commitChanges(null);
		_project = project.getProject();
		
		IPath folder = _env.addFolder(_project.getFullPath(), "source");
		ResourcesPlugin.getWorkspace().getRoot().findMember(folder);
		_env.addFile(folder, "first.test1", 
			"info - information\n" +
			"warning - warning\n" +
			"error - error\n\n" +
			"t1error - extra error\n" +
			"t1warning - extra warning");
		
		enableOnlyT4Validators();
	}
	
	/**
	 * Since other plug-ins can add and remove validators, turn off all the ones that are not part of
	 * these tests.
	 */
	private static void enableOnlyT4Validators() throws InvocationTargetException {
		ValidationFramework vf = ValidationFramework.getDefault();
		MutableWorkspaceSettings ws = vf.getWorkspaceSettings();
		for (IMutableValidator v : ws.getValidators()){
			boolean enable = v.getValidatorClassname().startsWith("org.eclipse.wst.validation.tests.T4");
			v.setBuildValidation(enable);
			v.setManualValidation(enable);
		}
		vf.applyChanges(ws, true);
	}

	protected void tearDown() throws Exception {
		_project.delete(true, null);
		_env.dispose();
		super.tearDown();
	}
	
	/**
	 * Ensure that the facet version expressions are working. We define two validators T4A and T4B. The first one operates on Java 5
	 * and the second on Java 6. Since we have created a project that has been set to Java 5, we only expect the T4A validator
	 * to be called. 
	 */
	public void testFacetVersions() throws CoreException, UnsupportedEncodingException, InterruptedException {
		Tracing.log("TestSuite4-01: testFacetVersions starting");
		IProgressMonitor monitor = new NullProgressMonitor();		
		ValidationFramework vf = ValidationFramework.getDefault();
		IProject[] projects = new IProject[1];
		projects[0] = _project;
		ValidationResults vr = vf.validate(projects, true, false, monitor);
		int errors = vr.getSeverityError();
		assertEquals("Number of errors", 1, errors);
				
		Tracing.log("TestSuite4-02:testFacetVersions finished");
	}
	


}
