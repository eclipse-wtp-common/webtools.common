package org.eclipse.wst.common.componentcore.internal.operation;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

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
	
	public IProject getTargetProject();
	
	public IVirtualComponent getTargetComponent();
}
