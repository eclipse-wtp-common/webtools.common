/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Dec 3, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.eclipse.wst.common.componentcore.internal.operation;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.wst.common.internal.emfworkbench.integration.ModelModifier;

/**
 * @author DABERG
 * 
 * To change the template for this generated type comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public abstract class ModelModifierOperation extends ArtifactEditOperation {
	protected ModelModifier modifier;

	/**
	 * @param dataModel
	 */
	public ModelModifierOperation(ModelModifierOperationDataModel dataModel) {
		super(dataModel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.internal.emfworkbench.operation.EditModelOperation#doInitialize(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void doInitialize(IProgressMonitor monitor) {
		super.doInitialize(monitor);
		EditingDomain domain = null;
		if (operationDataModel.isProperty(ModelModifierOperationDataModel.EDITING_DOMAIN)) { //added
			// so
			// regular
			// EditModelOperations
			// can
			// be
			// used
			domain = (EditingDomain) operationDataModel.getProperty(ModelModifierOperationDataModel.EDITING_DOMAIN);
		}
		if (domain == null) {
			domain = createDefaultEditingDomain();
		}
		modifier = new ModelModifier(domain);
	}

	/**
	 * @return
	 */
	private EditingDomain createDefaultEditingDomain() {
		return new AdapterFactoryEditingDomain(new AdapterFactory() {
			public boolean isFactoryForType(Object type) {
				return false;
			}

			public Object adapt(Object object, Object type) {
				return null;
			}

			public Adapter adapt(Notifier target, Object type) {
				return null;
			}

			public Adapter adaptNew(Notifier target, Object type) {
				return null;
			}

			public void adaptAllNew(Notifier notifier) {
				//do nothing
			}
		}, getArtifactEdit().getCommandStack());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.operation.WTPOperation#execute(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected final void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
		addHelpers();
		modifier.execute();
		postExecuteCommands(monitor);
	}

	/**
	 * This is a hook to allow subclasses to perform additional tasks after the commands are
	 * executed using the helpers.
	 */
	protected void postExecuteCommands(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		//do nothing
	}

	/**
	 * Add all necessary helpers to the modifier.
	 * 
	 * @see org.eclipse.wst.common.internal.emfworkbench.integration.ModifierHelper
	 */
	protected abstract void addHelpers() throws CoreException;
}