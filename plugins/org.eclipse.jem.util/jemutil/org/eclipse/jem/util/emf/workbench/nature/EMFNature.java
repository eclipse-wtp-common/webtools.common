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
 *  $$RCSfile: EMFNature.java,v $$
 *  $$Revision: 1.2 $$  $$Date: 2005/01/26 14:46:54 $$ 
 */
package org.eclipse.jem.util.emf.workbench.nature;

import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.emf.common.util.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;

import org.eclipse.jem.internal.util.emf.workbench.nature.EMFNatureRegistry;
import org.eclipse.jem.util.emf.workbench.*;
import org.eclipse.jem.util.logger.proxy.Logger;


/**
 * The base EMFNature.
 * <p>
 * This is expected to be subclassed by clients, but there are default subclasses available.
 * </p>
 * 
 * @since 1.0.0
 */
public abstract class EMFNature implements IProjectNature, IEMFContextContributor {

	protected IProject project;

	protected EMFWorkbenchContextBase emfContext;

	protected boolean hasConfigured = false;
	
	public static XMLResource SHARED_RESOURCE = new XMLResourceImpl();	

	public EMFNature() {
		super();
	}

	/**
	 * Add the nature id to the project.
	 * 
	 * @param proj
	 * @param natureId
	 * @throws CoreException
	 * 
	 * @since 1.0.0
	 */
	protected static void addNatureToProject(IProject proj, String natureId) throws CoreException {
		ProjectUtilities.addNatureToProject(proj, natureId);
	}

	/**
	 * Configures the project with this nature. This is called by <code>IProject.addNature</code> and should not be called directly by clients. The
	 * nature extension id is added to the list of natures on the project by <code>IProject.addNature</code>, and need not be added here.
	 * 
	 * <p>
	 * All subtypes must call super. The better way for subtypes is to override primConfigure instead.
	 * </p>
	 * 
	 * @throws CoreException
	 * @since 1.0.0
	 */
	public void configure() throws org.eclipse.core.runtime.CoreException {
		if (!hasConfigured) {
			hasConfigured = true;
			primConfigure();
		}
	}

	/**
	 * Called from configure the first time configure is called on the nature. Default is do nothing. Subclasses should override and add in their own
	 * configuration.
	 * 
	 * @throws org.eclipse.core.runtime.CoreException
	 * 
	 * @since 1.0.0
	 */
	protected void primConfigure() throws org.eclipse.core.runtime.CoreException {

	}

	/**
	 * Create an EMF context for the project.
	 * 
	 * @throws CoreException
	 * 
	 * @since 1.0.0
	 */
	protected void createEmfContext() throws CoreException {
		WorkbenchResourceHelperBase.createEMFContext(getProject(), this);
	}

	/**
	 * Create a folder relative to the project based on aProjectRelativePathString.
	 * 
	 * @param aProjectRelativePathString
	 * @return
	 * @throws CoreException
	 * 
	 * @since 1.0.0
	 */
	public IFolder createFolder(String aProjectRelativePathString) throws CoreException {
		if (aProjectRelativePathString != null && aProjectRelativePathString.length() > 0)
			return createFolder(new Path(aProjectRelativePathString));
		return null;
	}

	/**
	 * Create a folder relative to the project based on aProjectRelativePathString.
	 * 
	 * @param aProjectRelativePath
	 * @return
	 * @throws CoreException
	 * 
	 * @since 1.0.0
	 */
	public IFolder createFolder(IPath aProjectRelativePath) throws CoreException {
		if (aProjectRelativePath != null && !aProjectRelativePath.isEmpty()) {
			IFolder folder = getWorkspace().getRoot().getFolder(getProjectPath().append(aProjectRelativePath));
			if (!folder.exists()) {
				ProjectUtilities.ensureContainerNotReadOnly(folder);
				folder.create(true, true, null);
			}
			return folder;
		}
		return null;
	}

	/**
	 * Removes this nature from the project, performing any required deconfiguration. This is called by <code>IProject.removeNature</code> and
	 * should not be called directly by clients. The nature id is removed from the list of natures on the project by
	 * <code>IProject.removeNature</code>, and need not be removed here.
	 * 
	 * @throws CoreException
	 * @since 1.0.0
	 */
	public void deconfigure() throws org.eclipse.core.runtime.CoreException {
		emfContext = null;
	}

	/**
	 * Return true if the IFile with the given name exists in this project.
	 * 
	 * @param aFileName
	 *            filename can be relative to one of the input file paths for the WorkbenchURIConverter.
	 * @return <code>true</code> if filename exists in this project
	 * 
	 * @since 1.0.0
	 */
	public boolean fileExists(String aFileName) {
		if (aFileName == null)
			return false;

		IPath path = new Path(aFileName);
		if (path.isAbsolute())
			return ResourcesPlugin.getWorkspace().getRoot().getFile(path).exists();
		else
			return getWorkbenchURIConverter().canGetUnderlyingResource(aFileName);
	}

