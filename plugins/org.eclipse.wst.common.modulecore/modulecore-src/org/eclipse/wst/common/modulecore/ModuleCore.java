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
package org.eclipse.wst.common.modulecore;

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
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.wst.common.modulecore.internal.util.EclipseResourceAdapter;

/**
 * <p>
 * Provides a Facade pattern for accessing the Web Tools Platform EMF Module Model. ModuleCore can
 * be used as a static utility or an instance adapter.
 * </p>
 * <p>
 * ModuleCore hides the management of accessing edit models (
 * {@see org.eclipse.wst.common.modulecore.ModuleStructuralModel}) correctly. Each project has
 * exactly one ({@see org.eclipse.wst.common.modulecore.ModuleStructuralModel}) for read and
 * exactly one for write. Each of these is shared among all clients and reference counted as
 * necessary. Clients should use ModuleCore when working with the WTP Modules Model. easier.
 * </p>
 * 
 * <p>
 * Each ModuleCore instance facade is designed to manage the Edit Model lifecycle for clients.
 * However, while each ModuleCore is designed to be passed around as needed, clients must enforce
 * the ModuleCore lifecycle. The most common method of acquiring a ModuleCore instance facade is to
 * use {@see #getModuleCoreForRead(IProject)}or {@see #getModuleCoreForWrite(IProject)}.
 * </p>
 * <p>
 * When clients have concluded their use of the instance, <b>clients must call {@see #dispose()}
 * </b>.
 * </p>
 * 
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 * 
 * @see org.eclipse.wst.common.modulecore.ModuleCoreNature
 * @see org.eclipse.wst.common.modulecore.ModuleStructuralModel
 */
public class ModuleCore implements IEditModelHandler {

	static interface ModuleURI {
		public static final int SUB_PROTOCOL_INDX = 0;
		public static final int PROJECT_NAME_INDX = 1;
		public static final int MODULE_NAME_INDX = 2;
	}

	public static final Class ADAPTER_TYPE = ModuleCore.class;

	static final String DEPLOYABLES_ROOT = ".deployables/"; //$NON-NLS-1$
	static String MODULE_META_FILE_NAME = ".wtpmodules"; //$NON-NLS-1$

	private final static ModuleCoreFactory MODULE_FACTORY = ModuleCoreFactory.eINSTANCE;
	private static final WorkbenchModule[] NO_MODULES = new WorkbenchModule[0];
	private static final WorkbenchModuleResource[] NO_RESOURCES = new WorkbenchModuleResource[0];
	private static final IFolder[] NO_FOLDERS = new IFolder[0];


	private final ModuleStructuralModel structuralModel;
	private final Map dependentCores = new HashMap();
	private boolean isStructuralModelSelfManaged;
	private boolean isReadOnly;



	/**
	 * 
	 * <p>
	 * Each ModuleCore instance facade is tied to a specific project. A project may have multiple
	 * ModuleCore instance facades live at any given time.
	 * </p>
	 * <p>
	 * Use to acquire a ModuleCore facade for a specific project that will not be used for editing.
	 * Invocations of any save*() API on an instance returned from this method will throw
	 * exceptions.
	 * </p>
	 * 
	 * @param aProject
	 *            The IProject that contains the WTP Modules model to load
	 * @return A ModuleCore instance facade to access the WTP Modules Model
	 */
	public static ModuleCore getModuleCoreForRead(IProject aProject) {
		return new ModuleCore(ModuleCoreNature.getModuleCoreNature(aProject), true);
	}

	/**
	 * 
	 * <p>
	 * Each ModuleCore instance facade is tied to a specific project. A project may have multiple
	 * ModuleCore instance facades live at any given time.
	 * </p>
	 * <p>
	 * Use to acquire a ModuleCore facade for a specific project that may be used to modify the
	 * model.
	 * </p>
	 * 
	 * @param aProject
	 *            The IProject that contains the WTP Modules model to load
	 * @return A ModuleCore instance facade to access the WTP Modules Model
	 */
	public static ModuleCore getModuleCoreForWrite(IProject aProject) {
		return new ModuleCore(ModuleCoreNature.getModuleCoreNature(aProject), false);
	}

