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
package org.eclipse.wst.common.internal.emfworkbench.integration;

import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext;


/**
 * @author Administrator
 */
public class LooseComposedEditModel extends ComposedEditModel {

	public LooseComposedEditModel(String editModelID, EMFWorkbenchContext context) {
		super(editModelID, context);

	}

	public EditModel.Reference addChild(EditModel editModel) {
		getChildren().add(editModel);
		Reference ref = editModel.getReference();
		getChildrenMap().put(ref, editModel);
		return ref;
	}

	public void removeChild(EditModel editModel) {
		getChildren().remove(editModel);
		getChildrenMap().remove(editModel.getReference());
	}
}