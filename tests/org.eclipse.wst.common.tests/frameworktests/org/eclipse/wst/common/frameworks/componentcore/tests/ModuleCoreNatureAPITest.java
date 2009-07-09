package org.eclipse.wst.common.frameworks.componentcore.tests;

import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.etools.common.test.apitools.ProjectUnzipUtil;
import org.eclipse.jem.util.emf.workbench.EMFWorkbenchContextBase;
import org.eclipse.wst.common.componentcore.ModuleCoreNature;
import org.eclipse.wst.common.componentcore.UnresolveableURIException;
import org.eclipse.wst.common.componentcore.internal.ArtifactEditModel;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModel;
import org.eclipse.wst.common.tests.CommonTestsPlugin;

public class ModuleCoreNatureAPITest extends TestCase {

	public static String fileSep = System.getProperty("file.separator");
	public static final String PROJECT_NAME = "TestArtifactEdit";
	public static final String WEB_MODULE_NAME = "WebModule1";
	public static final URI moduleURI = URI.createURI("module:/resource/TestArtifactEdit/WebModule1");
	public static final String EDIT_MODEL_ID = "jst.web";
	private Path zipFilePath = new Path("TestData" + fileSep + "TestArtifactEdit.zip");
	private IProject project;


	// /This should be extracted out, dont have time, just trying to get coverage
	// for m4 integration....

	protected void setUp() throws Exception {
		if (!getTargetProject().exists())
			if (!createProject())
				fail();
		project = getTargetProject();
	}


	public IProject getTargetProject() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
	}

	public boolean createProject() {
		IPath localZipPath = getLocalPath();
		ProjectUnzipUtil util = new ProjectUnzipUtil(localZipPath, new String[]{PROJECT_NAME});
		return util.createProjects();
	}

	private IPath getLocalPath() {
		URL url = CommonTestsPlugin.instance.find(zipFilePath);
		try {
			url = Platform.asLocalURL(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Path(url.getPath());
	}


	public void testConfigure() {
		ModuleCoreNature nature = ModuleCoreNature.getModuleCoreNature(project);
		try {
			nature.configure();
		} catch (CoreException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void testGetModuleCoreNature() {
		new ModuleCoreNature();
		ModuleCoreNature nature = ModuleCoreNature.getModuleCoreNature(project);
		assertNotNull(nature);
	}



	public void testGetModuleStructuralModelForRead() {
		ModuleCoreNature nature = ModuleCoreNature.getModuleCoreNature(project);
		nature.getModuleStructuralModelForRead(this);
		ArtifactEditModel model = nature.getArtifactEditModelForRead(moduleURI, this);
		assertNotNull(model);
	}

	public void testGetModuleStructuralModelForWrite() {
		ModuleCoreNature nature = ModuleCoreNature.getModuleCoreNature(project);
		ArtifactEditModel model = nature.getArtifactEditModelForWrite(moduleURI, this);
		assertNotNull(model);
	}

	public void testGetArtifactEditModelForRead() {
		ModuleCoreNature nature = ModuleCoreNature.getModuleCoreNature(project);
		EditModel model = nature.getEditModelForRead(EDIT_MODEL_ID, this);
		assertNotNull(model);
	}

	public void testGetArtifactEditModelForWrite() {
		ModuleCoreNature nature = ModuleCoreNature.getModuleCoreNature(project);
		EditModel model = nature.getEditModelForWrite(EDIT_MODEL_ID, this);
		assertNotNull(model);
	}

	/*
	 * Class under test for String getNatureID()
	 */
	public void testGetNatureID() {
		ModuleCoreNature nature = ModuleCoreNature.getModuleCoreNature(project);
		String id = nature.getNatureID();
		assertTrue(id.equals(IModuleConstants.MODULE_NATURE_ID));
	}

	public void testPrimaryContributeToContext() {
		ModuleCoreNature nature = ModuleCoreNature.getModuleCoreNature(project);
		EMFWorkbenchContextBase context = new EMFWorkbenchContextBase(project);
		try {
			nature.primaryContributeToContext(context);
		} catch (Exception e) {
			fail();
		}

	}

	/*
	 * Class under test for ResourceSet getResourceSet()
	 */
	public void testGetResourceSet() {
		ModuleCoreNature nature = ModuleCoreNature.getModuleCoreNature(project);
		ResourceSet set = nature.getResourceSet();
		assertNotNull(set);
	}

	public void testSecondaryContributeToContext() {
		ModuleCoreNature nature = ModuleCoreNature.getModuleCoreNature(project);
		EMFWorkbenchContextBase context = new EMFWorkbenchContextBase(project);
		try {
			nature.secondaryContributeToContext(context);
		} catch (Exception e) {
			fail();
		}
	}

	/*
	 * Class under test for String getPluginID()
	 */
	public void testGetPluginID() {
		// protected cant test unless in same package....
		ModuleCoreNature nature = ModuleCoreNature.getModuleCoreNature(project);
		
	}

	public void testAddModuleCoreNatureIfNecessary() {
		try {
			project.getDescription().setNatureIds(new String[0]);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		ModuleCoreNature.addModuleCoreNatureIfNecessary(project, new NullProgressMonitor());
		ModuleCoreNature nature = ModuleCoreNature.getModuleCoreNature(project);
		assertNotNull(nature);
	}
	
	public void testUnresolveableURIException() {
		UnresolveableURIException uriEx =   new UnresolveableURIException(moduleURI);
		assertNotNull(uriEx);
	}
	



}
