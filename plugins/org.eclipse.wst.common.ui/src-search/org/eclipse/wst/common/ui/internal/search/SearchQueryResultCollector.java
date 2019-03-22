/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.ui.internal.search;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.search.ui.text.Match;
import org.eclipse.wst.common.core.search.SearchMatch;
import org.eclipse.wst.common.core.search.SearchRequestor;

class SearchQueryResultCollector extends SearchRequestor {
	
	private SearchResult fSearchResult;

	
	protected SearchQueryResultCollector(SearchResult result){
		super();
		fSearchResult= result;
	}

	public void acceptSearchMatch(SearchMatch match) throws CoreException {
		Match aMatch = new Match(match.getFile(), match.getOffset(), match.getLength());
		fSearchResult.addMatch(aMatch);
		
	}
}