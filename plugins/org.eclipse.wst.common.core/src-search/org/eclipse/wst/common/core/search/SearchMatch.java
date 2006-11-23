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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;

/**
 * A search match represents the result of a search query.
 * <p>
 * This class is intended to be instantiated and subclassed by clients.
 * </p>
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still under development and expected to
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
 * (repeatedly) as the API evolves.
 * </p>
 * 
 * @see SearchEngine#search(org.eclipse.wst.common.core.search.pattern.SearchPattern, SearchParticipant[], ISearchScope,
 *      SearchRequestor, org.eclipse.core.runtime.IProgressMonitor)
 */
public class SearchMatch implements IAdaptable
{

	/**
	 * Optional resource of the element
	 */
	IFile file; // resource where match is found

	/**
	 * The offset the match starts at, or -1 if unknown
	 */
	int offset;

	/**
	 * The length the length of the match, or -1 if unknown
	 */
	int length;

	/**
	 * Optional element that encloses or corresponds to the match
	 */
	Object object;

	/**
	 * Creates a new search match.
	 * 
	 * @param element
	 *            the element that encloses or corresponds to the match, or
	 *            <code>null</code> if none
	 * @param offset
	 *            the offset the match starts at, or -1 if unknown
	 * @param length
	 *            the length of the match, or -1 if unknown
	 * @param participant
	 *            the search participant that created the match
	 * @param resource
	 *            the resource of the element, or <code>null</code> if none
	 */
	public SearchMatch(Object element, int offset, int length, IFile resource)
	{
		this.object = element;
		this.offset = offset;
		this.length = length;
		this.file = resource;
	}

	public IFile getFile()
	{
		return file;
	}

	public void setFile(IFile file)
	{
		this.file = file;
	}

	public int getLength()
	{
		return length;
	}

	public void setLength(int length)
	{
		this.length = length;
	}

	// issue (cs/eb) holding objects is a bit odd, since it implies the object's
	// life span
	// is the same as the SearchMatch. What happens when an object is deleted?
	// does the SearchMatch get deleted? Aren't coordinates good enough?
	// at the very least we should document that using this field has some
	// consequences
	/**
	 * (eb) see comments for {@link #setObject(Object)}
	 */
	public Object getObject()
	{
		return object;
	}

	/**
	 * @param object
	 *            Object is an instance of some part of the model that represent
	 *            the content that was searched. It may require to calculate
	 *            some additional information that may be required by the tools
	 *            through {@link #getObject()) (e.g. XML node namespace,
	 *            nessesary to obtain the prefix for the refactoring tool).
	 *            <p>
	 *            There should be no danger here of preventing the object from
	 *            garbage collection because instances of the search matches
	 *            supposed to be short lived. {@link SearchMatch} is usually
	 *            collected by {@link SearchRequestor} which itself is lived for
	 *            the time of the search and then discarded or re-initialized.
	 *            Usually the tool that requested a search extracts the
	 *            information from the collected {@link SearchMatch} and after
	 *            that {@link SearchMatch} could be garbage collected, releasing
	 *            object that they hold to.
	 *            </p>
	 */
	public void setObject(Object object)
	{
		this.object = object;
	}

	public int getOffset()
	{
		return offset;
	}

	public void setOffset(int offset)
	{
		this.offset = offset;
	}

	public Object getAdapter(Class adapter)
	{
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}
    
    public Map map = new HashMap(); 

}
