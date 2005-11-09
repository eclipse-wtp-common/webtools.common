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

import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.operation.FacetDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action.Type;

public class FacetDataModelProvider extends AbstractDataModelProvider implements IFacetDataModelProperties {

	public Set getPropertyNames() {
		Set names = super.getPropertyNames();
		names.add(FACET_PROJECT_NAME);
		names.add(FACET_ID);
		names.add(FACET_VERSION_STR);
		names.add(FACET_TYPE);
		names.add(FACET_VERSION);
		names.add(FACET_ACTION);
		names.add(SHOULD_EXECUTE);
		return names;
	}

	public Object getDefaultProperty(String propertyName) {
		if (FACET_VERSION.equals(propertyName)) {
			return ProjectFacetsManager.getProjectFacet(getStringProperty(FACET_ID)).getVersion(getStringProperty(FACET_VERSION_STR));
		} else if (FACET_ACTION.equals(propertyName)) {
			return new IFacetedProject.Action((Type) model.getProperty(IFacetDataModelProperties.FACET_TYPE), (IProjectFacetVersion) model.getProperty(IFacetDataModelProperties.FACET_VERSION), model);
		} else if (SHOULD_EXECUTE.equals(propertyName)) {
			return Boolean.TRUE;
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

}
