/*******************************************************************************
 * Copyright (c) 2003, 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.internal.operations.WTPPropertyDescriptor;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonMessages;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonPlugin;
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.IProjectProperties;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.ServerCore;

/**
 * This dataModel is a common super class used for creation of WTP Components.
 * 
 * This class (and all its fields and methods) is likely to change during the
 * WTP 1.0 milestones as the new project structures are adopted. Use at your own
 * risk.
 * 
 * @since WTP 1.0
 */
public abstract class ComponentCreationDataModelProvider extends AbstractDataModelProvider implements IComponentCreationDataModelProperties{
    
    
	/* (non-Javadoc)
     * @see org.eclipse.wst.common.frameworks.operations.WTPOperationDataModel#init()
     */
    public void init() {
        super.init();
		propertySet(COMPONENT_VERSION, getDefaultProperty(COMPONENT_VERSION));
    }
    
	public String[] getPropertyNames() {
		return new String[]{PROJECT_NAME, COMPONENT_NAME, COMPONENT_DEPLOY_NAME, CREATE_DEFAULT_FILES,
					FINAL_PERSPECTIVE, COMPONENT_VERSION,
					VALID_MODULE_VERSIONS_FOR_PROJECT_RUNTIME};
	}

	
    public void propertyChanged(DataModelEvent event) {
        if (event.getFlag() == DataModelEvent.VALUE_CHG) {
            event.getDataModel();
        }
    }

    public boolean propertySet(String propertyName, Object propertyValue) {

        if (PROJECT_NAME.equals(propertyName) && propertyValue !=null && ((String)propertyValue).length()!=0) {
            IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject((String)propertyValue);
			if (project != null) {
	            IProjectProperties projProperties = ServerCore.getProjectProperties(project);
	            if( projProperties.getRuntimeTarget() != null ){
	            	String[] validModuleVersions = getServerVersions(getComponentID(), projProperties.getRuntimeTarget().getRuntimeType());
	            	propertySet(VALID_MODULE_VERSIONS_FOR_PROJECT_RUNTIME, validModuleVersions);
	            }
			}
        }
        else if (COMPONENT_NAME.equals(propertyName))
			propertySet(COMPONENT_DEPLOY_NAME, propertyValue);
        else if (COMPONENT_DEPLOY_NAME.equals(propertyName))
			getDataModel().setProperty(COMPONENT_DEPLOY_NAME, propertyValue);        
         return true;
    }
    
	public DataModelPropertyDescriptor[] getValidPropertyDescriptors(String propertyName) {
		if (propertyName.equals(COMPONENT_VERSION)) {
			return getValidComponentVersionDescriptors();
		}
		return super.getValidPropertyDescriptors(propertyName);
	}
	
    public IStatus validate(String propertyName) {
        if (propertyName.equals(COMPONENT_NAME)) {
            IStatus status = OK_STATUS;
            String moduleName = getDataModel().getStringProperty(COMPONENT_NAME);
            if (status.isOK()) {
                if (moduleName.indexOf("#") != -1) { //$NON-NLS-1$
                    String errorMessage = WTPCommonPlugin.getResourceString(WTPCommonMessages.ERR_INVALID_CHARS); //$NON-NLS-1$
                    return WTPCommonPlugin.createErrorStatus(errorMessage);
                } else if (moduleName==null || moduleName.equals("")) { //$NON-NLS-1$
					String errorMessage = WTPCommonPlugin.getResourceString(WTPCommonMessages.ERR_EMPTY_MODULE_NAME);
					return WTPCommonPlugin.createErrorStatus(errorMessage); 
                }else
                	return OK_STATUS;
            } else
                return status;

        } else if (COMPONENT_VERSION.equals(propertyName)) {
			return validateComponentVersionProperty();
		} else if (propertyName.equals(PROJECT_NAME)) {
			IStatus status = OK_STATUS;
			String projectName = getDataModel().getStringProperty(PROJECT_NAME);
			if (projectName == null || projectName.length()==0) {
				String errorMessage = WTPCommonPlugin.getResourceString(WTPCommonMessages.PROJECT_NAME_EMPTY);
				status =  WTPCommonPlugin.createErrorStatus(errorMessage); 
			}
			return status;
		}else if(propertyName.equals(COMPONENT_DEPLOY_NAME)){
			return OK_STATUS;
			
		}else if(propertyName.equals(CREATE_DEFAULT_FILES)){
			return OK_STATUS;
		}else if(propertyName.equals(FINAL_PERSPECTIVE)){
			return OK_STATUS;
		}else if(propertyName.equals(VALID_MODULE_VERSIONS_FOR_PROJECT_RUNTIME)){
			return OK_STATUS;
		}
        return super.validate(propertyName);
    }
    
	private IStatus validateComponentVersionProperty() {
		int componentVersion = getIntProperty(COMPONENT_VERSION);
		if (componentVersion == -1)
			return WTPCommonPlugin.createErrorStatus(WTPCommonPlugin.getResourceString(WTPCommonMessages.SPEC_LEVEL_NOT_FOUND));
		return OK_STATUS;
	}
	
    public Object getDefaultProperty(String propertyName) {
        if (propertyName.equals(CREATE_DEFAULT_FILES)) {
            return Boolean.TRUE;
        } else if (propertyName.equals(COMPONENT_VERSION)) {
			return getDefaultComponentVersion();
		}
        return super.getDefaultProperty(propertyName);
    }


	protected abstract DataModelPropertyDescriptor[] getValidComponentVersionDescriptors();

	protected abstract EClass getComponentType();

	protected abstract String getComponentExtension();
	
	protected abstract Integer getDefaultComponentVersion();

	
	
	protected abstract String getComponentID();
	
	protected static String[] getServerVersions(String moduleID, IRuntimeType type) {
        List list = new ArrayList();
        if (type == null)
            return null;
        IModuleType[] moduleTypes = type.getModuleTypes();
        if (moduleTypes != null) {
            int size = moduleTypes.length;
            for (int i = 0; i < size; i++) {
                IModuleType moduleType = moduleTypes[i];
                if (matches(moduleType.getId(), moduleID)) {
                    list.add(moduleType.getVersion());
                }

            }
        }
        String[] versions = null;
        if (!list.isEmpty()) {
            versions = new String[list.size()];
            list.toArray(versions);
        }
        return versions;
    }

    private static boolean matches(String a, String b) {
        if (a == null || b == null || "*".equals(a) || "*".equals(b) || a.startsWith(b) || b.startsWith(a)) //$NON-NLS-1$ //$NON-NLS-2$
            return true;
        return false;
    }
    
    
    protected String getComponentName(){
    	return model.getStringProperty(COMPONENT_NAME);
    }
    
    protected String getComponentDeployName(){
    	return model.getStringProperty(COMPONENT_DEPLOY_NAME);
    }
    
	protected abstract String getVersion();
	protected abstract List getProperties();
	
	public IDataModelOperation getDefaultOperation() {
		return null;
	}
		
}
