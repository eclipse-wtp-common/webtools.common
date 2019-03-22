/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.operations;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

public class OperationExtension {

	protected String opID = null;

	protected IConfigurationElement baseElement = null;

	protected String preOperationClass = null;

	protected String postOperationClass = null;

	protected String extensionId;

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
			this.extensionId = baseElement.getDeclaringExtension().getNamespace();
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

	public Object getPostOperation() throws CoreException {
		if (postOperationClass == null)
			return null;
		return baseElement.createExecutableExtension(OperationExtensionReader.ATT_POST_OP);
	}

	public Object getPreOperation() throws CoreException {
		if (preOperationClass == null)
			return null;
		return baseElement.createExecutableExtension(OperationExtensionReader.ATT_PRE_OP);
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
