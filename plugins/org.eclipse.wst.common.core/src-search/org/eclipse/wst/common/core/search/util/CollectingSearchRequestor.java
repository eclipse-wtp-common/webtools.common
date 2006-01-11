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
package org.eclipse.wst.common.core.search.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.core.search.SearchMatch;
import org.eclipse.wst.common.core.search.SearchRequestor;

/**
 * Collects the results returned by a <code>ISearchEngine</code>.
 * 
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still under development and expected to
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
 * (repeatedly) as the API evolves.
 * </p>
 */
// issue should you move this to a util package? what does jdt do?
public class CollectingSearchRequestor extends SearchRequestor
{
	private ArrayList fFound;

	public CollectingSearchRequestor()
	{
		fFound = new ArrayList();
	}

	/**
	 * @return a List of {@link SearchMatch}es (not sorted)
	 */
	public List/* <SearchMatch> */getResults()
	{
		return fFound;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.core.search.internal.provisional.SearchRequestor#acceptSearchMatch(org.eclipse.wst.common.search.internal.provisional.SearchMatch)
	 */
	public void acceptSearchMatch(SearchMatch match) throws CoreException
	{
		fFound.add(match);

	}
}
