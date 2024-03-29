/*******************************************************************************
 * Copyright (c) 2003, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emfworkbench.edit;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jem.util.RegistryReader;
import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext;
import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchEditResourceHandler;
import org.eclipse.wst.common.internal.emfworkbench.integration.EMFWorkbenchEditPlugin;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModel;
import org.eclipse.wst.common.internal.emfworkbench.integration.IEditModelFactory;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author mdelder
 */
public class EditModelRegistry extends RegistryReader {

	private final static EditModelRegistry INSTANCE =  new EditModelRegistry();

	private final Map factoryConfigurations = new HashMap();
	private static boolean initialized = false;
	

	public static final String EDIT_MODEL_ELEMENT = "editModel"; //$NON-NLS-1$
	public static final String EDIT_MODEL_RESOURCE_EXTENSION = "resourceExtension"; //$NON-NLS-1$
	public static final String EDIT_MODEL_RESOURCE_EXTENSION_NAME = "name"; //$NON-NLS-1$
	public static final String EDIT_MODEL_ID_ATTR = "editModelID"; //$NON-NLS-1$
	public static final String FACTORY_CLASS_ATTR = "factoryClass"; //$NON-NLS-1$
	public static final String PARENT_MODEL_ATTR = "parentModelID"; //$NON-NLS-1$



	public static final String LOAD_UNKNOWN_RESOURCES_ATTR = "loadUnknownResourcesAsReadOnly"; //$NON-NLS-1$

	protected EditModelRegistry() {
		super(EMFWorkbenchEditPlugin.ID, EMFWorkbenchEditPlugin.EDIT_MODEL_FACTORIES_EXTENSION_POINT);
	}

	public static EditModelRegistry getInstance() {
		if(isInitialized()) 
			return INSTANCE;
		synchronized(INSTANCE) {
			if(!isInitialized()) {
				INSTANCE.readRegistry();
				initialized = true;
			}
		} 
		return INSTANCE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.RegistryReader#readElement(org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	public boolean readElement(IConfigurationElement element) {
		/*
		 * The EditModel Extension Point defines Configuration elements named "editModel" with
		 * attributes "editModelID" and "factoryClass"
		 */
		boolean result = false;
		if (element.getName().equals(EDIT_MODEL_ELEMENT)) {
			String editModelID = element.getAttribute(EDIT_MODEL_ID_ATTR);
			if (editModelID != null) {
				this.factoryConfigurations.put(editModelID, new EditModelInfo(editModelID, element));
				result = true;
			}
		}
		return result;
	}

	public String getCacheID(String editModelID, Map params) {
		IEditModelFactory factory = getEditModelFactoryByKey(editModelID);
		return factory.getCacheID(editModelID, params);
	}

	public EditModel createEditModelForRead(String editModelID, EMFWorkbenchContext context, Map params) {
		return getEditModelFactoryByKey(editModelID).createEditModelForRead(editModelID, context, params);
	}

	public EditModel createEditModelForWrite(String editModelID, EMFWorkbenchContext context, Map params) {
		return getEditModelFactoryByKey(editModelID).createEditModelForWrite(editModelID, context, params);
	}

	public Collection getEditModelResources(String editModelID) {
		Collection resources = new TreeSet();

		EditModelInfo nextEditModelInfo = getEditModelInfoById(editModelID);

		String parentModelID = null;
		Map visitedEditModels = new HashMap();
		/* collect the resources from the parents */
		while (nextEditModelInfo != null && (parentModelID = nextEditModelInfo.getParentModelID()) != null) {
			if (visitedEditModels.containsKey(parentModelID))
				throw new IllegalStateException(EMFWorkbenchEditResourceHandler.getString(EMFWorkbenchEditResourceHandler.EditModelRegistry_ERROR_0, new Object[]{editModelID}));
			visitedEditModels.put(parentModelID, null);
			resources.addAll(getAllEditModelResources(parentModelID));
			nextEditModelInfo = getEditModelInfoById(parentModelID);
		}

		/* Get the resources for the actual edit model id */
		resources.addAll(getAllEditModelResources(editModelID));

		return resources;
	}
	
