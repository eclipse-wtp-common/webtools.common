/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.modulecore.internal.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.core.internal.events.ResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.emf.common.util.EList;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;
import org.eclipse.wst.common.modulecore.ModuleCore;
import org.eclipse.wst.common.modulecore.ReferencedComponent;
import org.eclipse.wst.common.modulecore.UnresolveableURIException;
import org.eclipse.wst.common.modulecore.WorkbenchComponent;

public class DeployableModuleProjectBuilderDataModel extends ComponentStructuralBuilderDataModel {

	public DeployableModuleProjectBuilderDataModel() {
		super();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#doSetProperty(java.lang.String,
	 *      java.lang.Object)
	 */
	protected boolean doSetProperty(String propertyName, Object propertyValue) {
		boolean status = super.doSetProperty(propertyName, propertyValue);
		if (PROJECT.equals(propertyName)) {
			setProperty(MODULE_BUILDER_DM_LIST, populateModuleBuilderDataModelList());
		}
		return status;
	}

	/**
	 * @return
	 */
	private ModuleCore getModuleCore() {
		return (ModuleCore) getProperty(MODULE_CORE);
	}

	/**
	 * @return
	 */
	private Object populateModuleBuilderDataModelList() {
		//TODO: delta information should be taken into consideration
		List moduleDMList = null;
		switch (((Integer) getProperty(BUILD_KIND)).intValue()) {
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
				if (getModuleCore().isLocalDependency(depModule)) {
					WorkbenchComponent depWBModule = getModuleCore().findWorkbenchModuleByModuleURI(depModule.getHandle());
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
		ModuleCore moduleCore = getModuleCore();
		List moduleBuilderDataModelList = new ArrayList();
		WorkbenchComponent[] wbModules = moduleCore.getWorkbenchModules();

		if (wbModules == null || wbModules.length == 0) {
			return null;
		}

		List sortedList = computeModuleBuildOrder(wbModules);

		DeployableModuleBuilderFactory factory = null;
		DeployableModuleBuilderDataModel dataModel = null;

		for (int i = 0; i < sortedList.size(); i++) {
			WorkbenchComponent wbModule = (WorkbenchComponent) sortedList.get(i);
			String id = wbModule.getComponentType().getModuleTypeId();
			if (id == null)
				break;
			factory = DeployableModuleBuilderFactoryRegistry.INSTANCE.createDeployableFactory(wbModule.getComponentType().getModuleTypeId());
			if (factory != null) {
				dataModel = factory.createDeploymentModuleDataModel();
				dataModel.setProperty(DeployableModuleBuilderDataModel.MODULE_CORE, moduleCore);
				dataModel.setProperty(DeployableModuleBuilderDataModel.PROJECT, getProperty(PROJECT));
				dataModel.setProperty(DeployableModuleBuilderDataModel.WORKBENCH_MODULE, wbModule);
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
	public WTPOperation getDefaultOperation() {
		return new DeployableModuleProjectBuilderOperation(this);
	}

}