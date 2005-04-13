package org.eclipse.wst.common.frameworks.datamodel.properties;

import org.eclipse.wst.common.frameworks.datamodel.IDataModelProperties;
/**
 * <p>
 * IFlexibleProjectCreationDataModelProperties provides properties to the DataModel associated with the 
 * FlexibleProjectCreationDataModelProperties as well as all extending interfaces extending 
 * IFlexibleProjectCreationDataModelProperties specifically, but not limited to the Java releated creatoin in the 
 * JST layer. 
 * @see FlexibleJavaProjectCreationDataModelProvider
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider
 * @see org.eclipse.wst.common.frameworks.datamodel.DataModelFactory
 * @see org.eclipse.wst.common.frameworks.datamodel.IDataModelProperties
 * 
 * @since 1.0
 */
public interface IFlexibleProjectCreationDataModelProperties extends IDataModelProperties {
    /**
     * Required, type String. The user defined name of the target project for the component to be created.
     */
    public static final String PROJECT_NAME = "IFlexibleProjectCreationDataModelProperties.PROJECT_NAME"; //$NON-NLS-1$
    /**
     * Required, type String. The user defined location on disk of the target project for the component to be created.
     * Defaulted to default eclipse workspace location
     */
    public static final String PROJECT_LOCATION = "IFlexibleProjectCreationDataModelProperties.PROJECT_LOCATION"; //$NON-NLS-1$
    /**
     * Required, type IDataModel. The user set IDataModel used to create the initial project.  Providers which currently exist for
     * this IDataModel include IProjectCreationProperties.
     * @see org.eclipse.wst.common.frameworks.internal.operations.IProjectCreationProperties
     */
    public static final String NESTED_MODEL_PROJECT_CREATION = "IFlexibleProjectCreationDataModelProperties.NESTED_MODEL_PROJECT_CREATION"; //$NON-NLS-1$
}
