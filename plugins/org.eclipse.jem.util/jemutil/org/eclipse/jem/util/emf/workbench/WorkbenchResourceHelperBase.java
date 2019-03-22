/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 *  $$RCSfile: WorkbenchResourceHelperBase.java,v $$
 *  $$Revision: 1.6 $$  $$Date: 2008/03/12 14:21:39 $$ 
 */
package org.eclipse.jem.util.emf.workbench;

import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.*;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.jem.internal.util.emf.workbench.EMFWorkbenchContextFactory;
import org.eclipse.jem.internal.util.emf.workbench.WorkspaceResourceHandler;
import org.eclipse.jem.util.plugin.JEMUtilPlugin;


/**
 * Workbench resource helper
 * 
 * @since 1.0.0
 */
public class WorkbenchResourceHelperBase {

	/**
	 * Everything is static, don't know why there is an instance here.
	 */
	public static final WorkbenchResourceHelperBase INSTANCE = new WorkbenchResourceHelperBase();

	protected static WorkspaceResourceHandler workspaceURILoader = new WorkspaceResourceHandler();

	protected static void resolveContainedProxies(EObject refObject) {
		List contained = refObject.eContents();
		EObject mofObject;
		for (int i = 0; i < contained.size(); i++) {
			mofObject = (EObject) contained.get(i);
			resolveProxies(mofObject);
		}
	}

	protected static void resolveNonContainedProxies(EObject refObject) {
		List references = refObject.eClass().getEAllReferences();
		EReference reference;
		for (int i = 0; i < references.size(); i++) {
			reference = (EReference) references.get(i);
			if (!reference.isContainment()) {
				if (reference.isMany()) {
					List value = (List) refObject.eGet(reference);
					for (int j = 0; j < value.size(); j++)
						value.get(j);
				} else {
					refObject.eGet(reference);
				}
			}
		}
	}

	/**
	 * Force all of the proxies with <code>resource</code> to be resolved.
	 * 
	 * @param resource
	 * 
	 * @since 1.0.0
	 */
	public static void resolveProxies(Resource resource) {
		if (resource != null) {
			List topLevels = resource.getContents();
			EObject mofObject;
			for (int i = 0; i < topLevels.size(); i++) {
				mofObject = (EObject) topLevels.get(i);
				resolveProxies(mofObject);
			}
		}
	}

	/**
	 * Return a List of proxies that are contained by the <code>resource</code>.
	 * 
	 * @param resource
	 * @return list of proxies.
	 * 
	 * @since 1.0.0
	 */
	public static List gatherProxies(Resource resource) {
		if (resource == null)
			return Collections.EMPTY_LIST;
		List list = new ArrayList();
		List topLevels = resource.getContents();
		int size = topLevels.size();
		EObject mofObject;
		for (int i = 0; i < size; i++) {
			mofObject = (EObject) topLevels.get(i);
			gatherProxies((InternalEObject) mofObject, list);
		}
		return list;
	}

	protected static void gatherProxies(InternalEObject refObject, List proxies) {
		if (refObject == null)
			return;
		List contains = refObject.eClass().getEAllContainments();
		if (contains != null) {
			int size = contains.size();
			EStructuralFeature sf = null;
			for (int i = 0; i < size; i++) {
				sf = (EStructuralFeature) contains.get(i);
				gatherProxies(refObject, sf, proxies);
			}
		}
	}

	protected static void gatherProxies(InternalEObject refObject, EStructuralFeature sf, List proxies) {
		Object value = null;
		InternalEObject proxy = null;
		if (sf.isMany() || refObject.eIsSet(sf)) {
			value = refObject.eGet(sf, false);
			if (value != null) {
				if (sf.isMany()) {
					Iterator j = ((InternalEList) value).basicIterator();
					while (j.hasNext()) {
						proxy = (InternalEObject) j.next();
						if (proxy.eIsProxy())
							proxies.add(proxy);
					}
				} else if (((InternalEObject) value).eIsProxy())
					proxies.add(value);
			}
		}
	}

	protected static void resolveProxies(EObject refObject) {
		if (refObject != null) {
			resolveNonContainedProxies(refObject);
			resolveContainedProxies(refObject);
		}
	}

	/**
	 * Return an existing context base on <code>aProject</code>.
	 * 
	 * @param aProject
	 * @return the context base for the project or <code>null</code> if none.
	 * 
	 * @since 1.0.0
	 */
	public static EMFWorkbenchContextBase getEMFContext(IProject aProject) {
		return EMFWorkbenchContextFactory.INSTANCE.getEMFContext(aProject);
	}

