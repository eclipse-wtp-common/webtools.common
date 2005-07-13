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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.jem.util.emf.workbench.WorkbenchResourceHelperBase;
import org.eclipse.wst.common.componentcore.ArtifactEdit;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.internal.util.ArtifactEditRegistryReader;
import org.eclipse.wst.common.componentcore.internal.util.IArtifactEditFactory;
import org.eclipse.wst.common.componentcore.resources.IFlexibleProject;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;
import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext;

public class ArtifactEditOperation extends WTPOperation {
	private ArtifactEdit artifactEdit;
	protected EMFWorkbenchContext emfWorkbenchContext;
	private CommandStack commandStack;

    /**
     * @param operationDataModel
     */
    public ArtifactEditOperation(ArtifactEditOperationDataModel operationDataModel) {
        super(operationDataModel);
    }
    
    /**
	 * This no argument constructor should not be used by clients. This is for extended operations.
	 * 
	 * ExtendedOperations
	 */
    public ArtifactEditOperation() {
    	super();
    }

    //TODO: move functionality from edit model operation to artifact edit operation
    protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
        // TODO Auto-generated method stub

    }
	protected final void initialize(IProgressMonitor monitor) {
		ArtifactEditOperationDataModel dataModel = (ArtifactEditOperationDataModel) operationDataModel;
		emfWorkbenchContext = (EMFWorkbenchContext) WorkbenchResourceHelperBase.createEMFContext(dataModel.getTargetProject(), null);
		artifactEdit = getArtifactEditForComponent(getComponent());
		doInitialize(monitor);
	}

	/**
     * @return
     */
    protected ArtifactEdit getArtifactEditForComponent(IVirtualComponent comp) {
    		ArtifactEditRegistryReader reader = ArtifactEditRegistryReader.instance();
    		IArtifactEditFactory factory = reader.getArtifactEdit(comp.getComponentTypeId());
    		return factory.createArtifactEditForWrite(comp);
    	}

    /**
     * @return
     */
    public WorkbenchComponent getWorkbenchModule() {
        ArtifactEditOperationDataModel dataModel = (ArtifactEditOperationDataModel) operationDataModel;
        StructureEdit moduleCore = null;
        WorkbenchComponent module = null;
        try {
            moduleCore = StructureEdit.getStructureEditForRead(dataModel.getTargetProject());
            module = moduleCore.findComponentByName(dataModel.getStringProperty(ArtifactEditOperationDataModel.MODULE_NAME));
        } finally {
            if (null != moduleCore) {
                moduleCore.dispose();
            }
        }
        return module;
    }

    public IVirtualComponent getComponent() {
    	IFlexibleProject proj = ComponentCore.createFlexibleProject(((ArtifactEditOperationDataModel)operationDataModel).getTargetProject());
    	return proj.getComponent(operationDataModel.getStringProperty(ArtifactEditOperationDataModel.MODULE_NAME));
    	
    }
    protected ArtifactEdit getArtifactEdit() {
        return artifactEdit;
    }
    
    protected void doInitialize(IProgressMonitor monitor) {
		//init
	}
	protected final void dispose(IProgressMonitor monitor) {
		try {
			doDispose(monitor);
		} finally {
			saveEditModel(monitor);
		}
	}

	private final void saveEditModel(IProgressMonitor monitor) {
		if (null != artifactEdit) {
			if (((ArtifactEditOperationDataModel) operationDataModel).getBooleanProperty(ArtifactEditOperationDataModel.PROMPT_ON_SAVE))
			    //TODO: reimplement for Artifact edit
			    //artifactEdit.saveIfNecessaryWithPrompt(monitor, (IOperationHandler) operationDataModel.getProperty(WTPOperationDataModel.UI_OPERATION_HANLDER), this);
			    artifactEdit.saveIfNecessary(monitor);
			else
			    artifactEdit.saveIfNecessary(monitor);
			artifactEdit.dispose();
			artifactEdit = null;
		}
		postSaveEditModel(monitor);
	}

	/**
	 * @param monitor
	 */
	protected void postSaveEditModel(IProgressMonitor monitor) {
		// do nothing by default
	}

	protected void doDispose(IProgressMonitor monitor) {
		//dispose
	}

	/**
	 * @return Returns the commandStack.
	 */
	public CommandStack getCommandStack() {
		if (commandStack == null && artifactEdit != null)
			commandStack = artifactEdit.getCommandStack();
		return commandStack;
	}

	/**
	 * @param commandStack
	 *            The commandStack to set.
	 */
	public void setCommandStack(CommandStack commandStack) {
		this.commandStack = commandStack;
	}

	/**
	 * @see org.eclipse.wst.common.frameworks.internal.operation.WTPOperation#validateEdit()
	 */
	protected boolean validateEdit() {
	    //TODO: reimplement
//		IValidateEditContext validator = (IValidateEditContext) UIContextDetermination.createInstance(IValidateEditContext.CLASS_KEY);
//		return validator.validateState(artifactEdit).isOK();
	    return true;
	}
}
