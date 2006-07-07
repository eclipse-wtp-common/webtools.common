/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.operation.FacetProjectCreationOperation;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelListener;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.internal.operations.IProjectCreationPropertiesNew;
import org.eclipse.wst.common.frameworks.internal.operations.ProjectCreationDataModelProviderNew;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonMessages;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonPlugin;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;

public class FacetProjectCreationDataModelProvider extends AbstractDataModelProvider implements IFacetProjectCreationDataModelProperties {

	public FacetProjectCreationDataModelProvider() {
		super();
	}

	public Set getPropertyNames() {
		Set names = super.getPropertyNames();
		names.add(FACET_PROJECT_NAME);
		names.add(FACET_DM_MAP);
		names.add(FACET_ACTION_MAP);
		names.add(FACET_RUNTIME);
		return names;
	}

	public void init() {
		super.init();
		IDataModel projectDataModel = DataModelFactory.createDataModel(new ProjectCreationDataModelProviderNew());
		projectDataModel.addListener(new IDataModelListener() {
			public void propertyChanged(DataModelEvent event) {
				if (IProjectCreationPropertiesNew.PROJECT_NAME.equals(event.getPropertyName())) {
					getDataModel().setProperty(FACET_PROJECT_NAME, event.getProperty());
				}
			}
		});
		model.addNestedModel(NESTED_PROJECT_DM, projectDataModel);
	}

	protected class FacetActionMapImpl extends HashMap implements FacetActionMap {
		private static final long serialVersionUID = 1L;
		private boolean supressNotification = false;

		public void add(Action action) {
			put(action.getProjectFacetVersion().getProjectFacet().getId(), action);
		}

		public Action getAction(String facetID) {
			return (Action) get(facetID);
		}

		public void clear() {
			try {
				supressNotification = true;
				super.clear();
			} finally {
				supressNotification = false;
				getDataModel().notifyPropertyChange(FACET_ACTION_MAP, IDataModel.VALUE_CHG);
			}
		}

		public Object remove(Object key) {
			try {
				return super.remove(key);
			} finally {
				if (!supressNotification) {
					getDataModel().notifyPropertyChange(FACET_ACTION_MAP, IDataModel.VALUE_CHG);
				}
			}
		}

		public Object put(Object key, Object value) {
			try {
				return super.put(key, value);
			} finally {
				if (!supressNotification) {
					getDataModel().notifyPropertyChange(FACET_ACTION_MAP, IDataModel.VALUE_CHG);
				}
			}
		}

		public void putAll(Map m) {
			try {
				supressNotification = true;
				super.putAll(m);
			} finally {
				supressNotification = false;
				getDataModel().notifyPropertyChange(FACET_ACTION_MAP, IDataModel.VALUE_CHG);
			}
		}
	}

	protected class FacetDataModelMapImpl extends HashMap implements FacetDataModelMap, IDataModelListener {
		private static final long serialVersionUID = 1L;
		private boolean supressNotification = false;

		public void add(IDataModel facetDataModel) {
			put(facetDataModel.getProperty(IFacetDataModelProperties.FACET_ID), facetDataModel);
		}

		public IDataModel getFacetDataModel(String facetID) {
			return (IDataModel) get(facetID);
		}

		public void clear() {
			try {
				supressNotification = true;
				for (Iterator iterator = values().iterator(); iterator.hasNext();) {
					((IDataModel) iterator.next()).removeListener(this);
				}
				super.clear();
			} finally {
				supressNotification = false;
				getDataModel().notifyPropertyChange(FACET_DM_MAP, IDataModel.VALUE_CHG);
			}
		}

		public Object put(Object key, Object value) {
			try {
				IDataModel dm = (IDataModel) value;
				Object lastValue = super.put(key, value);
				if (lastValue != null) {
					((IDataModel) lastValue).removeListener(this);
					((IDataModel) lastValue).setProperty(FacetInstallDataModelProvider.MASTER_PROJECT_DM, null);
				}
				dm.setProperty(FACET_PROJECT_NAME, getDataModel().getProperty(FACET_PROJECT_NAME));
				dm.setProperty(FacetInstallDataModelProvider.MASTER_PROJECT_DM, FacetProjectCreationDataModelProvider.this.model);
				dm.addListener(this);
				return lastValue;
			} finally {
				if (!supressNotification) {
					getDataModel().notifyPropertyChange(FACET_DM_MAP, IDataModel.VALUE_CHG);
				}
			}
		}