	public Collection getEditModelExtensions(String editModelID) { 
		Collection extensions = new TreeSet();
		
		EditModelInfo nextEditModelInfo = getEditModelInfoById(editModelID);

		String parentModelID = null;
		Map visitedEditModels = new HashMap();
		/* collect the resources from the parents */
		while(nextEditModelInfo != null && (parentModelID = nextEditModelInfo.getParentModelID()) != null) {
			if(visitedEditModels.containsKey(parentModelID)) 
				throw new IllegalStateException(EMFWorkbenchEditResourceHandler.getString(EMFWorkbenchEditResourceHandler.EditModelRegistry_ERROR_0,new Object [] {editModelID})); 
			visitedEditModels.put(parentModelID, null);
			extensions.addAll(getAllEditModelExtensions(parentModelID));
			nextEditModelInfo = getEditModelInfoById(parentModelID);
		}
		
		/* Get the resources for the actual edit model id */
		extensions.addAll(getAllEditModelExtensions(editModelID));
		
		return extensions;
	}
	
	public IEditModelFactory findEditModelFactoryByKey(Object editModelID) {
		IEditModelFactory factory = null;
		EditModelInfo editMdlInfo = (EditModelInfo) factoryConfigurations.get(editModelID);
		if (editMdlInfo != null)
			factory = editMdlInfo.getEditModelFactory();
		return factory; 
	}
	
	public IEditModelFactory findEditModelFactoryByProject(IProject project) {
		IFacetedProject facetedProject = null;
		try {
			facetedProject = ProjectFacetsManager.create(project);
		} catch (Exception e) {
			return null;
		}
		if (facetedProject == null) return null;
		Iterator keys = factoryConfigurations.keySet().iterator();
		while (keys.hasNext()) {
			Object key = keys.next();
			if (key instanceof String) {
				try {
					IProjectFacet projectFacet = ProjectFacetsManager.getProjectFacet((String)key);
					if (projectFacet != null && facetedProject.hasProjectFacet(projectFacet))
						return findEditModelFactoryByKey(key);
				} catch (Exception e) {
					continue;
				}
				
			}
		}
		
		return null;
	}

	protected Collection getAllEditModelResources(String editModelID) {
		Collection resources = new ArrayList();
		resources.addAll(getLocalEditModelResources(editModelID));
		resources.addAll(getExtendedEditModelResources(editModelID));
		return resources;
	}
	
	protected Collection getAllEditModelExtensions(String editModelID) {
		Collection resources = new ArrayList();
		resources.addAll(getLocalEditModelExtensions(editModelID));
		return resources;
	}

	protected Collection getLocalEditModelResources(String editModelID) { 
		EditModelInfo editMdlInfo = getEditModelInfoById(editModelID);
  		return (editMdlInfo != null) ? editMdlInfo.getEditModelResources() : Collections.EMPTY_LIST; 
	}
	protected Collection getLocalEditModelExtensions(String editModelID) { 
		EditModelInfo editMdlInfo = getEditModelInfoById(editModelID);
  		return (editMdlInfo != null) ? editMdlInfo.getEditModelExtensions() : Collections.EMPTY_LIST; 
	}

	protected Collection getExtendedEditModelResources(String editModelID) {
		return EditModelExtensionRegistry.getInstance().getEditModelResources(editModelID);
	}

	/**
	 * @param editModelKey
	 *            the editModelID of a given EditModelFactory defined in the Extension Point
	 * @throws IllegalArgumentException
	 *             if a IEditModelFactory cannot be found for the given ID.
	 * @return the EditModelFactory associated with a given EditModelID
	 */
	protected IEditModelFactory getEditModelFactoryByKey(Object editModelID) {
		IEditModelFactory factory = null;
		EditModelInfo editMdlInfo = getEditModelInfoById(editModelID);
		if (editMdlInfo != null)
			factory = editMdlInfo.getEditModelFactory();
		else
			throw new IllegalArgumentException(EMFWorkbenchEditResourceHandler.getString(EMFWorkbenchEditResourceHandler.EditModelRegistry_ERROR_2, new Object[]{editModelID}));

		return factory;
	}
	
	/**
     * @param editModelID
     * @return
     */
    protected EditModelInfo getEditModelInfoById(Object editModelID) {
        waitForInitializationIfNecessary();
        return (EditModelInfo) factoryConfigurations.get(editModelID);
    }
    
