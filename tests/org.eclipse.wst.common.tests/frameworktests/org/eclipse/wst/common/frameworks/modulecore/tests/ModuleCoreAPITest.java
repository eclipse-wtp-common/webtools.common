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
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.modulecore.ComponentResource;
import org.eclipse.wst.common.modulecore.ModuleCore;
import org.eclipse.wst.common.modulecore.WorkbenchComponent;
import org.eclipse.wst.common.modulecore.internal.impl.ResourceTreeRoot;

/**
 * 
 * <p>
 * To run this test, extract the /testData/virtual-api-test_workspace.zip 
 * found under the same plugin, and use "Run As -> JUnit Plugin Test". Be
 * sure to point the configuration at the extracted workspace, and make sure
 * that "Clear workspace contents" is NOT checked. The test may be run using 
 * a Headless Eclipse.
 * </p>
 */
public class ModuleCoreAPITest extends TestCase {

	private static final Class IFOLDER_CLASS = IFolder.class;
	private static final Class IFILE_CLASS = IFile.class;

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
		setupCreateLinkTest();
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
		virtualResourceTree.put(new Path("META-INF"), new HashMap()); //$NON-NLS-1$
		virtualResourceTree.put((WEB_INF = new Path("WEB-INF")), new HashMap()); //$NON-NLS-1$
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
	 * Checks for and removes the mapping and folder that will be created by {@link ModuleCoreAPITest#testCreateLink()}. 
	 */
	public void setupCreateLinkTest() throws Exception {
		IFolder module2Images = TestWorkspace.getTargetProject().getFolder(new Path("/WebModule2/images")); //$NON-NLS-1$
		if (module2Images.exists())
			module2Images.delete(IResource.FORCE | IResource.ALWAYS_DELETE_PROJECT_CONTENT, null);

		ModuleCore moduleCore = null;

		try {
			moduleCore = ModuleCore.getModuleCoreForWrite(TestWorkspace.getTargetProject());
			WorkbenchComponent wbComponent = moduleCore.findWorkbenchModuleByDeployName(TestWorkspace.WEB_MODULE_2_NAME);

			ComponentResource[] componentResources = 
				wbComponent.findWorkbenchModuleResourceByDeployPath(URI.createURI("/images")); //$NON-NLS-1$

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


	public void assertTree(Map resourceTree, IFolder virtualFolder) throws Exception {
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
	public void assertTree(Map resourceTree, IFolder virtualFolder, String indent) throws Exception {
		// API_TEST VirtualContainer.members()
		IResource[] members = virtualFolder.members(); 
		
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
					assertTree(subTree, (IFolder) members[i], indent + "\t"); //$NON-NLS-1$
			} else {
				assertType(members[i], IFILE_CLASS);
			} 
		} 
	}

	/**
	 */
	public void assertType(IResource resource, Class type) {
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
 
		IContainer component = ModuleCore.create(TestWorkspace.getTargetProject(), TestWorkspace.WEB_MODULE_1_NAME); 
		IFolder root = component.getFolder(new Path("/")); //$NON-NLS-1$ 
		assertTree(virtualResourceTree, root);

	}


	/**
	 * <p>
	 * All methods lised in the "see" clauses are tested by this method.
	 * </p>
	 * 
	 * @see ModuleCore#create(IProject, String)
	 * @see IContainer#getFolder(org.eclipse.core.runtime.IPath)
	 * @see IFolder#createLink(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
	 * 
	 */
	public void testCreateLink() throws Exception {
		
		IContainer component = ModuleCore.create(TestWorkspace.getTargetProject(), TestWorkspace.WEB_MODULE_2_NAME); 
		IFolder images = component.getFolder(new Path("/images")); //$NON-NLS-1$		
		images.createLink(new Path("/WebModule2/images"), 0, null); //$NON-NLS-1$

		IFolder realImages = TestWorkspace.getTargetProject().getFolder(new Path("/WebModule2/images")); //$NON-NLS-1$
		assertTrue("The /WebContent2/images directory must exist.", realImages.exists()); //$NON-NLS-1$

		ModuleCore moduleCore = ModuleCore.getModuleCoreForRead(TestWorkspace.getTargetProject());
		WorkbenchComponent wbComponent = moduleCore.findWorkbenchModuleByDeployName(TestWorkspace.WEB_MODULE_2_NAME);

		ComponentResource[] componentResources = wbComponent.findWorkbenchModuleResourceByDeployPath(URI.createURI("/images")); //$NON-NLS-1$

		assertTrue("There should be at least one mapping for virtual path \"/images\".", componentResources.length > 0); //$NON-NLS-1$

		ResourceTreeRoot resourceTreeRoot = ResourceTreeRoot.getSourceResourceTreeRoot(wbComponent);
		componentResources = resourceTreeRoot.findModuleResources(realImages.getFullPath(), false);
		
		assertTrue("There should be exactly one Component resource with the source path \"" + realImages.getProjectRelativePath() + "\".", componentResources.length == 1); //$NON-NLS-1$ //$NON-NLS-2$
		
		assertTrue("The runtime path should match \"/images\".", componentResources[0].getRuntimePath().path().equals("/images")); //$NON-NLS-1$ //$NON-NLS-2$

	} 

}