	/**
	 * Create a resource from the URI. The URI must contain the project name, either as the first segment, or if in platform resource url protocol.
	 * {@link #getResourceSet(URI)}
	 * 
	 * @param uri
	 * @return a new resource for the uri or <code>null</code> if not a project uri
	 * 
	 * @since 1.0.0
	 */
	public static Resource createResource(URI uri) {
		ResourceSet set = getResourceSet(uri);
		if (set != null)
			return set.createResource(uri);
		return null;
	}

	/**
	 * Check for a cached Resource for the given URI, if none is found, create a new Resource for with the URI against the proper ResourceSet.
	 * 
	 * @param uri The URI MUST be either a "<b>platform:/resource/</b>project-name/...." type URI or it
	 * must be of type "project-name/...". This method will only return resources that are workbench project resources.
	 * Any other type of URI will cause <code>null</code> to be returned.
	 * @return resource or <code>null</code> if not a project uri.
	 * 
	 * @since 1.0.0
	 */
	public static Resource getExistingOrCreateResource(URI uri) {
		return getExistingOrCreateResource(uri, getResourceSet(uri));
	}
	
	/**
	 * Get the IFile for the URI. The URI must be a workbench project style URI. 
	 * @param uri The URI MUST be either a "<b>platform:/resource/</b>project-name/...." type URI or it
	 * must be of type "project-name/...". This method will only return resources that are workbench project resources.
	 * Any other type of URI will cause <code>null</code> to be returned.
	 * @return the IFile if the URI is a project form, <code>null</code> if not a project form, OR the project doesn't exist. The IFile returned doesn't necessarily exist. Use {@link IFile#exists()} to test that.
	 * 
	 * @since 1.2.0
	 */
	public static IFile getIFile(URI uri) {
		IProject project = getProject(uri);
		if (project != null) {
			IPath path;
			if (isPlatformResourceURI(uri)) {
				// Need to get the path and remove the first two segments (/resource/project name/).
				path = new Path(URI.decode(uri.path())).removeFirstSegments(2);
			} else {
				// Need to get the path and remove the first segment (/project name/).
				path = new Path(URI.decode(uri.path())).removeFirstSegments(1);
			}
			return project.getFile(path);
		} else
			return null;
	}

	/**
	 * Check for a cached Resource for the given URI, if none is found, create a new Resource for with the URI against the given ResourceSet.
	 * 
	 * @param uri 
	 * @param set
	 * @return resource or <code>null</code> if set was <code>null</code>.
	 * 
	 * @since 1.0.0
	 */
	public static Resource getExistingOrCreateResource(URI uri, ResourceSet set) {
		if (set != null) {
			Resource res = set.getResource(uri, false);
			if (res == null)
				res = set.createResource(uri);
			return res;
		} else
			return null;
	}

	/**
	 * Return a new or existing context base on <code>aProject</code>. Allow the <code>contributor</code> to contribute to the new or existing
	 * nature prior to returning.
	 * 
	 * @param aProject
	 * @param contributor
	 * @return the context base for the project.
	 * 
	 * @since 1.0.0
	 */
	public static EMFWorkbenchContextBase createEMFContext(IProject aProject, IEMFContextContributor contributor) {
		return EMFWorkbenchContextFactory.INSTANCE.createEMFContext(aProject, contributor);
	}

	/**
	 * Does the passed URI have the form platform:/resource/... ?
	 * 
	 * @param uri
	 * @return <code>true</code> if it is a platform resource protocol.
	 * 
	 * @since 1.0.0
	 */
	public static boolean isPlatformResourceURI(URI uri) {
		return JEMUtilPlugin.PLATFORM_PROTOCOL.equals(uri.scheme()) && JEMUtilPlugin.PLATFORM_RESOURCE.equals(uri.segment(0));
	}

	/**
	 * This api may be used to cache a Resource if it has a URI that is Workspace relative. Return true if it is cached.
	 * 
	 * @param aResource
	 * @return <code>true</code> if it was successful to cache.
	 * 
	 * @since 1.0.0
	 */
	public static boolean cacheResource(Resource aResource) {
		if (aResource != null) {
			ResourceSet set = getResourceSet(aResource.getURI());
			if (set != null)
				return set.getResources().add(aResource);
		}
		return false;
	}

