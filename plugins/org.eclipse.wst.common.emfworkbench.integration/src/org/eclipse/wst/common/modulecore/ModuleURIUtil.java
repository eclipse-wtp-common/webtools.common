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
package org.eclipse.wst.common.modulecore;

import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.modulecore.impl.UnresolveableURIException;
import org.eclipse.wst.common.modulecore.util.ModuleCore;

/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public class ModuleURIUtil {

	public static String getDeployedName(URI aModuleURI) throws UnresolveableURIException{
		ensureValidFullyQualifiedModuleURI(aModuleURI);
		return aModuleURI.segment(ModuleCore.Constants.ModuleURISegments.MODULE_NAME);
	}
	
	public static void ensureValidFullyQualifiedModuleURI(URI aModuleURI) throws UnresolveableURIException {
		if (aModuleURI.segmentCount() < 3)
			throw new UnresolveableURIException(aModuleURI);
	}
	
	public static URI trimModuleResourcePathToModuleURI(URI aModuleResourcePath) throws UnresolveableURIException {
		ensureValidFullyQualifiedModuleURI(aModuleResourcePath);
		return aModuleResourcePath.trimSegments(aModuleResourcePath.segmentCount() - 3);
	}
}
