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
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> 
 * An EMF representation of a development-time module contained by an Eclipse project. 
 * <p> 
 * See the package overview for an <a href="package-summary.html">overview of the model components</a>.
 * </p>
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.modulecore.WorkbenchComponent#getHandle <em>Handle</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.WorkbenchComponent#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.WorkbenchComponent#getResources <em>Resources</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.WorkbenchComponent#getComponentType <em>Component Type</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.WorkbenchComponent#getReferencedComponents <em>Referenced Components</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getWorkbenchComponent()
 * @model
 * @generated
 */
public interface WorkbenchComponent extends EObject{
	/**
	 * Returns the value of the '<em><b>Handle</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Handle</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Handle</em>' attribute.
	 * @see #setHandle(URI)
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getWorkbenchComponent_Handle()
	 * @model dataType="org.eclipse.wst.common.modulecore.URI" required="true"
	 * @generated
	 */
	URI getHandle();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.modulecore.WorkbenchComponent#getHandle <em>Handle</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Handle</em>' attribute.
	 * @see #getHandle()
	 * @generated
	 */
	void setHandle(URI value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getWorkbenchComponent_Name()
	 * @model default="" required="true"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.modulecore.WorkbenchComponent#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Resources</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.wst.common.modulecore.ComponentResource}.
	 * It is bidirectional and its opposite is '{@link org.eclipse.wst.common.modulecore.ComponentResource#getComponent <em>Component</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Resources</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Resources</em>' containment reference list.
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getWorkbenchComponent_Resources()
	 * @see org.eclipse.wst.common.modulecore.ComponentResource#getComponent
	 * @model type="org.eclipse.wst.common.modulecore.ComponentResource" opposite="component" containment="true"
	 * @generated
	 */
	EList getResources();

	/**
	 * Returns the value of the '<em><b>Component Type</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Component Type</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Component Type</em>' reference.
	 * @see #setComponentType(ComponentType)
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getWorkbenchComponent_ComponentType()
	 * @model required="true"
	 * @generated
	 */
	ComponentType getComponentType();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.modulecore.WorkbenchComponent#getComponentType <em>Component Type</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Component Type</em>' reference.
	 * @see #getComponentType()
	 * @generated
	 */
	void setComponentType(ComponentType value);

	/**
	 * Returns the value of the '<em><b>Referenced Components</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.wst.common.modulecore.ReferencedComponent}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Referenced Components</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Referenced Components</em>' reference list.
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getWorkbenchComponent_ReferencedComponents()
	 * @model type="org.eclipse.wst.common.modulecore.ReferencedComponent"
	 * @generated
	 */
	EList getReferencedComponents();

	ComponentResource[] findWorkbenchModuleResourceByDeployPath(URI aDeployPath);
	
	ComponentResource[] findWorkbenchModuleResourceBySourcePath(URI aSourcePath);

} // WorkbenchComponent
