/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.componentcore.internal;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> 
 * Indicates how the {@see org.eclipse.wst.common.modulecore.WorkbenchComponent}&nbsp;should be handled by
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
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.ComponentType#getComponentTypeId <em>Component Type Id</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.ComponentType#getVersion <em>Version</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.ComponentType#getProperties <em>Properties</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.ComponentType#getMetadataResources <em>Metadata Resources</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.common.componentcore.internal.ComponentcorePackage#getComponentType()
 * @model
 * @generated
 */
public interface ComponentType extends EObject{
	/**
	 * Returns the value of the '<em><b>Component Type Id</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Component Type Id</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Component Type Id</em>' attribute.
	 * @see #setComponentTypeId(String)
	 * @see org.eclipse.wst.common.componentcore.internal.ComponentcorePackage#getComponentType_ComponentTypeId()
	 * @model default="" required="true"
	 * @generated
	 */
	String getComponentTypeId();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.componentcore.internal.ComponentType#getComponentTypeId <em>Component Type Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Component Type Id</em>' attribute.
	 * @see #getComponentTypeId()
	 * @generated
	 */
	void setComponentTypeId(String value);

	/**
	 * Returns the value of the '<em><b>Metadata Resources</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.core.runtime.IPath}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Metadata Resources</em>' attribute list isn't clear, there
	 * really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Metadata Resources</em>' attribute list.
	 * @see org.eclipse.wst.common.componentcore.internal.ComponentcorePackage#getComponentType_MetadataResources()
	 * @model type="org.eclipse.core.runtime.IPath" dataType="org.eclipse.wst.common.componentcore.internal.IPath"
	 * @generated
	 */
	EList getMetadataResources();

	/**
	 * Returns the value of the '<em><b>Version</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Version</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Version</em>' attribute.
	 * @see #setVersion(String)
	 * @see org.eclipse.wst.common.componentcore.internal.ComponentcorePackage#getComponentType_Version()
	 * @model default="" required="true"
	 * @generated
	 */
	String getVersion();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.componentcore.internal.ComponentType#getVersion <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Version</em>' attribute.
	 * @see #getVersion()
	 * @generated
	 */
	void setVersion(String value);

	/**
	 * Returns the value of the '<em><b>Properties</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.wst.common.componentcore.internal.Property}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Properties</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Properties</em>' reference list.
	 * @see org.eclipse.wst.common.componentcore.internal.ComponentcorePackage#getComponentType_Properties()
	 * @model type="org.eclipse.wst.common.componentcore.internal.Property"
	 * @generated
	 */
	EList getProperties();

} // ComponentType
