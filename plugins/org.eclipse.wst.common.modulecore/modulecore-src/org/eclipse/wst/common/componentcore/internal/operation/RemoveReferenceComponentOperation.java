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
		removeProjectReferences();
		return OK_STATUS;
	}

	private void removeProjectReferences() {
		IVirtualComponent sourceComp = (IVirtualComponent) model.getProperty(ICreateReferenceComponentsDataModelProperties.SOURCE_COMPONENT);
		List modList = (List) model.getProperty(ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENT_LIST);
		List targetprojectList = new ArrayList();
		for( int i=0; i< modList.size(); i++){
			IVirtualComponent targethandle = (IVirtualComponent) modList.get(i);
			IProject targetProject = targethandle.getProject();
			targetprojectList.add(targetProject);
		}
		try {
			ProjectUtilities.removeReferenceProjects(sourceComp.getProject(),targetprojectList);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void removeReferencedComponents(IProgressMonitor monitor) {
		
		IVirtualComponent sourceComp = (IVirtualComponent) model.getProperty(ICreateReferenceComponentsDataModelProperties.SOURCE_COMPONENT);
		//IVirtualComponent sourceComp = ComponentCore.createComponent(sourceProject);
		
        List modList = (List) model.getProperty(ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENT_LIST);
    
		for (int i = 0; i < modList.size(); i++) {
			IVirtualComponent comp = (IVirtualComponent) modList.get(i);
			IVirtualReference ref = sourceComp.getReference(comp.getName());
			if( ref != null && ref.getReferencedComponent() != null && ref.getReferencedComponent().isBinary()){
				removeRefereneceInComponent(sourceComp, ref);
			}else{
				//IVirtualComponent comp = ComponentCore.createComponent(handle);
				if (Arrays.asList(comp.getReferencingComponents()).contains(sourceComp)) {
					removeRefereneceInComponent(sourceComp,sourceComp.getReference(comp.getName()));
				}					
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
