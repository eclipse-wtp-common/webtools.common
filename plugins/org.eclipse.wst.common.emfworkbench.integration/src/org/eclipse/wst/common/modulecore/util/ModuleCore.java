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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.wst.common.modulecore.DependentModule;
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
public class ModuleCore implements IEditModelHandler {

	protected static final String DEPLOYABLES_ROOT = ".deployables/"; //$NON-NLS-1$
	
	public interface Constants {

		public interface ModuleURISegments {
			public static final int SUB_PROTOCOL = 0;
			public static final int PROJECT_NAME = 1;
			public static final int MODULE_NAME = 2;
		}
	}

	// TODO Use URIs should be minimized
	public static final Class ADAPTER_TYPE = ModuleCore.class;
	private static final WorkbenchModuleResource[] NO_RESOURCES = new WorkbenchModuleResource[0];

	static String MODULE_META_FILE_NAME = ".wtpmodules"; //$NON-NLS-1$

	private final ModuleStructuralModel structuralModel;
	private final Map dependentCores = new HashMap();
	private boolean isStructuralModelSelfManaged;

	public static ModuleCore getModuleCoreForRead(IProject aProject) {
		return new ModuleCore(ModuleCoreNature.getModuleCoreNature(aProject), true);
	}

	public static ModuleCore getModuleCoreForWrite(IProject aProject) {
		return new ModuleCore(ModuleCoreNature.getModuleCoreNature(aProject), false);
	}

	public static ModuleCoreNature getModuleCoreNature(URI aModuleURI) throws UnresolveableURIException {
		return ModuleCoreNature.getModuleCoreNature(getContainingProject(aModuleURI));
	}

	public static URI getOutputContainerRoot(WorkbenchModule aWorkbenchModule) {
		return URI.createURI(DEPLOYABLES_ROOT + aWorkbenchModule.getDeployedName()); 
	}

	public static Resource.Factory getResourceFactory(URI aURI) {
		return Resource.Factory.Registry.INSTANCE.getFactory(aURI);
	}

	/*
	 * Need to evaluate whether this should be part of the adaptive nature of this class or the
	 * utility nature of this class
	 */
	public static IProject getContainingProject(URI aModuleURI) throws UnresolveableURIException {
		ModuleURIUtil.ensureValidFullyQualifiedModuleURI(aModuleURI);
		String projectName = aModuleURI.segment(ModuleCore.Constants.ModuleURISegments.PROJECT_NAME);
		if (projectName == null || projectName.length() == 0)
			throw new UnresolveableURIException(aModuleURI);
		return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
	}

	public static String getDeployedNameForModule(URI aModuleURI) throws UnresolveableURIException {
		ModuleURIUtil.ensureValidFullyQualifiedModuleURI(aModuleURI);
		return aModuleURI.segment(ModuleCore.Constants.ModuleURISegments.MODULE_NAME);
	}

	public static ResourceTreeRoot getSourceResourceTreeRoot(WorkbenchModule aModule) {
		ResourceTreeRootAdapter resourceTreeAdapter = (ResourceTreeRootAdapter) EcoreUtil.getAdapter(aModule.eAdapters(), ResourceTreeRootAdapter.SOURCE_ADAPTER_TYPE);
		if (resourceTreeAdapter != null)
			return resourceTreeAdapter.getResourceTreeRoot();
		resourceTreeAdapter = new ResourceTreeRootAdapter(ResourceTreeRootAdapter.SOURCE_TREE);
		aModule.eAdapters().add(resourceTreeAdapter);
		return resourceTreeAdapter.getResourceTreeRoot();
	}

	public static ResourceTreeRoot getDeployResourceTreeRoot(WorkbenchModule aModule) {
		ResourceTreeRootAdapter resourceTreeAdapter = (ResourceTreeRootAdapter) EcoreUtil.getAdapter(aModule.eAdapters(), ResourceTreeRootAdapter.DEPLOY_ADAPTER_TYPE);
		if (resourceTreeAdapter != null)
			return resourceTreeAdapter.getResourceTreeRoot();
		resourceTreeAdapter = new ResourceTreeRootAdapter(ResourceTreeRootAdapter.DEPLOY_TREE);
		aModule.eAdapters().add(resourceTreeAdapter);
		return resourceTreeAdapter.getResourceTreeRoot();
	}

	public static IResource getResource(WorkbenchModuleResource aResource) {
		EclipseResourceAdapter eclipseResourceAdapter = (EclipseResourceAdapter) EcoreUtil.getAdapter(aResource.eAdapters(), EclipseResourceAdapter.ADAPTER_TYPE);
		if (eclipseResourceAdapter != null)
			return eclipseResourceAdapter.getEclipseResource();
		eclipseResourceAdapter = new EclipseResourceAdapter();
		aResource.eAdapters().add(eclipseResourceAdapter);
		return eclipseResourceAdapter.getEclipseResource();
	}

	public ModuleCore(ModuleCoreNature aNature, boolean toAccessAsReadOnly) {
		if (toAccessAsReadOnly)
			structuralModel = aNature.getModuleStructuralModelForRead(this);
		else
			structuralModel = aNature.getModuleStructuralModelForWrite(this);
		isStructuralModelSelfManaged = true;
	}

