/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.datamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelListener;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;
import org.eclipse.wst.common.frameworks.internal.WTPResourceHandler;

public final class DataModelImpl implements IDataModel, IDataModelListener {

	private static final String PROPERTY_NOT_LOCATED_ = WTPResourceHandler.getString("20"); //$NON-NLS-1$
	private static final String NESTED_MODEL_NOT_LOCATED = WTPResourceHandler.getString("21"); //$NON-NLS-1$

	private static final DataModelPropertyDescriptor[] NO_DESCRIPTORS = new DataModelPropertyDescriptor[0];

	private Collection basePropertyNames;
	private Collection allPropertyNames;
	private Collection nestedPropertyNames; // lazily initialzed when nested models added
	private Map propertyValues = new Hashtable();
	private Map nestedModels;
	private Set nestingModels;
	private List listeners;

	private IDataModelProvider provider;

	public DataModelImpl(IDataModelProvider dataModelProvider) {
		init(dataModelProvider);
	}

	private void init(IDataModelProvider dataModelProvider) {
		this.provider = dataModelProvider;
		dataModelProvider.setDataModel(this);
		Collection propertyNames = dataModelProvider.getPropertyNames();
		HashSet properties = new HashSet();
		properties.addAll(propertyNames);

		properties.add(IDataModelProperties.ALLOW_EXTENSIONS);
		properties.add(IDataModelProperties.RESTRICT_EXTENSIONS);
		basePropertyNames = Collections.unmodifiableCollection(properties);
		allPropertyNames = new HashSet();
		allPropertyNames.addAll(basePropertyNames);
		dataModelProvider.init();
	}

	public boolean isBaseProperty(String propertyName) {
		return basePropertyNames.contains(propertyName);
	}

	public Collection getBaseProperties() {
		return Collections.unmodifiableCollection(basePropertyNames);
	}

	public boolean isProperty(String propertyName) {
		return allPropertyNames.contains(propertyName);
	}

	public Collection getAllProperties() {
		return Collections.unmodifiableCollection(allPropertyNames);
	}

	public boolean isNestedProperty(String propertyName) {
		return null != nestedPropertyNames && nestedPropertyNames.contains(propertyName);
	}

	public Collection getNestedProperties() {
		return Collections.unmodifiableCollection(nestedPropertyNames);
	}

	private void checkValidPropertyName(String propertyName) {
		if (!isProperty(propertyName)) {
			throw new RuntimeException(PROPERTY_NOT_LOCATED_ + propertyName);
		}
	}

	private DataModelImpl getOwningDataModel(String propertyName) {
		checkValidPropertyName(propertyName);
		return searchNestedModels(propertyName);
	}

	private DataModelImpl searchNestedModels(String propertyName) {
		if (isBaseProperty(propertyName)) {
			return this;
		} else if (nestedModels != null) {
			DataModelImpl dataModel = null;
			Object[] keys = nestedModels.keySet().toArray();
			for (int i = 0; i < keys.length; i++) {
				dataModel = (DataModelImpl) nestedModels.get(keys[i]);
				if (dataModel.isProperty(propertyName)) {
					return dataModel.searchNestedModels(propertyName);
				}
			}
		}
		throw new RuntimeException(PROPERTY_NOT_LOCATED_ + propertyName);
	}

	public Object getProperty(String propertyName) {
		DataModelImpl dataModel = getOwningDataModel(propertyName);
		if (dataModel.propertyValues.containsKey(propertyName)) {
			return dataModel.propertyValues.get(propertyName);
		}
		return dataModel.provider.getDefaultProperty(propertyName);
	}

	public Object getDefaultProperty(String propertyName) {
		DataModelImpl dataModel = getOwningDataModel(propertyName);
		return dataModel.provider.getDefaultProperty(propertyName);
	}

	public int getIntProperty(String propertyName) {
		Object prop = getProperty(propertyName);
		if (prop == null)
			return -1;
		return ((Integer) prop).intValue();
	}

	public boolean getBooleanProperty(String propertyName) {
		Object prop = getProperty(propertyName);
		if (prop == null)
			return false;
		return ((Boolean) prop).booleanValue();
	}

	public String getStringProperty(String propertyName) {
		Object prop = getProperty(propertyName);
		if (prop == null)
			return ""; //$NON-NLS-1$
		return (String) prop;
	}

	public boolean isPropertySet(String propertyName) {
		DataModelImpl dataModel = getOwningDataModel(propertyName);
		return dataModel.propertyValues.containsKey(propertyName);
	}

	public boolean isPropertyEnabled(String propertyName) {
		DataModelImpl dataModel = getOwningDataModel(propertyName);
		return dataModel.provider.isPropertyEnabled(propertyName);
	}


	public void setProperty(String propertyName, Object propertyValue) {
		DataModelImpl dataModel = getOwningDataModel(propertyName);
		dataModel.internalSetProperty(propertyName, propertyValue);
	}