    /**
     * If we are not initialized, block until the INSTANCE is released ( from getInstance())
     */
    private void waitForInitializationIfNecessary() {
        /* We only need to acquire the semaphore (INSTANCE), we do not need 
         * to execute anything in this block. If the Registry is not initailized,
         * then it will block until the semaphore is released (from getInstance()),
         * and then release it and return immediately. 
         */
		if(!isInitialized()) 
		    synchronized(INSTANCE) { }
    }

	public class EditModelInfo {

		private String editModelID = null;
		private IConfigurationElement configurationElement = null;

		private IEditModelFactory factory = null;
		private List editModelResources = null;
		private List editModelExtensions = null;

		private String parentModelID = null;

		private String tostringCache = null;

		public EditModelInfo(String editModelID, IConfigurationElement configurationElement) {

			this.configurationElement = configurationElement;
			this.editModelID = editModelID;
			this.parentModelID = this.configurationElement.getAttribute(PARENT_MODEL_ATTR);
		}


		public List getEditModelResources() {
			/* this method is guarded */
			initializeResources();
			return editModelResources;
		}

		public IEditModelFactory getEditModelFactory() {
//			 Do not block if the factory is not null
			if (this.factory == null) {
			  synchronized (this) {
			      // another thread could have already initialized the factory
			      // while this thread was waiting to enter the sync block
			      if(this.factory == null) {
					if (this.configurationElement != null) {
						try {
							this.factory = (IEditModelFactory) this.configurationElement.createExecutableExtension(FACTORY_CLASS_ATTR);
							Boolean value = Boolean.valueOf(this.configurationElement.getAttribute(LOAD_UNKNOWN_RESOURCES_ATTR));
							this.factory.setLoadKnownResourcesAsReadOnly(value.booleanValue());
							discardConfigurationElementIfNecessary();
						} catch (CoreException e) {
							EMFWorkbenchEditPlugin.logError(e);
						}
					} else {
						EMFWorkbenchEditPlugin.logError(EMFWorkbenchEditResourceHandler.EditModelRegistry_ERROR_1);
					}
			      }
			    }
			}
			return this.factory;
		}

		private synchronized void initializeResources() {

			if (editModelResources == null) {
				if (configurationElement != null) {

					editModelResources = new ArrayList();

					IConfigurationElement[] resources = configurationElement.getChildren(EditModelResource.EDIT_MODEL_RESOURCE_ELEMENT);
					IConfigurationElement[] resExtensions = configurationElement.getChildren(EDIT_MODEL_RESOURCE_EXTENSION);
					// set the configurationElement to null- keeps code from reentering
					discardConfigurationElementIfNecessary();
					for (int j = 0; j < resources.length; j++) {
						editModelResources.add(new EditModelResource(resources[j]));
					}
					if (resExtensions == null || resExtensions.length == 0) {
						editModelExtensions = Collections.EMPTY_LIST;
					} else {
						editModelExtensions = new ArrayList();
						for (int i = 0; i < resExtensions.length; i++) {
							String extension = resExtensions[i].getAttribute(EDIT_MODEL_RESOURCE_EXTENSION_NAME);
							editModelExtensions.add(extension);
						}
					}
				} else {
					editModelResources = Collections.EMPTY_LIST;
					editModelExtensions = Collections.EMPTY_LIST;
				}
			}
		}

		private void discardConfigurationElementIfNecessary() {
			if (this.editModelResources != null && this.factory != null)
				this.configurationElement = null;
		}

		@Override
		public String toString() {
			if (tostringCache == null)
				tostringCache = "EditModelID: {" + this.editModelID + "}, Parent Model ID {" + this.parentModelID + "}, Configuration Element: [" + this.configurationElement + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$
			return tostringCache;
		}

		/**
		 * @return Returns the parentModelID.
		 */
		public String getParentModelID() {
			return parentModelID;
		}
		
		public List getEditModelExtensions() {
			/* this method is guarded */
			initializeResources();
			return editModelExtensions;
		}

	}
	/**
	 * @return Returns the initialized.
	 */
	protected static boolean isInitialized() {
		return initialized;
	}
	
	public String[] getRegisteredEditModelIDs() {
		return (String[]) factoryConfigurations.keySet().toArray(new String[factoryConfigurations.keySet().size()]);
	}
}
