/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.internal.emfworkbench.validateedit;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jem.util.emf.workbench.WorkbenchResourceHelperBase;
import org.eclipse.wst.common.internal.emf.resource.ReferencedResource;
import org.eclipse.wst.common.internal.emfworkbench.WorkbenchResourceHelper;

public class ResourceStateValidatorImpl implements ResourceStateValidator {
	protected ResourceStateInputProvider provider;
	protected Map lastNonRefreshStateMap;
	protected boolean isCheckingConsistency;

	class RefreshRunnable implements IWorkspaceRunnable {
		CoreException thrownException;
		List files;
		List resources;

		RefreshRunnable(List someFiles, List inconsistentResources) {
			files = someFiles;
			resources = inconsistentResources;
		}

		public CoreException getThrownException() {
			return thrownException;
		}

		public void run(IProgressMonitor aMonitor) {
			try {
				prepareResourcesForRefresh(resources);
				primRefreshFiles(files);
			} catch (CoreException e) {
				thrownException = e;
			}
		}
	}

	/**
	 * Constructor for ResourceStateValidator.
	 */
	public ResourceStateValidatorImpl(ResourceStateInputProvider aProvider) {
		provider = aProvider;
	}

	/**
	 * This method should be called whenever <code>aListener</code> is activated (becomes active).
	 * This will check the timestamps of the underlying files to see if they are different from the
	 * last cached modified value. <code>aListener</code> should be prepared to prompt the user if
	 * they would like to refresh with the contents on disk if we are dirty.
	 */
	public void checkActivation(ResourceStateValidatorPresenter presenter) throws CoreException {
		checkConsistency(presenter);
	}

	public void lostActivation(ResourceStateValidatorPresenter presenter) throws CoreException {
		checkConsistency(presenter);
	}

	public boolean checkSave(ResourceStateValidatorPresenter presenter) throws CoreException {
		if (presenter == null)
			return false;
		if (!provider.isDirty())
			return false;
		List inconsistentResources = getInconsistentResources();
		List inconsistentFiles = getFiles(inconsistentResources);
		inconsistentFiles = addOtherInconsistentFiles(inconsistentFiles);
		if (inconsistentFiles == null || inconsistentFiles.isEmpty())
			return true;
		return presenter.promptForInconsistentFileOverwrite(inconsistentFiles);
	}

	/**
	 * @see ResourceStateValidator#checkReadOnly()
	 */
	public boolean checkReadOnly() {
		boolean result = checkReadOnlyResources();
		if (!result)
			result = checkReadOnlyNonResourceFiles();
		return result;
	}

	/**
	 * Method checkReadOnlyNonResourceFiles.
	 * 
	 * @return boolean
	 */
	private boolean checkReadOnlyNonResourceFiles() {
		List files = provider.getNonResourceFiles();
		if (files == null || files.isEmpty())
			return false;
		int size = files.size();
		IFile file = null;
		for (int i = 0; i < size; i++) {
			file = (IFile) files.get(i);
			if (file.isReadOnly())
				return true;
		}
		return false;
	}

	/**
	 * Method checkReadOnlyResources.
	 * 
	 * @return boolean
	 */
	private boolean checkReadOnlyResources() {
		List resources = provider.getResources();
		if (resources == null || resources.isEmpty())
			return false;
		int size = resources.size();
		Resource res = null;
		IFile file = null;
		for (int i = 0; i < size; i++) {
			res = (Resource) resources.get(i);
			file = WorkbenchResourceHelper.getFile(res);
			if (file != null && file.isReadOnly())
				return true;
		}
		return false;
	}

	protected void checkConsistency(ResourceStateValidatorPresenter presenter) throws CoreException {
		if (isCheckingConsistency || presenter == null)
			return;
		isCheckingConsistency = true;
		try {
			List inconsistentResources = getInconsistentResources();
			List inconsistentFiles = getFiles(inconsistentResources);
			inconsistentFiles = addOtherInconsistentFiles(inconsistentFiles);
			if (inconsistentFiles == null || inconsistentFiles.isEmpty())
				return;
			boolean shouldRefreshFiles = true;
			//Defect 208654 & 209631 want prompt no matter what.
			if (anyFileChangedSinceLastRefreshPrompt(inconsistentFiles)) {
				clearLastNonRefreshStateMap();
				shouldRefreshFiles = presenter.promptForInconsistentFileRefresh(inconsistentFiles);
			} else
				return;
			if (shouldRefreshFiles)
				refreshFiles(inconsistentFiles, inconsistentResources);
			else
				cacheLastNonRefreshFileStamps(inconsistentFiles);
		} finally {
			isCheckingConsistency = false;
		}
	}

