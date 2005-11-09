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

import org.eclipse.wst.common.componentcore.datamodel.properties.IProjectMigratorDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.operation.ProjectMigratorDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;

public class ProjectMigratorDataModelProvider extends AbstractDataModelProvider implements IProjectMigratorDataModelProperties {

	public Set getPropertyNames() {
		Set names = super.getPropertyNames();
		names.add(PROJECT_NAME);
		return names;
	}

	public final IDataModelOperation getDefaultOperation() {
		return new ProjectMigratorDataModelOperation(model);
	}

}
