/**
 * <copyright>
 * </copyright>
 *
 * $Id: DeployedApplication.java,v 1.1 2005/01/14 21:02:42 cbridgha Exp $
 */
package org.eclipse.wst.common.projectmodule;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Deployed Application</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.projectmodule.DeployedApplication#getRoot <em>Root</em>}</li>
 *   <li>{@link org.eclipse.wst.common.projectmodule.DeployedApplication#getDeployScheme <em>Deploy Scheme</em>}</li>
 *   <li>{@link org.eclipse.wst.common.projectmodule.DeployedApplication#getApplication <em>Application</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.common.projectmodule.ProjectModulePackage#getDeployedApplication()
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
	 * @see #setRoot(String)
	 * @see org.eclipse.wst.common.projectmodule.ProjectModulePackage#getDeployedApplication_Root()
	 * @model 
	 * @generated
	 */
	String getRoot();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.projectmodule.DeployedApplication#getRoot <em>Root</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Root</em>' attribute.
	 * @see #getRoot()
	 * @generated
	 */
	void setRoot(String value);

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
	 * @see org.eclipse.wst.common.projectmodule.ProjectModulePackage#getDeployedApplication_DeployScheme()
	 * @model containment="true"
	 * @generated
	 */
	DeployScheme getDeployScheme();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.projectmodule.DeployedApplication#getDeployScheme <em>Deploy Scheme</em>}' containment reference.
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
	 * @see org.eclipse.wst.common.projectmodule.ProjectModulePackage#getDeployedApplication_Application()
	 * @model required="true"
	 * @generated
	 */
	WorkbenchApplication getApplication();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.projectmodule.DeployedApplication#getApplication <em>Application</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Application</em>' reference.
	 * @see #getApplication()
	 * @generated
	 */
	void setApplication(WorkbenchApplication value);

} // DeployedApplication
