/**
 * <copyright>
 * </copyright>
 *
 * $Id: ModuleResource.java,v 1.1 2005/01/14 21:02:42 cbridgha Exp $
 */
package org.eclipse.wst.common.projectmodule;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Module Resource</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.projectmodule.ModuleResource#getPath <em>Path</em>}</li>
 *   <li>{@link org.eclipse.wst.common.projectmodule.ModuleResource#getRoot <em>Root</em>}</li>
 *   <li>{@link org.eclipse.wst.common.projectmodule.ModuleResource#getExclusions <em>Exclusions</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.common.projectmodule.ProjectModulePackage#getModuleResource()
 * @model 
 * @generated
 */
public interface ModuleResource extends EObject {
	/**
	 * Returns the value of the '<em><b>Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Path</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Path</em>' attribute.
	 * @see #setPath(String)
	 * @see org.eclipse.wst.common.projectmodule.ProjectModulePackage#getModuleResource_Path()
	 * @model 
	 * @generated
	 */
	String getPath();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.projectmodule.ModuleResource#getPath <em>Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Path</em>' attribute.
	 * @see #getPath()
	 * @generated
	 */
	void setPath(String value);

	/**
	 * Returns the value of the '<em><b>Root</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Root</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Root</em>' attribute.
	 * @see #setRoot(String)
	 * @see org.eclipse.wst.common.projectmodule.ProjectModulePackage#getModuleResource_Root()
	 * @model 
	 * @generated
	 */
	String getRoot();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.projectmodule.ModuleResource#getRoot <em>Root</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Root</em>' attribute.
	 * @see #getRoot()
	 * @generated
	 */
	void setRoot(String value);

	/**
	 * Returns the value of the '<em><b>Exclusions</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Exclusions</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Exclusions</em>' attribute list.
	 * @see org.eclipse.wst.common.projectmodule.ProjectModulePackage#getModuleResource_Exclusions()
	 * @model type="java.lang.String"
	 * @generated
	 */
	EList getExclusions();

} // ModuleResource
