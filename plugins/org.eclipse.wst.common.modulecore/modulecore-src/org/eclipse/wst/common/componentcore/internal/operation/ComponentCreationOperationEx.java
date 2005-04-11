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

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.ComponentType;
import org.eclipse.wst.common.componentcore.internal.ComponentcoreFactory;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

public abstract class ComponentCreationOperationEx extends AbstractDataModelOperation implements IComponentCreationDataModelProperties{

	public ComponentCreationOperationEx(IDataModel model) {
		super(model);
	}

	public ComponentCreationOperationEx() {
		super();
	}
	protected void execute(String componentType, IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
        createComponent();
        setupComponentType(componentType);		
    }	
	
	public IStatus execute(String componentType, IProgressMonitor monitor, IAdaptable info) {
        createComponent();
        setupComponentType(componentType);	
        return OK_STATUS;
	}

	
	//to make it  abstract
	protected void createComponent(){};
	
    protected void setupComponentType(String typeID) {
    	//ComponentCreationDataModel dataModel = (ComponentCreationDataModel)operationDataModel;

		IVirtualComponent component = ComponentCore.createComponent(getProject(), model.getStringProperty(ComponentCreationDataModelProvider.COMPONENT_DEPLOY_NAME));    	
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
    
    protected IProject getProject(){
    	String name = model.getStringProperty(PROJECT_NAME);
    	return ProjectUtilities.getProject(name);
    }
    
    protected String getComponentName(){
    	return model.getStringProperty(COMPONENT_NAME);
    }
    public String getComponentDeployName(){
    	return model.getStringProperty(COMPONENT_DEPLOY_NAME);
    }    
    
    protected abstract String getVersion();
	protected abstract List getProperties();
	
}