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
package org.eclipse.wst.common.modulecore.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.wst.common.modulecore.ModuleCoreNature;
import org.eclipse.wst.common.modulecore.ModuleStructuralModel;
import org.eclipse.wst.common.modulecore.ModuleURIUtil;
import org.eclipse.wst.common.modulecore.ProjectModules;
import org.eclipse.wst.common.modulecore.WorkbenchModule;
import org.eclipse.wst.common.modulecore.WorkbenchModuleResource;
import org.eclipse.wst.common.modulecore.impl.UnresolveableURIException;

/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public class ModuleCore {
	
	public static final Class ADAPTER_CLASS = ModuleCore.class;
	
	private final ModuleStructuralModel structuralModel;

	public ModuleCore(ModuleStructuralModel aStructuralModel) { 
		structuralModel = aStructuralModel;
	}
	
	 
	public interface Constants {

		public interface ModuleURISegments {
			public static final int SUB_PROTOCOL = 0;
			public static final int PROJECT_NAME = 1;
			public static final int MODULE_NAME = 2;
		}
	}
// TODO Use URIs should be minimized 

	static String MODULE_META_FILE_NAME = ".wtpmodules";
	private static final WorkbenchModuleResource[] NO_RESOURCES = new WorkbenchModuleResource[0];

	public static ModuleStructuralModel getModuleStructuralModelForRead(IProject aProject, Object anAccessorKey) {
		ModuleCoreNature aNature = ModuleCoreNature.getModuleCoreNature(aProject);
		return aNature.getModuleStructuralModelForRead(anAccessorKey);
	}

	public static  ModuleStructuralModel getModuleStructuralModelForWrite(IProject aProject, Object anAccessorKey) {
		ModuleCoreNature aNature = ModuleCoreNature.getModuleCoreNature(aProject);
		return aNature.getModuleStructuralModelForWrite(anAccessorKey);
	} 
	
	public static ModuleCoreNature getModuleCoreNature(URI aModuleURI) throws UnresolveableURIException {
		return ModuleCoreNature.getModuleCoreNature(getContainingProject(aModuleURI));
	}

	public static URI getOutputContainerRoot(WorkbenchModule aWorkbenchModule) {
	    return URI.createURI(".deployables/"+aWorkbenchModule.getDeployedName()); //$NON-NLS-1$
	} 

	public static Resource.Factory getResourceFactory(URI aURI) {
		return Resource.Factory.Registry.INSTANCE.getFactory(aURI);
	}
	
	/* Need to evaluate whether this should be part of the adaptive nature of this class or the utility nature of this class */
	public static IProject getContainingProject(URI aModuleURI) throws UnresolveableURIException {
		ModuleURIUtil.ensureValidFullyQualifiedModuleURI(aModuleURI);
		String projectName = aModuleURI.segment(ModuleCore.Constants.ModuleURISegments.PROJECT_NAME);
		if(projectName == null || projectName.length() == 0)
			throw new UnresolveableURIException(aModuleURI);
		return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
	}		
	
	private static String getDeployedNameForModule(URI aModuleURI) throws UnresolveableURIException { 
		ModuleURIUtil.ensureValidFullyQualifiedModuleURI(aModuleURI);
		return aModuleURI.segment(ModuleCore.Constants.ModuleURISegments.MODULE_NAME);
	}
	
	
	public ProjectModules getProjectModules() {
		return (ProjectModules) structuralModel.getPrimaryRootObject();
	}

	public WorkbenchModule[] getWorkbenchModules() {
		List wbModules = getProjectModules().getWorkbenchModules();
		return (WorkbenchModule[]) wbModules.toArray(new WorkbenchModule[wbModules.size()]);
	}

	public WorkbenchModuleResource findWorkbenchModuleResourceByDeployPath(URI aModuleURI, URI aDeployedResourcePath)  throws UnresolveableURIException {
		WorkbenchModule module = findWorkbenchModuleByDeployName(getDeployedNameForModule(aModuleURI));
		return module.findWorkbenchModuleResourceByDeployPath(aDeployedResourcePath);
	}
	
	public WorkbenchModuleResource findWorkbenchModuleResourceByDeployPath(URI aModuleResourcePath) throws UnresolveableURIException {
		ModuleURIUtil.ensureValidFullyQualifiedModuleURI(aModuleResourcePath);
		URI moduleURI = aModuleResourcePath.trimSegments(aModuleResourcePath.segmentCount() - 3);
		URI deployedPath = aModuleResourcePath.deresolve(aModuleResourcePath.trimSegments(aModuleResourcePath.segmentCount() - 4));
		WorkbenchModule module = findWorkbenchModuleByDeployName(getDeployedNameForModule(moduleURI));
		return module.findWorkbenchModuleResourceByDeployPath(deployedPath);
	}	
	
	public WorkbenchModuleResource[] findWorkbenchModuleResourcesBySourcePath(URI aProjectRelativePath) throws UnresolveableURIException {
		ProjectModules projectModules = getProjectModules();
		EList modules = projectModules.getWorkbenchModules();
		
		WorkbenchModule module = null;
		WorkbenchModuleResource resource = null;
		List foundResources = new ArrayList(); 		
		for(int i=0; i<modules.size(); i++) {
			 module = (WorkbenchModule) modules.get(i);
			 resource = module.findWorkbenchModuleResourceBySourcePath(aProjectRelativePath);
			 if(resource != null)
			 	foundResources.add(resource);
		}
		if(foundResources.size() > 0)
			return (WorkbenchModuleResource[]) foundResources.toArray(new WorkbenchModuleResource[foundResources.size()]);
		return NO_RESOURCES;
	}	

	public WorkbenchModule findWorkbenchModuleByDeployName(String aModuleName) {
		return getProjectModules().findWorkbenchModule(aModuleName);
	}	

}
