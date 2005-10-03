package org.eclipse.wst.common.frameworks.componentcore.tests;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.UnresolveableURIException;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.frameworks.componentcore.virtualpath.tests.TestWorkspace;

public class StructureEditAPITest extends TestCase {
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
		TestWorkspace.init();
		project = TestWorkspace.getTargetProject();
	}


	public IProject getTargetProject() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
	}



	public void testGetStructureEditForRead() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForRead(project);
		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	public void testGetStructureEditForWrite() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	public void testGetModuleCoreNature() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForRead(project);
			try {
				moduleCore.getModuleCoreNature(moduleURI);
			} catch (UnresolveableURIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	/*
	 * Class under test for IProject getContainingProject(WorkbenchComponent)
	 */
	public void testGetContainingProjectWorkbenchComponent() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForRead(project);
			WorkbenchComponent wbComponent = moduleCore.getComponent();
			moduleCore.getContainingProject(wbComponent);

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	/*
	 * Class under test for IProject getContainingProject(URI)
	 */
	public void testGetContainingProjectURI() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForRead(project);
			try {
				moduleCore.getContainingProject(moduleURI);
			} catch (UnresolveableURIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
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
	public void testGetDeployedName() {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForRead(project);
			WorkbenchComponent wbComponent = moduleCore.getComponent();
			
			try {
				StructureEdit.getDeployedName(moduleURI);
			} catch (UnresolveableURIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);
		}
	}

	/**
	 * 
	 */
	public void testGetComponentType() {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForRead(project);
			WorkbenchComponent wbComponent = moduleCore.getComponent();
			
			StructureEdit.getComponentType(ComponentCore.createComponent(project,wbComponent.getName()));
		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);
		}
	}


	public void testSave() {

		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			moduleCore.save(new NullProgressMonitor());

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}


	public void testSaveIfNecessary() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			moduleCore.saveIfNecessary(new NullProgressMonitor());

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	public void testMultiThreadAccess() {
		
		Thread[] testJobs = new Thread[200];
		for (int i = 0; i < testJobs.length; i++) {
			Thread job = new Thread("Job " + i)
		      {
		        
		        protected IStatus run(IProgressMonitor monitor)
		        {
		          try
		          {
		        	StructureEdit moduleCore = StructureEdit.getStructureEditForRead(project);
		      		System.out.println(moduleCore.getWorkbenchModules());
		      		moduleCore.dispose();
		          }
		          catch (Exception e)
		          {
		        	  e.printStackTrace();
		        	  return Status.CANCEL_STATUS;
		          }
		          return Status.OK_STATUS;
		        }
		      };
			testJobs[i] = job;	
			}
		for (int j = 0; j < testJobs.length; j++) {
			Thread job = testJobs[j];
			job.run();
		}
		
	}

	public void testPrepareProjectComponentsIfNecessary() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			moduleCore.prepareProjectComponentsIfNecessary();

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	public void testGetComponentModelRoot() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			moduleCore.getComponentModelRoot();

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	public void testGetSourceContainers() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			WorkbenchComponent wbComponent = moduleCore.getComponent();
			moduleCore.getSourceContainers(wbComponent);

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	public void testGetWorkbenchModules() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			WorkbenchComponent wbComponent = moduleCore.getComponent();
			moduleCore.getWorkbenchModules();

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	public void testCreateWorkbenchModule() {

		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			WorkbenchComponent wbComponent = moduleCore.getComponent();
			moduleCore.createWorkbenchModule("test");

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}


	public void testCreateWorkbenchModuleResource() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			WorkbenchComponent wbComponent = moduleCore.getComponent();
			moduleCore.createWorkbenchModuleResource(project.getFile("WebModule1/NewFolder"));

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	public void testCreateModuleType() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			WorkbenchComponent wbComponent = moduleCore.getComponent();
			moduleCore.createModuleType(EDIT_MODEL_ID);

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}


	/*
	 * Class under test for ComponentResource[] findResourcesByRuntimePath(URI, URI)
	 */
	public void testFindResourcesByRuntimePathURIURI() {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			WorkbenchComponent wbComponent = moduleCore.getComponent();
			try {
				moduleCore.findResourcesByRuntimePath(moduleURI);
			} catch (UnresolveableURIException e) {
				e.printStackTrace();
			}

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	/*
	 * Class under test for ComponentResource[] findResourcesByRuntimePath(URI)
	 */
	public void testFindResourcesByRuntimePathURI() {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			WorkbenchComponent wbComponent = moduleCore.getComponent();
			try {
				moduleCore.findResourcesByRuntimePath(moduleURI, moduleURI);
			} catch (UnresolveableURIException e) {
				e.printStackTrace();
			}

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	public void testFindResourcesBySourcePath() {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			try {
				moduleCore.findResourcesBySourcePath(moduleURI);
			} catch (UnresolveableURIException e) {
				e.printStackTrace();
			}

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	public void testFindComponentByName() {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			moduleCore.getComponent();
		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	public void testFindComponentByURI() {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			try {
				moduleCore.findComponentByURI(moduleURI);
			} catch (UnresolveableURIException e) {
				e.printStackTrace();
			}

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}

	}

	public void testFindComponentsByType() {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			moduleCore.findComponentsByType(EDIT_MODEL_ID);


		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	public void testIsLocalDependency() {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForRead(project);
			moduleCore.isLocalDependency(null);
		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	public void testGetFirstModule() {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForRead(project);
			moduleCore.getFirstModule();
		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	public void testCreateComponentURI() {
		StructureEdit moduleCore = null;
		URI uri = StructureEdit.createComponentURI(project, "testComp");
		assertNotNull(uri);

	}
}
