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

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
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
 * Provides a Facade pattern for accessing the Web Tools 
 * Platform EMF Module Model. ModuleCore can be used as
 * a static utility or an instance adapter. 
 * </p>
 * <p>
 * ModuleCore hides the management of accessing Edit 
 * models ({@see org.eclipse.wst.common.modulecore.ModuleStructuralModel})
 * correctly. Each project has exactly one 
 * ({@see org.eclipse.wst.common.modulecore.ModuleStructuralModel})
 * for read and one for write. Each of these is shared among all 
 * clients and reference counted as necessary. Clients should
 * use ModuleCore when working with the WTP Modules Model. 
 * easier. 
 * </p>
 * 
 * <p>
 * Each ModuleCore instance facade is designed to manage the Edit Model
 * lifecycle for clients. However, while each ModuleCore is designed to 
 * be passed around as needed, clients must enforce the ModuleCore lifecycle.
 * The most common method of acquiring a ModuleCore instance facade is to use 
 * {@see #getModuleCoreForRead(IProject)} or {@see #getModuleCoreForWrite(IProject)}. 
 * When clients have concluded their use of the instance, clients must call 
 * {@see #dispose()}.  
 * </p> 
 *  
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 * 
 * @see org.eclipse.wst.common.modulecore.ModuleCoreNature
 * @see org.eclipse.wst.common.modulecore.ModuleStructuralModel
 */
// TODO Add API for creating model objects (WorkbenchModule, WorkbenchModuleResource, ModuleType)
public class ModuleCore implements IEditModelHandler {
	
	public static interface ModuleURI {
		public static final int SUB_PROTOCOL_INDX = 0;
		public static final int PROJECT_NAME_INDX = 1;
		public static final int MODULE_NAME_INDX = 2;
	} 

	public static final Class ADAPTER_TYPE = ModuleCore.class;
	
	static final String DEPLOYABLES_ROOT = ".deployables/"; //$NON-NLS-1$
	static String MODULE_META_FILE_NAME = ".wtpmodules"; //$NON-NLS-1$
	private static final WorkbenchModuleResource[] NO_RESOURCES = new WorkbenchModuleResource[0];
	private static final IFolder[] NO_FOLDERS = new IFolder[0];
	  

	private final ModuleStructuralModel structuralModel;
	private final Map dependentCores = new HashMap();
	private boolean isStructuralModelSelfManaged;
	private boolean isReadOnly;

	

	/**
	 * 
	 * <p>
	 * Each ModuleCore instance facade is tied to a specific project. A project
	 * may have multiple ModuleCore instance facades live at any given time.  
	 * </p>
	 * <p>
	 * Use to acquire a ModuleCore facade for a specific project that will not be
	 * used for editing.
	 * </p>
	 * 
	 * @param aProject The IProject that contains the WTP Modules model to load
	 * @return A ModuleCore instance facade to access the WTP Modules Model 
	 */
	public static ModuleCore getModuleCoreForRead(IProject aProject) {
		return new ModuleCore(ModuleCoreNature.getModuleCoreNature(aProject), true);
	}
	
	/**
	 * 
	 * <p>
	 * Each ModuleCore instance facade is tied to a specific project. A project
	 * may have multiple ModuleCore instance facades live at any given time.  
	 * </p>
	 * <p>
	 * Use to acquire a ModuleCore facade for a specific project that may be 
	 * used to modify the model.
	 * </p>
	 * @param aProject The IProject that contains the WTP Modules model to load
	 * @return A ModuleCore instance facade to access the WTP Modules Model 
	 */
	public static ModuleCore getModuleCoreForWrite(IProject aProject) {
		return new ModuleCore(ModuleCoreNature.getModuleCoreNature(aProject), false);
	}

	/**
	 * <p>
	 * A convenience API to fetch the {@see ModuleCoreNature} for a particular
	 * module URI. The module URI must be of the valid form, or an exception 
	 * will be thrown. The module URI must be contained by a project that 
	 * has a {@see ModuleCoreNature} or null will be returned. 
	 * </p> 
	 * <p>
	 * <b>The following method may return null.</b>
	 * </p>
	 * @param aModuleURI A valid, fully-qualified module URI
	 * @return The ModuleCoreNature of the project associated with aModuleURI
	 * @throws UnresolveableURIException If the supplied module URI is invalid or unresolveable.
	 */
	public static ModuleCoreNature getModuleCoreNature(URI aModuleURI) throws UnresolveableURIException {
		IProject container = getContainingProject(aModuleURI);
		if(container != null)
			return ModuleCoreNature.getModuleCoreNature(container);
		return null;
	}


	

	/**
	 * <p>
	 * For {@see WorkbenchModule}s that are contained within a project, the 
	 * containing project can be determined with the {@see WorkbenchModule}'s
	 * fully-qualified module URI.
	 * </p>
	 * <p>
	 * The following method will return the the corresponding project 
	 * for the supplied module URI, if it can be determined.
	 * </p>
	 * <p>
	 * The method will not return an inaccessible project. 
	 * </p>
	 * <p>
	 * <b>The following method may return null.</b>
	 * </p>
	 * @param aModuleURI A valid, fully-qualified module URI
	 * @return The project that contains the module referenced by the module URI 
	 * @throws UnresolveableURIException If the supplied module URI is invalid or unresolveable.
	 */
	public static IProject getContainingProject(URI aModuleURI) throws UnresolveableURIException {
		ModuleURIUtil.ensureValidFullyQualifiedModuleURI(aModuleURI);
		String projectName = aModuleURI.segment(ModuleCore.ModuleURI.PROJECT_NAME_INDX);
		if (projectName == null || projectName.length() == 0)
			throw new UnresolveableURIException(aModuleURI);
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if(project.isAccessible())
			return project;
		return null;
	}

	/**
	 * <p>
	 * A fully-qualified module URI will contain enough information to determine 
	 * the deployed name of the module. 
	 * </p>
	 * @param aModuleURI A valid, fully-qualified module URI
	 * @return The deployed name of the {@see WorkbenchModule} referenced by the module URI
	 * @throws UnresolveableURIException If the supplied module URI is invalid or unresolveable.
	 */
	public static String getDeployedNameForModule(URI aModuleURI) throws UnresolveableURIException {
		ModuleURIUtil.ensureValidFullyQualifiedModuleURI(aModuleURI);
		return aModuleURI.segment(ModuleCore.ModuleURI.MODULE_NAME_INDX);
	}
	
	// TODO Rename the following method to be more clear e.g. getCorrespondingIResource() or getEclipseResource()
	/**
	 * <p>
	 * Returns the corresponding Eclipse IResource, if it can be determined,
	 * for the given {@see WorkbenchModuleResource}.
	 * The {@see WorkbenchModuleResource#getSourcePath()} must return 
	 * a valid resource path for this method to return a valid value. The
	 * returned value may be either an {@see org.eclipse.core.resources.IFile}
	 * or {@see org.eclipse.core.resources.IFolder}. A client may use
	 * the return value of {@see IResource#getType()} to determine what type
	 * of resource was returned. (@see IResource#FILE} or {@see IResource#FOLDER}).
	 * </p>
	 * <p>
	 * <b>The following method may return null.</b>
	 * </p>
	 * @param aModuleResource A WorkbenchModuleResource with a valid sourcePath 
	 * @return The corresponding Eclipse IResource, if available. 
	 */
	public static IResource getResource(WorkbenchModuleResource aModuleResource) {
		EclipseResourceAdapter eclipseResourceAdapter = (EclipseResourceAdapter) EcoreUtil.getAdapter(aModuleResource.eAdapters(), EclipseResourceAdapter.ADAPTER_TYPE);
		if (eclipseResourceAdapter != null)
			return eclipseResourceAdapter.getEclipseResource();
		eclipseResourceAdapter = new EclipseResourceAdapter();
		aModuleResource.eAdapters().add(eclipseResourceAdapter);
		return eclipseResourceAdapter.getEclipseResource();
	}

	
	// TODO Change this to return an IContainer (or IFolder)
	/**
	 * <p>
	 * Returns a URI for the supplied {@see WorkbenchModule}. The URI will be
	 * relative to project root of the flexible project that contains the 
	 * {@see WorkbenchModule}.
	 * </p>
	 * @param aWorkbenchModule A valid WorkbenchModule
	 * @return A project-relative URI of the output folder for aWorkbenchModoule.
	 */
	public static URI getOutputContainerRoot(WorkbenchModule aWorkbenchModule) {
		return URI.createURI(DEPLOYABLES_ROOT + aWorkbenchModule.getDeployedName()); 
	}

	// TODO Change this to return an array IContainer (or IFolder)
	/**
	 * <p>
	 * Returns a collection of the output containers for the supplied
	 * project. The collection may be a single root output container or 
	 * an array of output containers without a common root. For clients that 
	 * are looking for an output container for a specific {@see WorkbenchModule}, 
	 * see {@see #getOutputContainerRoot(WorkbenchModule)}.  
	 * </p>
	 * <p>
	 * If the project is not a ModuleCore project, or has no ModuleCore output
	 * containers, an empty array will be returned. 
	 * </p>
	 * @param aProject A project with a {@see ModuleCoreNature}
	 * @return An array of output containers or an empty array.
	 */
	public static IResource[] getOutputContainersForProject(IProject aProject) {
		ModuleCoreNature moduleCoreNature = ModuleCoreNature.getModuleCoreNature(aProject);
		if(moduleCoreNature == null)
			return NO_FOLDERS;
	    IFolder folder = aProject.getFolder(new Path(DEPLOYABLES_ROOT));	    
	    IFolder[] outputResources = {folder};
	    return outputResources;
	}
	
	protected ModuleCore(ModuleCoreNature aNature, boolean toAccessAsReadOnly) {
		if (toAccessAsReadOnly)
			structuralModel = aNature.getModuleStructuralModelForRead(this);
		else
			structuralModel = aNature.getModuleStructuralModelForWrite(this);
		isReadOnly = toAccessAsReadOnly;
		isStructuralModelSelfManaged = true;
	}

	/**
	 * <p>
	 * The following constructor is used to manage an already loaded model. 
	 * Clients should use the following line if they intend to use this constructor:
	 * <br><br>
	 * <code>ModuleCore moduleCore = (ModuleCore) aStructuralModel.getAdapter(ModuleCore.ADAPTER_TYPE)</code>.
	 * </p>
	 * @param aStructuralModel The edit model to be managed by this ModuleCore
	 */
	protected ModuleCore(ModuleStructuralModel aStructuralModel) {
		structuralModel = aStructuralModel;
	}

	/**
	 * <p>
	 * Force a save of the underlying model. The following method
	 * should be used with care. Unless required, use 
	 * {@see #saveIfNecessary(IProgressMonitor) instead.
	 * </p>
	 * 
	 * @see org.eclipse.wst.common.modulecore.util.IEditModelHandler#save()
	 * @throws IllegalStateException If the ModuleCore object was created as read-only
	 */
	public void save(IProgressMonitor aMonitor) {
		if(isReadOnly)
			throw new IllegalStateException("Cannot save model changes. ModuleCore instance facade was loaded as read-only.");
		structuralModel.save(aMonitor, this);
	}

	/**
	 * <p>
	 * Save the underlying model only if no other clients are currently
	 * using the model. If the model is not shared, it will be saved. If 
	 * it is shared, the save will be deferred. 
	 * </p>
	 * 
	 * @see org.eclipse.wst.common.modulecore.util.IEditModelHandler#saveIfNecessary()
	 * @throws IllegalStateException If the ModuleCore object was created as read-only
	 */
	public void saveIfNecessary(IProgressMonitor aMonitor) {
		if(isReadOnly)
			throw new IllegalStateException("Cannot save model changes. ModuleCore instance facade was loaded as read-only.");
		structuralModel.saveIfNecessary(aMonitor, this);
	}

	/**
	 * <p>
	 * Clients must call the following method when they have finished using the model,
	 * even if the ModuleCore instance facade was created as read-only. 
	 * </p>
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

	/**
	 * <p>
	 * Creates a default WTP Module Structural Model file if
	 * necessary.
	 * </p> 
	 */
	public void prepareProjectModulesIfNecessary() {
		structuralModel.prepareProjectModulesIfNecessary();		
	}

	// TODO Rename the following method to be getModelRootObject()
	/** 
	 * <p>
	 * When loaded for write, the current ModuleCore can return 
	 * the root object, which can be used to add or remove
	 * {@see WorkbenchModule}s. If a client needs to just read
	 * the existing {@see WorkbenchModule}s, use {@see #getWorkbenchModules()}.
	 * </p>
	 * @return The root object of the underlying model
	 */
	public ProjectModules getProjectModules() {
		return (ProjectModules) structuralModel.getPrimaryRootObject();
	}
	
	/** 
	 * <p>
	 * Clients that wish to modify the individual {@see WorkbenchModule} 
	 * instances may use this method. If clients need to add or remove
	 * {@see WorkbenchModule} instances, use {@see #getProjectModules()}
	 * to get the root object and then access the contained 
	 * {@see WorkbenchModule}s through {@see ProjectModules#getWorkbenchModules()}. 
	 * @return The WorkbenchModules of the underlying model, if any.
	 */
	public WorkbenchModule[] getWorkbenchModules() {
		List wbModules = getProjectModules().getWorkbenchModules();
		return (WorkbenchModule[]) wbModules.toArray(new WorkbenchModule[wbModules.size()]);
	}

	/**
	 * <p>
	 * Search the given module (indicated by aModuleURI) for the 
	 * {@see WorkbenchModuleResource}s identified by the module-relative 
	 * path (indicated by aDeployedResourcePath). 
	 * </p>
	 * @param aModuleURI A valid, fully-qualified module URI
	 * @param aDeployedResourcePath A module-relative path to a deployed file
	 * @return An array of WorkbenchModuleResources that contain the URI specified by the module-relative aDeployedResourcePath
	 * @throws UnresolveableURIException If the supplied module URI is invalid or unresolveable.
	 */
	public WorkbenchModuleResource[] findWorkbenchModuleResourceByDeployPath(URI aModuleURI, URI aDeployedResourcePath) throws UnresolveableURIException {
		WorkbenchModule module = findWorkbenchModuleByDeployName(getDeployedNameForModule(aModuleURI));
		return module.findWorkbenchModuleResourceByDeployPath(aDeployedResourcePath);
	}

	/**
	 * <p>
	 * Search the the module (indicated by the root of aModuleResourcePath) 
	 * for the {@see WorkbenchModuleResource}s identified by the module-qualified
	 * path (indicated by aDeployedResourcePath). 
	 * </p>
	 * @param aModuleResourcePath A valid fully-qualified URI of a deployed resource within a specific WorkbenchModule  
	 * @return An array of WorkbenchModuleResources that contain the URI specified by aModuleResourcePath
	 * @throws UnresolveableURIException If the supplied module URI is invalid or unresolveable.
	 */
	public WorkbenchModuleResource[] findWorkbenchModuleResourceByDeployPath(URI aModuleResourcePath) throws UnresolveableURIException {
		ModuleURIUtil.ensureValidFullyQualifiedModuleURI(aModuleResourcePath);
		URI moduleURI = aModuleResourcePath.trimSegments(aModuleResourcePath.segmentCount() - 3);
		URI deployedPath = ModuleURIUtil.trimToDeployPathSegment(aModuleResourcePath);
		WorkbenchModule module = findWorkbenchModuleByDeployName(getDeployedNameForModule(moduleURI));
		return module.findWorkbenchModuleResourceByDeployPath(deployedPath);
	}

	// TODO Change the following to use an IResource as the parameter
	/**
	 * <p>
	 * Locates the {@see WorkbenchModuleResource}s that contain the 
	 * supplied resource in their source path. There are no representations 
	 * about the containment of the {@see WorkbenchModuleResource}s which are 
	 * returned. The only guarantee is that the returned elements are 
	 * contained within the same project. 
	 * </p>
	 * <p>
	 * The sourcePath of each {@see WorkbenchModuleResource} will be 
	 * mapped to either an IFile or an IFolder. As a result, if the
	 * {@see WorkbenchModuleResource} is a container mapping, the 
	 * path of the supplied resource may not be identical the sourcePath 
	 * of the {@see WorkbenchModuleResource}. 
	 * </p>   
	 * 
	 * @param aWorkspaceRelativePath A valid fully-qualified workspace-relative path of a given resource
	 * @return An array of WorkbenchModuleResources which have sourcePaths that contain the given resource
	 * @throws UnresolveableURIException If the supplied module URI is invalid or unresolveable.
	 */
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

	/**
	 * <p>
	 * Returns the {@see WorkbenchModule} contained by the current 
	 * ModuleCore with the deploy name aModuleName. 
	 * <p>
	 * <b>The following method may return null.</b>
	 * </p>
	 * @param aModuleName
	 * @return The {@see WorkbenchModule} contained by the current ModuleCore with the deploy name aModuleName
	 * @see WorkbenchModule#getDeployedName()
	 */
	public WorkbenchModule findWorkbenchModuleByDeployName(String aModuleName) {
		return getProjectModules().findWorkbenchModule(aModuleName);
	}

	/**
	 * <p>
	 * Locate and return the {@see WorkbenchModule} referenced by the 
	 * fully-qualified aModuleURI. The method will work correctly even 
	 * if the requested {@see WorkbenchModule} is contained by another 
	 * project.
	 * </p>
	 * @param aModuleURI A valid, fully-qualified module URI
	 * @return The {@see WorkbenchModule} referenced by aModuleURI
	 * @throws UnresolveableURIException If the supplied module URI is invalid or unresolveable.
	 * @see WorkbenchModule#getHandle()
	 */
	public WorkbenchModule findWorkbenchModuleByModuleURI(URI aModuleURI) throws UnresolveableURIException {
		ModuleURIUtil.ensureValidFullyQualifiedModuleURI(aModuleURI);
		String projectName = aModuleURI.segment(ModuleCore.ModuleURI.PROJECT_NAME_INDX);
		String moduleName = aModuleURI.segment(ModuleCore.ModuleURI.MODULE_NAME_INDX);
		/* Accessing a local module */
		if (structuralModel.getProject().getName().equals(projectName)) {
			return findWorkbenchModuleByDeployName(moduleName);
		}
		return getDependentModuleCore(aModuleURI).findWorkbenchModuleByDeployName(moduleName);
	}
	
	/**
	 * <p>
	 * Returns true if the {@see DependentModule} references a {@see WorkbenchModule} 
	 * ({@see DependentModule#getHandle()}) which is contained by the project 
	 * that the current ModuleCore is managing. The following method will 
	 * determine if the dependency can be satisfied by the current project.  
	 * </p> 
	 * @param aDependentModule
	 * @return True if the {@see DependentModule} references a {@see WorkbenchModule} managed directly by the current ModuleCore
	 */
	public boolean isLocalDependency(DependentModule aDependentModule) {
		String localProjectName = structuralModel.getProject().getName();
		String dependentProjectName = aDependentModule.getHandle().segment(ModuleCore.ModuleURI.PROJECT_NAME_INDX);
		return localProjectName.equals(dependentProjectName);
	}

	/**
	 * @param aModuleURI A valid, fully-qualified module URI
	 * @return The ModuleCore facade for the supplied URI  
	 * @throws UnresolveableURIException If the supplied module URI is invalid or unresolveable.
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

}
