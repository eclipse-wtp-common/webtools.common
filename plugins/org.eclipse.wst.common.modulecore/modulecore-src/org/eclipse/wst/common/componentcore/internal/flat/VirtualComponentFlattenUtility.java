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
package org.eclipse.wst.common.componentcore.internal.flat;

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
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;

/**
 * This utility class is almost entirely copied from ComponentDeployable
 * and deals with the nuts and bolts of creating the model objects when
 * walking a portion of the tree
 */
public class VirtualComponentFlattenUtility {
	public static interface ShouldIncludeUtilityCallback {
		public boolean shouldAddComponentFile(IVirtualComponent current, IFlatFile file);
	}
	
	private List<IFlatResource> members;
	private ShouldIncludeUtilityCallback handler;
	public VirtualComponentFlattenUtility(
			List<IFlatResource> members, ShouldIncludeUtilityCallback handler) {
		this.members = members;
		this.handler = handler;
	}
	
	public void addMembers(IVirtualComponent current, IVirtualContainer cont, IPath path) throws CoreException {
		ensureParentExists(members, path, null);
		FlatResource[] mr = addMembersInternal(current, cont, path);
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
		IFlatFolder mf = (FlatFolder) getExistingModuleResource(members,path.append(container.getName()).makeRelative());
		if( mf == null ) {
			mf = new FlatFolder(container, container.getName(), path);
			IFlatFolder parent = (FlatFolder) getExistingModuleResource(members, path);
			if (path.isEmpty())
				members.add(mf);
			else {
				if (parent == null)
					parent = ensureParentExists(members, path, container);
				addMembersToModuleFolder(parent, new IFlatResource[] {mf});
			}
		}
		// recurse
		addContainer(container, path.append(container.getName()));
	}
	
	protected FlatResource[] addMembersInternal(IVirtualComponent current, IVirtualContainer cont, IPath path) throws CoreException {
		IVirtualResource[] res = cont.members();
		int size2 = res.length;
		List list = new ArrayList(size2);
		for (int j = 0; j < size2; j++) {
			if (res[j] instanceof IVirtualContainer) {
				IVirtualContainer cc = (IVirtualContainer) res[j];
				IFlatFolder mf = addVirtualContainerInternal(cc, path);
				IFlatResource[] mr = addMembersInternal(current, cc, path.append(cc.getName()));
				addMembersToModuleFolder(mf, mr);
			} else {
				addFile(current, path, (IAdaptable)res[j]);
			}
		}
		FlatResource[] mr = new FlatResource[list.size()];
		list.toArray(mr);
		return mr;
	}
	
	public void addFile(IVirtualComponent current, IPath path, IAdaptable file) {
		IVirtualFile vf = (IVirtualFile)file.getAdapter(IVirtualFile.class);
		IFile f = (IFile)file.getAdapter(IFile.class);
		IFlatFile mf = null;
		String vfName = null;
		if( vf != null && vf.getName() != null )
			vfName = vf.getName();
		if( f != null )
			 mf = new FlatFile(f, vfName == null ? f.getName() : vfName, path);
		else {
			File f2 = (File)file.getAdapter(File.class);
			if( f2 != null )
				mf = new FlatFile(f2, vfName == null ? f2.getName() : vfName, path.makeRelative());
		}
		if( mf != null ) {
			if (handler == null || handler.shouldAddComponentFile(current, mf)) {
				if( mf.getModuleRelativePath().segmentCount() == 0) {
					members.remove(mf);
					members.add(mf);
				}
				else {
					IFlatFolder moduleParent = VirtualComponentFlattenUtility.ensureParentExists(members, mf.getModuleRelativePath(), null);
					List tempParentMembers = new ArrayList(Arrays.asList(moduleParent.members()));
					tempParentMembers.remove(mf);
					tempParentMembers.add(mf);
					moduleParent.setMembers((IFlatResource[]) tempParentMembers.toArray(new IFlatResource[tempParentMembers.size()]));
				}
			}
		}
	}
	