	private void internalSetProperty(String propertyName, Object propertyValue) {
		Object oldValue = propertyValues.get(propertyName);
		if (valueChanged(propertyValue, oldValue)) {
			if (null != propertyValue)
				propertyValues.put(propertyName, propertyValue);
			else if (propertyValues.containsKey(propertyName))
				propertyValues.remove(propertyName);
			if (provider.propertySet(propertyName, propertyValue)) {
				notifyPropertyChange(propertyName, DataModelEvent.VALUE_CHG);
			}
		}
	}

	private boolean valueChanged(Object o1, Object o2) {
		return o1 != o2 && ((o1 != null && !o1.equals(o2)) || !o2.equals(o1));
	}

	public void setIntProperty(String propertyName, int value) {
		setProperty(propertyName, new Integer(value));
	}

	public void setBooleanProperty(String propertyName, boolean value) {
		setProperty(propertyName, (value) ? Boolean.TRUE : Boolean.FALSE);
	}

	public void setStringProperty(String propertyName, String value) {
		setProperty(propertyName, value);
	}

	public boolean addNestedModel(String modelName, IDataModel dataModel) {
		if (this == dataModel) {
			return false;
		}
		if (null == nestedModels) {
			nestedModels = new Hashtable();
			nestedPropertyNames = new HashSet();
		}
		DataModelImpl nestedDataModel = (DataModelImpl) dataModel;
		if (null == nestedDataModel.nestingModels) {
			nestedDataModel.nestingModels = new HashSet();
		}
		if (nestedDataModel.nestingModels.contains(this)) {
			return false;
		}
		nestedDataModel.nestingModels.add(this);

		nestedModels.put(modelName, nestedDataModel);

		addNestedProperties(nestedDataModel.allPropertyNames);
		nestedDataModel.addListener(this);
		return true;
	}

	private void addNestedProperties(Collection nestedProperties) {
		boolean propertiesAdded = allPropertyNames.addAll(nestedProperties);
		propertiesAdded = nestedPropertyNames.addAll(nestedProperties) || propertiesAdded;
		// Pass the new properties up the nesting chain
		if (propertiesAdded && nestingModels != null) {
			Iterator iterator = nestingModels.iterator();
			while (iterator.hasNext()) {
				((DataModelImpl) iterator.next()).addNestedProperties(nestedProperties);
			}
		}
	}

	public Collection getNestedModels() {
		return nestedModels != null ? Collections.unmodifiableCollection(nestedModels.values()) : Collections.EMPTY_SET;
	}

	public Collection getNestedModelNames() {
		return nestedModels != null ? Collections.unmodifiableCollection(nestedModels.keySet()) : Collections.EMPTY_SET;
	}

	public Collection getNestingModels() {
		return nestingModels != null ? Collections.unmodifiableCollection(nestingModels) : Collections.EMPTY_SET;
	}

	public IDataModel removeNestedModel(String modelName) {
		if (!isNestedModel(modelName)) {
			return null;
		}
		DataModelImpl model = (DataModelImpl) nestedModels.remove(modelName);
		model.nestingModels.remove(this);
		removeNestedProperties(model.allPropertyNames);
		model.removeListener(this);
		if (nestedModels.isEmpty()) {
			nestedModels = null;
		}
		return model;
	}

	private void removeNestedProperties(Collection nestedProperties) {
		Iterator iterator = nestedProperties.iterator();
		String property = null;
		boolean keepProperty = false;
		Set nestedPropertiesToRemove = null;
		while (iterator.hasNext()) {
			keepProperty = false;
			property = (String) iterator.next();
			if (basePropertyNames.contains(property)) {
				keepProperty = true;
			}
			if (!keepProperty && nestedModels != null) {
				Iterator nestedModelsIterator = nestedModels.values().iterator();
				while (!keepProperty && nestedModelsIterator.hasNext()) {
					DataModelImpl nestedModel = (DataModelImpl) nestedModelsIterator.next();
					if (nestedModel.isProperty(property)) {
						keepProperty = true;
					}
				}
			}
			if (!keepProperty) {
				if (null == nestedPropertiesToRemove) {
					nestedPropertiesToRemove = new HashSet();
				}
				nestedPropertiesToRemove.add(property);
			}
		}

		if (null != nestedPropertiesToRemove) {
			allPropertyNames.removeAll(nestedPropertiesToRemove);
			nestedPropertyNames.removeAll(nestedPropertiesToRemove);
			if (nestingModels != null) {
				Iterator nestingModelsIterator = nestingModels.iterator();
				while (nestingModelsIterator.hasNext()) {
					((DataModelImpl) nestingModelsIterator.next()).removeNestedProperties(nestedPropertiesToRemove);
				}
			}
		}
	}

	public boolean isNestedModel(String modelName) {
		return modelName != null && null != nestedModels && nestedModels.containsKey(modelName);
	}

