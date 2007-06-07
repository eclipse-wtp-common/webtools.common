/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.operation;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jem.util.UIContextDetermination;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.jem.util.emf.workbench.WorkbenchResourceHelperBase;
import org.eclipse.wst.common.componentcore.ArtifactEdit;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.ArtifactEditModel;
import org.eclipse.wst.common.componentcore.internal.util.ArtifactEditRegistryReader;
import org.eclipse.wst.common.componentcore.internal.util.IArtifactEditFactory;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModel;
import org.eclipse.wst.common.internal.emfworkbench.validateedit.IValidateEditContext;

public abstract class ArtifactEditProviderOperation extends AbstractDataModelOperation {
	
	protected ArtifactEdit artifactEdit;
	protected EMFWorkbenchContext emfWorkbenchContext;
//	private CommandStack commandStack;

	public ArtifactEditProviderOperation() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ArtifactEditProviderOperation(IDataModel model) {
		super(model);
	}
	
	protected final void initialize(IProgressMonitor monitor) {
		emfWorkbenchContext = (EMFWorkbenchContext) WorkbenchResourceHelperBase.createEMFContext(getTargetProject(), null);
		IVirtualComponent component = getTargetComponent(); 
		artifactEdit = getArtifactEditForModule(component);
		doInitialize(monitor);
	}
	
	public IProject getTargetProject() {
		String projectName = model.getStringProperty(IArtifactEditOperationDataModelProperties.PROJECT_NAME);
		return ProjectUtilities.getProject(projectName);
	}
	
	public IVirtualComponent getTargetComponent() {
		return ComponentCore.createComponent(getTargetProject());
	}
	
	private void doInitialize(IProgressMonitor monitor) {
		//Default
	}

	protected ArtifactEdit getArtifactEditForModule(IVirtualComponent comp) {
		ArtifactEditRegistryReader reader = ArtifactEditRegistryReader.instance();
		IArtifactEditFactory factory = reader.getArtifactEdit(comp.getProject());
		if (factory == null)
			return null;
		return factory.createArtifactEditForWrite(comp);
	}
	
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return null;
	}

	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return null;
	}

	public void dispose() {
		if (artifactEdit!=null) {
			artifactEdit.saveIfNecessary(new NullProgressMonitor());
			artifactEdit.dispose();
		}
			
		super.dispose();
		
	}

	public final IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		try {
			initialize(monitor);
			if (validateEdit().isOK())
				return doExecute(monitor, info);
			return Status.CANCEL_STATUS;
		} finally {
			dispose();
		}
	}
	
	public abstract IStatus doExecute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException;
	
	public ArtifactEdit getArtifactEdit(){
		return artifactEdit;
	}

	/**
	 * Validate edit for resource state
	 */
	protected IStatus validateEdit() {
		IValidateEditContext validator = (IValidateEditContext) UIContextDetermination.createInstance(IValidateEditContext.CLASS_KEY);
		return validator.validateState((EditModel)getArtifactEdit().getAdapter(ArtifactEditModel.ADAPTER_TYPE));
		
	}
}
