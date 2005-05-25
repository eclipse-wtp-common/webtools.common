package org.eclipse.wst.common.componentcore.internal.operation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
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
		return OK_STATUS;
	}

	protected void addReferencedComponents(IProgressMonitor monitor) {
		
		ComponentHandle sourceHandle = (ComponentHandle) model.getProperty(ICreateReferenceComponentsDataModelProperties.SOURCE_COMPONENT_HANDLE);
		IVirtualComponent sourceComp = ComponentCore.createComponent(sourceHandle.getProject(), sourceHandle.getName());
		
		List vlist = new ArrayList();
		IVirtualReference[] oldrefs = sourceComp.getReferences();
		for (int i = 0; i < oldrefs.length; i++) {
			IVirtualReference ref = (IVirtualReference) oldrefs[i];
			vlist.add(ref);
		}		

		
        List modList = (List) model.getProperty(ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENTS_HANDLE_LIST);
		for( int i=0; i< modList.size(); i++){
			ComponentHandle handle = (ComponentHandle) modList.get(i);
			IVirtualComponent comp = ComponentCore.createComponent(handle.getProject(), handle.getName());
			IVirtualReference ref = ComponentCore.createReference( sourceComp, comp );
			vlist.add(ref);
		}
		
		
		IVirtualReference[] refs = new IVirtualReference[vlist.size()];
		for (int i = 0; i < vlist.size(); i++) {
			IVirtualReference ref = (IVirtualReference) vlist.get(i);
			refs[i] = ref;
		}
		
		sourceComp.setReferences(refs);
	}

	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		// TODO Auto-generated method stub
		return null;
	}

	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		// TODO Auto-generated method stub
		return null;
	}

}
