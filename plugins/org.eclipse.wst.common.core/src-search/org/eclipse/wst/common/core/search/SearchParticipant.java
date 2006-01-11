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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.core.search.document.SearchDocument;
import org.eclipse.wst.common.core.search.document.SearchDocumentSet;
import org.eclipse.wst.common.core.search.internal.SearchParticipantRegistry;
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
public abstract class SearchParticipant 
{

	protected static final boolean debugPerf = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.common.core/perf")); //$NON-NLS-1$ //$NON-NLS-2$

	protected String id;
	/**
	 * Creates a new search participant.
	 */
	public SearchParticipant()
	{
		// do nothing
	}

	/**
	 * This method is called from the {@link SearchParticipantRegistry} and
	 * allows participant to decide whether to participate or not in the search
	 * requests for the given search pattern and content types.
	 * 
	 * @param pattern
	 * @param contentTypes
	 * @return
	 */
	public boolean initialize(SearchPattern pattern, String[] contentTypes)
	{
		return false;
	}

	/**
	 * Notification that this participant's help is needed in a search.
	 * <p>
	 * This method should be re-implemented in subclasses that need to do
	 * something when the participant is needed in a search.
	 * </p>
	 */
	public void beginSearching(SearchPattern pattern)
	{
		// do nothing
	}

	/**
	 * Notification that this participant's help is no longer needed.
	 * <p>
	 * This method should be re-implemented in subclasses that need to do
	 * something when the participant is no longer needed in a search.
	 * </p>
	 */
	public void doneSearching(SearchPattern pattern)
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
	public abstract SearchDocument getDocument(String documentPath);

	/**
	 * Locates the matches in the given documents. This method should be called
	 * by the other search participant or search client once it has
	 * pre-processed documents and delegating search to this search participant.
	 * 
	 * @param documents
	 *            the documents to locate matches in
	 * @param pattern
	 *            the search pattern to use when locating matches
	 * @param scope
	 *            the scope to limit the search to
	 * @param requestor
	 *            the requestor to report matches to
	 * @param monitor
	 *            the progress monitor to report progress to, or
	 *            <code>null</code> if no progress should be reported
	 * @throws CoreException
	 *             if the requestor had problem accepting one of the matches
	 */
	public abstract void locateMatches(SearchDocumentSet documentSet,
			SearchPattern pattern, SearchScope scope,
			SearchRequestor requestor, IProgressMonitor monitor)
			throws CoreException;

	 // SAX : run the SAX parser and store enough information to support the requirements of the pattern
    //       parser may only store a few entries to satisfy the pattern
    //
    // INDEX : if the INDEX info for the document and pattern is stale, recompute info
    //         usually an index will compute all possibly require info at this time
    //         bit there's no reason why the index can compute just enough for this pattern
    //         and compute the rest when a different pattern arrives
    //
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
	 * 
	 * @param query
	 *            the search pattern to consider
	 * @param scope
	 *            the given search scope
	 * @return the collection of document paths to consider
	 */
	public SearchScope selectDocumentLocations(SearchPattern pattern,
			SearchScope scope, IProgressMonitor monitor)
	{	
		return scope;
	}
	
	public void createSearchDocument(SearchDocumentSet set, SearchPattern pattern, SearchScope scope, IProgressMonitor monitor)
	{
		IFile[] files = scope.enclosingFiles();
		
		for (int i = 0; i < files.length; i++)
		{
			String location = files[i].getLocation().toString();
			SearchDocument document = set.getSearchDocument(location, id);
			if(document == null && id != null){
				set.putSearchDocument(id, document = getDocument(location));
			}
			populateSearchDocument(document, pattern); 
			
		}

	}

	
	
}
