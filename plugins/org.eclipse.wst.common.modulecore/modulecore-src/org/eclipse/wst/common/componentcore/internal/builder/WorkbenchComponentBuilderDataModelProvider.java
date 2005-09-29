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
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.wst.common.componentcore.datamodel.properties.IReferencedComponentBuilderDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IWorkbenchComponentBuilderDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.internal.enablement.DataModelEnablementFactory;

public class WorkbenchComponentBuilderDataModelProvider extends AbstractDataModelProvider implements IWorkbenchComponentBuilderDataModelProperties {

	public Set getPropertyNames() {
		Set propertyNames = super.getPropertyNames();
		propertyNames.add(OUTPUT_CONTAINER);
		propertyNames.add(VIRTUAL_COMPONENT);
		propertyNames.add(DEPENDENT_COMPONENT_DM_LIST);
		propertyNames.add(BUILD_KIND_FOR_DEP);
		return propertyNames;
	}

	public boolean propertySet(String propertyName, Object propertyValue) {
		if (propertyName.equals(VIRTUAL_COMPONENT)) {
			model.setProperty(OUTPUT_CONTAINER, populateOutputContainer());
			model.setProperty(DEPENDENT_COMPONENT_DM_LIST, populateDependentModulesDM());
			// if(model.isPropertySet(BUILD_KIND_FOR_DEP))
			// updateDepGraphIfNecessary();
		}
		// else if(propertyName.equals(BUILD_KIND_FOR_DEP) &&
		// model.isPropertySet(WORKBENCH_COMPONENT)) {
		// updateDepGraphIfNecessary();
		// }
		return true;
	}

	// private void updateDepGraphIfNecessary() {
	// if(model.getIntProperty(BUILD_KIND_FOR_DEP) != IncrementalProjectBuilder.INCREMENTAL_BUILD) {
	// ComponentHandle componentHandle;
	// ComponentHandle refComponentHandle;
	// IProject project = null;
	//            
	// IProject refProject = (IProject)model.getProperty(PROJECT);
	// WorkbenchComponent wbModule = (WorkbenchComponent)model.getProperty(WORKBENCH_COMPONENT);
	// List depModules = wbModule.getReferencedComponents();
	//            
	// for(int i = 0; i<depModules.size(); i++){
	// project = null;
	// refComponentHandle = ComponentHandle.create(refProject, wbModule.getName());
	//                
	// try {
	// project =
	// StructureEdit.getContainingProject(((ReferencedComponent)depModules.get(i)).getHandle());
	// } catch (UnresolveableURIException e) {
	// Logger.getLogger().log(e.getMessage());
	// }
	//                
	// if(project != null) {
	// componentHandle = ComponentHandle.create(project,
	// ((ReferencedComponent)depModules.get(i)).getHandle());
	// DependencyGraph.getInstance().addReference(componentHandle, refComponentHandle);
	// }
	// }
	// }
	// }

	private Object populateDependentModulesDM() {
		IVirtualComponent vComponent = (IVirtualComponent) model.getProperty(VIRTUAL_COMPONENT);
		IVirtualReference[] vReferences = vComponent.getReferences();
		List depModulesDataModels = new ArrayList(vReferences.length);
		IDataModel dependentDataModel = null;
		IProject project = vComponent.getProject();
		IVirtualReference vReference = null;
		Object outputContainer = model.getProperty(OUTPUT_CONTAINER);
		for (int i = 0; i < vReferences.length; i++) {
			dependentDataModel = DataModelEnablementFactory.createDataModel(IModuleConstants.DEPENDENT_MODULE + ".builder", project);
			if (dependentDataModel != null) {
				dependentDataModel.setProperty(IReferencedComponentBuilderDataModelProperties.VIRTUAL_REFERENCE, vReferences[i]);
				depModulesDataModels.add(dependentDataModel);
			}
		}
		return depModulesDataModels;
	}

	/**
	 * @return
	 */
	private Object populateOutputContainer() {
		IVirtualComponent vComponent = (IVirtualComponent) model.getProperty(VIRTUAL_COMPONENT);
		IFolder outputContainer = null;
		if (vComponent != null)
			outputContainer = StructureEdit.getOutputContainerRoot(vComponent);
		return outputContainer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#getDefaultOperation()
	 */
	public IDataModelOperation getDefaultOperation() {
		return new WorkbenchComponentBuilderOperation(model);
	}

}
