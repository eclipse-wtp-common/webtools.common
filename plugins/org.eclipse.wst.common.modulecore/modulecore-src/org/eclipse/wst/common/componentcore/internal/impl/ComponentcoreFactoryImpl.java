/**
 * <copyright>
 * </copyright>
 *
 * $Id: ComponentcoreFactoryImpl.java,v 1.1 2005/04/04 07:04:59 cbridgha Exp $
 */
package org.eclipse.wst.common.componentcore.internal.impl;

import org.eclipse.core.runtime.IPath;

import org.eclipse.emf.common.util.URI;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.wst.common.componentcore.internal.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ComponentcoreFactoryImpl extends EFactoryImpl implements ComponentcoreFactory {
	/**
	 * Creates and instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ComponentcoreFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case ComponentcorePackage.WORKBENCH_COMPONENT: return createWorkbenchComponent();
			case ComponentcorePackage.COMPONENT_RESOURCE: return createComponentResource();
			case ComponentcorePackage.COMPONENT_TYPE: return createComponentType();
			case ComponentcorePackage.PROPERTY: return createProperty();
			case ComponentcorePackage.REFERENCED_COMPONENT: return createReferencedComponent();
			case ComponentcorePackage.PROJECT_COMPONENTS: return createProjectComponents();
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
			case ComponentcorePackage.DEPENDENCY_TYPE: {
				DependencyType result = DependencyType.get(initialValue);
				if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
				return result;
			}
			case ComponentcorePackage.IPATH:
				return createIPathFromString(eDataType, initialValue);
			case ComponentcorePackage.URI:
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
			case ComponentcorePackage.DEPENDENCY_TYPE:
				return instanceValue == null ? null : instanceValue.toString();
			case ComponentcorePackage.IPATH:
				return convertIPathToString(eDataType, instanceValue);
			case ComponentcorePackage.URI:
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
	public WorkbenchComponent createWorkbenchComponent() {
		WorkbenchComponentImpl workbenchComponent = new WorkbenchComponentImpl();
		return workbenchComponent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ComponentResource createComponentResource() {
		ComponentResourceImpl componentResource = new ComponentResourceImpl();
		return componentResource;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ComponentType createComponentType() {
		ComponentTypeImpl componentType = new ComponentTypeImpl();
		return componentType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Property createProperty() {
		PropertyImpl property = new PropertyImpl();
		return property;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ReferencedComponent createReferencedComponent() {
		ReferencedComponentImpl referencedComponent = new ReferencedComponentImpl();
		return referencedComponent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ProjectComponents createProjectComponents() {
		ProjectComponentsImpl projectComponents = new ProjectComponentsImpl();
		return projectComponents;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IPath createIPathFromString(EDataType eDataType, String initialValue) {
		return (IPath)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertIPathToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
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
	public ComponentcorePackage getComponentcorePackage() {
		return (ComponentcorePackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	public static ComponentcorePackage getPackage() {
		return ComponentcorePackage.eINSTANCE;
	}

} //ComponentcoreFactoryImpl
