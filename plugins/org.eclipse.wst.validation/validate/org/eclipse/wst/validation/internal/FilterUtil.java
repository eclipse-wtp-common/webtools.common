/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.wst.validation.core.IFileDelta;
import org.eclipse.wst.validation.internal.operations.IWorkbenchHelper;
import org.eclipse.wst.validation.internal.operations.WorkbenchFileDelta;
import org.eclipse.wst.validation.plugin.ValidationPlugin;

import com.ibm.wtp.common.logger.LogEntry;
import com.ibm.wtp.common.logger.proxy.Logger;

/**
 * Utility class for the ValidationOperation hierarchy.
 */
public final class FilterUtil {
	private static VMDDeltaVisitor _deltaVisitor = null;
	private static VMDResourceVisitor _resourceVisitor = null;

	private interface VMDRecorder {
		public Map getResult();

		public void setEnabledValidators(Set enabledValidators);

		public void setProgressMonitor(IProgressMonitor monitor);

		public IProgressMonitor getProgressMonitor();
	}

	private interface VMDDeltaVisitor extends VMDRecorder, IResourceDeltaVisitor {
		//constructor
	}

	private interface VMDResourceVisitor extends VMDRecorder, IResourceVisitor {
		//constructor
	}

	/**
	 * Don't let anyone create an instance of this class.
	 */
	private FilterUtil() {
		//default
	}

	/**
	 * Given a Set of enabled ValidatorMetaData, create a Map with each ValidatorMetaData as a key
	 * with an associated null value.
	 */
	static Map wrapInMap(Set enabledValidators) {
		Map result = new HashMap();
		if ((enabledValidators == null) || (enabledValidators.size() == 0)) {
			return result;
		}

		Iterator iterator = enabledValidators.iterator();
		while (iterator.hasNext()) {
			result.put(iterator.next(), null);
		}
		return result;
	}

	static void checkCanceled(IProgressMonitor monitor) throws OperationCanceledException {
		if (monitor == null) {
			return;
		} else if (monitor.isCanceled()) {
			throw new OperationCanceledException(""); //$NON-NLS-1$
		}
	}

	/**
	 * Given a Map of VMD <=>Set(IFileDelta), either return the existing Set or create a Set and
	 * return it.
	 */
	private static Set getResourceDeltas(Map enabledValidators, ValidatorMetaData vmd) {
		Set fileDeltas = (Set) enabledValidators.get(vmd);
		if (fileDeltas == null) {
			fileDeltas = new HashSet();
			enabledValidators.put(vmd, fileDeltas);
		}
		return fileDeltas;
	}

	/**
	 * Given the IFileDelta type, return the corresponding IResourceDelta type.
	 */
	private static int getResourceDeltaType(int ifileDeltaType) {
		switch (ifileDeltaType) {
			case (IFileDelta.ADDED) : {
				return IResourceDelta.ADDED;
			}

			case (IFileDelta.DELETED) : {
				return IResourceDelta.REMOVED;
			}

			case (IFileDelta.CHANGED) :
			default : {
				return IResourceDelta.CHANGED;
			}
		}
	}

	/**
	 * Given the IResourceDelta type, return the corresponding IFileDelta type.
	 */
	static int getFileDeltaType(int iresourceDeltaType) {
		switch (iresourceDeltaType) {
			case IResourceDelta.ADDED : // resource has been added to the workbench
			{
				return IFileDelta.ADDED;
			}

			case IResourceDelta.CHANGED : // resources has been changed in the workbench
			{
				return IFileDelta.CHANGED;
			}

			case IResourceDelta.REMOVED : // resource has been deleted from the workbench
			{
				return IFileDelta.DELETED;
			}

			case IResourceDelta.ADDED_PHANTOM : // incoming workbench resource
			{
				return IFileDelta.ADDED;
			}

			case IResourceDelta.REMOVED_PHANTOM : // outgoing workbench resource
			{
				return IFileDelta.DELETED;
			}

			default : {
				return IFileDelta.CHANGED;
			}
		}
	}


