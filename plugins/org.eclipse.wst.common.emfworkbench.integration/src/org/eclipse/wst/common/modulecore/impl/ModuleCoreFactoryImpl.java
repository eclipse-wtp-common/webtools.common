/**
 * <copyright>
 * </copyright>
 *
 * $Id: ModuleCoreFactoryImpl.java,v 1.1 2005/01/17 21:08:18 cbridgha Exp $
 */
package org.eclipse.wst.common.modulecore.impl;

import org.eclipse.emf.common.util.URI;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.wst.common.modulecore.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ModuleCoreFactoryImpl extends EFactoryImpl implements ModuleCoreFactory {
	/**
	 * Creates and instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ModuleCoreFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case ModuleCorePackage.DEPLOYED_APPLICATION: return createDeployedApplication();
			case ModuleCorePackage.DEPLOY_SCHEME: return createDeployScheme();
			case ModuleCorePackage.WORKBENCH_MODULE: return createWorkbenchModule();
			case ModuleCorePackage.MODULE_RESOURCE: return createModuleResource();
			case ModuleCorePackage.WORKBENCH_APPLICATION: return createWorkbenchApplication();
			case ModuleCorePackage.IMODULE_HANDLE: return createIModuleHandle();
			case ModuleCorePackage.IMODULE_TYPE: return createIModuleType();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
			case ModuleCorePackage.URI:
				return createURIFromString(eDataType, initialValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
			case ModuleCorePackage.URI:
				return convertURIToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
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
	public IModuleHandle createIModuleHandle() {
		IModuleHandleImpl iModuleHandle = new IModuleHandleImpl();
		return iModuleHandle;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IModuleType createIModuleType() {
		IModuleTypeImpl iModuleType = new IModuleTypeImpl();
		return iModuleType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public URI createURIFromString(EDataType eDataType, String initialValue) {
		return (URI)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertURIToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ModuleCorePackage getModuleCorePackage() {
		return (ModuleCorePackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	public static ModuleCorePackage getPackage() {
		return ModuleCorePackage.eINSTANCE;
	}

} //ModuleCoreFactoryImpl
