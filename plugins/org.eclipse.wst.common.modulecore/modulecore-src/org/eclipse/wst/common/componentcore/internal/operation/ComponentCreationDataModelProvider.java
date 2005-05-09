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
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.wst.common.componentcore.datamodel.properties.IComponentCreationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonMessages;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonPlugin;
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.IRuntimeType;

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

    public void init() {
        super.init();
        initProjectCreationModel();
    }
    
	public String[] getPropertyNames() {
		return new String[]{PROJECT_NAME, NESTED_PROJECT_CREATION_DM, COMPONENT_NAME, LOCATION, COMPONENT_DEPLOY_NAME, CREATE_DEFAULT_FILES};
	}

    public void propertyChanged(DataModelEvent event) {
        if (event.getFlag() == DataModelEvent.VALUE_CHG) {
            event.getDataModel();
        }
    }

    public boolean propertySet(String propertyName, Object propertyValue) {
        if (COMPONENT_NAME.equals(propertyName)) {
			model.setProperty(COMPONENT_DEPLOY_NAME, propertyValue);
            model.setProperty(PROJECT_NAME, propertyValue);
        } else if (COMPONENT_DEPLOY_NAME.equals(propertyName))
			model.setProperty(COMPONENT_DEPLOY_NAME, propertyValue);        
         return true;
    }
    
    public Object getDefaultProperty(String propertyName) {
        if (propertyName.equals(CREATE_DEFAULT_FILES)) {
            return Boolean.TRUE;
        }
        return super.getDefaultProperty(propertyName);
    }
    
    public IStatus validate(String propertyName) {
        if (propertyName.equals(COMPONENT_NAME)) {
            IStatus status = OK_STATUS;
            String moduleName = model.getStringProperty(COMPONENT_NAME);
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
        } else if (propertyName.equals(PROJECT_NAME)) {
			IStatus status = OK_STATUS;
			String projectName = model.getStringProperty(PROJECT_NAME);
			if (projectName == null || projectName.length()==0) {
				String errorMessage = WTPCommonPlugin.getResourceString(WTPCommonMessages.PROJECT_NAME_EMPTY);
				status =  WTPCommonPlugin.createErrorStatus(errorMessage); 
			}
			return status;
		}else if(propertyName.equals(COMPONENT_DEPLOY_NAME)){
			return OK_STATUS;	
		}else if(propertyName.equals(CREATE_DEFAULT_FILES)){
			return OK_STATUS;
		}
        return super.validate(propertyName);
    }
    
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
    
    protected abstract void initProjectCreationModel();
    
	protected abstract EClass getComponentType();

	protected abstract String getComponentExtension();

	protected abstract String getComponentID();
    
	protected abstract List getProperties();

    protected abstract Integer getDefaultComponentVersion();
}
