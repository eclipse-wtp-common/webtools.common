/**
 * <copyright>
 * </copyright>
 *
 * $Id: DeployedApplication.java,v 1.1 2005/01/17 21:08:17 cbridgha Exp $
 */
package org.eclipse.wst.common.modulecore;

import org.eclipse.emf.common.util.URI;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Deployed Application</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.modulecore.DeployedApplication#getRoot <em>Root</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.DeployedApplication#getDeployScheme <em>Deploy Scheme</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.DeployedApplication#getApplication <em>Application</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getDeployedApplication()
 * @model 
 * @generated
 */
public interface DeployedApplication extends EObject {
	/**
	 * Returns the value of the '<em><b>Root</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Root</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Root</em>' attribute.
	 * @see #setRoot(URI)
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getDeployedApplication_Root()
	 * @model dataType="org.eclipse.wst.common.modulecore.URI"
	 * @generated
	 */
	URI getRoot();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.modulecore.DeployedApplication#getRoot <em>Root</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Root</em>' attribute.
	 * @see #getRoot()
	 * @generated
	 */
	void setRoot(URI value);

	/**
	 * Returns the value of the '<em><b>Deploy Scheme</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Deploy Scheme</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Deploy Scheme</em>' containment reference.
	 * @see #setDeployScheme(DeployScheme)
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getDeployedApplication_DeployScheme()
	 * @model containment="true"
	 * @generated
	 */
	DeployScheme getDeployScheme();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.modulecore.DeployedApplication#getDeployScheme <em>Deploy Scheme</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Deploy Scheme</em>' containment reference.
	 * @see #getDeployScheme()
	 * @generated
	 */
	void setDeployScheme(DeployScheme value);

	/**
	 * Returns the value of the '<em><b>Application</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Application</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Application</em>' reference.
	 * @see #setApplication(WorkbenchApplication)
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getDeployedApplication_Application()
	 * @model required="true"
	 * @generated
	 */
	WorkbenchApplication getApplication();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.modulecore.DeployedApplication#getApplication <em>Application</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Application</em>' reference.
	 * @see #getApplication()
	 * @generated
	 */
	void setApplication(WorkbenchApplication value);

} // DeployedApplication
