/**
 * <copyright>
 * </copyright>
 *
 * $Id: WorkbenchModule.java,v 1.1 2005/01/14 21:02:42 cbridgha Exp $
 */
package org.eclipse.wst.common.projectmodule;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Workbench Module</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.projectmodule.WorkbenchModule#getModuleType <em>Module Type</em>}</li>
 *   <li>{@link org.eclipse.wst.common.projectmodule.WorkbenchModule#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.wst.common.projectmodule.WorkbenchModule#getDependentModules <em>Dependent Modules</em>}</li>
 *   <li>{@link org.eclipse.wst.common.projectmodule.WorkbenchModule#getResources <em>Resources</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.common.projectmodule.ProjectModulePackage#getWorkbenchModule()
 * @model 
 * @generated
 */
public interface WorkbenchModule extends EObject{
	/**
	 * Returns the value of the '<em><b>Module Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Module Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Module Type</em>' attribute.
	 * @see #setModuleType(String)
	 * @see org.eclipse.wst.common.projectmodule.ProjectModulePackage#getWorkbenchModule_ModuleType()
	 * @model 
	 * @generated
	 */
	String getModuleType();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.projectmodule.WorkbenchModule#getModuleType <em>Module Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Module Type</em>' attribute.
	 * @see #getModuleType()
	 * @generated
	 */
	void setModuleType(String value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.wst.common.projectmodule.ProjectModulePackage#getWorkbenchModule_Name()
	 * @model 
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.projectmodule.WorkbenchModule#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Dependent Modules</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.wst.common.projectmodule.WorkbenchModule}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Dependent Modules</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Dependent Modules</em>' reference list.
	 * @see org.eclipse.wst.common.projectmodule.ProjectModulePackage#getWorkbenchModule_DependentModules()
	 * @model type="org.eclipse.wst.common.projectmodule.WorkbenchModule"
	 * @generated
	 */
	EList getDependentModules();

	/**
	 * Returns the value of the '<em><b>Resources</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.wst.common.projectmodule.ModuleResource}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Resources</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Resources</em>' reference list.
	 * @see org.eclipse.wst.common.projectmodule.ProjectModulePackage#getWorkbenchModule_Resources()
	 * @model type="org.eclipse.wst.common.projectmodule.ModuleResource"
	 * @generated
	 */
	EList getResources();

} // WorkbenchModule
