/*******************************************************************************
 * Copyright (c) 2009, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *     
 * API in these packages is provisional in this release
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.wst.common.componentcore.ui.messages"; //$NON-NLS-1$
	public static String ModuleAssemblyRootPageDescription;
	public static String ModuleAssembly;
	public static String ErrorCheckingFacets;
	public static String ErrorNotVirtualComponent;
	public static String DeployPathColumn;
	public static String SourceColumn;
	public static String InternalLibJarWarning;
	public static String AddFolder;
	public static String AddFolderElipses;
	public static String AddFolderMappings;
	public static String AddEllipsis;
	public static String EditEllipsis;
	public static String RemoveSelected;
	public static String JarTitle;
	public static String JarDescription;
	public static String ExternalJarTitle;
	public static String ExternalJarDescription;
	public static String Browse;
	public static String NewReferenceTitle;
	public static String NewReferenceDescription;
	public static String NewReferenceWizard;
	public static String ProjectReferenceTitle;
	public static String ProjectReferenceDescription;
	public static String VariableReferenceTitle;
	public static String VariableReferenceDescription;
	public static String WizardError;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
