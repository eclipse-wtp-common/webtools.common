/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.util.ComponentUtilities;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;

public class DependencyGraphManager {
	
	private class DependencyReference {
		
		public IProject componentProject;
		public IProject targetProject;
		
		public DependencyReference(IProject target, IProject component) {
			super();
			componentProject=component;
			targetProject=target;
		}
	}

	private static DependencyGraphManager INSTANCE = null;
	private static final String MANIFEST_URI = "META-INF/MANIFEST.MF";
	private HashMap wtpModuleTimeStamps = null;
	private HashMap manifestTimeStamps = null;
	
	private DependencyGraphManager() {
		super();
	}
	
	public synchronized static final DependencyGraphManager getInstance() {
		if (INSTANCE == null)
			INSTANCE = new DependencyGraphManager();
		return INSTANCE;
	}
	
	public void construct(IProject project) {
		if (project!=null && project.isAccessible() && project.findMember(IModuleConstants.COMPONENT_FILE_PATH) !=null) //$NON-NLS-1$
			constructIfNecessary();
	}
	
	private void constructIfNecessary() {
		if (moduleTimeStampsChanged() || manifestTimeStampsChanged()) 
			buildDependencyGraph();
	}
	
	private boolean manifestTimeStampsChanged() {
		HashMap workspaceTimeStamps = collectManifestTimeStamps();
		if (getManifestTimeStamps().equals(workspaceTimeStamps))
			return false;
		return true;
	}

	private HashMap getManifestTimeStamps() {
		if (wtpModuleTimeStamps == null)
			wtpModuleTimeStamps = new HashMap();
		return wtpModuleTimeStamps;
	}

	private HashMap collectManifestTimeStamps() {
		HashMap timeStamps = new HashMap();
		IProject[] projects = ProjectUtilities.getAllProjects();
		for (int i=0; i<projects.length; i++) {
			IFile manifestFile = null;
			if (projects[i]==null || !projects[i].isAccessible())
				continue;
			manifestFile = getManifestFile(projects[i]);
			if (manifestFile != null) {
				Long currentTimeStamp = new Long(manifestFile.getLocalTimeStamp());
				timeStamps.put(projects[i],currentTimeStamp);
			}
		}
		return timeStamps;
	}
	private IFile getManifestFile(IProject p) {
		IVirtualComponent component = ComponentCore.createComponent(p);
		try {
			IFile file = ComponentUtilities.findFile(component, new Path(MANIFEST_URI));
			if (file != null)
				return file;
		} catch (CoreException ce) {
			Logger.getLogger().log(ce);
		}
		return null;
	}

	private boolean moduleTimeStampsChanged() {
		HashMap workspaceTimeStamps = collectModuleTimeStamps();
		if (getWtpModuleTimeStamps().equals(workspaceTimeStamps))
			return false;
		return true;
	}
	
	private HashMap collectModuleTimeStamps() {
		HashMap timeStamps = new HashMap();
		IProject[] projects = ProjectUtilities.getAllProjects();
		for (int i=0; i<projects.length; i++) {
			if (projects[i]==null || !projects[i].isAccessible())
				continue;
			IResource wtpModulesFile = projects[i].findMember(IModuleConstants.COMPONENT_FILE_PATH); //$NON-NLS-1$
			if (wtpModulesFile != null) {
				Long currentTimeStamp = new Long(wtpModulesFile.getLocalTimeStamp());
				timeStamps.put(projects[i],currentTimeStamp);
			}
		}
		return timeStamps;
	}
	
	private void buildDependencyGraph() {
		// Process and collect dependency references to add
		List referencesToAdd = new ArrayList();
		IProject[] projects = ProjectUtilities.getAllProjects();
		for (int k=0; k<projects.length; k++) {
			if (!projects[k].isAccessible() || projects[k].findMember(IModuleConstants.COMPONENT_FILE_PATH)==null) 
				continue;
			IVirtualComponent component= ComponentCore.createComponent(projects[k]);
			if (component == null) continue;
			referencesToAdd.addAll(getDependencyReferences(component));
		}
		
		//Update the actual graph and block other threads here
		synchronized (this) {
			List timeStampsUpdated = new ArrayList();
			cleanDependencyGraph();
			for (int i=0; i<referencesToAdd.size(); i++) {
				DependencyReference ref = (DependencyReference) referencesToAdd.get(i);
				DependencyGraph.getInstance().addReference(ref.targetProject,ref.componentProject);
				if (!timeStampsUpdated.contains(ref.componentProject)) {
					addTimeStamp(ref.componentProject);
					timeStampsUpdated.add(ref.componentProject);
				}
			}
		}
	}
	
	private List getDependencyReferences(IVirtualComponent component) {
		List refs = new ArrayList();
		IProject componentProject = component.getProject();
		IVirtualReference[] depRefs = component.getReferences();
		for(int i = 0; i<depRefs.length; i++){
			IVirtualComponent targetComponent = depRefs[i].getReferencedComponent();
			if (targetComponent!=null) {
				IProject targetProject = targetComponent.getProject();
				refs.add(new DependencyReference(targetProject,componentProject));
			}	
		}
		return refs;
	}
	
	private boolean addTimeStamp(IProject project) {
		// Get the .component file for the given project
		IResource wtpModulesFile = project.findMember(IModuleConstants.COMPONENT_FILE_PATH);
		if (wtpModulesFile==null)
			return false;
		Long currentTimeStamp = new Long(wtpModulesFile.getLocalTimeStamp());
		getWtpModuleTimeStamps().put(project,currentTimeStamp);
		//		 Get the MANIFEST file for the given project
		IResource manifestFile = getManifestFile(project);

		if (manifestFile==null)
			return false;
		getManifestTimeStamps().put(project,currentTimeStamp);
		return true;
	}
	
	private void cleanDependencyGraph() {
		DependencyGraph.getInstance().clear();
		getWtpModuleTimeStamps().clear();
		getManifestTimeStamps().clear();
	}

	/**
	 * Lazy initialization and return of the key valued pair of projects and wtp modules file
	 * timestamps.
	 * 
	 * @return HashMap of projects to .component file stamps
	 */
	private HashMap getWtpModuleTimeStamps() {
		if (wtpModuleTimeStamps == null)
			wtpModuleTimeStamps = new HashMap();
		return wtpModuleTimeStamps;
	}
	
	/**
	 * Return the dependency graph which was initialized if need be in the 
	 * singleton manager method.
	 */ 
	public DependencyGraph getDependencyGraph() {
		constructIfNecessary();
		return DependencyGraph.getInstance();
	}
	
	public void forceRefresh() {
		buildDependencyGraph();
	}
}
