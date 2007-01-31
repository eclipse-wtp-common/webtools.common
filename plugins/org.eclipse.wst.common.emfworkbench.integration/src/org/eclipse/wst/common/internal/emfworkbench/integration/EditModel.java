/***************************************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.internal.emfworkbench.integration;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jem.internal.util.emf.workbench.nls.EMFWorkbenchResourceHandler;
import org.eclipse.jem.util.emf.workbench.ResourceSetWorkbenchSynchronizer;
import org.eclipse.jem.util.emf.workbench.WorkbenchResourceHelperBase;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.frameworks.internal.ISaveHandler;
import org.eclipse.wst.common.frameworks.internal.SaveFailedException;
import org.eclipse.wst.common.frameworks.internal.SaveHandlerHeadless;
import org.eclipse.wst.common.frameworks.internal.SaveHandlerRegister;
import org.eclipse.wst.common.frameworks.internal.enablement.EnablementIdentifierEvent;
import org.eclipse.wst.common.frameworks.internal.enablement.IEnablementIdentifier;
import org.eclipse.wst.common.frameworks.internal.enablement.IEnablementIdentifierListener;
import org.eclipse.wst.common.frameworks.internal.enablement.nonui.IWFTWrappedException;
import org.eclipse.wst.common.frameworks.internal.operations.IOperationHandler;
import org.eclipse.wst.common.internal.emf.resource.CompatibilityXMIResource;
import org.eclipse.wst.common.internal.emf.resource.ReferencedResource;
import org.eclipse.wst.common.internal.emf.resource.TranslatorResource;
import org.eclipse.wst.common.internal.emf.utilities.ExtendedEcoreUtil;
import org.eclipse.wst.common.internal.emf.utilities.PleaseMigrateYourCodeError;
import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext;
import org.eclipse.wst.common.internal.emfworkbench.WorkbenchResourceHelper;
import org.eclipse.wst.common.internal.emfworkbench.edit.ClientAccessRegistry;
import org.eclipse.wst.common.internal.emfworkbench.edit.EditModelRegistry;
import org.eclipse.wst.common.internal.emfworkbench.edit.EditModelResource;
import org.eclipse.wst.common.internal.emfworkbench.edit.ReadOnlyClientAccessRegistry;
import org.eclipse.wst.common.internal.emfworkbench.validateedit.ResourceStateInputProvider;
import org.eclipse.wst.common.internal.emfworkbench.validateedit.ResourceStateValidator;
import org.eclipse.wst.common.internal.emfworkbench.validateedit.ResourceStateValidatorImpl;
import org.eclipse.wst.common.internal.emfworkbench.validateedit.ResourceStateValidatorPresenter;


public class EditModel implements CommandStackListener, ResourceStateInputProvider, ResourceStateValidator, IEnablementIdentifierListener {

	protected BasicCommandStack commandStack;
	protected final ListenerList listeners = new ListenerList();

	private Map params;
	private final String editModelID;
	private final boolean readOnly;
	// These are the current resource uris we need to track
	protected List knownResourceUris;
	// These are the current resource extensions to track
	protected List knownResourceExtensions;
	// This is a subset of the known resource uris, which we have requested be autoloaded
	protected List preloadResourceUris;
	// This is a map of identifiers to resources that we need to listen to in order to listen for
	// updates to the edit model resources
	protected Map resourceIdentifiers;

	protected EditModelEvent dirtyModelEvent;
	protected boolean isNotifing = false;
	protected boolean disposing = false;
	private boolean disposed = false;
	protected ResourceStateValidator stateValidator;
	protected boolean accessAsReadForUnKnownURIs;
	protected ResourceAdapter resourceAdapter = new ResourceAdapter();
	protected boolean isReverting = false;
	protected List resources;
	private ClientAccessRegistry registry;
	protected EMFWorkbenchContext emfContext = null;
	protected IProject project = null;

	private Reference reference;
	private List resourcesTargetedForTermination;

	protected class ResourceAdapter extends AdapterImpl {
		public void notifyChanged(Notification notification) {
			if (!isDisposing() && notification.getEventType() == Notification.SET && notification.getFeatureID(null) == Resource.RESOURCE__IS_LOADED) {
				resourceIsLoadedChanged((Resource) notification.getNotifier(), notification.getOldBooleanValue(), notification.getNewBooleanValue());
			}
		}
	}

	public EditModel(String editModelID, EMFWorkbenchContext context, boolean readOnly) {
		if (context == null)
			throw new IllegalStateException("EMF context can't be null"); //$NON-NLS-1$
		this.editModelID = editModelID;
		this.readOnly = readOnly;
		if (readOnly)
			this.registry = new ReadOnlyClientAccessRegistry();
		else
			this.registry = new ClientAccessRegistry();
		this.emfContext = context;
		this.project = context.getProject();
		initializeKnownResourceUris();
		processLoadedResources();
		processPreloadResources();
	}

	public EditModel(String editModelID, EMFWorkbenchContext context, boolean readOnly, boolean accessUnknownResourcesAsReadOnly) {
		this(editModelID, context, readOnly);
		this.accessAsReadForUnKnownURIs = accessUnknownResourcesAsReadOnly;
	}

	/**
	 * @return editModelID
	 */
	public String getEditModelID() {
		return editModelID;
	}

	public boolean isDisposing() {
		return disposing;
	}

	
	/**
	 * Subclasses should not override this method. This method will be made
	 * final in the next release. Subclasses should override doDispose() as
	 * necessary to dispose any additional artifacts.
	 */
	public void dispose() {
		try {
			if (disposing || disposed)
				return;
			disposing = true;
		
			if (hasListeners())
				notifyListeners(new EditModelEvent(EditModelEvent.PRE_DISPOSE, this));
			
			releaseResources();

			if (commandStack != null)
				commandStack.removeCommandStackListener(this);
			if (getEmfContext() != null)
				getEmfContext().removeEditModel(this, isReadOnly());
			releasePreloadResources();
			releaseIdentifiers();
			doDispose();
		} catch (RuntimeException re) {
			Logger.getLogger().logError(re);
		} finally {
			emfContext = null;
			resources = null;
			project = null;
			disposed = true;
			disposing = false;
		}
	}

	/**
	 * Subclasses should override as necessary
	 */
	protected void doDispose() {
	}

	protected void releaseIdentifiers() {
		if (resourceIdentifiers == null)
			return;
		Iterator iter = resourceIdentifiers.keySet().iterator();
		IEnablementIdentifier identifier = null;
		while (iter.hasNext()) {
			identifier = (IEnablementIdentifier) iter.next();
			identifier.removeIdentifierListener(this);
		}
	}

	private ResourceSetWorkbenchSynchronizer getResourceSetSynchronizer() {
		if (emfContext == null || !emfContext.hasResourceSet())
			return null;
		return getEmfContext().getResourceSet().getSynchronizer();
	}

	protected void releasePreloadResources() {
		ResourceSetWorkbenchEditSynchronizer sync = (ResourceSetWorkbenchEditSynchronizer) getResourceSetSynchronizer();
		if (sync != null) {
			for (int i = 0; i < preloadResourceUris.size(); i++) {
				URI uri = (URI) preloadResourceUris.get(i);
				sync.disableAutoload(uri);
			}
			for (int i = 0; i < knownResourceExtensions.size(); i++) {
				String ext = (String) knownResourceExtensions.get(i);
				sync.disableAutoload(ext);
			}
		}
	}


	/** ** BEGIN Command Stack Manipulation *** */

	/**
	 * Return the CommandStack.
	 */
	protected BasicCommandStack createCommandStack() {
		BasicCommandStack stack = new BasicCommandStack();
		return stack;
	}

	/**
	 * This is called with the {@linkorg.eclipse.emf.common.command.CommandStack}'s state has changed.
	 */
	public void commandStackChanged(java.util.EventObject event) {
		if (dirtyModelEvent == null)
			dirtyModelEvent = new EditModelEvent(EditModelEvent.DIRTY, this);
		if (hasListeners())
			notifyListeners(dirtyModelEvent);
	}

	/**
	 * Flush the Commands from the CommandStack.
	 */
	protected void flushCommandStack() {
		getCommandStack().flush();
		getCommandStack().saveIsDone();
	}

	/**
	 * Return the CommandStack.
	 */
	public BasicCommandStack getCommandStack() {
		if (commandStack == null) {
			commandStack = createCommandStack();
			commandStack.addCommandStackListener(this);
		}
		return commandStack;
	}

	/**
	 * Returns true if there are any listeners
	 */
	public boolean hasListeners() {
		return !listeners.isEmpty();
	}

	/** ** END Command Stack Manipulation *** */

	/** ** BEGIN Listeners *** */

	/**
	 * Add
	 * 
	 * @aListener to the list of listeners.
	 */
	public void addListener(EditModelListener aListener) {
		if (aListener != null)
			listeners.add(aListener);
	}

	/**
	 * Notify listeners of
	 * 
	 * @anEvent.
	 */
	protected void notifyListeners(final EditModelEvent anEvent) {
		
		NotifyRunner notifier = new NotifyRunner(anEvent); 
		
		Object[] notifyList = listeners.getListeners(); 
		for (int i = 0; i < notifyList.length; i++) {
			notifier.setListener( (EditModelListener) notifyList[i] );
			SafeRunner.run(notifier);
		}
	}

	/**
	 * Remove
	 * 
	 * @aListener from the list of listeners.
	 */
	public boolean removeListener(EditModelListener aListener) {
		listeners.remove(aListener);
		return true;
	}

	/** ** END Listeners *** */

	protected void makeFileEditable(IFile aFile) {
		if (aFile == null)
			return;
		aFile.getResourceAttributes().setReadOnly(false);
	}

	/**
	 * @return java.util.List of IFile; any read-only files that will be touched if this edit model
	 *         saves
	 */
	public List getReadOnlyAffectedFiles() {
		Iterator affected = getAffectedFiles().iterator();
		List result = new ArrayList();
		while (affected.hasNext()) {
			IFile aFile = (IFile) affected.next();
			if (aFile.isReadOnly())
				result.add(aFile);
		}
		return result;
	}

	/** ** BEGIN Save Handlers *** */

	protected ISaveHandler getSaveHandler() {
		return SaveHandlerRegister.getSaveHandler();
	}

	/**
	 * Default is to do nothing. This method is called if a saveIfNecessary or
	 * saveIfNecessaryWithPrompt determines not to save. This provides subclasses with an
	 * opportunity to do some other action.
	 */
	protected void handleSaveIfNecessaryDidNotSave(IProgressMonitor monitor) {
		// do nothing
	}

	/**
	 * This will force all of the referenced Resources to be saved.
	 */
	public void save(Object accessorKey) {
		save(null, accessorKey);
	}

	/**
	 * This will force all of the referenced Resources to be saved.
	 */
	public void save(IProgressMonitor monitor) throws PleaseMigrateYourCodeError {
		// save
	}

	/**
	 * Subclasses may override {@link #primSave}
	 */
	public final void save(IProgressMonitor monitor, Object accessorKey) {
		assertPermissionToSave(accessorKey);
		getSaveHandler().access();
		try {
			IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
				public void run(IProgressMonitor aMonitor) {
					primSave(aMonitor);
				}
			};
			runSaveOperation(runnable, monitor);
		} catch (SaveFailedException ex) {
			getSaveHandler().handleSaveFailed(ex, monitor);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			getSaveHandler().release();
		}
	}

	/**
	 * Save only resources that need to be saved (i.e., no other references).
	 */
	public void saveIfNecessary(Object accessorKey) {
		saveIfNecessary(null, accessorKey);
	}

	/**
	 * Save only resources that need to be saved (i.e., no other references).
	 */
	public void saveIfNecessary(IProgressMonitor monitor, Object accessorKey) {
		if (shouldSave())
			save(monitor, accessorKey);
		else
			handleSaveIfNecessaryDidNotSave(monitor);
	}

	/**
	 * Save only if necessary. If typically a save would not occur because this edit model is
	 * shared, the user will be prompted using the
	 * 
	 * @operationHandler. If the prompt returns true (the user wants to save) the entire edit model
	 *                    will be saved.
	 */
	public void saveIfNecessaryWithPrompt(IOperationHandler operationHandler, Object accessorKey) {
		saveIfNecessaryWithPrompt(null, operationHandler, accessorKey);
	}

	/**
	 * Save only if necessary. If typically a save would not occur because this edit model is
	 * shared, the user will be prompted using the
	 * 
	 * @operationHandler. If the prompt returns true (the user wants to save) the entire edit model
	 *                    will be saved. You may pass in a boolean <code>wasDirty</code> to
	 *                    indicate whether this edit model was dirty prior to making any changes and
	 *                    calling this method. {@link EditModel#isDirty()}
	 */
	public void saveIfNecessaryWithPrompt(IOperationHandler operationHandler, boolean wasDirty, Object accessorKey) {
		saveIfNecessaryWithPrompt(null, operationHandler, wasDirty, accessorKey);
	}

	/**
	 * Save only if necessary. If typically a save would not occur because this edit model is
	 * shared, the user will be prompted using the
	 * 
	 * @operationHandler. If the prompt returns true (the user wants to save) the entire edit model
	 *                    will be saved.
	 */
	public void saveIfNecessaryWithPrompt(IProgressMonitor monitor, IOperationHandler operationHandler, Object accessorKey) {
		saveIfNecessaryWithPrompt(monitor, operationHandler, true, accessorKey);
	}

	/**
	 * Save only if necessary. If typically a save would not occur because this edit model is
	 * shared, the user will be prompted using the
	 * 
	 * @operationHandler. If the prompt returns true (the user wants to save) the entire edit model
	 *                    will be saved. You may pass in a boolean <code>wasDirty</code> to
	 *                    indicate whether this edit model was dirty prior to making any changes and
	 *                    calling this method. {@link EditModel#isDirty()}
	 */
	public void saveIfNecessaryWithPrompt(IProgressMonitor monitor, IOperationHandler operationHandler, boolean wasDirty, Object accessorKey) {

		if (shouldSave(operationHandler, wasDirty))
			save(monitor, accessorKey);
		else
			handleSaveIfNecessaryDidNotSave(monitor);
	}

	protected void assertPermissionToSave(Object accessorKey) {
		if (registry != null)
			registry.assertAccess(accessorKey);
	}

	protected void runSaveOperation(IWorkspaceRunnable runnable, IProgressMonitor monitor) throws SaveFailedException {
		try {
			ResourcesPlugin.getWorkspace().run(runnable, ResourcesPlugin.getWorkspace().getRoot(), IWorkspace.AVOID_UPDATE, monitor);
		} catch (CoreException e) {
			throw new SaveFailedException(e);
		}
	}

	/**
	 * Should the resources be saved.
	 */
	protected boolean shouldSave(IOperationHandler operationHandler, boolean wasDirty) {
		return !wasDirty ? shouldSave() : shouldSave(operationHandler);
	}

	/**
	 * Return true if the uri for
	 * 
	 * @aResource is one of the known resource uris.
	 */
	public boolean isInterrestedInResource(Resource aResource) {
		return isInterrestedInResourceUri(aResource.getURI());
	}

	protected boolean isInterrestedInResourceUri(URI resURI) {
		URI uri;
		List uriStrings = getKnownResourceUris();
		for (int i = 0; i < uriStrings.size(); i++) {
			uri = (URI) uriStrings.get(i);
			if (ExtendedEcoreUtil.endsWith(resURI, uri))
				return true;
		}
		return false;
	}


	/**
	 * Subclasses should override and add URIs (type URI) of known resources. You must add resources
	 * that have references to other known resources first so they will be released first.
	 */
	protected void initializeKnownResourceUris() {
		knownResourceUris = new ArrayList();
		preloadResourceUris = new ArrayList();
		EditModelResource res = null;
		Collection editModelResources = EditModelRegistry.getInstance().getEditModelResources(getEditModelID());
		Iterator iter = editModelResources.iterator();
		while (iter.hasNext()) {
			res = (EditModelResource) iter.next();
			addEditModelResource(res);
		}
		Collection resourceExtensions = EditModelRegistry.getInstance().getEditModelExtensions(getEditModelID());
		/* bug 170690 - initialize knownResourceExtensions if necessary, and change reference from iter to it */
		if (knownResourceExtensions == null)
		{
			knownResourceExtensions = new ArrayList();
		}
		if (!resourceExtensions.isEmpty())
		{
			knownResourceExtensions.addAll(resourceExtensions);
			Iterator it = resourceExtensions.iterator();
			ResourceSetWorkbenchEditSynchronizer sync = (ResourceSetWorkbenchEditSynchronizer) getEmfContext().getResourceSet().getSynchronizer();
			while (it.hasNext()) {
				sync.enableAutoload(it.next().toString());
			}
		}
	}

	private void addEditModelResource(EditModelResource res) {
		boolean enabled = false;
		try {
			if (res.isCore()) {
				enabled = true;
			} else {
				IEnablementIdentifier identifier = res.getEnablementIdentifier(getProject());
				registerInterest(identifier, res);
				enabled = identifier.isEnabled();
			}
		} catch (RuntimeException re) {
			Logger.getLogger().logWarning(re);
		}
		if (enabled) {
			URI uri = res.getURI();
			knownResourceUris.add(uri);
			if (res.isAutoLoad()) {
				ResourceSetWorkbenchEditSynchronizer sync = (ResourceSetWorkbenchEditSynchronizer) getEmfContext().getResourceSet().getSynchronizer();
				sync.enableAutoload(uri);
				preloadResourceUris.add(uri);
			}
		}
	}

	/**
	 * @param res
	 */
	private void registerInterest(IEnablementIdentifier identifier, EditModelResource res) {
		getEditModelResources(identifier).add(res);
	}

	private List getEditModelResources(IEnablementIdentifier identifier) {
		if (resourceIdentifiers == null)
			resourceIdentifiers = new HashMap();
		List tResources = (List) resourceIdentifiers.get(identifier);
		if (tResources == null) {
			tResources = new ArrayList(3);
			resourceIdentifiers.put(identifier, tResources);
			identifier.addIdentifierListener(this);
		}
		return tResources;
	}



	public java.util.List getKnownResourceUris() {
		if (knownResourceUris == null)
			initializeKnownResourceUris();

		return knownResourceUris;
	}

	public boolean isShared() {
		return registry.size() > 1;
	}

	/**
	 * @see ResourceStateInputProvider#cacheNonResourceValidateState(List)
	 */
	public void cacheNonResourceValidateState(List roNonResourceFiles) {
		// do nothing
	}

	/**
	 * @see ResourceStateInputProvider#getNonResourceFiles()
	 */
	public List getNonResourceFiles() {
		return null;
	}

	/**
	 * @see ResourceStateInputProvider#getNonResourceInconsistentFiles()
	 */
	public List getNonResourceInconsistentFiles() {
		return null;
	}

	/**
	 * Gets the stateValidator.
	 * 
	 * @return Returns a ResourceStateValidator
	 */
	public ResourceStateValidator getStateValidator() {
		if (stateValidator == null)
			stateValidator = createStateValidator();
		return stateValidator;
	}

	/**
	 * Method createStateValidator.
	 * 
	 * @return ResourceStateValidator
	 */
	private ResourceStateValidator createStateValidator() {
		return new ResourceStateValidatorImpl(this);
	}

	/**
	 * @see ResourceStateValidator#checkActivation(ResourceStateValidatorPresenter)
	 */
	public void checkActivation(ResourceStateValidatorPresenter presenter) throws CoreException {
		getStateValidator().checkActivation(presenter);
	}

	/**
	 * @see ResourceStateValidator#lostActivation(ResourceStateValidatorPresenter)
	 */
	public void lostActivation(ResourceStateValidatorPresenter presenter) throws CoreException {
		getStateValidator().lostActivation(presenter);
	}

	/**
	 * @see ResourceStateValidator#validateState(ResourceStateValidatorPresenter)
	 */
	public IStatus validateState(ResourceStateValidatorPresenter presenter) throws CoreException {
		if (presenter == null)
			return Status.OK_STATUS;
		return getStateValidator().validateState(presenter);
	}

	/**
	 * @see ResourceStateValidator#checkSave(ResourceStateValidatorPresenter)
	 */
	public boolean checkSave(ResourceStateValidatorPresenter presenter) throws CoreException {
		return getStateValidator().checkSave(presenter);
	}

	/**
	 * @see ResourceStateValidator#checkReadOnly()
	 */
	public boolean checkReadOnly() {
		return getStateValidator().checkReadOnly();
	}

	/**
	 * Return the ResourceSet from the Nature.
	 * 
	 * @return org.eclipse.emf.ecore.resource.ResourceSet
	 */
	public ResourceSet getResourceSet() {
		ResourceSet resourceSet = null;
		if (getEmfContext() != null)
			resourceSet = getEmfContext().getResourceSet();
		return resourceSet;
	}

	protected void resourceIsLoadedChanged(Resource aResource, boolean oldValue, boolean newValue) {
		if (!isReverting && !disposing && hasListeners()) {
			int eventCode = newValue ? EditModelEvent.LOADED_RESOURCE : EditModelEvent.UNLOADED_RESOURCE;
			EditModelEvent evt = new EditModelEvent(eventCode, this);
			evt.addResource(aResource);
			notifyListeners(evt);
		}
	}

	public Resource getResource(URI aUri) {
		Resource res = getAndLoadLocalResource(aUri);
		if (res == null)
			res = WorkbenchResourceHelper.getOrCreateResource(aUri, getResourceSet());
		if (res != null)
			processResource(res);
		return res;
	}

	protected void processResource(Resource aResource) {
		if (aResource != null && !getResources().contains(aResource)) {
			if (aResource instanceof ReferencedResource) {
				access((ReferencedResource) aResource);
				// We need a better way to pass this through the save options instead.
				// We also need to make this dynamic based on the project target
				((ReferencedResource) aResource).setFormat(CompatibilityXMIResource.FORMAT_MOF5);
			} else if (aResource instanceof CompatibilityXMIResource) {
				((CompatibilityXMIResource) aResource).setFormat(CompatibilityXMIResource.FORMAT_MOF5);
			}

			addResource(aResource);
		}
	}

	protected void addResource(Resource aResource) {
		getResources().add(aResource);
		aResource.eAdapters().add(resourceAdapter);
	}

	/**
	 * Return a Resource for
	 * 
	 * @aUri.
	 */
	// TODO The following method will only use the last segment when looking for a resource.
	protected Resource getResource(List tResources, URI aUri) {
		Resource resource;
		for (int i = 0; i < tResources.size(); i++) {
			resource = (Resource) tResources.get(i);
			if (ExtendedEcoreUtil.endsWith(resource.getURI(), aUri))
				return resource;
		}
		return null;
	}

	public Resource createResource(URI uri) {
		Resource resource = getExistingOrCreateResource(uri);
		processResource(resource);
		return resource;
	}

	/**
	 * Get a cached Resource, either local or in the ResourceSet, before creating a Resource. This
	 * api handles the case that the Resource may be created during a demand load that failed.
	 */
	public Resource getExistingOrCreateResource(URI uri) {
		Resource res = getAndLoadLocalResource(uri);
		if (res == null)
			res = WorkbenchResourceHelperBase.getExistingOrCreateResource(uri, getResourceSet());
		return res;
	}

	/**
	 * Return a Resource for
	 * 
	 * @aUri.
	 */
	protected Resource getAndLoadLocalResource(URI aUri) {
		Resource resource = getLocalResource(aUri);
		if (null != resource && !resource.isLoaded()) {
			try {
				resource.load(Collections.EMPTY_MAP); // reload it
			} catch (IOException e) {
				// Ignore
			}
		}
		return resource;
	}

	/**
	 * Return a Resource for
	 * 
	 * @aUri.
	 */
	protected Resource getLocalResource(URI aUri) {
		return getResource(getResources(), aUri);
	}

	/*
	 * Return true if this is a ReadOnly EditModel or if we should only access unknown URIs as
	 * ReadOnly.
	 */
	protected boolean shouldAccessForRead(ReferencedResource aResource) {
		return isReadOnly() || (accessAsReadForUnKnownURIs && !isInterrestedInResource(aResource));
	}

	/**
	 * Save only resources that need to be saved (i.e., no other references).
	 */
	public void resourceChanged(EditModelEvent anEvent) {
		int code = anEvent.getEventCode();
		switch (code) {
			case EditModelEvent.REMOVED_RESOURCE : {
				if (!isReverting && hasResourceReference(anEvent.getChangedResources()))
					removeResources(anEvent.getChangedResources());
				else
					return;
				break;
			}
			case EditModelEvent.ADDED_RESOURCE :
				if (!processResourcesIfInterrested(anEvent.getChangedResources()))
					return;
		}
		if (hasListeners()) {
			anEvent.setEditModel(this);
			notifyListeners(anEvent);
		}
	}

	/**
	 * Return true if aResource is referenced by me.
	 */
	protected boolean hasResourceReference(Resource aResource) {
		if (aResource != null)
			return getResources().contains(aResource);
		return false;
	}

	/**
	 * Return true if any Resource in the list of
	 * 
	 * @resources is referenced by me.
	 */
	protected boolean hasResourceReference(List tResources) {
		for (int i = 0; i < tResources.size(); i++) {
			if (hasResourceReference((Resource) tResources.get(i)))
				return true;
		}
		return false;
	}

	/**
	 * Remove reference to the Resource objects in
	 * 
	 * @aList. This should be called when one or more Resource objects are removed from the
	 *         ResourceSet without the reference count going to zero.
	 */
	protected void removeResources(List aList) {
		Resource res;
		for (int i = 0; i < aList.size(); i++) {
			res = (Resource) aList.get(i);
			if (removeResource(res) && res instanceof ReferencedResource)
				removedResource((ReferencedResource) res);
		}
	}

	private final void removedResource(ReferencedResource referencedResource) {
		if (!isReadOnly() && referencedResource.wasReverted()) {
			isReverting = true;
			try {
				reverted(referencedResource);
			} finally {
				isReverting = false;
			}
		}
	}

	protected boolean removeResource(URI uri) {
		Resource res = getLocalResource(uri);
		return removeResource(res);
	}

	/**
	 * Remove reference to the aResource.
	 */
	protected boolean removeResource(Resource aResource) {
		if (aResource != null) {
			aResource.eAdapters().remove(resourceAdapter);
			return getResources().remove(aResource);
		}
		return false;
	}

	/**
	 * Subclasses should override to post process a removed ReferencedResource.
	 * 
	 * @see J2EEEditModel#revertAllResources()
	 */
	protected void reverted(ReferencedResource revertedResource) {
		revertAllResources();
	}

	protected void revertAllResources() {
		List someResources = getSortedResources();
		for (int i = 0; i < someResources.size(); i++)
			((Resource) someResources.get(i)).unload();
		getResources().removeAll(someResources);
		for (int i = 0; i < someResources.size(); i++)
			((Resource) someResources.get(i)).eAdapters().remove(resourceAdapter);
	}

	/**
	 * group the resources by XMI first, then XML
	 */
	protected List getSortedResources() {

		List theResources = getResources();
		int size = theResources.size();
		if (size == 0)
			return Collections.EMPTY_LIST;
		Resource[] sorted = new Resource[size];
		int xmlInsertPos = size - 1;
		int xmiInsertPos = 0;
		Resource res = null;
		for (int i = 0; i < size; i++) {
			res = (Resource) theResources.get(i);
			if (res instanceof TranslatorResource)
				sorted[xmlInsertPos--] = res;
			else
				sorted[xmiInsertPos++] = res;
		}

		return Arrays.asList(sorted);
	}

	/**
	 * Process Resources that we are interrested in.
	 */
	protected boolean processResourcesIfInterrested(List someResources) {
		int size = someResources.size();
		Resource res;
		boolean processed = false;
		for (int i = 0; i < size; i++) {
			res = (Resource) someResources.get(i);
			if ((res != null) && (isInterrestedInResource(res))) {
				processResource(res);
				processed = true;
			}
		}
		return processed;
	}

	public EMFWorkbenchContext getEmfContext() {
		if (isDisposed())
			throw new IllegalStateException("Edit Model already disposed"); //$NON-NLS-1$
		if (emfContext == null)
			throw new IllegalStateException("EMF context is null"); //$NON-NLS-1$
		return emfContext;
	}

	public boolean isDisposed() {
		return disposed;
	}



	public IProject getProject() {
		return project;
	}

	/**
	 * This method should only be called by the EMFWorkbenchContext.
	 */
	public void access(Object accessorKey) {
		registry.access(accessorKey);
	}

	/**
	 * Access
	 * 
	 * @aResource for read or write.
	 */
	protected void access(ReferencedResource aResource) {
		if (shouldAccessForRead(aResource))
			aResource.accessForRead();
		else
			aResource.accessForWrite();
	}

	/**
	 * This method should be called from each client when they are finished working with the
	 * EditModel.
	 */
	public void releaseAccess(Object accessorKey) {

		registry.release(accessorKey);

		if (!isDisposing()) {
			boolean shouldDispose = false;
			shouldDispose = registry.size() == 0;
			if (shouldDispose) {
				dispose();
			}
		}
	}

	/**
	 * Release each of the referenced resources.
	 */
	protected void release(Resource aResource) {

		removeResource(aResource);
		if (aResource != null && aResource instanceof ReferencedResource)
			release((ReferencedResource) aResource);
	}

	/**
	 * Release each of the referenced resources.
	 */
	protected void release(ReferencedResource aResource) {
		if (isReadOnly())
			aResource.releaseFromRead();
		else
			aResource.releaseFromWrite();

	}

	/**
	 * Release each of the referenced resources.
	 */
	protected void releaseResources() {
		List tResources = getSortedResources();
		Resource resource;
		for (int i = 0; i < tResources.size(); i++) {
			resource = (Resource) tResources.get(i);
			release(resource);
		}
	}

	public void deleteResource(Resource aResource) {
		if (aResource == null || resources == null || !getResources().contains(aResource))
			return;
		getResourcesTargetedForTermination().add(aResource);

	}

	/**
	 * @return
	 */
	protected List getResourcesTargetedForTermination() {
		if (resourcesTargetedForTermination == null)
			resourcesTargetedForTermination = new ArrayList(5);
		return resourcesTargetedForTermination;
	}



	/**
	 * Remove my reference to aResource, remove it from the ResourceSet, and delete its file from
	 * the Workbench. This only happens if there is currently a reference to
	 * 
	 * @aResource.
	 */
	public void primDeleteResource(Resource aResource) {
		if (primFlushResource(aResource)) {
			try {
				getEmfContext().deleteResource(aResource);
			} catch (CoreException e) {
				// what should we do here?
			}
			if (hasListeners()) {
				EditModelEvent event = new EditModelEvent(EditModelEvent.REMOVED_RESOURCE, this);
				event.addResource(aResource);
				notifyListeners(event);
			}
		}
	}

	/**
	 * Remove my reference to aResource and remove it from the ResourceSet.
	 */
	public void flushResource(Resource aResource) {
		if (primFlushResource(aResource)) {
			if (hasListeners()) {
				EditModelEvent event = new EditModelEvent(EditModelEvent.REMOVED_RESOURCE, this);
				event.addResource(aResource);
				notifyListeners(event);
			}
		}
	}

	public Set getAffectedFiles() {
		Set aSet = new HashSet();
		List mofResources = getResources();
		for (int i = 0; i < mofResources.size(); i++) {
			Resource aResource = (Resource) mofResources.get(i);
			IFile output = WorkbenchResourceHelper.getFile(aResource);
			if (output != null)
				aSet.add(output);
		}
		return aSet;
	}

	protected List resetKnownResourceUris() {

		initializeKnownResourceUris();

		return knownResourceUris;
	}

	/**
	 * Insert the method's description here. Creation date: (4/11/2001 4:14:26 PM)
	 * 
	 * @return java.util.List
	 */
	public List getResources() {
		if (resources == null)
			resources = new ArrayList(5);
		return resources;
	}

	public String[] getResourceURIs() {
		return getResourceURIs(false);
	}

	public String[] getResourceURIs(boolean onlyDirty) {
		List list = getResources();
		int dirtyCount = 0;
		String[] uris = new String[list.size()];
		Resource res;
		for (int i = 0; i < list.size(); i++) {
			res = (Resource) list.get(i);
			if (!onlyDirty)
				uris[i] = res.getURI().toString();
			else if (res.isModified()) {
				uris[i] = res.getURI().toString();
				dirtyCount++;
			}
		}
		if (onlyDirty && dirtyCount > 0) {
			String[] dirty = new String[dirtyCount];
			int j = 0;
			for (int i = 0; i < uris.length; i++) {
				if (uris[i] != null) {
					dirty[j] = uris[i];
					j++;
				}
			}
			uris = dirty;
		}
		return uris;
	}

	/**
	 * Returns the first element in the extent of the resource; logs an error and returns null if
	 * the extent is empty
	 */
	public static EObject getRoot(Resource aResource) {
		EList extent = aResource.getContents();
		if (extent.size() < 1)
			return null;
		return (EObject) extent.get(0);
	}

	/**
	 * Handle the failure of
	 * 
	 * @aResource.
	 */
	protected void handleSaveFailed(Resource aResource, Exception e) {
		aResource.setModified(true);
		if (isFailedWriteFileFailure(e) && shouldSaveReadOnly(aResource))
			saveResource(aResource);
		else
			primHandleSaveFailed(aResource, e);
	}

	/**
	 * Return whether any of my resources has been modified.
	 */
	protected boolean isAnyResourceDirty() {
		List list = getResources();
		for (int i = 0; i < list.size(); i++) {
			if (((Resource) list.get(i)).isModified())
				return true;
		}
		return false;
	}

	/**
	 * Return whether a save is needed on the CommandStack
	 */
	public boolean isDirty() {
		return isAnyResourceDirty();
	}

	protected boolean isFailedWriteFileFailure(Exception ex) {
		return SaveHandlerHeadless.isFailedWriteFileFailure(ex);
	}

	/**
	 * Return true if you can only read the resources and not write.
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	protected boolean isReadOnlyFailure(Exception ex) {
		return false;
	}

	public boolean hasReadOnlyResource() {
		try {
			List list = getResources();
			int size = list.size();
			Resource res = null;
			IFile file;
			for (int i = 0; i < size; i++) {
				res = (Resource) list.get(i);
				file = WorkbenchResourceHelper.getFile(res);
				if (file != null && file.isReadOnly())
					return true;
			}
		} catch (NullPointerException e) {
			System.out.println(e);
		}
		return false;
	}

	/**
	 * @deprecated use createResource(URI) instead
	 */
	public Resource makeResource(String aUri) {
		return createResource(URI.createURI(aUri));
	}

	/**
	 * Return whether any of my resources has a reference count of one and it has been modified.
	 */
	public boolean needsToSave() {
		return !isShared() && isDirty();
	}

	/**
	 * Remove my reference to aResource and remove it from the ResourceSet. Return true if aResource
	 * was removed.
	 */
	protected boolean primFlushResource(Resource aResource) {
		if (aResource != null && hasResourceReference(aResource)) {
			removeResource(aResource);
			removeResourceSetResource(aResource);
			return true;
		}
		return false;
	}

	/**
	 * Handle the failure of
	 * 
	 * @aResource.
	 */
	protected void primHandleSaveFailed(Resource aResource, Exception e) {
		org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError(e);
		Exception nested = null;
		if (e instanceof IWFTWrappedException)
			nested = ((IWFTWrappedException) e).getNestedException();
		else
			nested = e;

		throw new SaveFailedException(EMFWorkbenchResourceHandler.getString("An_error_occurred_while_sa_ERROR_"), nested); //$NON-NLS-1$ = "An error occurred while saving."
	}

	/**
	 * Prompt for a save.
	 */
	protected boolean promptToSave(IOperationHandler operationHandler) {
		if (operationHandler == null)
			return false;
		return operationHandler.canContinue(EMFWorkbenchResourceHandler.getString("The_following_resources_ne_UI_"), getResourceURIs(true)); //$NON-NLS-1$ = "The following resources need to be saved but are currently shared, do you want to save now?"
	}

	/**
	 * This will force all of the referenced Resources to be saved.
	 */
	public void primSave(IProgressMonitor monitor) {
		if (isReadOnly())
			return; // do nothing
		deleteResourcesIfNecessary();
		Resource resource;
		if (getResources().isEmpty())
			return; // nothing to save
		List localResources = getSortedResources();
		for (int i = 0; i < localResources.size(); i++) {
			resource = (Resource) localResources.get(i);
			saveResource(resource);
		}
		getCommandStack().saveIsDone();
		if (hasListeners()) {
			EditModelEvent event = new EditModelEvent(EditModelEvent.SAVE, this);
			notifyListeners(event);
		}
	}

	/**
	 * 
	 */
	protected void deleteResourcesIfNecessary() {
		if (resourcesTargetedForTermination == null || resourcesTargetedForTermination.size() == 0)
			return;
		Resource deadres = null;
		for (int i = 0; i < getResourcesTargetedForTermination().size(); i++) {
			deadres = (Resource) getResourcesTargetedForTermination().get(i);
			primDeleteResource(deadres);

			getResources().remove(deadres);
			getResourcesTargetedForTermination().remove(deadres);
		}
	}



	/**
	 * Save
	 * 
	 * @aResource.
	 */
	protected void primSaveResource(Resource aResource) throws Exception {
		if (aResource.isModified())
			aResource.save(Collections.EMPTY_MAP);
	}

	/**
	 * Process resources that have already been loaded.
	 */
	protected void processLoadedResources() {
		List loaded = getResourceSet().getResources();
		if (!loaded.isEmpty())
			processResourcesIfInterrested(loaded);
	}

	private void processPreloadResources() {
		for (int i = 0; i < preloadResourceUris.size(); i++) {
			URI uri = (URI) preloadResourceUris.get(i);
			getResource(uri);
		}
	}

	/**
	 * Remove aResource from my ResourceSet. Return true if aResource was removed.
	 */
	protected boolean removeResourceSetResource(Resource aResource) {
		aResource.eSetDeliver(false);
		aResource.unload();
		aResource.eSetDeliver(true);
		return getResourceSet().getResources().remove(aResource);
	}

	protected void saveResource(Resource resource) {
		try {
			primSaveResource(resource);
		} catch (Exception e) {
			handleSaveFailed(resource, e);
		}
	}

	/**
	 * Should the resources be saved.
	 */
	protected boolean shouldSave() {
		return !isReadOnly() && !isShared();
	}

	/**
	 * Should the resources be saved.
	 */
	protected boolean shouldSave(IOperationHandler operationHandler) {
		return shouldSave() || promptToSave(operationHandler);
	}

	protected boolean shouldSaveReadOnly(Resource aResource) {
		IFile aFile = WorkbenchResourceHelper.getFile(aResource);
		if (aFile == null || !aFile.isReadOnly())
			return false;

		return getSaveHandler().shouldContinueAndMakeFileEditable(aFile);
	}

	/**
	 * Force all of the known resource URIs to be loaded if they are not already.
	 */
	public void forceLoadKnownResources() {
		List uris = getKnownResourceUris();
		URI uri = null;
		for (int i = 0; i < uris.size(); i++) {
			uri = (URI) uris.get(i);
			getResource(uri);
		}
	}

	/**
	 * This method should be called when you want to extend this edit model to handle a resource
	 * with a URI equal to <code>aRelativeURI</code>.
	 */
	public void manageExtensionResourceURI(String aRelativeURI) {
		if (aRelativeURI != null && aRelativeURI.length() > 0) {
			URI uri = URI.createURI(aRelativeURI);
			if (!isInterrestedInResourceUri(uri)) {
				getKnownResourceUris().add(uri);
				// Process the resource if it is already loaded.
				try {
					Resource res = getEmfContext().getResource(uri);
					if (res != null)
						processResource(res);
				} catch (Exception e) {
					// Ignore
				}
			}
		}
	}

	/**
	 * Get a cached Resource or try to load the Resource prior to creating a Resource. This api
	 * handles the case that the Resource may be created during the load.
	 */
	public Resource getOrCreateResource(URI uri) {
		return getResource(uri);
	}

	/**
	 * @return boolean
	 */
	public boolean isAccessAsReadForUnKnownURIs() {
		return accessAsReadForUnKnownURIs;
	}

	/**
	 * Use this api to indicate that you want all unknown Resources to be accessed for ReadOnly.
	 * 
	 * @param b
	 */
	public void setAccessAsReadForUnKnownURIs(boolean b) {
		accessAsReadForUnKnownURIs = b;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer(getClass().getName());
		buffer.append(": "); //$NON-NLS-1$
		if (isReadOnly())
			buffer.append(" R = "); //$NON-NLS-1$
		else
			buffer.append(" W = "); //$NON-NLS-1$
		buffer.append(getRegistry().size());
		buffer.append("[ID: \""); //$NON-NLS-1$
		buffer.append(getEditModelID());
		buffer.append("\" Known Resources: ["); //$NON-NLS-1$
		List uris = getKnownResourceUris();
		if (uris != null) {
			int i = 0;
			for (i = 0; i < (uris.size() - 1); i++)
				buffer.append(uris.get(i) + ", "); //$NON-NLS-1$
			buffer.append(uris.get(i));
			buffer.append("]"); //$NON-NLS-1$
		} else
			buffer.append("none"); //$NON-NLS-1$


		buffer.append("]"); //$NON-NLS-1$
		return buffer.toString();
	}

	public Reference getReference() {
		if (reference == null)
			reference = new Reference();
		return reference;
	}

	/**
	 * @return
	 */
	protected ClientAccessRegistry getRegistry() {
		return registry;
	}

	public class Reference {

		protected String tostring = null;

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			if (tostring == null) {
				StringBuffer result = new StringBuffer("EditModel.Reference ["); //$NON-NLS-1$
				result.append("{"); //$NON-NLS-1$
				result.append(getEditModelID());
				result.append("} {"); //$NON-NLS-1$
				result.append(getProject().getName());
				result.append("}]"); //$NON-NLS-1$
				tostring = result.toString();
			}
			return tostring;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode() {
			return toString().hashCode();
		}
	}

	/**
	 * Subclasses can override - by default this will return the first root object from the first
	 * resource referenced by the known resource URIs for this EditModel
	 * 
	 * @return an EObject or Null
	 */
	public EObject getPrimaryRootObject() {
		Resource res = getPrimaryResource();
		if (res == null || res.getContents().isEmpty())
			return null;
		return (EObject) res.getContents().get(0);
	}

	/**
	 * Subclasses can override - by default this will return the first resource referenced by the
	 * known resource URIs for this EditModel
	 * 
	 * @return
	 */
	public Resource getPrimaryResource() {
		if (knownResourceUris == null)
			getKnownResourceUris();
		if (knownResourceUris == null || knownResourceUris.isEmpty())
			return null;

		URI uri = (URI) knownResourceUris.get(0);
		return getResource(uri);
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.enablement.IEnablementIdentifierListener#identifierChanged(org.eclipse.wst.common.frameworks.internal.enablement.EnablementIdentifierEvent)
	 */
	public void identifierChanged(EnablementIdentifierEvent evt) {
		if (evt.hasEnabledChanged()) {
			EditModelEvent editModelEvent = new EditModelEvent(EditModelEvent.KNOWN_RESOURCES_ABOUT_TO_CHANGE, this);
			notifyListeners(editModelEvent);
			IEnablementIdentifier id = evt.getIdentifier();
			if (id.isEnabled())
				addKnownResources(id);
			else
				removeKnownResources(id);
			editModelEvent = new EditModelEvent(EditModelEvent.KNOWN_RESOURCES_CHANGED, this);
			notifyListeners(editModelEvent);
		}
	}

	private void removeKnownResources(IEnablementIdentifier id) {
		List editModelResources = getEditModelResources(id);
		EditModelResource editModelResource = null;
		ResourceSetWorkbenchEditSynchronizer sync = (ResourceSetWorkbenchEditSynchronizer) getResourceSetSynchronizer();
		for (int i = 0; i < editModelResources.size(); i++) {
			editModelResource = (EditModelResource) editModelResources.get(i);
			if (editModelResource.isAutoLoad() && sync != null) {
				sync.disableAutoload(editModelResource.getURI());
				preloadResourceUris.remove(editModelResource.getURI());
			}
			knownResourceUris.remove(editModelResource.getURI());
			removeResource(editModelResource.getURI());
		}

	}



	private void addKnownResources(IEnablementIdentifier id) {
		List editModelResources = getEditModelResources(id);
		EditModelResource editModelResource = null;
		ResourceSetWorkbenchEditSynchronizer sync = (ResourceSetWorkbenchEditSynchronizer) getResourceSetSynchronizer();
		for (int i = 0; i < editModelResources.size(); i++) {
			editModelResource = (EditModelResource) editModelResources.get(i);
			if (editModelResource.isAutoLoad() && sync != null) {
				sync.enableAutoload(editModelResource.getURI());
				preloadResourceUris.add(editModelResource.getURI());
				getResource(editModelResource.getURI());
			}
			knownResourceUris.add(editModelResource.getURI());

		}
	}


	/**
	 * @return Returns the params.
	 */
	public Map getParams() {
		return params;
	}

	/**
	 * @param params
	 *            The params to set.
	 */
	public void setParams(Map params) {
		this.params = params;
	}
	
	public class NotifyRunner implements ISafeRunnable { 
		
		private final EditModelEvent event;
		private EditModelListener listener;
		
		public NotifyRunner(EditModelEvent event) {
			Assert.isNotNull(event);
			this.event = event;
		}
		
		
		public void setListener(EditModelListener listener) {
			this.listener = listener;
		}

		public void handleException(Throwable exception) { 
			EMFWorkbenchEditPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, EMFWorkbenchEditPlugin.ID, 0, exception.getMessage(), exception));
			
		}

		public void run() throws Exception {
			if(listener != null)
				listener.editModelChanged(event); 
		}
		
	};
}
