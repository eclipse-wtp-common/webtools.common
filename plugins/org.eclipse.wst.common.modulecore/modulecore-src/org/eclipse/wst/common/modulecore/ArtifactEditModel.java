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

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext;
import org.eclipse.wst.common.internal.emfworkbench.WorkbenchResourceHelper;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModel;
import org.eclipse.wst.common.modulecore.internal.impl.PlatformURLModuleConnection;

/**
 * 
 * <p>
 * Provides resource life cycle management between an editmodel pattern and a WTP flexible module
 * pattern.
 * </p>
 * <p>
 * ArtifactEditModel provides a framework for managing a set of EMF resources within a unit of work.
 * This provide basic services such as loads, saves, and synchronization for the managed resources.
 * ArtifactEditModels are reference counted, therefore a single instance can be shared by more than
 * one operation or editor.
 * </p>
 * <p>
 * AritfactEditModel utilizes EMFWorkbenchContext (@see EMFWorkbenchContext) to maintain the life
 * cycle of a resource (@see Resource) in a given resource set (@see ResourceSet) per workbench
 * module/project. When an interested resource, resources within a flexible module structure, is
 * modified it notifies ModuleCore(@see ModuleCore) to update the corresponding
 * WorkbenchModules/WorkbenchModuleResource ((@see WorkbenchModule)(@see WorkbenchModuleResource)).
 * Retrieval and creation of resources are delegated to the WorkbenchResourceHelper(@see
 * WorkbenchResourceHelper).
 * </p>
 * 
 * @see EMFWorkbenchContext
 * @see Resource
 * @see ResourceSet
 * @see ModuleCore
 * @see WorkbenchResourceHelper
 * @see WorkbenchModule
 * @see WorkbenchModuleResource
 */

public class ArtifactEditModel extends EditModel implements IAdaptable {

	private final URI moduleURI;
	private final IPath modulePath;

	/**
	 * <p>
	 * Creates a read only ArtifactEditModel instance. Containted resources will have read only
	 * access.
	 * </p>
	 * 
	 * @param anEditModelId
	 *            a unique identifier for the edit model
	 * @param aContext
	 *            resource/resoruce set life cycle manager
	 * @param toMakeReadOnly
	 *            type of file access
	 * @param aModuleURI
	 *            a URI that contains resource location and module information
	 */

	public ArtifactEditModel(String anEditModelId, EMFWorkbenchContext aContext, boolean toMakeReadOnly, URI aModuleURI) {
		this(anEditModelId, aContext, toMakeReadOnly, true, aModuleURI);
	}

	/**
	 * <p>
	 * Creates a read/write ArtifactEditModel instance.
	 * </p>
	 * 
	 * @param anEditModelId
	 *            a unique identifier for the edit model
	 * @param aContext
	 *            resource/resoruce set life cycle manager
	 * @param toMakeReadOnly
	 *            type of file access
	 * @param toAccessUnknownResourcesAsReadOnly
	 *            if resource is requested and not loaded, file access will be set to read only
	 * 
	 * @param aModuleURI
	 *            a URI that contains resource location and module information
	 */

	public ArtifactEditModel(String anEditModelId, EMFWorkbenchContext aContext, boolean toMakeReadOnly, boolean toAccessUnknownResourcesAsReadOnly, URI aModuleURI) {
		super(anEditModelId, aContext, toMakeReadOnly, toAccessUnknownResourcesAsReadOnly);
		moduleURI = aModuleURI;
		modulePath = new Path(moduleURI.path());
		processLoadedResources(moduleURI);
	}

	/**
	 * <p>
	 * Accesses resources within the underlying resource set. Takes a standard URI attaches module
	 * protocol and information. This data can be used to determine if the resource is part of a
	 * flexible module structure. If the resource does not exist in the resource set it will try and
	 * load the resource.
	 * </p>
	 * 
	 * @param aUri -
	 *            location of resource
	 * 
	 * @return Resource (@see Resource)
	 */
	public Resource getResource(URI aUri) {
		IPath requestPath = modulePath.append(new Path(aUri.path()));
		URI resourceURI = URI.createURI(PlatformURLModuleConnection.MODULE_PROTOCOL + requestPath.toString());
		return super.getResource(resourceURI);
	}

	/**
	 * <p>
	 * Uses the cached moduleURI to query ModuleCore (@see ModuleCore) for the module type this
	 * ArtifactEditModel represents. Module types can be found in IModuleConstants (@see
	 * IModuleConstants).
	 * </p>
	 * 
	 * <@return string reprentation of a module type i.e. "wst.web">
	 */


