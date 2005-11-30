/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.internal.ModuleFile;
import org.eclipse.wst.server.core.internal.ModuleFolder;
import org.eclipse.wst.server.core.model.IModuleFolder;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.util.ProjectModule;

public abstract class ComponentDeployable extends ProjectModule {

	protected IVirtualComponent component = null;
	protected List members = new ArrayList();
	
	public ComponentDeployable(IProject project) {
		super(project);
		this.component = ComponentCore.createComponent(project);
	}
	
	/**
	 * @see org.eclipse.jst.server.core.IJ2EEModule#isBinary()
	 */
	public boolean isBinary() {
		return false;
	}
	
	private void addMembersToModuleFolder(ModuleFolder mf, IModuleResource[] mr) {
		IModuleResource[] existingMembers = mf.members();
		if (existingMembers==null)
			existingMembers = new IModuleResource[0];
		List membersJoin = new ArrayList();
		membersJoin.addAll(Arrays.asList(existingMembers));
		List newMembers = Arrays.asList(mr);
		for (int i=0; i<newMembers.size(); i++) {
			boolean found = false;
			IModuleResource newMember = (IModuleResource) newMembers.get(i);
			for (int k=0; k<membersJoin.size(); k++) {
				IModuleResource existingResource = (IModuleResource) membersJoin.get(k);
				if (existingResource.equals(newMember)) {
					found = true;
					break;
				}
			}
			if (!found)
				membersJoin.add(newMember);
		}
		mf.setMembers((IModuleResource[]) membersJoin.toArray(new IModuleResource[membersJoin.size()]));
	}
	
	 /**
     * Returns the child modules of this module.
     * 
     * @return org.eclipse.wst.server.core.model.IModule[]
     */
    public IModule[] getChildModules() {
        return getModules();
    }
    
    public IModule[] getModules() {
		List modules = new ArrayList();
    	IVirtualReference[] components = component.getReferences();
    	for (int i = 0; i < components.length; i++) {
			IVirtualReference reference = components[i];
			IVirtualComponent virtualComp = reference.getReferencedComponent();
			if (virtualComp.getProject()!=component.getProject()) {
				Object module = ServerUtil.getModule(virtualComp.getProject());
				if (module != null && !modules.contains(module))
					modules.add(module);
			}
		}
      return (IModule[]) modules.toArray(new IModule[modules.size()]);
	}
    
    /**
	 * Find the module resources for a given container and path. Inserts in the java containers
	 * at a given path if not null.
	 * 
	 * @param cont a container
	 * @param path the current module relative path
	 * @param javaPath the path where Java resources fit in the root
	 * @param javaCont
	 * @return a possibly-empty array of module resources
	 * @throws CoreException
	 */
	protected IModuleResource[] getMembers(IContainer cont, IPath path, IPath javaPath, IContainer[] javaCont) throws CoreException {
		IResource[] res = cont.members();
		int size2 = res.length;
		List list = new ArrayList(size2);
		for (int j = 0; j < size2; j++) {
			if (res[j] instanceof IContainer) {
				IContainer cc = (IContainer) res[j];
				// Retrieve already existing module folder if applicable
				ModuleFolder mf = (ModuleFolder) getExistingModuleResource(members,new Path(cc.getName()));
				if (mf == null)
					mf = new ModuleFolder(cc, cc.getName(), path);
				IModuleResource[] mr = getMembers(cc, path.append(cc.getName()), javaPath, javaCont);
				IPath curPath = path.append(cc.getName());
				
				if (javaPath != null && curPath.isPrefixOf(javaPath))
					mr = handleJavaPath(path, javaPath, curPath, javaCont, mr, cc);

				addMembersToModuleFolder(mf, mr);
				list.add(mf);
			} else {
				IFile f = (IFile) res[j];
				ModuleFile mf = new ModuleFile(f, f.getName(), path, f.getModificationStamp());
				list.add(mf);
			}
		}
		IModuleResource[] mr = new IModuleResource[list.size()];
		list.toArray(mr);
		return mr;
	}
	
	private IModuleResource getExistingModuleResource(List aList, IPath path) {
    	IModuleResource result = null;
    	// If the list is empty, return null
    	if (aList==null || aList.isEmpty())
    		return null;
    	// Otherwise recursively check to see if given resource matches current resource or if it is a child
    	int i=0;
    	do {
	    	IModuleResource moduleResource = (IModuleResource) aList.get(i);
	    		if (moduleResource.getModuleRelativePath().append(moduleResource.getName()).equals(path))
	    			result = moduleResource;
	    		// if it is a folder, check its children for the resource path
	    		else if (moduleResource instanceof IModuleFolder) {
	    			result = getExistingModuleResource(Arrays.asList(((IModuleFolder)moduleResource).members()),path);
	    		}
	    		i++;
    	} while (result == null && i<aList.size() );
    	return result;
    }
	
	protected IModuleResource[] handleJavaPath(IPath path, IPath javaPath, IPath curPath, IContainer[] javaCont, IModuleResource[] mr, IContainer cc) throws CoreException {
		//subclasses may override
		return new IModuleResource[]{};
	}
	
	public IModuleResource[] members() throws CoreException {
		members.clear();
		IPath javaPath = null;
		IContainer[] javaCont = null;
		IContainer[] cont = getResourceFolders();
		int size = cont.length;
		for (int i = 0; i < size; i++) {
			IModuleResource[] mr = getMembers(cont[i], Path.EMPTY, javaPath, javaCont);
			int size2 = mr.length;
			for (int j = 0; j < size2; j++) {
				if (!members.contains(mr[j]))
					members.add(mr[j]);
			}
		}
		
		IModuleResource[] mr = new IModuleResource[members.size()];
		members.toArray(mr);
		return mr;
	}
	
	/**
	 * Returns the root folders for the resources in this module.
	 * 
	 * @return a possibly-empty array of resource folders
	 */
	public IContainer[] getResourceFolders() {
		IVirtualComponent vc = ComponentCore.createComponent(getProject());
		if (vc != null) {
			IVirtualFolder vFolder = vc.getRootFolder();
			if (vFolder != null)
				return vFolder.getUnderlyingFolders();
		}
		return new IContainer[]{};
	}

}
