/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 *  $$RCSfile: EMFWorkbenchContextBase.java,v $$
 *  $$Revision: 1.1 $$  $$Date: 2005/01/07 20:19:23 $$ 
 */
package org.eclipse.jem.util.emf.workbench;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

import org.eclipse.jem.internal.util.emf.workbench.*;
import org.eclipse.jem.internal.util.emf.workbench.nls.EMFWorkbenchResourceHandler;
import org.eclipse.jem.util.plugin.JEMUtilPlugin;


/**
 * ContextBase for EMFWorkbench.
 * 
 * <p>
 * This is meant to be subclassed as needed for additional or override function. It will be instantiated by default.
 * </p>
 * 
 * @since 1.0.0
 */
public class EMFWorkbenchContextBase {

	protected IProject project;

	protected ProjectResourceSet resourceSet;

	/**
	 * Construct with a project.
	 * 
	 * @param aProject
	 * 
	 * @since 1.0.0
	 */
	public EMFWorkbenchContextBase(IProject aProject) {
		if (aProject == null)
			throw new IllegalArgumentException(EMFWorkbenchResourceHandler.getString("EMFWorkbenchContextBase_ERROR_1")); //$NON-NLS-1$
		project = aProject;
	}

	/**
	 * Dispose of the context base.
	 * 
	 * 
	 * @since 1.0.0
	 */
	public void dispose() {
		if (resourceSet != null)
			resourceSet.release();
		resourceSet = null;
		project = null;
	}

	/**
	 * Get the project this context is associated with.
	 * 
	 * @return project
	 * 
	 * @since 1.0.0
	 */
	public IProject getProject() {
		return project;
	}

	/**
	 * Return the resource set (creating if needed) for context.
	 * 
	 * @return resource set
	 * 
	 * @since 1.0.0
	 */
	public ProjectResourceSet getResourceSet() {
		if (resourceSet == null) {
			resourceSet = createResourceSet();
			initializeResourceSet(resourceSet);
		}
		return resourceSet;
	}

	/**
	 * Used for optimization; answer whether a resourceSet has been created
	 * 
	 * @return <code>true</code> if a resource set has been created.
	 * 
	 * @since 1.0.0
	 */
	public boolean hasResourceSet() {
		return resourceSet != null;
	}

	/**
	 * Initialize the resource set.
	 * 
	 * @param aResourceSet
	 * 
	 * @since 1.0.0
	 */
	protected void initializeResourceSet(ProjectResourceSet aResourceSet) {
		createResourceSetSynchronizer(aResourceSet);
		aResourceSet.setURIConverter(createURIConverter(aResourceSet));
		aResourceSet.add(new WorkspaceResourceHandler());
		JEMUtilPlugin.getDefault().addExtendedResourceHandlers(aResourceSet);

	}

	/**
	 * Create the resource set. By default it is a ProjectResourceSetImpl.
	 * 
	 * @return project's new resource set.
	 * 
	 * @since 1.0.0
	 */
	protected ProjectResourceSet createResourceSet() {
		if (project == null)
			throw new IllegalStateException(EMFWorkbenchResourceHandler.getString("EMFWorkbenchContextBase_ERROR_2")); //$NON-NLS-1$
		return new ProjectResourceSetImpl(project);
	}

	/**
	 * Create a URIConverter for the resource set.
	 * 
	 * @param aResourceSet
	 * @return a uri converter.
	 * 
	 * @since 1.0.0
	 */
	protected WorkbenchURIConverter createURIConverter(ProjectResourceSet aResourceSet) {
		return new WorkbenchURIConverterImpl(getProject(), aResourceSet.getSynchronizer());
	}

	/**
	 * Create a resource set workbench synchronizer.
	 * 
	 * @param aResourceSet
	 * @return a resource set workbench synchronizer.
	 * 
	 * @since 1.0.0
	 */
	protected ResourceSetWorkbenchSynchronizer createResourceSetSynchronizer(ProjectResourceSet aResourceSet) {
		return EMFWorkbenchContextFactory.INSTANCE.createSynchronizer(aResourceSet, getProject());
	}

	/**
	 * Delete the resource from the workspace.
	 * 
	 * @param aResource
	 * @throws CoreException
	 * 
	 * @since 1.0.0
	 */
	public void deleteResource(Resource aResource) throws CoreException {
		if (aResource != null)
			deleteFile(aResource);
	}

	/**
	 * Delete the file associated with the resource.
	 * 
	 * @param resource
	 * 
	 * @since 1.0.0
	 */
	public void deleteFile(Resource resource) {
		throw new UnsupportedOperationException(EMFWorkbenchResourceHandler.getString("EMFWorkbenchContextBase_ERROR_0")); //$NON-NLS-1$
	}

	/**
	 * Get resource (with the given URI) from the project resource set. Load it if not already loaded.
	 * 
	 * @param uri
	 * @return resource for the uri, or <code>null</code> if not found.
	 * 
	 * @since 1.0.0
	 */
	public Resource getResource(URI uri) {
		return this.resourceSet.getResource(uri, true);
	}

}