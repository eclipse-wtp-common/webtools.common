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
/*
 * Created on May 5, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.eclipse.wst.common.frameworks.internal.operation.extensionui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;

import org.eclipse.jem.util.logger.proxy.Logger;

/**
 * @author jsholl
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Generation - Code and Comments
 */
public class SlaveDescriptor {

	public static final String SLAVE_OPERATION = "slaveOperation"; //$NON-NLS-1$

	public static final String ATT_OPERATION_CLASS = "operationClass"; //$NON-NLS-1$
	public static final String ATT_ID = "id"; //$NON-NLS-1$ 
	private static final String ATT_OVERRIDE_ID = "overrideId"; //$NON-NLS-1$
	public static final String ATT_NAME = "name"; //$NON-NLS-1$
	public static final String ATT_DESCRIPTION = "description"; //$NON-NLS-1$

	private String id;
	private String name;
	private String description;
	private String operationClass;
	private String overrideId;
	private final IConfigurationElement element;

	public SlaveDescriptor(IConfigurationElement element) {
		this.element = element;
		init();
	}

	private void init() {
		this.id = this.element.getAttribute(ATT_ID);
		this.overrideId = this.element.getAttribute(ATT_OVERRIDE_ID);
		this.name = this.element.getAttribute(ATT_NAME);
		this.description = this.element.getAttribute(ATT_DESCRIPTION);
		this.operationClass = this.element.getAttribute(ATT_OPERATION_CLASS);
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public WTPOperation createOperation() {
		try {
			return (WTPOperation) this.element.createExecutableExtension(ATT_OPERATION_CLASS);
		} catch (CoreException e) {
			Logger.getLogger().logError(e);
		}
		return null;
	}

	/**
	 * @return Returns the element.
	 */
	protected IConfigurationElement getElement() {
		return element;
	}

	/**
	 * @return Returns the operationClass.
	 */
	public String getOperationClass() {
		return operationClass;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getClass() + "[name=" + getName() + ", operationClass=" + getOperationClass() + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * @return Returns the id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return Returns the overrideId.
	 */
	public String getOverrideId() {
		return overrideId;
	}
}