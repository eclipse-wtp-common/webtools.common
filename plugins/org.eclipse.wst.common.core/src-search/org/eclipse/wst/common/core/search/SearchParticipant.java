/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.core.search;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.core.search.document.SearchDocument;
import org.eclipse.wst.common.core.search.document.SearchDocumentSet;
import org.eclipse.wst.common.core.search.pattern.SearchPattern;
import org.eclipse.wst.common.core.search.scope.SearchScope;

/**
 * <p>
 * A search participant describes a particular extension to a generic search
 * mechanism, permitting combined search actions which will involve all required
 * participants.
 * </p>
 * <p>
 * When there is a need to search a document that has some mixed content, then
 * using a one participant will not be enough. E.g. for the searching of the JSP
 * content, a JSP participant needs to create {@link SearchDocument} that
 * contain Java compilation unit and then pass it to the default Java search
 * participant of the Java Search Engine. The same with XML: when there is an
 * XML content that default search participant could not process, but it is
 * required to be search on, a new search participant needs to be defined. That
 * search participant would know how create search document from the content and
 * then it can call default XML search participant.
 * </p>
 * <p>
 * Passing its own {@link SearchRequestor} this participant can then map the
 * match positions back to the original contents, create its own matches and
 * report them to the original requestor.
 * </p>
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still under development and expected to
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
 * (repeatedly) as the API evolves.
 * </p>
 * 
 */
public abstract class SearchParticipant implements ISearchOptions 
{

	protected static final boolean debugPerf = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.common.core/perf")); //$NON-NLS-1$ //$NON-NLS-2$

	protected String id;
	/**
	 * Creates a new search participant.
	 */
	protected SearchParticipant()
	{
		// do nothing
	}

	/**
	 * Callback method allowing a search participant to indicate whether it supports
	 * the specified search pattern and options.
	 * <p>
	 * If this method returns <code>true</code>, the participant will participate in
	 * finding matches for the search defined by the search pattern. In this case, the
	 * search pattern and options will be passed to the following methods of the
	 * search participant:
	 * <ol>
	 * <li>{@link #beginSearching(SearchPattern, Map)}
	 * <li>{@link #selectDocumentLocations(SearchPattern, SearchScope, Map, IProgressMonitor)}
	 * <li>{@link #createSearchDocuments(SearchDocumentSet, SearchPattern, SearchScope, Map, IProgressMonitor)}
	 * <li>{@link #locateMatches(SearchDocumentSet, SearchPattern, SearchScope, SearchRequestor, Map, IProgressMonitor)}
	 * <li>{@link #doneSearching(SearchPattern, Map)}
	 * </ol>
	 * <p>
	 * If this method returns <code>false</code>, none of the above methods will be
	 * called, and the search participant contributes no matches for the search
	 * requrest.
	 * <p>
	 * A search participant should only participate in a search when in understands
	 * the search pattern. Unrecognized search options, however, can be ignored.
	 * <p>
	 * This method returns <code>false</code> by default. As a result, subclasses must
	 * override this method to participate in any searches.
	 * @param pattern The pattern describing the information to search for
	 * @param searchOptions Map of options and values defining behavior of the search;
	 *         <code>null</code> if no options are specified;
	 *         some options and values are provided by {@link ISearchOptions}
	 * @return <code>true</code> if the search participant will participate in the
	 *         search request; <code>false</code> otherwise
	 */
	 public abstract boolean isApplicable(SearchPattern pattern, Map searchOptions);

	/**
	 * Notification that this participant's help is needed in a search.
	 * <p>
	 * This method should be re-implemented in subclasses that need to do
	 * something when the participant is needed in a search.
	 * </p>
	 * @param pattern The pattern describing the information to search for
	 * @param searchOptions Map of options and values defining behavior of the search;
	 *         <code>null</code> if no options are specified;
	 *         some options and values are provided by {@link ISearchOptions}
	 */
	public void beginSearching(SearchPattern pattern, Map searchOptions)
	{
		// do nothing
	}

	/**
	 * Notification that this participant's help is no longer needed.
	 * <p>
	 * This method should be re-implemented in subclasses that need to do
	 * something when the participant is no longer needed in a search.
	 * </p>
	 * @param pattern
	 *            The pattern describing the information to search for
	 * @param searchOptions
	 *            Map of options and values defining behavior of the search;
	 *            <code>null</code> if no options are specified;
	 *            some options and values are provided by {@link ISearchOptions}
	 */
	public void doneSearching(SearchPattern pattern, Map searchOptions)
	{
		// do nothing
	}
	