	public void addFile(IVirtualComponent current, IFile f, IPath path) {
		addFile(current, path, (IAdaptable)f);
	}
	
	protected IFlatFolder addVirtualContainerInternal(IVirtualContainer cc, IPath path) {
		// Retrieve already existing module folder if applicable
		IFlatFolder mf = (FlatFolder) getExistingModuleResource(members,path.append(cc.getName()).makeRelative());
		if (mf == null) {
			mf = new FlatFolder((IContainer)cc.getUnderlyingResource(), cc.getName(), path);
			IFlatFolder parent = (FlatFolder) getExistingModuleResource(members, path);
			if (path.isEmpty())
				members.add(mf);
			else {
				if (parent == null)
					parent = ensureParentExists(members, path, (IContainer)cc.getUnderlyingResource());
				addMembersToModuleFolder(parent, new IFlatResource[] {mf});
			}
		}
		return mf;
	}
	
	public static FlatFile createModuleFile(final IFile file, final IPath path) {
		return new FlatFile(file, file.getName(), path);
	}

	
	/**
	 * Check the current cache to see if we already have an existing module resource for
	 * the given path.
	 * @param aList
	 * @param path
	 * @return an existing moduleResource from the cached result
	 */
	 
	public static FlatResource getExistingModuleResource(List aList, IPath path) { 
    	// If the list is empty, return null
    	if (aList==null || aList.isEmpty() || path == null)
    		return null;
    	// Otherwise recursively check to see if given resource matches current resource or if it is a child
    	String[] pathSegments = path.segments(); 
    	FlatResource exportableResource = null;
    	
    	if(pathSegments.length == 0)
    		return null;
    	for (Iterator iter = aList.iterator(); iter.hasNext();) {
    		exportableResource = (FlatResource) iter.next();     	
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
	    	if(exportableResource instanceof FlatFolder && 
	    			startsWith(moduleSegments, pathSegments) && pathSegments.length > moduleSegments.length &&
	    			exportableResource.getName().equals(pathSegments[moduleSegments.length > 0 ? moduleSegments.length : 0]))	    	  
    			if (((FlatFolder)exportableResource).members()!=null)
    				return getExistingModuleResource(Arrays.asList(((FlatFolder)exportableResource).members()),path);
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
	
	public static IFlatFolder ensureParentExists(List<IFlatResource> members, IPath path, IContainer cc) {
		IFlatFolder parent = (IFlatFolder) getExistingModuleResource(members, path);
		if (parent == null && path.segmentCount() > 0) {
			String folderName = path.lastSegment();
			IPath folderPath = Path.EMPTY;
			if (path.segmentCount()>1)
				folderPath = path.removeLastSegments(1);
			parent = new FlatFolder(cc, folderName, folderPath);
			if (path.segmentCount()>1)
				addMembersToModuleFolder(ensureParentExists(members, path.removeLastSegments(1),cc), new IFlatResource[] {parent});
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
	public static void addMembersToModuleFolder(IFlatFolder mf, IFlatResource[] mr) {
		// If the folder is null or the resources to add are null or empty, bail and return
		if (mf == null || mr == null || mr.length==0) 
			return;
		// Get the existing members in the module folder
		IFlatResource[] mf_members = mf.members();
		int mf_size = 0;
		// Get the length of the existing members in the module folder
		if (mf_members != null)
			mf_size = mf_members.length;
		// Create a new array to set on the module folder which will combine the existing and
		// new module resources
		IFlatResource[] res = new FlatResource[mf_size + mr.length];
		// Copy the existing members into the array if there are any
		if (mf_members != null && mf_size > 0)
			System.arraycopy(mf_members, 0, res, 0, mf_size);
		// Copy the new members into the array
		System.arraycopy(mr, 0, res, mf_size, mr.length);
		// Set the new members array on the module folder
		mf.setMembers(res);
	}
	
}