	/**
	 * <p>
	 * A convenience API to fetch the {@see ModuleCoreNature}for a particular module URI. The
	 * module URI must be of the valid form, or an exception will be thrown. The module URI must be
	 * contained by a project that has a {@see ModuleCoreNature}or null will be returned.
	 * </p>
	 * <p>
	 * <b>The following method may return null. </b>
	 * </p>
	 * 
	 * @param aModuleURI
	 *            A valid, fully-qualified module URI
	 * @return The ModuleCoreNature of the project associated with aModuleURI
	 * @throws UnresolveableURIException
	 *             If the supplied module URI is invalid or unresolveable.
	 */
	public static ModuleCoreNature getModuleCoreNature(URI aModuleURI) throws UnresolveableURIException {
		IProject container = getContainingProject(aModuleURI);
		if (container != null)
			return ModuleCoreNature.getModuleCoreNature(container);
		return null;
	}

	/**
	 * <p>
	 * For {@see WorkbenchModule}s that are contained within a project, the containing project can
	 * be determined with the {@see WorkbenchModule}'s fully-qualified module URI.
	 * </p>
	 * <p>
	 * The following method will return the the corresponding project for the supplied module URI,
	 * if it can be determined.
	 * </p>
	 * <p>
	 * The method will not return an inaccessible project.
	 * </p>
	 * <p>
	 * <b>The following method may return null. </b>
	 * </p>
	 * 
	 * @param aModuleURI
	 *            A valid, fully-qualified module URI
	 * @return The project that contains the module referenced by the module URI
	 * @throws UnresolveableURIException
	 *             If the supplied module URI is invalid or unresolveable.
	 */
	public static IProject getContainingProject(URI aModuleURI) throws UnresolveableURIException {
		ModuleURIUtil.ensureValidFullyQualifiedModuleURI(aModuleURI);
		String projectName = aModuleURI.segment(ModuleCore.ModuleURI.PROJECT_NAME_INDX);
		if (projectName == null || projectName.length() == 0)
			throw new UnresolveableURIException(aModuleURI);
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if (project.isAccessible())
			return project;
		return null;
	}

	/**
	 * <p>
	 * Returns the corresponding Eclipse IResource, if it can be determined, for the given
	 * {@see WorkbenchModuleResource}. The {@see WorkbenchModuleResource#getSourcePath()}must
	 * return a valid resource path for this method to return a valid value. The returned value may
	 * be either an {@see org.eclipse.core.resources.IFile}or
	 * {@see org.eclipse.core.resources.IFolder}. A client may use the return value of
	 * {@see IResource#getType()}to determine what type of resource was returned. (@see
	 * IResource#FILE} or {@see IResource#FOLDER}).
	 * </p>
	 * <p>
	 * <b>The following method may return null. </b>
	 * </p>
	 * 
	 * @param aModuleResource
	 *            A WorkbenchModuleResource with a valid sourcePath
	 * @return The corresponding Eclipse IResource, if available.
	 */
	public static IResource getEclipseResource(WorkbenchModuleResource aModuleResource) {
		EclipseResourceAdapter eclipseResourceAdapter = (EclipseResourceAdapter) EcoreUtil.getAdapter(aModuleResource.eAdapters(), EclipseResourceAdapter.ADAPTER_TYPE);
		if (eclipseResourceAdapter != null)
			return eclipseResourceAdapter.getEclipseResource();
		eclipseResourceAdapter = new EclipseResourceAdapter();
		aModuleResource.eAdapters().add(eclipseResourceAdapter);
		return eclipseResourceAdapter.getEclipseResource();
	}

