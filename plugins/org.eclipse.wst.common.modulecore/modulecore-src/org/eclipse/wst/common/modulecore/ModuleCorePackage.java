/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.modulecore;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>.
 * 
 * <!-- end-user-doc -->
 * @see org.eclipse.wst.common.modulecore.ModuleCoreFactory
 * @generated
 */
public interface ModuleCorePackage extends EPackage{
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "modulecore"; //$NON-NLS-1$

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "modulecore.xmi"; //$NON-NLS-1$

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "org.eclipse.wst.common.modulecore"; //$NON-NLS-1$

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ModuleCorePackage eINSTANCE = org.eclipse.wst.common.modulecore.internal.impl.ModuleCorePackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.wst.common.modulecore.impl.WorkbenchComponentImpl <em>Workbench Component</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.modulecore.impl.WorkbenchComponentImpl
	 * @see org.eclipse.wst.common.modulecore.impl.ModuleCorePackageImpl#getWorkbenchComponent()
	 * @generated
	 */
	int WORKBENCH_COMPONENT = 0;

	/**
	 * The feature id for the '<em><b>Handle</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_COMPONENT__HANDLE = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_COMPONENT__NAME = 1;

	/**
	 * The feature id for the '<em><b>Resources</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_COMPONENT__RESOURCES = 2;

	/**
	 * The feature id for the '<em><b>Component Type</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_COMPONENT__COMPONENT_TYPE = 3;

	/**
	 * The feature id for the '<em><b>Referenced Components</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_COMPONENT__REFERENCED_COMPONENTS = 4;

	/**
	 * The number of structural features of the the '<em>Workbench Component</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_COMPONENT_FEATURE_COUNT = 5;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.common.modulecore.impl.ComponentResourceImpl <em>Component Resource</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.modulecore.impl.ComponentResourceImpl
	 * @see org.eclipse.wst.common.modulecore.impl.ModuleCorePackageImpl#getComponentResource()
	 * @generated
	 */
	int COMPONENT_RESOURCE = 1;

	/**
	 * The feature id for the '<em><b>Source Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_RESOURCE__SOURCE_PATH = 0;

	/**
	 * The feature id for the '<em><b>Runtime Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_RESOURCE__RUNTIME_PATH = 1;

	/**
	 * The feature id for the '<em><b>Exclusions</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_RESOURCE__EXCLUSIONS = 2;

	/**
	 * The feature id for the '<em><b>Component</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_RESOURCE__COMPONENT = 3;

	/**
	 * The number of structural features of the the '<em>Component Resource</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_RESOURCE_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.common.modulecore.impl.ComponentTypeImpl <em>Component Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.modulecore.impl.ComponentTypeImpl
	 * @see org.eclipse.wst.common.modulecore.impl.ModuleCorePackageImpl#getComponentType()
	 * @generated
	 */
	int COMPONENT_TYPE = 2;

	/**
	 * The feature id for the '<em><b>Module Type Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_TYPE__MODULE_TYPE_ID = 0;

	/**
	 * The feature id for the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_TYPE__VERSION = 1;

	/**
	 * The feature id for the '<em><b>Properties</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_TYPE__PROPERTIES = 2;

	/**
	 * The feature id for the '<em><b>Metadata Resources</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_TYPE__METADATA_RESOURCES = 3;

	/**
	 * The number of structural features of the the '<em>Component Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_TYPE_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.common.modulecore.impl.PropertyImpl <em>Property</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.modulecore.impl.PropertyImpl
	 * @see org.eclipse.wst.common.modulecore.impl.ModuleCorePackageImpl#getProperty()
	 * @generated
	 */
	int PROPERTY = 3;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY__NAME = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY__VALUE = 1;

	/**
	 * The number of structural features of the the '<em>Property</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.common.modulecore.impl.ReferencedComponentImpl <em>Referenced Component</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.modulecore.impl.ReferencedComponentImpl
	 * @see org.eclipse.wst.common.modulecore.impl.ModuleCorePackageImpl#getReferencedComponent()
	 * @generated
	 */
	int REFERENCED_COMPONENT = 4;

