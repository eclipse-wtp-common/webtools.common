/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
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
import org.eclipse.wst.common.modulecore.internal.impl.ModuleURIUtil;
import org.eclipse.wst.common.modulecore.internal.impl.PlatformURLModuleConnection;
import org.eclipse.wst.common.modulecore.internal.resources.VirtualContainer;
import org.eclipse.wst.common.modulecore.internal.resources.VirtualFile;
import org.eclipse.wst.common.modulecore.internal.resources.VirtualFolder;
import org.eclipse.wst.common.modulecore.internal.util.EclipseResourceAdapter;
import org.eclipse.wst.common.modulecore.resources.IVirtualContainer;
import org.eclipse.wst.common.modulecore.resources.IVirtualFile;
import org.eclipse.wst.common.modulecore.resources.IVirtualFolder;
import org.eclipse.wst.common.modulecore.resources.IVirtualReference;

/**
 * <p>
 * Provides a Facade pattern for accessing the Web Tools Platform EMF Module Model. ModuleCore can
 * be used as a static utility or an instance adapter.
 * </p>
 * <p>
 * ModuleCore hides the management of accessing EditModels (
 * {@see org.eclipse.wst.common.modulecore.ModuleStructuralModel}) correctly. Each project has
 * exactly one ({@see org.eclipse.wst.common.modulecore.ModuleStructuralModel}) for read and
 * exactly one for write. Each of these is shared among all clients and reference counted as
 * necessary. Clients should use ModuleCore when working with the WTP Modules Strcutrual Model.
 * </p>
 * 
 * <p>
 * Each ModuleCore edit facade is designed to manage the EditModel lifecycle for clients. However,
 * while each ModuleCore is designed to be passed around as needed, clients must enforce the
 * ModuleCore lifecycle. The most common method of acquiring a ModuleCore edit facade is to use
 * {@see #getModuleCoreForRead(IProject)}&nbsp;or {@see #getModuleCoreForWrite(IProject)}.
 * </p>
 * <p>
 * When clients have concluded their use of their ModuleCore instance adapter , <b>clients must call
 * {@see #dispose()}</b>.
 * </p>
 * <p>
 * For more information about the underlying EditModel, see <a
 * href="ModuleCoreNature.html#module-structural-model">the discussion of the ModuleStructuralModel
 * </a>.
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 * 
 * @see org.eclipse.wst.common.modulecore.ModuleCoreNature
 * @see org.eclipse.wst.common.modulecore.ModuleStructuralModel
 */
public class ModuleCore implements IEditModelHandler {

	public static final Class ADAPTER_TYPE = ModuleCore.class;

	static final String DEPLOYABLES_ROOT = ".deployables/"; //$NON-NLS-1$
	static String MODULE_META_FILE_NAME = ".wtpmodules"; //$NON-NLS-1$

	private final static ModuleCoreFactory MODULE_FACTORY = ModuleCoreFactory.eINSTANCE;
	private static final WorkbenchComponent[] NO_MODULES = new WorkbenchComponent[0];
	private static final ComponentResource[] NO_RESOURCES = new ComponentResource[0];
	private static final IFolder[] NO_FOLDERS = new IFolder[0];


	private final ModuleStructuralModel structuralModel;
	private final Map dependentCores = new HashMap();
	private boolean isStructuralModelSelfManaged;
	private boolean isReadOnly;

	/**
	 * 
	 * <p>
	 * Each ModuleCore edit facade is tied to a specific project. A project may have multiple
	 * ModuleCore edit facades live at any given time.
	 * </p>
	 * <p>
	 * Use to acquire a ModuleCore facade for a specific project that will not be used for editing.
	 * Invocations of any save*() API on an instance returned from This method will throw
	 * exceptions.
	 * </p>
	 * 
	 * @param aProject
	 *            The IProject that contains the WTP Modules model to load
	 * @return A ModuleCore edit facade to access the WTP Modules Model
	 */
	public static ModuleCore getModuleCoreForRead(IProject aProject) {
		ModuleCoreNature nature = ModuleCoreNature.getModuleCoreNature(aProject);
		return nature != null ? new ModuleCore(nature, true) : null;
	}

