package org.eclipse.wst.common.componentcore.internal.operation;


public interface IArtifactEditOperationDataModelProperties {

	/**
	 * Required
	 */
	public static final String TYPE_ID = "IArtifactEditOperationDataModelProperties.TYPE_ID"; //$NON-NLS-1$
	
	/**
	 * Required
	 */
	public static final String PROJECT_NAME = "IArtifactEditOperationDataModelProperties.PROJECT_NAME"; //$NON-NLS-1$
	/**
	 * Required
	 */
	public static final String COMPONENT_NAME = "IArtifactEditOperationDataModelProperties.COMPONENT_NAME"; //$NON-NLS-1$
	/**
	 * Optional, should save with prompt...defaults to false
	 */
	public static final String PROMPT_ON_SAVE = "IArtifactEditOperationDataModelProperties.PROMPT_ON_SAVE"; //$NON-NLS-1$
	
	public static final String TARGET_PROJECT = "IArtifactEditOperationDataModelProperties.TARGET_PROJECT"; //$NON-NLS-1$
	
	public static final String TARGET_COMPONENT = "IArtifactEditOperationDataModelProperties.TARGET_COMPONENT"; //$NON-NLS-1$
	
}
