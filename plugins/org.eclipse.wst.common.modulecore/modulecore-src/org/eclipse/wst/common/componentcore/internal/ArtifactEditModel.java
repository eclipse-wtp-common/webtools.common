/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.componentcore.internal;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.ModuleCoreNature;
import org.eclipse.wst.common.componentcore.UnresolveableURIException;
import org.eclipse.wst.common.componentcore.internal.impl.PlatformURLModuleConnection;
import org.eclipse.wst.common.componentcore.resources.ComponentHandle;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;
import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext;
import org.eclipse.wst.common.internal.emfworkbench.WorkbenchResourceHelper;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModel;

/**
 * 
 * Provides resource life cycle management between an EditModel and a WTP flexible module pattern.
 * <p>
 * ArtifactEditModel provides a framework for managing a set of EMF resources within a unit of work.
 * Management of these resources includes basic services such as loads, saves, and synchronization
 * for the managed resources. ArtifactEditModels are reference counted so that a single instance can
 * be shared by more than one operation or editor.
 * </p>
 * <p>
 * AritfactEditModel utilizes
 * {@see org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext}&nbsp; to manage the life
 * cycle of an EMF {@see org.eclipse.emf.ecore.resource.Resource}&nbsp; in a given EMF
 * {@see org.eclipse.emf.ecore.resource.ResourceSet}. There is one ArtifactEditModel per
 * {@see org.eclipse.wst.common.modulecore.WorkbenchComponent}&nbsp;in each project.
 * </p>
 * 
 * @see org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext
 * @see org.eclipse.emf.ecore.resource.Resource
 * @see org.eclipse.emf.ecore.resource.ResourceSet
 * @see org.eclipse.wst.common.componentcore.internal.StructureEdit
 * @see org.eclipse.wst.common.componentcore.internal.WorkbenchComponent
 * @see org.eclipse.wst.common.componentcore.internal.ComponentResource
 */

public class ArtifactEditModel extends EditModel implements IAdaptable {

	public static final Class ADAPTER_TYPE = ArtifactEditModel.class;
	private final ComponentHandle componentHandle; 
	private final IPath modulePath;
	private final IVirtualComponent virtualComponent;
	private final URI componentURI;

	/**
	 * <p>
	 * Creates a ArtifactEditModel instance that uses information from the
	 * <b>org.eclipse.wst.common.emfworkbench.integration.editModelFactory </b> extension point
	 * associated with anEditModelId attached to the project managed by aContext for a specific
	 * module referenced by aModuleURI. Resoures that are not recognized as defined Resources via
	 * the appropriate EditModel extension points will be accessed as read-only.
	 * </p>
	 * <p>
	 * This method is functionally equivalent to:
	 * </p>
	 * <p>
	 * <code>ArtifactEditModel(anEditModelId, aContext, toMakeReadOnly, true, aModuleURI)</code>
	 * </p>
	 * 
	 * @param anEditModelId
	 *            A unique identifier for the EditModel defined by the appropriate
	 *            <b>org.eclipse.wst.common.emfworkbench.integration.editModelFactory </b> extension
	 *            point.
	 * @param aContext
	 *            A valid EMFWorkbenchContext which helps manage the lifecycle of EMF resources for
	 *            a given project.
	 * @param toMakeReadOnly
	 *            True indicates that Resources loaded by the EditModel will not allow
	 *            modifications.
	 * @param aModuleURI
	 *            A fully-qualified URI that conforms to the "module:" format.
	 */

	public ArtifactEditModel(String anEditModelId, EMFWorkbenchContext aContext, boolean toMakeReadOnly, URI aModuleURI) {
		this(anEditModelId, aContext, toMakeReadOnly, true, aModuleURI);
	}

