package org.eclipse.wst.common.core.search.scope;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;

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
