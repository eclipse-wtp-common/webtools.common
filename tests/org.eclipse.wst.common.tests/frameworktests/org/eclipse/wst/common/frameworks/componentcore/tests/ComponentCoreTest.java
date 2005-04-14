package org.eclipse.wst.common.frameworks.componentcore.tests;

import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.etools.common.test.apitools.ProjectUnzipUtil;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualContainer;
import org.eclipse.wst.common.tests.CommonTestsPlugin;

public class ComponentCoreTest extends TestCase {
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

	public void testCreateFlexibleProject() {
		try {
			ComponentCore.createFlexibleProject(project);
		} catch (Exception e) {
			fail(e.toString());
		};

	}

	public void testCreateComponent() {
		try {
			new ComponentCore();
			ComponentCore.createComponent(project, "test");
		
		} catch (Exception e) {
			fail(e.toString());
		};
	}

	public void testCreateFolder() {
		try {
			ComponentCore.createFolder(project, "test", new Path("test/runtimePath"));
		} catch (Exception e) {
			fail(e.toString());
		};
	}

	public void testCreateFile() {
		try {
			ComponentCore.createFile(project, "test", new Path("test/runtimePath/file"));
		} catch (Exception e) {
			fail(e.toString());
		};
	}

	public void testCreateReference() {
		VirtualContainer container = new VirtualContainer(project, "test", new Path("test/runtimePath/file"));
		
		try {
			ComponentCore.createReference(container,container);
		} catch (Exception e) {
			fail(e.toString());
		};
	}

}
