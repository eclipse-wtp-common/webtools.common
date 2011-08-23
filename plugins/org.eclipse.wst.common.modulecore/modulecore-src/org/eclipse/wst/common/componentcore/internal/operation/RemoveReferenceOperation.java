/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.operation;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.wst.common.componentcore.datamodel.properties.IAddReferenceDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.ModulecorePlugin;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * This class is meant to be an alternative to RemoveReferenceComponentOperation
 * in order to remove pre-made or pre-crafted IVirtualReferences, rather than 
 * searching for their individual properties
 */
public class RemoveReferenceOperation extends AbstractDataModelOperation {

	public RemoveReferenceOperation() {
		super();
	}

	public RemoveReferenceOperation(IDataModel model) {
		super(model);
	}

	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		removeReferences(monitor);
		return OK_STATUS;
	}

	protected List<IVirtualReference> getListFromModel(IDataModel model) {
		Object value = model.getProperty(IAddReferenceDataModelProperties.TARGET_REFERENCE_LIST);
		List<IVirtualReference> modList = value instanceof List ?  (List<IVirtualReference>)value : Arrays.asList(new IVirtualReference[]{(IVirtualReference)value});
		return modList;
	}

	protected void removeReferences(IProgressMonitor monitor) {
		
		IVirtualComponent sourceComp = (IVirtualComponent) model.getProperty(IAddReferenceDataModelProperties.SOURCE_COMPONENT);
		if (sourceComp == null || !sourceComp.getProject().isAccessible() || sourceComp.isBinary()) return;
		
		Map<String, Object> options = new HashMap<String, Object>();
		options.put(IVirtualComponent.REQUESTED_REFERENCE_TYPE, IVirtualComponent.DISPLAYABLE_REFERENCES_ALL);
		IVirtualReference [] existingReferencesArray = sourceComp.getReferences(options);
		if(existingReferencesArray == null || existingReferencesArray.length == 0){
			return;
		}
		
		List<IVirtualReference> existingReferences = new ArrayList<IVirtualReference>();
		existingReferences.addAll(Arrays.asList(existingReferencesArray));
		List<IVirtualReference> modList = getListFromModel(model);
		List targetprojectList = new ArrayList();
		for (int i = 0; i < modList.size() && !existingReferences.isEmpty(); i++) {
			IVirtualReference ref = modList.get(i);
			if (ref.getReferencedComponent()==null )
				continue;

			IVirtualReference existing = findMatchingReference(existingReferences, 
					ref.getReferencedComponent(), ref.getRuntimePath());
			//if a ref was found matching the specified deployPath, then remove it
			if(existing != null){
				removeRefereneceInComponent(sourceComp, existing);
				existingReferences.remove(existing);
				//after removing the ref, check to see if it was the last ref removed to that component
				//and if it was, then also remove the project reference
				existing = findMatchingReference(existingReferences, ref.getReferencedComponent());
				if(existing == null){
					IProject targetProject = ref.getReferencedComponent().getProject();
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