	/**
	 * Returns a displayable name of this search participant.
	 * <p>
	 * This method should be re-implemented in subclasses that need to display a
	 * meaningfull name.
	 * </p>
	 * 
	 * @return the displayable name of this search participant
	 */
	public String getDescription()
	{
		return "Search participant"; //$NON-NLS-1$
	}

	/**
	 * Returns a search document for the given path. The given document path is
	 * a string that uniquely identifies the document. Most of the time it is a
	 * workspace-relative path, but it can also be a file system path, or a path
	 * inside a zip file.
	 * <p>
	 * Implementors of this method can either create an instance of their own
	 * subclass of {@link SearchDocument} or return an existing instance of such
	 * a subclass.
	 * </p>
	 * 
	 * @param documentPath
	 *            the path of the document.
	 * @return a search document
	 */
	public abstract SearchDocument createSearchDocument(String documentPath);

	/**
	 * Locates the matches in the given documents. This method should be called
	 * by the other search participant or search client once it has
	 * pre-processed documents and delegating search to this search participant.
	 * @param documentSet
	 *            The documents to locate matches in
	 * @param pattern
	 *            The pattern describing the information to search for
	 * @param scope
	 *            Search scope to limit the source of search candidates;
	 *            <code>null</code> indicates that the entire workspace is to be
	 *            searched
	 * @param requestor
	 *            Callback object to notify with the results of the search (each match
	 *            is reported to {@link SearchRequestor#acceptSearchMatch(SearchMatch)})
	 * @param searchOptions
	 *            Map of options and values defining behavior of the search;
	 *            <code>null</code> if no options are specified;
	 *            some options and values are provided by {@link ISearchOptions}
	 * @param monitor
	 *            Progress monitor used to report work completed; <code>null</code>
	 *            if no progress needs to be reported
	 * @throws CoreException
	 *            If the requestor had problem accepting one of the matches
	 */
	public abstract void locateMatches(SearchDocumentSet documentSet,
			SearchPattern pattern, SearchScope scope,
			SearchRequestor requestor, Map searchOptions, IProgressMonitor monitor)
			throws CoreException;

	/**
	 * 
	 * @param document
	 * @param pattern
	 */
	public abstract void populateSearchDocument(SearchDocument document,
			SearchPattern pattern);

	/**
	 * Returns the collection of document locations to consider when performing
	 * the given search query in the given scope. The search engine calls this
	 * method before locating matches.
	 * <p>
	 * An document location represents a path workspace to the file with the
	 * content that has potential matches
	 * </p>
	 * <p>
	 * Clients are not expected to call this method.
	 * </p>
	 * @param pattern
	 *            The pattern describing the information to search for
	 * @param scope
	 *            Search scope to limit the source of search candidates;
	 *            <code>null</code> indicates that the entire workspace is to be
	 *            searched
	 * @param searchOptions
	 *            Map of options and values defining behavior of the search;
	 *            <code>null</code> if no options are specified;
	 *            some options and values are provided by {@link ISearchOptions}
	 * @param monitor
	 *            Progress monitor used to report work completed; <code>null</code>
	 *            if no progress needs to be reported
	 * @return the collection of document paths to consider
	 */
	public SearchScope selectDocumentLocations(SearchPattern pattern,
			SearchScope scope, Map searchOptions, IProgressMonitor monitor)
	{	
		return scope;
	}

	/**
	 * <p>
	 * This method calls the following methods for each file in the search scope:
	 * <ol>
	 * <li>{@link #createSearchDocument(String)}
	 * <li>{@link #populateSearchDocument(SearchDocument, SearchPattern)}
	 * </ol>
	 * @param documentSet
	 *            The documents to locate matches in
	 * @param pattern
	 *            The pattern describing the information to search for
	 * @param scope
	 *            Search scope to limit the source of search candidates;
	 *            <code>null</code> indicates that the entire workspace is to be
	 *            searched
	 * @param searchOptions
	 *            Map of options and values defining behavior of the search;
	 *            <code>null</code> if no options are specified;
	 *            some options and values are provided by {@link ISearchOptions}
	 * @param monitor
	 *            Progress monitor used to report work completed; <code>null</code>
	 *            if no progress needs to be reported
	 */
	public void createSearchDocuments(SearchDocumentSet documentSet,
			SearchPattern pattern, SearchScope scope, Map searchOptions,
			IProgressMonitor monitor)
	{
		Assert.isNotNull(id, "The SearchPartipants id has not been initalized");
		IFile[] files = scope.enclosingFiles();
		
		for (int i = 0; i < files.length; i++)
		{
			String location = files[i].getLocation().toString();
			SearchDocument document = documentSet.getSearchDocument(location, id);
			if(document == null && id != null){
				documentSet.putSearchDocument(id, document = createSearchDocument(location));
			}
			populateSearchDocument(document, pattern); 
			
		}

	}	
}
