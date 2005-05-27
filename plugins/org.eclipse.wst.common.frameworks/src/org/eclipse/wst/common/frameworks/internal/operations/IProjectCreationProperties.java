/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.operations;

public interface IProjectCreationProperties {

	/**
	 * A required dataModel propertyName for a <code>java.lang.String</code> type. This is used to
	 * specify the project name.
	 */
	//public static final String PROJECT_NAME = "ProjectCreationDataModel.PROJECT_NAME"; //$NON-NLS-1$
	public static final String PROJECT_NAME = "IProjectCreationProperties.PROJECT_NAME"; //$NON-NLS-1$

	/**
	 * An optonal dataModel propertyName for a <code>java.lang.String</code> type. Sets the local
	 * file system location for the described project. The path must be either an absolute file
	 * system path, or a relative path whose first segment is the name of a defined workspace path
	 * variable. The default value is the workspace's default location.
	 */
	//public static final String PROJECT_LOCATION = "ProjectCreationDataModel.PROJECT_LOCATION"; //$NON-NLS-1$
	public static final String PROJECT_LOCATION = "IProjectCreationProperties.PROJECT_LOCATION"; //$NON-NLS-1$


	/**
	 * An optional dataModel propertyName for a <code>java.lang.String[]</code> type. This is a
	 * list of all natures to add to the project. There is no default value.
	 */
	public static final String PROJECT_NATURES = "ProjectCreationDataModel.PROJECT_NATURES"; //$NON-NLS-1$

	/**
	 * IProject. An non settable property. This is a conveniece for constructiong getting the
	 * IProject
	 */
	public static final String PROJECT = IProjectCreationProperties.class.getName() + "PROJECT";

	/**
	 * IProjectDescription. An non settable property. This is a conveniece for constructiong a
	 * project description.
	 */
	public static final String PROJECT_DESCRIPTION = IProjectCreationProperties.class.getName() + "PROJECT_DESCRIPTION";

}
