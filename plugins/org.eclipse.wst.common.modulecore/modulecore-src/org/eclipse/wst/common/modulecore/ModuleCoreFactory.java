/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.modulecore;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> 
 * The EMF Factory used to create empty, uncontained instances of model objects. 
 * <p>
 * Clients are encouraged to use {@see org.eclipse.wst.common.modulecore.ModuleCore}&nbsp;when
 * constructing specific Module Structural Metamodels for a particular project. However, the Factory
 * provides a create method for each non-abstract class of the model.
 * </p>
 * <p>
 * To acquire an instance of the Factory, use:
 * </p>
 * <p>
 * <code>ModuleCoreFactory moduleCoreFactory = ModuleCorePackage.eINSTANCE.getModuleCoreFactory();</code>
 * </p>
 * <!-- end-user-doc -->
 * 
 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage
 * @generated
 */
public interface ModuleCoreFactory extends EFactory {
	/**
	 * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	ModuleCoreFactory eINSTANCE = new org.eclipse.wst.common.modulecore.internal.impl.ModuleCoreFactoryImpl();

	/**
	 * Returns a new object of class '<em>Workbench Module</em>'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @return a new object of class '<em>Workbench Module</em>'.
	 * @generated
	 */
	WorkbenchModule createWorkbenchModule();

	/**
	 * Returns a new object of class '<em>Workbench Module Resource</em>'. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Workbench Module Resource</em>'.
	 * @generated
	 */
	WorkbenchModuleResource createWorkbenchModuleResource();

	/**
	 * Returns a new object of class '<em>Module Type</em>'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @return a new object of class '<em>Module Type</em>'.
	 * @generated
	 */
	ModuleType createModuleType();

	/**
	 * Returns a new object of class '<em>Project Modules</em>'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @return a new object of class '<em>Project Modules</em>'.
	 * @generated
	 */
	ProjectModules createProjectModules();

	/**
	 * Returns a new object of class '<em>Dependent Module</em>'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @return a new object of class '<em>Dependent Module</em>'.
	 * @generated
	 */
	DependentModule createDependentModule();

	/**
	 * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the package supported by this factory.
	 * @generated
	 */
	ModuleCorePackage getModuleCorePackage();

} //ModuleCoreFactory
