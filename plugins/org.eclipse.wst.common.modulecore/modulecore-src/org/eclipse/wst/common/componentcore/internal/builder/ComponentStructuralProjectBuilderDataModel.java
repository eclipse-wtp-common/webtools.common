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
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel;
import org.eclipse.wst.server.core.IProjectProperties;
import org.eclipse.wst.server.core.ServerCore;
/**
 * Provides common set of preregisterd data model properties related to build status information 
 * present in IncrementalProjectBuilder (@see org.eclipse.core.resources.IncrementalProjectBuilder) as well as a ModuleCore instance for the current project which is being
 * built.
 * <p>
 * The data model should be subclassed by any vendor which aims to override the default ComponentStructuralBuilder.
 * Subclasses should implement all required methods from the super class including but not limited to getDefaultOperation.
 * which should return a WTPOperation associated with the data model.  The ComponentStructuralBuilder extension point should 
 * be used to register the overriding builder.
 * </p>
 * 
 * This class is experimental until fully documented.
 * </p>
 * 
 */
public class ComponentStructuralProjectBuilderDataModel extends WTPOperationDataModel{
	/**
	 * Required, type String. The initializing builder will set this field to the name value of the 
	 * project which is currently being built.
	 */
	public static final String PROJECT = "ComponentStructuralProjectBuilderDataModel.PROJECT"; //$NON-NLS-1$

	/**
	 * Required, type Integer. The initializing builder will set this field to the int value based on the build
	 * kind passed to the IncrementalProjectBuilder
	 * 
	 * @see IncrementalProjectBuilder.FULL_BUILD
	 * <li><code>FULL_BUILD</code>- indicates a full build.</li>
	 * 
	 * @see IncrementalProjectBuilder.INCREMENTAL_BUILD
	 * <li><code>INCREMENTAL_BUILD</code>- indicates an incremental build.</li>
	 * 
	 * @see IncrementalProjectBuilder.AUTO_BUILD
	 * <li><code>AUTO_BUILD</code>- indicates an automatically triggered
	 */
	public static final String BUILD_KIND = "ComponentStructuralProjectBuilderDataModel.BUILD_KIND"; //$NON-NLS-1$
	/**
	 * Required, type Integer. The initializing builder will set this field to the IResourceDelta value based on the delta
	 * passed to the IncrementalProjectBuilder during a build call.  This field can be used along with the BUILD_KIND to 
	 * create a more efficient builder
	 * 
	 * @see org.eclipse.core.resources.IResourceDelta
	 */
	public static final String PROJECT_DETLA = "ComponentStructuralProjectBuilderDataModel.PROJECT_DETLA"; //$NON-NLS-1$

	/**
	 * Required, type org.eclipse.wst.common.modulecore.ModuleCore. The initializing builder will set this field to the ModuleCore associated
	 * with the project which is currently being built.  This field can be used to retrieve information about components and their associated 
	 * dependent components present in the current project.
	 * 
	 * @see org.eclipse.wst.common.componentcore.StructureEdit
	 */
	public static final String MODULE_CORE = "ComponentStructuralProjectBuilderDataModel.MODULE_CORE";
	
	public static final String MODULE_BUILDER_DM_LIST = "BasicComponentStructuralProjectBuilderDataModel.MODULE_BUILDER_DM_LIST"; //$NON-NLS-1$

	/**
	 * <p>
	 * The ComponentStructuralBuilderDataModel constructor. This constructor will first add the base
	 * ComponentStructuralBuilderDataModel properties (PROJECT, BUILD_KIND, PROJECT_DETLA and
	 * MODULE_CORE). 
	 * 
	 * @see #initValidBaseProperties()
	 * 
	 * It then invokes the base WTPOperationDataModel.
	 * 
	 * @see WTPOperationDataModel
	 * 
	 * 
	 */
    public ComponentStructuralProjectBuilderDataModel() {
        super();
    }
	/**
	 * Subclasses should use this method within <code>initValidBaseProperties()</code> to add
	 * properties.
	 * 
	 * @param propertyName
	 *            The property name to be added.
	 * @see #initValidBaseProperties()
	 */
	protected void initValidBaseProperties() {
		addValidBaseProperty(PROJECT);
		addValidBaseProperty(BUILD_KIND);
		addValidBaseProperty(PROJECT_DETLA);
		addValidBaseProperty(MODULE_CORE);
		addValidBaseProperty(MODULE_BUILDER_DM_LIST);
		super.initValidBaseProperties();
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
	private StructureEdit getModuleCore() {
		return (StructureEdit) getProperty(MODULE_CORE);
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
					WorkbenchComponent depWBModule = getModuleCore().findComponentByURI(depModule.getHandle());
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
		StructureEdit moduleCore = getModuleCore();
		List moduleBuilderDataModelList = new ArrayList();
		WorkbenchComponent[] wbModules = moduleCore.getWorkbenchModules();

		if (wbModules == null || wbModules.length == 0) {
			return null;
		}

		List sortedList = computeModuleBuildOrder(wbModules);

		ComponentStructuralBuilderExtensionRegistry registry = null;
		ComponentStructuralBuilderDataModel dataModel = null;
		
        IProjectProperties props = ServerCore.getProjectProperties((IProject)getProperty(PROJECT));
        String runtimeID = props.getRuntimeTarget().getId();

		for (int i = 0; i < sortedList.size(); i++) {
			WorkbenchComponent wbModule = (WorkbenchComponent) sortedList.get(i);
			String id = wbModule.getComponentType().getComponentTypeId();
			if (id == null)
				break;
			dataModel = ComponentStructuralBuilderExtensionRegistry.getComponentStructuralBuilderDMForServerTargetID(runtimeID, wbModule.getComponentType().getComponentTypeId());
			
			if(dataModel == null) 
			    break;
			
			dataModel.setProperty(ComponentStructuralBuilderDataModel.MODULE_CORE, moduleCore);
			dataModel.setProperty(ComponentStructuralBuilderDataModel.PROJECT, getProperty(PROJECT));
			dataModel.setProperty(ComponentStructuralBuilderDataModel.WORKBENCH_MODULE, wbModule);
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
	public WTPOperation getDefaultOperation() {
		return new ComponentStructuralProjectBuilderOperation(this);
	}
	/**
	 * Override this method to compute default property values.
	 * 
	 * @param propertyName
	 * @return
	 */
	protected Object getDefaultProperty(String propertyName) {
		if (propertyName.equals(BUILD_KIND))
			return new Integer(IncrementalProjectBuilder.FULL_BUILD);
		return super.getDefaultProperty(propertyName);
	}
	
}
