package org.eclipse.wst.common.frameworks.componentcore.tests;

import junit.framework.Test;
import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.ModuleCoreNature;
import org.eclipse.wst.common.componentcore.internal.ComponentResource;
import org.eclipse.wst.common.componentcore.internal.ComponentType;
import org.eclipse.wst.common.componentcore.internal.ProjectComponents;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.frameworks.componentcore.virtualpath.tests.TestWorkspace;
import org.eclipse.wst.common.tests.SimpleTestSuite;

public class StructureEditAPITest extends TestCase {
	public static String fileSep = System.getProperty("file.separator");
	public static final String PROJECT_NAME = "TestArtifactEdit";
	public static final String WEB_MODULE_NAME = "WebModule1";
	public static final URI moduleURI = URI.createURI("module:/resource/TestArtifactEdit/WebModule1");
	public static final String EDIT_MODEL_ID = "jst.web";
	public static final String EDITMODEL_STRESS = "stresstest";
	private Path zipFilePath = new Path("TestData" + fileSep + "TestArtifactEdit.zip");
	private IProject project;

	public static Test suite() {
		return new SimpleTestSuite(StructureEditAPITest.class);
	}

	// /This should be extracted out, dont have time, just trying to get coverage
	// for m4 integration....

	protected void setUp() throws Exception {
		TestWorkspace.init();
		project = TestWorkspace.getTargetProject();
	}

	public IProject getTargetProject() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
	}

	public void testGetStructureEditForRead() throws Exception {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForRead(project);
			assertNotNull(moduleCore);
		} finally {
			moduleCore.dispose();
		}
	}

	public void testGetStructureEditForWrite() throws Exception{
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			assertNotNull(moduleCore);
		} finally {
			moduleCore.dispose();
		}
	}

	public void testGetModuleCoreNature() throws Exception{
		ModuleCoreNature nature = StructureEdit.getModuleCoreNature(moduleURI);
		assertNotNull(nature);
	}

	/*
	 * Class under test for IProject getContainingProject(WorkbenchComponent)
	 */
	public void testGetContainingProjectWorkbenchComponent() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForRead(project);
			assertNotNull(moduleCore);
			WorkbenchComponent wbComponent = moduleCore.getComponent();
			assertNotNull(wbComponent);
			IProject aProject = StructureEdit.getContainingProject(wbComponent);
			assertNotNull(aProject);
		} finally {
			moduleCore.dispose();
		}
	}

	/*
	 * Class under test for IProject getContainingProject(URI)
	 */
	public void testGetContainingProjectURI() throws Exception {
		IProject aProject = StructureEdit.getContainingProject(moduleURI);
		assertNotNull(aProject);
	}

	/**
	 * 
	 */
//	public void testGetEclipseResource() {
//		StructureEdit moduleCore = null;
//
//		try {
//			moduleCore = StructureEdit.getStructureEditForRead(project);
//			WorkbenchComponent wbComponent = moduleCore.findComponentByName(WEB_MODULE_NAME);
//			ComponentResource componentResource = wbComponent.findResourcesByRuntimePath(new Path("/TestArtifactEdit/WebModule1"))[0];
//			moduleCore.getEclipseResource(componentResource);
//		} finally {
//			if (moduleCore != null) {
//				moduleCore.dispose();
//
//			}
//			assertNotNull(moduleCore);
//
//		}
//	}

	/**
	 * 
	 */
//	public void testGetOutputContainerRoot() {
//		StructureEdit moduleCore = null;
//		try {
//			moduleCore = StructureEdit.getStructureEditForRead(project);
//			WorkbenchComponent wbComponent = moduleCore.findComponentByName(WEB_MODULE_NAME);
//			ComponentResource componentResource = wbComponent.findResourcesByRuntimePath(new Path("/TestArtifactEdit/WebModule1"))[0];
//			StructureEdit.getOutputContainerRoot(wbComponent);
//		} finally {
//			if (moduleCore != null) {
//				moduleCore.dispose();
//
//			}
//			assertNotNull(moduleCore);
//		}
//	}

	/**
	 * 
	 */