	/**
	 * The feature id for the '<em><b>Handle</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCED_COMPONENT__HANDLE = 0;

	/**
	 * The feature id for the '<em><b>Runtime Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCED_COMPONENT__RUNTIME_PATH = 1;

	/**
	 * The feature id for the '<em><b>Dependency Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCED_COMPONENT__DEPENDENCY_TYPE = 2;

	/**
	 * The number of structural features of the the '<em>Referenced Component</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCED_COMPONENT_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.common.modulecore.impl.ProjectComponentsImpl <em>Project Components</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.modulecore.impl.ProjectComponentsImpl
	 * @see org.eclipse.wst.common.modulecore.impl.ModuleCorePackageImpl#getProjectComponents()
	 * @generated
	 */
	int PROJECT_COMPONENTS = 5;

	/**
	 * The feature id for the '<em><b>Project Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT_COMPONENTS__PROJECT_NAME = 0;

	/**
	 * The feature id for the '<em><b>Components</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT_COMPONENTS__COMPONENTS = 1;

	/**
	 * The number of structural features of the the '<em>Project Components</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT_COMPONENTS_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.common.modulecore.DependencyType <em>Dependency Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.modulecore.DependencyType
	 * @see org.eclipse.wst.common.modulecore.impl.ModuleCorePackageImpl#getDependencyType()
	 * @generated
	 */
	int DEPENDENCY_TYPE = 6;

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
	 * Returns the meta object for class '{@link org.eclipse.wst.common.modulecore.WorkbenchComponent <em>Workbench Component</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Workbench Component</em>'.
	 * @see org.eclipse.wst.common.modulecore.WorkbenchComponent
	 * @generated
	 */
	EClass getWorkbenchComponent();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.modulecore.WorkbenchComponent#getHandle <em>Handle</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Handle</em>'.
	 * @see org.eclipse.wst.common.modulecore.WorkbenchComponent#getHandle()
	 * @see #getWorkbenchComponent()
	 * @generated
	 */
	EAttribute getWorkbenchComponent_Handle();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.modulecore.WorkbenchComponent#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.wst.common.modulecore.WorkbenchComponent#getName()
	 * @see #getWorkbenchComponent()
	 * @generated
	 */
	EAttribute getWorkbenchComponent_Name();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.wst.common.modulecore.WorkbenchComponent#getResources <em>Resources</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Resources</em>'.
	 * @see org.eclipse.wst.common.modulecore.WorkbenchComponent#getResources()
	 * @see #getWorkbenchComponent()
	 * @generated
	 */
	EReference getWorkbenchComponent_Resources();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.wst.common.modulecore.WorkbenchComponent#getComponentType <em>Component Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Component Type</em>'.
	 * @see org.eclipse.wst.common.modulecore.WorkbenchComponent#getComponentType()
	 * @see #getWorkbenchComponent()
	 * @generated
	 */
	EReference getWorkbenchComponent_ComponentType();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.wst.common.modulecore.WorkbenchComponent#getReferencedComponents <em>Referenced Components</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Referenced Components</em>'.
	 * @see org.eclipse.wst.common.modulecore.WorkbenchComponent#getReferencedComponents()
	 * @see #getWorkbenchComponent()
	 * @generated
	 */
	EReference getWorkbenchComponent_ReferencedComponents();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.common.modulecore.ComponentResource <em>Component Resource</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Component Resource</em>'.
	 * @see org.eclipse.wst.common.modulecore.ComponentResource
	 * @generated
	 */
	EClass getComponentResource();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.modulecore.ComponentResource#getSourcePath <em>Source Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Source Path</em>'.
	 * @see org.eclipse.wst.common.modulecore.ComponentResource#getSourcePath()
	 * @see #getComponentResource()
	 * @generated
	 */
	EAttribute getComponentResource_SourcePath();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.modulecore.ComponentResource#getRuntimePath <em>Runtime Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Runtime Path</em>'.
	 * @see org.eclipse.wst.common.modulecore.ComponentResource#getRuntimePath()
	 * @see #getComponentResource()
	 * @generated
	 */
	EAttribute getComponentResource_RuntimePath();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.wst.common.modulecore.ComponentResource#getExclusions <em>Exclusions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Exclusions</em>'.
	 * @see org.eclipse.wst.common.modulecore.ComponentResource#getExclusions()
	 * @see #getComponentResource()
	 * @generated
	 */
	EAttribute getComponentResource_Exclusions();

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.wst.common.modulecore.ComponentResource#getComponent <em>Component</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Component</em>'.
	 * @see org.eclipse.wst.common.modulecore.ComponentResource#getComponent()
	 * @see #getComponentResource()
	 * @generated
	 */
	EReference getComponentResource_Component();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.common.modulecore.ComponentType <em>Component Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Component Type</em>'.
	 * @see org.eclipse.wst.common.modulecore.ComponentType
	 * @generated
	 */
	EClass getComponentType();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.modulecore.ComponentType#getModuleTypeId <em>Module Type Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Module Type Id</em>'.
	 * @see org.eclipse.wst.common.modulecore.ComponentType#getModuleTypeId()
	 * @see #getComponentType()
	 * @generated
	 */
	EAttribute getComponentType_ModuleTypeId();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.modulecore.ComponentType#getVersion <em>Version</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Version</em>'.
	 * @see org.eclipse.wst.common.modulecore.ComponentType#getVersion()
	 * @see #getComponentType()
	 * @generated
	 */
	EAttribute getComponentType_Version();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.wst.common.modulecore.ComponentType#getProperties <em>Properties</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Properties</em>'.
	 * @see org.eclipse.wst.common.modulecore.ComponentType#getProperties()
	 * @see #getComponentType()
	 * @generated
	 */
	EReference getComponentType_Properties();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.wst.common.modulecore.ComponentType#getMetadataResources <em>Metadata Resources</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Metadata Resources</em>'.
	 * @see org.eclipse.wst.common.modulecore.ComponentType#getMetadataResources()
	 * @see #getComponentType()
	 * @generated
	 */
	EAttribute getComponentType_MetadataResources();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.common.modulecore.Property <em>Property</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Property</em>'.
	 * @see org.eclipse.wst.common.modulecore.Property
	 * @generated
	 */
	EClass getProperty();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.modulecore.Property#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.wst.common.modulecore.Property#getName()
	 * @see #getProperty()
	 * @generated
	 */
	EAttribute getProperty_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.modulecore.Property#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.wst.common.modulecore.Property#getValue()
	 * @see #getProperty()
	 * @generated
	 */
	EAttribute getProperty_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.common.modulecore.ReferencedComponent <em>Referenced Component</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Referenced Component</em>'.
	 * @see org.eclipse.wst.common.modulecore.ReferencedComponent
	 * @generated
	 */
	EClass getReferencedComponent();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.modulecore.ReferencedComponent#getHandle <em>Handle</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Handle</em>'.
	 * @see org.eclipse.wst.common.modulecore.ReferencedComponent#getHandle()
	 * @see #getReferencedComponent()
	 * @generated
	 */
	EAttribute getReferencedComponent_Handle();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.modulecore.ReferencedComponent#getRuntimePath <em>Runtime Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Runtime Path</em>'.
	 * @see org.eclipse.wst.common.modulecore.ReferencedComponent#getRuntimePath()
	 * @see #getReferencedComponent()
	 * @generated
	 */
	EAttribute getReferencedComponent_RuntimePath();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.modulecore.ReferencedComponent#getDependencyType <em>Dependency Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Dependency Type</em>'.
	 * @see org.eclipse.wst.common.modulecore.ReferencedComponent#getDependencyType()
	 * @see #getReferencedComponent()
	 * @generated
	 */
	EAttribute getReferencedComponent_DependencyType();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.common.modulecore.ProjectComponents <em>Project Components</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Project Components</em>'.
	 * @see org.eclipse.wst.common.modulecore.ProjectComponents
	 * @generated
	 */
	EClass getProjectComponents();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.modulecore.ProjectComponents#getProjectName <em>Project Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Project Name</em>'.
	 * @see org.eclipse.wst.common.modulecore.ProjectComponents#getProjectName()
	 * @see #getProjectComponents()
	 * @generated
	 */
	EAttribute getProjectComponents_ProjectName();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.wst.common.modulecore.ProjectComponents#getComponents <em>Components</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Components</em>'.
	 * @see org.eclipse.wst.common.modulecore.ProjectComponents#getComponents()
	 * @see #getProjectComponents()
	 * @generated
	 */
	EReference getProjectComponents_Components();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.wst.common.modulecore.DependencyType <em>Dependency Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Dependency Type</em>'.
	 * @see org.eclipse.wst.common.modulecore.DependencyType
	 * @generated
	 */
	EEnum getDependencyType();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.emf.common.util.URI <em>URI</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>URI</em>'.
	 * @see org.eclipse.emf.common.util.URI
	 * @model instanceClass="org.eclipse.emf.common.util.URI"
	 *        annotation="keywords datatype='null'" 
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
