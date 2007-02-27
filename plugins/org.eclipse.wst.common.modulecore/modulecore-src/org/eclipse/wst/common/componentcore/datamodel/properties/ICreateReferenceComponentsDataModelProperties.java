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
package org.eclipse.wst.common.componentcore.datamodel.properties;

import org.eclipse.wst.common.frameworks.internal.DoNotUseMeThisWillBeDeletedPost15;

public interface ICreateReferenceComponentsDataModelProperties {

	/**
	 * <p>
	 * This required property is the {@link org.eclipse.wst.common.componentcore.resources.IVirtualComponent} which will reference the
	 * {@link org.eclipse.wst.common.componentcore.resources.IVirtualComponent}s specified by {@link #TARGET_COMPONENT_LIST}.
	 * </p>
	 * <p>
	 * For example, if {@link org.eclipse.wst.common.componentcore.resources.IVirtualComponent}s A, B, and C exist and references are required
	 * from A to B and A to C, then {@link #SOURCE_COMPONENT} should be set to A, and
	 * {@link #TARGET_COMPONENT_LIST} should be set to a {@link java.util.List} containing B and C.
	 * </p>
	 */
	public static final String SOURCE_COMPONENT = "ICreateReferenceComponentsDataModelProperties.SOURCE_COMPONENT"; //$NON-NLS-1$

	/**
	 * <p>
	 * This required property is the {@link java.util.List} containing the {@link org.eclipse.wst.common.componentcore.resources.IVirtualComponent}s that
	 * will be referenced from the {@link org.eclipse.wst.common.componentcore.resources.IVirtualComponent} specified by {@link #SOURCE_COMPONENT}.
	 * </p>
	 */
	public static final String TARGET_COMPONENT_LIST = "ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENT"; //$NON-NLS-1$

	//TODO this should be a map
	/**
	 * Optional, deploy path for the dependent component, default is "/"
	 */
	public static final String TARGET_COMPONENTS_DEPLOY_PATH = "ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENT_DEPLOY_PATH"; //$NON-NLS-1$

	/**
	 * Optional, archive name for the dependent component, default is ""
	 * 
	 * @deprecated
	 * @see DoNotUseMeThisWillBeDeletedPost15
	 */
	public static final String TARGET_COMPONENT_ARCHIVE_NAME = "ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENT_ARCHIVE_NAME"; //$NON-NLS-1$

	/**
	 * <p>
	 * This optional property is the {@link java.util.Map} containing keys of {@link org.eclipse.wst.common.componentcore.resources.IVirtualComponent}s and
	 * values of {@link String}s. This map is used to specify the String which should be used by
	 * the {@link org.eclipse.wst.common.componentcore.resources.IVirtualComponent} specified by the {@link #SOURCE_COMPONENT} property to lookup
	 * the keyed {@link org.eclipse.wst.common.componentcore.resources.IVirtualComponent}. The {@link java.util.Set} of {@link org.eclipse.wst.common.componentcore.resources.IVirtualComponent}s in the
	 * keys of this {@link java.util.Map} should have the same contents as the {@link java.util.List}.
	 * </p>
	 */
	public static final String TARGET_COMPONENTS_TO_URI_MAP = "ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENTS_TO_URI_MAP"; //$NON-NLS-1$

}
