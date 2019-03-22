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

import org.eclipse.core.runtime.CoreException;

/**
 * Collects the results from a search engine query. Clients implement a subclass
 * to pass to <code>SearchEngine.search</code> and implement the
 * {@link #acceptSearchMatch(SearchMatch)} method.
 * <p>
 * The subclasses of the SearchRequestor could collected search matches, filter,
 * sort and group them. It's up to the client to pass the required extension of
 * the SearchRequestor to the search engine.
 * </p>
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still under development and expected to
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
 * (repeatedly) as the API evolves.
 * </p>
 * 
 * @see SearchEngine
 */
public abstract class SearchRequestor
{

	/**
	 * Accepts the given search match.
	 * 
	 * @param match
	 *            the found match
	 * @throws CoreException
	 */
	public abstract void acceptSearchMatch(SearchMatch match)
			throws CoreException;

}