	/**
	 * <p>
	 * Returns a URI for the supplied {@see WorkbenchModule}. The URI will be relative to project
	 * root of the flexible project that contains the {@see WorkbenchModule}.
	 * </p>
	 * <p>
	 * <b>The following method may return null. </b>
	 * </p>
	 * 
	 * @param aWorkbenchModule
	 *            A valid WorkbenchModule
	 * @return A project-relative URI of the output folder for aWorkbenchModoule.
	 */
	public static IFolder getOutputContainerRoot(WorkbenchModule aWorkbenchModule) {
		IProject project = null;
		try {
			project = getContainingProject(aWorkbenchModule.getHandle());
		} catch (UnresolveableURIException e) {
		}
		if (project != null)
			return project.getFolder(new Path(DEPLOYABLES_ROOT + aWorkbenchModule.getDeployedName()));
		return null;
	}

	/**
	 * <p>
	 * Returns a collection of the output containers for the supplied project. The collection may be
	 * a single root output container or an array of output containers without a common root. For
	 * clients that are looking for an output container for a specific {@see WorkbenchModule}, see
	 * {@see #getOutputContainerRoot(WorkbenchModule)}.
	 * </p>
	 * <p>
	 * If the project is not a ModuleCore project, or has no ModuleCore output containers, an empty
	 * array will be returned.
	 * </p>
	 * 
	 * @param aProject
	 *            A project with a {@see ModuleCoreNature}
	 * @return An array of output containers or an empty array.
	 */
	public static IFolder[] getOutputContainersForProject(IProject aProject) {
		ModuleCoreNature moduleCoreNature = ModuleCoreNature.getModuleCoreNature(aProject);
		if (moduleCoreNature == null)
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
	 * The following constructor is used to manage an already loaded model. Clients should use the
	 * following line if they intend to use this constructor: <br>
	 * <br>
	 * <code>ModuleCore moduleCore = (ModuleCore) aStructuralModel.getAdapter(ModuleCore.ADAPTER_TYPE)</code>.
	 * </p>
	 * 
	 * @param aStructuralModel
	 *            The edit model to be managed by this ModuleCore
	 */
	public ModuleCore(ModuleStructuralModel aStructuralModel) {
		structuralModel = aStructuralModel;
	}

	/**
	 * <p>
	 * Force a save of the underlying model. The following method should be used with care. Unless
	 * required, use {@see #saveIfNecessary(IProgressMonitor) instead.
	 * </p>
	 * 
	 * @see org.eclipse.wst.common.modulecore.IEditModelHandler#save()
	 * @throws IllegalStateException
	 *             If the ModuleCore object was created as read-only
	 */
	public void save(IProgressMonitor aMonitor) {
		if (isReadOnly)
			throwAttemptedReadOnlyModification();
		structuralModel.save(aMonitor, this);
	}

	/**
	 * <p>
	 * Save the underlying model only if no other clients are currently using the model. If the
	 * model is not shared, it will be saved. If it is shared, the save will be deferred.
	 * </p>
	 * 
	 * @see org.eclipse.wst.common.modulecore.IEditModelHandler#saveIfNecessary()
	 * @throws IllegalStateException
	 *             If the ModuleCore object was created as read-only
	 */
	public void saveIfNecessary(IProgressMonitor aMonitor) {
		if (isReadOnly)
			throwAttemptedReadOnlyModification();
		structuralModel.saveIfNecessary(aMonitor, this);
	}

	/**
	 * <p>
	 * Clients must call the following method when they have finished using the model, even if the
	 * ModuleCore instance facade was created as read-only.
	 * </p>
	 * 
	 * @see org.eclipse.wst.common.modulecore.IEditModelHandler#dispose()
	 */
	public void dispose() {
		if (isStructuralModelSelfManaged)
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
	 * Creates a default WTP Module Structural Model file if necessary.
	 * </p>
	 */
	public void prepareProjectModulesIfNecessary() {
		structuralModel.prepareProjectModulesIfNecessary();
	}

	/**
	 * <p>
	 * When loaded for write, the current ModuleCore may return the root object, which can be used
	 * to add or remove {@see WorkbenchModule}s. If a client needs to just read the existing
	 * {@see WorkbenchModule}s, use {@see #getWorkbenchModules()}.
	 * </p>
	 * 
	 * @return The root object of the underlying model
	 */
	public ProjectModules getModuleModelRoot() {
		return (ProjectModules) structuralModel.getPrimaryRootObject();
	}

	/**
	 * <p>
	 * Clients that wish to modify the individual {@see WorkbenchModule}instances may use this
	 * method. If clients need to add or remove {@see WorkbenchModule}instances, use
	 * {@see #getProjectModules()}to get the root object and then access the contained
	 * {@see WorkbenchModule}s through {@see ProjectModules#getWorkbenchModules()}.
	 * 
	 * @return The WorkbenchModules of the underlying model, if any.
	 */
	public WorkbenchModule[] getWorkbenchModules() {
		List wbModules = getModuleModelRoot().getWorkbenchModules();
		return (WorkbenchModule[]) wbModules.toArray(new WorkbenchModule[wbModules.size()]);
	}

	/**
	 * <p>
	 * Create a {@see WorkbenchModule}with the given deployed name. The returned module will be
	 * contained by the root object of the current ModuleCore (so no need to re-add it to the Module
	 * Module root object). The current ModuleCore must not be read-only to invoke this method.
	 * </p>
	 * 
	 * @param aDeployName
	 *            A non-null String that will be assigned as the deployed-name
	 * @return A {@see WorkbenchModule}associated with the current ModuleCore with the supplied
	 *         deployed name
	 * @throws IllegalStateException
	 *             If the current ModuleCore was created as read-only
	 */
	public WorkbenchModule createWorkbenchModule(String aDeployName) {
		if (isReadOnly)
			throwAttemptedReadOnlyModification();
		WorkbenchModule module = MODULE_FACTORY.createWorkbenchModule();
		module.setDeployedName(aDeployName);
		getModuleModelRoot().getWorkbenchModules().add(module);
		return module;
	}

	/**
	 * <p>
	 * Create a {@see WorkbenchModuleResource}with the sourcePath of aResource. The current
	 * ModuleCore must not be read-only to invoke this method.
	 * </p>
	 * 
	 * @param aModule
	 *            A non-null {@see WorkbenchModule}to contain the created
	 *            {@see WorkbenchModuleResource}
	 * @param aResource
	 *            A non-null IResource that will be used to set the sourcePath
	 * @return A {@see WorkbenchModuleResource}associated with the current ModuleCore with its
	 *         sourcePath equivalent to aResource
	 * @throws IllegalStateException
	 *             If the current ModuleCore was created as read-only
	 */
	public WorkbenchModuleResource createWorkbenchModuleResource(IResource aResource) {
		if (isReadOnly)
			throwAttemptedReadOnlyModification();

		WorkbenchModuleResource moduleResource = MODULE_FACTORY.createWorkbenchModuleResource();
		String sourcePath = IPath.SEPARATOR + aResource.getName() + IPath.SEPARATOR + aResource.getProjectRelativePath().toString();
		moduleResource.setSourcePath(URI.createURI(sourcePath));
		return moduleResource;
	}

	/**
	 * <p>
	 * Create a {@see ModuleType}with the sourcePath of aResource. The returned resource will be
	 * associated with the current ModuleCore. The current ModuleCore must not be read-only to
	 * invoke this method.
	 * </p>
	 * 
	 * @param aResource
	 *            A non-null IResource that will be used to set the sourcePath
	 * @return A {@see WorkbenchModuleResource}associated with the current ModuleCore with its
	 *         sourcePath equivalent to aResource
	 * @throws IllegalStateException
	 *             If the current ModuleCore was created as read-only
	 */
	public ModuleType createModuleType(String aModuleTypeId) {
		if (isReadOnly)
			throwAttemptedReadOnlyModification();

		ModuleType moduleType = MODULE_FACTORY.createModuleType();
		moduleType.setModuleTypeId(aModuleTypeId);
		return moduleType;
	}

	/**
	 * <p>
	 * Search the given module (indicated by aModuleURI) for the {@see WorkbenchModuleResource}s
	 * identified by the module-relative path (indicated by aDeployedResourcePath).
	 * </p>
	 * 
	 * @param aModuleURI
	 *            A valid, fully-qualified module URI
	 * @param aDeployedResourcePath
	 *            A module-relative path to a deployed file
	 * @return An array of WorkbenchModuleResources that contain the URI specified by the
	 *         module-relative aDeployedResourcePath
	 * @throws UnresolveableURIException
	 *             If the supplied module URI is invalid or unresolveable.
	 */
	public WorkbenchModuleResource[] findWorkbenchModuleResourceByDeployPath(URI aModuleURI, URI aDeployedResourcePath) throws UnresolveableURIException {
		WorkbenchModule module = findWorkbenchModuleByDeployName(ModuleURIUtil.getDeployedName(aModuleURI));
		return module.findWorkbenchModuleResourceByDeployPath(aDeployedResourcePath);
	}

	/**
	 * <p>
	 * Search the the module (indicated by the root of aModuleResourcePath) for the
	 * {@see WorkbenchModuleResource}s identified by the module-qualified path (indicated by
	 * aDeployedResourcePath).
	 * </p>
	 * 
	 * @param aModuleResourcePath
	 *            A valid fully-qualified URI of a deployed resource within a specific
	 *            WorkbenchModule
	 * @return An array of WorkbenchModuleResources that contain the URI specified by
	 *         aModuleResourcePath
	 * @throws UnresolveableURIException
	 *             If the supplied module URI is invalid or unresolveable.
	 */
	public WorkbenchModuleResource[] findWorkbenchModuleResourceByDeployPath(URI aModuleResourcePath) throws UnresolveableURIException {
		ModuleURIUtil.ensureValidFullyQualifiedModuleURI(aModuleResourcePath);
		URI moduleURI = aModuleResourcePath.trimSegments(aModuleResourcePath.segmentCount() - 3);
		URI deployedPath = ModuleURIUtil.trimToDeployPathSegment(aModuleResourcePath);
		WorkbenchModule module = findWorkbenchModuleByDeployName(ModuleURIUtil.getDeployedName(moduleURI));
		return module.findWorkbenchModuleResourceByDeployPath(deployedPath);
	}

	/**
	 * <p>
	 * Locates the {@see WorkbenchModuleResource}s that contain the supplied resource in their
	 * source path. There are no representations about the containment of the
	 * {@see WorkbenchModuleResource}s which are returned. The only guarantee is that the returned
	 * elements are contained within the same project.
	 * </p>
	 * <p>
	 * The sourcePath of each {@see WorkbenchModuleResource}will be mapped to either an IFile or an
	 * IFolder. As a result, if the {@see WorkbenchModuleResource}is a container mapping, the path
	 * of the supplied resource may not be identical the sourcePath of the
	 * {@see WorkbenchModuleResource}.
	 * </p>
	 * 
	 * @param aWorkspaceRelativePath
	 *            A valid fully-qualified workspace-relative path of a given resource
	 * @return An array of WorkbenchModuleResources which have sourcePaths that contain the given
	 *         resource
	 * @throws UnresolveableURIException
	 *             If the supplied module URI is invalid or unresolveable.
	 */
	public WorkbenchModuleResource[] findWorkbenchModuleResourcesBySourcePath(URI aWorkspaceRelativePath) throws UnresolveableURIException {
		ProjectModules projectModules = getModuleModelRoot();
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
	 * Returns the {@see WorkbenchModule}contained by the current ModuleCore with the deploy name
	 * aModuleName.
	 * </p>
	 * <p>
	 * <b>The following method may return null. </b>
	 * </p>
	 * 
	 * @param aModuleName
	 * @return The {@see WorkbenchModule}contained by the current ModuleCore with the deploy name
	 *         aModuleName
	 * @see WorkbenchModule#getDeployedName()
	 */
	public WorkbenchModule findWorkbenchModuleByDeployName(String aModuleName) {
		return getModuleModelRoot().findWorkbenchModule(aModuleName);
	}

	/**
	 * <p>
	 * Locate and return the {@see WorkbenchModule}referenced by the fully-qualified aModuleURI.
	 * The method will work correctly even if the requested {@see WorkbenchModule}is contained by
	 * another project.
	 * </p>
	 * 
	 * @param aModuleURI
	 *            A valid, fully-qualified module URI
	 * @return The {@see WorkbenchModule}referenced by aModuleURI
	 * @throws UnresolveableURIException
	 *             If the supplied module URI is invalid or unresolveable.
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
	 * Searches the available {@see WorkbenchModule}s as available through
	 * {@see #getWorkbenchModules()}for {@see WorkbenchModule}s that have a
	 * {@see WorkbenchModule#getModuleType()}with a a module type Id as specified by aModuleTypeId.
	 * </p>
	 * 
	 * @param aModuleTypeId
	 *            A non-null module type id ({@see ModuleType#getModuleTypeId()})
	 * @return A non-null array of the {@see WorkbenchModule}s that match the given module type id
	 */
	public WorkbenchModule[] findWorkbenchModuleByType(String aModuleTypeId) {
		WorkbenchModule[] availableModules = getWorkbenchModules();
		ModuleType moduleType;
		List results = new ArrayList();
		for (int i = 0; i < availableModules.length; i++) {
			moduleType = availableModules[i].getModuleType();
			if (moduleType != null && aModuleTypeId.equals(moduleType.getModuleTypeId()))
				results.add(availableModules[i]);
		}
		if (results.size() == 0)
			return NO_MODULES;
		return (WorkbenchModule[]) results.toArray(new WorkbenchModule[results.size()]);
	}

	/**
	 * <p>
	 * Returns true if the {@see DependentModule}references a {@see WorkbenchModule}(
	 * {@see DependentModule#getHandle()}) which is contained by the project that the current
	 * ModuleCore is managing. The following method will determine if the dependency can be
	 * satisfied by the current project.
	 * </p>
	 * 
	 * @param aDependentModule
	 * @return True if the {@see DependentModule}references a {@see WorkbenchModule}managed
	 *         directly by the current ModuleCore
	 */
	public boolean isLocalDependency(DependentModule aDependentModule) {
		String localProjectName = structuralModel.getProject().getName();
		String dependentProjectName = aDependentModule.getHandle().segment(ModuleCore.ModuleURI.PROJECT_NAME_INDX);
		return localProjectName.equals(dependentProjectName);
	}

	/**
	 * @param aModuleURI
	 *            A valid, fully-qualified module URI
	 * @return The ModuleCore facade for the supplied URI
	 * @throws UnresolveableURIException
	 *             If the supplied module URI is invalid or unresolveable.
	 */
	private ModuleCore getDependentModuleCore(URI aModuleURI) throws UnresolveableURIException {
		ModuleCore dependentCore = (ModuleCore) dependentCores.get(aModuleURI);
		if (dependentCore != null)
			return dependentCore;
		synchronized (dependentCores) {
			dependentCore = (ModuleCore) dependentCores.get(aModuleURI);
			if (dependentCore == null) {
				dependentCore = getModuleCoreForRead(getContainingProject(aModuleURI));
				dependentCores.put(aModuleURI, dependentCore);
			}
		}
		return dependentCore;
	}

	private void throwAttemptedReadOnlyModification() {
		throw new IllegalStateException("Attempt to modify a ModuleCore instance facade that was loaded as read-only.");
	}


}