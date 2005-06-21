package org.eclipse.wst.common.componentcore.internal.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.datamodel.properties.ICreateReferenceComponentsDataModelProperties;
import org.eclipse.wst.common.componentcore.resources.ComponentHandle;
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
		removeProjectReferences();
		return OK_STATUS;
	}

	private void removeProjectReferences() {
		ComponentHandle handle = (ComponentHandle) model.getProperty(ICreateReferenceComponentsDataModelProperties.SOURCE_COMPONENT_HANDLE);
		IProject sourceProject = handle.getProject();
		List modList = (List) model.getProperty(ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENTS_HANDLE_LIST);
		List targetprojectList = new ArrayList();
		for( int i=0; i< modList.size(); i++){
			ComponentHandle targethandle = (ComponentHandle) modList.get(i);
			IProject targetProject = targethandle.getProject();
			targetprojectList.add(targetProject);
		}
		try {
			ProjectUtilities.removeReferenceProjects(sourceProject,targetprojectList);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void removeReferencedComponents(IProgressMonitor monitor) {
		
		ComponentHandle sourceHandle = (ComponentHandle) model.getProperty(ICreateReferenceComponentsDataModelProperties.SOURCE_COMPONENT_HANDLE);
		IVirtualComponent sourceComp = ComponentCore.createComponent(sourceHandle.getProject(), sourceHandle.getName());
		
        List modList = (List) model.getProperty(ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENTS_HANDLE_LIST);
    
		for (int i = 0; i < modList.size(); i++) {
			ComponentHandle handle = (ComponentHandle) modList.get(i);
			IVirtualComponent comp = ComponentCore.createComponent(handle.getProject(), handle.getName());
			if (Arrays.asList(comp.getReferencingComponents()).contains(sourceComp)) {
				removeRefereneceInComponent(sourceComp,sourceComp.getReference(comp.getName()));
			}
		}
		
	}

	private void removeRefereneceInComponent(IVirtualComponent component, IVirtualReference reference) {
		List refList = new ArrayList();
		IVirtualReference[] refArray = component.getReferences();
		for (int i = 0; i < refArray.length; i++) {
			if (!refArray[i].getReferencedComponent().equals(reference.getReferencedComponent()))
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
