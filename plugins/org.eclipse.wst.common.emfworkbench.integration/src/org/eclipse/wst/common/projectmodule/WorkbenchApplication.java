/**
 * <copyright>
 * </copyright>
 *
 * $Id: WorkbenchApplication.java,v 1.1 2005/01/14 21:02:42 cbridgha Exp $
 */
package org.eclipse.wst.common.projectmodule;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Workbench Application</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.projectmodule.WorkbenchApplication#getModules <em>Modules</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.common.projectmodule.ProjectModulePackage#getWorkbenchApplication()
 * @model 
 * @generated
 */
public interface WorkbenchApplication extends EObject {
	/**
	 * Returns the value of the '<em><b>Modules</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.wst.common.projectmodule.WorkbenchModule}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Modules</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Modules</em>' reference list.
	 * @see org.eclipse.wst.common.projectmodule.ProjectModulePackage#getWorkbenchApplication_Modules()
	 * @model type="org.eclipse.wst.common.projectmodule.WorkbenchModule"
	 * @generated
	 */
	EList getModules();

} // WorkbenchApplication
