/******************************************************************************
 * Copyright (c) 2009 Red Hat
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rob Stryker - initial implementation and ongoing maintenance
 *    IBM - Ongoing maintenance
 *    
 * API in these packages is provisional in this release
 ******************************************************************************/
package org.eclipse.wst.common.componentcore.ui.propertypage;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;

public interface IReferenceWizardConstants {
	/**
	 * The key representing that this wizard can return a folder mapping
	 * The value should be an instanceof ComponentResourceProxy
	 */
	public static final String FOLDER_MAPPING = "folder.mapping"; //$NON-NLS-1$
	
	/**
	 * This property should map to the IProject which is the root project
	 * which will enclose the returned reference
	 */
	public static final String PROJECT = "root.project"; //$NON-NLS-1$
	
	/**
	 * This property should map to the IVirtualComponent which is the root component
	 * which will enclose the returned reference
	 */
	public static final String ROOT_COMPONENT = "root.component"; //$NON-NLS-1$
	
	/**
	 * This property should map to the IModuleHandler for customized behaviour
	 */
	public static final String MODULEHANDLER = "module.handler"; //$NON-NLS-1$
	
	/**
	 * This property should map to the original reference, if the wizard has been opened
	 * in editing mode. 
	 */
	public static final String ORIGINAL_REFERENCE = "dependency.reference.original";//$NON-NLS-1$
	
	/**
	 * This property should map to the final reference, either new reference, 
	 * or a new instance of a modified reference. 
	 */
	public static final String FINAL_REFERENCE = "dependency.reference.final";//$NON-NLS-1$
	
	/**
	 * This property should map to the default location that libraries should be placed.
	 * If this is unset, the creating or editing wizard fragment can choose to 
	 * set the runtimePath of the reference to whatever it wants.
	 */
	public static final String DEFAULT_LIBRARY_LOCATION = "default.library.location";
	
	/**
	 * This property should map to the default location that generic resources should be placed.
	 * If this is unset, the creating or editing wizard fragment can choose to 
	 * set the runtimePath of the reference to whatever it wants.
	 */
	public static final String DEFAULT_RESOURCE_LOCATION = "default.resource.location";

	/**
	 * This property helps track the current state of the component ref model as each subsequent action is taken, but not committed
	 */
	public static final String CURRENT_REFS = "current.references";
	
	/**
	 * This key should be used when you want to convert a project into 
	 * a specific modulecore project type.
	 * 
	 */
	public static final String PROJECT_CONVERTER_OPERATION_PROVIDER = "project.converter.operation.provider";
	
	/**
	 * An interface to provide an operation for converting a specific project
	 */
	public static interface ProjectConverterOperationProvider {
		public IDataModelOperation getConversionOperation(IProject project);
	}
}
