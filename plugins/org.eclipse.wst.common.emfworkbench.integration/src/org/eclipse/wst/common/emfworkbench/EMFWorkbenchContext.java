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
 * Created on Mar 3, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.eclipse.wst.common.emfworkbench;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.wst.common.emf.utilities.DefaultOverridableResourceFactoryRegistry;
import org.eclipse.wst.common.emfworkbench.integration.EditModel;
import org.eclipse.wst.common.emfworkbench.integration.EditModelEvent;
import org.eclipse.wst.common.emfworkbench.integration.ProjectResourceSetEditImpl;
import org.eclipse.wst.common.internal.emf.resource.CompatibilityXMIResource;
import org.eclipse.wst.common.internal.emf.resource.ReferencedXMIFactoryImpl;
import org.eclipse.wst.common.internal.emfworkbench.edit.EditModelRegistry;

import com.ibm.wtp.common.logger.proxy.Logger;
import com.ibm.wtp.emf.workbench.EMFWorkbenchContextBase;
import com.ibm.wtp.emf.workbench.ISynchronizerExtender;
import com.ibm.wtp.emf.workbench.ProjectResourceSet;
import com.ibm.wtp.emf.workbench.WorkbenchURIConverter;

/**
 * @author schacher
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EMFWorkbenchContext extends EMFWorkbenchContextBase implements ISynchronizerExtender {

	private Map readOnlyModels = new HashMap();
	private Map editableModels = new HashMap();

	protected Adapter resourceSetListener;

	protected boolean defaultToMOF5Compatibility = false;


	/**
	 * @param aProject
	 */
	public EMFWorkbenchContext(IProject aProject) {
		super(aProject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.emfworkbench.EMFWorkbenchContext#initializeResourceSet(org.eclipse.wst.common.emfworkbench.ProjectResourceSet)
	 */
	protected void initializeResourceSet(ProjectResourceSet aResourceSet) {
		super.initializeResourceSet(aResourceSet);
		Resource.Factory.Registry reg = new DefaultOverridableResourceFactoryRegistry();
		Resource.Factory factory = new ReferencedXMIFactoryImpl();
		reg.getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, factory);
		// add xmi because other plugins are registering it globally
		reg.getExtensionToFactoryMap().put("xmi", factory); //$NON-NLS-1$
		aResourceSet.setResourceFactoryRegistry(reg);
		aResourceSet.getSynchronizer().addExtender(this); //added so we can be informed of closes
		// to the project.
		startListeningToResourceSet();
	}

	public static String getCacheID(String editModelID, Map params) {
		return EditModelRegistry.getInstance().getCacheID(editModelID, params);
	}

	/**
	 * This is the API that clients should use when they have an intent to modify a particular
	 * resource. You should only access the resources through the J2EEEditModel that is returned by
	 * this method if you have the intent to modify.
	 * 
	 * @see J2EEEditModel
	 */
	public final EditModel getEditModelForWrite(String editModelID, Object accessorKey, Map params) {
		EditModel editModel = getExistingEditModel(editModelID, params, false);
		if (editModel == null) {
			editModel = createEditModelForWrite(editModelID, params);
			cacheEditModel(editModel, params);
		}
		editModel.access(accessorKey);
		return editModel;
	}

	/**
	 * This is the API that clients should use when they want to read a group of resources that are
	 * normally managed by the edit model with
	 * 
	 * @aKey. You should only access the resources through the J2EEEditModel that is returned by
	 *        this method. You must call releaseEditModel(...) when you are finished with the edit
	 *        model.
	 * @see J2EEEditModel
	 */
	public final EditModel getEditModelForRead(String editModelID, Object accessorKey, Map params) {
		EditModel editModel = getExistingEditModel(editModelID, params, true);
		if (editModel == null) {
			editModel = createEditModelForRead(editModelID, params);
			cacheEditModel(editModel, params);
		}
		editModel.access(accessorKey);
		return editModel;
	}

	/**
	 * This is the API that clients should use when they have an intent to modify a particular
	 * resource. You should only access the resources through the J2EEEditModel that is returned by
	 * this method if you have the intent to modify.
	 * 
	 * @see J2EEEditModel
	 */
	public final EditModel getEditModelForWrite(String editModelID, Object accessorKey) {
		return getEditModelForWrite(editModelID, accessorKey, null);
	}

	/**
	 * This is the API that clients should use when they want to read a group of resources that are
	 * normally managed by the edit model with
	 * 
	 * @aKey. You should only access the resources through the J2EEEditModel that is returned by
	 *        this method. You must call releaseEditModel(...) when you are finished with the edit
	 *        model.
	 * @see J2EEEditModel
	 */
	public final EditModel getEditModelForRead(String editModelID, Object accessorKey) {
		return getEditModelForRead(editModelID, accessorKey, null);
	}



	private EditModel getExistingEditModel(String editModelID, Map params, boolean isReadOnly) {
		EditModel editModel = null;
		if (isReadOnly) {
			editModel = (EditModel) this.readOnlyModels.get(getCacheID(editModelID, params));
		} else {
			editModel = (EditModel) this.editableModels.get(getCacheID(editModelID, params));
		}
		return editModel;
	}

	/**
	 * Subclasses may override to return the appropriate read-only J2EEEditModel.
	 */
	protected EditModel createEditModelForRead(String editModelID, Map params) {
		return EditModelRegistry.getInstance().createEditModelForRead(editModelID, this, params);
	}

	/**
	 * Subclasses may override to return the appropriate J2EEEditModel.
	 */
	protected EditModel createEditModelForWrite(String editModelID, Map params) {
		return EditModelRegistry.getInstance().createEditModelForWrite(editModelID, this, params);
	}

	/**
	 * Insert the method's description here. Creation date: (4/16/2001 12:25:39 PM)
	 * 
	 * @return java.util.List
	 */
	public void cacheEditModel(EditModel editModel, Map params) {
		editModel.setParams(params);
		if (editModel.isReadOnly())
			this.readOnlyModels.put(getCacheID(editModel.getEditModelID(), params), editModel);
		else
			this.editableModels.put(getCacheID(editModel.getEditModelID(), params), editModel);
	}

	protected void discardAllEditModels() {
		discardModels(readOnlyModels.values());
		discardModels(editableModels.values());
	}

	private void discardModels(Collection editModels) {
		if (editModels != null && !editModels.isEmpty()) {
			//Make a copy for safety against concurrent modification
			Iterator it = new ArrayList(editModels).iterator();
			while (it.hasNext()) {
				((EditModel) it.next()).dispose();
			}
		}
	}

	public void removeEditModel(EditModel editModel, boolean readOnly) {
		//The best way would be to recompute the cache id, but we don't care
		//because the edit model should only be cached once anyway
		if (readOnly)
			readOnlyModels.values().remove(editModel);
		else
			editableModels.values().remove(editModel);
	}

	/**
	 * Notify all editModels of the change.
	 */
	protected void notifyEditModels(EditModelEvent anEvent) {
		if (anEvent == null)
			return;
		List aList = new ArrayList();
		aList.addAll(readOnlyModels.values());
		aList.addAll(editableModels.values());
		EditModel editModel;
		for (int i = 0; i < aList.size(); i++) {
			editModel = (EditModel) aList.get(i);
			try {
				editModel.resourceChanged(anEvent);
			} catch (Exception e) {
				Logger.getLogger().logError(e);
			}
		}
	}

	protected boolean shouldNotifyEditModels() {
		return !this.readOnlyModels.isEmpty() || !this.editableModels.isEmpty();
	}

	protected Adapter getResourceSetListener() {
		if (resourceSetListener == null)
			resourceSetListener = new ResourceSetListener();
		return resourceSetListener;
	}


	protected class ResourceSetListener extends AdapterImpl {
		/*
		 * @see Adapter#notifyChanged(new ENotificationImpl((InternalEObject)Notifier,
		 *      int,(EStructuralFeature) EObject, Object, Object, int))
		 */
		public void notifyChanged(Notification notification) {
			switch (notification.getEventType()) {
				case Notification.ADD :
					addedResource((Resource) notification.getNewValue());
					break;
				case Notification.REMOVE :
					removedResource((Resource) notification.getOldValue());
					break;
				case Notification.REMOVE_MANY :
					removedResources((List) notification.getOldValue());
					break;
			}
		}
	}

	/**
	 * Notify all editModels of the change.
	 */
	public void addedResource(Resource addedResource) {
		if (defaultToMOF5Compatibility && (addedResource != null) && (addedResource instanceof CompatibilityXMIResource))
			((CompatibilityXMIResource) addedResource).setFormat(CompatibilityXMIResource.FORMAT_MOF5);
		if (shouldNotifyEditModels()) {
			EditModelEvent event = new EditModelEvent(EditModelEvent.ADDED_RESOURCE, null);
			event.addResource(addedResource);
			notifyEditModels(event);
		}
	}

	/**
	 * Notify all editModels of the change.
	 */
	public void removedResource(Resource removedResource) {
		if (shouldNotifyEditModels()) {
			EditModelEvent event = new EditModelEvent(EditModelEvent.REMOVED_RESOURCE, null);
			event.addResource(removedResource);
			notifyEditModels(event);
		}
	}

	/**
	 * Notify all editModels of the change.
	 */
	public void removedResources(List removedResources) {
		if (shouldNotifyEditModels()) {
			EditModelEvent event = new EditModelEvent(EditModelEvent.REMOVED_RESOURCE, null);
			event.addResources(removedResources);
			notifyEditModels(event);
		}
	}

	protected void startListeningToResourceSet() {
		ResourceSet set = getResourceSet();
		if (set != null)
			set.eAdapters().add(getResourceSetListener());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.emfworkbench.ISynchronizerExtender#projectChanged(org.eclipse.core.resources.IResourceDelta)
	 */
	public void projectChanged(IResourceDelta delta) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.emfworkbench.ISynchronizerExtender#projectClosed()
	 */
	public void projectClosed() {
		discardAllEditModels();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.emfworkbench.EMFWorkbenchContextBase#createURIConverter(org.eclipse.wst.common.emfworkbench.ProjectResourceSet)
	 */
	protected WorkbenchURIConverter createURIConverter(ProjectResourceSet aResourceSet) {
		return new CompatibilityWorkbenchURIConverterImpl(getProject(), aResourceSet.getSynchronizer());
	}

	protected ProjectResourceSet createResourceSet() {
		if (project == null)
			throw new IllegalStateException("Attempt to create resource set with null project"); //$NON-NLS-1$
		return new ProjectResourceSetEditImpl(project);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.emfworkbench.EMFWorkbenchContextBase#deleteFile(org.eclipse.emf.ecore.resource.Resource)
	 */
	public void deleteFile(Resource resource) {
		try {
			WorkbenchResourceHelper.deleteResource(resource);
		} catch (CoreException ex) {
			Logger.getLogger().logError(ex);
		}

	}

	/**
	 * @return Returns the defaultToMOF5Compatibility.
	 */
	public boolean isDefaultToMOF5Compatibility() {
		return defaultToMOF5Compatibility;
	}

	/**
	 * @param defaultToMOF5Compatibility
	 *            The defaultToMOF5Compatibility to set.
	 */
	public void setDefaultToMOF5Compatibility(boolean defaultToMOF5Compatibility) {
		this.defaultToMOF5Compatibility = defaultToMOF5Compatibility;
	}

}