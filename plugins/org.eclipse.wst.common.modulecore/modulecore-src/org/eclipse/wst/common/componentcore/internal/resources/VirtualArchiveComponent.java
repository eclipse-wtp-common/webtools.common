/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.resources;


import java.io.File;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;
import org.eclipse.wst.common.frameworks.internal.HashUtil;


public class VirtualArchiveComponent implements IVirtualComponent, IAdaptable {

	public static final Class ADAPTER_TYPE = VirtualArchiveComponent.class;
	public static final String LIBARCHIVETYPE = "lib";
	public static final String VARARCHIVETYPE = "var";
	/**
	 * VirtualArchiveComponent type for VirtualArchiveComponents that represent classpath component dependencies.
	 */
	public static final String CLASSPATHARCHIVETYPE = "cpe"; //$NON-NLS-1
	
	private static final IVirtualReference[] NO_REFERENCES = new VirtualReference[0];
	private static final IVirtualComponent[] NO_COMPONENTS = new VirtualComponent[0];
//	private static final IResource[] NO_RESOURCES = null;
	private static final IVirtualResource[] NO_VIRTUAL_RESOURCES = null;
	private static final Properties NO_PROPERTIES = new Properties();
	private static final IPath[] NO_PATHS = new Path[0];

	private IPath runtimePath;
	private IProject componentProject;
//	private IVirtualFolder rootFolder;
	private int flag = 1;
//	private String archiveLocation;


	private IPath archivePath;
	private String archiveType;



	public VirtualArchiveComponent(IProject aComponentProject,String archiveLocation, IPath aRuntimePath) {
		if(aComponentProject == null){
			throw new NullPointerException();
		}
		componentProject = aComponentProject;
		runtimePath = aRuntimePath;

		String archivePathString = archiveLocation.substring(4, archiveLocation.length());
		archiveType	= archiveLocation.substring(0, archiveLocation.length() - archivePathString.length() -1);
		archivePath = new  Path(archivePathString);
	}

	public IVirtualComponent getComponent() {
		return this;
	}

	public String getName() {
		return this.archiveType + IPath.SEPARATOR + this.archivePath.toString();
	}
	
	public String getDeployedName() {
		return getName();
		//return getArchivePath().lastSegment();
	}

	public void setComponentTypeId(String aComponentTypeId) {
		return;
	}

	public int getType() {
		return IVirtualResource.COMPONENT;
	}

	public boolean isBinary() {
		boolean ret = (flag & BINARY) == 1 ? true : false;
		return ret;
	}

	public IPath[] getMetaResources() {
		return NO_PATHS;
	}

	public void setMetaResources(IPath[] theMetaResourcePaths) {

	}

	public void delete(int updateFlags, IProgressMonitor monitor) throws CoreException {

	}

	public String getFileExtension() {
		return archivePath.getFileExtension();
	}

	public IPath getWorkspaceRelativePath() {
		if( archivePath.segmentCount() > 1 ){
			IFile aFile = ResourcesPlugin.getWorkspace().getRoot().getFile(archivePath);
			if (aFile.exists())
				return aFile.getFullPath();
		}
		return null;
	}

	public IPath getProjectRelativePath() {
		IFile aFile = ResourcesPlugin.getWorkspace().getRoot().getFile(getWorkspaceRelativePath());
		if (aFile.exists())
			return aFile.getProjectRelativePath();
		return null;
	}

	public IProject getProject() {
		return componentProject;
	}

	public IPath getRuntimePath() {
		return runtimePath == null ? ROOT : runtimePath;
	}

	public IPath getArchivePath() {
		return archivePath;
	}
	
	public boolean isAccessible() {
		return true;
	}

	public Properties getMetaProperties() {
		return NO_PROPERTIES;
	}

	public IVirtualResource[] getResources(String aResourceType) {
		return NO_VIRTUAL_RESOURCES;
	}

	public void create(int updateFlags, IProgressMonitor aMonitor) throws CoreException {

	}
	
	public IVirtualReference[] getReferences(Map<String, Object> options) {
		return NO_REFERENCES;
	}

	public IVirtualReference[] getReferences() {
		return NO_REFERENCES;
	}

	public void setReferences(IVirtualReference[] theReferences) {
		// no op
	}
	
	public void addReferences(IVirtualReference[] references) {
		// no op
	}

	public IVirtualReference getReference(String aComponentName) {
		return null;
	}

	public boolean exists() {
		boolean exists = false;
		java.io.File diskFile = getUnderlyingDiskFile();
		if( diskFile != null )
			exists = diskFile.exists();
		
		if( !exists ){
			IFile utilityJar = getUnderlyingWorkbenchFile();
			if( utilityJar != null )
				exists =  utilityJar.exists();
		}
		return exists;
	}

	public IVirtualFolder getRootFolder() {
		return null;
	}

	public IVirtualComponent[] getReferencingComponents() {
		return NO_COMPONENTS;
	}


	public Object getAdapter(Class adapterType) {
		return Platform.getAdapterManager().getAdapter(this, adapterType);
	}

	public String getArchiveType() {
		return archiveType;
	}

	public int hashCode() {
		int hash = HashUtil.SEED;
		hash = HashUtil.hash(hash, getProject().getName());
		hash = HashUtil.hash(hash, getName());
		hash = HashUtil.hash(hash, isBinary());
		return hash;
	}
	
	public boolean equals(Object anOther) {
		if (anOther instanceof VirtualArchiveComponent) {
			VirtualArchiveComponent otherComponent = (VirtualArchiveComponent) anOther;
			return getProject().equals(otherComponent.getProject()) && 
					getName().equals(otherComponent.getName()) && 
					isBinary() == otherComponent.isBinary();
		}
		return false;
	}

	public void setMetaProperty(String name, String value) {

	}

	public void setMetaProperties(Properties properties) {

	}
	public IFile getUnderlyingWorkbenchFile() {
		if (getWorkspaceRelativePath()==null)
			return null;
		return ResourcesPlugin.getWorkspace().getRoot().getFile(getWorkspaceRelativePath());
	}

	public File getUnderlyingDiskFile() {
		String osPath = null;
		IPath loc = null;
		if (getArchiveType().equals(VirtualArchiveComponent.VARARCHIVETYPE)) {
			Object adapted = getAdapter(VirtualArchiveComponent.ADAPTER_TYPE);
			if (adapted instanceof IPath) {
				IPath resolvedpath = (IPath) adapted;
				osPath = resolvedpath.toOSString();
			} 
		} else if(!archivePath.isAbsolute()) {
			IFile file = getProject().getFile(archivePath);
			if(file.exists())
				loc  = file.getLocation();
			else if(archivePath.segmentCount() > 1) {
				file = ResourcesPlugin.getWorkspace().getRoot().getFile(archivePath);
				if(file.exists())
					loc = file.getLocation();
			}
			// this is a file on the local filesystem
			if(loc != null)  
				osPath = loc.toOSString();
		} else {
			osPath = archivePath.toOSString();
		}
		if (osPath==null || osPath.length()==0)
			return null;
		File diskFile = new File(osPath);
		return diskFile;
	}
	
	public String toString() {
		if(archivePath != null){
			return componentProject + " " +archivePath;
		}
		return super.toString();
	}

}