		public void putAll(Map m) {
			try {
				supressNotification = true;
				super.putAll(m);
			} finally {
				supressNotification = false;
				getDataModel().notifyPropertyChange(FACET_DM_MAP, IDataModel.VALUE_CHG);
			}
		}

		public Object remove(Object key) {
			try {
				IDataModel dm = (IDataModel) super.remove(key);
				dm.removeListener(this);
				return dm;
			} finally {
				if (!supressNotification) {
					getDataModel().notifyPropertyChange(FACET_DM_MAP, IDataModel.VALUE_CHG);
				}
			}
		}

		public void propertyChanged(DataModelEvent event) {
			if (event.getPropertyName().equals(FACET_PROJECT_NAME)) {
				if (containsValue(event.getDataModel())) {
					getDataModel().setProperty(FACET_PROJECT_NAME, event.getProperty());
				} else {
					event.getDataModel().removeListener(this);
				}
			} else if (event.getPropertyName().equals(FACET_RUNTIME)) {
				if (containsValue(event.getDataModel())) {
					if (event.getFlag() == IDataModel.VALID_VALUES_CHG) {
						getDataModel().notifyPropertyChange(FACET_RUNTIME, IDataModel.VALID_VALUES_CHG);
					} else if(event.getFlag() == IDataModel.ENABLE_CHG) {
						getDataModel().notifyPropertyChange(FACET_RUNTIME, IDataModel.ENABLE_CHG);
					} else {
						getDataModel().setProperty(FACET_RUNTIME, event.getProperty());
					}
				} else {
					event.getDataModel().removeListener(this);
				}
			} else if (event.getPropertyName().equals(IFacetDataModelProperties.FACET_VERSION)) {
				getDataModel().notifyPropertyChange(FACET_RUNTIME, IDataModel.VALID_VALUES_CHG);
			}
		}

	}

	public boolean propertySet(String propertyName, Object propertyValue) {
		if (FACET_PROJECT_NAME.equals(propertyName)) {
			for (Iterator iterator = ((Map) getDataModel().getProperty(FACET_DM_MAP)).values().iterator(); iterator.hasNext();) {
				((IDataModel) iterator.next()).setProperty(FACET_PROJECT_NAME, propertyValue);
			}
			IDataModel projModel = model.getNestedModel(NESTED_PROJECT_DM);
			projModel.setProperty(IProjectCreationPropertiesNew.PROJECT_NAME, propertyValue);
		} else if (FACET_RUNTIME.equals(propertyName)) {
			IRuntime runtime = (IRuntime) propertyValue;
			for (Iterator iterator = ((Map) getDataModel().getProperty(FACET_DM_MAP)).values().iterator(); iterator.hasNext();) {
				IDataModel dm = (IDataModel) iterator.next();
				if (dm.isProperty(FACET_RUNTIME)) {
					dm.setProperty(FACET_RUNTIME, runtime);
				}
			}
			if (runtime != null) {
				Map facetDMs = (Map) getProperty(FACET_DM_MAP);

				for (Iterator iterator = facetDMs.values().iterator(); iterator.hasNext();) {
					IDataModel facetDataModel = (IDataModel) iterator.next();
					IProjectFacet facet = ProjectFacetsManager.getProjectFacet((String) facetDataModel.getProperty(IFacetDataModelProperties.FACET_ID));

					try {
						IDataModel facetModel = ((FacetDataModelMap) facetDMs).getFacetDataModel(facet.getId());
						IProjectFacetVersion oldVersion = (IProjectFacetVersion) facetModel.getProperty(IFacetDataModelProperties.FACET_VERSION);
						IProjectFacetVersion newVersion = facet.getLatestSupportedVersion(runtime);
						if (newVersion != null && (oldVersion == null || oldVersion.getVersionString().compareTo(newVersion.getVersionString()) > 0 || !runtime.supports(oldVersion))) {
							facetModel.setProperty(IFacetDataModelProperties.FACET_VERSION, newVersion);
						}
					} catch (CoreException e) {
						Logger.getLogger().logError(e);
					}
				}
			}
		}
		return super.propertySet(propertyName, propertyValue);
	}

