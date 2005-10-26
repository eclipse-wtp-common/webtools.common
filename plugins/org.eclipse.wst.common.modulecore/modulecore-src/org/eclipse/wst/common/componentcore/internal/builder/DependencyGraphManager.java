package org.eclipse.wst.common.componentcore.internal.builder;

import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;

public class DependencyGraphManager {

	private static DependencyGraphManager INSTANCE = null;
	private HashMap wtpModuleTimeStamps = null;
	
	private DependencyGraphManager() {
		super();
	}
	
	public static final DependencyGraphManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DependencyGraphManager();
			INSTANCE.constructIfNecessary();
		}
		return INSTANCE;
	}
	
	public void construct(IProject project) {
		if (project!=null && project.isAccessible() && project.findMember(IModuleConstants.COMPONENT_FILE_PATH) !=null) //$NON-NLS-1$
			constructIfNecessary();
	}
	
	private void constructIfNecessary() {
		// Block other clients here while we are building
		synchronized (this) {
			if (moduleTimeStampsChanged()) {
				cleanDependencyGraph();
				buildDependencyGraph();
			}
		}
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
		IProject[] projects = ProjectUtilities.getAllProjects();
		for (int k=0; k<projects.length; k++) {
			
			if (!projects[k].isAccessible() || !addTimeStamp(projects[k])) 
				continue;
			IVirtualComponent component= ComponentCore.createComponent(projects[k]);
			if (component == null) continue;
			addDependencyReference(component);
		}
	}
	
	private void addDependencyReference(IVirtualComponent component) {
		IProject componentProject = component.getProject();
		IVirtualReference[] depRefs = component.getReferences();
		for(int i = 0; i<depRefs.length; i++){
			IVirtualComponent targetComponent = depRefs[i].getReferencedComponent();
			if (targetComponent!=null) {
				IProject targetProject = targetComponent.getProject();
				DependencyGraph.getInstance().addReference(targetProject,componentProject);
			}	
		}
		
	}
	
	private boolean addTimeStamp(IProject project) {
		// Get the .component file for the given project
		IResource wtpModulesFile = project.findMember(IModuleConstants.COMPONENT_FILE_PATH); //$NON-NLS-1$
		if (wtpModulesFile==null)
			return false;
		Long currentTimeStamp = new Long(wtpModulesFile.getLocalTimeStamp());
		getWtpModuleTimeStamps().put(project,currentTimeStamp);
		return true;
	}
	
	private void cleanDependencyGraph() {
		DependencyGraph.getInstance().clear();
		getWtpModuleTimeStamps().clear();
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
}
