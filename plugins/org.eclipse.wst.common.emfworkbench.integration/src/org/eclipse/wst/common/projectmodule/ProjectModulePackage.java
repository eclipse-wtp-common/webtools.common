/**
 * <copyright>
 * </copyright>
 *
 * $Id: ProjectModulePackage.java,v 1.1 2005/01/14 21:02:42 cbridgha Exp $
 */
package org.eclipse.wst.common.projectmodule;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
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
 * @see org.eclipse.wst.common.projectmodule.ProjectModuleFactory
 * @generated
 */
public interface ProjectModulePackage extends EPackage{
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "projectmodule";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "projectmodule.xmi";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "org.eclipse.wst.common.projectmodule";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ProjectModulePackage eINSTANCE = org.eclipse.wst.common.projectmodule.impl.ProjectModulePackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.wst.common.projectmodule.impl.DeployedApplicationImpl <em>Deployed Application</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.projectmodule.impl.DeployedApplicationImpl
	 * @see org.eclipse.wst.common.projectmodule.impl.ProjectModulePackageImpl#getDeployedApplication()
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
	 * The meta object id for the '{@link org.eclipse.wst.common.projectmodule.impl.DeploySchemeImpl <em>Deploy Scheme</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.projectmodule.impl.DeploySchemeImpl
	 * @see org.eclipse.wst.common.projectmodule.impl.ProjectModulePackageImpl#getDeployScheme()
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
	 * The meta object id for the '{@link org.eclipse.wst.common.projectmodule.impl.WorkbenchModuleImpl <em>Workbench Module</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.projectmodule.impl.WorkbenchModuleImpl
	 * @see org.eclipse.wst.common.projectmodule.impl.ProjectModulePackageImpl#getWorkbenchModule()
	 * @generated
	 */
	int WORKBENCH_MODULE = 2;

