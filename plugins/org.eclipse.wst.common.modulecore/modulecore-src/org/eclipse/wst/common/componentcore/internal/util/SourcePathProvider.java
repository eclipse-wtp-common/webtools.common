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
package org.eclipse.wst.common.componentcore.internal.util;

import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.common.componentcore.internal.ComponentResource;

/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public class SourcePathProvider implements IPathProvider {
	
	public static IPathProvider INSTANCE = new SourcePathProvider();
	
	private SourcePathProvider() {} 

	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.modulecore.util.IPathProvider#getPath(org.eclipse.wst.common.modulecore.WorkbenchComponent)
	 */
	public IPath getPath(ComponentResource aModuleResource) {
		return aModuleResource.getSourcePath();
	}

}