	public Object getDefaultProperty(String propertyName) {
		if (FACET_DM_MAP.equals(propertyName)) {
			Object obj = new FacetDataModelMapImpl();
			setProperty(FACET_DM_MAP, obj);
			return obj;
		} else if (FACET_ACTION_MAP.equals(propertyName)) {
			Object obj = new FacetActionMapImpl();
			setProperty(FACET_ACTION_MAP, obj);
			return obj;
		}
		return super.getDefaultProperty(propertyName);
	}

	public DataModelPropertyDescriptor getPropertyDescriptor(String propertyName) {
		if (FACET_RUNTIME.equals(propertyName)) {
			IRuntime runtime = (IRuntime) getProperty(propertyName);
			if (null != runtime) {
				return new DataModelPropertyDescriptor(runtime, runtime.getName());
			}
			return new DataModelPropertyDescriptor(null, WTPCommonPlugin.getResourceString(WTPCommonMessages.RUNTIME_NONE, null));
		}
		return super.getPropertyDescriptor(propertyName);
	}

	public DataModelPropertyDescriptor[] getValidPropertyDescriptors(String propertyName) {
		if (FACET_RUNTIME.equals(propertyName)) {
			Set projectFacets = new HashSet();
			Map facetDMs = (Map) getProperty(FACET_DM_MAP);
			for (Iterator iterator = facetDMs.values().iterator(); iterator.hasNext();) {
				IDataModel facetDataModel = (IDataModel) iterator.next();
				if (facetDataModel.getBooleanProperty(IFacetDataModelProperties.SHOULD_EXECUTE)) {
					IProjectFacet facet = ProjectFacetsManager.getProjectFacet((String) facetDataModel.getProperty(IFacetDataModelProperties.FACET_ID));
					projectFacets.add(facet);
				}
			}
			Map facetActions = (Map) getProperty(FACET_ACTION_MAP);
			for (Iterator iterator = facetActions.values().iterator(); iterator.hasNext();) {
				IFacetedProject.Action action = (IFacetedProject.Action) iterator.next();
				projectFacets.add(action.getProjectFacetVersion().getProjectFacet());
			}

			Set runtimes = RuntimeManager.getRuntimes();
			ArrayList list = new ArrayList();

			for (Iterator it = runtimes.iterator(); it.hasNext();) {
				IRuntime rt = (IRuntime) it.next();

				// add this runtime in the list only if this runtime supports all of the facets
				// in the project

				boolean supportsFactet = true;
				for (Iterator facetIt = projectFacets.iterator(); facetIt.hasNext();) {
					IProjectFacet facet = (IProjectFacet) facetIt.next();
					if (!rt.supports(facet)) {
						supportsFactet = false;
					}
				}
				if (supportsFactet) {
					list.add(rt);
				}
			}

			DataModelPropertyDescriptor[] descriptors = new DataModelPropertyDescriptor[list.size() + 1];
			Iterator iterator = list.iterator();
			for (int i = 0; i < descriptors.length - 1; i++) {
				IRuntime runtime = (IRuntime) iterator.next();
				descriptors[i] = new DataModelPropertyDescriptor(runtime, runtime.getName());
			}
			descriptors[descriptors.length - 1] = new DataModelPropertyDescriptor(null, WTPCommonPlugin.getResourceString(WTPCommonMessages.RUNTIME_NONE, null));
			return descriptors;
		}
		return super.getValidPropertyDescriptors(propertyName);
	}

	public IStatus validate(String propertyName) {
		if (FACET_PROJECT_NAME.equals(propertyName)) {
			IDataModel projModel = model.getNestedModel(NESTED_PROJECT_DM);
			return projModel.validateProperty(IProjectCreationPropertiesNew.PROJECT_NAME);
		}
		return super.validate(propertyName);
	}

	public IDataModelOperation getDefaultOperation() {
		return new FacetProjectCreationOperation(model);
	}

}