	/**
	 * 
	 * <p>
	 * Each ModuleCore edit facade is tied to a specific project. A project may have multiple
	 * ModuleCore edit facades live at any given time.
	 * </p>
	 * <p>
	 * Use to acquire a ModuleCore facade for a specific project that may be used to modify the
	 * model.
	 * </p>
	 * 
	 * @param aProject
	 *            The IProject that contains the WTP Modules model to load
	 * @return A ModuleCore edit facade to access the WTP Modules Model
	 */
	public static ModuleCore getModuleCoreForWrite(IProject aProject) {
		ModuleCoreNature nature = ModuleCoreNature.getModuleCoreNature(aProject);
		return nature != null ? new ModuleCore(nature, false) : null;
	}

	/**
	 * <p>
	 * A convenience API to fetch the {@see ModuleCoreNature}&nbsp;for a particular module URI. The
	 * module URI must be of the valid form, or an exception will be thrown. The module URI must be
	 * contained by a project that has a {@see ModuleCoreNature}&nbsp;or null will be returned.
	 * </p>
	 * <p>
	 * <b>This method may return null. </b>
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
	 * For {@see WorkbenchComponent}s that are contained within a project, the containing project
	 * can be determined with the {@see WorkbenchComponent}'s fully-qualified module URI.
	 * </p>
	 * <p>
	 * The following method will return the the corresponding project for the supplied module URI,
	 * if it can be determined.
	 * </p>
	 * <p>
	 * The method will not return an inaccessible project.
	 * </p>
	 * <p>
	 * <b>This method may return null. </b>
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
		String projectName = aModuleURI.segment(ModuleURIUtil.ModuleURI.PROJECT_NAME_INDX);
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
	 * {@see ComponentResource}. The {@see ComponentResource#getSourcePath()} &nbsp;must return a
	 * valid resource path for This method to return a valid value. The returned value may be either
	 * an {@see org.eclipse.core.resources.IFile}&nbsp;or {@see org.eclipse.core.resources.IFolder}.
	 * A client may use the return value of {@see IResource#getType()}&nbsp;to determine what type
	 * of resource was returned. (@see IResource#FILE} or {@see IResource#FOLDER}).
	 * </p>
	 * <p>
	 * <b>This method may return null. </b>
	 * </p>
	 * 
	 * @param aModuleResource
	 *            A ComponentResource with a valid sourcePath
	 * @return The corresponding Eclipse IResource, if available.
	 */
	public static IResource getEclipseResource(ComponentResource aModuleResource) {
		EclipseResourceAdapter eclipseResourceAdapter = (EclipseResourceAdapter) EcoreUtil.getAdapter(aModuleResource.eAdapters(), EclipseResourceAdapter.ADAPTER_TYPE);
		if (eclipseResourceAdapter != null)
			return eclipseResourceAdapter.getEclipseResource();
		eclipseResourceAdapter = new EclipseResourceAdapter();
		aModuleResource.eAdapters().add(eclipseResourceAdapter);
		return eclipseResourceAdapter.getEclipseResource();
	}

	/**
	 * <p>
	 * Returns a URI for the supplied {@see WorkbenchComponent}. The URI will be relative to
	 * project root of the flexible project that contains the {@see WorkbenchComponent}.
	 * </p>
	 * <p>
	 * <b>This method may return null. </b>
	 * </p>
	 * 
	 * @param aWorkbenchModule
	 *            A valid WorkbenchComponent
	 * @return A project-relative URI of the output folder for aWorkbenchModoule.
	 */
	public static IFolder getOutputContainerRoot(WorkbenchComponent aWorkbenchModule) {
		IProject project = null;
		try {
			project = getContainingProject(aWorkbenchModule.getHandle());
		} catch (UnresolveableURIException e) {
		}
		if (project != null)
			return project.getFolder(new Path(DEPLOYABLES_ROOT + aWorkbenchModule.getName()));
		return null;
	}

