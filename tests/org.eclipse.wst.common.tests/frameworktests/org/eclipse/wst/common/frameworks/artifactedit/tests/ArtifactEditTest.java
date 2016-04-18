package org.eclipse.wst.common.frameworks.artifactedit.tests;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.etools.common.test.apitools.ProjectUnzipUtil;
import org.eclipse.wst.common.componentcore.ArtifactEdit;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.UnresolveableURIException;
import org.eclipse.wst.common.componentcore.internal.ArtifactEditModel;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.frameworks.internal.operations.IOperationHandler;
import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModelEvent;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModelListener;
import org.eclipse.wst.common.tests.CommonTestsPlugin;



public class ArtifactEditTest extends TestCase {
	public static String fileSep = System.getProperty("file.separator");
	public static final String PROJECT_NAME = "TestArtifactEdit";
	public static final String WEB_MODULE_NAME = "WebModule1";
	public static final URI moduleURI = URI.createURI("module:/resource/TestArtifactEdit/WebModule1");
	public static final String EDIT_MODEL_ID = "jst.web";
	private ArtifactEditModel artifactEditModelForRead;
	private ArtifactEditModel artifactEditModelForWrite;
	private ArtifactEdit artifactEditForRead;
	private ArtifactEdit artifactEditForWrite;
	private EditModelListener emListener;
	private Path zipFilePath = new Path("TestData" + fileSep + "TestArtifactEdit.zip");
	private IProject project;



	private IOperationHandler handler = new IOperationHandler() {


		public boolean canContinue(String message) {
			return false;
		}


		public boolean canContinue(String message, String[] items) {

			return false;
		}

		public int canContinueWithAllCheck(String message) {

			return 0;
		}

		public int canContinueWithAllCheckAllowCancel(String message) {

			return 0;
		}

		public void error(String message) {


		}

		public void inform(String message) {


		}


		public Object getContext() {

			return null;
		}
	};

	public ArtifactEditTest() {
		super();

	}

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

		URL url = Platform.getBundle("org.eclipse.wst.common.tests").getEntry("");

