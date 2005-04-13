package org.eclipse.wst.common.frameworks.componentcore.tests;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;

public class BaseVirtualTest extends TestCase {

	public static final IProject TEST_PROJECT = ResourcesPlugin.getWorkspace().getRoot().getProject(TestWorkspace.PROJECT_NAME);

	public static final String TEST_FOLDER_NAME = "WEB-INF"; //$NON-NLS-1$
	
	public static final Path WEBINF_FOLDER_REAL_PATH = new Path("/WebModule1/WebContent/"+TEST_FOLDER_NAME); //$NON-NLS-1$ //$NON-NLS-2$
	public static final Path WEBINF_FOLDER_RUNTIME_PATH = new Path("/"+TEST_FOLDER_NAME); //$NON-NLS-1$
	
	public static final Path TESTDATA_FOLDER_REAL_PATH = new Path("WebModule1/testdata"); //$NON-NLS-1$ //$NON-NLS-2$
	public static final Path TESTDATA_FOLDER_RUNTIME_PATH = new Path("/"); //$NON-NLS-1$
	
	protected static final IPath DELETEME_PATH = new Path("/deleteme"); //$NON-NLS-1$
	
	protected IVirtualComponent component;
	
	protected IVirtualFolder webInfFolder;
	protected IFolder realWebInfFolder;
	
	protected IVirtualFolder deletemeVirtualFolder;
	protected IFolder deletemeFolder;	

	protected IVirtualFolder testdataFolder;
	protected IFolder realTestdataFolder;
	
	

	public BaseVirtualTest(String name) {
		super(name);
	} 

	protected void setUp() throws Exception {
		super.setUp();
		TestWorkspace.init();		
		
		realWebInfFolder = TEST_PROJECT.getFolder(WEBINF_FOLDER_REAL_PATH);
		
		component = ComponentCore.createComponent(TEST_PROJECT, TestWorkspace.WEB_MODULE_1_NAME);
		webInfFolder = component.getFolder(WEBINF_FOLDER_RUNTIME_PATH); 		

		testdataFolder = component.getFolder(TESTDATA_FOLDER_RUNTIME_PATH); 
		realTestdataFolder = TEST_PROJECT.getFolder(TESTDATA_FOLDER_REAL_PATH);
		
		deletemeVirtualFolder = component.getFolder(DELETEME_PATH);
		deletemeVirtualFolder.create(IVirtualResource.FORCE, null);
		
		deletemeFolder = deletemeVirtualFolder.getUnderlyingFolder();		
		
	}
	

	protected void tearDown() throws Exception {
		super.tearDown();
		
		if(deletemeFolder.exists())
			deletemeFolder.delete(IVirtualResource.FORCE, null);
		
	}
	


}
