package org.eclipse.wst.common.frameworks.componentcore.tests;

import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.etools.common.test.apitools.ProjectUnzipUtil;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.tests.CommonTestsPlugin;

public class ComponentCoreTest extends TestCase {
	public static String fileSep = System.getProperty("file.separator");
	public static final String PROJECT_NAME = "TestArtifactEdit";
	public static final String WEB_MODULE_NAME = "WebModule1";
	public static final URI moduleURI = URI.createURI("module:/resource/TestArtifactEdit/WebModule1");
	public static final String EDIT_MODEL_ID = "jst.web";
	private Path zipFilePath = new Path("TestData" + fileSep + "TestArtifactEdit.zip");
	private IProject project;
	private int i, seed = 0;

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


	public void testCreateComponent() {
		try {
			new ComponentCore();
			ComponentCore.createComponent(project, "test");
		
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	public void testCreateComponentUsingCreate() {
		
		Job[] createComponentJob = new Job[500];
		for (  i = 0; i < 500; i++) {
			
		 createComponentJob[i] = new Job("CreateComponent Test") {
		        protected IStatus run(IProgressMonitor monitor) {
		        	try {
		    			IVirtualComponent c
		                = ComponentCore.createComponent( project, project.getName() + getNextSeed() );
		    			c.create( 0, null );
		    		
		    		} catch (Exception e) {
		    			fail(e.toString());
		    			return Status.CANCEL_STATUS;
		    		}
		            return Status.OK_STATUS;
		        }
		    };
		}
		for (int n = 0; n < createComponentJob.length; n++) {
			createComponentJob[n].setRule(project);
			createComponentJob[n].schedule();
		}
		
	}

	protected int getNextSeed() {
		// TODO Auto-generated method stub
		return seed++;
	}


	public void testCreateFolder() {
		try {
			ComponentCore.createFolder(project, new Path("test/runtimePath"));
		} catch (Exception e) {
			fail(e.toString());
		}
	}

	public void testCreateFile() {
		try {
			ComponentCore.createFile(project, new Path("test/runtimePath/file"));
		} catch (Exception e) {
			fail(e.toString());
		}
	}

	public void testCreateReference() {
		IVirtualComponent container = new VirtualComponent(project, new Path("test/runtimePath/file"));
		
		try {
			ComponentCore.createReference(container,container);
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	public void testCreateResources() {
		IResource res = project.getFile(new Path("WebModule1/WebContent/WEB-INF/web.xml"));
		
		try {
			ComponentCore.createResources(res);
		} catch (Exception e) {
			fail(e.toString());
		}
	}

}
