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
package org.eclipse.wst.common.frameworks.modulecore.tests;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

public class TestWorkspace {

	public static final String PROJECT_NAME = "TestVirtualAPI"; //$NON-NLS-1$
	public static final String WEB_MODULE_1_NAME = "WebModule1.war"; //$NON-NLS-1$
	public static final String WEB_MODULE_2_NAME = "WebModule2.war"; //$NON-NLS-1$

	public static IProject getTargetProject() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
	}
}
