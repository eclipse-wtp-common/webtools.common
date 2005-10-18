/**
 * <copyright>
 * </copyright>
 *
 * $Id: ComponentcorePackage.java,v 1.6 2005/10/18 22:25:43 cbridgha Exp $
 */
package org.eclipse.wst.common.componentcore.internal;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
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
 * @see org.eclipse.wst.common.componentcore.internal.ComponentcoreFactory
 * @model kind="package"
 * @generated
 */
public interface ComponentcorePackage extends EPackage{
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "componentcore";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "componentcore.xmi";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "org.eclipse.wst.common.componentcore";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ComponentcorePackage eINSTANCE = org.eclipse.wst.common.componentcore.internal.impl.ComponentcorePackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.wst.common.componentcore.internal.impl.WorkbenchComponentImpl <em>Workbench Component</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.componentcore.internal.impl.WorkbenchComponentImpl
	 * @see org.eclipse.wst.common.componentcore.internal.impl.ComponentcorePackageImpl#getWorkbenchComponent()
	 * @generated
	 */
	int WORKBENCH_COMPONENT = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_COMPONENT__NAME = 0;

	/**
	 * The feature id for the '<em><b>Resources</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_COMPONENT__RESOURCES = 1;

	/**
	 * The feature id for the '<em><b>Component Type</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_COMPONENT__COMPONENT_TYPE = 2;

	/**
	 * The feature id for the '<em><b>Referenced Components</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_COMPONENT__REFERENCED_COMPONENTS = 3;

	/**
	 * The feature id for the '<em><b>Properties</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_COMPONENT__PROPERTIES = 4;

	/**
	 * The feature id for the '<em><b>Metadata Resources</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_COMPONENT__METADATA_RESOURCES = 5;

	/**
	 * The number of structural features of the the '<em>Workbench Component</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_COMPONENT_FEATURE_COUNT = 6;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.common.componentcore.internal.impl.ComponentResourceImpl <em>Component Resource</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.componentcore.internal.impl.ComponentResourceImpl
	 * @see org.eclipse.wst.common.componentcore.internal.impl.ComponentcorePackageImpl#getComponentResource()
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
	 * The feature id for the '<em><b>Resource Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_RESOURCE__RESOURCE_TYPE = 4;

	/**
	 * The number of structural features of the the '<em>Component Resource</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_RESOURCE_FEATURE_COUNT = 5;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.common.componentcore.internal.impl.ComponentTypeImpl <em>Component Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.componentcore.internal.impl.ComponentTypeImpl
	 * @see org.eclipse.wst.common.componentcore.internal.impl.ComponentcorePackageImpl#getComponentType()
	 * @generated
	 */
	int COMPONENT_TYPE = 2;

	/**
	 * The feature id for the '<em><b>Component Type Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_TYPE__COMPONENT_TYPE_ID = 0;

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
	 * The meta object id for the '{@link org.eclipse.wst.common.componentcore.internal.impl.PropertyImpl <em>Property</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.componentcore.internal.impl.PropertyImpl
	 * @see org.eclipse.wst.common.componentcore.internal.impl.ComponentcorePackageImpl#getProperty()
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
	 * The meta object id for the '{@link org.eclipse.wst.common.componentcore.internal.impl.ReferencedComponentImpl <em>Referenced Component</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.componentcore.internal.impl.ReferencedComponentImpl
	 * @see org.eclipse.wst.common.componentcore.internal.impl.ComponentcorePackageImpl#getReferencedComponent()
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
	 * The feature id for the '<em><b>Dependent Object</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCED_COMPONENT__DEPENDENT_OBJECT = 3;

	/**
	 * The number of structural features of the the '<em>Referenced Component</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCED_COMPONENT_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.common.componentcore.internal.impl.ProjectComponentsImpl <em>Project Components</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.componentcore.internal.impl.ProjectComponentsImpl
	 * @see org.eclipse.wst.common.componentcore.internal.impl.ComponentcorePackageImpl#getProjectComponents()
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
	 * The meta object id for the '{@link org.eclipse.wst.common.componentcore.internal.DependencyType <em>Dependency Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.componentcore.internal.DependencyType
	 * @see org.eclipse.wst.common.componentcore.internal.impl.ComponentcorePackageImpl#getDependencyType()
	 * @generated
	 */
	int DEPENDENCY_TYPE = 6;

	/**
	 * The meta object id for the '<em>IPath</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.core.runtime.IPath
	 * @see org.eclipse.wst.common.componentcore.internal.impl.ComponentcorePackageImpl#getIPath()
	 * @generated
	 */
	int IPATH = 7;

