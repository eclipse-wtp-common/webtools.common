/**
 * <copyright>
 * </copyright>
 *
 * $Id: ComponentcoreFactory.java,v 1.2 2006/04/27 04:17:40 cbridgha Exp $
 */
package org.eclipse.wst.common.componentcore.internal;

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.wst.common.componentcore.internal.impl.ComponentcoreFactoryImpl;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.wst.common.componentcore.internal.ComponentcorePackage
 * @generated
 */
public interface ComponentcoreFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ComponentcoreFactory eINSTANCE = ComponentcoreFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Workbench Component</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Workbench Component</em>'.
	 * @generated
	 */
	WorkbenchComponent createWorkbenchComponent();

	/**
	 * Returns a new object of class '<em>Component Resource</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Component Resource</em>'.
	 * @generated
	 */
	ComponentResource createComponentResource();

	/**
	 * Returns a new object of class '<em>Component Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Component Type</em>'.
	 * @generated
	 */
	ComponentType createComponentType();

	/**
	 * Returns a new object of class '<em>Property</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Property</em>'.
	 * @generated
	 */
	Property createProperty();

	/**
	 * Returns a new object of class '<em>Referenced Component</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Referenced Component</em>'.
	 * @generated
	 */
	ReferencedComponent createReferencedComponent();

	/**
	 * Returns a new object of class '<em>Project Components</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Project Components</em>'.
	 * @generated
	 */
	ProjectComponents createProjectComponents();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	ComponentcorePackage getComponentcorePackage();

} //ComponentcoreFactory
