/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.datamodel;

/**
 * <p>
 * IDataModelProperties provides the base interface for all Data Model
 * Properties interfaces. Sub interface classes (e.g.
 * <code>some.company.IFooDataModelProperties.class</code>) should be used to
 * create instances of IDataModels using the
 * {@link DataModelFactory#createDataModel(Class)} method. In order for this
 * mechanism to work, the backing {@link IDataModelProvider} class must be
 * registerd to support the sub IDataModelProperties interface in one of two
 * ways. This registration should be performed by the model developer.
 * </p>
 * <p>
 * The first way to register the correct {@link IDataModelProvider} class is for
 * the interface to define a public static Class _provider_class field
 * which is set to the {@link IDataModelProvider} class, e.g.
 * </p>
 * <p>
 * <code>public static final Class _provider_class = some.company.FooDataModelProvider.class</code>
 * </p>
 * The above mechanism is recommended whenever the {@link IDataModelProperties} and
 * {@link IDataModelProvider} classes are both defined in the same plugin scope.
 * A second mechanism using the DataModelProviderExtension extension point is
 * available when the {@link IDataModelProvider} class is definend outside the
 * {@link IDataModelProperties} plugin scope.
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider
 * @see org.eclipse.wst.common.frameworks.datamodel.DataModelFactory
 * 
 * @since 1.0
 */
public interface IDataModelProperties {

	/**
	 * A boolean property defaults to Boolean.TRUE. If this is set to Boolean.FALSE no extended
	 * operations will be executed.
	 */
	public static final String ALLOW_EXTENSIONS = "IDataModelProperties.ALLOW_EXTENSIONS"; //$NON-NLS-1$
	
	/**
	 * A List containing String objects, defautls to an empty List. If this list contains elements
	 * and ALLOW_EXTENSIONS is set to Boolean.TRUE, then only extended operations not identified in
	 * this list will be executed. These strings should either be the operation id or the fully
	 * qualified operation class name.
	 */
	public static final String RESTRICT_EXTENSIONS = "IDataModelProperties.RESTRICT_EXTENSIONS"; //$NON-NLS-1$
	
}
