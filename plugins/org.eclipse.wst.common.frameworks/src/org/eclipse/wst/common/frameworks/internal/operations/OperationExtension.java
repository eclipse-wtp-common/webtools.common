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
import org.eclipse.wst.common.frameworks.datamodel.provisional.IDataModelOperation;

public class OperationExtension {

	String opID = null;

	IConfigurationElement baseElement = null;

	String preOperationClass = null;

	WTPOperation preOperation = null;

	String postOperationClass = null;

	WTPOperation postOperation = null;

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

	public WTPOperation getPostOperation() throws CoreException {
		if (postOperationClass == null)
			return null;
		WTPOperation op = (WTPOperation) baseElement.createExecutableExtension(OperationExtensionReader.ATT_POST_OP);
		if (op != null)
			op.setID(getExtensionId());
		return op;
	}

	public WTPOperation getPreOperation() throws CoreException {
		if (preOperationClass == null)
			return null;
		WTPOperation op = (WTPOperation) baseElement.createExecutableExtension(OperationExtensionReader.ATT_PRE_OP);
		if (op != null)
			op.setID(getExtensionId());
		return op;
	}

	public IDataModelOperation getDMPostOperation() throws CoreException {
		if (postOperationClass == null)
			return null;
		IDataModelOperation op = (IDataModelOperation) baseElement.createExecutableExtension(OperationExtensionReader.ATT_POST_OP);
		if (op != null)
			op.setID(getExtensionId());
		return op;
	}

	public IDataModelOperation getDMPreOperation() throws CoreException {
		if (preOperationClass == null)
			return null;
		IDataModelOperation op = (IDataModelOperation) baseElement.createExecutableExtension(OperationExtensionReader.ATT_PRE_OP);
		if (op != null)
			op.setID(getExtensionId());
		return op;
	}

	/**
	 * @return Returns the extensionId.
	 */
	public String getExtensionId() {
		return extensionId;
	}
}