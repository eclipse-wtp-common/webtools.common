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

package org.eclipse.wst.common.modulecore.internal.operation;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.modulecore.ComponentType;
import org.eclipse.wst.common.modulecore.ModuleCore;
import org.eclipse.wst.common.modulecore.ModuleCoreFactory;
import org.eclipse.wst.common.modulecore.internal.util.IModuleConstants;
import org.eclipse.wst.common.modulecore.resources.IVirtualContainer;

public abstract class ComponentCreationOperationEx extends AbstractDataModelOperation {

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
	protected  void createComponent(){
		
	}
	
    protected void setupComponentType(String typeID) {
    	//ComponentCreationDataModel dataModel = (ComponentCreationDataModel)operationDataModel;

        IVirtualContainer component = ModuleCore.createContainer(getProject(), model.getStringProperty(ComponentCreationDataModelProvider.COMPONENT_DEPLOY_NAME));    	
        ComponentType componentType = ModuleCoreFactory.eINSTANCE.createComponentType();
        componentType.setModuleTypeId(typeID);
        componentType.setVersion(getVersion());
        List newProps = getProperties();
        if (newProps != null && !newProps.isEmpty()) {
            EList existingProps = componentType.getProperties();
            for (int i = 0; i < newProps.size(); i++) {
                existingProps.add(newProps.get(i));
		}
		}
        ModuleCore.setComponentType(component, componentType);
    }	
    
    protected IProject getProject(){
    	String name = model.getStringProperty(ComponentCreationDataModelProvider.PROJECT_NAME);
    	return ProjectUtilities.getProject(name);
    }
    
    protected String getComponentName(){
    	return model.getStringProperty(ComponentCreationDataModelProvider.COMPONENT_NAME);
    }
    public String getComponentDeployName(){
    	return model.getStringProperty(ComponentCreationDataModelProvider.COMPONENT_DEPLOY_NAME);
    }    
    
    protected abstract String getVersion();
	protected abstract List getProperties();
	
}