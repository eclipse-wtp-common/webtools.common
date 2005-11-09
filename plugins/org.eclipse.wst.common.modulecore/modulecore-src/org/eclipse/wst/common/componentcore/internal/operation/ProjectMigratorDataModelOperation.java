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
package org.eclipse.wst.common.componentcore.internal.operation;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.wst.common.componentcore.datamodel.properties.IProjectMigratorDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.IComponentProjectMigrator;
import org.eclipse.wst.common.componentcore.internal.ProjectMigratorRegistry;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;

public class ProjectMigratorDataModelOperation extends AbstractDataModelOperation {

	public ProjectMigratorDataModelOperation(IDataModel model) {
		super(model);
	}
	
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		IFacetedProject facetProj;
		try {
			IProject proj = ProjectUtilities.getProject(model.getStringProperty(IProjectMigratorDataModelProperties.PROJECT_NAME));
			IComponentProjectMigrator[] migrators = ProjectMigratorRegistry.getInstance().getProjectMigrators();
			for (int i = 0; i < migrators.length; i++) {
				IComponentProjectMigrator migrator = migrators[i];
				migrator.migrateProject(proj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return OK_STATUS;
	}

}
