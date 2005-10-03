/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.componentcore.internal.operation;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.datamodel.properties.IComponentCreationDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFlexibleProjectCreationDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.ComponentType;
import org.eclipse.wst.common.componentcore.internal.ComponentcoreFactory;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;


public abstract class ComponentCreationOperation extends AbstractDataModelOperation implements IComponentCreationDataModelProperties {

    public ComponentCreationOperation(IDataModel model) {
        super(model);
    }

    public IStatus execute(String componentType, IProgressMonitor monitor, IAdaptable info) {
        createProjectIfNeeded(monitor, info);
		StructureEdit edit = null;
        try {
			edit = StructureEdit.getStructureEditForWrite(getProject());

            createAndLinkJ2EEComponentsForSingleComponent();
            setupComponentType(componentType);
        }
        catch (CoreException e) {
            Logger.getLogger().log(e);
        }
		finally {
			if (edit != null) {
				edit.saveIfNecessary(monitor);
				edit.dispose();
			}
			
		}
        
        return OK_STATUS;
    }

    private void createProjectIfNeeded(IProgressMonitor monitor, IAdaptable info) {
        Object dm = model.getNestedModel(NESTED_PROJECT_CREATION_DM);
        if(dm == null) return;
        String projName = ((IDataModel)dm).getStringProperty(IFlexibleProjectCreationDataModelProperties.PROJECT_NAME);
           
        IProject proj = ProjectUtilities.getProject(projName);
        if(projName == null || projName.equals("") || proj.exists()) return;
        IDataModelOperation op = ((IDataModel)dm).getDefaultOperation();
        try {
            op.execute(monitor, info);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    protected abstract void createAndLinkJ2EEComponentsForMultipleComponents() throws CoreException;
    
    protected abstract void createAndLinkJ2EEComponentsForSingleComponent() throws CoreException;
    
    
    protected void setupComponentType(String typeID) {
        IVirtualComponent component = ComponentCore.createComponent(getProject());
        ComponentType componentType = ComponentcoreFactory.eINSTANCE.createComponentType();
        componentType.setComponentTypeId(typeID);
        componentType.setVersion(getVersion());
        List newProps = getProperties();
        if (newProps != null && !newProps.isEmpty()) {
            EList existingProps = componentType.getProperties();
            for (int i = 0; i < newProps.size(); i++) {
                existingProps.add(newProps.get(i));
            }
        }
        StructureEdit.setComponentType(component, componentType);
    }

    protected String getComponentName() {
        return model.getStringProperty(COMPONENT_NAME);
    }

    public String getComponentDeployName() {
        return model.getStringProperty(COMPONENT_DEPLOY_NAME);
    }

    protected abstract String getVersion();

    protected abstract List getProperties();

	public IProject getProject() {
	    String projName = model.getStringProperty(PROJECT_NAME);
	    return ProjectUtilities.getProject(projName);
	}

}