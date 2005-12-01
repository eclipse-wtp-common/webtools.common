/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal;

import org.eclipse.core.runtime.IPath;
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
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.WorkbenchComponent#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.WorkbenchComponent#getResources <em>Resources</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.WorkbenchComponent#getComponentType <em>Component Type</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.WorkbenchComponent#getReferencedComponents <em>Referenced Components</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.WorkbenchComponent#getProperties <em>Properties</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.WorkbenchComponent#getMetadataResources <em>Metadata Resources</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.common.componentcore.internal.ComponentcorePackage#getWorkbenchComponent()
 * @model
 * @generated
 */
public interface WorkbenchComponent extends EObject{
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
	 * @see org.eclipse.wst.common.componentcore.internal.ComponentcorePackage#getWorkbenchComponent_Name()
	 * @model default="" required="true"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.componentcore.internal.WorkbenchComponent#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Resources</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.wst.common.componentcore.internal.ComponentResource}.
	 * It is bidirectional and its opposite is '{@link org.eclipse.wst.common.componentcore.internal.ComponentResource#getComponent <em>Component</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Resources</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Resources</em>' containment reference list.
	 * @see org.eclipse.wst.common.componentcore.internal.ComponentcorePackage#getWorkbenchComponent_Resources()
	 * @see org.eclipse.wst.common.componentcore.internal.ComponentResource#getComponent
	 * @model type="org.eclipse.wst.common.componentcore.internal.ComponentResource" opposite="component" containment="true"
	 * @generated
	 */
	EList getResources();

	/**
	 * Returns the value of the '<em><b>Component Type</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Component Type</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Component Type</em>' containment reference.
	 * @see #setComponentType(ComponentType)
	 * @see org.eclipse.wst.common.componentcore.internal.ComponentcorePackage#getWorkbenchComponent_ComponentType()
	 * @model containment="true" required="true"
	 * @generated
	 */
	ComponentType getComponentType();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.componentcore.internal.WorkbenchComponent#getComponentType <em>Component Type</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Component Type</em>' containment reference.
	 * @see #getComponentType()
	 * @generated
	 */
	void setComponentType(ComponentType value);

	/**
	 * Returns the value of the '<em><b>Referenced Components</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.wst.common.componentcore.internal.ReferencedComponent}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Referenced Components</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Referenced Components</em>' containment reference list.
	 * @see org.eclipse.wst.common.componentcore.internal.ComponentcorePackage#getWorkbenchComponent_ReferencedComponents()
	 * @model type="org.eclipse.wst.common.componentcore.internal.ReferencedComponent" containment="true"
	 * @generated
	 */
	EList getReferencedComponents();

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
	 * @see org.eclipse.wst.common.componentcore.internal.ComponentcorePackage#getWorkbenchComponent_Properties()
	 * @model type="org.eclipse.wst.common.componentcore.internal.Property"
	 * @generated
	 */
	EList getProperties();

	/**
	 * Returns the value of the '<em><b>Metadata Resources</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.core.runtime.IPath}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Metadata Resources</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Metadata Resources</em>' attribute list.
	 * @see org.eclipse.wst.common.componentcore.internal.ComponentcorePackage#getWorkbenchComponent_MetadataResources()
	 * @model type="org.eclipse.core.runtime.IPath" dataType="org.eclipse.wst.common.componentcore.internal.IPath"
	 * @generated
	 */
	EList getMetadataResources();

	URI getHandle();

	ComponentResource[] findResourcesByRuntimePath(IPath aDeployPath);
	
	ComponentResource[] findResourcesBySourcePath(IPath aSourcePath, int resourceFlag);
	
	boolean exists(IPath aSourcePath, int resourceFlag);

} // WorkbenchComponent
