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

package org.eclipse.wst.common.core.search.document;

import org.eclipse.wst.common.core.search.SearchParticipant;
import org.eclipse.wst.common.core.search.SearchRequestor;

/**
 * A search document encapsulates a content to be searched in. A search
 * participant creates a search document based on the file locations to locate
 * matches.
 * <p>
 * This class is intended to be subclassed by clients.
 * </p>
 * issue (cs/eb) does a search participant always create a SearchDocument?
 * 
 * <p>
 * SearchParticipant or search client create search documents for the search
 * pass where precise locations of the matches will be determined by calling
 * {@link SearchParticipan#locateMatches}
 * </p>
 * <p>
 * SearchParticipant knows how to create search document that it can process.
 * </p>
 * <p>
 * The intent of the separation of the {@link SearchDocument} from the
 * {@link SearchParticipant} is to enable the other search participants to
 * process parts of the document. For example, if XML document has a fragment
 * that it can not process, e.g. Java, then XML participant would create
 * SearchDocument for that fragment which contains Java compilation unit, and
 * pass it to the Java search participant. Passing its own
 * {@link SearchRequestor} this participant can then map the match positions
 * back to the original contents, create its own matches and report them to the
 * original requestor.
 * </p>
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still under development and expected to
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
 * (repeatedly) as the API evolves.
 * </p>
 * 
 * @see SearchParticipant
 */
public abstract class SearchDocument
{

	private String documentPath;
	private SearchParticipant participant; 

	/**
	 * Creates a new search document. The given document path is a string that
	 * uniquely identifies the document. Most of the time it is a
	 * workspace-relative path, but it can also be a file system path, or a path
	 * inside a zip file.
	 * 
	 * @param documentPath
	 *            the path to the document, or <code>null</code> if none
	 */
	protected SearchDocument(String documentPath, SearchParticipant participant)
	{
		this.documentPath = documentPath;
		this.participant = participant;
	}

	public abstract Entry[] getEntries(String category, String key, int matchRule);

	/**
	 * Returns the model of this document. Model may be different from actual
	 * resource at corresponding document path due to preprocessing.
	 * <p>
	 * This method must be implemented in subclasses.
	 * </p>
	 * 
	 * @return the model of this document, or <code>null</code> if none
	 */
	public abstract Object getModel();
	
	
	/**
	 * Returns the participant that created this document.
	 * 
	 * @return the participant that created this document
	 */
	public final SearchParticipant getParticipant() {
		return this.participant;
	}
	
	
//	 this class represents a collection of information
//	 that has been produced by a search participant
//	 typically after a file has been processed
//	 an Entry is typically course grained info that results from the SearchParticipants
//	 first step ... and is used to compute more accurate SearchMatches during a SearchParticipants 2nd step

	/**
	 * Returns the path to the original document.
	 * 
	 * @return the path to the document
	 */
	public final String getPath()
	{
		return this.documentPath;
	} 
	
	public abstract void putEntry(Entry entry);
    
    public void dispose()
    {      
    }
}