	/**
	 * <p>
	 * Returns a collection of the output containers for the supplied project. The collection may be
	 * a single root output container or an array of output containers without a common root. For
	 * clients that are looking for an output container for a specific {@see WorkbenchComponent},
	 * see {@see #getOutputContainerRoot(WorkbenchComponent)}.
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

	/**
	 * <p>
	 * Parses the supplied URI for the deployed name name of the {@see WorkbenchComponent}&nbsp;referenced
	 * by the URI.
	 * </p>
	 * 
	 * @param aFullyQualifiedModuleURI
	 *            A valid, fully-qualified module URI
	 * @return The deployed name of the referenced {@see WorkbenchComponent}
	 * @throws UnresolveableURIException
	 *             If the supplied URI is invalid or unresolveable
	 */
	public static String getDeployedName(URI aFullyQualifiedModuleURI) throws UnresolveableURIException {
		return ModuleURIUtil.getDeployedName(aFullyQualifiedModuleURI);
	}

	public static IVirtualContainer createContainer(IProject aProject, String aComponentName) {
		return new VirtualContainer(aProject, aComponentName, new Path("/")); //$NON-NLS-1$
	}

	public static IVirtualFolder createFolder(IProject aProject, String aComponentName, IPath aRuntimePath) {
		return new VirtualFolder(aProject, aComponentName, aRuntimePath);	
	}

	public static IVirtualFile createFile(IProject aProject, String aComponentName, IPath aRuntimePath) {
		return new VirtualFile(aProject, aComponentName, aRuntimePath);	
	}
	
	public static IVirtualReference createReference(IVirtualContainer aContainer, IVirtualContainer aReferencedContainer) {
		return null;
	}
		
	public static ComponentType getComponentType(IVirtualContainer aComponent) {
		ModuleCore moduleCore = null;
		ComponentType componentType = null;
		try {
			moduleCore = ModuleCore.getModuleCoreForRead(aComponent.getProject());
			WorkbenchComponent wbComponent = moduleCore.findWorkbenchModuleByDeployName(aComponent.getComponentName());
			componentType = wbComponent.getComponentType();
		} finally {
			if (moduleCore != null)
				moduleCore.dispose();
		}
		return componentType;
	}

	public static void setComponentType(IVirtualContainer component, ComponentType aComponentType) {
		ModuleCore moduleCore = null;
		try {
			moduleCore = ModuleCore.getModuleCoreForWrite(component.getProject());
			WorkbenchComponent wbComponent = moduleCore.findWorkbenchModuleByDeployName(component.getComponentName());
			wbComponent.setComponentType(aComponentType);
		} finally {
			if (moduleCore != null) {
				moduleCore.saveIfNecessary(null);
				moduleCore.dispose();
			}
		}
	}

