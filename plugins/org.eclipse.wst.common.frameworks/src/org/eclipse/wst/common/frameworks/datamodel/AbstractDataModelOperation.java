/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.datamodel;

import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IStatus;

public abstract class AbstractDataModelOperation extends AbstractOperation implements IDataModelOperation {

	protected static final IStatus OK_STATUS = AbstractDataModelProvider.OK_STATUS;

	private String id;
	protected IDataModel model;

	public AbstractDataModelOperation() {
		super("");
	}

	public AbstractDataModelOperation(IDataModel model) {
		super(""); // TODO add a label property to IDataModel???
		this.model = model;
	}


	public void setID(String id) {
		this.id = id;
	}

	public String getID() {
		return id;
	}

	public void setDataModel(IDataModel model) {
		this.model = model;
	}

	public IDataModel getDataModel() {
		return model;
	}
}
