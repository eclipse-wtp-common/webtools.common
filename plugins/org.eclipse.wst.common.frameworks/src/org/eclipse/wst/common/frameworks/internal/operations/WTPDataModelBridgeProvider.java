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
package org.eclipse.wst.common.frameworks.internal.operations;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;

/**
 * <p>
 * This provider acts as wrapper around the old WTPOperationDataModel framework so a exiting
 * WTPOperationDataModel can work with the new IDataModel framework. This class should be used only
 * as a temporary solution while porting to the new framework.
 * </p>
 * <p>
 * This provider only allows the old WTPOperationDataModels to run within the new framework; not
 * vice versa.
 * </p>
 * <p>
 * There are some known limitations:
 * <ul>
 * <li> All property names must be defined up front on the WTPOperationDataModel during the
 * iniitWTPDataModel() method. Any properties added later cause runtime failures. </li>
 * <li>Any model nested under the WTPOperationDataModel must either be nested during
 * initWTPDataModel() or must not add any new property names.</li>
 * <li>Any model nested under the WTPOperationDataModel should not be unnested if doing so will
 * remove property names with respect to the WTPOperationDataModel</li>
 * </ul>
 * </p>
 * 
 */
public abstract class WTPDataModelBridgeProvider extends AbstractDataModelProvider {

	/**
	 * A property key used to access the underlying data model. This should be used sparingly.
	 */
	public static final String WRAPPED_WTP_DATA_MODEL = "WTPDataModelBridgeProvider.WRAPPED_WTP_DATA_MODEL";

	protected WTPOperationDataModel wtpDataModel;

	/**
	 * Subclasses need to return an initiaized WTPOperationDataModel here.
	 * 
	 * @return
	 */
	protected abstract WTPOperationDataModel initWTPDataModel();

	public void init() {
	}

	public String[] getPropertyNames() {
		wtpDataModel = initWTPDataModel();
		wtpDataModel.addListener(new WTPOperationDataModelListener() {
			public void propertyChanged(WTPOperationDataModelEvent event) {
				int flag = event.getFlag();
				String propertyName = event.getPropertyName();
				switch (flag) {
					case WTPOperationDataModelEvent.ENABLE_CHG :
						model.notifyPropertyChange(propertyName, IDataModel.ENABLE_CHG);
						break;
					case WTPOperationDataModelEvent.VALID_VALUES_CHG :
						model.notifyPropertyChange(propertyName, IDataModel.VALID_VALUES_CHG);
						break;
					case WTPOperationDataModelEvent.PROPERTY_CHG :
						if (!wtpDataModel.isSet(propertyName)) {
							if (model.isPropertySet(propertyName)) {
								model.setProperty(propertyName, null);
							}
							model.notifyPropertyChange(propertyName, IDataModel.DEFAULT_CHG);
						} else {
							model.setProperty(propertyName, event.getProperty());
							model.notifyPropertyChange(propertyName, IDataModel.VALUE_CHG);
						}
						break;
				}
			}
		});
		Set validProperties = wtpDataModel.getValidProperties();
		String[] propertyNames = new String[validProperties.size() + 1];
		propertyNames[0] = WRAPPED_WTP_DATA_MODEL;
		Iterator iterator = validProperties.iterator();
		for (int i = 1; i < propertyNames.length; i++) {
			propertyNames[i] = (String) iterator.next();
		}
		return propertyNames;
	}

	public boolean propertySet(String propertyName, Object propertyValue) {
		if (WRAPPED_WTP_DATA_MODEL.equals(propertyName)) {
			throw new RuntimeException();
		}
		wtpDataModel.setProperty(propertyName, propertyValue);
		return true;
	}

	public Object getDefaultProperty(String propertyName) {
		if (WRAPPED_WTP_DATA_MODEL.equals(propertyName)) {
			return wtpDataModel;
		}
		return wtpDataModel.getDefaultProperty(propertyName);
	}

	public DataModelPropertyDescriptor getPropertyDescriptor(String propertyName) {
		if (WRAPPED_WTP_DATA_MODEL.equals(propertyName)) {
			return null;
		}
		return convert(wtpDataModel.getPropertyDescriptor(propertyName));
	}

	public DataModelPropertyDescriptor[] getValidPropertyDescriptors(String propertyName) {
		if (WRAPPED_WTP_DATA_MODEL.equals(propertyName)) {
			return null;
		}
		return convert(wtpDataModel.getValidPropertyDescriptors(propertyName));
	}

	public boolean isPropertyEnabled(String propertyName) {
		if (WRAPPED_WTP_DATA_MODEL.equals(propertyName)) {
			return true;
		}
		Boolean b = wtpDataModel.isEnabled(propertyName);
		return b != null ? b.booleanValue() : true;
	}

	public IStatus validate(String name) {
		if (WRAPPED_WTP_DATA_MODEL.equals(name)) {
			return OK_STATUS;
		}
		return wtpDataModel.validateProperty(name);
	}

	public List getExtendedContext() {
		return (List) wtpDataModel.getProperty(WTPOperationDataModel.EXTENDED_CONTEXT);
	}

	public IDataModelOperation getDefaultOperation() {
		final WTPOperation wtpOperation = wtpDataModel.getDefaultOperation();
		IDataModelOperation op = new AbstractDataModelOperation() {
			public ISchedulingRule getSchedulingRule() {
				return wtpOperation.getSchedulingRule();
			}

			public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
				try {
					wtpOperation.run(monitor);
				} catch (InvocationTargetException e) {
					Logger.getLogger().logError(e);
					throw new ExecutionException(e.getMessage(), e);
				} catch (InterruptedException e) {
					Logger.getLogger().logError(e);
					throw new ExecutionException(e.getMessage(), e);
				}
				return null;
			}

			public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
				return null;
			}

			public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
				return null;
			}
		};

		op.setID(getID());
		return op;
	}


	protected DataModelPropertyDescriptor convert(WTPPropertyDescriptor wtpDescriptor) {
		return new DataModelPropertyDescriptor(wtpDescriptor.getPropertyValue(), wtpDescriptor.getPropertyDescription());
	}

	protected DataModelPropertyDescriptor[] convert(WTPPropertyDescriptor[] wtpDescriptors) {
		DataModelPropertyDescriptor[] descriptors = new DataModelPropertyDescriptor[wtpDescriptors.length];
		for (int i = 0; i < descriptors.length; i++) {
			descriptors[i] = convert(wtpDescriptors[i]);
		}
		return descriptors;
	}

	public void dispose() {
		wtpDataModel.dispose();
		super.dispose();
	}

}
