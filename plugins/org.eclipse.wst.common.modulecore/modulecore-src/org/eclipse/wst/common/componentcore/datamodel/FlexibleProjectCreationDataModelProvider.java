package org.eclipse.wst.common.componentcore.datamodel;

import java.util.Set;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFlexibleProjectCreationDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.operation.FlexibleProjectCreationOperation;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.internal.operations.IProjectCreationProperties;
import org.eclipse.wst.common.frameworks.internal.operations.ProjectCreationDataModelProvider;

public class FlexibleProjectCreationDataModelProvider extends AbstractDataModelProvider implements IFlexibleProjectCreationDataModelProperties {

	public FlexibleProjectCreationDataModelProvider() {
		super();

	}

	public void init() {
		super.init();
		initNestedProjectModel();
	}

	protected void initNestedProjectModel() {
		IDataModel projModel = DataModelFactory.createDataModel(new ProjectCreationDataModelProvider());
		model.addNestedModel(NESTED_MODEL_PROJECT_CREATION, projModel);
	}

	public Set getPropertyNames() {
		Set propertyNames = super.getPropertyNames();
		propertyNames.add(PROJECT_NAME);
		propertyNames.add(PROJECT_LOCATION);
		propertyNames.add(NESTED_MODEL_PROJECT_CREATION);
		return propertyNames;
	}

	public Object getDefaultProperty(String propertyName) {
		if (PROJECT_LOCATION.equals(propertyName)) {
			return getDefaultLocation();
		}
		return super.getDefaultProperty(propertyName);
	}

	public boolean propertySet(String propertyName, Object propertyValue) {
		boolean status = super.propertySet(propertyName, propertyValue);
		if (PROJECT_NAME.equals(propertyName)) {
			IDataModel projModel = model.getNestedModel(NESTED_MODEL_PROJECT_CREATION);
			projModel.setProperty(IProjectCreationProperties.PROJECT_NAME, propertyValue);
		} else if (PROJECT_LOCATION.equals(propertyName)) {
			IDataModel projModel = model.getNestedModel(NESTED_MODEL_PROJECT_CREATION);
			projModel.setProperty(IProjectCreationProperties.PROJECT_LOCATION, propertyValue);
		}
		return status;
	}

	public IStatus validate(String propertyName) {
		if (PROJECT_NAME.equals(propertyName)) {
			return validateProjectName();
		} else if (PROJECT_LOCATION.equals(propertyName)) {
			return validateProjectLocation();
		}
		return OK_STATUS;
	}

	private IStatus validateProjectName() {
		IDataModel projModel = model.getNestedModel(NESTED_MODEL_PROJECT_CREATION);
		return projModel.validateProperty(IProjectCreationProperties.PROJECT_NAME);
	}

	private IStatus validateProjectLocation() {
		IDataModel projModel = model.getNestedModel(NESTED_MODEL_PROJECT_CREATION);
		return projModel.validateProperty(IProjectCreationProperties.PROJECT_LOCATION);
	}

	private String getDefaultLocation() {
		IPath path = getRootLocation();
		String projectName = (String) getProperty(PROJECT_NAME);
		if (projectName != null)
			path = path.append(projectName);
		return path.toOSString();
	}

	private IPath getRootLocation() {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation();
	}

	//	
	// protected void initNestedModels() {
	// super.initNestedModels();
	// initProjectModel();
	// addNestedModel(NESTED_MODEL_PROJECT_CREATION, projectDataModel);
	//
	// serverTargetDataModel = new J2EEProjectServerTargetDataModel();
	// addNestedModel(NESTED_MODEL_SERVER_TARGET, serverTargetDataModel);
	// }

	// protected void initProjectModel() {
	// projectDataModel = new ProjectCreationDataModel();
	// }
	//	
	public IDataModelOperation getDefaultOperation() {
		return new FlexibleProjectCreationOperation(model);
	}
}
