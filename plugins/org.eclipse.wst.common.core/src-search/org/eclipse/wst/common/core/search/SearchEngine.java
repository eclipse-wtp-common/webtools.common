/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.core.search;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.wst.common.core.search.document.SearchDocumentSet;
import org.eclipse.wst.common.core.search.internal.Messages;
import org.eclipse.wst.common.core.search.internal.SearchDocumentSetImpl;
import org.eclipse.wst.common.core.search.pattern.SearchPattern;
import org.eclipse.wst.common.core.search.scope.SearchScope;

/**
 * The {@link SearchEngine} class provides a generic way of searching for information
 * without the need of knowing how or where that information is stored. The results
 * returned by a search could be scattered in a number of files or stored in an index.
 * Examples of the information you can search for include element declarations and
 * references, references between files, and use of qualifiers.
 * <p>
 * The search can be limited to a specified search scope, or the entire workspace can
 * be searched. Search matches are returned to the specified {@link SearchRequestor}.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * 
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still under development and expected to
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
 * (repeatedly) as the API evolves.
 * </p>
 */
public class SearchEngine implements ISearchOptions
{

	/**
	 * Searches for matches of a given search pattern using a specified set of search
	 * participants and search scope. Search patterns can be created using factory
	 * methods and encapsulate the description of the information to be searched for
	 * (for example, element declarations of a specified type, in a case sensitive
	 * manner).
	 * @param pattern
	 *            The pattern describing the information to search for
	 * @param requestor
	 *            Callback object to notify with the results of the search (each match
	 *            is reported to {@link SearchRequestor#acceptSearchMatch(SearchMatch)})
	 * @param participants
	 *            The search participants that will conduct the search
	 * @param scope
	 *            Optional search scope to limit the source of search candidates;
	 *            specify <code>null</code> to search the entire workspace
	 * @param searchOptions
	 *            Optional map of options and values defining behavior of the search;
	 *            some options and values are provided by {@link ISearchOptions}
	 * @param monitor
	 *            Optional progress monitor used to report work completed
	 * @exception CoreException
	 *            if the search fails
	 */
	public void search(SearchPattern pattern, SearchRequestor requestor,
			SearchParticipant[] participants, SearchScope scope, Map searchOptions,
			IProgressMonitor monitor) throws CoreException
	{

		if (monitor != null && monitor.isCanceled())
			throw new OperationCanceledException();

		/* initialize progress monitor */
		if (monitor != null)
			monitor.beginTask(Messages.engine_searching, 100);

        SearchDocumentSet set = new SearchDocumentSetImpl();
		try
		{
			// requestor.beginReporting();
            SearchScope[] scopeArray = new SearchScope[participants.length];
			for (int i = 0, l = participants == null ? 0 : participants.length; i < l; i++)
			{
				if (monitor != null && monitor.isCanceled())
					throw new OperationCanceledException();

				SearchParticipant participant = participants[i];
				SubProgressMonitor subMonitor = monitor == null ? null
						: new SubProgressMonitor(monitor, 1000);
				if (subMonitor != null)
					subMonitor.beginTask("", 1000); //$NON-NLS-1$
				try
				{
					if (subMonitor != null)
						subMonitor.subTask(Messages.bind(
								Messages.engine_searching_locatingDocuments,
								new String[]
								{ participant.getDescription() }));
					participant.beginSearching(pattern, searchOptions);
					// requestor.enterParticipant(participant);
					// participant creates it's own search scope 
					SearchScope newScope =
						participant.selectDocumentLocations(pattern, scope, searchOptions, monitor);
                    scopeArray[i] = newScope;
					// participant creates search documents based on it's search scope
					participant.createSearchDocuments(set, pattern, newScope, searchOptions, subMonitor);
                }
                catch(Exception e)
                {                  
                }
			}
            for (int i = 0, l = participants == null ? 0 : participants.length; i < l; i++)
            {        
                if (monitor != null && monitor.isCanceled())                                  
                    throw new OperationCanceledException();
                
                SearchParticipant participant = participants[i];
                SubProgressMonitor subMonitor = monitor == null ? null
                    : new SubProgressMonitor(monitor, 1000);                     
                if (subMonitor != null && subMonitor.isCanceled())
                    throw new OperationCanceledException();
                try
                {
                // locate index matches if any (note that all search matches
                // could have been issued during index querying)
                if (subMonitor != null)
                    subMonitor.subTask(Messages.bind(
                            Messages.engine_searching_matching,
                            new String[]
                            { participant.getDescription() }));
                // a search document set should contain enough info to reduce the search scope even further 
                // before finding precize locations
                participant.locateMatches(set, pattern, scopeArray[i], requestor, searchOptions, subMonitor);
                }
                finally
                {
                  // requestor.exitParticipant(participant);
                  participant.doneSearching(pattern, searchOptions);
                }         
            }    
		} finally
		{
            set.dispose();
			// requestor.endReporting();
			if (monitor != null)
				monitor.done();
		}
	}

	/**
	 * Searches for matches of a given search pattern. Search patterns can be created
	 * using factory methods and encapsulate the description of the information to be
	 * searched for (for example, element declarations of a specified type, in a case
	 * sensitive manner).
	 * @param pattern
	 *            The pattern describing the information to search for
	 * @param requestor
	 *            Callback object to notify with the results of the search (each match
	 *            is reported to {@link SearchRequestor#acceptSearchMatch(SearchMatch)})
	 * @param scope
	 *            Optional search scope to limit the source of search candidates;
	 *            specify <code>null</code> to search the entire workspace
	 * @param searchOptions
	 *            Optional map of options and values defining behavior of the search;
	 *            some options and values are provided by {@link ISearchOptions}
	 * @param monitor
	 *            Optional progress monitor used to report work completed
	 * @exception CoreException
	 *            if the search fails
	 */
	public void search(SearchPattern pattern, SearchRequestor requestor,
			SearchScope scope, Map searchOptions, IProgressMonitor monitor)
			throws CoreException
	{
		SearchParticipant[] participants =
			getApplicableParticipants(pattern, searchOptions);
        //System.out.println("participants = " + participants.length);
		search(pattern, requestor, participants, scope, searchOptions, monitor);
	}

	/**
	 * Queries the set of participants that support searches described by the
	 * specified search pattern and options.
	 * @param pattern
	 *            The pattern describing the information to search for
	 * @param searchOptions
	 *            Optional map of options and values defining behavior of the search;
	 *            some options and values are provided by {@link ISearchOptions}
	 * @return Array of applicable search participants
	 */
	public SearchParticipant[] getApplicableParticipants(SearchPattern pattern,
			Map searchOptions)
	{
		return SearchPlugin.getDefault().loadSearchParticipants(pattern, searchOptions);
	}

}
