package org.eclipse.wst.common.componentcore.internal.operation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.datamodel.properties.ICreateReferenceComponentsDataModelProperties;
import org.eclipse.wst.common.componentcore.resources.ComponentHandle;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

public class CreateReferenceComponentsOp extends AbstractDataModelOperation {


	public CreateReferenceComponentsOp(IDataModel model) {
		super(model);
	}
	
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		addReferencedComponents(monitor);
		addProjectReferences();
		return OK_STATUS;
	}
	private void addProjectReferences() {
		
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
			ProjectUtilities.addReferenceProjects(sourceProject,targetprojectList);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
	}
	protected void addReferencedComponents(IProgressMonitor monitor) {
		
		ComponentHandle sourceHandle = (ComponentHandle) model.getProperty(ICreateReferenceComponentsDataModelProperties.SOURCE_COMPONENT_HANDLE);
		IVirtualComponent sourceComp = ComponentCore.createComponent(sourceHandle.getProject(), sourceHandle.getName());
		
		List vlist = new ArrayList();
		IVirtualReference[] oldrefs = sourceComp.getReferences();
		for (int i = 0; i < oldrefs.length; i++) {
			IVirtualReference ref = oldrefs[i];
			vlist.add(ref);
		}		

		
        List modList = (List) model.getProperty(ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENTS_HANDLE_LIST);
		for (int i = 0; i < modList.size(); i++) {
			ComponentHandle handle = (ComponentHandle) modList.get(i);
			IVirtualComponent comp = ComponentCore.createComponent(handle.getProject(), handle.getName());
			if (!srcComponentContainsReference(sourceComp, comp)) {
				IVirtualReference ref = ComponentCore.createReference(sourceComp, comp);
				String deployPath = model.getStringProperty(ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENTS_DEPLOY_PATH);
				if(deployPath != null && deployPath.length() > 0)
					ref.setRuntimePath(new Path(deployPath));
				vlist.add(ref);
			}
		}
		
		
		IVirtualReference[] refs = new IVirtualReference[vlist.size()];
		for (int i = 0; i < vlist.size(); i++) {
			IVirtualReference ref = (IVirtualReference) vlist.get(i);
			refs[i] = ref;
		}
		
		sourceComp.setReferences(refs);
	}

	private boolean srcComponentContainsReference(IVirtualComponent sourceComp, IVirtualComponent comp) {
		IVirtualReference[] existingReferences = sourceComp.getReferences();
		for (int i = 0; i < existingReferences.length; i++) {
			if(existingReferences[i].getReferencedComponent().getComponentHandle().equals(comp.getComponentHandle())){
				return true;
			}
		}
		return false;
	}

	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return null;
	}

	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return null;
	}

}
