/**
 * <copyright>
 * </copyright>
 *
 * $Id: ModuleCorePackageImpl.java,v 1.1 2005/02/13 16:27:46 cbridgha Exp $
 */
package org.eclipse.wst.common.modulecore.internal.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.wst.common.modulecore.DependencyType;
import org.eclipse.wst.common.modulecore.DependentModule;
import org.eclipse.wst.common.modulecore.ModuleCoreFactory;
import org.eclipse.wst.common.modulecore.ModuleCorePackage;
import org.eclipse.wst.common.modulecore.ModuleType;
import org.eclipse.wst.common.modulecore.ProjectModules;
import org.eclipse.wst.common.modulecore.WorkbenchModule;
import org.eclipse.wst.common.modulecore.WorkbenchModuleResource;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ModuleCorePackageImpl extends EPackageImpl implements ModuleCorePackage {
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
	private EClass workbenchModuleResourceEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass moduleTypeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass projectModulesEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dependentModuleEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum dependencyTypeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType uriEDataType = null;

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
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private ModuleCorePackageImpl() {
		super(eNS_URI, ModuleCoreFactory.eINSTANCE);
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
	public static ModuleCorePackage init() {
		if (isInited) return (ModuleCorePackage)EPackage.Registry.INSTANCE.getEPackage(ModuleCorePackage.eNS_URI);

		// Obtain or create and register package
		ModuleCorePackageImpl theModuleCorePackage = (ModuleCorePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof ModuleCorePackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new ModuleCorePackageImpl());

		isInited = true;

		// Create package meta-data objects
		theModuleCorePackage.createPackageContents();

		// Initialize created meta-data
		theModuleCorePackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theModuleCorePackage.freeze();

		return theModuleCorePackage;
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
	public EAttribute getWorkbenchModule_Handle() {
		return (EAttribute)workbenchModuleEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWorkbenchModule_DeployedName() {
		return (EAttribute)workbenchModuleEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getWorkbenchModule_Modules() {
		return (EReference)workbenchModuleEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getWorkbenchModule_Resources() {
		return (EReference)workbenchModuleEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getWorkbenchModule_ModuleType() {
		return (EReference)workbenchModuleEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getWorkbenchModuleResource() {
		return workbenchModuleResourceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWorkbenchModuleResource_SourcePath() {
		return (EAttribute)workbenchModuleResourceEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWorkbenchModuleResource_DeployedPath() {
		return (EAttribute)workbenchModuleResourceEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWorkbenchModuleResource_Exclusions() {
		return (EAttribute)workbenchModuleResourceEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getWorkbenchModuleResource_Module() {
		return (EReference)workbenchModuleResourceEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getModuleType() {
		return moduleTypeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getModuleType_MetadataResources() {
		return (EAttribute)moduleTypeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getModuleType_ModuleTypeId() {
		return (EAttribute)moduleTypeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getProjectModules() {
		return projectModulesEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getProjectModules_ProjectName() {
		return (EAttribute)projectModulesEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getProjectModules_WorkbenchModules() {
		return (EReference)projectModulesEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDependentModule() {
		return dependentModuleEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDependentModule_Handle() {
		return (EAttribute)dependentModuleEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDependentModule_DeployedPath() {
		return (EAttribute)dependentModuleEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDependentModule_DependencyType() {
		return (EAttribute)dependentModuleEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getDependencyType() {
		return dependencyTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getURI() {
		return uriEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ModuleCoreFactory getModuleCoreFactory() {
		return (ModuleCoreFactory)getEFactoryInstance();
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
		workbenchModuleEClass = createEClass(WORKBENCH_MODULE);
		createEAttribute(workbenchModuleEClass, WORKBENCH_MODULE__HANDLE);
		createEAttribute(workbenchModuleEClass, WORKBENCH_MODULE__DEPLOYED_NAME);
		createEReference(workbenchModuleEClass, WORKBENCH_MODULE__RESOURCES);
		createEReference(workbenchModuleEClass, WORKBENCH_MODULE__MODULE_TYPE);
		createEReference(workbenchModuleEClass, WORKBENCH_MODULE__MODULES);

		workbenchModuleResourceEClass = createEClass(WORKBENCH_MODULE_RESOURCE);
		createEAttribute(workbenchModuleResourceEClass, WORKBENCH_MODULE_RESOURCE__SOURCE_PATH);
		createEAttribute(workbenchModuleResourceEClass, WORKBENCH_MODULE_RESOURCE__DEPLOYED_PATH);
		createEAttribute(workbenchModuleResourceEClass, WORKBENCH_MODULE_RESOURCE__EXCLUSIONS);
		createEReference(workbenchModuleResourceEClass, WORKBENCH_MODULE_RESOURCE__MODULE);

		moduleTypeEClass = createEClass(MODULE_TYPE);
		createEAttribute(moduleTypeEClass, MODULE_TYPE__METADATA_RESOURCES);
		createEAttribute(moduleTypeEClass, MODULE_TYPE__MODULE_TYPE_ID);

		projectModulesEClass = createEClass(PROJECT_MODULES);
		createEAttribute(projectModulesEClass, PROJECT_MODULES__PROJECT_NAME);
		createEReference(projectModulesEClass, PROJECT_MODULES__WORKBENCH_MODULES);

		dependentModuleEClass = createEClass(DEPENDENT_MODULE);
		createEAttribute(dependentModuleEClass, DEPENDENT_MODULE__HANDLE);
		createEAttribute(dependentModuleEClass, DEPENDENT_MODULE__DEPLOYED_PATH);
		createEAttribute(dependentModuleEClass, DEPENDENT_MODULE__DEPENDENCY_TYPE);

		// Create enums
		dependencyTypeEEnum = createEEnum(DEPENDENCY_TYPE);

		// Create data types
		uriEDataType = createEDataType(URI);
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
		initEClass(workbenchModuleEClass, WorkbenchModule.class, "WorkbenchModule", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getWorkbenchModule_Handle(), this.getURI(), "handle", null, 0, 1, WorkbenchModule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWorkbenchModule_DeployedName(), ecorePackage.getEString(), "deployedName", null, 0, 1, WorkbenchModule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getWorkbenchModule_Resources(), this.getWorkbenchModuleResource(), this.getWorkbenchModuleResource_Module(), "resources", null, 0, -1, WorkbenchModule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getWorkbenchModule_ModuleType(), this.getModuleType(), null, "moduleType", null, 1, 1, WorkbenchModule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getWorkbenchModule_Modules(), this.getDependentModule(), null, "modules", null, 0, -1, WorkbenchModule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(workbenchModuleResourceEClass, WorkbenchModuleResource.class, "WorkbenchModuleResource", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getWorkbenchModuleResource_SourcePath(), this.getURI(), "sourcePath", null, 0, 1, WorkbenchModuleResource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWorkbenchModuleResource_DeployedPath(), this.getURI(), "deployedPath", null, 0, 1, WorkbenchModuleResource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWorkbenchModuleResource_Exclusions(), this.getURI(), "exclusions", null, 0, -1, WorkbenchModuleResource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getWorkbenchModuleResource_Module(), this.getWorkbenchModule(), this.getWorkbenchModule_Resources(), "module", null, 1, 1, WorkbenchModuleResource.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(moduleTypeEClass, ModuleType.class, "ModuleType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getModuleType_MetadataResources(), this.getURI(), "metadataResources", null, 0, -1, ModuleType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getModuleType_ModuleTypeId(), ecorePackage.getEString(), "moduleTypeId", null, 0, 1, ModuleType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(projectModulesEClass, ProjectModules.class, "ProjectModules", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getProjectModules_ProjectName(), ecorePackage.getEString(), "projectName", null, 0, 1, ProjectModules.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getProjectModules_WorkbenchModules(), this.getWorkbenchModule(), null, "workbenchModules", null, 0, -1, ProjectModules.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dependentModuleEClass, DependentModule.class, "DependentModule", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDependentModule_Handle(), this.getURI(), "handle", null, 0, 1, DependentModule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDependentModule_DeployedPath(), this.getURI(), "deployedPath", null, 0, 1, DependentModule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDependentModule_DependencyType(), this.getDependencyType(), "dependencyType", null, 0, 1, DependentModule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Initialize enums and add enum literals
		initEEnum(dependencyTypeEEnum, DependencyType.class, "DependencyType");
		addEEnumLiteral(dependencyTypeEEnum, DependencyType.USES_LITERAL);
		addEEnumLiteral(dependencyTypeEEnum, DependencyType.CONSUMES_LITERAL);

		// Initialize data types
		initEDataType(uriEDataType, org.eclipse.emf.common.util.URI.class, "URI", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);
	}

} //ModuleCorePackageImpl
