/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/ 
package org.eclipse.wst.common.frameworks.componentcore.tests;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

public class TestWorkspace {

	public static final String PROJECT_NAME = "TestVirtualAPI"; //$NON-NLS-1$
	public static final String WEB_MODULE_1_NAME = "WebModule1.war"; //$NON-NLS-1$
	public static final String WEB_MODULE_2_NAME = "WebModule2.war"; //$NON-NLS-1$
	
	public static final String NEW_WEB_MODULE_NAME = "NewWebModule.war"; //$NON-NLS-1$
	

	public static final String META_INF = "META-INF"; //$NON-NLS-1$
	public static final String WEB_INF = "WEB-INF"; //$NON-NLS-1$

	public static IProject getTargetProject() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
	}

	public static void init() {
		
		try {
			getTargetProject().refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) { 
			e.printStackTrace();
		}
	}
}
