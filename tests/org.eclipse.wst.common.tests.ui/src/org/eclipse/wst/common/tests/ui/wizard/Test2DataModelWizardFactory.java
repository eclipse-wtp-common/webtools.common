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
package org.eclipse.wst.common.tests.ui.wizard;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizardPage;
import org.eclipse.wst.common.frameworks.internal.operation.extensionui.DMWizardExtensionFactory;

public class Test2DataModelWizardFactory extends DMWizardExtensionFactory {

	protected String getPageName() {
		return "A Page"; //$NON-NLS-1$
	}

	protected IDataModelProvider getProvider(){
		return new Test2DataModelProvider(){
			public IDataModelOperation getDefaultOperation() {
				return new AbstractDataModelOperation(){
					public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
						System.out.println(getPageName());	
						return OK_STATUS;
					}
				};
			}
		};
	}
	
	public DataModelWizardPage[] createPageGroup(IDataModel dataModel, String pageGroupID) {
		return new DataModelWizardPage[]{new DataModelWizardPage(DataModelFactory.createDataModel(getProvider()), getPageName()) {
			protected String[] getValidationPropertyNames() {
				// TODO Auto-generated method stub
				return null;
			}

			protected Composite createTopLevelComposite(Composite parent) {
				Composite composite = new Composite(parent, SWT.NULL);
				composite.setLayout(new GridLayout());
				GridData data = new GridData(GridData.FILL_BOTH);
				Label label = new Label(composite, SWT.NULL);
				label.setLayoutData(data);
				label.setText(getPageName());
				return composite;
			}
		}};
	}

}