	/**
	 * Get the resource set for the project
	 * 
	 * @return
	 * 
	 * @since 1.0.0
	 */
	public ResourceSet getResourceSet() {
		return getEmfContextBase().getResourceSet();
	}

	/**
	 * Returns the EMF root folder for the project. Defaults to the project. Subclasses can override.
	 * 
	 * @return EMF root folder for the project.
	 * 
	 * @since 1.0.0
	 */
	public IContainer getEMFRoot() {
		return getProject();
	}

	/**
	 * Used for optimizations; answers whether a mof context for this nature has exists yet
	 * 
	 * @deprecated use hasResourceSet();
	 * @since 1.0.0
	 */
	public boolean hasContext() {
		return hasResourceSet();
	}

	/**
	 * Is there a resource set yet for the project.
	 * 
	 * @return <code>true</code> if there is a resource set for the project.
	 * 
	 * @since 1.0.0
	 */
	public boolean hasResourceSet() {
		return emfContext != null && emfContext.hasResourceSet();
	}

	/**
	 * Lazy initializer; for migration of existing workspaces where configure will never get called.
	 * 
	 * @return context base for the project.
	 * 
	 * @since 1.0.0
	 */
	protected EMFWorkbenchContextBase getEmfContextBase() {
		if (emfContext == null) {
			try {
				createEmfContext();
			} catch (CoreException ex) {
				Logger.getLogger().logError(ex);
			}
		}
		return emfContext;
	}

	/**
	 * Get the IFile with the given name if it is in this project.
	 * 
	 * @param aFileName
	 *            filename can be relative to one of the input file paths for the WorkbenchURIConverter.
	 * @return file it it is in this project, or <code>null</code> if it doesn't.
	 * 
	 * @since 1.0.0
	 */
	public IFile getFile(String aFileName) {
		return getWorkbenchURIConverter().getFile(aFileName);
	}

	/**
	 * Return the nature's ID.
	 * 
	 * @return nature id
	 * 
	 * @since 1.0.0
	 */
	public abstract String getNatureID();

	/**
	 * Return the ID of the plugin that this nature is contained within.
	 * 
	 * @return
	 * 
	 * @since 1.0.0
	 */
	protected abstract String getPluginID();

	/**
	 * Returns the project to which this project nature applies.
	 * 
	 * @return the project handle
	 * @since 1.0.0
	 */
	public org.eclipse.core.resources.IProject getProject() {
		return project;
	}

	/**
	 * Return the full path of the project.
	 * 
	 * @return full project path (relative to workspace)
	 * @since 1.0.0
	 */
	public IPath getProjectPath() {
		return getProject().getFullPath();
	}

	/**
	 * Get the server property of the project from the supplied key
	 * 
	 * @param key
	 *            java.lang.String
	 * @deprecated we cannont use persistent properties because they are not stored in the repository
	 * @since 1.0.0
	 */
	protected String getProjectServerValue(String key) {
		if (key == null)
			return null;
		try {
			QualifiedName wholeName = qualifiedKey(key);
			return getProject().getPersistentProperty(wholeName);
		} catch (CoreException exception) {
			//If we can't find it assume it is null
			exception.printStackTrace();
			return null;
		}
	}

	/**
	 * Get WorkbenchURIConverter for this project.
	 * <p>
	 * This method assumes the URIConverter on the ResourceSet is the one that was created for the ResourceSet on behalf of this nature runtime.
	 * </p>
	 * 
	 * @return
	 * 
	 * @since 1.0.0
	 */
	protected WorkbenchURIConverter getWorkbenchURIConverter() {
		return (WorkbenchURIConverter) getResourceSet().getURIConverter();
	}

	public IWorkspace getWorkspace() {
		return getProject().getWorkspace();
	}

	/**
	 * @deprecated use getResource(URI)
	 */
	public Resource getXmiResource(String uri) {
		return getResource(URI.createURI(uri));
	}

	/**
	 * Get the resource for this uri. It will use the resource set of the project to find it. It will load if not already loaded.
	 * 
	 * @param uri
	 * @return resource or <code>null</code> if resource is not found.
	 * 
	 * @since 1.0.0
	 */
	public Resource getResource(URI uri) {
		try {
			return getResourceSet().getResource(uri, true);
		} catch (WrappedException ex) {
			if (!WorkbenchResourceHelperBase.isResourceNotFound(ex))
				throw ex;
		}
		return null;
	}

	/**
	 * @deprecated use getResourceSet()
	 */
	public ResourceSet getXmiResourceSet() {
		return getResourceSet();
	}

	/**
	 * Make sure that all dependent components are initialized before creating the ResourceSet.
	 */
	protected void initializeDependentComponents() {
		//com.ibm.etools.java.init.JavaInit.init();
	}

	/**
	 * @deprecated use createResource(URI)
	 */
	public Resource makeXmiResource(String uri) {
		return createResource(URI.createURI(uri));
	}

