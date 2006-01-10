package org.eclipse.wst.common.core.search.scope;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

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
