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
/*
 * Created on Mar 4, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.eclipse.wst.common.internal.emfworkbench.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.wst.common.internal.emf.resource.ReferencedResource;
import org.eclipse.wst.common.internal.emfworkbench.WorkbenchResourceHelper;

import com.ibm.wtp.common.logger.proxy.Logger;
import com.ibm.wtp.emf.workbench.ProjectResourceSet;
import com.ibm.wtp.emf.workbench.ResourceSetWorkbenchSynchronizer;
import com.ibm.wtp.emf.workbench.plugin.EMFWorkbenchPlugin;
import com.ibm.wtp.internal.emf.workbench.EMFWorkbenchContextFactory;

/**
 * @author schacher
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ResourceSetWorkbenchEditSynchronizer extends ResourceSetWorkbenchSynchronizer implements IResourceDeltaVisitor {
	private static final String CLASS_EXTENSION = "class"; //$NON-NLS-1$
	private static final String JAVA_EXTENSION = "java"; //$NON-NLS-1$
	private Set recentlySavedFiles = new HashSet();
	private Map ignoredFilesCache = new HashMap();

	/** The emf resources to be removed from the resource set as a result of a delta */
	protected List deferredRemoveResources = new ArrayList();
	protected List deferredUnloadResources = new ArrayList();
	protected List deferredLoadResources = new ArrayList();

	protected List autoloadResourcesURIs = new ArrayList();



	/**
	 * @param aResourceSet
	 * @param aProject
	 */
	public ResourceSetWorkbenchEditSynchronizer(ResourceSet aResourceSet, IProject aProject) {
		super(aResourceSet, aProject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.internal.emfworkbench.ResourceSetWorkbenchSynchronizer#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		super.resourceChanged(event);
		try {
			acceptDelta(event);
			notifyExtendersIfNecessary();
			processDeferredResources();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			deferredRemoveResources.clear();
			deferredUnloadResources.clear();
			deferredLoadResources.clear();
		}
	}

	protected void processDeferredRemovedResources() {
		Resource resource = null;
		for (int i = 0; i < deferredRemoveResources.size(); i++) {
			resource = (Resource) deferredRemoveResources.get(i);
			resourceSet.getResources().remove(resource);
			resource.unload();
		}
	}

	protected void processDeferredUnloadedResources() {
		Resource resource = null;
		for (int i = 0; i < deferredUnloadResources.size(); i++) {
			resource = (Resource) deferredUnloadResources.get(i);
			resource.unload();
		}
	}

	private void processDeferredLoadResources() {
		URI uri = null;
		for (int i = 0; i < deferredLoadResources.size(); i++) {
			uri = (URI) deferredLoadResources.get(i);
			try {
				resourceSet.getResource(uri, true);
			} catch (WrappedException ex) {
				Logger.getLogger().logError(ex);
			}

		}
	}

	protected void acceptDelta(IResourceChangeEvent event) {
		IResourceDelta delta = event.getDelta();
		// search for changes to any projects using a visitor
		if (delta != null) {
			try {
				delta.accept(this);
			} catch (Exception e) {
				Logger.getLogger().logError(e);
			}
		}
	}

	/**
	 * The project is going away so we need to cleanup ourself and the ResourceSet. TODO Need to
	 * push up this code to ResourceSetWorkbenchSynchronizer in next release.
	 */
	protected void release() {
		if (EMFWorkbenchPlugin.isActivated()) {
			try {
				if (resourceSet instanceof ProjectResourceSet)
					((ProjectResourceSet) resourceSet).release();
			} finally {
				EMFWorkbenchContextFactory.INSTANCE.removeCachedProject(getProject());
				dispose();
			}
		}
	}

	private void processDeferredResources() {
		processDeferredRemovedResources();
		processDeferredUnloadedResources();
		processDeferredLoadResources();
	}

	public boolean visit(IResourceDelta delta) {
		IResource resource = delta.getResource();
		// only respond to project changes
		if (resource != null) {
			if (resource.getType() == IResource.PROJECT) {
				IProject p = (IProject) resource;
				if (isInterrestedInProject(p)) {
					currentProjectDelta = delta;
					return true;
				}
				// added line
				currentProjectDelta = null;
				return false;
			}
			if (resource.getType() == IResource.FILE && isInterrestedInFile((IFile) resource)) {
				switch (delta.getKind()) {
					case IResourceDelta.REMOVED :
						removedResource((IFile) resource);
						break;
					case IResourceDelta.ADDED :
						addedResource((IFile) resource);
						break;
					case IResourceDelta.CHANGED :
						if ((delta.getFlags() & IResourceDelta.CONTENT) != 0)
							changedResource((IFile) resource);
						break;
					default :
						if ((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0 || (delta.getFlags() & IResourceDelta.MOVED_TO) != 0)
							movedResource((IFile) resource);
						break;
				}
				return false;
			}
		}
		return true;
	}

	/**
	 * Queue up the <code>Resource</code> that corresponds to <code>aFile</code>, for removal
	 * from the cache of resources.
	 * 
	 * @post Return true if a <code>Resource</code> was queued up to be removed.
	 */
	protected boolean removedResource(IFile aFile) {
		return processResource(aFile, true);
	}

	/**
	 * Queue up the <code>Resource</code> that corresponds to <code>aFile</code>, for reload.
	 * 
	 * @post Return true if a <code>Resource</code> was queued up to be reloaded.
	 */
	protected boolean addedResource(IFile aFile) {
		//Process resource as a refresh.
		return processResource(aFile, false);
	}

	protected boolean processResource(IFile aFile, boolean isRemove) {
		Resource resource = getResource(aFile);
		if (resource != null) {
			if (resource.isModified()) {
				if (WorkbenchResourceHelper.isReferencedResource(resource)) {
					ReferencedResource refRes = (ReferencedResource) resource;
					if (!refRes.shouldForceRefresh())
						return false; //Do not do anything
				} else
					return false;
			}
			if (isRemove)
				deferredRemoveResources.add(resource);
			else if (resource.isLoaded())
				deferredUnloadResources.add(resource);
			else if (autoloadResourcesURIs.contains(resource.getURI()))
				deferredLoadResources.add(resource.getURI());
		}
		return false;
	}

	/**
	 * For now, do the same as if the <code>aFile</code> was removed.
	 */
	protected boolean movedResource(IFile aFile) {
		return removedResource(aFile);
	}

	/**
	 * The contents of <code>aFile</code> have changed in the Workbench and we may need to update
	 * our cached resources.
	 * 
	 * We will process this resource to be refreshed and not removed.
	 * 
	 * @post Return true if a <code>Resource</code> was actually removed.
	 */

	protected boolean changedResource(IFile aFile) {
		//Process resource as a refresh.
		return processResource(aFile, false);
	}

	protected Resource getResource(IFile aFile) {
		return resourceSet.getResource(URI.createPlatformResourceURI(aFile.getFullPath().toString()), false);
	}


	/**
	 * This method should be called prior to writing to an IFile from a MOF resource.
	 */
	public void preSave(IFile aFile) {
		if (aFile != null) {
			recentlySavedFiles.add(aFile);
			ignoredFilesCache.remove(aFile);
		}
	}

	/**
	 * This method should be called after a preSave if the save fails
	 */
	public void removeFromRecentlySavedList(IFile aFile) {
		if (aFile != null) {
			recentlySavedFiles.remove(aFile);
			ignoredFilesCache.remove(aFile);
		}
	}

	/**
	 * This method should be called just after writing to an IFile from a MOF resource.
	 * 
	 * @deprecated No longer needs to be called.
	 */
	public void postSave(IFile aFile) {
		//TODO remove this method
	}

	/**
	 * Return <code>true</code> if <code>aProject</code> has the projectNatureID.
	 */
	protected boolean isInterrestedInProject(IProject aProject) {
		return aProject.equals(getProject());
	}

	/**
	 * Optimized not to be not interrested in files with an extension of .java or .class or if the
	 * file has just been saved by our own internal mechanism.
	 */
	protected boolean isInterrestedInFile(IFile aFile) {
		String extension = aFile.getFileExtension();
		if (CLASS_EXTENSION.equals(extension) || JAVA_EXTENSION.equals(extension))
			return false;
		if (recentlySavedFiles.remove(aFile)) {
			cacheIgnored(aFile);
			return false;
		}
		return !hasIgnored(aFile);
	}

	/**
	 * Return true if we have already ignored this <code>file</code> and that its modification
	 * stamp is the same as when we processed it.
	 * 
	 * @param file
	 * @return
	 */
	private boolean hasIgnored(IFile file) {
		Long cachedStamp = (Long) ignoredFilesCache.get(file);
		if (cachedStamp == null)
			return false;
		long stamp = WorkbenchResourceHelper.computeModificationStamp(file);
		return cachedStamp.longValue() == stamp;
	}

	/**
	 * Cache the modification stamp of the <code>file</code>.
	 * 
	 * @param file
	 */
	private void cacheIgnored(IFile file) {
		long stamp = WorkbenchResourceHelper.computeModificationStamp(file);
		ignoredFilesCache.put(file, new Long(stamp));
	}

	public void enableAutoload(URI uri) {
		URI normalized = resourceSet.getURIConverter().normalize(uri);
		autoloadResourcesURIs.add(normalized);
	}

	public void disableAutoload(URI uri) {
		URI normalized = resourceSet.getURIConverter().normalize(uri);
		autoloadResourcesURIs.remove(normalized);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.internal.emfworkbench.ResourceSetWorkbenchSynchronizer#initialize()
	 */
	protected void initialize() {
		getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE | IResourceChangeEvent.POST_CHANGE);
	}

}