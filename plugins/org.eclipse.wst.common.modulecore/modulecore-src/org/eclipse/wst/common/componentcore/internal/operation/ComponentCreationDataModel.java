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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModelEvent;
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
public abstract class ComponentCreationDataModel extends WTPOperationDataModel {
    
    /**
     * Required
     */
    public static final String PROJECT_NAME = "ComponentCreationDataModel.PROJECT_NAME"; //$NON-NLS-1$

    /**
     * Required
     */
    public static final String COMPONENT_NAME = "ComponentCreationDataModel.COMPONENT_NAME"; //$NON-NLS-1$
	/**
	 * Required
	 */	
	
	public static final String COMPONENT_DEPLOY_NAME = "ComponentCreationDataModel.MODULE_DEPLOY_NAME"; //$NON-NLS-1$
	
    /**
     * An optional dataModel propertyName for a <code>Boolean</code> type. The
     * default value is <code>Boolean.TRUE</code>. If this property is set to
     * <code>Boolean.TRUE</code> then a default deployment descriptor and
     * supporting bindings files will be generated.
     */
    public static final String CREATE_DEFAULT_FILES = "ComponentCreationDataModel.CREATE_DEFAULT_FILES"; //$NON-NLS-1$

    /**
     * An optional dataModel propertyName for a <code>Boolean</code> type. The
     * default value is <code>Boolean.TRUE</code>. If this property is set to
     * <code>Boolean.TRUE</code> then a default deployment descriptor and
     * supporting bindings files will be generated.
     */
    public static final String SHOULD_CREATE_PROJECT = "ComponentCreationDataModel.SHOULD_CREATE_PROJECT"; //$NON-NLS-1$

    /**
     * Optional, type String
     */
    public static final String FINAL_PERSPECTIVE = "ComponentCreationDataModel.FINAL_PERSPECTIVE"; //$NON-NLS-1$

    protected static final String IS_ENABLED = "ComponentCreationDataModel.IS_ENABLED"; //$NON-NLS-1$
	
	/**
	 * type Integer
	 */
	public static final String COMPONENT_VERSION = "ComponentCreationDataModel.COMPONENT_VERSION"; //$NON-NLS-1$
	
	/**
	 * type Integer
	 */
	public static final String VALID_MODULE_VERSIONS_FOR_PROJECT_RUNTIME = "ComponentCreationDataModel.VALID_MODULE_VERSIONS_FOR_PROJECT_RUNTIME"; //$NON-NLS-1$

	/* (non-Javadoc)
     * @see org.eclipse.wst.common.frameworks.operations.WTPOperationDataModel#init()
     */
    protected void init() {
        super.init();
		setProperty(COMPONENT_VERSION, getDefaultProperty(COMPONENT_VERSION));
    }
    
    protected void initValidBaseProperties() {
        addValidBaseProperty(PROJECT_NAME);
        addValidBaseProperty(COMPONENT_NAME);
        addValidBaseProperty(COMPONENT_DEPLOY_NAME);
        addValidBaseProperty(CREATE_DEFAULT_FILES);
        addValidBaseProperty(IS_ENABLED);
        addValidBaseProperty(FINAL_PERSPECTIVE);
		addValidBaseProperty(COMPONENT_VERSION);
		addValidBaseProperty(VALID_MODULE_VERSIONS_FOR_PROJECT_RUNTIME);
        super.initValidBaseProperties();
    }

    protected Boolean basicIsEnabled(String propertyName) {
        return (Boolean) getProperty(IS_ENABLED);
    }

    public void propertyChanged(WTPOperationDataModelEvent event) {
        if (event.getFlag() == WTPOperationDataModelEvent.PROPERTY_CHG) {
            event.getDataModel();
        }
        super.propertyChanged(event);
    }

