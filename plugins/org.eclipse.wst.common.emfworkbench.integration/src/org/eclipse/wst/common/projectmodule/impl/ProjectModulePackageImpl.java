/**
 * <copyright>
 * </copyright>
 *
 * $Id: ProjectModulePackageImpl.java,v 1.1 2005/01/14 21:02:41 cbridgha Exp $
 */
package org.eclipse.wst.common.projectmodule.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.wst.common.projectmodule.DeployScheme;
import org.eclipse.wst.common.projectmodule.DeployedApplication;
import org.eclipse.wst.common.projectmodule.ModuleResource;
import org.eclipse.wst.common.projectmodule.ProjectModuleFactory;
import org.eclipse.wst.common.projectmodule.ProjectModulePackage;
import org.eclipse.wst.common.projectmodule.WorkbenchApplication;
import org.eclipse.wst.common.projectmodule.WorkbenchModule;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ProjectModulePackageImpl extends EPackageImpl implements ProjectModulePackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass deployedApplicationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass deploySchemeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass workbenchModuleEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass moduleResourceEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass workbenchApplicationEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.wst.common.projectmodule.ProjectModulePackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private ProjectModulePackageImpl() {
		super(eNS_URI, ProjectModuleFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this
	 * model, and for any others upon which it depends.  Simple
	 * dependencies are satisfied by calling this method on all
	 * dependent packages before doing anything else.  This method drives
	 * initialization for interdependent packages directly, in parallel
	 * with this package, itself.
	 * <p>Of this package and its interdependencies, all packages which
	 * have not yet been registered by their URI values are first created
	 * and registered.  The packages are then initialized in two steps:
	 * meta-model objects for all of the packages are created before any
	 * are initialized, since one package's meta-model objects may refer to
	 * those of another.
	 * <p>Invocation of this method will not affect any packages that have
	 * already been initialized.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static ProjectModulePackage init() {
		if (isInited) return (ProjectModulePackage)EPackage.Registry.INSTANCE.getEPackage(ProjectModulePackage.eNS_URI);

		// Obtain or create and register package
		ProjectModulePackageImpl theProjectModulePackage = (ProjectModulePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof ProjectModulePackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new ProjectModulePackageImpl());

		isInited = true;

		// Create package meta-data objects
		theProjectModulePackage.createPackageContents();

		// Initialize created meta-data
		theProjectModulePackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theProjectModulePackage.freeze();

		return theProjectModulePackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDeployedApplication() {
		return deployedApplicationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDeployedApplication_Root() {
		return (EAttribute)deployedApplicationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDeployedApplication_DeployScheme() {
		return (EReference)deployedApplicationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDeployedApplication_Application() {
		return (EReference)deployedApplicationEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDeployScheme() {
		return deploySchemeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDeployScheme_Type() {
		return (EAttribute)deploySchemeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDeployScheme_ServerTarget() {
		return (EAttribute)deploySchemeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getWorkbenchModule() {
		return workbenchModuleEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWorkbenchModule_ModuleType() {
		return (EAttribute)workbenchModuleEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWorkbenchModule_Name() {
		return (EAttribute)workbenchModuleEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getWorkbenchModule_DependentModules() {
		return (EReference)workbenchModuleEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getWorkbenchModule_Resources() {
		return (EReference)workbenchModuleEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getModuleResource() {
		return moduleResourceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getModuleResource_Path() {
		return (EAttribute)moduleResourceEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getModuleResource_Root() {
		return (EAttribute)moduleResourceEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getModuleResource_Exclusions() {
		return (EAttribute)moduleResourceEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getWorkbenchApplication() {
		return workbenchApplicationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getWorkbenchApplication_Modules() {
		return (EReference)workbenchApplicationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ProjectModuleFactory getProjectModuleFactory() {
		return (ProjectModuleFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		deployedApplicationEClass = createEClass(DEPLOYED_APPLICATION);
		createEAttribute(deployedApplicationEClass, DEPLOYED_APPLICATION__ROOT);
		createEReference(deployedApplicationEClass, DEPLOYED_APPLICATION__DEPLOY_SCHEME);
		createEReference(deployedApplicationEClass, DEPLOYED_APPLICATION__APPLICATION);

		deploySchemeEClass = createEClass(DEPLOY_SCHEME);
		createEAttribute(deploySchemeEClass, DEPLOY_SCHEME__TYPE);
		createEAttribute(deploySchemeEClass, DEPLOY_SCHEME__SERVER_TARGET);

		workbenchModuleEClass = createEClass(WORKBENCH_MODULE);
		createEAttribute(workbenchModuleEClass, WORKBENCH_MODULE__MODULE_TYPE);
		createEAttribute(workbenchModuleEClass, WORKBENCH_MODULE__NAME);
		createEReference(workbenchModuleEClass, WORKBENCH_MODULE__DEPENDENT_MODULES);
		createEReference(workbenchModuleEClass, WORKBENCH_MODULE__RESOURCES);

		moduleResourceEClass = createEClass(MODULE_RESOURCE);
		createEAttribute(moduleResourceEClass, MODULE_RESOURCE__PATH);
		createEAttribute(moduleResourceEClass, MODULE_RESOURCE__ROOT);
		createEAttribute(moduleResourceEClass, MODULE_RESOURCE__EXCLUSIONS);

		workbenchApplicationEClass = createEClass(WORKBENCH_APPLICATION);
		createEReference(workbenchApplicationEClass, WORKBENCH_APPLICATION__MODULES);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Add supertypes to classes

		// Initialize classes and features; add operations and parameters
		initEClass(deployedApplicationEClass, DeployedApplication.class, "DeployedApplication", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDeployedApplication_Root(), ecorePackage.getEString(), "root", null, 0, 1, DeployedApplication.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDeployedApplication_DeployScheme(), this.getDeployScheme(), null, "deployScheme", null, 0, 1, DeployedApplication.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDeployedApplication_Application(), this.getWorkbenchApplication(), null, "application", null, 1, 1, DeployedApplication.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(deploySchemeEClass, DeployScheme.class, "DeployScheme", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDeployScheme_Type(), ecorePackage.getEString(), "type", null, 0, 1, DeployScheme.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDeployScheme_ServerTarget(), ecorePackage.getEString(), "serverTarget", null, 0, 1, DeployScheme.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(workbenchModuleEClass, WorkbenchModule.class, "WorkbenchModule", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getWorkbenchModule_ModuleType(), ecorePackage.getEString(), "moduleType", null, 0, 1, WorkbenchModule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWorkbenchModule_Name(), ecorePackage.getEString(), "name", null, 0, 1, WorkbenchModule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getWorkbenchModule_DependentModules(), this.getWorkbenchModule(), null, "dependentModules", null, 0, -1, WorkbenchModule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getWorkbenchModule_Resources(), this.getModuleResource(), null, "resources", null, 0, -1, WorkbenchModule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(moduleResourceEClass, ModuleResource.class, "ModuleResource", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getModuleResource_Path(), ecorePackage.getEString(), "path", null, 0, 1, ModuleResource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getModuleResource_Root(), ecorePackage.getEString(), "root", null, 0, 1, ModuleResource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getModuleResource_Exclusions(), ecorePackage.getEString(), "exclusions", null, 0, -1, ModuleResource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(workbenchApplicationEClass, WorkbenchApplication.class, "WorkbenchApplication", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getWorkbenchApplication_Modules(), this.getWorkbenchModule(), null, "modules", null, 0, -1, WorkbenchApplication.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);
	}

} //ProjectModulePackageImpl
