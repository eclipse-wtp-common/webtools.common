/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.datamodel.properties;

import org.eclipse.wst.common.componentcore.internal.operation.ServerContextRootDataModelProvider;

public interface IServerContextRootDataModelProperties {
	/**
	 * This field should not be used.  It is not part of the API and may be modified in the future.
	 */
	public static Class _provider_class = ServerContextRootDataModelProvider.class;

	public static final String PROJECT = "IServerContextRootDataModelProperties.PROJECT"; //$NON-NLS-1$	
	public static final String CONTEXT_ROOT = "IServerContextRootDataModelProperties.CONTEXT_ROOT"; //$NON-NLS-1$

}