	public ModuleCore(ModuleStructuralModel aStructuralModel) {
		structuralModel = aStructuralModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.modulecore.util.IEditModelHandler#save()
	 */
	public void save(IProgressMonitor aMonitor) {
		structuralModel.save(aMonitor, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.modulecore.util.IEditModelHandler#saveIfNecessary()
	 */
	public void saveIfNecessary(IProgressMonitor aMonitor) {
		structuralModel.saveIfNecessary(aMonitor, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.modulecore.util.IEditModelHandler#dispose()
	 */
	public void dispose() {
		if(isStructuralModelSelfManaged)
			structuralModel.releaseAccess(this);
		if (dependentCores.size() > 0) {
			synchronized (dependentCores) {
				for (Iterator cores = dependentCores.values().iterator(); cores.hasNext();)
					((ModuleCore) cores.next()).dispose();
			}
		}
	}

	public ProjectModules getProjectModules() {
		return (ProjectModules) structuralModel.getPrimaryRootObject();
	}

	public WorkbenchModule[] getWorkbenchModules() {
		List wbModules = getProjectModules().getWorkbenchModules();
		return (WorkbenchModule[]) wbModules.toArray(new WorkbenchModule[wbModules.size()]);
	}

	public WorkbenchModuleResource[] findWorkbenchModuleResourceByDeployPath(URI aModuleURI, URI aDeployedResourcePath) throws UnresolveableURIException {
		WorkbenchModule module = findWorkbenchModuleByDeployName(getDeployedNameForModule(aModuleURI));
		return module.findWorkbenchModuleResourceByDeployPath(aDeployedResourcePath);
	}

	public WorkbenchModuleResource[] findWorkbenchModuleResourceByDeployPath(URI aModuleResourcePath) throws UnresolveableURIException {
		ModuleURIUtil.ensureValidFullyQualifiedModuleURI(aModuleResourcePath);
		URI moduleURI = aModuleResourcePath.trimSegments(aModuleResourcePath.segmentCount() - 3);
		URI deployedPath = ModuleURIUtil.trimToDeployPathSegment(aModuleResourcePath);
		WorkbenchModule module = findWorkbenchModuleByDeployName(getDeployedNameForModule(moduleURI));
		return module.findWorkbenchModuleResourceByDeployPath(deployedPath);
	}

	public WorkbenchModuleResource[] findWorkbenchModuleResourcesBySourcePath(URI aWorkspaceRelativePath) throws UnresolveableURIException {
		ProjectModules projectModules = getProjectModules();
		EList modules = projectModules.getWorkbenchModules();

		WorkbenchModule module = null;
		WorkbenchModuleResource[] resources = null;
		List foundResources = new ArrayList();
		for (int i = 0; i < modules.size(); i++) {
			module = (WorkbenchModule) modules.get(i);
			resources = module.findWorkbenchModuleResourceBySourcePath(aWorkspaceRelativePath);
			if (resources.length != 0)
				foundResources.addAll(Arrays.asList(resources));
		}
		if (foundResources.size() > 0)
			return (WorkbenchModuleResource[]) foundResources.toArray(new WorkbenchModuleResource[foundResources.size()]);
		return NO_RESOURCES;
	}

	public WorkbenchModule findWorkbenchModuleByDeployName(String aModuleName) {
		return getProjectModules().findWorkbenchModule(aModuleName);
	}

	public WorkbenchModule findWorkbenchModuleByModuleURI(URI aModuleURI) throws UnresolveableURIException {
		ModuleURIUtil.ensureValidFullyQualifiedModuleURI(aModuleURI);
		String projectName = aModuleURI.segment(Constants.ModuleURISegments.PROJECT_NAME);
		String moduleName = aModuleURI.segment(Constants.ModuleURISegments.MODULE_NAME);
		/* Accessing a local module */
		if (structuralModel.getProject().getName().equals(projectName)) {
			return findWorkbenchModuleByDeployName(moduleName);
		}
		return getDependentModuleCore(aModuleURI).findWorkbenchModuleByDeployName(moduleName);
	}
	
	public boolean isLocalDependency(DependentModule aDependentModule) {
		String localProjectName = structuralModel.getProject().getName();
		String dependentProjectName = aDependentModule.getHandle().segment(Constants.ModuleURISegments.PROJECT_NAME);
		return localProjectName.equals(dependentProjectName);
	}

	/**
	 * @param aModuleURI
	 * @return
	 * @throws UnresolveableURIException
	 */
	private ModuleCore getDependentModuleCore(URI aModuleURI) throws UnresolveableURIException {
		ModuleCore dependentCore = (ModuleCore) dependentCores.get(aModuleURI);
		if (dependentCore != null)
			return dependentCore;
		synchronized (dependentCores) {
			dependentCore = (ModuleCore) dependentCores.get(aModuleURI);
			if (dependentCore == null){
				dependentCore = getModuleCoreForRead(getContainingProject(aModuleURI));
				dependentCores.put(aModuleURI, dependentCore);
			}
		} 
		return dependentCore;
	}

	/**
	 * 
	 */
	public void prepareProjectModulesIfNecessary() {
		structuralModel.prepareProjectModulesIfNecessary();		
	}
}
