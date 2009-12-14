/*******************************************************************************
 * Copyright (c) 2009 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.export;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualContainer;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;

/**
 * This utility class is almost entirely copied from ComponentDeployable
 * and deals with the nuts and bolts of creating the model objects when
 * walking a portion of the tree
 */
public class ExportModelUtil {
	public static interface ShouldIncludeUtilityCallback {
		public boolean shouldAddComponentFile(IVirtualComponent current, IExportableFile file);
	}
	
	private List<IExportableResource> members;
	private ShouldIncludeUtilityCallback handler;
	public ExportModelUtil(
			List<IExportableResource> members, ShouldIncludeUtilityCallback handler) {
		this.members = members;
		this.handler = handler;
	}
	
	public void addMembers(IVirtualComponent current, IVirtualContainer cont, IPath path) throws CoreException {
		ensureParentExists(members, path, null);
		ExportableResource[] mr = addMembersInternal(current, cont, path);
		int size = mr.length;
		for (int j = 0; j < size; j++) {
			members.add(mr[j]);
		}
	}
	
	// Add this container's children
	public void addContainer(IContainer container, IPath path) throws CoreException {
		IResource[] children = container.members();
		for( int i = 0; i < children.length; i++ ) {
			if( children[i] instanceof IContainer ) {
				addContainerInternal((IContainer)children[i], path);
			} else if( children[i] instanceof IFile ){
				addFile(null, (IFile)children[i], path);
			}
		}
	}
	
	// Physically adds this container
	protected void addContainerInternal(IContainer container, IPath path) throws CoreException {
		IExportableFolder mf = (ExportableFolder) getExistingModuleResource(members,path.append(container.getName()).makeRelative());
		if( mf == null ) {
			mf = new ExportableFolder(container, container.getName(), path);
		}
		IExportableFolder parent = (ExportableFolder) getExistingModuleResource(members, path);
		if (path.isEmpty())
			members.add(mf);
		else {
			if (parent == null)
				parent = ensureParentExists(members, path, container);
			addMembersToModuleFolder(parent, new IExportableResource[] {mf});
		}
		
		// recurse
		addContainer(container, path.append(container.getName()));
	}
	
	protected ExportableResource[] addMembersInternal(IVirtualComponent current, IVirtualContainer cont, IPath path) throws CoreException {
		IVirtualResource[] res = cont.members();
		int size2 = res.length;
		List list = new ArrayList(size2);
		for (int j = 0; j < size2; j++) {
			if (res[j] instanceof IVirtualContainer) {
				IVirtualContainer cc = (IVirtualContainer) res[j];
				IExportableFolder mf = addVirtualContainerInternal(cc, path);
				IExportableResource[] mr = addMembersInternal(current, cc, path.append(cc.getName()));
				addMembersToModuleFolder(mf, mr);
			} else {
				IFile f = (IFile) res[j].getUnderlyingResource();
				addFile(current, f, path);
			}
		}
		ExportableResource[] mr = new ExportableResource[list.size()];
		list.toArray(mr);
		return mr;
	}
	
	public void addFile(IVirtualComponent current, IPath path, IAdaptable file) {
		IFile f = (IFile)file.getAdapter(IFile.class);
		IExportableFile mf = null;
		if( f != null )
			 mf = createModuleFile(f, path.makeRelative());
		else {
			File f2 = (File)file.getAdapter(File.class);
			if( f2 != null )
				mf = new ExportableFile(f2, f2.getName(), path.makeRelative());
		}
		if( mf != null ) {
			if (handler == null || handler.shouldAddComponentFile(current, mf)) {
				if( mf.getModuleRelativePath().isEmpty() )
					members.add(mf);
				else {
					IExportableFolder moduleParent = ExportModelUtil.ensureParentExists(members, mf.getModuleRelativePath(), null);
					ExportModelUtil.addMembersToModuleFolder((ExportableFolder)moduleParent, new IExportableResource[] {mf});
				}
			}
		}
	}
	
	public void addFile(IVirtualComponent current, IFile f, IPath path) {
		addFile(current, path, (IAdaptable)f);
	}
	
	protected IExportableFolder addVirtualContainerInternal(IVirtualContainer cc, IPath path) {
		// Retrieve already existing module folder if applicable
		IExportableFolder mf = (ExportableFolder) getExistingModuleResource(members,path.append(cc.getName()).makeRelative());
		if (mf == null) {
			mf = new ExportableFolder((IContainer)cc.getUnderlyingResource(), cc.getName(), path);
			IExportableFolder parent = (ExportableFolder) getExistingModuleResource(members, path);
			if (path.isEmpty())
				members.add(mf);
			else {
				if (parent == null)
					parent = ensureParentExists(members, path, (IContainer)cc.getUnderlyingResource());
				addMembersToModuleFolder(parent, new IExportableResource[] {mf});
			}
		}
		return mf;
	}
	
