/******************************************************************************
 * Copyright (c) 2009 Red Hat
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rob Stryker - initial implementation and ongoing maintenance
 ******************************************************************************/
package org.eclipse.wst.common.componentcore.datamodel.properties;


/**
 * This is an alternative to ICreateReferenceComponentsDataModelProperties
 * It is meant to be a simpler type of operation where you can pass in
 * an already-formed IVirtualReference rather than the individual properties
 * to be set. 
 */
public interface IAddReferenceDataModelProperties {
	
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
	public static final String SOURCE_COMPONENT = "IAddReferenceDataModelProperties.SOURCE_COMPONENT"; //$NON-NLS-1$

	/**
	 * <p>
	 * This required property is the {@link java.util.List} containing the {@link org.eclipse.wst.common.componentcore.resources.IVirtualReference}s that
	 * will be referenced from the {@link org.eclipse.wst.common.componentcore.resources.IVirtualComponent} specified by {@link #SOURCE_COMPONENT}.
	 * It may also be a single {@link org.eclipse.wst.common.componentcore.resources.IVirtualReference} if desired
	 * </p>
	 */
	public static final String TARGET_REFERENCE_LIST = "IAddReferenceDataModelProperties.TARGET_REFERENCE_LIST"; //$NON-NLS-1$
}
