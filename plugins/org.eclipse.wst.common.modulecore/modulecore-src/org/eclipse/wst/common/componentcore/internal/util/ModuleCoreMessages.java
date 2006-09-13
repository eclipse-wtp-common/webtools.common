/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.util;

import org.eclipse.osgi.util.NLS;

public class ModuleCoreMessages extends NLS {

	private static final String BUNDLE_NAME = "modulecoreNLS"; //$NON-NLS-1$
	
	private ModuleCoreMessages() {
		//do not instantiate
	}
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, ModuleCoreMessages.class);
	}
	
	public static String Acquiring_ArtifactEdit_For_Read_Exception;
	public static String Acquiring_ArtifactEdit_For_Write_Exception;
}
