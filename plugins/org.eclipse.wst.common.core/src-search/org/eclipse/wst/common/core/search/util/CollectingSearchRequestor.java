package org.eclipse.wst.common.core.search.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.core.search.SearchMatch;
import org.eclipse.wst.common.core.search.SearchRequestor;

/**
 * Collects the results returned by a <code>ISearchEngine</code>.
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
