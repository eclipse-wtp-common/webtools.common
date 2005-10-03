package org.eclipse.wst.common.componentcore.datamodel.properties;

public interface ICreateReferenceComponentsDataModelProperties {
	
    /**
     * Required, type IVirtualComponent
     */	
	public static final String SOURCE_COMPONENT = "ICreateReferenceComponentsDataModelProperties.SOURCE_COMPONENT";
	
    /**
     * Required, type ArrayList, ArrayList  should contain list of IVirtualComponent
     */
	public static final String TARGET_COMPONENT_LIST = "ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENT"; //$NON-NLS-1$
	
	/**
     * Optional, deploy path for the dependent component, default is "/"
     */
	public static final String TARGET_COMPONENTS_DEPLOY_PATH = "ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENT_DEPLOY_PATH"; //$NON-NLS-1$
	
	
	
}
