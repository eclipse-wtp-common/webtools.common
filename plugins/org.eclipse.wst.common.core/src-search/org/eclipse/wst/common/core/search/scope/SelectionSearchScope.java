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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;

/**
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still under development and expected to
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
 * (repeatedly) as the API evolves.
 * </p>
 */
public class SelectionSearchScope extends SearchScopeImpl
{

	protected IResource[] resources;

	/**
	 * Creates a scope that ecloses workspace path and eclosing project
	 * 
	 * @param workspacePath -
	 *            path to the resource in the workspace, e.g.
	 *            /MyProject/MyFile.xml
	 */
	public SelectionSearchScope(IResource[] resources)
	{
		super();
		this.resources = resources;
		initialize();

	}

	protected void initialize()
	{
		if (resources == null)
			return;
		for (int index = 0; index < resources.length; index++)
		{
			IResource resource = resources[index];
			if (resource != null)
			{
				if (resource.getType() == IResource.FOLDER)
				{
					traverseContainer((IFolder) resource);
				} else if (resource.getType() == IResource.FILE)
				{
					acceptFile((IFile) resource);
				}
			}

		}

	}

}
