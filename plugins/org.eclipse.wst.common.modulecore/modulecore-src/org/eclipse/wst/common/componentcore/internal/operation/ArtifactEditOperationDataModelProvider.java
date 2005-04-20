package org.eclipse.wst.common.componentcore.internal.operation;

import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.componentcore.ArtifactEdit;
import org.eclipse.wst.common.componentcore.StructureEdit;
import org.eclipse.wst.common.componentcore.UnresolveableURIException;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.internal.operations.ProjectCreationDataModel;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonMessages;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonPlugin;

public class ArtifactEditOperationDataModelProvider extends AbstractDataModelProvider implements IArtifactEditOperationDataModelProperties {

	public ArtifactEditOperationDataModelProvider() {
		super();
	}

	public String[] getPropertyNames() {
		return new String[]{PROJECT_NAME, MODULE_NAME,PROMPT_ON_SAVE};
	}
	
	public IProject getTargetProject() {
		return ProjectCreationDataModel.getProjectHandleFromProjectName(getStringProperty(PROJECT_NAME));
	}
	
	public String[] addToSuperPropertyNames(String[] propertyNames) {
		String[] thisProperties = getPropertyNames();
		System.arraycopy(propertyNames,0,thisProperties,0,thisProperties.length);
		return thisProperties;
		
	}
	
	public boolean propertySet(String propertyName, Object propertyValue) {
	    boolean status = super.propertySet(propertyName, propertyValue);
	    if(MODULE_NAME.equals(propertyName)){
	        WorkbenchComponent module = getWorkbenchModule();
	        IProject proj = getProjectForGivenComponent(module);
	        if(proj != null)
	            setProperty(PROJECT_NAME, proj.getName());
	    }
	    return status;
	}
	
	public Object getDefaultProperty(String propertyName) {
		if (propertyName.equals(PROMPT_ON_SAVE))
			return Boolean.FALSE;
		return super.getDefaultProperty(propertyName);
	}
	
	 /**
     * @return
     */
    public WorkbenchComponent getWorkbenchModule() {
        StructureEdit moduleCore = null;
        WorkbenchComponent module = null;
        try {
            moduleCore = StructureEdit.getStructureEditForRead(getTargetProject());
            module = moduleCore.findComponentByName(getStringProperty(MODULE_NAME));
        } finally {
            if (null != moduleCore) {
                moduleCore.dispose();
            }
        }
        return module;
    }
	private IProject getProjectForGivenComponent(WorkbenchComponent wbComp) {
	    IProject modProject = null;
	    try {
		    modProject = StructureEdit.getContainingProject(wbComp.getHandle());
	    } catch (UnresolveableURIException ex) {
			Logger.getLogger().logError(ex);
	    }
	    return modProject;
	}
	
	public ArtifactEdit getArtifactEditForRead(){
		WorkbenchComponent module = getWorkbenchModule(); 
		return ArtifactEdit.getArtifactEditForRead(module);
	}
	
	public IStatus validate(String propertyName) {
		IStatus result = super.validate(propertyName);
		if (!result.isOK())
			return result;
		if (propertyName.equals(PROJECT_NAME))
			return validateProjectName();
		else if (propertyName.equals(MODULE_NAME))
			return validateModuleName();
		return result;
	}
	
	protected IStatus validateProjectName() {
		String projectName = getStringProperty(PROJECT_NAME);
		if (projectName == null || projectName.length()==0)
			return WTPCommonPlugin.createErrorStatus(WTPCommonPlugin.getResourceString(WTPCommonMessages.PROJECT_NAME_EMPTY));
		return WTPCommonPlugin.OK_STATUS;
	}
	
	protected IStatus validateModuleName() {
		String moduleName = getStringProperty(MODULE_NAME);
		if (moduleName==null || moduleName.length()==0)
			return WTPCommonPlugin.createErrorStatus(WTPCommonPlugin.getResourceString(WTPCommonMessages.ERR_EMPTY_MODULE_NAME));
		return WTPCommonPlugin.OK_STATUS;
	}
	
}
