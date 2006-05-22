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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.frameworks.internal.DoNotUseMeThisWillBeDeletedPost15;

public interface ICreateReferenceComponentsDataModelProperties {

	/**
	 * <p>
	 * This required property is the {@link IVirtualComponent} which will reference the
	 * {@link IVirtualComponent}s specified by {@link #TARGET_COMPONENT_LIST}.
	 * </p>
	 * <p>
	 * For example, if {@link IVirtualComponent}s A, B, and C exist and references are required
	 * from A to B and A to C, then {@link #SOURCE_COMPONENT} should be set to A, and
	 * {@link #TARGET_COMPONENT_LIST} should be set to a {@link List} containing B and C.
	 * </p>
	 */
	public static final String SOURCE_COMPONENT = "ICreateReferenceComponentsDataModelProperties.SOURCE_COMPONENT"; //$NON-NLS-1$

	/**
	 * <p>
	 * This required property is the {@link List} containing the {@link IVirtualComponent}s that
	 * will be referenced from the {@link IVirtualComponent} specified by {@link #SOURCE_COMPONENT}.
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
	 * This optional property is the {@link Map} containing keys of {@link IVirtualComponent}s and
	 * values of {@link String}s. This map is used to specify the String which should be used by
	 * the {@link IVirtualComponent} specified by the {@link #SOURCE_COMPONENT} property to lookup
	 * the keyed {@link IVirtualComponent}. The {@link Set} of {@link IVirtualComponent}s in the
	 * keys of this {@link Map} should have the same contents as the {@link List}.
	 * </p>
	 */
	public static final String TARGET_COMPONENTS_TO_URI_MAP = "ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENTS_TO_URI_MAP"; //$NON-NLS-1$

}
