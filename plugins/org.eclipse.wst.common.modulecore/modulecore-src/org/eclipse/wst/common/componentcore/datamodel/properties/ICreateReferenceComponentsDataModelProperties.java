package org.eclipse.wst.common.componentcore.datamodel.properties;

public interface ICreateReferenceComponentsDataModelProperties {
	
    /**
     * Required, type ComponentHandle
     */	
	public static final String SOURCE_COMPONENT_HANDLE = "ICreateReferenceComponentsDataModelProperties.SOURCE_COMPONENT_HANDLE";
	
    /**
     * Required, type ArrayList, ArrayList  should contain list of ComponentHandle
     */
	public static final String TARGET_COMPONENTS_HANDLE_LIST = "ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENTS_HANDLE_LIST"; //$NON-NLS-1$
	
	/**
     * Optional, deploy path for the dependent component, default is "/"
     */
	public static final String TARGET_COMPONENTS_DEPLOY_PATH = "ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENT_DEPLOY_PATH"; //$NON-NLS-1$
	
	
	
}