	/**
	 * 
	 * <p>
	 * Creates a ArtifactEditModel instance that uses information from the
	 * <b>org.eclipse.wst.common.emfworkbench.integration.editModelFactory </b> extension point
	 * associated with anEditModelId attached to the project managed by aContext for a specific
	 * module referenced by aModuleURI. Resoures that are not recognized as defined
	 * </p>*
	 * 
	 * @param anEditModelId
	 *            A unique identifier for the EditModel defined by the appropriate
	 *            <b>org.eclipse.wst.common.emfworkbench.integration.editModelFactory </b> extension
	 *            point.
	 * @param aContext
	 *            A valid EMFWorkbenchContext which helps manage the lifecycle of EMF resources for
	 *            a given project.
	 * @param toMakeReadOnly
	 *            True indicates that Resources loaded by the EditModel will not allow
	 *            modifications.
	 * @param toAccessUnknownResourcesAsReadOnly
	 *            True indicates that Resources not recognized by the EditModel be loaded as
	 *            read-only - such those loaded via {@see #getResource(URI)}.
	 * @param aModuleURI
	 *            A fully-qualified URI that conforms to the "module:" format.
	 *  
	 */

	public ArtifactEditModel(String anEditModelId, EMFWorkbenchContext aContext, boolean toMakeReadOnly, boolean toAccessUnknownResourcesAsReadOnly, URI aModuleURI) {
		super(anEditModelId, aContext, toMakeReadOnly, toAccessUnknownResourcesAsReadOnly);
		componentHandle =  ComponentHandle.create(null, aModuleURI);
		virtualComponent = ComponentCore.createComponent(componentHandle.getProject(), componentHandle.getName());
		componentURI = aModuleURI;
		modulePath = new Path(aModuleURI.path());
		processLoadedResources(componentHandle);
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
		// First check if passed URI is already normalized...
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
		return virtualComponent.getComponentTypeId();
	}
	public ComponentHandle getComponentHandle() { 
		return componentHandle;
	}

	public URI getModuleURI() {
		return componentURI;
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

		return super.getOrCreateResource(aUri);
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
	 * Gathers resources from the underlying resource set, determines if interested (@link
	 * processLoadedResources(URI aModuleURI))and request access (@link access(ReferencedResource
	 * aResource))to each resource incrementing the write/read count.
	 * </p>
	 * 
	 * @param aUri -
	 *            location of resource
	 */


	protected void processLoadedResources(ComponentHandle aComponentHandle) {
		List loadedResources = getResourceSet().getResources();
		if (!loadedResources.isEmpty()) {
			processResourcesIfInterrested(loadedResources);
		}
	}

	/**
	 * <p>
	 * Checks to make sure a flexible module structure exist for the resources in the resource set.
	 * This is achieved by querying ModuleCore(@see ModuleCore) for existing
	 * WorkbenchModuleResources (@see WorkbenchModuleResources). If the resource exist it processes
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
 
		IResource resourceResource;
		IVirtualResource[] virtualResources;
		for (int i = 0; i < size; i++) { 
			resourceToProcess = (Resource) theResources.get(i);
			if (resourceToProcess.getURI().lastSegment().equals(ModuleCoreNature.WTPMODULE_FILE_NAME)) continue;
			resourceResource = WorkbenchResourceHelper.getFile(resourceToProcess);
			if (resourceResource != null) {
				virtualResources = ComponentCore.createResources(resourceResource); 
				for (int resourcesIndex = 0; resourcesIndex < virtualResources.length; resourcesIndex++) {
					if (virtualComponent.equals(virtualResources[resourcesIndex].getComponent())) {
						if (resourceToProcess !=null && isInterrestedInResource(resourceToProcess)) {
							processResource(resourceToProcess);
							processed = true;
						}
					}
				}
			}
		}  
		return processed;
	}

	/**
	 * 
	 * <p>
	 * Generic way to retrieve containing information, within the platform.
	 * </p>
	 * 
	 * @return instance of the adapterType for this adapter class from adapter factory stored in the
	 *         adapter manager (@see AdapterManager)
	 *  
	 */

	public Object getAdapter(Class adapterType) {
		return Platform.getAdapterManager().getAdapter(this, adapterType);
	}

	public IVirtualComponent getVirtualComponent() {
		return virtualComponent;
	}
}