	/**
	 * Return the validators which are both configured on this type of project, (as stored in
	 * getProject()), and enabled by the user on this project.
	 */
	static void addFileDelta(Map enabledValidators, ValidatorMetaData vmd, WorkbenchFileDelta fileDelta) {
		Set fileDeltas = getResourceDeltas(enabledValidators, vmd);
		fileDeltas.add(fileDelta);
		enabledValidators.put(vmd, fileDeltas);
	}

	/**
	 * Return a Map wrapper, with each VMD from enabledValidators as the key, and the value a Set of
	 * IFileDelta wrapper around the changed Object[].
	 * 
	 * If filterIn is true, do not check if the resources are filtered in by the validator. If
	 * filterIn is false, check if the resources are filtered in by the validator (recommended).
	 */
	public static Map getFileDeltas(Set enabledValidators, Object[] changedResources, boolean filterIn) {
		return getFileDeltas(enabledValidators, changedResources, IFileDelta.CHANGED, filterIn); // by
		// default,
		// assume
		// that
		// the
		// resources
		// have
		// changed,
		// not
		// added
		// or
		// deleted
	}

	/**
	 * Return a Map wrapper, with each VMD from enabledValidators as the key, and the value a Set of
	 * IFileDelta wrapper around the changed Object[], with each delta of type deltaType.
	 */
	public static Map getFileDeltas(Set enabledValidators, Object[] changedResources, int ifileDeltaType) {
		return getFileDeltas(enabledValidators, changedResources, ifileDeltaType, false); // by
		// default,
		// check
		// if the
		// Object[]
		// are
		// filtered
		// in by
		// the
		// validator
	}

	/**
	 * Return a Map wrapper, with each VMD from enabledValidators as the key, and the value a Set of
	 * IFileDelta wrapper around the changed Object[].
	 * 
	 * If "force" is true, then don't check if the object is filtered in by the validator or not.
	 * ValidatorSubsetOperation can use validators that don't filter in these particular resources,
	 * but can use a defaultExtension's validators instead.
	 */
	public static Map getFileDeltas(Set enabledValidators, Object[] changedResources, int ifileDeltaType, boolean force) {
		Map result = new HashMap();
		if ((enabledValidators == null) || (enabledValidators.size() == 0)) {
			return result;
		}

		Iterator iterator = enabledValidators.iterator();
		boolean cannotLoad = false;
		IWorkbenchHelper helper = null;
		while (iterator.hasNext()) {
			ValidatorMetaData vmd = (ValidatorMetaData) iterator.next();
			try {
				Set deltas = new HashSet();
				IProgressMonitor monitor = new NullProgressMonitor();
				for (int i = 0; i < changedResources.length; i++) {
					Object obj = changedResources[i];
					WorkbenchFileDelta wfd = null;
					if (obj instanceof IResource) {
						IResource res = (IResource) obj;
						if (force || !filterOut(monitor, vmd, res, getResourceDeltaType(ifileDeltaType))) {
							helper = vmd.getHelper(res.getProject());

							wfd = getFileDelta(helper, vmd, res, getResourceDeltaType(ifileDeltaType));
						}
					} else {
						wfd = new WorkbenchFileDelta(obj);
					}

					if (wfd != null) {
						deltas.add(wfd);
					}
				}
				result.put(vmd, deltas);
			} catch (InstantiationException exc) {
				cannotLoad = true;

				// Remove the vmd from the reader's list
				ValidationRegistryReader.getReader().disableValidator(vmd);

				// Log the reason for the disabled validator
				Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
				if (logger.isLoggingLevel(Level.SEVERE)) {
					LogEntry entry = ValidationPlugin.getLogEntry();
					entry.setSourceID("FilterUtil::getFileDeltas(Set, Object[], int, boolean)"); //$NON-NLS-1$
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
				continue;
			}

		}

		if (cannotLoad) {
			// Some of the validators should not be in the result set because either their
			// validator class or helper class could not be instantiated.
			Object[] vmds = enabledValidators.toArray();
			for (int i = 0; i < vmds.length; i++) {
				ValidatorMetaData vmd = (ValidatorMetaData) vmds[i];
				if (vmd.cannotLoad()) {
					result.remove(vmd);
				}
			}
		}


		return result;
	}

	public static WorkbenchFileDelta getFileDelta(IWorkbenchHelper helper, ValidatorMetaData vmd, IResource resource, int iresourceDeltaType) {
		// strip off the eclipse-specific information
		String fileName = helper.getPortableName(resource);
		if (fileName == null) {
			// The resource is not contained in the current project.
			// Can't see how this would happen, but check for it anyway.
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceID("FilterUtil::getFileDelta(IWorkbenchHelper, ValidatorMetaData, IResource, int)"); //$NON-NLS-1$
				entry.setMessageTypeID(ResourceConstants.VBF_EXC_SYNTAX_NULL_NAME);
				entry.setTokens(new String[]{resource.getName(), vmd.getValidatorDisplayName()});
				logger.write(Level.SEVERE, entry);
			}

			IPath resourcePath = resource.getFullPath();
			if (resourcePath != null) {
				// Since null file names are not allowed, default to the fully-qualified name of the
				// resource.
				fileName = resourcePath.toString();
			} else {
				if (logger.isLoggingLevel(Level.SEVERE)) {
					LogEntry entry = ValidationPlugin.getLogEntry();
					entry.setSourceID("FilterUtil::getFileDelta(IWorkbenchHelper, ValidtaorMetaData, IResource, int)"); //$NON-NLS-1$
					entry.setText("portableName is null and path is null for resource " + resource); //$NON-NLS-1$
					logger.write(Level.SEVERE, entry);
				}
				return null;
			}
		}

		int ifileDeltaType = getFileDeltaType(iresourceDeltaType);
		return new WorkbenchFileDelta(fileName, ifileDeltaType, resource);
	}


