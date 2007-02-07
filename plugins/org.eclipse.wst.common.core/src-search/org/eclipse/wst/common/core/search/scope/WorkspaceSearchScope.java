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
package org.eclipse.wst.common.core.search.scope;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still under development and expected to
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
 * (repeatedly) as the API evolves.
 * </p>
 */
public class WorkspaceSearchScope extends SearchScopeImpl
{

	protected IResource currentResource;

	/**
	 * Creates a scope that ecloses workspace path and eclosing project
	 * 
	 * @param workspacePath -
	 *            path to the resource in the workspace, e.g.
	 *            /MyProject/MyFile.xml
	 */
	public WorkspaceSearchScope()
	{
		super();
		initialize();

	}

	protected void initialize()
	{
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		traverseContainer(root);

	}

}
