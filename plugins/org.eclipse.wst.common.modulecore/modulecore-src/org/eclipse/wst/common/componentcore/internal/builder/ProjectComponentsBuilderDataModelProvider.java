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
import org.eclipse.wst.common.componentcore.internal.ReferencedComponent;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.server.core.IProjectProperties;
import org.eclipse.wst.server.core.ServerCore;

public class ProjectComponentsBuilderDataModelProvider extends AbstractDataModelProvider implements IProjectComponentsBuilderDataModelProperties {

    /* (non-Javadoc)
     * @see org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider#getPropertyNames()
     */
    public String[] getPropertyNames() {
        return new String[]{PROJECT, BUILD_KIND, PROJECT_DETLA, MODULE_CORE, MODULE_BUILDER_DM_LIST};
    }
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#doSetProperty(java.lang.String,
	 *      java.lang.Object)
	 */
	public boolean setProperty(String propertyName, Object propertyValue) {
		if (PROJECT.equals(propertyName)) {

			//TODO: remove for M4 when incremental build available
			model.setProperty(BUILD_KIND, new Integer(IncrementalProjectBuilder.FULL_BUILD));
			model.setProperty(MODULE_BUILDER_DM_LIST, populateModuleBuilderDataModelList());
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
				if (((StructureEdit) model.getProperty(MODULE_CORE)).isLocalDependency(depModule)) {
					WorkbenchComponent depWBModule = ((StructureEdit) model.getProperty(MODULE_CORE)).findComponentByURI(depModule.getHandle());
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
		StructureEdit moduleCore = (StructureEdit) model.getProperty(MODULE_CORE);
		List moduleBuilderDataModelList = new ArrayList();
		WorkbenchComponent[] wbModules = moduleCore.getWorkbenchModules();

		if (wbModules == null || wbModules.length == 0) {
			return null;
		}

		List sortedList = computeModuleBuildOrder(wbModules);

		ComponentStructuralBuilderExtensionRegistry registry = null;
		WorkbenchComponentBuilderDataModelProvider provider = null;
		
        IProjectProperties props = ServerCore.getProjectProperties((IProject)model.getProperty(PROJECT));
        String runtimeID = props.getRuntimeTarget().getId();

		for (int i = 0; i < sortedList.size(); i++) {
			WorkbenchComponent wbModule = (WorkbenchComponent) sortedList.get(i);
			String id = wbModule.getComponentType().getComponentTypeId();
			if (id == null)
				break;
			provider = ComponentStructuralBuilderExtensionRegistry.getComponentStructuralBuilderDMForServerTargetID(runtimeID, wbModule.getComponentType().getComponentTypeId());
			
			if(provider == null) 
			    break;
			IDataModel dataModel = DataModelFactory.createDataModel(provider);
			dataModel.setProperty(IWorkbenchComponentBuilderDataModelProperties.MODULE_CORE, moduleCore);
			dataModel.setProperty(IWorkbenchComponentBuilderDataModelProperties.PROJECT, model.getProperty(PROJECT));
			dataModel.setProperty(IWorkbenchComponentBuilderDataModelProperties.WORKBENCH_MODULE, wbModule);
			moduleBuilderDataModelList.add(dataModel);
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