	/**
	 * Add the IResource to the vmd's list of resources to validate. Return true if the add was
	 * successful or false if the add was not successful.
	 */
	static boolean addToFileList(Map enabledValidators, IWorkbenchHelper helper, ValidatorMetaData vmd, IResource resource, int resourceDelta, boolean isFullBuild) {
		if ((vmd == null) || (resource == null)) {
			return false;
		}

		try {
			helper.registerResource(resource);
		} catch (Throwable exc) {
			// How to log this????
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceID("FilterUtil.addToFileList"); //$NON-NLS-1$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);
			}

			InternalValidatorManager.getManager().addInternalErrorTask(resource.getProject(), vmd, exc);

			// Don't return ... even though the register threw an exception, that's not to say
			// that the validator can't validate.
		}

		if (isFullBuild) {
			// To indicate a full build to the validator, don't build up a list of files;
			// pass in null instead. Given that the list of files should not be used,
			// don't calculate it.
			return true;
		}


		WorkbenchFileDelta newFileDelta = getFileDelta(helper, vmd, resource, resourceDelta);
		if (newFileDelta != null) {
			// if delta is null, getFileDelta will have logged the problem already
			addFileDelta(enabledValidators, vmd, newFileDelta);
		}

		return true;
	}

	/**
	 * Whether a full verification or a delta verification is in progress, both will call this
	 * method to process the resource. This method calls the current Validator to filter the
	 * resource (i.e., this method returns if the resource fails the filter test).
	 * <code>process</code> also sends output to the <code>IProgressMonitor</code>, and calls
	 * the current Validator to validate the resource.
	 * 
	 * To process a resource, there are several steps: 1. check if the resource is registered for
	 * this validator (i.e., the validator has either specified it in a filter, or has not filtered
	 * it out explicitly) 2. call <code>isValidationSource</code> on the current validator with
	 * the current resource. This method performs further filtering by the Validator itself, in
	 * addition to the static filtering done by the framework, based on the information in
	 * plugin.xml. 3. If the resource passes both filters, call <code>validate</code> on the
	 * validator, with the resource. 4. When complete (either by failing to pass a filter, or by the
	 * completion of the <code>validate</code>), increment the IProgressMonitor's status by one
	 * (i.e., one resource has been processed.)
	 */
	static boolean filterOut(IProgressMonitor monitor, ValidatorMetaData vmd, IResource resource, int resourceDelta) {
		if (monitor == null) {
			return false;
		}

		checkCanceled(monitor);
		return !(vmd.isApplicableTo(resource, resourceDelta));
	}

	/**
	 * Whether a full verification or a delta verification is in progress, both will call this
	 * method to process the resource. This method calls the current Validator to filter the
	 * resource (i.e., this method returns if the resource fails the filter test).
	 * <code>process</code> also sends output to the <code>IProgressMonitor</code>, and calls
	 * the current Validator to validate the resource.
	 * 
	 * To process a resource, there are several steps: 1. check if the resource is registered for
	 * this validator (i.e., the validator has either specified it in a filter, or has not filtered
	 * it out explicitly) 2. call <code>isValidationSource</code> on the current validator with
	 * the current resource. This method performs further filtering by the Validator itself, in
	 * addition to the static filtering done by the framework, based on the information in
	 * plugin.xml. 3. If the resource passes both filters, call <code>validate</code> on the
	 * validator, with the resource. 4. When complete (either by failing to pass a filter, or by the
	 * completion of the <code>validate</code>), increment the IProgressMonitor's status by one
	 * (i.e., one resource has been processed.)
	 */
	static void filterOut(IProgressMonitor monitor, Map enabledValidators, IResource resource, int resourceDelta, boolean isFullBuild) {
		if (monitor == null) {
			return;
		}

		checkCanceled(monitor);

		Iterator iterator = enabledValidators.keySet().iterator();
		boolean cannotLoad = false;
		while (iterator.hasNext()) {
			checkCanceled(monitor);

			ValidatorMetaData vmd = (ValidatorMetaData) iterator.next();

			if (!filterOut(monitor, vmd, resource, resourceDelta)) {
				try {
					// Notify the helper that a resource is about to be filtered in
					IWorkbenchHelper helper = vmd.getHelper(resource.getProject());
					addToFileList(enabledValidators, helper, vmd, resource, resourceDelta, isFullBuild);
				} catch (InstantiationException exc) {
					cannotLoad = true;

					// Remove the vmd from the reader's list
					ValidationRegistryReader.getReader().disableValidator(vmd);

					// Log the reason for the disabled validator
					Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
					if (logger.isLoggingLevel(Level.SEVERE)) {
						LogEntry entry = ValidationPlugin.getLogEntry();
						entry.setSourceID("FilterUtil::filterOut(IProgressMonitor, Map, IResource, int, boolean)"); //$NON-NLS-1$
						entry.setTargetException(exc);
						logger.write(Level.SEVERE, entry);
					}
				}
			}
		}

		if (cannotLoad) {
			// Some of the validators need to be removed from the set because the validator
			// or helper cannot be instantiated.
			Object[] vmds = enabledValidators.keySet().toArray();
			for (int i = 0; i < vmds.length; i++) {
				ValidatorMetaData vmd = (ValidatorMetaData) vmds[i];
				if (vmd.cannotLoad()) {
					enabledValidators.remove(vmd);
				}
			}
		}
	}

	/**
	 * Whether a full verification or a delta verification is in progress, both will call this
	 * method to process the resource. This method calls the current Validator to filter the
	 * resource (i.e., this method returns if the resource fails the filter test).
	 * <code>process</code> also sends output to the <code>IProgressMonitor</code>, and calls
	 * the current Validator to validate the resource.
	 * 
	 * This method is called during an incremental, not a full, validation. The full validation
	 * fakes an IResourceDelta, and the incremental needs to check that the delta is one of the
	 * deltas which is filtered in by the validation framework.
	 * 
	 * @see filterOut(IResourceDelta)
	 * 
	 * To process a resource, there are several steps: 1. check if the resource is registered for
	 * this validator (i.e., the validator has either specified it in a filter, or has not filtered
	 * it out explicitly) 2. call <code>isValidationSource</code> on the current validator with
	 * the current resource. This method performs further filtering by the Validator itself, in
	 * addition to the static filtering done by the framework, based on the information in
	 * plugin.xml. 3. If the resource passes both filters, call <code>validate</code> on the
	 * validator, with the resource. 4. When complete (either by failing to pass a filter, or by the
	 * completion of the <code>validate</code>), increment the IProgressMonitor's status by one
	 * (i.e., one resource has been processed.)
	 */
	static void filterOut(IProgressMonitor monitor, Map enabledValidators, IResource resource, IResourceDelta delta) {
		// filter in only resources which have been added, deleted, or its content changed.
		// moves will be registered as an add & delete combination
		if (filterOut(delta)) {
			return;
		}
		filterOut(monitor, enabledValidators, resource, delta.getKind(), false); // false =
		// incremental
		// build
	}

	/**
	 * Filter out resource deltas which don't correspond to changes that validators can validate.
	 * 
	 * This method will filter in deltas only if the delta is an add, a delete, or if the content of
	 * the file has changed.
	 * 
	 * Return true if the delta should be filtered out, and false if we should validate it.
	 * 
	 * @see IResourceDelta
	 */
	static boolean filterOut(IResourceDelta delta) {
		if (delta == null) {
			return true;
		}

		switch (delta.getKind()) {
			case IResourceDelta.ADDED : // resource has been added to the workbench
			{
				return false;
			}

			case IResourceDelta.REMOVED : // resource has been deleted from the workbench
			{
				// If the delta is an IProject, and the IProject is getting deleted or closed, don't
				// validate it or its children.
				if (delta.getResource() instanceof IProject) {
					return true;
				}
				return false;
			}

			case IResourceDelta.CHANGED : // resources has been changed in the workbench
			{
				// We want to add the enterprise bean only if its source file's
				// contents have changed. (for example, if a folder has been
				// added to the project, the IFile will be changed because
				// its position has been changed, but the enterprise bean
				// doesn't need to be redeployed. See IResourceDelta.getFlags()
				// for more information.)
				//
				// Or, if ejb-jar.xml has changed, the EJBJar is destroyed & created
				// from scratch, so the list of EnterpriseBean is new. Purge the old
				// EJBJar from the EJBCache (since it will never be referenced again),
				// and load the new EJBJar into the cache.
				if ((delta.getResource() instanceof IFile) && ((delta.getFlags() & IResourceDelta.CONTENT) != 0)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * This method returns true if the given resource and its children should be processed by the
	 * validators. That is, there are several types of changes which can occur to an IProject which
	 * should not trigger a revalidation of the project or its children. (e.g. project is deleted or
	 * closed.) For those cases, or if the IResourceDelta is invalid, this method will return false
	 * (do not validate the IProject or its children). Otherwise, return true (validate the resource &
	 * its children). If an IProject itself has not changed, but one of its children has
	 * (delta.getKind() of NO_CHANGE), then return true so that the children are validated.
	 */
	static boolean shouldProcess(IResource resource, IResourceDelta delta) {
		if ((resource != null) && !(resource instanceof IProject)) {
			return true;
		}

		if (delta == null) {
			return false;
		}

		switch (delta.getKind()) {
			case IResourceDelta.ADDED : // resource has been deleted from the workbench; may be part
			// of a move
			{
				if (0 != (delta.getFlags() & IResourceDelta.MOVED_FROM)) {
					// If it's being moved, don't revalidate its children. If it's being added, fall
					// through to the "return true;" at the end of this method.
					return false;
				}
				break;
			}

			case IResourceDelta.REMOVED : // resource has been deleted from the workbench; may be
			// part of a move
			{
				// Whether it's being deleted or moved, don't revalidate its children.
				return false;
			}

			case IResourceDelta.CHANGED : // resource has been changed in the workbench; may be part
			// of a move
			{
				if ((delta.getFlags() & IResourceDelta.OPEN) != 0) {
					// Change is related to the OPEN bit. Whether the project was closed and is now
					// open,
					// or the project was open and is now closed, don't need to revalidate the
					// children.
					return false;
				} else if ((delta.getFlags() & IResourceDelta.REPLACED) != 0) {
					// project was moved
					return false;
				}

				break;
			}
		}

		return true;
	}

	private static VMDResourceVisitor getResourceVisitor(IProgressMonitor monitor, Set enabledValidators) {
		if (_resourceVisitor == null) {
			_resourceVisitor = new VMDResourceVisitor() {
				private Map _vmdDeltas = null;
				private IProgressMonitor _progressMonitor = null;

				public Map getResult() {
					return _vmdDeltas;
				}

				public void setEnabledValidators(Set validators) {
					_vmdDeltas = wrapInMap(validators);
				}

				public IProgressMonitor getProgressMonitor() {
					return _progressMonitor;
				}

				public void setProgressMonitor(IProgressMonitor m) {
					_progressMonitor = m;
				}

				public boolean visit(IResource res) throws CoreException {
					FilterUtil.checkCanceled(getProgressMonitor());

					// We don't need to filter out anything, because a full validation
					// is about to be performed.
					filterOut(getProgressMonitor(), _vmdDeltas, res, IResourceDelta.CHANGED, true); // true
					// -
					// this
					// is a
					// full
					// build

					return true; // visit the resource's children as well
				}
			};
		}
		_resourceVisitor.setProgressMonitor(monitor);
		_resourceVisitor.setEnabledValidators(enabledValidators);

		return _resourceVisitor;
	}

	private static VMDDeltaVisitor getDeltaVisitor(IProgressMonitor monitor, Set enabledValidators) {
		if (_deltaVisitor == null) {
			_deltaVisitor = new VMDDeltaVisitor() {
				private Map _vmdDeltas = null;
				private IProgressMonitor _progressMonitor = null;

				public Map getResult() {
					return _vmdDeltas;
				}

				public void setEnabledValidators(Set validators) {
					_vmdDeltas = wrapInMap(validators);
				}

				public IProgressMonitor getProgressMonitor() {
					return _progressMonitor;
				}

				public void setProgressMonitor(IProgressMonitor m) {
					_progressMonitor = m;
				}

				public boolean visit(IResourceDelta subdelta) throws CoreException {
					checkCanceled(getProgressMonitor());
					if (subdelta == null)
						return true;

					IResource resource = subdelta.getResource();

					Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
					if (logger.isLoggingLevel(Level.FINEST)) {
						StringBuffer buffer = new StringBuffer("subdelta of "); //$NON-NLS-1$
						buffer.append(resource.getName());
						buffer.append(" is "); //$NON-NLS-1$
						buffer.append(subdelta.getKind());
						buffer.append(" resource exists? "); //$NON-NLS-1$
						buffer.append(resource.exists());
						buffer.append(" resource.isPhantom?"); //$NON-NLS-1$
						buffer.append(resource.isPhantom());

						if (logger.isLoggingLevel(Level.FINEST)) {
							LogEntry entry = ValidationPlugin.getLogEntry();
							entry.setSourceID("FilterUtil::visit(IResourceDelta)"); //$NON-NLS-1$
							entry.setText(buffer.toString());
							logger.write(Level.FINEST, entry);
						}

					}

					// If the delta is an IProject, and the IProject is getting deleted or closed,
					// don't validate it or its children.
					if (shouldProcess(resource, subdelta)) {
						filterOut(getProgressMonitor(), _vmdDeltas, resource, subdelta);
						return true; // visit the delta's children as well
					}
					return false; // do not visit the delta's children
				}
			};
		}
		_deltaVisitor.setProgressMonitor(monitor);
		_deltaVisitor.setEnabledValidators(enabledValidators);

		return _deltaVisitor;
	}

	public static Map loadDeltas(final IProgressMonitor monitor, final Set enabledValidators, IResourceDelta delta) throws CoreException {
		VMDDeltaVisitor visitor = getDeltaVisitor(monitor, enabledValidators);
		delta.accept(visitor, true); // true means include phantom resources
		return visitor.getResult();
	}

	public static Map loadDeltas(final IProgressMonitor monitor, final Set enabledValidators, IProject project) throws CoreException {
		VMDResourceVisitor visitor = getResourceVisitor(monitor, enabledValidators);
		project.accept(visitor, IResource.DEPTH_INFINITE, true); // true means include phantom
		// resources
		return visitor.getResult();
	}
}