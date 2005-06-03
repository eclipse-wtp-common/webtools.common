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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.datamodel.properties.IComponentCreationDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.properties.IFlexibleProjectCreationDataModelProperties;
import org.eclipse.wst.common.frameworks.internal.FlexibleJavaProjectPreferenceUtil;
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
public abstract class ComponentCreationDataModelProvider extends AbstractDataModelProvider implements IComponentCreationDataModelProperties {

    protected boolean isProjMultiComponents = false;
    
	public void init() {
		super.init();
        isProjMultiComponents = FlexibleJavaProjectPreferenceUtil.getMultipleModulesPerProjectProp();
		initProjectCreationModel();
	}

	public String[] getPropertyNames() {
		return new String[]{PROJECT_NAME, NESTED_PROJECT_CREATION_DM, COMPONENT_NAME, LOCATION, COMPONENT_DEPLOY_NAME, CREATE_DEFAULT_FILES, COMPONENT};
	}

	public void propertyChanged(DataModelEvent event) {
		if (event.getFlag() == DataModelEvent.VALUE_CHG) {
			event.getDataModel();
		}
	}

	public boolean propertySet(String propertyName, Object propertyValue) {
		if (COMPONENT_NAME.equals(propertyName)) {
			model.setProperty(COMPONENT_DEPLOY_NAME, propertyValue);
            if(!FlexibleJavaProjectPreferenceUtil.getMultipleModulesPerProjectProp())
                model.setProperty(PROJECT_NAME, propertyValue);
        } else if (COMPONENT_DEPLOY_NAME.equals(propertyName)){
			model.setProperty(COMPONENT_DEPLOY_NAME, propertyValue);
		} else if (COMPONENT.equals(propertyName)) {
			throw new RuntimeException(propertyName + " should not be set.");
		}else if (PROJECT_NAME.equals(propertyName)) {
            //if(!FlexibleJavaProjectPreferenceUtil.getMultipleModulesPerProjectProp()){
				//model.notifyPropertyChange(PROJECT_NAME, IDataModel.VALUE_CHG);
				//set the property in nested FlexibleJavaProjectCreationDataModelProvider
				IDataModel dm = (IDataModel)model.getNestedModel(NESTED_PROJECT_CREATION_DM);
	            dm.setProperty(IFlexibleProjectCreationDataModelProperties.PROJECT_NAME, propertyValue);				
				return true; 
            //}	
        }
		return true;
	}

	public Object getDefaultProperty(String propertyName) {
		if (propertyName.equals(CREATE_DEFAULT_FILES)) {
			return Boolean.TRUE;
		} else if (propertyName.equals(COMPONENT)) {
			String projectName = getStringProperty(PROJECT_NAME);
			IProject project = ProjectUtilities.getProject(projectName);
			return ComponentCore.createComponent(project, getStringProperty(COMPONENT_NAME));
		}
		return super.getDefaultProperty(propertyName);
	}

	public IStatus validate(String propertyName) {
		if (propertyName.equals(COMPONENT_NAME)) {
			IStatus status = OK_STATUS;
			String moduleName = model.getStringProperty(COMPONENT_NAME);
			if (status.isOK()) {
                if (moduleName.indexOf("#") != -1 || moduleName.indexOf("/") != -1) { //$NON-NLS-1$
					String errorMessage = WTPCommonPlugin.getResourceString(WTPCommonMessages.ERR_INVALID_CHARS); //$NON-NLS-1$
					return WTPCommonPlugin.createErrorStatus(errorMessage);
				} else if (moduleName == null || moduleName.equals("")) { //$NON-NLS-1$
					String errorMessage = WTPCommonPlugin.getResourceString(WTPCommonMessages.ERR_EMPTY_MODULE_NAME);
					return WTPCommonPlugin.createErrorStatus(errorMessage);
				} else
					return OK_STATUS;
			} else
				return status;
		} 
		else if (propertyName.equals(PROJECT_NAME)) {
			IStatus status = OK_STATUS;
			if(!FlexibleJavaProjectPreferenceUtil.getMultipleModulesPerProjectProp()){
				String projectName = model.getStringProperty(PROJECT_NAME);
				if (projectName == null || projectName.length() == 0) {
					String errorMessage = WTPCommonPlugin.getResourceString(WTPCommonMessages.PROJECT_NAME_EMPTY);
					status = WTPCommonPlugin.createErrorStatus(errorMessage);
				}
				if( status.isOK()){
					status = validateProjectName(projectName);	
				}
	            if(status.isOK() && !FlexibleJavaProjectPreferenceUtil.getMultipleModulesPerProjectProp()){
	                IProject proj = ProjectUtilities.getProject(projectName);
	                if(proj.exists()) {
	                    String errorMessage = WTPCommonPlugin.getResourceString(WTPCommonMessages.PROJECT_EXISTS_ERROR);
	                    status =  WTPCommonPlugin.createErrorStatus(errorMessage); 
	                }
	            }
			}
			return status;
		}
		else if (propertyName.equals(COMPONENT_DEPLOY_NAME)) {
			return OK_STATUS;
		} else if (propertyName.equals(CREATE_DEFAULT_FILES)) {
			return OK_STATUS;
		}
		return OK_STATUS;
	}
	
