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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.ComponentType;
import org.eclipse.wst.common.componentcore.internal.ComponentcoreFactory;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;

public abstract class ComponentCreationOperation extends WTPOperation {

	public ComponentCreationOperation(ComponentCreationDataModel dataModel) {
		super(dataModel);
	}

	public ComponentCreationOperation() {
		super();
	}
	protected void execute(String componentType, IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
        //super.execute(monitor);
        createComponent();
        setupComponentType(componentType);		
    }	
	//to make it  abstract
	protected  void createComponent(){
		
	}
	
    protected void setupComponentType(String typeID) {
    	ComponentCreationDataModel dataModel = (ComponentCreationDataModel)operationDataModel;

		IVirtualComponent component = ComponentCore.createComponent(dataModel.getProject(), dataModel.getComponentDeployName());    	
        ComponentType componentType = ComponentcoreFactory.eINSTANCE.createComponentType();
        componentType.setComponentTypeId(typeID);
        componentType.setVersion(dataModel.getVersion());
        List newProps = dataModel.getProperties();
        if (newProps != null && !newProps.isEmpty()) {
            EList existingProps = componentType.getProperties();
            for (int i = 0; i < newProps.size(); i++) {
                existingProps.add(newProps.get(i));
		}
		}
        StructureEdit.setComponentType(component, componentType);
    }	
}