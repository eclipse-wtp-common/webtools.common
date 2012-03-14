/*******************************************************************************
 * Copyright (c) 2003, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.impl;

import java.util.Map;

import org.eclipse.wst.common.componentcore.internal.ModuleStructuralModel;
import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModel;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModelFactory;

public class ModuleStructuralModelFactory extends EditModelFactory {
	
	public static final String MODULE_STRUCTURAL_MODEL_ID = "org.eclipse.wst.modulecore.structuralModel"; //$NON-NLS-1$

	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.internal.emfworkbench.integration.EditModelFactory#createEditModelForRead(java.lang.String, org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext, java.util.Map)
	 */
	public EditModel createEditModelForRead(String anEditModelId, EMFWorkbenchContext aContext, Map theParams) {
		return new ModuleStructuralModel(anEditModelId, aContext, true);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.internal.emfworkbench.integration.EditModelFactory#createEditModelForWrite(java.lang.String, org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext, java.util.Map)
	 */
	public EditModel createEditModelForWrite(String anEditModelId, EMFWorkbenchContext aContext, Map theParams) {
		return new ModuleStructuralModel(anEditModelId, aContext, false);
	}
	
}
