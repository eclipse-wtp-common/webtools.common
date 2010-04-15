/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.operation;

import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;

/**
 * This class is meant to  be an alternative to RemoveReferenceComponentsDataModelProvider
 * in order to remove pre-made or pre-crafted IVirtualReferences, rather than 
 * searching for their individual properties
 */
public class RemoveReferenceDataModelProvider extends AddReferenceDataModelProvider {

	public RemoveReferenceDataModelProvider() {
		super();
	}

	public IDataModelOperation getDefaultOperation() {
		return new RemoveReferenceOperation(model);
	}

}
