package org.eclipse.wst.common.componentcore.internal.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.wst.common.componentcore.ArtifactEdit;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.resources.ComponentHandle;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonMessages;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonPlugin;

public class ArtifactEditOperationDataModelProvider extends AbstractDataModelProvider implements IArtifactEditOperationDataModelProperties {

	public ArtifactEditOperationDataModelProvider() {
		super();
	}

	public String[] getPropertyNames() {
		return new String[]{TYPE_ID,PROJECT_NAME, COMPONENT_NAME,PROMPT_ON_SAVE,TARGET_PROJECT,TARGET_COMPONENT};
	}
	
	public IProject getTargetProject() {
		String projectName = (String)model.getProperty(IArtifactEditOperationDataModelProperties.PROJECT_NAME);
		if(projectName != null)
			return ProjectUtilities.getProject(projectName);
		return null;
	}
	
	public String[] addToSuperPropertyNames(String[] propertyNames,String[] superPropertyNames) {
		List allNames = new ArrayList();
		allNames.addAll(Arrays.asList(propertyNames));
		allNames.addAll(Arrays.asList(superPropertyNames));
		String[] allStrings = new String[allNames.size()];
		for(int i = 0; i < allNames.size();i++) {
			allStrings[i] = (String)allNames.get(i);
		}
		return allStrings;
		
	}
	
	public Object getDefaultProperty(String propertyName) {
		if (propertyName.equals(PROMPT_ON_SAVE))
			return Boolean.FALSE;
		if (propertyName.equals(TARGET_PROJECT))
			return getTargetProject();
		if (propertyName.equals(TARGET_COMPONENT))
			return getTargetComponent();
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
            module = moduleCore.findComponentByName(getStringProperty(COMPONENT_NAME));
        } finally {
            if (null != moduleCore) {
                moduleCore.dispose();
            }
        }
        return module;
    }
	
	public ArtifactEdit getArtifactEditForRead(){
		WorkbenchComponent module = getWorkbenchModule(); 
		ComponentHandle handle = ComponentHandle.create(StructureEdit.getContainingProject(module),module.getName());
		return ArtifactEdit.getArtifactEditForRead(handle);
	}
	
	public IStatus validate(String propertyName) {
		IStatus result = super.validate(propertyName);
		if (result != null && !result.isOK())
			return result;
		else if (propertyName.equals(COMPONENT_NAME))
			return validateModuleName();
		return result;
	}
	
	protected IStatus validateModuleName() {
		String moduleName = getStringProperty(COMPONENT_NAME);
		if (moduleName==null || moduleName.length()==0)
			return WTPCommonPlugin.createErrorStatus(WTPCommonPlugin.getResourceString(WTPCommonMessages.ERR_EMPTY_MODULE_NAME));
		return WTPCommonPlugin.OK_STATUS;
	}

	public IVirtualComponent getTargetComponent() {
		String moduleName = getStringProperty(COMPONENT_NAME);
		if(moduleName != null && moduleName.length() > 0)
			return ComponentCore.createComponent(getTargetProject(),moduleName);
		return null;
			
		
	}
	
}
