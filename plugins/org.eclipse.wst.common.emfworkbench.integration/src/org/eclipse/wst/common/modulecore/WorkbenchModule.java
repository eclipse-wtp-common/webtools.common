/**
 * <copyright>
 * </copyright>
 *
 * $Id: WorkbenchModule.java,v 1.6 2005/01/26 21:34:08 cbridgha Exp $
 */
package org.eclipse.wst.common.modulecore;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
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
 *   <li>{@link org.eclipse.wst.common.modulecore.WorkbenchModule#getDeployedPath <em>Deployed Path</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.WorkbenchModule#getModules <em>Modules</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.WorkbenchModule#getResources <em>Resources</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.WorkbenchModule#getModuleType <em>Module Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getWorkbenchModule()
 * @model 
 * @generated
 */
public interface WorkbenchModule extends EObject{
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
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getWorkbenchModule_Handle()
	 * @model dataType="org.eclipse.wst.common.modulecore.URI"
	 * @generated
	 */
	URI getHandle();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.modulecore.WorkbenchModule#getHandle <em>Handle</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Handle</em>' attribute.
	 * @see #getHandle()
	 * @generated
	 */
	void setHandle(URI value);

	/**
	 * Returns the value of the '<em><b>Deployed Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Deployed Path</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Deployed Path</em>' attribute.
	 * @see #setDeployedPath(URI)
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getWorkbenchModule_DeployedPath()
	 * @model dataType="org.eclipse.wst.common.modulecore.URI"
	 * @generated
	 */
	URI getDeployedPath();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.modulecore.WorkbenchModule#getDeployedPath <em>Deployed Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Deployed Path</em>' attribute.
	 * @see #getDeployedPath()
	 * @generated
	 */
	void setDeployedPath(URI value);

	/**
	 * Returns the value of the '<em><b>Modules</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.wst.common.modulecore.WorkbenchModule}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Modules</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Modules</em>' reference list.
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getWorkbenchModule_Modules()
	 * @model type="org.eclipse.wst.common.modulecore.WorkbenchModule"
	 * @generated
	 */
	EList getModules();

	/**
	 * Returns the value of the '<em><b>Resources</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.wst.common.modulecore.WorkbenchModuleResource}.
	 * It is bidirectional and its opposite is '{@link org.eclipse.wst.common.modulecore.WorkbenchModuleResource#getModule <em>Module</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Resources</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Resources</em>' containment reference list.
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getWorkbenchModule_Resources()
	 * @see org.eclipse.wst.common.modulecore.WorkbenchModuleResource#getModule
	 * @model type="org.eclipse.wst.common.modulecore.WorkbenchModuleResource" opposite="module" containment="true"
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