	public static ExportableFile createModuleFile(final IFile file, final IPath path) {
		return new ExportableFile(file, file.getName(), path);
	}

	
	/**
	 * Check the current cache to see if we already have an existing module resource for
	 * the given path.
	 * @param aList
	 * @param path
	 * @return an existing moduleResource from the cached result
	 */
	 
	public static ExportableResource getExistingModuleResource(List aList, IPath path) { 
    	// If the list is empty, return null
    	if (aList==null || aList.isEmpty() || path == null)
    		return null;
    	// Otherwise recursively check to see if given resource matches current resource or if it is a child
    	String[] pathSegments = path.segments(); 
    	ExportableResource exportableResource = null;
    	
    	if(pathSegments.length == 0)
    		return null;
    	for (Iterator iter = aList.iterator(); iter.hasNext();) {
    		exportableResource = (ExportableResource) iter.next();     	
    		String[] moduleSegments = exportableResource.getModuleRelativePath().segments();
    		// If the last segment in passed in path equals the module resource name 
    		// and segment count is the same and the path segments start with the module path segments
    		// then we have a match and we return the existing moduleResource
    		if (pathSegments[pathSegments.length - 1].equals(exportableResource.getName()) && 
		    		(moduleSegments.length + 1) == pathSegments.length && 
		    		startsWith(moduleSegments, pathSegments))
		    	return exportableResource; 
    		
    		// Otherwise, if it is a folder, check its children for the existing resource path
    		// but only check if the beginning segments are a match
	    	if(exportableResource instanceof ExportableFolder && 
	    			startsWith(moduleSegments, pathSegments) && pathSegments.length > moduleSegments.length &&
	    			exportableResource.getName().equals(pathSegments[moduleSegments.length > 0 ? moduleSegments.length : 0]))	    	  
    			if (((ExportableFolder)exportableResource).members()!=null)
    				return getExistingModuleResource(Arrays.asList(((ExportableFolder)exportableResource).members()),path);		
    	}
    	return null;
    }
	
	/**
	 * @return True if beginningSegments[i] == testSegments[i] for all 0<=i<beginningSegments[i] 
	 */
	private static boolean startsWith(String[] beginningSegments, String[] testSegments) { 
		for(int i=0; i < beginningSegments.length; i++) {
			if(!beginningSegments[i].equals(testSegments[i]))
				return false;
		}
		return true;
	}
	
	public static IExportableFolder ensureParentExists(List<IExportableResource> members, IPath path, IContainer cc) {
		IExportableFolder parent = (IExportableFolder) getExistingModuleResource(members, path);
		if (parent == null && path.segmentCount() > 0) {
			String folderName = path.lastSegment();
			IPath folderPath = Path.EMPTY;
			if (path.segmentCount()>1)
				folderPath = path.removeLastSegments(1);
			parent = new ExportableFolder(cc, folderName, folderPath);
			if (path.segmentCount()>1)
				addMembersToModuleFolder(ensureParentExists(members, path.removeLastSegments(1),cc), new IExportableResource[] {parent});
			else
				members.add(parent);
		}
		return parent;
	}
	
	/**
	 * Add the resources from mr to the existing resources in Module Folder mf
	 * @param ModuleFolder mf
	 * @param IModuleResource[] mr
	 */
	public static void addMembersToModuleFolder(IExportableFolder mf, IExportableResource[] mr) {
		// If the folder is null or the resources to add are null or empty, bail and return
		if (mf == null || mr == null || mr.length==0) 
			return;
		// Get the existing members in the module folder
		IExportableResource[] mf_members = mf.members();
		int mf_size = 0;
		// Get the length of the existing members in the module folder
		if (mf_members != null)
			mf_size = mf_members.length;
		// Create a new array to set on the module folder which will combine the existing and
		// new module resources
		IExportableResource[] res = new ExportableResource[mf_size + mr.length];
		// Copy the existing members into the array if there are any
		if (mf_members != null && mf_size > 0)
			System.arraycopy(mf_members, 0, res, 0, mf_size);
		// Copy the new members into the array
		System.arraycopy(mr, 0, res, mf_size, mr.length);
		// Set the new members array on the module folder
		mf.setMembers(res);
	}
	
}
