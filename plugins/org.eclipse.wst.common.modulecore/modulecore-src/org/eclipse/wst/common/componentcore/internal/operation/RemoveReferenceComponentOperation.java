/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
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
import java.util.List;

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
		if (!sourceComp.getProject().isAccessible()) return;
		
        List modList = (List) model.getProperty(ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENT_LIST);
    
		List targetprojectList = new ArrayList();

		for (int i = 0; i < modList.size(); i++) {
			IVirtualComponent comp = (IVirtualComponent) modList.get(i);
			if (comp==null || sourceComp==null)
				continue;
			IVirtualReference ref = sourceComp.getReference(comp.getName());
			if( ref != null && ref.getReferencedComponent() != null && ref.getReferencedComponent().isBinary()){
				removeRefereneceInComponent(sourceComp, ref);
			}else{
				if (Arrays.asList(comp.getReferencingComponents()).contains(sourceComp)) {
					
					String deployPath = model.getStringProperty( ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENTS_DEPLOY_PATH );
					IPath path = new Path( deployPath );
					
					if( ref.getRuntimePath() != null && path != null && ref.getRuntimePath().equals( path )){
						removeRefereneceInComponent(sourceComp,sourceComp.getReference(comp.getName()));
						IProject targetProject = comp.getProject();
						targetprojectList.add(targetProject);
					}
				}					
			}
		}
		
		try {
			ProjectUtilities.removeReferenceProjects(sourceComp.getProject(),targetprojectList);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
	}

	protected void removeRefereneceInComponent(IVirtualComponent component, IVirtualReference reference) {
		List refList = new ArrayList();
		IVirtualReference[] refArray = component.getReferences();
		for (int i = 0; i < refArray.length; i++) {
			if (refArray[i].getReferencedComponent() != null && !refArray[i].getReferencedComponent().equals(reference.getReferencedComponent()))
				refList.add(refArray[i]);
		}
		component.setReferences((IVirtualReference[]) refList.toArray(new IVirtualReference[refList.size()]));
	}

	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return null;
	}

	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return null;
	}

}
