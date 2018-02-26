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
 * Created on Mar 3, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.eclipse.wst.common.internal.emfworkbench;

import java.io.OutputStream;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jem.util.emf.workbench.WorkbenchResourceHelperBase;
import org.eclipse.jem.util.emf.workbench.WorkbenchURIConverter;
import org.eclipse.jem.util.plugin.JEMUtilPlugin;
import org.eclipse.wst.common.internal.emf.resource.ReferencedResource;
import org.eclipse.wst.common.internal.emf.resource.ReferencedXMIFactoryImpl;
import org.eclipse.wst.common.internal.emf.utilities.ExtendedEcoreUtil;
import org.eclipse.wst.common.internal.emfworkbench.integration.EMFWorkbenchEditPlugin;

/**
 * @author schacher
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WorkbenchResourceHelper extends WorkbenchResourceHelperBase {
	protected static Class REFERENCED_RES_CLASS = ReferencedResource.class;
	private static boolean fileAdapterFactoryInitialized = false;

	private static class FileAdapterFactory extends AdapterFactoryImpl {

		@Override
		public Adapter adaptNew(Notifier target, Object type) {
			FileAdapter adapter = new FileAdapter();
			adapter.setTarget(target);
			return adapter;
		}

	}

	/**
	 * This class is internal and is used to store state on the resource, specifically, a cached
	 * reference to the IFile
	 */
	private static class FileAdapter extends AdapterImpl {
		public static final Object ADAPTER_KEY = FileAdapter.class.getName();
		private static final long delay = 30;
		private IFile file;
		private long synchronizationStamp;
		protected ResourceSet previousResourceSet;
		private ILock saveLock;
		public static final int FILE_NOT_LOADED = 0;
		public static final int FILE_INACCESSIBLE = -1;

		@Override
		public boolean isAdapterForType(Object type) {
			return ADAPTER_KEY.equals(type);
		}

		/*
		 * Update the synchronization stamp where appropriate
		 */
		@Override
		public void notifyChanged(Notification msg) {
			switch (msg.getFeatureID(null)) {
				case Resource.RESOURCE__IS_LOADED :
					if (getResource().isLoaded())
						handleLoaded();
					else
						handleUnloaded();
					break;
				case ReferencedResource.RESOURCE_ABOUT_TO_SAVE:
	                handleAboutToSave();
	                break;
	            case ReferencedResource.RESOURCE_WAS_SAVED:
	                handleSaved();
	                break;
	            case ReferencedResource.RESOURCE_SAVE_FAILED:
	                handleSaveFailed();
	                break;
				case Resource.RESOURCE__URI :
					handleURIChanged();
			}
		}
		
		private void handleSaveFailed() {
            releaseSaveLock();

        }

        private void handleAboutToSave() {
            aquireSaveLock();
        }

        private void aquireSaveLock() {
//            System.out.println("FileName: " + getFile().getName() + " " +getFile());
//            System.out.println("aquiredSaveLock: " + Thread.currentThread().getName());
//            System.out.println("Depth" + getSaveLock().getDepth());
//            System.out.println("Instance:"+getSaveLock().hashCode());
//            new Exception().printStackTrace(System.out);
            getSaveLock().acquire();

        }

        private boolean aquireSaveLock(long delay) throws InterruptedException {
//            System.out.println("FileName: " + getFile().getName()  + " " +getFile());
//            System.out.println("aquiredSaveLock with delay: " + Thread.currentThread().getName());
//            System.out.println("Depth" + getSaveLock().getDepth());
//            System.out.println("Instance:"+getSaveLock().hashCode());
//            new Exception().printStackTrace(System.out);
            
            return getSaveLock().acquire(delay);

        }

        private void releaseSaveLock() {
//            System.out.println("FileName: " + getFile().getName()  + " " +getFile());
//            System.out.println("releasedSaveLock: " + Thread.currentThread().getName());
//            System.out.println("Depth" + getSaveLock().getDepth());
//            System.out.println("Instance:"+getSaveLock().hashCode());
//            new Exception().printStackTrace(System.out);
            getSaveLock().release();

        }

        private ILock getSaveLock() {
            if (saveLock == null)
                saveLock = Job.getJobManager().newLock();
            return saveLock;
        }

		/**
		 *  
		 */
		private void handleURIChanged() {
			file = null;
			synchronizationStamp = FILE_NOT_LOADED;
		}

		public IFile getFile() {
			//First test to see if we should reset the file.
			if (file != null && (!file.isAccessible() || previousResourceSet != getResourceSet())) {
				file = null;
				synchronizationStamp = FILE_NOT_LOADED;
			}
			if (file == null) {
				if (isPlatformResourceURI(getURI())) {
					file = getPlatformFile(getURI());
				} else {
					//we should not be here anymore.
					file = internalGetFile(getResource());
				}
				if(null!= file && !file.isAccessible()){
					synchronizationStamp = FILE_INACCESSIBLE;
				}
				previousResourceSet = getResourceSet();
			}
			return file;
		}

		/**
		 * @return
		 */
		public long getSynchronizationStamp() {
			return synchronizationStamp;
		}

		/**
		 * @param l
		 */
		public void setSynchronizationStamp(long l) {
			synchronizationStamp = l;
		}

		/**
		 * @see ReferencedResource#isConsistent()
		 */
		public boolean isConsistent() {
			//This checks for the case where the resource hasn't finished saving fo the first time
			if(!getResource().isLoaded())
				return true;
            boolean hasLocked = false;
            try {
                hasLocked = aquireSaveLock(delay);
            } catch (InterruptedException e) {
            	EMFWorkbenchEditPlugin.logError(e);
            }
            boolean result = false;
            try {

                if (getFile() == null || !getFile().isAccessible())
                    result = true;
                else {
                    if (!getFile().isSynchronized(IFile.DEPTH_ZERO))
                        result = false;
                    else {
                        result = synchronizationStamp == computeModificationStamp(getFile());
                    }
                }
            } catch (Exception e) {
            	EMFWorkbenchEditPlugin.logError(e);
            } finally {
                if (hasLocked)
                    releaseSaveLock();
            }
            return result;
        }

		public void cacheSynchronizationStamp() {
			setSynchronizationStamp(computeModificationStamp(getFile()));
		}

		public ReferencedResource getResource() {
			return (ReferencedResource) target;
		}

		public URI getURI() {
			return target == null ? null : getResource().getURI();
		}

		public ResourceSet getResourceSet() {
			return target == null ? null : getResource().getResourceSet();
		}

		public void handleUnloaded() {
			file = null;
			synchronizationStamp = FILE_NOT_LOADED;
		}

		public void handleLoaded() {
			cacheSynchronizationStamp();
		}

		public void handleSaved() {
			cacheSynchronizationStamp();
			releaseSaveLock();
		}
	}

	/**
	 * This is an internal method to be used by the plugin only
	 */
	public static synchronized void initializeFileAdapterFactory() {
		if (!fileAdapterFactoryInitialized) {
			ReferencedXMIFactoryImpl.addGlobalAdapterFactory(new FileAdapterFactory());
			fileAdapterFactoryInitialized = true;
		}
	}


	private static FileAdapter getFileAdapter(ReferencedResource res) {
		FileAdapter adapter = (FileAdapter) EcoreUtil.getExistingAdapter(res, FileAdapter.ADAPTER_KEY);
		return adapter == null ? createFileAdapter(res) : adapter;
	}

	private static FileAdapter createFileAdapter(ReferencedResource res) {
		FileAdapter adapter = new FileAdapter();
		adapter.setTarget(res);
		res.eAdapters().add(adapter);
		return adapter;
	}

	/**
	 * Return the underlying IFile for the resource if one exists. This may return null if the
	 * resource does not belong to a ProjectResourceSet.
	 */
	public static IFile getFile(ReferencedResource res) {
		FileAdapter adapter = getFileAdapter(res);
		return adapter == null ? null : adapter.getFile();
	}

	public static long getSynchronizationStamp(ReferencedResource res) {
		FileAdapter adapter = getFileAdapter(res);
		return adapter == null ? FileAdapter.FILE_NOT_LOADED : adapter.getSynchronizationStamp();
	}

	public static void setSynhronizationStamp(ReferencedResource res, long stamp) {
		FileAdapter adapter = getFileAdapter(res);
		if (adapter != null)
			adapter.setSynchronizationStamp(stamp);
	}

	public static boolean isConsistent(ReferencedResource res) {
		FileAdapter adapter = getFileAdapter(res);
		return adapter != null && adapter.isConsistent();
	}

	/**
	 * Method cacheSynchronizationStamp.
	 * 
	 * @param r
	 */
	public static void cacheSynchronizationStamp(ReferencedResource refResource) {
		if (refResource != null) {
			FileAdapter adapter = getFileAdapter(refResource);
			if (adapter != null && adapter.getSynchronizationStamp() <= FileAdapter.FILE_NOT_LOADED)
				adapter.setSynchronizationStamp(computeModificationStamp(refResource));
		}
	}

	public static boolean isReferencedResource(Resource aResource) {
		return REFERENCED_RES_CLASS.isInstance(aResource);
	}

	public static long computeModificationStamp(ReferencedResource resource) {
		FileAdapter adapter = getFileAdapter(resource);
		return adapter == null ? FileAdapter.FILE_NOT_LOADED : computeModificationStamp(adapter.getFile());
	}

	public static long computeModificationStamp(IFile file) {
		if (file == null)
			return FileAdapter.FILE_NOT_LOADED;
		if(!file.isAccessible()){
			return FileAdapter.FILE_INACCESSIBLE;
		}
		long currentStamp = file.getModificationStamp();
		IPath path = file.getLocation();
		if (path != null)
			return path.toFile().lastModified();
		return currentStamp;
	}

	/**
	 * Return the IFile that currently corresponds to <code>aResource</code>.
	 */
	public static IFile getFile(Resource aResource) {
		if (aResource != null) {
			if (isReferencedResource(aResource))
				return getFile((ReferencedResource) aResource);
			return internalGetFile(aResource);
		}
		return null;
	}

	public static IFile getFile(EObject obj) {
		if (obj == null)
			return null;

		Resource mofResource = obj.eResource();
		if (mofResource == null)
			return null;
		return getFile(mofResource);
	}

	/**
	 * Get or load a cached Resource or create one if it is not found. A WrappedException will only
	 * be thrown if the corresponding file exists but it failed to load.
	 */
	public static Resource getOrCreateResource(URI uri, ResourceSet set) throws WrappedException {
		try {
			return set.getResource(uri, true); //this will create the resource no matter what
		} catch (WrappedException e) {
			if (ExtendedEcoreUtil.getFileNotFoundDetector().isFileNotFound(e))
				return set.getResource(uri, false);
			throw e;
		}
	}

	protected static boolean isSameProject(Resource resourceA, Resource resourceB) {
		IProject pA, pB;
		pA = getProject(resourceA);
		pB = getProject(resourceB);
		if (pA != null && pB != null)
			return pA.equals(pB);
		//otherwise we do not have enough info to determine false so we must return true
		return true;
	}

	public static IProject getProject(Resource res) {
		IProject proj = getProject(res.getResourceSet());
		if (proj == null) {
			IFile file = getFile(res);
			if (file != null)
				proj = file.getProject();
		}
		return proj;
	}

	/*
	 * This method should not be called by clients. It is used internally by clients that also call
	 * getFile(...). This is to avoid endless loops.
	 * 
	 * @see getFile(Resource)
	 */
	protected static IFile internalGetFile(Resource aResource) {
		if (aResource != null)
			return getFile(aResource.getResourceSet(), aResource.getURI());

		return null;
	}

	protected static IFile getFile(ResourceSet set, URI uri) {
		IFile file = getPlatformFile(uri);
		if (file == null) {
			if (set != null) {
				URIConverter converter = set.getURIConverter();
				URI convertedUri = converter.normalize(uri);
				if (!uri.equals(convertedUri))
					return getPlatformFile(convertedUri);
			}
		}
		return file;
	}

	/**
	 * Return the IFile for the <code>uri</code> within the Workspace. This URI is assumed to be
	 * absolute in the following format: platform:/resource/....
	 */
	public static IFile getPlatformFile(URI uri) {
		if (isPlatformResourceURI(uri)) {
			String fileString = URI.decode(uri.path());
			fileString = fileString.substring(JEMUtilPlugin.PLATFORM_RESOURCE.length() + 1);
			return getWorkspace().getRoot().getFile(new Path(fileString));
		}
		return null;
	}



	public static IFile getFile(IProject project, URI uri) {
		ResourceSet set = getResourceSet(project);
		return getFile(set, uri);
	}

	/**
	 * This should only be used if you want to save <code>aResource</code> within the IProject
	 * that it is currently residing but you do not want to save it in the default output location.
	 * You should not use this api to save a Resource to an existing file.
	 * 
	 * @deprecated This api is no longer required. You should create a resource with the absolute
	 *             path (platform:/resource/...). Upon save, the file will be saved to this
	 *             location.
	 */
	public static boolean saveResourceToFile(Resource aResource, IFile aFile) throws Exception {
		return saveResourceToFile(aResource, aFile, null);
	}

	/**
	 * This should only be used if you want to save <code>aResource</code> within the IProject
	 * that it is currently residing but you do not want to save it in the default output location.
	 * You should not use this api to save a Resource to an existing file.
	 * 
	 * @deprecated This api is no longer required. You should create a resource with the absolute
	 *             path (platform:/resource/...). Upon save, the file will be saved to this
	 *             location.
	 */
	public static boolean saveResourceToFile(Resource aResource, IFile aFile, Map saveOptions) throws Exception {
		if (aResource != null && aFile != null && !aFile.exists()) {
			ResourceSet set = aResource.getResourceSet();
			if (set != null) {
				URIConverter conv = set.getURIConverter();
				if (conv != null && conv instanceof WorkbenchURIConverter) {
					WorkbenchURIConverter wbConv = (WorkbenchURIConverter) conv;
					String uri = aResource.getURI().toString();
					IPath resPath, filePath;
					resPath = new Path(uri);
					filePath = aFile.getProjectRelativePath();
					int resCount, fileCount;
					resCount = resPath.segmentCount();
					fileCount = filePath.segmentCount();
					if (resCount <= fileCount) {
						filePath = filePath.removeFirstSegments(fileCount - resCount);
						if (resPath.equals(filePath)) {
							OutputStream os = wbConv.createOutputStream(URI.createPlatformResourceURI(aFile.toString()));
							if (os != null) {
								try {
									aResource.save(os, saveOptions);
								} finally {
									os.close();
								}
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	protected static void deleteFile(IFile aFile) throws CoreException {
		if (aFile != null && aFile.exists())
			aFile.delete(true, null);
	}

	/**
	 * Delete
	 * 
	 * @aResource in the Workbench.
	 */
	public static void deleteResource(Resource aResource) throws CoreException {
		if (aResource != null)
			deleteFile(getFile(aResource));
	}



}
