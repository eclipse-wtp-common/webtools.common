/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.frameworks.modulecore.tests;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.modulecore.ComponentResource;
import org.eclipse.wst.common.modulecore.ModuleCore;
import org.eclipse.wst.common.modulecore.WorkbenchComponent;
import org.eclipse.wst.common.modulecore.internal.impl.ResourceTreeRoot;
import org.eclipse.wst.common.modulecore.resources.IVirtualContainer;
import org.eclipse.wst.common.modulecore.resources.IVirtualFile;
import org.eclipse.wst.common.modulecore.resources.IVirtualFolder;
import org.eclipse.wst.common.modulecore.resources.IVirtualResource;

/**
 * 
 * <p>
 * To run this test, extract the /testData/virtual-api-test_workspace.zip found under the same
 * plugin, and use "Run As -> JUnit Plugin Test". Be sure to point the configuration at the
 * extracted workspace, and make sure that "Clear workspace contents" is NOT checked. The test may
 * be run using a Headless Eclipse.
 * </p>
 */
public class ModuleCoreAPITest extends TestCase {

	private static final Class IFOLDER_CLASS = IVirtualFolder.class;
	private static final Class IFILE_CLASS = IVirtualFile.class;

	private final Map virtualResourceTree = new HashMap();
	private static final Map IGNORE = new HashMap();



	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(ModuleCoreAPITest.class);
		return suite;
	}

	public ModuleCoreAPITest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		setupNavigateComponentTest();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown(); 
		tearDownCreateNewModuleTest();
		tearDownCreateLinkTest();
	}

	public void tearDownCreateNewModuleTest() throws Exception {
		IFolder rootFolder = TestWorkspace.getTargetProject().getFolder(TestWorkspace.NEW_WEB_MODULE_NAME);
		if (rootFolder.exists())
			rootFolder.delete(IResource.FORCE | IResource.ALWAYS_DELETE_PROJECT_CONTENT, null);

		ModuleCore moduleCore = null;

		try {
			moduleCore = ModuleCore.getModuleCoreForWrite(TestWorkspace.getTargetProject());
			WorkbenchComponent wbComponent = moduleCore.findWorkbenchModuleByDeployName(TestWorkspace.NEW_WEB_MODULE_NAME);

			if (wbComponent != null) {
				ComponentResource[] componentResources = wbComponent.findWorkbenchModuleResourceByDeployPath(URI.createURI("/")); //$NON-NLS-1$				

				for (int i = 0; i < componentResources.length; i++) {
					wbComponent.getResources().remove(componentResources[i]);
				}
			}

		} finally {
			if (moduleCore != null) {
				moduleCore.save(null);
				moduleCore.dispose();
			}
		}

	}

	/**
	 * Create a Map structure that mimics the expected structure on disk.
	 */
	public void setupNavigateComponentTest() throws Exception {
		IPath images;
		IPath jsps;
		IPath WEB_INF;

		virtualResourceTree.put((images = new Path("images")), new HashMap()); //$NON-NLS-1$
		virtualResourceTree.put((jsps = new Path("jsps")), new HashMap()); //$NON-NLS-1$
		virtualResourceTree.put(new Path(TestWorkspace.META_INF), new HashMap()); //$NON-NLS-1$
		virtualResourceTree.put((WEB_INF = new Path(TestWorkspace.WEB_INF)), new HashMap()); //$NON-NLS-1$
		virtualResourceTree.put(new Path("TestFile1.txt"), null); //$NON-NLS-1$
		virtualResourceTree.put(new Path("TestFile2.txt"), null); //$NON-NLS-1$

		((Map) virtualResourceTree.get(images)).put(new Path("icon.gif"), null); //$NON-NLS-1$

		((Map) virtualResourceTree.get(jsps)).put(new Path("TestJsp1.jsp"), null); //$NON-NLS-1$
		((Map) virtualResourceTree.get(jsps)).put(new Path("TestJsp2.jsp"), null); //$NON-NLS-1$
		((Map) virtualResourceTree.get(jsps)).put(new Path("TestJsp3.jsp"), null); //$NON-NLS-1$

		((Map) virtualResourceTree.get(WEB_INF)).put(new Path("web.xml"), null); //$NON-NLS-1$
		((Map) virtualResourceTree.get(WEB_INF)).put(new Path("classes"), IGNORE); //$NON-NLS-1$
		((Map) virtualResourceTree.get(WEB_INF)).put(new Path("lib"), new HashMap()); //$NON-NLS-1$
	}

	/**
	 * Checks for and removes the mapping and folder that will be created by
	 * {@link ModuleCoreAPITest#testCreateLink()}.
	 */
	public void tearDownCreateLinkTest() throws Exception {
		IFolder module2Images = TestWorkspace.getTargetProject().getFolder(new Path("/WebModule2/images")); //$NON-NLS-1$
		if (module2Images.exists())
			module2Images.delete(IResource.FORCE | IResource.ALWAYS_DELETE_PROJECT_CONTENT, null);

		ModuleCore moduleCore = null;

		try {
			moduleCore = ModuleCore.getModuleCoreForWrite(TestWorkspace.getTargetProject());
			WorkbenchComponent wbComponent = moduleCore.findWorkbenchModuleByDeployName(TestWorkspace.WEB_MODULE_2_NAME);

			ComponentResource[] componentResources = wbComponent.findWorkbenchModuleResourceByDeployPath(URI.createURI("/images")); //$NON-NLS-1$

			for (int i = 0; i < componentResources.length; i++) {
				wbComponent.getResources().remove(componentResources[i]);
			}

		} finally {
			if (moduleCore != null) {
				moduleCore.save(null);
				moduleCore.dispose();
			}
		}
	}


	public void assertTree(Map resourceTree, IVirtualFolder virtualFolder) throws Exception {
		assertTree(resourceTree, virtualFolder, ""); //$NON-NLS-1$
	}

	/**
	 * <p>
	 * All methods lised in the "see" clauses are tested by this method.
	 * </p>
	 * 
	 * @see IContainer#members()
	 * @see IResource#getName()
	 * 
	 * @param resourceTree
	 * @param virtualFolder
	 * @param indent
	 */
	public void assertTree(Map resourceTree, IVirtualFolder virtualFolder, String indent) throws Exception {
		// API_TEST VirtualContainer.members()
		IVirtualResource[] members = virtualFolder.members();

		assertEquals("The number of resources contained by \"" + virtualFolder.getProjectRelativePath() + "\"", //$NON-NLS-1$ //$NON-NLS-2$
					resourceTree.size(), members.length);
		IPath relativePath;
		Map subTree;
		for (int i = 0; i < members.length; i++) {
			System.out.println(indent + members[i]);
			relativePath = new Path(members[i].getName());

			subTree = (Map) resourceTree.get(relativePath);
			if (subTree != null) {
				assertType(members[i], IFOLDER_CLASS);
				if (subTree != IGNORE)
					assertTree(subTree, (IVirtualFolder) members[i], indent + "\t"); //$NON-NLS-1$
			} else {
				assertType(members[i], IFILE_CLASS);
			}
		}
	}

	/**
	 */
	public void assertType(IVirtualResource resource, Class type) {
		assertTrue("Expected a(n) " + type.getName() + " for member: " + resource.getProjectRelativePath(), //$NON-NLS-1$ //$NON-NLS-2$
					type.isInstance(resource));
	}

	/**
	 * <p>
	 * All methods lised in the "see" clauses are tested by this method.
	 * </p>
	 * 
	 * @see ModuleCore#create(IProject, String)
	 * @see IContainer#getFolder(org.eclipse.core.runtime.IPath)
	 * @see IContainer#members()
	 */
	public void testNavigateComponent() throws Exception {

		IVirtualContainer component = ModuleCore.create(TestWorkspace.getTargetProject(), TestWorkspace.WEB_MODULE_1_NAME);
		IVirtualFolder root = component.getFolder(new Path("/")); //$NON-NLS-1$ 
		assertTree(virtualResourceTree, root);

	}


	/**
	 * <p>
	 * All methods lised in the "see" clauses are tested by this method.
	 * </p>
	 * 
	 * @see ModuleCore#create(IProject, String)
	 * @see IContainer#getFolder(org.eclipse.core.runtime.IPath)
	 * @see IFolder#createLink(org.eclipse.core.runtime.IPath, int,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 * 
	 */
	public void testCreateLink() throws Exception {

		IVirtualContainer component = ModuleCore.create(TestWorkspace.getTargetProject(), TestWorkspace.WEB_MODULE_2_NAME);
		IVirtualFolder images = component.getFolder(new Path("/images")); //$NON-NLS-1$		
		images.createLink(new Path("/WebModule2/images"), 0, null); //$NON-NLS-1$

		IFolder realImages = TestWorkspace.getTargetProject().getFolder(new Path("/WebModule2/images")); //$NON-NLS-1$
		assertTrue("The /WebContent2/images directory must exist.", realImages.exists()); //$NON-NLS-1$

		ModuleCore moduleCore = null;
		try {
			moduleCore = ModuleCore.getModuleCoreForRead(TestWorkspace.getTargetProject());
			WorkbenchComponent wbComponent = moduleCore.findWorkbenchModuleByDeployName(TestWorkspace.WEB_MODULE_2_NAME);

			ComponentResource[] componentResources = wbComponent.findWorkbenchModuleResourceByDeployPath(URI.createURI("/images")); //$NON-NLS-1$

			assertTrue("There should be at least one mapping for virtual path \"/images\".", componentResources.length > 0); //$NON-NLS-1$

			ResourceTreeRoot resourceTreeRoot = ResourceTreeRoot.getSourceResourceTreeRoot(wbComponent);
			componentResources = resourceTreeRoot.findModuleResources(realImages.getFullPath(), false);

			assertTrue("There should be exactly one Component resource with the source path \"" + realImages.getProjectRelativePath() + "\".", componentResources.length == 1); //$NON-NLS-1$ //$NON-NLS-2$

			assertTrue("The runtime path should match \"/images\".", componentResources[0].getRuntimePath().path().equals("/images")); //$NON-NLS-1$ //$NON-NLS-2$

			// make sure that only one component resource is created

			images.createLink(new Path("/WebModule2/images"), 0, null); //$NON-NLS-1$

			componentResources = resourceTreeRoot.findModuleResources(realImages.getFullPath(), false);

			assertTrue("There should be exactly one Component resource with the source path \"" + realImages.getProjectRelativePath() + "\".", componentResources.length == 1); //$NON-NLS-1$ //$NON-NLS-2$

			assertTrue("The runtime path should match \"/images\".", componentResources[0].getRuntimePath().path().equals("/images")); //$NON-NLS-1$ //$NON-NLS-2$
		} finally {
			if (moduleCore != null)
				moduleCore.dispose();
		}

	}

	/**
	 * <p>
	 * All methods lised in the "see" clauses are tested by this method.
	 * </p>
	 * 
	 * @see ModuleCore#create(IProject, String)
	 * @see IContainer#getFolder(org.eclipse.core.runtime.IPath)
	 * @see IFolder#createLink(org.eclipse.core.runtime.IPath, int,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 * 
	 */
	public void testCreateWebModule() throws Exception {

		IVirtualContainer component = ModuleCore.create(TestWorkspace.getTargetProject(), TestWorkspace.NEW_WEB_MODULE_NAME);
		// if(!component.exists())
		component.commit();
		IVirtualFolder root = component.getFolder(new Path("/")); //$NON-NLS-1$
		IPath realWebContentPath = new Path(TestWorkspace.NEW_WEB_MODULE_NAME + IPath.SEPARATOR + "WebContent"); //$NON-NLS-1$
		root.createLink(realWebContentPath, 0, null); //$NON-NLS-1$

		IVirtualFolder metaInfFolder = root.getFolder(TestWorkspace.META_INF);
		metaInfFolder.create(IVirtualResource.FORCE, null);

		IVirtualFolder webInfFolder = root.getFolder(TestWorkspace.WEB_INF);
		webInfFolder.create(IVirtualResource.FORCE, null);

		IFolder realWebContent = TestWorkspace.getTargetProject().getFolder(realWebContentPath);
		assertTrue("The " + realWebContent + " directory must exist.", realWebContent.exists()); //$NON-NLS-1$ //$NON-NLS-2$

		IFolder realMetaInfFolder = realWebContent.getFolder(TestWorkspace.META_INF);
		assertTrue("The " + realMetaInfFolder + " directory must exist.", realMetaInfFolder.exists()); //$NON-NLS-1$ //$NON-NLS-2$

		IFolder realWebInfFolder = realWebContent.getFolder(TestWorkspace.WEB_INF);
		assertTrue("The " + realWebInfFolder + " directory must exist.", realWebInfFolder.exists()); //$NON-NLS-1$ //$NON-NLS-2$

		ModuleCore moduleCore = null;
		try {
			moduleCore = ModuleCore.getModuleCoreForRead(TestWorkspace.getTargetProject());
			WorkbenchComponent wbComponent = moduleCore.findWorkbenchModuleByDeployName(TestWorkspace.NEW_WEB_MODULE_NAME);

			ComponentResource[] componentResources = wbComponent.findWorkbenchModuleResourceByDeployPath(URI.createURI("/" + TestWorkspace.META_INF)); //$NON-NLS-1$

			assertTrue("There should be at least one mapping for virtual path \"/" + TestWorkspace.META_INF + "\".", componentResources.length > 0); //$NON-NLS-1$ //$NON-NLS-2$

			ResourceTreeRoot resourceTreeRoot = ResourceTreeRoot.getSourceResourceTreeRoot(wbComponent);
			componentResources = resourceTreeRoot.findModuleResources(metaInfFolder.getWorkspaceRelativePath(), false);

			assertTrue("There should be exactly one Component resource with the source path \"" + metaInfFolder.getProjectRelativePath() + "\".", componentResources.length == 1); //$NON-NLS-1$ //$NON-NLS-2$

			assertTrue("The runtime path should match \"/" + TestWorkspace.META_INF + "\".", componentResources[0].getRuntimePath().path().equals("/" + TestWorkspace.META_INF)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			
			// try to force duplicate mappings
			metaInfFolder.create(IVirtualResource.FORCE, null);
			webInfFolder.create(IVirtualResource.FORCE, null);
			
			// ensure that multiple mappings aren't added
			
			assertTrue("The mapping should not be duplicated.", !isDuplicated(wbComponent, metaInfFolder.getRuntimePath())); //$NON-NLS-1$
			assertTrue("The mapping should not be duplicated.", !isDuplicated(wbComponent, webInfFolder.getRuntimePath())); //$NON-NLS-1$
		} finally {
			if (moduleCore != null)
				moduleCore.dispose();
		}

	}

	private boolean isDuplicated(WorkbenchComponent wbComponent, IPath runtimePath) {
		
		URI runtimeURI = URI.createURI(runtimePath.toString());
		boolean found = false;
		List resourceList = wbComponent.getResources();
		for (Iterator iter = resourceList.iterator(); iter.hasNext();) {
			ComponentResource resource = (ComponentResource) iter.next();
			if(resource.getRuntimePath().equals(runtimeURI))
				if(found)
					return true;
				else
					found = true;
		}	
		return false;
	}

}
