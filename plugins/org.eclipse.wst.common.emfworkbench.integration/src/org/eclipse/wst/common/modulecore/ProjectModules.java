/**
 * <copyright>
 * </copyright>
 *
 * $Id: ProjectModules.java,v 1.5 2005/02/02 19:51:06 cbridgha Exp $
 */
package org.eclipse.wst.common.modulecore;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Project Modules</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.modulecore.ProjectModules#getProjectName <em>Project Name</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.ProjectModules#getWorkbenchApplications <em>Workbench Applications</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.ProjectModules#getWorkbenchModules <em>Workbench Modules</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.ProjectModules#getDeploymentSchemes <em>Deployment Schemes</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getProjectModules()
 * @model 
 * @generated
 */
public interface ProjectModules extends EObject{
	/**
	 * Returns the value of the '<em><b>Project Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Project Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Project Name</em>' attribute.
	 * @see #setProjectName(String)
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getProjectModules_ProjectName()
	 * @model 
	 * @generated
	 */
	String getProjectName();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.modulecore.ProjectModules#getProjectName <em>Project Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Project Name</em>' attribute.
	 * @see #getProjectName()
	 * @generated
	 */
	void setProjectName(String value);

	/**
	 * Returns the value of the '<em><b>Workbench Applications</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.wst.common.modulecore.WorkbenchApplication}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Workbench Applications</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Workbench Applications</em>' containment reference list.
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getProjectModules_WorkbenchApplications()
	 * @model type="org.eclipse.wst.common.modulecore.WorkbenchApplication" containment="true"
	 * @generated
	 */
	EList getWorkbenchApplications();

	/**
	 * Returns the value of the '<em><b>Workbench Modules</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.wst.common.modulecore.WorkbenchModule}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Workbench Modules</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Workbench Modules</em>' containment reference list.
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getProjectModules_WorkbenchModules()
	 * @model type="org.eclipse.wst.common.modulecore.WorkbenchModule" containment="true"
	 * @generated
	 */
	EList getWorkbenchModules();

	/**
	 * Returns the value of the '<em><b>Deployment Schemes</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.wst.common.modulecore.DeployScheme}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Deployment Schemes</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Deployment Schemes</em>' containment reference list.
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getProjectModules_DeploymentSchemes()
	 * @model type="org.eclipse.wst.common.modulecore.DeployScheme" containment="true"
	 * @generated
	 */
	EList getDeploymentSchemes();

	public WorkbenchModule findWorkbenchModule(URI aModuleURI);

} // ProjectModules