	/**
	 * This api is used if you create a new MOF resource and you want to add it to the correct ResourceSet. In order to do that, we need the IProject
	 * that you want aResource to be cached within as well as the IPath which is the full path of the location of the new Resource.
	 * 
	 * @param aProject
	 * @param aResource
	 * @param fullPath
	 * @return <code>true</code> if resource was cached.
	 * 
	 * @since 1.0.0
	 */
	public static boolean cacheResource(IProject aProject, Resource aResource, IPath fullPath) {
		if (aProject == null || aResource == null || !aProject.isAccessible())
			return false;
		ResourceSet set = getResourceSet(aProject);
		if (set != null) {
			URI converted = set.getURIConverter().normalize(aResource.getURI());
			if (converted != aResource.getURI())
				aResource.setURI(converted);
			return set.getResources().add(aResource);
		}
		return false;
	}

	/**
	 * Get the path of the project resource relative to the workspace or relative to the list of containers in this project.
	 * 
	 * @param aResource
	 * @return path
	 * 
	 * @since 1.0.0
	 */
	public static String getActualProjectRelativeURI(IResource aResource) {
		if (aResource == null || !aResource.isAccessible())
			return null;
		IProject project = aResource.getProject();
		IPath path = getPathInProject(project, aResource.getFullPath());
		return path.makeRelative().toString();
	}

	/**
	 * Return an IPath that can be used to load a Resource using the <code>fullPath</code>. This will be a project relative path.
	 * 
	 * @param project
	 * @param fullPath
	 * @return path
	 * 
	 * @since 1.0.0
	 */
	public static IPath getPathInProject(IProject project, IPath fullPath) {
		List containers = getProjectURIConverterContainers(project);
		if (!containers.isEmpty())
			return getPathFromContainers(containers, fullPath);
		return fullPath;
	}

