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

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.wst.common.internal.emfworkbench.WorkbenchResourceHelper;
import org.eclipse.wst.common.modulecore.ModuleCoreFactory;
import org.eclipse.wst.common.modulecore.ModuleCoreNature;
import org.eclipse.wst.common.modulecore.ModuleCorePackage;
import org.eclipse.wst.common.modulecore.ModuleStructuralModel;
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

	public interface Constants {

		public interface ModuleURISegments {
			public static final int SUB_PROTOCOL = 0;
			public static final int PROJECT_NAME = 1;
			public static final int MODULE_NAME = 1;
		}
	}

	public static ModuleCore INSTANCE = new ModuleCore();
	static String MODULE_META_FILE_NAME = ".wtpmodules";

	public ModuleStructuralModel getModuleStructuralModelForRead(IProject aProject, Object anAccessorKey) {
		ModuleCoreNature aNature = ModuleCoreNature.getModuleCoreNature(aProject);
		return aNature.getModuleStructuralModelForRead(anAccessorKey);
	}

	public ModuleStructuralModel getModuleStructuralModelForWrite(IProject aProject, Object anAccessorKey) {
		ModuleCoreNature aNature = ModuleCoreNature.getModuleCoreNature(aProject);
		return aNature.getModuleStructuralModelForWrite(anAccessorKey);
	}

	public ModuleStructuralModel getModuleStructuralModelForRead(URI aModuleURI, Object anAccessorKey) throws UnresolveableURIException {
		return getModuleCoreNature(aModuleURI).getModuleStructuralModelForRead(anAccessorKey);
	}
	
	public ModuleStructuralModel getModuleStructuralModelForWrite(URI aModuleURI, Object anAccessorKey) throws UnresolveableURIException {
		return getModuleCoreNature(aModuleURI).getModuleStructuralModelForWrite(anAccessorKey);
	}

	public IProject getContainingProject(URI aModuleURI) throws UnresolveableURIException {
		ensureValidFullyQualifiedModuleURI(aModuleURI);
		String projectName = aModuleURI.segment(ModuleCore.Constants.ModuleURISegments.PROJECT_NAME);
		if(projectName == null || projectName.length() == 0)
			throw new UnresolveableURIException(aModuleURI);
		return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
	}	
	
	private String getDeployedNameForModule(URI aModuleURI) throws UnresolveableURIException { 
		ensureValidFullyQualifiedModuleURI(aModuleURI);
		return aModuleURI.segment(ModuleCore.Constants.ModuleURISegments.MODULE_NAME);
	}
	
	public ModuleCoreNature getModuleCoreNature(URI aModuleURI) throws UnresolveableURIException {
		return ModuleCoreNature.getModuleCoreNature(getContainingProject(aModuleURI));
	}
	
	public ProjectModules getProjectModules(ModuleStructuralModel aModuleStucturalModule) {
		return (ProjectModules) aModuleStucturalModule.getPrimaryRootObject();
	}

	public WorkbenchModule[] getWorkbenchModules(ModuleStructuralModel aModuleStucturalModule) {
		List wbModules = getProjectModules(aModuleStucturalModule).getWorkbenchModules();
		return (WorkbenchModule[]) wbModules.toArray(new WorkbenchModule[wbModules.size()]);
	}

	public void ensureValidFullyQualifiedModuleURI(URI aModuleURI) throws UnresolveableURIException {
		if (aModuleURI.segmentCount() < 3)
			throw new UnresolveableURIException(aModuleURI);
	}
	
	public WorkbenchModuleResource findWorkbenchModuleResourceByDeployPath(ModuleStructuralModel aModuleStucturalModule, URI aModuleURI, URI aDeployedResourcePath)  throws UnresolveableURIException {
	
		WorkbenchModule module = ModuleCore.INSTANCE.findWorkbenchModuleByDeployName(aModuleStucturalModule, getDeployedNameForModule(aModuleURI));
		return module.findWorkbenchModuleResourceByDeployPath(aDeployedResourcePath);
	}
	
	public WorkbenchModuleResource findWorkbenchModuleResourceByDeployPath(ModuleStructuralModel aModuleStucturalModule, URI aModuleResourcePath) throws UnresolveableURIException {
		ensureValidFullyQualifiedModuleURI(aModuleResourcePath);
		URI moduleURI = aModuleResourcePath.trimSegments(aModuleResourcePath.segmentCount() - 3);
		URI deployedPath = aModuleResourcePath.deresolve(aModuleResourcePath.trimSegments(aModuleResourcePath.segmentCount() - 4));
		WorkbenchModule module = ModuleCore.INSTANCE.findWorkbenchModuleByDeployName(aModuleStucturalModule, getDeployedNameForModule(moduleURI));
		return module.findWorkbenchModuleResourceByDeployPath(deployedPath);
	}	

	public Resource.Factory getResourceFactory(URI uri) {
		return Resource.Factory.Registry.INSTANCE.getFactory(uri);
	}

	public WorkbenchModule findWorkbenchModuleByDeployName(ModuleStructuralModel aStructuralModel, String aModuleName) {
		return getProjectModules(aStructuralModel).findWorkbenchModule(aModuleName);
	}	
	
	public String getOutputContainerRoot() {
	    return ".deployables"; //$NON-NLS-1$
	}

}
	public String getOutputContainerRoot() {
	    return ".deployables"; //$NON-NLS-1$
	}
}
