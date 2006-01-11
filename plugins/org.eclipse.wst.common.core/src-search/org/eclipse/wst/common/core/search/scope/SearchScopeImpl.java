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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

/**
 *
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still under development and expected to
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
 * (repeatedly) as the API evolves.
 * </p>
 */
public class SearchScopeImpl extends SearchScope
{

	protected List files = new ArrayList();

	protected HashSet projects = new HashSet();

	public SearchScopeImpl()
	{
		super();
	}

	/**
	 * Checks whether the resource at the given path is belongs to this scope.
	 * Resource path could be added to the scope, if scope conditions are met.
	 * if {@link ISearchScope.encloses(String resourcePath)} returns false and
	 * then this method is called and returns true, next call to
	 * {@link ISearchScope.eclipses(String resourcePath)} should return true.
	 * 
	 * @param file -
	 *            workspace file
	 * @return whether the resource is enclosed by this scope
	 */
	protected boolean acceptFile(IFile file)
	{
		if (file == null)
		{
			return false;
		}
		files.add(file);
		projects.add(file.getProject());
		return true;

	}

	/**
	 * Checks whether the resource at the given path is enclosed by this scope.
	 * 
	 * @param resourcePath -
	 *            workspace relative resource path
	 * @return whether the resource is enclosed by this scope
	 */
	protected boolean encloses(String resourcePath)
	{
		IResource resource = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(resourcePath);
		if (resource == null)
			return false;
		return (files.contains(resource));
	}

	/**
	 * Returns the path to the workspace files that belong in this search scope.
	 * (see <code>IResource.getFullPath()</code>). For example,
	 * /MyProject/MyFile.txt
	 * 
	 * @return an array of files in the workspace that belong to this scope.
	 */
	public IFile[] enclosingFiles()
	{
		if (files == null)
			return new IFile[0];
		return (IFile[]) files.toArray(new IFile[files.size()]);
	}

	/**
	 * Returns the paths to the enclosing projects for this search scope. (see
	 * <code>IResource.getFullPath()</code>). For example, /MyProject
	 * 
	 * @return an array of paths to the enclosing projects.
	 */
	protected IProject[] enclosingProjects()
	{

		return (IProject[]) projects.toArray(new IProject[projects.size()]);

	}
	
	protected void traverseContainer(IContainer container)
	{

		IResourceVisitor visitor = new IResourceVisitor()
		{
			public boolean visit(IResource resource)
			{
				if (resource.getType() == IResource.FILE)
					acceptFile((IFile) resource);
				return true;
			}
		};
		try
		{
			container.accept(visitor);
		} catch (CoreException e)
		{
			// ignore resource
		}

	}
}