	protected static List getProjectURIConverterContainers(IProject project) {
		EMFWorkbenchContextBase nature = createEMFContext(project, null);
		if (nature != null) {
			WorkbenchURIConverter conv = (WorkbenchURIConverter) nature.getResourceSet().getURIConverter();
			if (conv != null)
				return conv.getInputContainers();
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * If this path is contained within one of the listed containers, then return the path relative to the container.
	 * 
	 * @param containers
	 * @param fullPath
	 * @return path relative to a container, or unchanged path if not in a container.
	 * 
	 * @since 1.0.0
	 */
	public static IPath getPathFromContainers(List containers, IPath fullPath) {
		IContainer container = null;
		IPath result;
		int size = containers.size();
		int matching = -1;
		IPath containerPath;
		for (int i = 0; i < size; i++) {
			container = (IContainer) containers.get(i);
			containerPath = container.getFullPath();
			matching = fullPath.matchingFirstSegments(containerPath);
			if (matching > 0 && matching == containerPath.segmentCount()) {
				result = fullPath.removeFirstSegments(matching);
				result = result.makeRelative();
				return result;
			}
		}
		return fullPath;
	}

	/**
	 * Return true if the <code>uri</code> has its container segments visible from the input containers for the <code>project</code>.
	 * 
	 * @param project
	 * @param uri
	 * @return <code>true</code> if the uri is visible from the input containers.
	 * 
	 * @since 1.0.0
	 */
	public static boolean hasContainerStructure(IProject project, URI uri) {
		if (project != null && uri != null) {
			IPath path = new Path(uri.toString());
			List containers = getProjectURIConverterContainers(project);
			int segmentCount = path.segmentCount();
			IPath containerPath = segmentCount > 1 ? path.removeLastSegments(1) : null;
			IContainer container = null;
			for (int i = 0; i < containers.size(); i++) {
				container = (IContainer) containers.get(i);
				if (!container.isAccessible())
					continue;
				if (segmentCount == 1) {
					if (container == project)
						return true;
				} else if (containerPath != null) {
					IFolder folder = container.getFolder(containerPath);
					if (folder != null && folder.isAccessible())
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * Get the resource for the uri. The URI MUST be either a "<b>platform:/resource/</b>project-name/...." type URI or it
	 * must be of type "project-name/...". This method will only return resources that are workbench project resources.
	 * Any other type of URI will cause <code>null</code> to be returned. It will be loaded if not already loaded. If it is not to
	 * be loaded if not loaded use {@link #getResource(URI, boolean)} instead.
	 * 
	 * @param uri must be either a "<b>platform:/resource/</b>project-name/..." form or it must be "project-name/...". Any other form will be invalid.
	 * @return resource if uri is for a valid workbench project resource or <code>null</code> if project not found or not a valid project resource.
	 * 
	 * @throws WrappedException if valid project format URI but file not found or some other error on load.
	 * @since 1.0.0
	 */
	public static Resource getResource(URI uri) {
		return getResource(uri, true);
	}

	/**
	 * Return the Resource for the passed IFile without forcing a load.
	 * 
	 * @param aFile
	 * @return
	 * 
	 * @since 1.0.0
	 */
	public static Resource getResource(IFile aFile) {
		return getResource(aFile, false);
	}

	/**
	 * Return the Resource for the passed IFile, forcing a load if <code>loadOnDemand</code> says so.
	 * 
	 * @param aFile
	 * @param loadOnDemand
	 *            <code>true</code> will force a load of resource if not loaded.
	 * @return
	 * 
	 * @since 1.0.0
	 */
	public static Resource getResource(IFile aFile, boolean loadOnDemand) {
		if (aFile != null)
			return getResource(URI.createPlatformResourceURI(aFile.getFullPath().toString()), loadOnDemand);
		return null;
	}

	/**
	 * Return the Resource for the passed IFile without a load if not loaded.
	 * 
	 * @param aFile
	 * @return
	 * 
	 * @since 1.0.0
	 */
	public static Resource load(IFile aFile) {
		return getResource(aFile, true);
	}

	/**
	 * Get the resource for the uri. The URI MUST be either a "<b>platform:/resource/</b>project-name/...." type URI or it
	 * must be of type "project-name/...". This method will only return resources that are workbench project resources.
	 * Any other type of URI will cause <code>null</code> to be returned.
	 * 
	 * @param uri must be either a "<b>platform:/resource/</b>project-name/..." form or it must be "project-name/...". Any other form will be invalid.
	 * @param loadOnDemand <code>true</code> will cause resource to be loaded if not already loaded.
	 * @return resource if uri is for a valid workbench project resource, or <code>null</code> if project not found, or not a valid project resource uri.
	 * 
	 * @throws WrappedException if valid project format URI but file not found or some other error on load if loadOnDemand is true.
	 * @since 1.0.0
	 */
	public static Resource getResource(URI uri, boolean loadOnDemand) {
		ResourceSet set = getResourceSet(uri);
		if (set != null)
			return set.getResource(uri, loadOnDemand);
		return null;
	}

	/**
	 * Return a ResourceSet for the passed URI. The URI should be in the format platform:/resource/{project name}/... or {project name}/... for this
	 * api to work.
	 * 
	 * @param uri
	 * @return the resource set or <code>null</code> if not of correct form or project doesn't have a resource set.
	 * 
	 * @since 1.0.0
	 */
	public static ResourceSet getResourceSet(URI uri) {
		IProject project = getProject(uri);
		if (project != null && project.isAccessible())
			return getResourceSet(project);
		else
			return null;
	}
	
	/*
	 * Get the project for the uri if the uri is a valid workbench project format uri. null otherwise.
	 */
	private static IProject getProject(URI uri) {
		String projectName;
		if (isPlatformResourceURI(uri))
			projectName = uri.segment(1);
		else if (uri.scheme() == null) {
			projectName = new Path(uri.path()).segment(0); //assume project name is first in the URI
		} else
			return null;
		IProject project = getWorkspace().getRoot().getProject(URI.decode(projectName));
		if (project != null && project.isAccessible())
			return project;
		else
			return null;
	}

	/**
	 * Return the ResourceSet for the passed IProject.
	 * 
	 * @param project
	 * @return resource set
	 */
	public static ResourceSet getResourceSet(IProject project) {
		EMFWorkbenchContextBase nat = createEMFContext(project, null);
		if (nat != null)
			return nat.getResourceSet();
		return null;
	}

	/**
	 * Get the workspace. (just use {@link ResourcesPlugin#getWorkspace()}).
	 * 
	 * @return
	 * 
	 * @since 1.0.0
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Get the project associated with the resource set.
	 * 
	 * @param set
	 * @return project or <code>null</code> if resource set not associated with a project.
	 * 
	 * @since 1.0.0
	 */
	public static IProject getProject(ResourceSet set) {
		if (set != null) {
			if (set instanceof ProjectResourceSet) {
				ProjectResourceSet pset = (ProjectResourceSet) set;
				return pset.getProject();
			}
		}
		return null;
	}

	protected static boolean isRegisteredURIMapping(String href) {
		if (href != null) {
			String file = href;
			int index = href.indexOf('#');
			if (index > -1)
				file = href.substring(0, index);
			return URIConverter.URI_MAP.get(file) != null;
		}
		return false;
	}

	/**
	 * Remove all of the resources from the resource set and then unload them. Unload forces all of the objects to become proxies so next resolve will
	 * reload the resource.
	 * 
	 * @param resources
	 * @param aSet
	 * 
	 * @since 1.0.0
	 */
	public static void removeAndUnloadAll(List resources, ResourceSet aSet) {
		if (aSet == null || resources == null || resources.isEmpty())
			return;
		aSet.getResources().removeAll(resources);
		Resource res;
		for (int i = 0; i < resources.size(); i++) {
			res = (Resource) resources.get(i);
			res.unload();
		}
	}

	/**
	 * Turn object into a proxy.
	 * 
	 * @param anObject
	 * @return <code>true</code> if object was able to become a proxy.
	 * 
	 * @since 1.0.0
	 */
	public static boolean becomeProxy(EObject anObject) {
		if (anObject != null) {
			Resource res = anObject.eResource();
			if (res != null) {
				URI uri = res.getURI();
				((InternalEObject) anObject).eSetProxyURI(uri.appendFragment(res.getURIFragment(anObject)));
				//anObject.eAdapters().clear();
				return true;
			}
		}
		return false;
	}

	/**
	 * Return true if the WrappedException is actually a Resource Not Found.
	 * 
	 * @param wrappedEx
	 * @return <code>true</code> is exception wrappers a resource not found.
	 * @since 1.0.0
	 */
	public static boolean isResourceNotFound(WrappedException wrappedEx) {
		Exception excep = wrappedEx.exception();
		while (excep instanceof WrappedException) {
			excep = ((WrappedException) excep).exception();
		}
		return primIsResourceNotFound(excep);
	}

	private static boolean primIsResourceNotFound(Throwable excep) {
		if (excep instanceof CoreException) {
			IStatus status = ((CoreException) excep).getStatus();
			return status.getCode() == IResourceStatus.RESOURCE_NOT_FOUND && ResourcesPlugin.PI_RESOURCES.equals(status.getPlugin());
		}
		return false;
	}

	/**
	 * Return true if the WrappedException is actually a Resource Not Found.
	 * 
	 * @param wrappedEx
	 * @return <code>true</code> is exception wrappers a resource not found.
	 * @since 1.0.0
	 */
	public static boolean isResourceNotFound(Resource.IOWrappedException wrappedEx) {
		return primIsResourceNotFound(wrappedEx.getCause());
	}

	/**
	 * Return a URI represenation of the platformURI without the leading "platform:/resource/" if present.
	 * 
	 * @param platformURI
	 * @return uri
	 * @since 1.0.0
	 */
	public static URI getNonPlatformURI(URI platformURI) {
		if (isPlatformResourceURI(platformURI)) {
			String uriString = primGetNonPlatformURIString(platformURI);
			return URI.createURI(uriString);
		}
		return platformURI;
	}

	/**
	 * Return a String represenation of the platformURI without the leading "platform:/resource/" if present.
	 * 
	 * @param platformURI
	 * @return
	 * @since 1.0.0
	 */
	public static String getNonPlatformURIString(URI platformURI) {
		if (isPlatformResourceURI(platformURI)) { return primGetNonPlatformURIString(platformURI); }
		return platformURI.toString();
	}

	/*
	 * Remove "platform:/resource/" from the front of the platformURI and return the remaining String.
	 */
	private static String primGetNonPlatformURIString(URI platformURI) {
		String uriString = platformURI.toString();
		//"platform:/resource/" is 19 characters.
		return uriString.substring(19, uriString.length());
	}

	/**
	 * Does the passed URI have the form platform:/plugin/... ?
	 * 
	 * @param uri
	 * @return <code>true</code> if uri is platform plugin protocol.
	 * 
	 * @since 1.0.0
	 */
	public static boolean isPlatformPluginResourceURI(URI uri) {
		if (uri == null) return false;
		return JEMUtilPlugin.PLATFORM_PROTOCOL.equals(uri.scheme()) && JEMUtilPlugin.PLATFORM_PLUGIN.equals(uri.segment(0));
	}

}