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
 * Provides a root object to store and manage the
 * {@see org.eclipse.wst.common.modulecore.WorkbenchModule}s. * 
 * <p>
 * See the package overview for an <a href="package-summary.html">overview of the model components </a>.
 * </p>
 * <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>
 * {@link org.eclipse.wst.common.modulecore.ProjectModules#getProjectName <em>Project Name</em>}
 * </li>
 * <li>
 * {@link org.eclipse.wst.common.modulecore.ProjectModules#getWorkbenchModules <em>Workbench Modules</em>}
 * </li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getProjectModules()
 * @model
 * @generated
 */
public interface ProjectModules extends EObject {
	/**
	 * Returns the value of the '<em><b>Project Name</b></em>' attribute. <!-- begin-user-doc
	 * -->
	 * <p>
	 * If the meaning of the '<em>Project Name</em>' attribute isn't clear, there really should
	 * be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Project Name</em>' attribute.
	 * @see #setProjectName(String)
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getProjectModules_ProjectName()
	 * @model
	 * @generated
	 */
	String getProjectName();

	/**
	 * Sets the value of the '
	 * {@link org.eclipse.wst.common.modulecore.ProjectModules#getProjectName <em>Project Name</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Project Name</em>' attribute.
	 * @see #getProjectName()
	 * @generated
	 */
	void setProjectName(String value);

	/**
	 * Returns the value of the '<em><b>Workbench Modules</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.wst.common.modulecore.WorkbenchModule}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Workbench Modules</em>' containment reference list isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Workbench Modules</em>' containment reference list.
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getProjectModules_WorkbenchModules()
	 * @model type="org.eclipse.wst.common.modulecore.WorkbenchModule" containment="true"
	 * @generated
	 */
	EList getWorkbenchModules();

	public WorkbenchModule findWorkbenchModule(String aDeployName);

} // ProjectModules