	/**
	 * The feature id for the '<em><b>Module Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_MODULE__MODULE_TYPE = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_MODULE__NAME = 1;

	/**
	 * The feature id for the '<em><b>Dependent Modules</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_MODULE__DEPENDENT_MODULES = 2;

	/**
	 * The feature id for the '<em><b>Resources</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_MODULE__RESOURCES = 3;

	/**
	 * The number of structural features of the the '<em>Workbench Module</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_MODULE_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.common.projectmodule.impl.ModuleResourceImpl <em>Module Resource</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.projectmodule.impl.ModuleResourceImpl
	 * @see org.eclipse.wst.common.projectmodule.impl.ProjectModulePackageImpl#getModuleResource()
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
	 * The meta object id for the '{@link org.eclipse.wst.common.projectmodule.impl.WorkbenchApplicationImpl <em>Workbench Application</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.projectmodule.impl.WorkbenchApplicationImpl
	 * @see org.eclipse.wst.common.projectmodule.impl.ProjectModulePackageImpl#getWorkbenchApplication()
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
	 * Returns the meta object for class '{@link org.eclipse.wst.common.projectmodule.DeployedApplication <em>Deployed Application</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Deployed Application</em>'.
	 * @see org.eclipse.wst.common.projectmodule.DeployedApplication
	 * @generated
	 */
	EClass getDeployedApplication();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.projectmodule.DeployedApplication#getRoot <em>Root</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Root</em>'.
	 * @see org.eclipse.wst.common.projectmodule.DeployedApplication#getRoot()
	 * @see #getDeployedApplication()
	 * @generated
	 */
	EAttribute getDeployedApplication_Root();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.wst.common.projectmodule.DeployedApplication#getDeployScheme <em>Deploy Scheme</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Deploy Scheme</em>'.
	 * @see org.eclipse.wst.common.projectmodule.DeployedApplication#getDeployScheme()
	 * @see #getDeployedApplication()
	 * @generated
	 */
	EReference getDeployedApplication_DeployScheme();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.wst.common.projectmodule.DeployedApplication#getApplication <em>Application</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Application</em>'.
	 * @see org.eclipse.wst.common.projectmodule.DeployedApplication#getApplication()
	 * @see #getDeployedApplication()
	 * @generated
	 */
	EReference getDeployedApplication_Application();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.common.projectmodule.DeployScheme <em>Deploy Scheme</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Deploy Scheme</em>'.
	 * @see org.eclipse.wst.common.projectmodule.DeployScheme
	 * @generated
	 */
	EClass getDeployScheme();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.projectmodule.DeployScheme#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see org.eclipse.wst.common.projectmodule.DeployScheme#getType()
	 * @see #getDeployScheme()
	 * @generated
	 */
	EAttribute getDeployScheme_Type();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.projectmodule.DeployScheme#getServerTarget <em>Server Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Server Target</em>'.
	 * @see org.eclipse.wst.common.projectmodule.DeployScheme#getServerTarget()
	 * @see #getDeployScheme()
	 * @generated
	 */
	EAttribute getDeployScheme_ServerTarget();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.common.projectmodule.WorkbenchModule <em>Workbench Module</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Workbench Module</em>'.
	 * @see org.eclipse.wst.common.projectmodule.WorkbenchModule
	 * @generated
	 */
	EClass getWorkbenchModule();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.projectmodule.WorkbenchModule#getModuleType <em>Module Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Module Type</em>'.
	 * @see org.eclipse.wst.common.projectmodule.WorkbenchModule#getModuleType()
	 * @see #getWorkbenchModule()
	 * @generated
	 */
	EAttribute getWorkbenchModule_ModuleType();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.projectmodule.WorkbenchModule#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.wst.common.projectmodule.WorkbenchModule#getName()
	 * @see #getWorkbenchModule()
	 * @generated
	 */
	EAttribute getWorkbenchModule_Name();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.wst.common.projectmodule.WorkbenchModule#getDependentModules <em>Dependent Modules</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Dependent Modules</em>'.
	 * @see org.eclipse.wst.common.projectmodule.WorkbenchModule#getDependentModules()
	 * @see #getWorkbenchModule()
	 * @generated
	 */
	EReference getWorkbenchModule_DependentModules();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.wst.common.projectmodule.WorkbenchModule#getResources <em>Resources</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Resources</em>'.
	 * @see org.eclipse.wst.common.projectmodule.WorkbenchModule#getResources()
	 * @see #getWorkbenchModule()
	 * @generated
	 */
	EReference getWorkbenchModule_Resources();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.common.projectmodule.ModuleResource <em>Module Resource</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Module Resource</em>'.
	 * @see org.eclipse.wst.common.projectmodule.ModuleResource
	 * @generated
	 */
	EClass getModuleResource();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.projectmodule.ModuleResource#getPath <em>Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Path</em>'.
	 * @see org.eclipse.wst.common.projectmodule.ModuleResource#getPath()
	 * @see #getModuleResource()
	 * @generated
	 */
	EAttribute getModuleResource_Path();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.projectmodule.ModuleResource#getRoot <em>Root</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Root</em>'.
	 * @see org.eclipse.wst.common.projectmodule.ModuleResource#getRoot()
	 * @see #getModuleResource()
	 * @generated
	 */
	EAttribute getModuleResource_Root();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.wst.common.projectmodule.ModuleResource#getExclusions <em>Exclusions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Exclusions</em>'.
	 * @see org.eclipse.wst.common.projectmodule.ModuleResource#getExclusions()
	 * @see #getModuleResource()
	 * @generated
	 */
	EAttribute getModuleResource_Exclusions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.common.projectmodule.WorkbenchApplication <em>Workbench Application</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Workbench Application</em>'.
	 * @see org.eclipse.wst.common.projectmodule.WorkbenchApplication
	 * @generated
	 */
	EClass getWorkbenchApplication();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.wst.common.projectmodule.WorkbenchApplication#getModules <em>Modules</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Modules</em>'.
	 * @see org.eclipse.wst.common.projectmodule.WorkbenchApplication#getModules()
	 * @see #getWorkbenchApplication()
	 * @generated
	 */
	EReference getWorkbenchApplication_Modules();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	ProjectModuleFactory getProjectModuleFactory();

} //ProjectModulePackage