	public String getModuleType() {
		String type = null;
		WorkbenchModule wbModule;
		ModuleCore moduleCore = null;
		try {
			moduleCore = ModuleCore.getModuleCoreForRead(ModuleCore.getContainingProject(moduleURI));
			wbModule = moduleCore.findWorkbenchModuleByModuleURI(moduleURI);
			type = wbModule.getModuleType().getModuleTypeId();
		} catch (UnresolveableURIException e) {
			e.printStackTrace();
		} finally {
			moduleCore.dispose();
		}
		return type;
	}

	public URI getModuleURI() {
		return moduleURI;
	}

	/**
	 * <p>
	 * Accesses resoureces within the underlying resource set. Takes a starndard URI attaches module
	 * information. This data can be used to determine if the resource is part of a flexible module
	 * structure.
	 * </p>
	 * 
	 * @param aUri -
	 *            location of resource
	 * 
	 * @return Resource (@see Resource)
	 */

	public Resource getOrCreateResource(URI aUri) {

		IPath requestPath = modulePath.append(new Path(aUri.path()));
		URI resourceURI = URI.createURI(requestPath.toString());
		return super.getOrCreateResource(resourceURI);
	}

	/**
	 * <p>
	 * Overridden to prevent super() implementation, processLoadedResources(aModuleURI) used
	 * instead. (@link processLoadedResources(URI aModuleURI)
	 * </p>
	 * 
	 * @param aUri -
	 *            location of resource
	 */



	protected void processLoadedResources() {
	}

	/**
	 * <p>
	 * Gathers resources the underlying resource set, determines if interested (@link
	 * processLoadedResources(URI aModuleURI))and request access (@link access(ReferencedResource
	 * aResource))to each resource incrementing the write/read count.
	 * </p>
	 * 
	 * @param aUri -
	 *            location of resource
	 */


	protected void processLoadedResources(URI aModuleURI) {
		List loadedResources = getResourceSet().getResources();
		if (!loadedResources.isEmpty()) {
			processResourcesIfInterrested(loadedResources);
		}
	}

	/**
	 * <p>
	 * Checks to make sure a flexible module structure exist for the resources in the resource set.
	 * This is achieved by querying ModuleCore(@see ModuleCore) for existing
	 * WorkbenchModuleResources (@see WorkbenchModuleResources). If the resource exist it process
	 * the resource (@link processResource(Resource aResource)). Processing the resource increments
	 * the read/write count.
	 * </p>*
	 * 
	 * @param theResources -
	 *            list of resources to verify flexible module status, and process
	 * @throws UnresolveableURIException
	 *             could not WorkbenchResource with the corresponding URI.
	 */



	protected boolean processResourcesIfInterrested(List theResources) {
		int size = theResources.size();
		Resource resourceToProcess;
		boolean processed = false;
		ModuleCore moduleCore = null;
		try {
			moduleCore = ModuleCore.getModuleCoreForRead(ModuleCore.getContainingProject(moduleURI));

			WorkbenchModuleResource[] relevantModuleResources = null;
			URI aResourceURI = null;
			for (int i = 0; i < size; i++) {
				try {
					resourceToProcess = (Resource) theResources.get(i);
					aResourceURI = resourceToProcess.getURI();
					relevantModuleResources = moduleCore.findWorkbenchModuleResourcesBySourcePath(aResourceURI);
					for (int resourcesIndex = 0; resourcesIndex < relevantModuleResources.length; resourcesIndex++) {
						if (moduleURI.equals(relevantModuleResources[resourcesIndex].getModule().getHandle())) {
							processResource(resourceToProcess);
							processed = true;
						}
					}

				} catch (UnresolveableURIException uurie) {
				}
			}
		} catch (UnresolveableURIException uurie) {
		} finally {
			if (moduleCore != null)
				moduleCore.dispose();
		}
		return processed;
	}

	/**
	 * 
	 * <p>
	 * Generic way to retrieve containing information, within the platform
	 * </p>
	 * 
	 * @return instance of adapterType for this adapter class from adapter factory stored in the
	 *         adapter manager (@see AdapterManager)
	 *  
	 */

	public Object getAdapter(Class adapterType) {
		return Platform.getAdapterManager().getAdapter(this, adapterType);
	}
}