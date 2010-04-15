/******************************************************************************
 * Copyright (c) 2009 Red Hat
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rob Stryker - initial implementation and ongoing maintenance
 ******************************************************************************/
package org.eclipse.wst.common.componentcore.ui.propertypage;

public interface IReferenceWizardConstants {
	/**
	 * The key representing that this wizard can return a folder mapping
	 * The value should be an instanceof ComponentResourceProxy
	 */
	public static final String FOLDER_MAPPING = "folder.mapping"; //$NON-NLS-1$
	public static final String PROJECT = "root.project"; //$NON-NLS-1$
	public static final String ROOT_COMPONENT = "root.component"; //$NON-NLS-1$
	public static final String MODULEHANDLER = "module.handler"; //$NON-NLS-1$
	public static final String ORIGINAL_REFERENCE = "dependency.reference.original";//$NON-NLS-1$
	public static final String FINAL_REFERENCE = "dependency.reference.final";//$NON-NLS-1$
}
