/**
 * <copyright>
 * </copyright>
 *
 * $Id: ProjectModuleFactoryImpl.java,v 1.1 2005/01/14 21:02:41 cbridgha Exp $
 */
package org.eclipse.wst.common.projectmodule.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.wst.common.projectmodule.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ProjectModuleFactoryImpl extends EFactoryImpl implements ProjectModuleFactory {
	/**
	 * Creates and instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ProjectModuleFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case ProjectModulePackage.DEPLOYED_APPLICATION: return createDeployedApplication();
			case ProjectModulePackage.DEPLOY_SCHEME: return createDeployScheme();
			case ProjectModulePackage.WORKBENCH_MODULE: return createWorkbenchModule();
			case ProjectModulePackage.MODULE_RESOURCE: return createModuleResource();
			case ProjectModulePackage.WORKBENCH_APPLICATION: return createWorkbenchApplication();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeployedApplication createDeployedApplication() {
		DeployedApplicationImpl deployedApplication = new DeployedApplicationImpl();
		return deployedApplication;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeployScheme createDeployScheme() {
		DeploySchemeImpl deployScheme = new DeploySchemeImpl();
		return deployScheme;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public WorkbenchModule createWorkbenchModule() {
		WorkbenchModuleImpl workbenchModule = new WorkbenchModuleImpl();
		return workbenchModule;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ModuleResource createModuleResource() {
		ModuleResourceImpl moduleResource = new ModuleResourceImpl();
		return moduleResource;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public WorkbenchApplication createWorkbenchApplication() {
		WorkbenchApplicationImpl workbenchApplication = new WorkbenchApplicationImpl();
		return workbenchApplication;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ProjectModulePackage getProjectModulePackage() {
		return (ProjectModulePackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	public static ProjectModulePackage getPackage() {
		return ProjectModulePackage.eINSTANCE;
	}

} //ProjectModuleFactoryImpl
