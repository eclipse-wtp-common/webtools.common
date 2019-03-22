package org.eclipse.wst.common.componentcore.internal.operation;

/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.datamodel.properties.IServerContextRootDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

public class ServerContextRootUpdateOperation 
 extends  AbstractDataModelOperation 
 implements IServerContextRootDataModelProperties{
	

	public ServerContextRootUpdateOperation(IDataModel model) {
		super(model);
	}

	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		IProject project = (IProject)model.getProperty( IServerContextRootDataModelProperties.PROJECT );
		String contextRoot = model.getStringProperty( IServerContextRootDataModelProperties.CONTEXT_ROOT );
		if (contextRoot != null) {
			IVirtualComponent comp = ComponentCore.createComponent(project);
			comp.setMetaProperty(IModuleConstants.CONTEXTROOT, contextRoot);	
		}
		return OK_STATUS;
	}

}
