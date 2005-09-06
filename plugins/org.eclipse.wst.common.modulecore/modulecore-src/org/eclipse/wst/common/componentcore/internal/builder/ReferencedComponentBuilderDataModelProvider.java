/*******************************************************************************
 * Copyright (c) 2003, 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.builder;

import java.util.Set;

import org.eclipse.wst.common.componentcore.datamodel.properties.IReferencedComponentBuilderDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;

public class ReferencedComponentBuilderDataModelProvider extends AbstractDataModelProvider implements IReferencedComponentBuilderDataModelProperties {

	public Set getPropertyNames() {
		Set propertyNames = super.getPropertyNames();
		propertyNames.add(VIRTUAL_REFERENCE);
		return propertyNames;
	}

	public IDataModelOperation getDefaultOperation() {
		return new ReferencedComponentBuilderOperation(model);
	}
}
