package org.eclipse.wst.common.frameworks.componentcore.tests;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.resources.IVirtualContainer;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;

public class IVirtualContainerTestAPI extends BaseVirtualTest {
	
	protected IVirtualContainer deletemeVirtualFolder2;
	protected static final IPath DELETEME_PATH2 = new Path("/deleteme2"); //$NON-NLS-1$

	public IVirtualContainerTestAPI(String name) {
		super(name);
	} 
	

	public void test_create()
			throws CoreException {
		
		IVirtualFolder rootFolder = component.getRootFolder();
		deletemeVirtualFolder2 = rootFolder.getFolder(DELETEME_PATH2);
		deletemeVirtualFolder2.create(IVirtualResource.FORCE, null);
		deletemeVirtualFolder2.delete(IVirtualResource.FORCE, null);
	}

	public void test_exists() {
		IVirtualContainer container = webInfFolder;
		IPath path = new Path("/deleteme");
		boolean bRetValue = container.exists(path);
	}

	public void test_findMember() {
		String name = "lib";
		IVirtualContainer container = webInfFolder;
		IVirtualResource resource= container.findMember(name);
		
	}

	public void test_findMember2() {
		String name = "lib";
		int searchFlags = 0;
		IVirtualContainer container = webInfFolder;
		IVirtualResource resource= container.findMember(name,searchFlags);
	}
	
	public void test_findMember3() {
		IPath path = new Path("/lib");
		IVirtualContainer container = webInfFolder;
		IVirtualResource resource= container.findMember(path);
		
	}

	public void test_findMember4() {
		IPath path = new Path("/lib");
		int searchFlags = 0;
		IVirtualContainer container = webInfFolder;
		IVirtualResource resource= container.findMember(path,searchFlags);
	}

	

	public void test_getFile() {
		IPath path = new Path("/deleteme");
		IVirtualContainer container = webInfFolder;
		IVirtualResource resource= container.getFile(path);
	}

	public void test_getFolder() {
		IPath path = new Path("/deleteme");
		IVirtualContainer container = webInfFolder;
		IVirtualResource resource= container.getFolder(path);
	}

	public void test_getFile2() {
		String name = "/deleteme";
		IVirtualContainer container = webInfFolder;
		IVirtualResource resource= container.getFile(name);
	}

	public void test_getFolder2() {
		String name = "/deleteme";
		IVirtualContainer container = webInfFolder;
		IVirtualResource resource= container.getFolder(name);
	}

	public  void test_members() throws CoreException {
		IVirtualContainer container = webInfFolder;
		IVirtualResource[] resource= container.members();
	}

	public void members() throws CoreException {
		IVirtualContainer container = webInfFolder;
		int memberFlags = 0;
		IVirtualResource[] resource= container.members(memberFlags);
		

	}

	

}
