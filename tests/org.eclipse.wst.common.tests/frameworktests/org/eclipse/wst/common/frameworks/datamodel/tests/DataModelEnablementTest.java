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
package org.eclipse.wst.common.frameworks.datamodel.tests;

import junit.framework.TestCase;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.internal.enablement.DataModelEnablementFactory;
import org.eclipse.wst.common.frameworks.internal.operations.IProjectCreationProperties;
import org.eclipse.wst.common.frameworks.internal.operations.ProjectCreationDataModelProvider;

public class DataModelEnablementTest extends TestCase {

    public void testValidExtensionIDAndProviderType() {
        IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject("temp");
        if(proj == null){
            IDataModel model = DataModelFactory.createDataModel(new ProjectCreationDataModelProvider());
            model.setProperty(IProjectCreationProperties.PROJECT_NAME, "temp");
            IDataModelOperation op = model.getDefaultOperation();
            try {
                op.execute(new NullProgressMonitor(), null);
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
            proj = ResourcesPlugin.getWorkspace().getRoot().getProject("temp");
        }
        IDataModel dataModel = DataModelEnablementFactory.createDataModel("testProviderBase", proj);
        assertTrue(dataModel.isProperty(ITestDataModel.FOO));
    }

    public void testValidExtensionIDImplementorForProviderType() {
        IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject("temp");
        if(proj == null){
            IDataModel model = DataModelFactory.createDataModel(new ProjectCreationDataModelProvider());
            model.setProperty(IProjectCreationProperties.PROJECT_NAME, "temp");
            IDataModelOperation op = model.getDefaultOperation();
            try {
                op.execute(new NullProgressMonitor(), null);
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
            proj = ResourcesPlugin.getWorkspace().getRoot().getProject("temp");
        }
        IDataModel dataModel = DataModelEnablementFactory.createDataModel("testProviderBogus", proj);
        assertTrue(dataModel == null);
    }

}
