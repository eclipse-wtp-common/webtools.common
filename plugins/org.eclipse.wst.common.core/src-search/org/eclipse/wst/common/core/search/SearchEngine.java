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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.wst.common.core.search.document.SearchDocumentSet;
import org.eclipse.wst.common.core.search.internal.Messages;
import org.eclipse.wst.common.core.search.internal.SearchDocumentSetImpl;
import org.eclipse.wst.common.core.search.pattern.SearchPattern;
import org.eclipse.wst.common.core.search.scope.SearchScope;
import org.eclipse.wst.common.core.search.util.CollectingSearchRequestor;

/**
 * A {@link SearchEngine} searches for the file references, component
 * declarations and references, provided they have a quialified name and a
 * component description. The search can be limited to a search scope. By
 * default, whole workspace is searched.
 * 
 * The search engine also provides a generic way of accessing the search
 * function.
 * 
 * {@link SearchRequestor} is expected to be passed in when performing searches
 * and a client can use {@link CollectingSearchRequestor} to access the results
 * of the search.
 * 
 * This class may be instantiated; it is not intended to be subclassed.
 * 
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still under development and expected to
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
 * (repeatedly) as the API evolves.
 * </p>
 */
public class SearchEngine
{

	/**
	 * Searches for matches of a given search pattern using set participant and
	 * search scope. Search patterns can be created using factory methods and
	 * encapsulate the description of what is being searched (for example,
	 * search type declarations in a case sensitive way).
	 * 
	 * @param pattern
	 *            the pattern to search
	 * @param requestor
	 *            the requestor to report the matches to
	 * @param monitor
	 *            the progress monitor used to report progress
	 * @exception CoreException
	 *                if the search failed.
	 */
	public void search(SearchPattern pattern, SearchRequestor requestor,
			SearchParticipant[] participants, SearchScope scope,
			IProgressMonitor monitor) throws CoreException
	{

		if (monitor != null && monitor.isCanceled())
			throw new OperationCanceledException();

		/* initialize progress monitor */
		if (monitor != null)
			monitor.beginTask(Messages.engine_searching, 100);

		try
		{
			// requestor.beginReporting();
            SearchDocumentSet set = new SearchDocumentSetImpl();
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
					participant.beginSearching(pattern);
					// requestor.enterParticipant(participant);
					// participant creates it's own search scope 
					SearchScope newScope = participant.selectDocumentLocations(pattern, scope, monitor);
                    scopeArray[i] = newScope;
					// participant creates search documents based on it's search scope
					participant.createSearchDocument(set, pattern, newScope, subMonitor);
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
                participant.locateMatches(set, pattern, scopeArray[i], requestor, subMonitor);
                }
                finally
                {
                  // requestor.exitParticipant(participant);
                  participant.doneSearching(pattern);
                }                
            }    
		} finally
		{
			// requestor.endReporting();
			if (monitor != null)
				monitor.done();
		}
	}

	public void search(SearchPattern pattern, SearchRequestor requestor,
			SearchScope scope, IProgressMonitor monitor) throws CoreException
	{
		SearchParticipant[] participants = getApplicableParticipants(pattern);
		search(pattern, requestor, participants, scope, monitor);
	}

	public SearchParticipant[] getApplicableParticipants(SearchPattern pattern)
	{
		return SearchPlugin.getDefault().loadSearchParticipants(pattern);
	}

}
