/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.eclipse.wst.common.componentcore.internal.resources;

import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;

public abstract class AbstractResourceListVirtualComponent implements IVirtualComponent {
	protected IProject project;
	protected IVirtualComponent referencingComp;	

	public AbstractResourceListVirtualComponent(IProject p, IVirtualComponent referencingComponent) {
		this.project = p;
		this.referencingComp = referencingComponent;
	}

	public void create(int updateFlags, IProgressMonitor aMonitor)
			throws CoreException {
		// Ignore
	}

	public boolean exists() {
		return true;
	}

	public IVirtualComponent getComponent() {
		return this;
	}

	public String getName() {
		return getId();
	}

	public String getDeployedName() {
		return getName();
	}

	public String getId() {
		String firstSegment = getFirstIdSegment();
		if (project != null && project.equals(
				referencingComp.getProject()))
			return firstSegment; 
		return firstSegment + Path.SEPARATOR + project.getName();
	}

	protected abstract String getFirstIdSegment();
	
	public IProject getProject() {
		return project;
	}

	public IVirtualComponent[] getReferencingComponents() {
		return referencingComp == null ? new IVirtualComponent[] {}
				: new IVirtualComponent[] { referencingComp };
	}

	public IVirtualFolder getRootFolder() {
		// Creates a new instance each time to ensure it's not cached
		IContainer[] containers = getUnderlyingContainers();
		IResource[] looseResources = getLooseResources();
		ResourceListVirtualFolder folder = 
			new ResourceListVirtualFolder(project, new Path("/"), containers, looseResources); //$NON-NLS-1$
		return folder;
	}

	protected abstract IContainer[] getUnderlyingContainers();
	protected abstract IResource[] getLooseResources();
	
	public Properties getMetaProperties() {
		return null;
	}

	public IPath[] getMetaResources() {
		return null;
	}

	public IVirtualReference getReference(String aComponentName) {
		// Ignore
		return null;
	}

	public IVirtualReference[] getReferences() {
		// Ignore; no children
		return new IVirtualReference[] {};
	}

	public IVirtualReference[] getReferences(Map<String, Object> options) {
		// Ignore, no children
		return new IVirtualReference[]{};
	}

	
	public boolean isBinary() {
		return false;
	}

	public void setMetaProperties(Properties properties) {
		// Ignore
	}

	public void setMetaProperty(String name, String value) {
		// Ignore
	}

	public void setMetaResources(IPath[] theMetaResourcePaths) {
		// Ignore
	}

	public void setReferences(IVirtualReference[] theReferences) {
		// Ignore
	}

	public Object getAdapter(Class adapter) {
		// Ignore
		return null;
	}

	public void addReferences(IVirtualReference[] references) {
		// Ignore
	}
}