//	public void testGetOutputContainersForProject() {
//		StructureEdit moduleCore = null;
//		try {
//			moduleCore = StructureEdit.getStructureEditForRead(project);
//			WorkbenchComponent wbComponent = moduleCore.findComponentByName(WEB_MODULE_NAME);
//			ComponentResource componentResource = wbComponent.findResourcesByRuntimePath(new Path("/TestArtifactEdit/WebModule1"))[0];
//			StructureEdit.getOutputContainersForProject(project);
//		} finally {
//			if (moduleCore != null) {
//				moduleCore.dispose();
//
//			}
//			assertNotNull(moduleCore);
//		}
//	}

	/**
	 * 
	 */
	public void testGetDeployedName() throws Exception{
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForRead(project);
			assertNotNull(moduleCore);
			WorkbenchComponent wbComponent = moduleCore.getComponent();
			assertNotNull(wbComponent);
			String deployName = StructureEdit.getDeployedName(moduleURI);
			assertNotNull(deployName);
		} finally {
			moduleCore.dispose();
		}
	}

	/**
	 * 
	 */
	public void testGetComponentType() {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForRead(project);
			assertNotNull(moduleCore);
			WorkbenchComponent wbComponent = moduleCore.getComponent();
			assertNotNull(wbComponent);
			ComponentType type = StructureEdit.getComponentType(ComponentCore.createComponent(project,wbComponent.getName()));
			assertNotNull(type);
		} finally {
			moduleCore.dispose();
		}
	}

	public void testSave() {

		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			assertNotNull(moduleCore);
			moduleCore.save(new NullProgressMonitor());
		} finally {
			moduleCore.dispose();
		}
	}

	public void testSaveIfNecessary() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			assertNotNull(moduleCore);
			moduleCore.saveIfNecessary(new NullProgressMonitor());
		} finally {
			moduleCore.dispose();
		}
	}

	public void testPrepareProjectComponentsIfNecessary() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			assertNotNull(moduleCore);
			moduleCore.prepareProjectComponentsIfNecessary();
		} finally {
			moduleCore.dispose();
		}
	}

	public void testGetComponentModelRoot() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			assertNotNull(moduleCore);
			ProjectComponents projectComponents = moduleCore.getComponentModelRoot();
			assertNotNull(projectComponents);
		} finally {
			moduleCore.dispose();
		}
	}

	public void testGetSourceContainers() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			assertNotNull(moduleCore);
			WorkbenchComponent wbComponent = moduleCore.getComponent();
			ComponentResource[] containers = moduleCore.getSourceContainers(wbComponent);
			assertNotNull(containers);

		} finally {
			moduleCore.dispose();
		}
	}

	public void testGetWorkbenchModules() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			assertNotNull(moduleCore);
			WorkbenchComponent wbComponent = moduleCore.getComponent();
			assertNotNull(wbComponent);
			WorkbenchComponent[] components = moduleCore.getWorkbenchModules();
			assertNotNull(components);
			assertTrue(components.length > 0);
		} finally {
			moduleCore.dispose();
		}
	}

	public void testCreateWorkbenchModule() {

		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			assertNotNull(moduleCore);
			WorkbenchComponent wbComponent = moduleCore.getComponent();
			assertNotNull(wbComponent);
			wbComponent = moduleCore.createWorkbenchModule("test");
			assertNotNull(wbComponent);
		} finally {
			moduleCore.dispose();
		}
	}


	public void testCreateWorkbenchModuleResource() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			assertNotNull(moduleCore);
			WorkbenchComponent wbComponent = moduleCore.getComponent();
			assertNotNull(wbComponent);
			ComponentResource resource = moduleCore.createWorkbenchModuleResource(project.getFile("WebModule1/NewFolder"));
			assertNotNull(resource);
		} finally {
			moduleCore.dispose();
		}
	}

	public void testCreateModuleType() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			assertNotNull(moduleCore);
			WorkbenchComponent wbComponent = moduleCore.getComponent();
			assertNotNull(wbComponent);
			ComponentType type = moduleCore.createModuleType(EDIT_MODEL_ID);
			assertNotNull(type);
		} finally {
			moduleCore.dispose();
		}
	}


	/*
	 * Class under test for ComponentResource[] findResourcesByRuntimePath(URI, URI)
	 */
	public void testFindResourcesByRuntimePathURIURI() throws Exception{
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			assertNotNull(moduleCore);
			WorkbenchComponent wbComponent = moduleCore.getComponent();
			assertNotNull(wbComponent);
			ComponentResource[] resources = moduleCore.findResourcesByRuntimePath(moduleURI);
			assertNotNull(resources);
			assertTrue(resources.length > 0);
		} finally {
			moduleCore.dispose();
		}
	}

	/*
	 * Class under test for ComponentResource[] findResourcesByRuntimePath(URI)
	 */
	public void testFindResourcesByRuntimePathURI() throws Exception{
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			assertNotNull(moduleCore);
			WorkbenchComponent wbComponent = moduleCore.getComponent();
			assertNotNull(wbComponent);
			ComponentResource[] resources = moduleCore.findResourcesByRuntimePath(moduleURI, moduleURI);
			assertNotNull(resources);
			assertTrue(resources.length > 0);
		} finally {
			moduleCore.dispose();
		}
	}

	public void testFindResourcesBySourcePath() throws Exception {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			assertNotNull(moduleCore);
			ComponentResource[] resources = moduleCore.findResourcesBySourcePath(moduleURI);
			assertNotNull(resources);
			assertTrue(resources.length > 0);
		} finally {
			moduleCore.dispose();
		}
	}

	public void testFindComponentByName() {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			assertNotNull(moduleCore);
			WorkbenchComponent wbComponent =  moduleCore.getComponent();
			assertNotNull(wbComponent);
		} finally {
			moduleCore.dispose();
		}
	}

	public void testFindComponentByURI() throws Exception {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			assertNotNull(moduleCore);
			WorkbenchComponent wbComponent = moduleCore.findComponentByURI(moduleURI);
			assertNotNull(wbComponent);
		} finally {
			moduleCore.dispose();
		}
	}

	public void testIsLocalDependency() {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForRead(project);
			assertNotNull(moduleCore);
			moduleCore.isLocalDependency(null);
		} finally {
			moduleCore.dispose();
		}
	}

	public void testGetFirstModule() {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForRead(project);
			assertNotNull(moduleCore);
			WorkbenchComponent wbComponent =  moduleCore.getFirstModule();
			assertNotNull(wbComponent);
		} finally {
			moduleCore.dispose();
		}
	}

	public void testCreateComponentURI() {
		StructureEdit moduleCore = null;
		URI uri = StructureEdit.createComponentURI(project, "testComp");
		assertNotNull(uri);
	}
}
