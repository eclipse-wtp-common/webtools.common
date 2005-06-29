package org.eclipse.wst.common.componentcore.internal.operation;

import java.util.ArrayList;

import org.eclipse.wst.common.componentcore.datamodel.properties.ICreateReferenceComponentsDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;

public class CreateReferenceComponentsDataModelProvider extends AbstractDataModelProvider implements ICreateReferenceComponentsDataModelProperties {
	

	public CreateReferenceComponentsDataModelProvider() {
		super();
		
	}

	public String[] getPropertyNames() {
		String[] props = new String[]{SOURCE_COMPONENT_HANDLE, TARGET_COMPONENTS_HANDLE_LIST,TARGET_COMPONENTS_DEPLOY_PATH};
		return props;
	}

	
	public IDataModelOperation getDefaultOperation() {
		return new CreateReferenceComponentsOp(model);
	}
	
	public Object getDefaultProperty(String propertyName) {
	if (propertyName.equals(TARGET_COMPONENTS_HANDLE_LIST) ) 
			return new ArrayList();
	 return super.getDefaultProperty(propertyName);
	}
	
	
}
