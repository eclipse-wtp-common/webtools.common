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

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.core.internal.events.ResourceDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.emf.common.util.EList;
import org.eclipse.wst.common.componentcore.StructureEdit;
import org.eclipse.wst.common.componentcore.UnresolveableURIException;
import org.eclipse.wst.common.componentcore.datamodel.properties.IProjectComponentsBuilderDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IWorkbenchComponentBuilderDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.ReferencedComponent;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.internal.enablement.DataModelEnablementFactory;

public class ProjectComponentsBuilderDataModelProvider extends AbstractDataModelProvider implements IProjectComponentsBuilderDataModelProperties {

    /* (non-Javadoc)
     * @see org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider#getPropertyNames()
     */
    public String[] getPropertyNames() {
        return new String[]{PROJECT, BUILD_KIND, PROJECT_DETLA, COMPONENT_CORE, COMPONENT_BUILDER_DM_LIST};
    }
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#doSetProperty(java.lang.String,
	 *      java.lang.Object)
	 */
	public boolean propertySet(String propertyName, Object propertyValue) {
		if (PROJECT.equals(propertyName)) {

			//TODO: remove for M4 when incremental build available
			model.setProperty(BUILD_KIND, new Integer(IncrementalProjectBuilder.FULL_BUILD));
			model.setProperty(COMPONENT_BUILDER_DM_LIST, populateModuleBuilderDataModelList());
		}
		return true;
	}
	/**
	 * @return
	 */
	private Object populateModuleBuilderDataModelList() {
		//TODO: delta information should be taken into consideration
		List moduleDMList = null;
		switch (((Integer) model.getProperty(BUILD_KIND)).intValue()) {
			case IncrementalProjectBuilder.FULL_BUILD :
				moduleDMList = populateFullModuleBuilderDataModelList();
				break;
			case IncrementalProjectBuilder.INCREMENTAL_BUILD :
				moduleDMList = populateDeltaModuleBuilderDataModelList((ResourceDelta) getProperty(PROJECT_DETLA));
				break;
			default :
				moduleDMList = populateFullModuleBuilderDataModelList();
				break;
		}
		return moduleDMList;
	}

	/**
	 * Sorts the wbModules such that the returned list
	 * 
	 * @param wbModule
	 * @param sortedModuleList
	 * @param wbModuleList
	 * @return
	 * @throws UnresolveableURIException
	 */
	private List computeModuleBuildOrder(WorkbenchComponent wbModule, List sortedModuleList, List wbModuleList, Stack callStack) throws UnresolveableURIException {
		if (callStack.contains(wbModule)) {
			//TODO do something meaningful with this.
			throw new RuntimeException("Cyclical module dependency detected.");
		}
		try {
			callStack.push(wbModule);
			EList depModules = wbModule.getReferencedComponents();
			for (int i = 0; i < depModules.size(); i++) {
				ReferencedComponent depModule = (ReferencedComponent) depModules.get(i);
				if (((StructureEdit) model.getProperty(COMPONENT_CORE)).isLocalDependency(depModule)) {
					WorkbenchComponent depWBModule = ((StructureEdit) model.getProperty(COMPONENT_CORE)).findComponentByURI(depModule.getHandle());
					if (!sortedModuleList.contains(depWBModule)) {
						computeModuleBuildOrder(depWBModule, sortedModuleList, null, callStack);
					}
				}
			}
			if (!sortedModuleList.contains(wbModule)) {
				sortedModuleList.add(wbModule);
			}
			if (null != wbModuleList && !wbModuleList.isEmpty()) {
				wbModule = (WorkbenchComponent) wbModuleList.remove(wbModuleList.size() - 1);
				return computeModuleBuildOrder(wbModule, sortedModuleList, wbModuleList, callStack);
			}
			return sortedModuleList;
		} finally {
			callStack.pop();
		}
	}

	/**
	 * Returns the list of WorkbenchModules in a sorted build order
	 * 
	 * @param wbModules
	 * @return
	 */
	private List computeModuleBuildOrder(WorkbenchComponent[] wbModules) {
		ArrayList unsortedList = new ArrayList(wbModules.length - 1);
		for (int i = 1; i < wbModules.length; i++) {
			unsortedList.add(wbModules[i]);
		}
		WorkbenchComponent firstModule = wbModules[0];
		List sortedList = new ArrayList(wbModules.length);
		try {
			sortedList = computeModuleBuildOrder(firstModule, sortedList, unsortedList, new Stack());
			//			for(int i=0;i<sortedList.size(); i++){
			//				System.out.println(sortedList.get(i));
			//			}
		} catch (UnresolveableURIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sortedList;
	}


	private List populateFullModuleBuilderDataModelList() {
		StructureEdit moduleCore = (StructureEdit) model.getProperty(COMPONENT_CORE);
		List moduleBuilderDataModelList = new ArrayList();
		WorkbenchComponent[] wbModules = moduleCore.getWorkbenchModules();

		if (wbModules == null || wbModules.length == 0) {
			return null;
		}

		List sortedList = computeModuleBuildOrder(wbModules);

        IDataModel dataModel = null;
        IProject curProject = (IProject)model.getProperty(PROJECT);
		
        String builderType = null;
        for (int i = 0; i < sortedList.size(); i++) {
			WorkbenchComponent wbComponent = (WorkbenchComponent) sortedList.get(i);
			String typeId = wbComponent.getComponentType().getComponentTypeId();
			if (typeId == null)
				break;
            builderType = typeId + ".builder";
			dataModel = DataModelEnablementFactory.createDataModel(builderType, curProject);
            if(dataModel != null) {
    			dataModel.setProperty(IWorkbenchComponentBuilderDataModelProperties.COMPONENT_CORE, moduleCore);
    			dataModel.setProperty(IWorkbenchComponentBuilderDataModelProperties.PROJECT, model.getProperty(PROJECT));
    			dataModel.setProperty(IWorkbenchComponentBuilderDataModelProperties.WORKBENCH_COMPONENT, wbComponent);
    			moduleBuilderDataModelList.add(dataModel);
            }
		}
		return moduleBuilderDataModelList;
	}
    
	private List populateDeltaModuleBuilderDataModelList(ResourceDelta delta) {
		//TODO: handle delta information correcty
		return populateFullModuleBuilderDataModelList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#getDefaultOperation()
	 */
	public IDataModelOperation getDefaultOperation() {
		return new ProjectComponentsBuilderOperation(model);
	}

}
