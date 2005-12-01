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
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> 
 * Used to reference {@see org.eclipse.wst.common.modulecore.WorkbenchComponent}s either contained in
 * the same project or remotely. 
 * <p>
 * Each {@see org.eclipse.wst.common.modulecore.WorkbenchComponent}&nbsp; contains a list of its
 * DependentModules.
 * </p> 
 * <p>
 * The referenced {@see WorkbenchComponent}&nbsp; may be in the same project as the
 * {@see WorkbenchComponent}&nbsp; that contains the current ReferencedComponent. Use
 * {@see ModuleCore#isLocalDependency(ReferencedComponent)}&nbsp; to make the determination.
 * </p> 
 * <p>
 * See the package overview for an <a href="package-summary.html">overview of the model components </a>.
 * </p>
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.ReferencedComponent#getHandle <em>Handle</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.ReferencedComponent#getRuntimePath <em>Runtime Path</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.ReferencedComponent#getDependencyType <em>Dependency Type</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.ReferencedComponent#getDependentObject <em>Dependent Object</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.common.componentcore.internal.ComponentcorePackage#getReferencedComponent()
 * @model
 * @generated
 */
public interface ReferencedComponent extends EObject{
	/**
	 * Returns the value of the '<em><b>Handle</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * Returns a fully-qualified URI that conforms to the standard "module:" URI format. The handle
	 * references the {@see WorkbenchComponent}represented by the current ReferencedComponent.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Handle</em>' attribute.
	 * @see #setHandle(URI)
	 * @see org.eclipse.wst.common.componentcore.internal.ComponentcorePackage#getReferencedComponent_Handle()
	 * @model dataType="org.eclipse.wst.common.componentcore.internal.URI" required="true"
	 * @generated
	 */
	URI getHandle();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.componentcore.internal.ReferencedComponent#getHandle <em>Handle</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @param value the new value of the '<em>Handle</em>' attribute.
	 * @see #getHandle()
	 * @generated
	 */
	void setHandle(URI value);

	/**
	 * Returns the value of the '<em><b>Runtime Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Runtime Path</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Runtime Path</em>' attribute.
	 * @see #setRuntimePath(IPath)
	 * @see org.eclipse.wst.common.componentcore.internal.ComponentcorePackage#getReferencedComponent_RuntimePath()
	 * @model dataType="org.eclipse.wst.common.componentcore.internal.IPath" required="true"
	 * @generated
	 */
	IPath getRuntimePath();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.componentcore.internal.ReferencedComponent#getRuntimePath <em>Runtime Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Runtime Path</em>' attribute.
	 * @see #getRuntimePath()
	 * @generated
	 */
	void setRuntimePath(IPath value);

	/**
	 * Returns the value of the '<em><b>Dependency Type</b></em>' attribute. The literals are
	 * from the enumeration {@link org.eclipse.wst.common.componentcore.internal.DependencyType}. <!--
	 * begin-user-doc -->
	 * <p>
	 * See the class documentation {@see DependencyType}for information on the purpose of this
	 * field.
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Dependency Type</em>' attribute.
	 * @see org.eclipse.wst.common.componentcore.internal.DependencyType
	 * @see #setDependencyType(DependencyType)
	 * @see org.eclipse.wst.common.componentcore.internal.ModuleCorePackage#getDependentModule_DependencyType()
	 * @model
	 * @generated
	 */
	DependencyType getDependencyType();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.componentcore.internal.ReferencedComponent#getDependencyType <em>Dependency Type</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @param value the new value of the '<em>Dependency Type</em>' attribute.
	 * @see org.eclipse.wst.common.componentcore.internal.DependencyType
	 * @see #getDependencyType()
	 * @generated
	 */
	void setDependencyType(DependencyType value);

	/**
	 * Returns the value of the '<em><b>Dependent Object</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Dependent Object</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Dependent Object</em>' reference.
	 * @see #setDependentObject(EObject)
	 * @see org.eclipse.wst.common.componentcore.internal.ComponentcorePackage#getReferencedComponent_DependentObject()
	 * @model
	 * @generated
	 */
	EObject getDependentObject();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.componentcore.internal.ReferencedComponent#getDependentObject <em>Dependent Object</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Dependent Object</em>' reference.
	 * @see #getDependentObject()
	 * @generated
	 */
	void setDependentObject(EObject value);

} // ReferencedComponent
