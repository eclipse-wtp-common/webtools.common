/**
 * <copyright>
 * </copyright>
 *
 * $Id: ProjectModuleFactory.java,v 1.1 2005/01/14 21:02:42 cbridgha Exp $
 */
package org.eclipse.wst.common.projectmodule;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.wst.common.projectmodule.ProjectModulePackage
 * @generated
 */
public interface ProjectModuleFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ProjectModuleFactory eINSTANCE = new org.eclipse.wst.common.projectmodule.impl.ProjectModuleFactoryImpl();

	/**
	 * Returns a new object of class '<em>Deployed Application</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Deployed Application</em>'.
	 * @generated
	 */
	DeployedApplication createDeployedApplication();

	/**
	 * Returns a new object of class '<em>Deploy Scheme</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Deploy Scheme</em>'.
	 * @generated
	 */
	DeployScheme createDeployScheme();

	/**
	 * Returns a new object of class '<em>Workbench Module</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Workbench Module</em>'.
	 * @generated
	 */
	WorkbenchModule createWorkbenchModule();

	/**
	 * Returns a new object of class '<em>Module Resource</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Module Resource</em>'.
	 * @generated
	 */
	ModuleResource createModuleResource();

	/**
	 * Returns a new object of class '<em>Workbench Application</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Workbench Application</em>'.
	 * @generated
	 */
	WorkbenchApplication createWorkbenchApplication();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	ProjectModulePackage getProjectModulePackage();

} //ProjectModuleFactory
