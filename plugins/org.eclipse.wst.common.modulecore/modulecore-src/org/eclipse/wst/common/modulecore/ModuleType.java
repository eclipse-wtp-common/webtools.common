/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.modulecore;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> 
 * Indicates how the {@see org.eclipse.wst.common.modulecore.WorkbenchModule}&nbsp;should be handled by
 * the tooling.  
 * <p>
 * In particular, the {@see #getModuleTypeId()}&nbsp; is used to coordinate other
 * extensions such as EditModels and the module structure preparation builder.
 * </p>
 * 
 * <p>
 * See the package overview for an <a href="package-summary.html">overview of the model components </a>.
 * </p>
 * <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>
 * {@link org.eclipse.wst.common.modulecore.ModuleType#getMetadataResources <em>Metadata Resources</em>}
 * </li>
 * <li>{@link org.eclipse.wst.common.modulecore.ModuleType#getModuleTypeId <em>Module Type Id</em>}
 * </li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getModuleType()
 * @model
 * @generated
 */
public interface ModuleType extends EObject {
	/**
	 * Returns the value of the '<em><b>Metadata Resources</b></em>' attribute list. The list
	 * contents are of type {@link org.eclipse.emf.common.util.URI}. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Metadata Resources</em>' attribute list isn't clear, there
	 * really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Metadata Resources</em>' attribute list.
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getModuleType_MetadataResources()
	 * @model type="org.eclipse.emf.common.util.URI"
	 *        dataType="org.eclipse.wst.common.modulecore.URI"
	 * @generated
	 */
	EList getMetadataResources();

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * Used to hint to the tooling how the {@see WorkbenchModule}should be edited and processed by
	 * the tooling.
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Module Type Id</em>' attribute.
	 * @see #setModuleTypeId(String)
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getModuleType_ModuleTypeId()
	 * @model
	 * @generated
	 */
	String getModuleTypeId();

	/**
	 * Sets the value of the '
	 * {@link org.eclipse.wst.common.modulecore.ModuleType#getModuleTypeId <em>Module Type Id</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Module Type Id</em>' attribute.
	 * @see #getModuleTypeId()
	 * @generated
	 */
	void setModuleTypeId(String value);

} // ModuleType