	/**
	 * Method cacheLastNonRefreshFileStamps.
	 * 
	 * @param inconsistentFiles
	 */
	private void cacheLastNonRefreshFileStamps(List inconsistentFiles) {
		if (inconsistentFiles != null && !inconsistentFiles.isEmpty()) {
			Map map = getLastNonRefreshStateMap();
			IFile file = null;
			long stamp = 0;
			for (int i = 0; i < inconsistentFiles.size(); i++) {
				file = (IFile) inconsistentFiles.get(i);
				stamp = WorkbenchResourceHelper.computeModificationStamp(file);
				map.put(file, new Long(stamp));
			}
		}
	}

	/**
	 * Method cacheValidateState.
	 * 
	 * @param result
	 */
	private void cacheValidateState(IStatus aStatus, List readOnlyResources, List roNonResourceFiles) {
		if (aStatus.isOK()) {
			if (readOnlyResources != null && !readOnlyResources.isEmpty()) {
				ReferencedResource res = null;
				for (int i = 0; i < readOnlyResources.size(); i++) {
					res = (ReferencedResource) readOnlyResources.get(i);
					WorkbenchResourceHelper.setSynhronizationStamp(res, computeModificationStamp(res));
				}
			}
			provider.cacheNonResourceValidateState(roNonResourceFiles);
		}
	}

	private void clearLastNonRefreshStateMap() {
		if (lastNonRefreshStateMap != null)
			lastNonRefreshStateMap.clear();
	}

	/**
	 * Method anyFileChangedSinceLastRefreshPrompt.
	 * 
	 * @param inconsistentFiles
	 * @return boolean
	 */
	private boolean anyFileChangedSinceLastRefreshPrompt(List inconsistentFiles) {
		if (inconsistentFiles == null || inconsistentFiles.isEmpty())
			return false;
		if (lastNonRefreshStateMap == null || lastNonRefreshStateMap.isEmpty())
			return true;
		int size = inconsistentFiles.size();
		IFile file = null;
		Long stamp = null;
		for (int i = 0; i < size; i++) {
			file = (IFile) inconsistentFiles.get(i);
			stamp = (Long) getLastNonRefreshStateMap().get(file);
			if (stamp == null || (stamp.longValue() != WorkbenchResourceHelper.computeModificationStamp(file)))
				return true;
		}
		return false;
	}

	protected List addOtherInconsistentFiles(List inconsistentFiles) {
		if (inconsistentFiles == null || inconsistentFiles.isEmpty())
			return getNonResourceInconsistentFiles();
		List nonResFiles = getNonResourceInconsistentFiles();
		if (nonResFiles != null)
			inconsistentFiles.addAll(nonResFiles);
		return inconsistentFiles;
	}

	/**
	 * Method getNonResourceInconsistentFiles.
	 * 
	 * @return List
	 */
	private List getNonResourceInconsistentFiles() {
		List files = provider.getNonResourceInconsistentFiles();
		if (files != null && !files.isEmpty())
			return files;
		//Determine consistency based on the synchronization of the IFile
		files = provider.getNonResourceFiles();
		if (files == null || files.isEmpty())
			return Collections.EMPTY_LIST;
		List inconsistent = null;
		int size = files.size();
		IFile file = null;
		for (int i = 0; i < size; i++) {
			file = (IFile) files.get(i);
			if (file.isAccessible() && !file.isSynchronized(IResource.DEPTH_ZERO)) {
				if (inconsistent == null)
					inconsistent = new ArrayList();
				inconsistent.add(file);
			}
		}
		if (inconsistent == null)
			inconsistent = Collections.EMPTY_LIST;
		return inconsistent;
	}

	protected List getInconsistentResources() {
		List mofResources = provider.getResources();
		List inconsistent = null;
		int size = mofResources.size();
		Resource res = null;
		ReferencedResource refRes = null;
		for (int i = 0; i < size; i++) {
			res = (Resource) mofResources.get(i);
			if (WorkbenchResourceHelper.isReferencedResource(res)) {
				refRes = (ReferencedResource) res;
				if (!WorkbenchResourceHelper.isConsistent(refRes)) {
					if (inconsistent == null)
						inconsistent = new ArrayList();
					inconsistent.add(refRes);
				}
			}
		}
		if (inconsistent == null)
			inconsistent = Collections.EMPTY_LIST;
		return inconsistent;
	}

	protected List getFiles(List refResources) {
		List files = new ArrayList(refResources.size());
		IFile file = null;
		ReferencedResource refRes = null;
		for (int i = 0; i < refResources.size(); i++) {
			refRes = (ReferencedResource) refResources.get(i);
			file = WorkbenchResourceHelper.getFile(refRes);
			if (file != null)
				files.add(file);
		}
		return files;
	}

