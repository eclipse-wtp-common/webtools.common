package org.eclipse.wst.common.core.search.scope;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

public class ProjectSearchScope extends SearchScopeImpl
{

	/**
	 * Creates a scope that ecloses workspace path and eclosing project
	 * 
	 * @param workspacePath -
	 *            path to the resource in the workspace, e.g.
	 *            /MyProject/MyFile.xml
	 */
	public ProjectSearchScope(IPath workspacePath)
	{
		super();
		initialize(workspacePath);

	}

	protected void initialize(IPath workspacePath)
	{
		IResource resource = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(workspacePath);
		if (resource != null)
		{
			IProject project = resource.getProject();
			traverseContainer(project);
		}
	}

}