    protected boolean doSetProperty(String propertyName, Object propertyValue) {
        super.doSetProperty(propertyName, propertyValue);
        if (PROJECT_NAME.equals(propertyName) && propertyValue !=null && ((String)propertyValue).length()!=0) {
            IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject((String)propertyValue);
			if (project != null) {
	            IProjectProperties projProperties = ServerCore.getProjectProperties(project);
	            if( projProperties.getRuntimeTarget() != null ){
	            	String[] validModuleVersions = getServerVersions(getComponentID(), projProperties.getRuntimeTarget().getRuntimeType());
	            	setProperty(VALID_MODULE_VERSIONS_FOR_PROJECT_RUNTIME, validModuleVersions);
	            }
			}
        } else if (IS_ENABLED.equals(propertyName)) {
            notifyEnablementChange(PROJECT_NAME);
        }  else if (COMPONENT_NAME.equals(propertyName))
			setProperty(COMPONENT_DEPLOY_NAME, propertyValue);
        return true;
    }
    
	protected WTPPropertyDescriptor[] doGetValidPropertyDescriptors(String propertyName) {
		if (propertyName.equals(COMPONENT_VERSION)) {
			return getValidComponentVersionDescriptors();
		}
		return super.doGetValidPropertyDescriptors(propertyName);
	}
	
    protected IStatus doValidateProperty(String propertyName) {
        if (propertyName.equals(COMPONENT_NAME)) {
            IStatus status = OK_STATUS;
            String moduleName = getStringProperty(COMPONENT_NAME);
            if (status.isOK()) {
                if (moduleName.indexOf("#") != -1) { //$NON-NLS-1$
                    String errorMessage = WTPCommonPlugin.getResourceString(WTPCommonMessages.ERR_INVALID_CHARS); //$NON-NLS-1$
                    return WTPCommonPlugin.createErrorStatus(errorMessage);
                } else if (moduleName==null || moduleName.equals("")) { //$NON-NLS-1$
					String errorMessage = WTPCommonPlugin.getResourceString(WTPCommonMessages.ERR_EMPTY_MODULE_NAME);
					return WTPCommonPlugin.createErrorStatus(errorMessage); 
                }
            } else
                return status;

        } else if (propertyName.equals(NESTED_MODEL_VALIDATION_HOOK)) {
            return OK_STATUS;
        }  else if (COMPONENT_VERSION.equals(propertyName)) {
			return validateComponentVersionProperty();
		} else if (propertyName.equals(PROJECT_NAME)) {
			String projectName = getStringProperty(PROJECT_NAME);
			if (projectName == null || projectName.length()==0) {
				String errorMessage = WTPCommonPlugin.getResourceString(WTPCommonMessages.PROJECT_NAME_EMPTY);
				return WTPCommonPlugin.createErrorStatus(errorMessage); 
			}
		}
        return super.doValidateProperty(propertyName);
    }
    
	private IStatus validateComponentVersionProperty() {
		int componentVersion = getIntProperty(COMPONENT_VERSION);
		if (componentVersion == -1)
			return WTPCommonPlugin.createErrorStatus(WTPCommonPlugin.getResourceString(WTPCommonMessages.SPEC_LEVEL_NOT_FOUND));
		return OK_STATUS;
	}
	
    protected Object getDefaultProperty(String propertyName) {
        if (propertyName.equals(CREATE_DEFAULT_FILES) || propertyName.equals(IS_ENABLED)) {
            return Boolean.TRUE;
        } else if (propertyName.equals(COMPONENT_VERSION)) {
			return getDefaultComponentVersion();
		}
        return super.getDefaultProperty(propertyName);
    }

    protected boolean isResultProperty(String propertyName) {
        if (propertyName.equals(FINAL_PERSPECTIVE))
            return true;
        return super.isResultProperty(propertyName);
    }
	protected abstract WTPPropertyDescriptor[] getValidComponentVersionDescriptors();

	protected abstract EClass getComponentType();

	protected abstract String getComponentExtension();
	
	protected abstract Integer getDefaultComponentVersion();
	
	protected abstract String getComponentID();
	
	public static String[] getServerVersions(String moduleID, IRuntimeType type) {
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
    
    public IProject getProject(){
    	String projName = getStringProperty(PROJECT_NAME);
    	return ProjectUtilities.getProject(projName);
    }
    
    public String getComponentName(){
    	return getStringProperty(COMPONENT_NAME);
    }
    
    public String getComponentDeployName(){
    	return getStringProperty(COMPONENT_DEPLOY_NAME);
    }
	protected abstract String getVersion();
	protected abstract List getProperties();
}