	/**
	 * This method should be called at least the first time a ResourceStateValidatorPresenter
	 * becomes active and is about to edit its contents. The returned IStatus may have an ERROR
	 * status which should be presented to the user.
	 */
	public IStatus validateState(ResourceStateValidatorPresenter presenter) throws CoreException {
		List roResources, nonResROFiles, roFiles = null;
		List[] readOnly = selectReadOnlyResources(provider.getResources());
		roResources = readOnly[0];
		roFiles = readOnly[1];
		nonResROFiles = selectReadOnlyFiles(provider.getNonResourceFiles());
		if (nonResROFiles != null) {
			if (roFiles == null)
				roFiles = nonResROFiles;
			else
				roFiles.addAll(nonResROFiles);
		}
		if (roFiles == null || roFiles.isEmpty())
			return OK_STATUS;
		IFile[] files = new IFile[roFiles.size()];
		roFiles.toArray(files);
		Object ctx = presenter != null ? presenter.getValidateEditContext() : null;
		IStatus result = ResourcesPlugin.getWorkspace().validateEdit(files, ctx);
		cacheValidateState(result, roResources, nonResROFiles);
		if (!result.isOK())
			checkConsistency(presenter);
		return result;
	}

	/**
	 * Method selectReadOnlyFiles.
	 * 
	 * @param list
	 * @param roFiles
	 */
	private List selectReadOnlyFiles(List files) {
		if (files == null || files.isEmpty())
			return files;
		int size = files.size();
		List readOnly = null;
		IFile file = null;
		for (int i = 0; i < size; i++) {
			file = (IFile) files.get(i);
			if (file.isReadOnly()) {
				if (readOnly == null)
					readOnly = new ArrayList(size);
				readOnly.add(file);
			}
		}
		return readOnly;
	}

	/**
	 * Method selectReadOnlyResources.
	 * 
	 * @param list
	 * @param roFiles
	 * @return List
	 */
	private List[] selectReadOnlyResources(List resources) {
		if (resources == null || resources.isEmpty())
			return new List[]{resources, null};
		IFile file = null;
		int size = resources.size();
		Resource res = null;
		List readOnly = null;
		List roFiles = null;
		for (int i = 0; i < size; i++) {
			res = (Resource) resources.get(i);
			file = WorkbenchResourceHelper.getFile(res);
			if (file != null && file.isReadOnly()) {
				if (readOnly == null)
					readOnly = new ArrayList(size);
				readOnly.add(res);
				if (roFiles == null)
					roFiles = new ArrayList(size);
				roFiles.add(file);
			}
		}
		return new List[]{readOnly, roFiles};
	}

	protected long computeModificationStamp(ReferencedResource resource) {
		return WorkbenchResourceHelper.computeModificationStamp(resource);
	}


	protected void refreshFiles(List someFiles, List inconsitentResources) throws CoreException {
		RefreshRunnable runnable = new RefreshRunnable(someFiles, inconsitentResources);
		ResourcesPlugin.getWorkspace().run(runnable, null);
		if (runnable.getThrownException() != null)
			throw runnable.getThrownException();
	}

	protected void primRefreshFiles(List someFiles) throws CoreException {
		int size = someFiles.size();
		IFile file = null;
		for (int i = 0; i < size; i++) {
			file = (IFile) someFiles.get(i);
			if (!file.isSynchronized(IResource.DEPTH_ZERO))
				file.refreshLocal(IResource.DEPTH_ONE, null);
			else
				refreshResource(file);
		}
	}

	/**
	 * We need to remove the Resource that corresponds to the <code>file</code> to force a
	 * refresh.
	 */
	protected void refreshResource(IFile file) {
		Resource res = WorkbenchResourceHelperBase.getResource(file);
		if (res != null)
			res.unload();
	}

	/**
	 * Force the resources to not be dirty to ensure that they will be removed from their
	 * ResourceSet when their file is refreshed.
	 */
	protected void prepareResourcesForRefresh(List refResources) {
		ReferencedResource res = null;
		for (int i = 0; i < refResources.size(); i++) {
			res = (ReferencedResource) refResources.get(i);
			res.setForceRefresh(true);
		}
	}

	/**
	 * Gets the lastNonRefreshStateMap.
	 * 
	 * @return Returns a Map
	 */
	protected Map getLastNonRefreshStateMap() {
		if (lastNonRefreshStateMap == null)
			lastNonRefreshStateMap = new HashMap();
		return lastNonRefreshStateMap;
	}
}