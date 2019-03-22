/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.operation;

import java.util.Set;

import org.eclipse.wst.common.componentcore.datamodel.properties.IAddReferenceDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;

/**
 * This class is meant to be an alternative to CreateReferenceComponentsDataModelProvider
 * which requires less options and allows for a pre-made IVirtualReference to be stored.
 */
public class AddReferenceDataModelProvider extends AbstractDataModelProvider implements IAddReferenceDataModelProperties {

	public AddReferenceDataModelProvider() {
		super();
	}

	public Set getPropertyNames() {
		Set propertyNames = super.getPropertyNames();
		propertyNames.add(SOURCE_COMPONENT);
		propertyNames.add(TARGET_REFERENCE_LIST);
		propertyNames.add(SYNC_PRIMARY_RUNTIME);
		return propertyNames;
	}

	public IDataModelOperation getDefaultOperation() {
		return new AddReferencesOp(model);
	}

	public Object getDefaultProperty(String propertyName) {
		Object retVal = super.getDefaultProperty(propertyName);
		if (SYNC_PRIMARY_RUNTIME.equals(propertyName)){
			retVal = Boolean.TRUE;
		}
		return retVal;
	}
}
