/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.util;

public interface WTPModulesXmlMapperI {
	String PROJECT_MODULES = "project-modules";//$NON-NLS-1$  
	String WORKBENCH_COMPONENT = "wb-module";//$NON-NLS-1$
	String REFERENCED_COMPONENT = "dependent-module";//$NON-NLS-1$
	String COMPONENT_RESOURCE = "wb-resource"; //$NON-NLS-1$
	String MODULE_TYPE = "module-type";//$NON-NLS-1$ 
	String META_RESOURCES = "meta-resources";//$NON-NLS-1$ 
	String COMPONENT_TYPE_VERSION = "version";//$NON-NLS-1$ 
	String HANDLE = "handle";//$NON-NLS-1$
	String DEP_OBJECT = "dependent-object";//$NON-NLS-1$ 
	String OBJECTREF = "href";//$NON-NLS-1$
	String DEPENDENCY_TYPE = "dependency-type";//$NON-NLS-1$
	String SOURCE_PATH = "source-path"; //$NON-NLS-1$
	String RUNTIME_PATH = "deploy-path"; //$NON-NLS-1$
	String EXCLUSIONS = "exclusions";//$NON-NLS-1$  
	String COMPONENT_TYPE_ID = "module-type-id"; //$NON-NLS-1$
	String RUNTIME_NAME = "deploy-name"; //$NON-NLS-1$
	String PROPERTY = "property"; //$NON-NLS-1$
	String PROPERTY_NAME = "name";//$NON-NLS-1$
	String PROPERTY_VALUE = "value";//$NON-NLS-1$
	String RESOURCE_TYPE = "resource-type";//$NON-NLS-1$
	String ARCHIVE_NAME="archiveName";//$NON-NLS-1$
	String PROJECT_VERSION="project-version"; //$NON-NLS-1$
	
}
