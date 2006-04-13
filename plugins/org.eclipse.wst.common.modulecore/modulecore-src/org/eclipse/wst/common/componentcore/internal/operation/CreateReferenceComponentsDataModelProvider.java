/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.operation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.wst.common.componentcore.datamodel.properties.ICreateReferenceComponentsDataModelProperties;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;

public class CreateReferenceComponentsDataModelProvider extends AbstractDataModelProvider implements ICreateReferenceComponentsDataModelProperties {

	public CreateReferenceComponentsDataModelProvider() {
		super();
	}

	public Set getPropertyNames() {
		Set propertyNames = super.getPropertyNames();
		propertyNames.add(SOURCE_COMPONENT);
		propertyNames.add(TARGET_COMPONENT_LIST);
		propertyNames.add(TARGET_COMPONENTS_DEPLOY_PATH);
		propertyNames.add(TARGET_COMPONENT_ARCHIVE_NAME);
		propertyNames.add(TARGET_COMPONENTS_TO_URI_MAP);
		return propertyNames;
	}

	public IDataModelOperation getDefaultOperation() {
		return new CreateReferenceComponentsOp(model);
	}

	public Object getDefaultProperty(String propertyName) {
		if (TARGET_COMPONENTS_TO_URI_MAP.equals(propertyName)) {
			Map map = new HashMap();
			List components = (List) getProperty(TARGET_COMPONENT_LIST);
			for (int i = 0; i < components.size(); i++) {
				IVirtualComponent component = (IVirtualComponent) components.get(i);
				String name = component.getName();
				map.put(component, name);
			}
			return map;
		}
		
		if (propertyName.equals(TARGET_COMPONENT_LIST))
			return new ArrayList();
		else if (propertyName.equals(TARGET_COMPONENTS_DEPLOY_PATH)){
			return "/"; //$NON-NLS-1$
		}
		return super.getDefaultProperty(propertyName);
	}
}
