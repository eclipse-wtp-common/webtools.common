/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.operations;

public interface IProjectCreationPropertiesNew {

	/**
	 * A required dataModel propertyName for a <code>java.lang.String</code> type. This is used to
	 * specify the project name.
	 */
	public static final String PROJECT_NAME = "IProjectCreationPropertiesNew.PROJECT_NAME"; //$NON-NLS-1$

	/**
	 * A Boolean property used for determining how the PROJECT_LOCATION is computed. If this
	 * property is true then the PROJECT_LOCATION is null. Otherwise the PROJECT_LOCATION is the
	 * value of USER_DEFINED_LOCATION.
	 */
	public static final String USE_DEFAULT_LOCATION = "IProjectCreationPropertiesNew.USE_DEFAULT_LOCATION"; //$NON-NLS-1$

	/**
	 * A String property used in conjuction with USE_DEFAULT_LOCATION to override the
	 * DEFAULT_LOCATION.
	 */
	public static final String USER_DEFINED_LOCATION = "IProjectCreationPropertiesNew.USER_DEFINED_LOCATION"; //$NON-NLS-1$

	/**
	 * A String property used in conjunction with {@link #USE_DEFAULT_LOCATION} and
	 * {@link #PROJECT_NAME} to overrid the {@link #PROJECT_LOCATION}.
	 * 
	 * If {@link #USE_DEFAULT_LOCATION} is <code>false</code> and this property is set, then the
	 * {@link #PROJECT_LOCATION} is defined by {@link #USER_DEFINED_BASE_LOCATION}/{@link #PROJECT_NAME}.
	 */
	public static final String USER_DEFINED_BASE_LOCATION = "IProjectCreationPropertiesNew.USER_DEFINED_BASE_LOCATION"; //$NON-NLS-1$

	/**
	 * An unsettable property which specified the default location for a newly created project. The
	 * value is computed by appending the project name to the workspace location.
	 */
	public static final String DEFAULT_LOCATION = "IProjectCreationPropertiesNew.DEFAULT_LOCATION"; //$NON-NLS-1$

	/**
	 * An unsettable property used to specify the project location. If USE_DEFAULT_LOCATION this
	 * property evaluates to USER_DEFINED_LOCATION; otherwise it i <code>null</code>.
	 */
	public static final String PROJECT_LOCATION = "IProjectCreationPropertiesNew.PROJECT_LOCATION"; //$NON-NLS-1$

	/**
	 * An optional dataModel propertyName for a <code>java.lang.String[]</code> type. This is a
	 * list of all natures to add to the project. There is no default value.
	 */
	public static final String PROJECT_NATURES = "IProjectCreationPropertiesNew.PROJECT_NATURES"; //$NON-NLS-1$

	/**
	 * IProject. An non settable property. This is a conveniece for constructiong getting the
	 * IProject
	 */
	public static final String PROJECT = "IProjectCreationPropertiesNew.PROJECT"; //$NON-NLS-1$

	/**
	 * IProjectDescription. An non settable property. This is a conveniece for constructiong a
	 * project description.
	 */
	public static final String PROJECT_DESCRIPTION = "IProjectCreationPropertiesNew.PROJECT_DESCRIPTION"; //$NON-NLS-1$

}
