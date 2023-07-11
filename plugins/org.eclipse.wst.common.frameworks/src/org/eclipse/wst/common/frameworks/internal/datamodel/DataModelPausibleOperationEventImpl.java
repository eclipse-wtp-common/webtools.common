/*******************************************************************************
 * Copyright (c) 2003, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.datamodel;

import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;

public class DataModelPausibleOperationEventImpl implements IDataModelPausibleOperationEvent {

	private IDataModelOperation operation;
	private int operationType;
	private int executionType;

	public DataModelPausibleOperationEventImpl(IDataModelOperation operation, int operationType, int executionType) {
		this.operation = operation;
		this.operationType = operationType;
		this.executionType = executionType;
	}

	@Override
	public IDataModelOperation getOperation() {
		return operation;
	}

	@Override
	public int getOperationType() {
		return operationType;
	}

	@Override
	public int getExecutionType() {
		return executionType;
	}
}
