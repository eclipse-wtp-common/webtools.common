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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.common.componentcore.ArtifactEdit;
import org.eclipse.wst.common.componentcore.UnresolveableURIException;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.frameworks.internal.operations.ProjectCreationDataModel;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonMessages;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonPlugin;

import org.eclipse.jem.util.logger.proxy.Logger;
/**
 * This dataModel is a common super class used for create in a module context.
 * 
 * This class (and all its fields and methods) is likely to change during the WTP 1.0 milestones as
 * the new project structures are adopted. Use at your own risk.
 * 
 * @since WTP 1.0
 */
public abstract class ArtifactEditOperationDataModel extends WTPOperationDataModel {
	/**
	 * Required
	 */
	public static final String PROJECT_NAME = "ArtifactEditOperationDataModel.PROJECT_NAME"; //$NON-NLS-1$
	/**
	 * Required
	 */
	public static final String MODULE_NAME = "ArtifactEditOperationDataModel.MODULE_NAME"; //$NON-NLS-1$
	/**
	 * Optional, should save with prompt...defaults to false
	 */
	public static final String PROMPT_ON_SAVE = "ArtifactEditOperationDataModel.PROMPT_ON_SAVE"; //$NON-NLS-1$
	
	protected void initValidBaseProperties() {
		super.initValidBaseProperties();
		addValidBaseProperty(PROJECT_NAME);
		addValidBaseProperty(MODULE_NAME);
		addValidBaseProperty(PROMPT_ON_SAVE);
	}

	public IProject getTargetProject() {
		return ProjectCreationDataModel.getProjectHandleFromProjectName(getStringProperty(PROJECT_NAME));
	}

	protected Object getDefaultProperty(String propertyName) {
		if (propertyName.equals(PROMPT_ON_SAVE))
			return Boolean.FALSE;
		return super.getDefaultProperty(propertyName);
	}
	
	/* (non-Javadoc)
     * @see org.eclipse.wst.common.frameworks.operations.WTPOperationDataModel#getDefaultOperation()
     */
    public WTPOperation getDefaultOperation() {
        return new ArtifactEditOperation(this);
    }
	
	public ArtifactEdit getArtifactEditForRead(){
		WorkbenchComponent module = getWorkbenchModule(); 
		return ArtifactEdit.getArtifactEditForRead(module);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.frameworks.operations.WTPOperationDataModel#doSetProperty(java.lang.String, java.lang.Object)
	 */
	protected boolean doSetProperty(String propertyName, Object propertyValue) {
	    boolean status = super.doSetProperty(propertyName, propertyValue);
	    if(MODULE_NAME.equals(propertyName)){
	        WorkbenchComponent module = getWorkbenchModule();
	        IProject proj = getProjectForGivenComponent(module);
	        if(proj != null)
	            setProperty(PROJECT_NAME, proj.getName());
	    }
	    return status;
	}
    /**
     * @return
     */
    public WorkbenchComponent getWorkbenchModule() {
        StructureEdit moduleCore = null;
        WorkbenchComponent module = null;
		IProject project = getTargetProject();
		if (project.exists() && project.isAccessible()) {
	        try {
				
	            moduleCore = StructureEdit.getStructureEditForRead(project);
				if(moduleCore == null )
					return null;
	            module = moduleCore.findComponentByName(getStringProperty(MODULE_NAME));
	        } finally {
	            if (null != moduleCore) {
	                moduleCore.dispose();
	            }
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

	protected IStatus doValidateProperty(String propertyName) {
		IStatus result = super.doValidateProperty(propertyName);
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