	protected  IStatus validateProjectName(String projectName) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IStatus status = workspace.validateName(projectName, IResource.PROJECT);
		if (!status.isOK())
			return status;

		if (ProjectUtilities.getProject(projectName).exists())
			return WTPCommonPlugin.createErrorStatus(WTPCommonPlugin.getResourceString(WTPCommonMessages.PROJECT_EXISTS_ERROR, new Object[] { projectName }));
		
		if ( projectName.indexOf("#") != -1 ){ //$NON-NLS-1$
			//String errorMessage = J2EECreationResourceHandler.getString("InvalidCharsError"); //$NON-NLS-1$
			String errorMessage = "InvalidCharsError"; //$NON-NLS-1$
			return WTPCommonPlugin.createErrorStatus(errorMessage);
		}	
		return OK_STATUS;
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

	protected static boolean matches(String serverTypeID, String j2eeModuleID) {

		if (serverTypeID.equals("j2ee")) {
			if (j2eeModuleID.equals(IModuleConstants.JST_WEB_MODULE) || j2eeModuleID.equals(IModuleConstants.JST_EJB_MODULE) || j2eeModuleID.equals(IModuleConstants.JST_EAR_MODULE) || j2eeModuleID.equals(IModuleConstants.JST_APPCLIENT_MODULE) || j2eeModuleID.equals(IModuleConstants.JST_CONNECTOR_MODULE)) {
				return true;
			}
		}else if (serverTypeID.equals("j2ee.*")) {
			if (j2eeModuleID.equals(IModuleConstants.JST_WEB_MODULE) || j2eeModuleID.equals(IModuleConstants.JST_EJB_MODULE) || j2eeModuleID.equals(IModuleConstants.JST_EAR_MODULE) || j2eeModuleID.equals(IModuleConstants.JST_APPCLIENT_MODULE) || j2eeModuleID.equals(IModuleConstants.JST_CONNECTOR_MODULE)) {
				return true;
			}
		} else if (serverTypeID.equals("j2ee.web")) {//$NON-NLS-1$
			if (j2eeModuleID.equals(IModuleConstants.JST_WEB_MODULE)) {
				return true;
			}
		} else if (serverTypeID.equals("j2ee.ejb")) {//$NON-NLS-1$
			if (j2eeModuleID.equals(IModuleConstants.JST_EJB_MODULE)) {
				return true;
			}
		} else if (serverTypeID.equals("j2ee.ear")) {//$NON-NLS-1$
			if (j2eeModuleID.equals(IModuleConstants.JST_EAR_MODULE) || j2eeModuleID.equals(IModuleConstants.JST_APPCLIENT_MODULE) || j2eeModuleID.equals(IModuleConstants.JST_CONNECTOR_MODULE)) {
				return true;
			}
		}
		return false;
	}
	
//	private static boolean matches(String a, String b) {
//		if (a == null || b == null || "*".equals(a) || "*".equals(b) || a.startsWith(b) || b.startsWith(a)) //$NON-NLS-1$ //$NON-NLS-2$
//			return true;
//		return false;
//	}


	protected String getComponentName() {
		return model.getStringProperty(COMPONENT_NAME);
	}

	protected String getComponentDeployName() {
		return model.getStringProperty(COMPONENT_DEPLOY_NAME);
	}

	protected abstract void initProjectCreationModel();

	protected abstract EClass getComponentType();

	protected abstract String getComponentExtension();

	protected abstract String getComponentID();

	protected abstract List getProperties();

	protected abstract Integer getDefaultComponentVersion();
}
