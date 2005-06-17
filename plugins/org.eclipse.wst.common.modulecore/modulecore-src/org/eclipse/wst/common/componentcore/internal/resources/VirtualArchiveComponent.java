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
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.componentcore.resources.ComponentHandle;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;


public class VirtualArchiveComponent implements IVirtualComponent {

	private String name;
	private IPath runtimePath;
	private int flag = 1;

	public VirtualArchiveComponent(String aName, IPath aRuntimePath) {
		name = aName;
		runtimePath = aRuntimePath;
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
		boolean ret =  (flag & BINARY) == 1  ? true :false;
		return ret;
	}
	
	public IPath getRuntimePath() {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public IPath[] getMetaResources() {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public void setMetaResources(IPath[] theMetaResourcePaths) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public ComponentHandle getComponentHandle() {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public void delete(int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public String getFileExtension() {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public IPath getWorkspaceRelativePath() {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public IPath getProjectRelativePath() {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public IVirtualResource getParent() {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public IProject getProject() {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public boolean isAccessible() {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public String getResourceType() {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public void setResourceType(String aResourceType) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public boolean contains(ISchedulingRule rule) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public boolean isConflicting(ISchedulingRule rule) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public Object getAdapter(Class adapter) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public Properties getMetaProperties() {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public IVirtualResource[] getResources(String aResourceType) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public String getVersion() {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public IVirtualFolder getFolder(String name) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public IVirtualFolder getFolder(IPath path) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public IVirtualFile getFile(IPath path) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public void create(int updateFlags, IProgressMonitor aMonitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$	
	}

	public IVirtualResource[] getResources() {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public IVirtualResource getResource(IPath path) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public IVirtualResource getResource(IPath path, int searchFlags) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public IVirtualResource getResource(String name) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public IVirtualResource getResource(String name, int searchFlags) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public void createLink(IPath aProjectRelativeLocation, int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public IResource getUnderlyingResource() {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public IResource[] getUnderlyingResources() {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public IVirtualReference[] getReferences() {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public void setReferences(IVirtualReference[] theReferences) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public IVirtualReference getReference(String aComponentName) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$l;
	}

	public boolean exists() {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public IVirtualFolder getRootFolder() {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public IVirtualComponent[] getReferencingComponents() {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}
}