	/**
	 * @deprecated use createResource(URI)
	 */
	public Resource makeXmiResource(String uri, EList anExtent) {
		Resource res = makeXmiResource(uri);
		if (res != null)
			res.getContents().addAll(anExtent);
		return res;
	}

	/**
	 * Create the resource for this uri. It will use the resource set of the project to create it.
	 * 
	 * @param uri
	 * @return resource
	 * 
	 * @since 1.0.0
	 */
	public Resource createResource(URI uri) {
		return getResourceSet().createResource(uri);
	}

	/*
	 * Return the QualifedValue for key for storage in the repository. The key is qualifed with the package name to avoid collision. @return
	 * QualifedName @param key java.lang.String
	 */
	private QualifiedName qualifiedKey(String key) {
		return new QualifiedName(getPluginID(), key);
	}

	/**
	 * Register the given nature id as an EMFNature.
	 * 
	 * @param natureID
	 * 
	 * @since 1.0.0
	 */
	public static void registerNatureID(String natureID) {
		EMFNatureRegistry.singleton().REGISTERED_NATURE_IDS.add(natureID);
	}

	/**
	 * Sets the project to which this nature applies. Used when instantiating this project nature runtime. This is called by
	 * <code>IProject.addNature</code> and should not be called directly by clients.
	 * 
	 * @param project
	 *            the project to which this nature applies
	 * 
	 * @since 1.0.0
	 */
	public void setProject(org.eclipse.core.resources.IProject newProject) {
		project = newProject;
	}

	/**
	 * Set the server property of the project from the supplied value
	 * 
	 * @param key
	 *            java.lang.String
	 * @param value
	 *            String
	 * @deprecated we cannont use persistent properties because they are not stored in the repository
	 */
	protected void setProjectServerValue(String key, String value) {
		if (key != null) {
			try {
				QualifiedName wholeName = qualifiedKey(key);
				getProject().setPersistentProperty(wholeName, value);
			} catch (CoreException exception) {
				//If we can't find it assume it is null
				exception.printStackTrace();
				return;
			}
		}
	}

	/**
	 * Shutdown the EMF nature
	 * 
	 * 
	 * @since 1.0.0
	 */
	public void shutdown() {
		if (getResourceSet() != null)
			((ProjectResourceSet) getResourceSet()).release();
	}

	/**
	 * Return a list of EMFNatures based on the natures that have been configured for this project.
	 * 
	 * @return List of EMFNatures
	 * @param project
	 * @return list of natures configured for the project.
	 * @since 1.0.0
	 */
	public static List getRegisteredRuntimes(IProject project) {
		List result = null;
		EMFNature nature = null;
		if (project != null && project.isAccessible()) {
			String natureID;
			Iterator it = EMFNatureRegistry.singleton().REGISTERED_NATURE_IDS.iterator();
			while (it.hasNext()) {
				natureID = (String) it.next();
				try {
					nature = (EMFNature) project.getNature(natureID);
				} catch (CoreException e) {
				}
				if (nature != null) {
					if (result == null)
						result = new ArrayList(2);
					result.add(nature);
				}
			}
		}
		return result == null ? Collections.EMPTY_LIST : result;
	}

	/**
	 * Return a list of nature ids based on the natures that have been configured for this project.
	 * 
	 * @return list of configured nature ids.
	 * @param project
	 */
	public static List getRegisteredRuntimeIDs(IProject project) {
		List result = null;
		String natureID = null;
		if (project != null && project.isAccessible()) {
			Iterator it = EMFNatureRegistry.singleton().REGISTERED_NATURE_IDS.iterator();
			while (it.hasNext()) {
				natureID = (String) it.next();
				try {
					if (project.hasNature(natureID)) {
						if (result == null)
							result = new ArrayList(2);
						result.add(natureID);
					}
				} catch (CoreException e) {
				}
			}
		}
		return result == null ? Collections.EMPTY_LIST : result;
	}

	/**
	 * Return if the project has the given nature.
	 * 
	 * @param project
	 * @param natureId
	 * @return <code>true</code> if project has given nature
	 * 
	 * @since 1.0.0
	 */
	public static boolean hasRuntime(IProject project, String natureId) {
		if (project == null || !project.isAccessible())
			return false;
		try {
			return project.hasNature(natureId);
		} catch (CoreException e) {
			return false;
		}
	}

	/**
	 * Return if the project has any one of the possible given nature ids.
	 * 
	 * @param project
	 * @param possibleNatureIds
	 * @return <code>true</code> if at least one of the possible natures id is configured for the project.
	 * 
	 * @since 1.0.0
	 */
	public static boolean hasRuntime(IProject project, String[] possibleNatureIds) {
		if (project != null) {
			for (int i = 0; i < possibleNatureIds.length; i++) {
				if (hasRuntime(project, possibleNatureIds[i]))
					return true;
			}
		}
		return false;
	}

}