/**
 * <copyright>
 * </copyright>
 *
 * $Id: ModuleCorePackage.java,v 1.1 2005/01/17 21:08:17 cbridgha Exp $
 */
package org.eclipse.wst.common.modulecore;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.wst.common.modulecore.ModuleCoreFactory
 * @generated
 */
public interface ModuleCorePackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "modulecore";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "modulecore.xmi";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "org.eclipse.wst.common.modulecore";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ModuleCorePackage eINSTANCE = org.eclipse.wst.common.modulecore.impl.ModuleCorePackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.wst.common.modulecore.impl.DeployedApplicationImpl <em>Deployed Application</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.modulecore.impl.DeployedApplicationImpl
	 * @see org.eclipse.wst.common.modulecore.impl.ModuleCorePackageImpl#getDeployedApplication()
	 * @generated
	 */
	int DEPLOYED_APPLICATION = 0;

	/**
	 * The feature id for the '<em><b>Root</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYED_APPLICATION__ROOT = 0;

	/**
	 * The feature id for the '<em><b>Deploy Scheme</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYED_APPLICATION__DEPLOY_SCHEME = 1;

	/**
	 * The feature id for the '<em><b>Application</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYED_APPLICATION__APPLICATION = 2;

	/**
	 * The number of structural features of the the '<em>Deployed Application</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOYED_APPLICATION_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.common.modulecore.impl.DeploySchemeImpl <em>Deploy Scheme</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.modulecore.impl.DeploySchemeImpl
	 * @see org.eclipse.wst.common.modulecore.impl.ModuleCorePackageImpl#getDeployScheme()
	 * @generated
	 */
	int DEPLOY_SCHEME = 1;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOY_SCHEME__TYPE = 0;

	/**
	 * The feature id for the '<em><b>Server Target</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOY_SCHEME__SERVER_TARGET = 1;

	/**
	 * The number of structural features of the the '<em>Deploy Scheme</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOY_SCHEME_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.common.modulecore.impl.WorkbenchModuleImpl <em>Workbench Module</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.modulecore.impl.WorkbenchModuleImpl
	 * @see org.eclipse.wst.common.modulecore.impl.ModuleCorePackageImpl#getWorkbenchModule()
	 * @generated
	 */
	int WORKBENCH_MODULE = 2;

	/**
	 * The feature id for the '<em><b>Handle</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_MODULE__HANDLE = 0;

	/**
	 * The feature id for the '<em><b>Dependent Modules</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_MODULE__DEPENDENT_MODULES = 1;

	/**
	 * The feature id for the '<em><b>Resources</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_MODULE__RESOURCES = 2;

	/**
	 * The feature id for the '<em><b>Module Type</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_MODULE__MODULE_TYPE = 3;

	/**
	 * The number of structural features of the the '<em>Workbench Module</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_MODULE_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.common.modulecore.impl.ModuleResourceImpl <em>Module Resource</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.modulecore.impl.ModuleResourceImpl
	 * @see org.eclipse.wst.common.modulecore.impl.ModuleCorePackageImpl#getModuleResource()
	 * @generated
	 */
	int MODULE_RESOURCE = 3;

	/**
	 * The feature id for the '<em><b>Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODULE_RESOURCE__PATH = 0;

	/**
	 * The feature id for the '<em><b>Root</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODULE_RESOURCE__ROOT = 1;

	/**
	 * The feature id for the '<em><b>Exclusions</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODULE_RESOURCE__EXCLUSIONS = 2;

	/**
	 * The number of structural features of the the '<em>Module Resource</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODULE_RESOURCE_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.common.modulecore.impl.WorkbenchApplicationImpl <em>Workbench Application</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.modulecore.impl.WorkbenchApplicationImpl
	 * @see org.eclipse.wst.common.modulecore.impl.ModuleCorePackageImpl#getWorkbenchApplication()
	 * @generated
	 */
	int WORKBENCH_APPLICATION = 4;

	/**
	 * The feature id for the '<em><b>Modules</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_APPLICATION__MODULES = 0;

	/**
	 * The number of structural features of the the '<em>Workbench Application</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_APPLICATION_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.common.modulecore.impl.IModuleHandleImpl <em>IModule Handle</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.modulecore.impl.IModuleHandleImpl
	 * @see org.eclipse.wst.common.modulecore.impl.ModuleCorePackageImpl#getIModuleHandle()
	 * @generated
	 */
	int IMODULE_HANDLE = 5;

	/**
	 * The feature id for the '<em><b>Handle</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMODULE_HANDLE__HANDLE = 0;

	/**
	 * The number of structural features of the the '<em>IModule Handle</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMODULE_HANDLE_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.common.modulecore.impl.IModuleTypeImpl <em>IModule Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.modulecore.impl.IModuleTypeImpl
	 * @see org.eclipse.wst.common.modulecore.impl.ModuleCorePackageImpl#getIModuleType()
	 * @generated
	 */
	int IMODULE_TYPE = 6;

	/**
	 * The feature id for the '<em><b>Root</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMODULE_TYPE__ROOT = 0;

	/**
	 * The feature id for the '<em><b>Metadata Resources</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMODULE_TYPE__METADATA_RESOURCES = 1;

	/**
	 * The feature id for the '<em><b>Type Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMODULE_TYPE__TYPE_NAME = 2;

	/**
	 * The number of structural features of the the '<em>IModule Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMODULE_TYPE_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '<em>URI</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.common.util.URI
	 * @see org.eclipse.wst.common.modulecore.impl.ModuleCorePackageImpl#getURI()
	 * @generated
	 */
	int URI = 7;


	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.common.modulecore.DeployedApplication <em>Deployed Application</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Deployed Application</em>'.
	 * @see org.eclipse.wst.common.modulecore.DeployedApplication
	 * @generated
	 */
	EClass getDeployedApplication();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.modulecore.DeployedApplication#getRoot <em>Root</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Root</em>'.
	 * @see org.eclipse.wst.common.modulecore.DeployedApplication#getRoot()
	 * @see #getDeployedApplication()
	 * @generated
	 */
	EAttribute getDeployedApplication_Root();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.wst.common.modulecore.DeployedApplication#getDeployScheme <em>Deploy Scheme</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Deploy Scheme</em>'.
	 * @see org.eclipse.wst.common.modulecore.DeployedApplication#getDeployScheme()
	 * @see #getDeployedApplication()
	 * @generated
	 */
	EReference getDeployedApplication_DeployScheme();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.wst.common.modulecore.DeployedApplication#getApplication <em>Application</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Application</em>'.
	 * @see org.eclipse.wst.common.modulecore.DeployedApplication#getApplication()
	 * @see #getDeployedApplication()
	 * @generated
	 */
	EReference getDeployedApplication_Application();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.common.modulecore.DeployScheme <em>Deploy Scheme</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Deploy Scheme</em>'.
	 * @see org.eclipse.wst.common.modulecore.DeployScheme
	 * @generated
	 */
	EClass getDeployScheme();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.modulecore.DeployScheme#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see org.eclipse.wst.common.modulecore.DeployScheme#getType()
	 * @see #getDeployScheme()
	 * @generated
	 */
	EAttribute getDeployScheme_Type();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.modulecore.DeployScheme#getServerTarget <em>Server Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Server Target</em>'.
	 * @see org.eclipse.wst.common.modulecore.DeployScheme#getServerTarget()
	 * @see #getDeployScheme()
	 * @generated
	 */
	EAttribute getDeployScheme_ServerTarget();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.common.modulecore.WorkbenchModule <em>Workbench Module</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Workbench Module</em>'.
	 * @see org.eclipse.wst.common.modulecore.WorkbenchModule
	 * @generated
	 */
	EClass getWorkbenchModule();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.wst.common.modulecore.WorkbenchModule#getHandle <em>Handle</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Handle</em>'.
	 * @see org.eclipse.wst.common.modulecore.WorkbenchModule#getHandle()
	 * @see #getWorkbenchModule()
	 * @generated
	 */
	EReference getWorkbenchModule_Handle();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.wst.common.modulecore.WorkbenchModule#getDependentModules <em>Dependent Modules</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Dependent Modules</em>'.
	 * @see org.eclipse.wst.common.modulecore.WorkbenchModule#getDependentModules()
	 * @see #getWorkbenchModule()
	 * @generated
	 */
	EReference getWorkbenchModule_DependentModules();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.wst.common.modulecore.WorkbenchModule#getResources <em>Resources</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Resources</em>'.
	 * @see org.eclipse.wst.common.modulecore.WorkbenchModule#getResources()
	 * @see #getWorkbenchModule()
	 * @generated
	 */
	EReference getWorkbenchModule_Resources();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.wst.common.modulecore.WorkbenchModule#getModuleType <em>Module Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Module Type</em>'.
	 * @see org.eclipse.wst.common.modulecore.WorkbenchModule#getModuleType()
	 * @see #getWorkbenchModule()
	 * @generated
	 */
	EReference getWorkbenchModule_ModuleType();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.common.modulecore.ModuleResource <em>Module Resource</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Module Resource</em>'.
	 * @see org.eclipse.wst.common.modulecore.ModuleResource
	 * @generated
	 */
	EClass getModuleResource();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.modulecore.ModuleResource#getPath <em>Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Path</em>'.
	 * @see org.eclipse.wst.common.modulecore.ModuleResource#getPath()
	 * @see #getModuleResource()
	 * @generated
	 */
	EAttribute getModuleResource_Path();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.modulecore.ModuleResource#getRoot <em>Root</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Root</em>'.
	 * @see org.eclipse.wst.common.modulecore.ModuleResource#getRoot()
	 * @see #getModuleResource()
	 * @generated
	 */
	EAttribute getModuleResource_Root();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.wst.common.modulecore.ModuleResource#getExclusions <em>Exclusions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Exclusions</em>'.
	 * @see org.eclipse.wst.common.modulecore.ModuleResource#getExclusions()
	 * @see #getModuleResource()
	 * @generated
	 */
	EAttribute getModuleResource_Exclusions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.common.modulecore.WorkbenchApplication <em>Workbench Application</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Workbench Application</em>'.
	 * @see org.eclipse.wst.common.modulecore.WorkbenchApplication
	 * @generated
	 */
	EClass getWorkbenchApplication();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.wst.common.modulecore.WorkbenchApplication#getModules <em>Modules</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Modules</em>'.
	 * @see org.eclipse.wst.common.modulecore.WorkbenchApplication#getModules()
	 * @see #getWorkbenchApplication()
	 * @generated
	 */
	EReference getWorkbenchApplication_Modules();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.common.modulecore.IModuleHandle <em>IModule Handle</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>IModule Handle</em>'.
	 * @see org.eclipse.wst.common.modulecore.IModuleHandle
	 * @generated
	 */
	EClass getIModuleHandle();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.modulecore.IModuleHandle#getHandle <em>Handle</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Handle</em>'.
	 * @see org.eclipse.wst.common.modulecore.IModuleHandle#getHandle()
	 * @see #getIModuleHandle()
	 * @generated
	 */
	EAttribute getIModuleHandle_Handle();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.common.modulecore.IModuleType <em>IModule Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>IModule Type</em>'.
	 * @see org.eclipse.wst.common.modulecore.IModuleType
	 * @generated
	 */
	EClass getIModuleType();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.modulecore.IModuleType#getRoot <em>Root</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Root</em>'.
	 * @see org.eclipse.wst.common.modulecore.IModuleType#getRoot()
	 * @see #getIModuleType()
	 * @generated
	 */
	EAttribute getIModuleType_Root();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.wst.common.modulecore.IModuleType#getMetadataResources <em>Metadata Resources</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Metadata Resources</em>'.
	 * @see org.eclipse.wst.common.modulecore.IModuleType#getMetadataResources()
	 * @see #getIModuleType()
	 * @generated
	 */
	EAttribute getIModuleType_MetadataResources();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.modulecore.IModuleType#getTypeName <em>Type Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type Name</em>'.
	 * @see org.eclipse.wst.common.modulecore.IModuleType#getTypeName()
	 * @see #getIModuleType()
	 * @generated
	 */
	EAttribute getIModuleType_TypeName();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.emf.common.util.URI <em>URI</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>URI</em>'.
	 * @see org.eclipse.emf.common.util.URI
	 * @model instanceClass="org.eclipse.emf.common.util.URI"
	 * @generated
	 */
	EDataType getURI();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	ModuleCoreFactory getModuleCoreFactory();

} //ModuleCorePackage
