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
package org.eclipse.wst.common.modulecore.internal.impl;

import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.wst.common.modulecore.UnresolveableURIException;
import org.eclipse.wst.common.modulecore.WorkbenchComponent;
import org.eclipse.wst.common.modulecore.ComponentResource;
import org.eclipse.wst.common.modulecore.internal.util.IPathProvider;

/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public class ResourceTreeRoot extends ResourceTreeNode {

	
	public static ResourceTreeRoot getSourceResourceTreeRoot(WorkbenchComponent aModule) {
		ResourceTreeRootAdapter resourceTreeAdapter = (ResourceTreeRootAdapter) EcoreUtil.getAdapter(aModule.eAdapters(), ResourceTreeRootAdapter.SOURCE_ADAPTER_TYPE);
		if (resourceTreeAdapter != null)
			return resourceTreeAdapter.getResourceTreeRoot();
		resourceTreeAdapter = new ResourceTreeRootAdapter(ResourceTreeRootAdapter.SOURCE_TREE);
		aModule.eAdapters().add(resourceTreeAdapter);
		return resourceTreeAdapter.getResourceTreeRoot();
	}

	public static ResourceTreeRoot getDeployResourceTreeRoot(WorkbenchComponent aModule) {
		ResourceTreeRootAdapter resourceTreeAdapter = (ResourceTreeRootAdapter) EcoreUtil.getAdapter(aModule.eAdapters(), ResourceTreeRootAdapter.DEPLOY_ADAPTER_TYPE);
		if (resourceTreeAdapter != null)
			return resourceTreeAdapter.getResourceTreeRoot();
		resourceTreeAdapter = new ResourceTreeRootAdapter(ResourceTreeRootAdapter.DEPLOY_TREE);
		aModule.eAdapters().add(resourceTreeAdapter);
		return resourceTreeAdapter.getResourceTreeRoot();
	}
	
	private final WorkbenchComponent module;

	public ResourceTreeRoot(WorkbenchComponent aModule, IPathProvider aPathProvider) {
		super("/", null, aPathProvider); //$NON-NLS-1$
		module = aModule; 	
		init();
	}

	private void init() {
		List moduleResources = module.getResources();
		ComponentResource moduleResource = null;
		for (int i = 0; i < moduleResources.size(); i++) {
			moduleResource = (ComponentResource) moduleResources.get(i);
			addChild(moduleResource);
		}
	}

	public ComponentResource[] findModuleResources(URI aURI) {
		IPath path = new Path(aURI.toString());
		try {
			if (ModuleURIUtil.ensureValidFullyQualifiedPlatformURI(aURI, false))
				path = path.removeFirstSegments(1); 
		} catch (UnresolveableURIException uurie) {

		}
		return findModuleResources(path, false);
	}

}
