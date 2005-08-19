/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.frameworks.internal.operations;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;

public class OperationExtension {

	String opID = null;

	IConfigurationElement baseElement = null;

	String preOperationClass = null;

	String postOperationClass = null;

	private String extensionId;

	public OperationExtension(IConfigurationElement element, String id, String preOp, String postOp) {
		super();
		baseElement = element;
		setUpExtension(id, preOp, postOp);
	}

	private void setUpExtension(String id, String preOp, String postOp) {
		opID = id;
		preOperationClass = preOp;
		postOperationClass = postOp;
		this.extensionId = baseElement.getDeclaringExtension().getUniqueIdentifier();
		if (this.extensionId == null)
			this.extensionId = baseElement.getDeclaringExtension().getDeclaringPluginDescriptor().getUniqueIdentifier();
	}

	public String getOpID() {
		return opID;
	}

	public String getPostOperationClass() {
		return postOperationClass;
	}

	public String getPreOperationClass() {
		return preOperationClass;
	}

	public IDataModelOperation getPostOperation() throws CoreException {
		if (postOperationClass == null)
			return null;
		IDataModelOperation op = (IDataModelOperation) baseElement.createExecutableExtension(OperationExtensionReader.ATT_POST_OP);
		return op;
	}

	public IDataModelOperation getPreOperation() throws CoreException {
		if (preOperationClass == null)
			return null;
		IDataModelOperation op = (IDataModelOperation) baseElement.createExecutableExtension(OperationExtensionReader.ATT_PRE_OP);
		return op;
	}

	/**
	 * @return Returns the extensionId.
	 */
	public String getExtensionId() {
		return extensionId;
	}

	public IConfigurationElement getBaseElement() {
		return baseElement;
	}
}