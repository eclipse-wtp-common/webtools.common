/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.modulecore;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> 
 * Used to reference {@see org.eclipse.wst.common.modulecore.WorkbenchModule}s either contained in
 * the same project or remotely. 
 * <p>
 * Each {@see org.eclipse.wst.common.modulecore.WorkbenchModule}&nbsp; contains a list of its
 * DependentModules.
 * </p> 
 * <p>
 * The referenced {@see WorkbenchModule}&nbsp; may be in the same project as the
 * {@see WorkbenchModule}&nbsp; that contains the current DependentModule. Use
 * {@see ModuleCore#isLocalDependency(DependentModule)}&nbsp; to make the determination.
 * </p> 
 * <p>
 * See the package overview for an <a href="package-summary.html">overview of the model components </a>.
 * </p>
 * <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.wst.common.modulecore.DependentModule#getHandle <em>Handle</em>}</li>
 * <li>
 * {@link org.eclipse.wst.common.modulecore.DependentModule#getDeployedPath <em>Deployed Path</em>}
 * </li>
 * <li>
 * {@link org.eclipse.wst.common.modulecore.DependentModule#getDependencyType <em>Dependency Type</em>}
 * </li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getDependentModule()
 * @model
 * @generated
 */
public interface DependentModule extends EObject {
	/**
	 * Returns the value of the '
	 * {@link org.eclipse.wst.common.modulecore.DependentModule#getHandle <em>Handle</em>}'
	 * attribute. <!-- begin-user-doc -->
	 * <p>
	 * Returns a fully-qualified URI that conforms to the standard "module:" URI format. The handle
	 * references the {@see WorkbenchModule}represented by the current DependentModule.
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Handle</em>' attribute.
	 * @see #setHandle(URI)
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getDependentModule_Handle()
	 * @model dataType="org.eclipse.wst.common.modulecore.URI"
	 * @generated
	 */
	URI getHandle();

	/**
	 * Sets the value of the '
	 * {@link org.eclipse.wst.common.modulecore.DependentModule#getHandle <em>Handle</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param A
	 *            fully-qualified URI of the module: format.
	 * @see #getHandle()
	 * @generated
	 */
	void setHandle(URI value);

	/**
	 * Returns the value of the '<em><b>Deployed Path</b></em>' attribute. <!-- begin-user-doc
	 * -->
	 * <p>
	 * The deployedPath specifies the location of the DependentModule's contents relative to the
	 * containing {@see WorkbenchModule}.
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Deployed Path</em>' attribute.
	 * @see #setDeployedPath(URI)
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getDependentModule_DeployedPath()
	 * @model dataType="org.eclipse.wst.common.modulecore.URI"
	 * @generated
	 */
	URI getDeployedPath();

	/**
	 * Sets the value of the '
	 * {@link org.eclipse.wst.common.modulecore.DependentModule#getDeployedPath <em>Deployed Path</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param A
	 *            path relative to the containing {@see WorkbenchModule}'s output location
	 * @see #getDeployedPath()
	 * @generated
	 */
	void setDeployedPath(URI value);

	/**
	 * Returns the value of the '<em><b>Dependency Type</b></em>' attribute. The literals are
	 * from the enumeration {@link org.eclipse.wst.common.modulecore.DependencyType}. <!--
	 * begin-user-doc -->
	 * <p>
	 * See the class documentation {@see DependencyType}for information on the purpose of this
	 * field.
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Dependency Type</em>' attribute.
	 * @see org.eclipse.wst.common.modulecore.DependencyType
	 * @see #setDependencyType(DependencyType)
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getDependentModule_DependencyType()
	 * @model
	 * @generated
	 */
	DependencyType getDependencyType();

	/**
	 * Sets the value of the '
	 * {@link org.eclipse.wst.common.modulecore.DependentModule#getDependencyType <em>Dependency Type</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Dependency Type</em>' attribute.
	 * @see org.eclipse.wst.common.modulecore.DependencyType
	 * @see #getDependencyType()
	 * @generated
	 */
	void setDependencyType(DependencyType value);

} // DependentModule