	/**
	 * The meta object id for the '<em>URI</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.common.util.URI
	 * @see org.eclipse.wst.common.componentcore.internal.impl.ComponentcorePackageImpl#getURI()
	 * @generated
	 */
	int URI = 8;


	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.common.componentcore.internal.WorkbenchComponent <em>Workbench Component</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Workbench Component</em>'.
	 * @see org.eclipse.wst.common.componentcore.internal.WorkbenchComponent
	 * @generated
	 */
	EClass getWorkbenchComponent();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.componentcore.internal.WorkbenchComponent#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.wst.common.componentcore.internal.WorkbenchComponent#getName()
	 * @see #getWorkbenchComponent()
	 * @generated
	 */
	EAttribute getWorkbenchComponent_Name();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.wst.common.componentcore.internal.WorkbenchComponent#getResources <em>Resources</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Resources</em>'.
	 * @see org.eclipse.wst.common.componentcore.internal.WorkbenchComponent#getResources()
	 * @see #getWorkbenchComponent()
	 * @generated
	 */
	EReference getWorkbenchComponent_Resources();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.wst.common.componentcore.internal.WorkbenchComponent#getComponentType <em>Component Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Component Type</em>'.
	 * @see org.eclipse.wst.common.componentcore.internal.WorkbenchComponent#getComponentType()
	 * @see #getWorkbenchComponent()
	 * @generated
	 */
	EReference getWorkbenchComponent_ComponentType();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.wst.common.componentcore.internal.WorkbenchComponent#getReferencedComponents <em>Referenced Components</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Referenced Components</em>'.
	 * @see org.eclipse.wst.common.componentcore.internal.WorkbenchComponent#getReferencedComponents()
	 * @see #getWorkbenchComponent()
	 * @generated
	 */
	EReference getWorkbenchComponent_ReferencedComponents();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.wst.common.componentcore.internal.WorkbenchComponent#getProperties <em>Properties</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Properties</em>'.
	 * @see org.eclipse.wst.common.componentcore.internal.WorkbenchComponent#getProperties()
	 * @see #getWorkbenchComponent()
	 * @generated
	 */
	EReference getWorkbenchComponent_Properties();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.wst.common.componentcore.internal.WorkbenchComponent#getMetadataResources <em>Metadata Resources</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Metadata Resources</em>'.
	 * @see org.eclipse.wst.common.componentcore.internal.WorkbenchComponent#getMetadataResources()
	 * @see #getWorkbenchComponent()
	 * @generated
	 */
	EAttribute getWorkbenchComponent_MetadataResources();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.common.componentcore.internal.ComponentResource <em>Component Resource</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Component Resource</em>'.
	 * @see org.eclipse.wst.common.componentcore.internal.ComponentResource
	 * @generated
	 */
	EClass getComponentResource();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.componentcore.internal.ComponentResource#getSourcePath <em>Source Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Source Path</em>'.
	 * @see org.eclipse.wst.common.componentcore.internal.ComponentResource#getSourcePath()
	 * @see #getComponentResource()
	 * @generated
	 */
	EAttribute getComponentResource_SourcePath();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.componentcore.internal.ComponentResource#getRuntimePath <em>Runtime Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Runtime Path</em>'.
	 * @see org.eclipse.wst.common.componentcore.internal.ComponentResource#getRuntimePath()
	 * @see #getComponentResource()
	 * @generated
	 */
	EAttribute getComponentResource_RuntimePath();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.wst.common.componentcore.internal.ComponentResource#getExclusions <em>Exclusions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Exclusions</em>'.
	 * @see org.eclipse.wst.common.componentcore.internal.ComponentResource#getExclusions()
	 * @see #getComponentResource()
	 * @generated
	 */
	EAttribute getComponentResource_Exclusions();

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.wst.common.componentcore.internal.ComponentResource#getComponent <em>Component</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Component</em>'.
	 * @see org.eclipse.wst.common.componentcore.internal.ComponentResource#getComponent()
	 * @see #getComponentResource()
	 * @generated
	 */
	EReference getComponentResource_Component();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.componentcore.internal.ComponentResource#getResourceType <em>Resource Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Resource Type</em>'.
	 * @see org.eclipse.wst.common.componentcore.internal.ComponentResource#getResourceType()
	 * @see #getComponentResource()
	 * @generated
	 */
	EAttribute getComponentResource_ResourceType();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.common.componentcore.internal.ComponentType <em>Component Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Component Type</em>'.
	 * @see org.eclipse.wst.common.componentcore.internal.ComponentType
	 * @generated
	 */
	EClass getComponentType();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.componentcore.internal.ComponentType#getComponentTypeId <em>Component Type Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Component Type Id</em>'.
	 * @see org.eclipse.wst.common.componentcore.internal.ComponentType#getComponentTypeId()
	 * @see #getComponentType()
	 * @generated
	 */
	EAttribute getComponentType_ComponentTypeId();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.componentcore.internal.ComponentType#getVersion <em>Version</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Version</em>'.
	 * @see org.eclipse.wst.common.componentcore.internal.ComponentType#getVersion()
	 * @see #getComponentType()
	 * @generated
	 */
	EAttribute getComponentType_Version();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.wst.common.componentcore.internal.ComponentType#getProperties <em>Properties</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Properties</em>'.
	 * @see org.eclipse.wst.common.componentcore.internal.ComponentType#getProperties()
	 * @see #getComponentType()
	 * @generated
	 */
	EReference getComponentType_Properties();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.wst.common.componentcore.internal.ComponentType#getMetadataResources <em>Metadata Resources</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Metadata Resources</em>'.
	 * @see org.eclipse.wst.common.componentcore.internal.ComponentType#getMetadataResources()
	 * @see #getComponentType()
	 * @generated
	 */
	EAttribute getComponentType_MetadataResources();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.common.componentcore.internal.Property <em>Property</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Property</em>'.
	 * @see org.eclipse.wst.common.componentcore.internal.Property
	 * @generated
	 */
	EClass getProperty();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.componentcore.internal.Property#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.wst.common.componentcore.internal.Property#getName()
	 * @see #getProperty()
	 * @generated
	 */
	EAttribute getProperty_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.componentcore.internal.Property#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.wst.common.componentcore.internal.Property#getValue()
	 * @see #getProperty()
	 * @generated
	 */
	EAttribute getProperty_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.common.componentcore.internal.ReferencedComponent <em>Referenced Component</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Referenced Component</em>'.
	 * @see org.eclipse.wst.common.componentcore.internal.ReferencedComponent
	 * @generated
	 */
	EClass getReferencedComponent();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.componentcore.internal.ReferencedComponent#getHandle <em>Handle</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Handle</em>'.
	 * @see org.eclipse.wst.common.componentcore.internal.ReferencedComponent#getHandle()
	 * @see #getReferencedComponent()
	 * @generated
	 */
	EAttribute getReferencedComponent_Handle();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.componentcore.internal.ReferencedComponent#getRuntimePath <em>Runtime Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Runtime Path</em>'.
	 * @see org.eclipse.wst.common.componentcore.internal.ReferencedComponent#getRuntimePath()
	 * @see #getReferencedComponent()
	 * @generated
	 */
	EAttribute getReferencedComponent_RuntimePath();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.componentcore.internal.ReferencedComponent#getDependencyType <em>Dependency Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Dependency Type</em>'.
	 * @see org.eclipse.wst.common.componentcore.internal.ReferencedComponent#getDependencyType()
	 * @see #getReferencedComponent()
	 * @generated
	 */
	EAttribute getReferencedComponent_DependencyType();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.wst.common.componentcore.internal.ReferencedComponent#getDependentObject <em>Dependent Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Dependent Object</em>'.
	 * @see org.eclipse.wst.common.componentcore.internal.ReferencedComponent#getDependentObject()
	 * @see #getReferencedComponent()
	 * @generated
	 */
	EReference getReferencedComponent_DependentObject();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.common.componentcore.internal.ProjectComponents <em>Project Components</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Project Components</em>'.
	 * @see org.eclipse.wst.common.componentcore.internal.ProjectComponents
	 * @generated
	 */
	EClass getProjectComponents();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.componentcore.internal.ProjectComponents#getProjectName <em>Project Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Project Name</em>'.
	 * @see org.eclipse.wst.common.componentcore.internal.ProjectComponents#getProjectName()
	 * @see #getProjectComponents()
	 * @generated
	 */
	EAttribute getProjectComponents_ProjectName();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.wst.common.componentcore.internal.ProjectComponents#getComponents <em>Components</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Components</em>'.
	 * @see org.eclipse.wst.common.componentcore.internal.ProjectComponents#getComponents()
	 * @see #getProjectComponents()
	 * @generated
	 */
	EReference getProjectComponents_Components();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.wst.common.componentcore.internal.DependencyType <em>Dependency Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Dependency Type</em>'.
	 * @see org.eclipse.wst.common.componentcore.internal.DependencyType
	 * @generated
	 */
	EEnum getDependencyType();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.core.runtime.IPath <em>IPath</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>IPath</em>'.
	 * @see org.eclipse.core.runtime.IPath
	 * @model instanceClass="org.eclipse.core.runtime.IPath"
	 *        annotation="keywords datatype='null'" 
	 * @generated
	 */
	EDataType getIPath();

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
	ComponentcoreFactory getComponentcoreFactory();

} //ComponentcorePackage
