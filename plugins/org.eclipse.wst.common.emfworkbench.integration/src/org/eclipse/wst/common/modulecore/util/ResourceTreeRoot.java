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
package org.eclipse.wst.common.modulecore.util;

import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.modulecore.ModuleURIUtil;
import org.eclipse.wst.common.modulecore.WorkbenchModule;
import org.eclipse.wst.common.modulecore.WorkbenchModuleResource;
import org.eclipse.wst.common.modulecore.impl.UnresolveableURIException;

/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public class ResourceTreeRoot extends ResourceTreeNode {

	private final WorkbenchModule module;

	public ResourceTreeRoot(WorkbenchModule aModule, IPathProvider aPathProvider) {
		super("/", null, aPathProvider); //$NON-NLS-1$
		module = aModule; 	
		init();
	}

	private void init() {
		List moduleResources = module.getResources();
		WorkbenchModuleResource moduleResource = null;
		for (int i = 0; i < moduleResources.size(); i++) {
			moduleResource = (WorkbenchModuleResource) moduleResources.get(i);
			addChild(moduleResource);
		}
	}

	public WorkbenchModuleResource[] findModuleResources(URI aURI) {
		IPath path = new Path(aURI.toString());
		try {
			if (ModuleURIUtil.ensureValidFullyQualifiedPlatformURI(aURI, false))
				path = path.removeFirstSegments(1); 
		} catch (UnresolveableURIException uurie) {

		}
		return findModuleResources(path, false);
	}

}
