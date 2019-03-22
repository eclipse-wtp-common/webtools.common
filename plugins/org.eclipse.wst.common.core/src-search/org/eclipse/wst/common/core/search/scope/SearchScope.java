/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.core.search.scope;

import org.eclipse.core.resources.IFile;

/**
 * A <code>SearchScope</code> defines where search result should be found by a
 * <code>SearchEngine</code> (e.g. project, workspace).
 * 
 * Clients must pass an instance of this class to the <code>search(...)</code>
 * methods. Such an instance can be created using the following factory methods
 * on <code>SearchScope</code>: <code>newSearchScope(IResource[])</code>,
 * <code>newWorkspaceScope()</code>
 * 
 * The default implementaion of the search scope has no filter, and at creation
 * does not contain any files, It could accept any workspace file.
 * 
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still under development and expected to
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
 * (repeatedly) as the API evolves.
 * </p>
 */
public abstract class SearchScope
{
	/**
	 * Returns the path to the workspace files that belong in this search scope.
	 * (see <code>IResource.getFullPath()</code>). For example,
	 * /MyProject/MyFile.txt
	 * 
	 * @return an array of files in the workspace that belong to this scope.
	 */
	public abstract IFile[] enclosingFiles();



	

}
