/**
 * <copyright>
 * </copyright>
 *
 * $Id: WorkbenchApplication.java,v 1.4 2005/01/26 16:48:35 cbridgha Exp $
 */
package org.eclipse.wst.common.modulecore;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Workbench Application</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.modulecore.WorkbenchApplication#getDeployScheme <em>Deploy Scheme</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getWorkbenchApplication()
 * @model 
 * @generated
 */
public interface WorkbenchApplication extends WorkbenchModule {
	/**
	 * Returns the value of the '<em><b>Deploy Scheme</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Deploy Scheme</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Deploy Scheme</em>' reference.
	 * @see #setDeployScheme(DeployScheme)
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getWorkbenchApplication_DeployScheme()
	 * @model required="true"
	 * @generated
	 */
	DeployScheme getDeployScheme();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.modulecore.WorkbenchApplication#getDeployScheme <em>Deploy Scheme</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Deploy Scheme</em>' reference.
	 * @see #getDeployScheme()
	 * @generated
	 */
	void setDeployScheme(DeployScheme value);

} // WorkbenchApplication
