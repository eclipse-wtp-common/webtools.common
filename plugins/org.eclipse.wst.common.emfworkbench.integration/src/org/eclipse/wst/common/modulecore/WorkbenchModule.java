/**
 * <copyright>
 * </copyright>
 *
 * $Id: WorkbenchModule.java,v 1.1 2005/01/17 21:08:17 cbridgha Exp $
 */
package org.eclipse.wst.common.modulecore;

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
 *   <li>{@link org.eclipse.wst.common.modulecore.WorkbenchModule#getHandle <em>Handle</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.WorkbenchModule#getDependentModules <em>Dependent Modules</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.WorkbenchModule#getResources <em>Resources</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.WorkbenchModule#getModuleType <em>Module Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getWorkbenchModule()
 * @model 
 * @generated
 */
public interface WorkbenchModule extends EObject {
	/**
	 * Returns the value of the '<em><b>Handle</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Handle</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Handle</em>' containment reference.
	 * @see #setHandle(IModuleHandle)
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getWorkbenchModule_Handle()
	 * @model containment="true"
	 * @generated
	 */
	IModuleHandle getHandle();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.modulecore.WorkbenchModule#getHandle <em>Handle</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Handle</em>' containment reference.
	 * @see #getHandle()
	 * @generated
	 */
	void setHandle(IModuleHandle value);

	/**
	 * Returns the value of the '<em><b>Dependent Modules</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.wst.common.modulecore.WorkbenchModule}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Dependent Modules</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Dependent Modules</em>' reference list.
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getWorkbenchModule_DependentModules()
	 * @model type="org.eclipse.wst.common.modulecore.WorkbenchModule"
	 * @generated
	 */
	EList getDependentModules();

	/**
	 * Returns the value of the '<em><b>Resources</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.wst.common.modulecore.ModuleResource}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Resources</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Resources</em>' reference list.
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getWorkbenchModule_Resources()
	 * @model type="org.eclipse.wst.common.modulecore.ModuleResource"
	 * @generated
	 */
	EList getResources();

	/**
	 * Returns the value of the '<em><b>Module Type</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.wst.common.modulecore.IModuleType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Module Type</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Module Type</em>' reference list.
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getWorkbenchModule_ModuleType()
	 * @model type="org.eclipse.wst.common.modulecore.IModuleType"
	 * @generated
	 */
	EList getModuleType();

} // WorkbenchModule
