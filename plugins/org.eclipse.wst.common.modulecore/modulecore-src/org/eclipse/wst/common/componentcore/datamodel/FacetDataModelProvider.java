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

import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.operation.FacetDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.internal.datamodel.DataModelPausibleOperationImpl;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action.Type;

public class FacetDataModelProvider extends AbstractDataModelProvider implements IFacetDataModelProperties {

	public static final String NOTIFICATION_OPERATION = "FacetDataModelProvider.NOTIFICATION_OPERATION"; //$NON-NLS-1$

	public Set getPropertyNames() {
		Set names = super.getPropertyNames();
		names.add(FACET_PROJECT_NAME);
		names.add(FACET_ID);
		names.add(FACET_VERSION_STR);
		names.add(FACET_TYPE);
		names.add(FACET_VERSION);
		names.add(FACET_ACTION);
		names.add(SHOULD_EXECUTE);
		names.add(NOTIFICATION_OPERATION);
		return names;
	}

	public Object getDefaultProperty(String propertyName) {
		if (FACET_VERSION.equals(propertyName)) {
			return ProjectFacetsManager.getProjectFacet(getStringProperty(FACET_ID)).getVersion(getStringProperty(FACET_VERSION_STR));
		} else if (FACET_ACTION.equals(propertyName)) {
			return new IFacetedProject.Action((Type) model.getProperty(FACET_TYPE), (IProjectFacetVersion) model.getProperty(FACET_VERSION), model);
		} else if (SHOULD_EXECUTE.equals(propertyName)) {
			return Boolean.TRUE;
		} else if (NOTIFICATION_OPERATION.equals(propertyName)) {
			return getFacetNotificationOperation();
		}
		return super.getDefaultProperty(propertyName);
	}

	public boolean propertySet(String propertyName, Object propertyValue) {
		if (FACET_ACTION.equals(propertyName)) {
			throw new RuntimeException();
		}
		return super.propertySet(propertyName, propertyValue);
	}

	public final IDataModelOperation getDefaultOperation() {
		return new FacetDataModelOperation(model);
	}

	protected IDataModelOperation getFacetNotificationOperation() {
		return new DataModelPausibleOperationImpl(new AbstractDataModelOperation(this.model) {
			public String getID() {
				return "FacetDataModelProvider.Notification." + model.getProperty(FACET_TYPE) + "." + model.getStringProperty(FACET_ID); //$NON-NLS-1$//$NON-NLS-2$ 
			}

			public org.eclipse.core.runtime.IStatus execute(IProgressMonitor monitor, org.eclipse.core.runtime.IAdaptable info) throws ExecutionException {
				return AbstractDataModelProvider.OK_STATUS;
			}
		});
	}
}
