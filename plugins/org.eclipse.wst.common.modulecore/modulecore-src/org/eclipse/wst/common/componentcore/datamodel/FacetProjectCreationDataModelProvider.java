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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
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

	protected class FacetDataModelMapImpl extends HashMap implements FacetDataModelMap, IDataModelListener {
		private static final long serialVersionUID = 1L;
		private boolean supressNotification = false;

		public void add(IDataModel facetDataModel) {
			put(facetDataModel.getProperty(IFacetDataModelProperties.FACET_ID), facetDataModel);
		}

		public IDataModel getFacetDataModel(String facetID) {
			return (IDataModel)get(facetID);
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
					((IDataModel)lastValue).setProperty(FacetInstallDataModelProvider.MASTER_PROJECT_DM, null);
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
			IDataModel dm = (IDataModel) super.remove(key);
			dm.removeListener(this);
			return dm;
		}

		public void propertyChanged(DataModelEvent event) {
			if (event.getPropertyName().equals(FACET_PROJECT_NAME)) {
				if (containsValue(event.getDataModel())) {
					getDataModel().setProperty(FACET_PROJECT_NAME, event.getProperty());
				} else {
					event.getDataModel().removeListener(this);
				}
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
		}
		return super.propertySet(propertyName, propertyValue);
	}

	public Object getDefaultProperty(String propertyName) {
		if (FACET_DM_MAP.equals(propertyName)) {
			Object obj = new FacetDataModelMapImpl();
			setProperty(FACET_DM_MAP, obj);
			return obj;
		} 
//		else if (FACET_RUNTIME.equals(propertyName)) {
//			DataModelPropertyDescriptor[] runtimes = getValidPropertyDescriptors(FACET_RUNTIME);
//			return runtimes.length > 0 ? runtimes[runtimes.length - 1].getPropertyValue() : null;
//		}
		return super.getDefaultProperty(propertyName);
	}
	
	public DataModelPropertyDescriptor getPropertyDescriptor(String propertyName) {
		if(FACET_RUNTIME.equals(propertyName)){
			IRuntime runtime = (IRuntime)getProperty(propertyName);
			if(null != runtime){
				return new DataModelPropertyDescriptor(runtime, runtime.getName());
			}
		}
		return super.getPropertyDescriptor(propertyName);
	}
	

	public DataModelPropertyDescriptor[] getValidPropertyDescriptors(String propertyName) {
		if (FACET_RUNTIME.equals(propertyName)) {
			Set projectFacetVersions = new HashSet();
			Map facetDMs = (Map) getProperty(FACET_DM_MAP);
			for (Iterator iterator = facetDMs.values().iterator(); iterator.hasNext();) {
				IDataModel facetDataModel = (IDataModel) iterator.next();
				projectFacetVersions.add(facetDataModel.getProperty(IFacetDataModelProperties.FACET_VERSION));
			}
			Set runtimes = RuntimeManager.getRuntimes(projectFacetVersions);
			DataModelPropertyDescriptor [] descriptors = new DataModelPropertyDescriptor[runtimes.size()];
			Iterator iterator = runtimes.iterator();
			for(int i=0; i<descriptors.length; i++){
				IRuntime runtime = (IRuntime)iterator.next();
				descriptors[i]=new DataModelPropertyDescriptor(runtime, runtime.getName());
			}
			return descriptors;
		}
		return super.getValidPropertyDescriptors(propertyName);
	}

	public IStatus validate(String propertyName) {
		if (FACET_PROJECT_NAME.equals(propertyName)) {
			return validate(IProjectCreationPropertiesNew.PROJECT_NAME);
		}
		return super.validate(propertyName);
	}

	public IDataModelOperation getDefaultOperation() {
		return new FacetProjectCreationOperation(model);
	}

}