		try {
			url = new URL(url.toString() + zipFilePath);

		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		try {
			url = FileLocator.toFileURL(url);
			printLocalPath(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Path(url.getPath());
	}



	private void printLocalPath(URL url) {
		IPath path = ResourcesPlugin.getWorkspace().getRoot().getLocation();
		File file = new File(path.append("PlatformInfo.txt").toOSString());
		BufferedWriter bw;
		String osName = System.getProperty("os.name");
		String fileSeperator = System.getProperty("path.separator");
		URL urlFindUsingPlugin = CommonTestsPlugin.instance.find(zipFilePath);
		try {
			urlFindUsingPlugin = Platform.asLocalURL(urlFindUsingPlugin);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String javaFileSeperator = new Character(File.pathSeparatorChar).toString();


		try {
			bw = new BufferedWriter(new FileWriter(file));
			bw.write("pluginDescriptor URL " + url.toString());
			bw.write("\n");
			bw.write("Operating System: " + osName);
			bw.write("\n");
			bw.write("System file seperator: " + fileSeperator);
			bw.write("\n");
			bw.write("Using Pluign.find URL:" + urlFindUsingPlugin.toString());
			bw.write("\n");
			bw.write("Java file seperator: " + javaFileSeperator);
			bw.write("\n");
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}



	public void testGetArtifactEditForReadWorkbenchComponent() {
		ArtifactEdit edit = null;
		try {
			edit = ArtifactEdit.getArtifactEditForRead(project);
		} finally {
			if (edit != null) {
				edit.dispose();
			}
			assertTrue(edit != null);
		}
	}

	public void testGetArtifactEditForWriteWorkbenchComponent() {
		ArtifactEdit edit = null;
		try {
			edit = ArtifactEdit.getArtifactEditForWrite(project);

		} finally {
			if (edit != null) {
				edit.dispose();
			}
			assertTrue(edit != null);
		}
	}


	public void testGetArtifactEditForReadComponentHandle() {
		StructureEdit moduleCore = null;
		ArtifactEdit edit = null;
		try {
			moduleCore = StructureEdit.getStructureEditForRead(project);
			WorkbenchComponent wbComponent = moduleCore.getComponent();
			edit = ArtifactEdit.getArtifactEditForRead(project);

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();
				edit.dispose();
			}
			assertTrue(edit != null);
		}
	}


	public void testGetArtifactEditForWriteComponentHandle() {
		ArtifactEdit edit = null;
		try {
			edit = ArtifactEdit.getArtifactEditForWrite(project);
		} finally {
			if (edit != null) {
				edit.dispose();
			}
			assertTrue(edit != null);
		}
	}

	public void testIsValidEditableModule() {
		assertTrue(ArtifactEdit.isValidEditableModule(ComponentCore.createComponent(project)));
	}

	public void testArtifactEditArtifactEditModel() {
		ArtifactEdit edit = new ArtifactEdit(getArtifactEditModelforRead());
		assertNotNull(edit);
		edit.dispose();
	}


	public void testArtifactEditModuleCoreNatureWorkbenchComponentboolean() {
		try {
			StructureEdit.getModuleCoreNature(moduleURI);
		}  catch (UnresolveableURIException e) {
			fail();
		}
		ArtifactEdit edit = null;
		try {
			edit = new ArtifactEdit(project, true);
			assertNotNull(edit);
		} finally {
			if (edit != null)
				edit.dispose();
		}
	}

	public void testSave() {
		ArtifactEdit edit = null;
		try {
			edit = ArtifactEdit.getArtifactEditForWrite(project);
			try {
				edit.save(new NullProgressMonitor());
			} catch (Exception e) {
				fail(e.getMessage());
			}
		} finally {
			if (edit != null) {
				edit.dispose();
			}
			assertTrue(edit != null);
		}
		assertTrue(true);
	}

	public void testSaveIfNecessary() {
		ArtifactEdit edit = null;
		try {
			edit = ArtifactEdit.getArtifactEditForWrite(project);
			try {
				edit.saveIfNecessary(new NullProgressMonitor());
			} catch (Exception e) {
				fail(e.getMessage());
			}

		} finally {
			if (edit != null) {
				edit.dispose();
			}
		}
		pass();
	}

	public void testSaveIfNecessaryWithPrompt() {
		ArtifactEdit edit = null;
		try {
			edit = ArtifactEdit.getArtifactEditForWrite(project);
			try {
				edit.saveIfNecessaryWithPrompt(new NullProgressMonitor(), handler, true);
			} catch (Exception e) {
				fail(e.getMessage());
			}
		} finally {
			if (edit != null) {
				edit.dispose();
			}
			pass();
		}
	}

	public void testDispose() {
		ArtifactEdit edit;
		try {
			edit = new ArtifactEdit(getArtifactEditModelforRead());
			edit.dispose();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		pass();
	}

	public void testGetContentModelRoot() {
		ArtifactEdit edit = null;
		try {
			edit = ArtifactEdit.getArtifactEditForRead(project);
			Object object = edit.getContentModelRoot();
			// assertNotNull(object);
		} catch (Exception e) {
			// TODO fail(e.getMessage());
		} finally {
			if (edit != null) {
				edit.dispose();
			}
		}
	}

	public void testAddListener() {
		ArtifactEdit edit = getArtifactEditForRead();
		try {
			edit.addListener(getEmListener());
		} catch (Exception e) {
			fail(e.getMessage());
		}
		pass();
		edit.dispose();
	}

	public void testRemoveListener() {
		ArtifactEdit edit = getArtifactEditForRead();
		try {
			edit.removeListener(getEmListener());
		} catch (Exception e) {
			fail(e.getMessage());
		}
		pass();
	}

	public void testHasEditModel() {

		ArtifactEdit edit = getArtifactEditForRead();
		assertTrue(edit.hasEditModel(artifactEditModelForRead));
		edit.dispose();
	}

	public void testGetArtifactEditModel() {
		ArtifactEdit edit = getArtifactEditForRead();
		assertTrue(edit.hasEditModel(artifactEditModelForRead));
		edit.dispose();
	}

	public void testObject() {
		pass();
	}

	public void testGetClass() {
		ArtifactEdit edit = getArtifactEditForRead();
		assertNotNull(edit.getClass());
		edit.dispose();
	}

	public void testHashCode() {
		ArtifactEdit edit = getArtifactEditForRead();
		int y = -999999999;
		int x = edit.hashCode();
		assertTrue(x != y);
		edit.dispose();
	}

	public void testEquals() {
		assertTrue(getArtifactEditForRead().equals(artifactEditForRead));
	}

	public void testClone() {
		pass();
	}

	public void testToString() {
		assertTrue(getArtifactEditForRead().toString() != null);
	}

	public void testNotify() {
		try {
			synchronized (getArtifactEditForRead()) {
				artifactEditForRead.notify();
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
		pass();

	}

	public void testNotifyAll() {
		try {
			synchronized (getArtifactEditForRead()) {
				artifactEditForRead.notifyAll();
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
		pass();
	}

	public void testWaitlong() {
		long x = 2;
		try {
			synchronized (getArtifactEditForRead()) {
				getArtifactEditForRead().wait(x);
			}
		} catch (Exception e) {
			// fail(e.getMessage());
		}
		pass();
	}

	/*
	 * Class under test for void wait(long, int)
	 */
	public void testWaitlongint() {
		int x = 2;
		try {
			synchronized (getArtifactEditForRead()) {
				getArtifactEditForRead().wait(x);
			}
		} catch (Exception e) {
			// fail(e.getMessage());
		}
		pass();
	}

	public void testWait() {
		try {
			synchronized (getArtifactEditForRead()) {
				getArtifactEditForRead().wait();
			}
		} catch (Exception e) {
			// fail(e.getMessage());
		}
		pass();
	}

	public void testFinalize() {
		pass();
	}


	public ArtifactEditModel getArtifactEditModelforRead() {
		EMFWorkbenchContext context = new EMFWorkbenchContext(project);
		artifactEditModelForRead = new ArtifactEditModel(EDIT_MODEL_ID, context, true, moduleURI);
		return artifactEditModelForRead;
	}



	public ArtifactEdit getArtifactEditForRead() {
		artifactEditForRead = new ArtifactEdit(getArtifactEditModelforRead());
		return artifactEditForRead;
	}

	public void pass() {
		assertTrue(true);
	}

	public EditModelListener getEmListener() {
		if (emListener == null)
			emListener = new EditModelListener() {
				public void editModelChanged(EditModelEvent anEvent) {
				}
			};
		return emListener;
	}

	public ArtifactEditModel getArtifactEditModelForWrite() {
		EMFWorkbenchContext context = new EMFWorkbenchContext(project);
		return new ArtifactEditModel(EDIT_MODEL_ID, context, false, moduleURI);

	}

	public ArtifactEdit getArtifactEditForWrite() {
		return new ArtifactEdit(getArtifactEditModelForWrite());

	}
}
