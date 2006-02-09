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

/**
 * This interface provides values that can be used in the map of search options passed
 * to
 * {@link SearchEngine#search(org.eclipse.wst.common.core.search.pattern.SearchPattern, SearchRequestor, org.eclipse.wst.common.core.search.scope.SearchScope, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
 * SearchEngine.search()}.
 * <p>
 * Note that not all search participants will provide specialized behavior based on
 * use of these search options.
 * <p>
 * Also note that individual search participants may support additional options not
 * listed here. These search options should be formed so that a search participant can
 * safely ignore any that it does not understand.
 * 
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still under development and expected to
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
 * (repeatedly) as the API evolves.
 * </p>
 */
public interface ISearchOptions {

	/**
	 * Search option specifying a trade-off choice between search performance and
	 * completeness.
	 */
	public static final String PRIORITY_OPTION = "org.eclipse.wst.common.core.search.PRIORITY"; //$NON_NLS_1$
	/**
	 * Choice for the {@link #PRIORITY_OPTION} search option that emphasizes
	 * performance at the possible expense of accuracy. This value should be used for
	 * all search calls made on the user interface thread. In some cases, search
	 * results may not be complete because of inaccuracies caused by timing windows.
	 */
	public static final String PRIORITY_VALUE_TIMELINESS = "FAST_SEARCH"; //$NON_NLS_1$
	/**
	 * Choice for the {@link #PRIORITY_OPTION} search option that emphasizes
	 * accuracy at the possible expense of timeliness. This value should never be used
	 * for search calls made on the user interface thread, because some search
	 * participants may choose to use time-consuming background processing to return a
	 * complete set of matches.
	 */
	public static final String PRIORITY_VALUE_COMPLETENESS = "COMPLETE_SEARCH"; //$NON_NLS_1$
	/**
	 * Default choice for the {@link #PRIORITY_OPTION} search option.
	 */
	public static final String PRIORITY_VALUE_DEFAULT = PRIORITY_VALUE_TIMELINESS;

}
