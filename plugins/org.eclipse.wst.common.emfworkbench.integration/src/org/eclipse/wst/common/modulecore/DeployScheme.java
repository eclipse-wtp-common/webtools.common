/**
 * <copyright>
 * </copyright>
 *
 * $Id: DeployScheme.java,v 1.3 2005/01/24 21:05:22 cbridgha Exp $
 */
package org.eclipse.wst.common.modulecore;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Deploy Scheme</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.modulecore.DeployScheme#getType <em>Type</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.DeployScheme#getServerTarget <em>Server Target</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getDeployScheme()
 * @model 
 * @generated
 */
public interface DeployScheme extends EObject {
	/**
	 * Returns the value of the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' attribute.
	 * @see #setType(String)
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getDeployScheme_Type()
	 * @model 
	 * @generated
	 */
	String getType();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.modulecore.DeployScheme#getType <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' attribute.
	 * @see #getType()
	 * @generated
	 */
	void setType(String value);

	/**
	 * Returns the value of the '<em><b>Server Target</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Server Target</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Server Target</em>' attribute.
	 * @see #setServerTarget(String)
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getDeployScheme_ServerTarget()
	 * @model 
	 * @generated
	 */
	String getServerTarget();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.modulecore.DeployScheme#getServerTarget <em>Server Target</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Server Target</em>' attribute.
	 * @see #getServerTarget()
	 * @generated
	 */
	void setServerTarget(String value);

} // DeployScheme
