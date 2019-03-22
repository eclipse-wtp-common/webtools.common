/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.operation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.wst.common.componentcore.datamodel.properties.ICreateReferenceComponentsDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.ModulecorePlugin;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

public class RemoveReferenceComponentOperation extends AbstractDataModelOperation {

	public RemoveReferenceComponentOperation() {
		super();
	}

	public RemoveReferenceComponentOperation(IDataModel model) {
		super(model);
	}

	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		removeReferencedComponents(monitor);
		return OK_STATUS;
	}

	protected void removeReferencedComponents(IProgressMonitor monitor) {
		
		IVirtualComponent sourceComp = (IVirtualComponent) model.getProperty(ICreateReferenceComponentsDataModelProperties.SOURCE_COMPONENT);
		if (sourceComp == null || !sourceComp.getProject().isAccessible() || sourceComp.isBinary()) return;

		Map<String, Object> options = new HashMap<String, Object>();
		options.put(IVirtualComponent.REQUESTED_REFERENCE_TYPE, IVirtualComponent.DISPLAYABLE_REFERENCES_ALL);
		IVirtualReference [] existingReferencesArray = sourceComp.getReferences(options);
		if(existingReferencesArray == null || existingReferencesArray.length == 0){
			return;
		}
		
		List existingReferences = new ArrayList();
		for(int i=0;i<existingReferencesArray.length; i++){
			existingReferences.add(existingReferencesArray[i]);
		}
		
		IPath path = null;
		//if the deployPath is not explicitly set, then leave it as null so the first reference is removed.
		if(model.isPropertySet( ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENTS_DEPLOY_PATH )){
			String deployPath = model.getStringProperty( ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENTS_DEPLOY_PATH );
			path = new Path( deployPath );
		}
		
		List modList = (List) model.getProperty(ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENT_LIST);
    
		
		List targetprojectList = new ArrayList();

		for (int i = 0; i < modList.size() && !existingReferences.isEmpty(); i++) {
			IVirtualComponent comp = (IVirtualComponent) modList.get(i);
			if (comp==null )
				continue;

			IVirtualReference ref = findMatchingReference(existingReferences, comp, path);
			//if a ref was found matching the specified deployPath, then remove it
			if(ref != null){
				removeRefereneceInComponent(sourceComp, ref);
				existingReferences.remove(ref);
				//after removing the ref, check to see if it was the last ref removed to that component
				//and if it was, then also remove the project reference
				ref = findMatchingReference(existingReferences, comp);
				if(ref == null){
					IProject targetProject = comp.getProject();
					targetprojectList.add(targetProject);
				}
			}
		}
		
		try {
			ProjectUtilities.removeReferenceProjects(sourceComp.getProject(),targetprojectList);
		} catch (CoreException e) {
			ModulecorePlugin.logError(e);
		}		
		
	}

	private IVirtualReference findMatchingReference(List existingReferences, IVirtualComponent comp, IPath path) {
		for(int i=0;i<existingReferences.size(); i++){
			IVirtualReference ref = (IVirtualReference)existingReferences.get(i);
			IVirtualComponent c = ref.getReferencedComponent();
			if(c != null && c.getName().equals(comp.getName())){
				if(path == null){
					return ref;
				} else if(path.equals(ref.getRuntimePath())){
					return ref;
				}
			}
		}
		return null;
	}

	private IVirtualReference findMatchingReference(List existingReferences, IVirtualComponent comp) {
		return findMatchingReference(existingReferences, comp, null);
	}

	protected void removeRefereneceInComponent(IVirtualComponent component, IVirtualReference reference) {
		((VirtualComponent)component.getComponent()).removeReference(reference);
	}

}
