package org.eclipse.wst.common.componentcore.datamodel.properties;

public interface ICreateReferenceComponentsDataModelProperties {
	
    /**
     * Required, type ComponentHandle
     */	
	public static final String SOURCE_COMPONENT_PROJECT = "ICreateReferenceComponentsDataModelProperties.SOURCE_COMPONENT_PROJECT";
	
    /**
     * Required, type ArrayList, ArrayList  should contain list of ComponentHandle
     */
	public static final String TARGET_COMPONENT_PROJECT_LIST = "ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENT_PROJECT_LIST"; //$NON-NLS-1$
	
	/**
     * Optional, deploy path for the dependent component, default is "/"
     */
	public static final String TARGET_COMPONENTS_DEPLOY_PATH = "ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENT_DEPLOY_PATH"; //$NON-NLS-1$
	
	
	
}
