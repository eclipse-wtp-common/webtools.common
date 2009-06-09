/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jem.internal.util.emf.workbench.EMFWorkbenchContextFactory;
import org.eclipse.jem.util.emf.workbench.ProjectResourceSet;
import org.eclipse.jem.util.emf.workbench.ResourceSetWorkbenchSynchronizer;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.jem.util.plugin.JEMUtilPlugin;
import org.eclipse.wst.common.internal.emf.resource.ReferencedResource;
import org.eclipse.wst.common.internal.emfworkbench.WorkbenchResourceHelper;

/**
 * @author schacher
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ResourceSetWorkbenchEditSynchronizer extends ResourceSetWorkbenchSynchronizer implements IResourceDeltaVisitor {
	private static final String CLASS_EXTENSION = "class"; //$NON-NLS-1$
	private static final String JAVA_EXTENSION = "java"; //$NON-NLS-1$
	private static final String JAVA_ARCHIVE = "jar"; //$NON-NLS-1$
	private Set recentlySavedFiles = new HashSet();
	private Map ignoredFilesCache = new HashMap();
	private class SavedFileKey {
		private WeakReference res;
		private IFile savedFile;
		public SavedFileKey(Resource res, IFile savedFile) {
			super();
			this.res = new WeakReference(res);
			this.savedFile = savedFile;
		}
		public Resource getRes() {
			return this.res == null ? null : (Resource)res.get();
		}
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((getRes() == null) ? 0 : getRes().hashCode());
			result = prime * result + ((savedFile == null) ? 0 : savedFile.hashCode());
			return result;
		}
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SavedFileKey other = (SavedFileKey) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (getRes() == null) {
				if (other.getRes() != null)
					return false;
			} else if (!getRes().equals(other.getRes()))
				return false;
			if (savedFile == null) {
				if (other.savedFile != null)
					return false;
			} else if (!savedFile.equals(other.savedFile))
				return false;
			return true;
		}
		private ResourceSetWorkbenchEditSynchronizer getOuterType() {
			return ResourceSetWorkbenchEditSynchronizer.this;
		}
	}

	/** The emf resources to be removed from the resource set as a result of a delta */
	protected List deferredRemoveResources = new ArrayList();
	protected List deferredUnloadResources = new ArrayList();
	protected List deferredLoadResources = new ArrayList();

	protected List autoloadResourcesURIs = new ArrayList();
	protected List autoloadResourcesExts = new ArrayList();


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

	private ILock lock;
	private static final long delay = 30;
	
    private ILock getLock() {
        if (lock == null)
            lock = Platform.getJobManager().newLock();
        return lock;
    }
    
    private void releaseLock() {
        getLock().release();
    }
    private boolean aquireLock() throws InterruptedException{
    	return getLock().acquire(delay);
    }
    
	protected void acceptDelta(final IResourceChangeEvent event) {

		boolean hasLocked = false;
		try {
			hasLocked = aquireLock();
		} catch (InterruptedException e) {
			Logger.getLogger().write(e);
		}		
		
		try{
			final IResourceDelta delta = event.getDelta();
	
			if (ResourcesPlugin.getWorkspace().isTreeLocked()) {
				primAcceptDelta(delta, event);
			}
			else {
				IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
					public void run(IProgressMonitor monitor) throws CoreException {
						primAcceptDelta(delta, event);
					}
				};
				try {
					ResourcesPlugin.getWorkspace().run(runnable, project, IWorkspace.AVOID_UPDATE, null);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}finally{
			if( hasLocked )
				releaseLock();
		}
	}

	private void primAcceptDelta(IResourceDelta delta, IResourceChangeEvent event) {
		if (delta != null) {
			try {
				currentProjectDelta = null;
				delta.accept(ResourceSetWorkbenchEditSynchronizer.this);
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
		if (JEMUtilPlugin.isActivated()) {
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
		boolean didProcess = false;
		List resources = getResources(aFile);
		for (Iterator iterator = resources.iterator(); iterator.hasNext();) {
			Resource resource = (Resource) iterator.next();

			if (resource != null) {
				if (recentlySavedFilesContains(resource)) {

					/*
					 * The IFile was just added to the workspace but we have a
					 * changed resource in memory. Need to decide if it should
					 * be unloaded.
					 */
					if (resource.isModified()) {
						if (WorkbenchResourceHelper.isReferencedResource(resource)) {
							ReferencedResource refRes = (ReferencedResource) resource;
							if (refRes.shouldForceRefresh()) {
								deferredUnloadResources.add(resource);
								didProcess = true;
							}
						}
					}
				} else {
					/* Unload if found and is not modified but inconsistent. */
					if (resource.isLoaded()) {
						if (WorkbenchResourceHelper.isReferencedResource(resource)) {
							if (!WorkbenchResourceHelper.isConsistent((ReferencedResource) resource)) {
								deferredUnloadResources.add(resource);
								didProcess = true;
							}
						} else {
							deferredUnloadResources.add(resource);
							didProcess = true;
						}
					} else {
						/* Process autoload resources on added file */
						URI uri = URI.createPlatformResourceURI(aFile.getFullPath().toString());
						if ((autoloadResourcesURIs.contains(uri)) || (autoloadResourcesExts.contains(aFile.getFileExtension()))) {
							deferredLoadResources.add(uri);
							didProcess = true;
						}
					}
				}
			}
		}
		return didProcess;
	}

	private synchronized boolean recentlySavedFilesContains(Resource resource) {
		for (Iterator iterator = recentlySavedFiles.iterator(); iterator.hasNext();) {
			SavedFileKey key = (SavedFileKey) iterator.next();
			if (key.getRes() != null && (key.getRes().getURI().equals(resource.getURI()) && key.getRes().getClass().equals(resource.getClass())))
				return true;
			}
		return false;
	}

	protected boolean processResource(IFile aFile, boolean isRemove) {
		List resources = getResources(aFile);
        for (Iterator iterator = resources.iterator(); iterator.hasNext();) {
			Resource resource = (Resource) iterator.next();
			if ((resource != null)) {
				if (recentlySavedFilesContains(resource)) {
					if (resource.isModified()) {
						if (WorkbenchResourceHelper.isReferencedResource(resource)) {
							ReferencedResource refRes = (ReferencedResource) resource;
							if (!refRes.shouldForceRefresh())
								continue; // Do not do anything
						} else
							continue;
					}
				}
				if (isRemove)
					deferredRemoveResources.add(resource);
				else if (resource.isLoaded()) {
					if (WorkbenchResourceHelper.isReferencedResource(resource)) {
						if (!WorkbenchResourceHelper.isConsistent((ReferencedResource) resource))
							deferredUnloadResources.add(resource);
					} else
						deferredUnloadResources.add(resource);
				}
			}
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
	
	protected List getResources(IFile aFile) {

		List resources = new ArrayList();
		List allResources = resourceSet.getResources();
		for (Iterator iterator = allResources.iterator(); iterator.hasNext();) {
			Resource res = (Resource) iterator.next();
			URI resURI = res.getURI();
			String resURIString = "";
			if (resURI.path() != null) {
				IPath resURIPath;
				if (WorkbenchResourceHelper.isPlatformResourceURI(resURI))
					resURIPath = new Path(URI.decode(resURI.path())).removeFirstSegments(2);
				else
					resURIPath = new Path(URI.decode(resURI.path())).removeFirstSegments(1);
				resURIString = resURIPath.toString();
			}
			if (!resURIString.equals("") && aFile.getFullPath().toString().indexOf(resURIString) != -1)
				resources.add(res);
		}
		return resources;
	}


	/**
	 * This method should be called prior to writing to an IFile from a MOF resource.
	 */
	public void preSave(IFile aFile) {
		if (aFile != null) {
			recentlySavedFilesAdd(aFile,null);
			ignoredFilesCache.remove(aFile);
		}
	}
	
	/**
	 * This method should be called prior to writing to an IFile from a MOF resource.
	 */
	public void preSave(IFile aFile, Resource res) {
		if (aFile != null) {
			recentlySavedFilesAdd(aFile, res);
			ignoredFilesCache.remove(aFile);
		}
	}

	private synchronized boolean recentlySavedFilesAdd(IFile file, Resource res) {
		return recentlySavedFiles.add(new SavedFileKey(res, file));
	}

	/**
	 * This method should be called after a preSave if the save fails
	 */
	public void removeFromRecentlySavedList(IFile aFile) {
		if (aFile != null) {
			recentlySavedFilesForceRemove(aFile);
			ignoredFilesCache.remove(aFile);
		}
	}

	private synchronized boolean recentlySavedFilesRemove(IFile file) {
		
		boolean removedFromList = false;
		for (Iterator iterator = recentlySavedFiles.iterator(); iterator.hasNext();) {
			SavedFileKey key = (SavedFileKey) iterator.next();
			if (key.savedFile != null && key.savedFile.equals(file)) {
				List resources = getResources(file);
				if (key.getRes() == null || resources.contains(key.getRes()) ) {
					iterator.remove();
					removedFromList = true;
				}
			}
		}
		return removedFromList;
	}
	private synchronized boolean recentlySavedFilesForceRemove(IFile file) {
		
		boolean removedFromList = false;
		for (Iterator iterator = recentlySavedFiles.iterator(); iterator.hasNext();) {
			SavedFileKey key = (SavedFileKey) iterator.next();
			if (key.savedFile != null && key.savedFile.equals(file)) {
					iterator.remove();
					removedFromList = true;
			}
		}
		return removedFromList;
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
		if (CLASS_EXTENSION.equals(extension) || JAVA_EXTENSION.equals(extension) || JAVA_ARCHIVE.equals(extension))
			return false;
		if (recentlySavedFilesRemove(aFile)) {
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
	public void enableAutoload(String extension) {
		autoloadResourcesExts.add(extension);
		
	}
	public void disableAutoload(String extension) {
		autoloadResourcesExts.remove(extension);
	}

	public void dispose() {
		super.dispose();
		currentProjectDelta = null;
		extenders = null;
	}

}
