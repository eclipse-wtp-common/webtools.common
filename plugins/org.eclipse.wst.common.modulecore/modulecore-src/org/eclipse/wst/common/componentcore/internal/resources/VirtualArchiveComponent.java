/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/ 
package org.eclipse.wst.common.componentcore.internal.resources;


import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.componentcore.resources.ComponentHandle;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;


public class VirtualArchiveComponent implements IVirtualComponent {

	private static final IVirtualReference[] NO_REFERENCES = new VirtualReference[0];
	private static final IVirtualComponent[] NO_COMPONENTS = new VirtualComponent[0];
	private static final IResource[] NO_RESOURCES = null;
	private static final IVirtualResource[] NO_VIRTUAL_RESOURCES = null;
	private static final Properties NO_PROPERTIES = new Properties();
	private static final IPath[] NO_PATHS = new Path[0];
	
	
	private String name;
	private int flag = 1;
	private ComponentHandle handle;
	private IPath archivePath;
	private IProject project;
	private String archiveType;
	
	/**
	 * 
	 * @param aProject The containing project
	 * @param aName A project relative path or a string of the form 
	 */
	public VirtualArchiveComponent(String aName) {
		name = aName;
		handle = ComponentHandle.create(project, aName);
		IPath namePath = new Path(aName);
		archiveType = namePath.segment(0);
		archivePath = namePath.removeFirstSegments(1).makeRelative();
	}
	/**
	 * 
	 * @param aProject The containing project
	 * @param aName A project relative path or a string of the form 
	 */
	public VirtualArchiveComponent(IProject aProject, String aName) {
		name = aName;
		project = aProject;
		handle = ComponentHandle.create(project, aName);
		archivePath = new Path(aName);
	}
	
	public IVirtualComponent getComponent() {
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public String getComponentTypeId() {
		return IModuleConstants.JST_UTILITY_MODULE;
	}

	public void setComponentTypeId(String aComponentTypeId) {
		return;
	}
	
	public int getType() {
		return IVirtualResource.COMPONENT;
	}
	
	public boolean isBinary(){
		return (flag & BINARY) == 1  ? true :false;		
	}
	
	public IPath getRuntimePath() {
		return ROOT;
	}

	public IPath[] getMetaResources() {
		return NO_PATHS;
	}

	public void setMetaResources(IPath[] theMetaResourcePaths) {
		
	}

	public ComponentHandle getComponentHandle() {
		return handle;
	}

	public void delete(int updateFlags, IProgressMonitor monitor) throws CoreException {
		
	}

	public String getFileExtension() {
		return archivePath.getFileExtension();
	}

	public IPath getWorkspaceRelativePath() {
		if(getProject() != null)
			return getProject().getFile(archivePath).getFullPath();
		return archivePath;
	}

	public IPath getProjectRelativePath() {
		return archivePath;
	} 

	public IProject getProject() {
		return project;
	}

	public boolean isAccessible() {
		return true;
	} 

	public Object getAdapter(Class adapter) {
		return null;
	}

	public Properties getMetaProperties() {
		return NO_PROPERTIES;
	}

	public IVirtualResource[] getResources(String aResourceType) {
		return NO_VIRTUAL_RESOURCES;
	}

	public void create(int updateFlags, IProgressMonitor aMonitor) throws CoreException {
			
	}

	public IVirtualReference[] getReferences() {
		return NO_REFERENCES;
	}

	public void setReferences(IVirtualReference[] theReferences) {
		// no op
	}

	public IVirtualReference getReference(String aComponentName) {
		return null;
	}

	public boolean exists() {
		return false;
	}

	public IVirtualFolder getRootFolder() {
		return null; 
	}

	public IVirtualComponent[] getReferencingComponents() {
		return NO_COMPONENTS;
	}
	public String getVersion() {
		return null;
	}
}
