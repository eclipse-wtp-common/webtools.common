/**
 * <copyright>
 * </copyright>
 *
 * $Id: ModuleCoreFactory.java,v 1.1 2005/01/17 21:08:17 cbridgha Exp $
 */
package org.eclipse.wst.common.modulecore;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage
 * @generated
 */
public interface ModuleCoreFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ModuleCoreFactory eINSTANCE = new org.eclipse.wst.common.modulecore.impl.ModuleCoreFactoryImpl();

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
	 * Returns a new object of class '<em>IModule Handle</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>IModule Handle</em>'.
	 * @generated
	 */
	IModuleHandle createIModuleHandle();

	/**
	 * Returns a new object of class '<em>IModule Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>IModule Type</em>'.
	 * @generated
	 */
	IModuleType createIModuleType();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	ModuleCorePackage getModuleCorePackage();

} //ModuleCoreFactory