	public IDataModel getNestedModel(String modelName) {
		IDataModel dataModel = (null != nestedModels && null != modelName) ? (IDataModel) nestedModels.get(modelName) : null;
		if (null == dataModel) {
			throw new RuntimeException(NESTED_MODEL_NOT_LOCATED + modelName);
		}
		return dataModel;
	}

	public DataModelPropertyDescriptor[] getValidPropertyDescriptors(String propertyName) {
		DataModelImpl dataModel = getOwningDataModel(propertyName);
		DataModelPropertyDescriptor[] descriptors = dataModel.provider.getValidPropertyDescriptors(propertyName);
		return descriptors == null ? NO_DESCRIPTORS : descriptors;
	}

	public DataModelPropertyDescriptor getPropertyDescriptor(String propertyName) {
		DataModelImpl dataModel = getOwningDataModel(propertyName);
		DataModelPropertyDescriptor descriptor = dataModel.provider.getPropertyDescriptor(propertyName);
		return descriptor == null ? new DataModelPropertyDescriptor(getProperty(propertyName)) : descriptor;
	}

	public void notifyPropertyChange(String propertyName, int flag) {
		if (flag == DEFAULT_CHG) {
			if (isPropertySet(propertyName)) {
				return;
			}
			flag = VALUE_CHG;
		}
		notifyListeners(new DataModelEvent(this, propertyName, flag));
	}

	private void notifyListeners(DataModelEvent event) {
		if (listeners != null && !listeners.isEmpty()) {
			IDataModelListener listener;
			for (int i = 0; i < listeners.size(); i++) {
				listener = (IDataModelListener) listeners.get(i);
				if (listener != event.getDataModel()) {
					listener.propertyChanged(event);
				}
			}
		}
	}

	public void propertyChanged(DataModelEvent event) {
		notifyListeners(event);
	}

	public IStatus validate() {
		return validate(true);
	}

	public IStatus validate(boolean stopOnFirstFailure) {
		IStatus status = null;
		IStatus propStatus;
		String propName;
		Iterator it;
		for (int i = 0; i < 2; i++) {
			switch (i) {
				case 0 :
					it = basePropertyNames.iterator();
					break;
				case 1 :
				default :
					it = getNestedModelNames().iterator();
			}
			while (it.hasNext()) {
				propName = (String) it.next();
				propStatus = provider.validate(propName);
				if (propStatus != null) {
					if (status == null || status.isOK())
						status = propStatus;
					else {
						if (status.isMultiStatus())
							((MultiStatus) status).merge(propStatus);
						else {
							MultiStatus multi = new MultiStatus("org.eclipse.wst.common.frameworks.internal", 0, "", null); //$NON-NLS-1$ //$NON-NLS-2$
							multi.merge(status);
							multi.merge(propStatus);
							status = multi;
						}
					}
					if (stopOnFirstFailure && status != null && !status.isOK() && status.getSeverity() == IStatus.ERROR)
						return status;
				}
			}
		}

		if (status == null)
			return IDataModelProvider.OK_STATUS;
		return status;
	}

	public void addListener(IDataModelListener listener) {
		if (listener != null) {
			if (listeners == null) {
				listeners = new ArrayList();
				listeners.add(listener);
			} else if (!listeners.contains(listener))
				listeners.add(listener);
		}
	}

	public void removeListener(IDataModelListener listener) {
		if (listeners != null && listener != null)
			listeners.remove(listener);
	}

	/**
	 * Return true if the model doesn't have any errors.
	 * 
	 * @return boolean
	 */
	public boolean isValid() {
		return validate(true).getSeverity() != IStatus.ERROR;
	}

	public boolean isPropertyValid(String propertyName) {
		return validateProperty(propertyName).getSeverity() != IStatus.ERROR;
	}

	public IStatus validateProperty(String propertyName) {
		DataModelImpl dataModel = getOwningDataModel(propertyName);
		IStatus status = dataModel.provider.validate(propertyName);
		return status == null ? IDataModelProvider.OK_STATUS : status;
	}

	public List getExtendedContext() {
		List extendedContext = provider.getExtendedContext();
		return extendedContext == null ? Collections.EMPTY_LIST : extendedContext;
	}

	public void dispose() {
		provider.dispose();
	}

	public IDataModelOperation getDefaultOperation() {
		IDataModelOperation providerOp = provider.getDefaultOperation();
		if (null == providerOp) {
			providerOp = new AbstractDataModelOperation(this) {
				public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
					return OK_STATUS;
				}

				public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
					return OK_STATUS;
				}

				public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
					return OK_STATUS;
				}
			};
		}
		return new ExtendableOperationImpl(providerOp);
	}

	public String toString() {
		return "IDataModel, provider=" + provider.toString(); //$NON-NLS-1$
	}

	public String getID() {
		String id = provider.getID();
		return null != id ? id : ""; //$NON-NLS-1$
	}
}
