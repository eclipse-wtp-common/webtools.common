/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.tests.ui.manager;

import java.util.Vector;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.environment.ILog;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

public abstract class BaseOperation extends AbstractDataModelOperation {
	public static Vector resultList;
	public static Vector undoList;
	private IStatus status;
	private boolean checkModels;
	private boolean modelsOK = false;

	public BaseOperation() {
		super();
	}

	public void setStatus(IStatus status) {
		this.status = status;
	}

	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		resultList.add(getID());

		getDataModel().setProperty("executedOps", resultList);
		getDataModel().setProperty("executedUndoOps", undoList);
		if (checkModels)
			modelsOK = checkModels();

		getEnvironment().getLog().log(ILog.OK, 1234, this, "BaseOperation", (Throwable) null); //$NON-NLS-1$

		return status;
	}

	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return Status.OK_STATUS;
	}

	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		resultList.remove(resultList.size() - 1);
		undoList.add(getID());
		return Status.OK_STATUS;
	}

	public void setCheckModels(boolean checkModels) {
		this.checkModels = checkModels;
	}

	public boolean getModelsOK() {
		return modelsOK;
	}

	private boolean checkModels() {
		IDataModel model = getDataModel();

		boolean containsModel1 = model.isNestedModel("testprovider1");
		boolean containsModel2 = model.isNestedModel("testprovider2");
		boolean prop1 = model.isPropertySet("provider1Prop1");
		boolean prop2 = model.isPropertySet("provider1Prop2");
		boolean prop3 = model.isPropertySet("provider1Prop3");
		boolean prop4 = model.isPropertySet("provider1Prop4");
		boolean prop5 = model.isPropertySet("provider2Prop1");
		boolean prop6 = model.isPropertySet("provider2Prop2");
		boolean prop7 = model.isPropertySet("provider2Prop3");
		boolean prop8 = model.isPropertySet("provider2Prop4");
		boolean value1 = model.getProperty("provider1Prop1").equals("11");
		boolean value2 = model.getProperty("provider1Prop2").equals("22");
		boolean value3 = model.getProperty("provider1Prop3").equals("33");
		boolean value4 = model.getProperty("provider1Prop4").equals("44");
		boolean value5 = model.getProperty("provider2Prop1").equals("1111");
		boolean value6 = model.getProperty("provider2Prop2").equals("2222");
		boolean value7 = model.getProperty("provider2Prop3").equals("3333");
		boolean value8 = model.getProperty("provider2Prop4").equals("4444");

		return containsModel1 && containsModel2 && prop1 && prop2 && prop3 && prop4 && prop5 && prop6 && prop7 && prop8 && value1 && value2 && value3 && value4 && value5 && value6 && value7 && value8;
	}
}
