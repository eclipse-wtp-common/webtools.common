package org.eclipse.wst.common.componentcore.internal.operation;

import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;


public class RemoveReferenceComponentsDataModelProvider extends CreateReferenceComponentsDataModelProvider {

	public RemoveReferenceComponentsDataModelProvider() {
		super();
	}

	public String[] getPropertyNames() {
		return super.getPropertyNames();
	}
	
	public IDataModelOperation getDefaultOperation() {
		return new RemoveReferenceComponentOperation(model);
	}

}