	public static URI createComponentURI(IProject aContainingProject, String aComponentName) {
		return URI.createURI(PlatformURLModuleConnection.MODULE_PROTOCOL + IPath.SEPARATOR + PlatformURLModuleConnection.RESOURCE_MODULE + aContainingProject.getName() + IPath.SEPARATOR + aComponentName);
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
	 * {@inheritDoc}
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
	 * {@inheritDoc}
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
	 * ModuleCore edit facade was created as read-only.
	 * </p>
	 * {@inheritDoc}
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
	 * Creates a default WTP Module Structural Metamodel file if necessary.
	 * </p>
	 */
	public void prepareProjectModulesIfNecessary() {
		structuralModel.prepareProjectModulesIfNecessary();
	}

	/**
	 * <p>
	 * When loaded for write, the current ModuleCore may return the root object, which can be used
	 * to add or remove {@see WorkbenchComponent}s. If a client needs to just read the existing
	 * {@see WorkbenchComponent}s, use {@see #getWorkbenchModules()}.
	 * </p>
	 * 
	 * @return The root object of the underlying model
	 */
	public ProjectComponents getModuleModelRoot() {
		return (ProjectComponents) structuralModel.getPrimaryRootObject();
	}

	/**
	 * <p>
	 * Return the an array of ComponentResource which basically represent the source containers of a
	 * WorkbenchResource.
	 * <p>
	 * 
	 * @param component
	 * @return
	 */
	public ComponentResource[] getSourceContainers(WorkbenchComponent component) {
		// TODO Api in progress: Need to return the Java Source containers of the project
		// TODO MDE: I don't know if I agree with the placement of this method.
		return null;
	}

	/**
	 * <p>
	 * Clients that wish to modify the individual {@see WorkbenchComponent}&nbsp;instances may use
	 * this method. If clients need to add or remove {@see WorkbenchComponent}&nbsp;instances, use
	 * {@see #getProjectModules()}&nbsp;to get the root object and then access the contained
	 * {@see WorkbenchComponent}s through {@see ProjectComponents#getWorkbenchModules()}.
	 * 
	 * @return The WorkbenchModules of the underlying model, if any.
	 */
	public WorkbenchComponent[] getWorkbenchModules() {
		List wbModules = getModuleModelRoot().getComponents();
		return (WorkbenchComponent[]) wbModules.toArray(new WorkbenchComponent[wbModules.size()]);
	}

	/**
	 * <p>
	 * Create a {@see WorkbenchComponent}&nbsp;with the given deployed name. The returned module
	 * will be contained by the root object of the current ModuleCore (so no need to re-add it to
	 * the Module Module root object). The current ModuleCore must not be read-only to invoke This
	 * method.
	 * </p>
	 * 
	 * @param aDeployName
	 *            A non-null String that will be assigned as the deployed-name
	 * @return A {@see WorkbenchComponent}associated with the current ModuleCore with the supplied
	 *         deployed name
	 * @throws IllegalStateException
	 *             If the current ModuleCore was created as read-only
	 */
	public WorkbenchComponent createWorkbenchModule(String aDeployName) {
		if (isReadOnly)
			throwAttemptedReadOnlyModification();
		WorkbenchComponent module = MODULE_FACTORY.createWorkbenchComponent();
		module.setName(aDeployName);
		getModuleModelRoot().getComponents().add(module);
		return module;
	}

	/**
	 * <p>
	 * Create a {@see ComponentResource}&nbsp;with the sourcePath of aResource. The current
	 * ModuleCore must not be read-only to invoke This method.
	 * </p>
	 * 
	 * @param aModule
	 *            A non-null {@see WorkbenchComponent}to contain the created
	 *            {@see ComponentResource}
	 * @param aResource
	 *            A non-null IResource that will be used to set the sourcePath
	 * @return A {@see ComponentResource}associated with the current ModuleCore with its sourcePath
	 *         equivalent to aResource
	 * @throws IllegalStateException
	 *             If the current ModuleCore was created as read-only
	 */
	public ComponentResource createWorkbenchModuleResource(IResource aResource) {
		if (isReadOnly)
			throwAttemptedReadOnlyModification();

		ComponentResource moduleResource = MODULE_FACTORY.createComponentResource();
		String sourcePath = IPath.SEPARATOR + aResource.getProject().getName() + IPath.SEPARATOR + aResource.getProjectRelativePath().toString();
		moduleResource.setSourcePath(URI.createURI(sourcePath));
		return moduleResource;
	}

	/**
	 * <p>
	 * Create a {@see ComponentType}&nbsp;with the sourcePath of aResource. The returned resource
	 * will be associated with the current ModuleCore. The current ModuleCore must not be read-only
	 * to invoke This method.
	 * </p>
	 * 
	 * @param aResource
	 *            A non-null IResource that will be used to set the sourcePath
	 * @return A {@see ComponentResource}associated with the current ModuleCore with its sourcePath
	 *         equivalent to aResource
	 * @throws IllegalStateException
	 *             If the current ModuleCore was created as read-only
	 */
	public ComponentType createModuleType(String aModuleTypeId) {
		if (isReadOnly)
			throwAttemptedReadOnlyModification();

		ComponentType moduleType = MODULE_FACTORY.createComponentType();
		moduleType.setModuleTypeId(aModuleTypeId);
		return moduleType;
	}

	/**
	 * <p>
	 * Search the given module (indicated by aModuleURI) for the {@see ComponentResource}s
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
	public ComponentResource[] findWorkbenchModuleResourceByDeployPath(URI aModuleURI, URI aDeployedResourcePath) throws UnresolveableURIException {
		WorkbenchComponent module = findWorkbenchModuleByDeployName(ModuleURIUtil.getDeployedName(aModuleURI));
		return module.findWorkbenchModuleResourceByDeployPath(aDeployedResourcePath);
	}

	/**
	 * <p>
	 * Search the the module (indicated by the root of aModuleResourcePath) for the
	 * {@see ComponentResource}s identified by the module-qualified path (indicated by
	 * aDeployedResourcePath).
	 * </p>
	 * 
	 * @param aModuleResourcePath
	 *            A valid fully-qualified URI of a deployed resource within a specific
	 *            WorkbenchComponent
	 * @return An array of WorkbenchModuleResources that contain the URI specified by
	 *         aModuleResourcePath
	 * @throws UnresolveableURIException
	 *             If the supplied module URI is invalid or unresolveable.
	 */
	public ComponentResource[] findWorkbenchModuleResourceByDeployPath(URI aModuleResourcePath) throws UnresolveableURIException {
		ModuleURIUtil.ensureValidFullyQualifiedModuleURI(aModuleResourcePath);
		URI moduleURI = aModuleResourcePath.trimSegments(aModuleResourcePath.segmentCount() - 3);
		URI deployedPath = ModuleURIUtil.trimToDeployPathSegment(aModuleResourcePath);
		WorkbenchComponent module = findWorkbenchModuleByDeployName(ModuleURIUtil.getDeployedName(moduleURI));
		return module.findWorkbenchModuleResourceByDeployPath(deployedPath);
	}

	/**
	 * <p>
	 * Locates the {@see ComponentResource}s that contain the supplied resource in their source
	 * path. There are no representations about the containment of the {@see ComponentResource}s
	 * which are returned. The only guarantee is that the returned elements are contained within the
	 * same project.
	 * </p>
	 * <p>
	 * The sourcePath of each {@see ComponentResource}&nbsp;will be mapped to either an IFile or an
	 * IFolder. As a result, if the {@see ComponentResource}&nbsp;is a container mapping, the path
	 * of the supplied resource may not be identical the sourcePath of the {@see ComponentResource}.
	 * </p>
	 * 
	 * @param aWorkspaceRelativePath
	 *            A valid fully-qualified workspace-relative path of a given resource
	 * @return An array of WorkbenchModuleResources which have sourcePaths that contain the given
	 *         resource
	 * @throws UnresolveableURIException
	 *             If the supplied module URI is invalid or unresolveable.
	 */
	public ComponentResource[] findWorkbenchModuleResourcesBySourcePath(URI aWorkspaceRelativePath) throws UnresolveableURIException {
		ProjectComponents projectModules = getModuleModelRoot();
		EList modules = projectModules.getComponents();

		WorkbenchComponent module = null;
		ComponentResource[] resources = null;
		List foundResources = new ArrayList();
		for (int i = 0; i < modules.size(); i++) {
			module = (WorkbenchComponent) modules.get(i);
			resources = module.findWorkbenchModuleResourceBySourcePath(aWorkspaceRelativePath);
			if (resources != null && resources.length != 0)
				foundResources.addAll(Arrays.asList(resources));
		}
		if (foundResources.size() > 0)
			return (ComponentResource[]) foundResources.toArray(new ComponentResource[foundResources.size()]);
		return NO_RESOURCES;
	}

	/**
	 * <p>
	 * Returns the {@see WorkbenchComponent}&nbsp;contained by the current ModuleCore with the
	 * deploy name aModuleName.
	 * </p>
	 * <p>
	 * <b>This method may return null. </b>
	 * </p>
	 * 
	 * @param aModuleName
	 * @return The {@see WorkbenchComponent}contained by the current ModuleCore with the deploy
	 *         name aModuleName
	 * @see WorkbenchComponent#getDeployedName()
	 */
	public WorkbenchComponent findWorkbenchModuleByDeployName(String aModuleName) {
		return getModuleModelRoot().findWorkbenchModule(aModuleName);
	}

	/**
	 * <p>
	 * Locate and return the {@see WorkbenchComponent}&nbsp;referenced by the fully-qualified
	 * aModuleURI. The method will work correctly even if the requested {@see WorkbenchComponent}
	 * &nbsp;is contained by another project.
	 * </p>
	 * 
	 * @param aModuleURI
	 *            A valid, fully-qualified module URI
	 * @return The {@see WorkbenchComponent}referenced by aModuleURI
	 * @throws UnresolveableURIException
	 *             If the supplied module URI is invalid or unresolveable.
	 * @see WorkbenchComponent#getHandle()
	 */
	public WorkbenchComponent findWorkbenchModuleByModuleURI(URI aModuleURI) throws UnresolveableURIException {
		ModuleURIUtil.ensureValidFullyQualifiedModuleURI(aModuleURI);
		String projectName = aModuleURI.segment(ModuleURIUtil.ModuleURI.PROJECT_NAME_INDX);
		String moduleName = aModuleURI.segment(ModuleURIUtil.ModuleURI.MODULE_NAME_INDX);
		/* Accessing a local module */
		if (structuralModel.getProject().getName().equals(projectName)) {
			return findWorkbenchModuleByDeployName(moduleName);
		}
		return getDependentModuleCore(aModuleURI).findWorkbenchModuleByDeployName(moduleName);
	}

	/**
	 * <p>
	 * Searches the available {@see WorkbenchComponent}s as available through
	 * {@see #getWorkbenchModules()}&nbsp;for {@see WorkbenchComponent}s that have a
	 * {@see WorkbenchComponent#getModuleType()}with a a module type Id as specified by
	 * aModuleTypeId.
	 * </p>
	 * 
	 * @param aModuleTypeId
	 *            A non-null module type id ({@see ComponentType#getModuleTypeId()})
	 * @return A non-null array of the {@see WorkbenchComponent}s that match the given module type
	 *         id
	 */
	public WorkbenchComponent[] findWorkbenchModuleByType(String aModuleTypeId) {
		WorkbenchComponent[] availableModules = getWorkbenchModules();
		ComponentType moduleType;
		List results = new ArrayList();
		for (int i = 0; i < availableModules.length; i++) {
			moduleType = availableModules[i].getComponentType();
			if (moduleType != null && aModuleTypeId.equals(moduleType.getModuleTypeId()))
				results.add(availableModules[i]);
		}
		if (results.size() == 0)
			return NO_MODULES;
		return (WorkbenchComponent[]) results.toArray(new WorkbenchComponent[results.size()]);
	}

	/**
	 * <p>
	 * Returns true if the {@see ReferencedComponent}&nbsp;references a {@see WorkbenchComponent}(
	 * {@see ReferencedComponent#getHandle()}) which is contained by the project that the current
	 * ModuleCore is managing. The following method will determine if the dependency can be
	 * satisfied by the current project.
	 * </p>
	 * 
	 * @param aDependentModule
	 * @return True if the {@see ReferencedComponent}references a {@see WorkbenchComponent}managed
	 *         directly by the current ModuleCore
	 */
	public boolean isLocalDependency(ReferencedComponent aDependentModule) {
		String localProjectName = structuralModel.getProject().getName();
		String dependentProjectName = aDependentModule.getHandle().segment(ModuleURIUtil.ModuleURI.PROJECT_NAME_INDX);
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
				IProject container = getContainingProject(aModuleURI);
				if (container != null) {
					dependentCore = getModuleCoreForRead(container);
					dependentCores.put(aModuleURI, dependentCore);
				} else
					throw new UnresolveableURIException(aModuleURI);
			}
		}
		return dependentCore;
	}

	private void throwAttemptedReadOnlyModification() {
		throw new IllegalStateException("Attempt to modify a ModuleCore edit facade that was loaded as read-only.");
	}

	/**
	 * temporary method to return first module in the project
	 * 
	 * @return first module in the project
	 * @deprecated
	 */
	public WorkbenchComponent getFirstModule() {
		if (getWorkbenchModules().length > 0)
			return getWorkbenchModules()[0];
		return null;
	}

	/**
	 * temporary method to return artifact edit for first module in project USERS MUST DISPOSE THE
	 * ARTIFACT EDIT
	 * 
	 * @param project
	 * @return the artifact edit for the first module
	 * @deprecated
	 */
	public static ArtifactEdit getFirstArtifactEditForRead(IProject project) {
		ModuleCore moduleCore = null;
		ArtifactEdit artEdit = null;
		WorkbenchComponent module = null;
		try {
			moduleCore = ModuleCore.getModuleCoreForRead(project);
			module = moduleCore.getFirstModule();
			artEdit = ArtifactEdit.getArtifactEditForRead(module);
		} finally {
			if (moduleCore != null)
				moduleCore.dispose();
		}
		return artEdit;
	}
}