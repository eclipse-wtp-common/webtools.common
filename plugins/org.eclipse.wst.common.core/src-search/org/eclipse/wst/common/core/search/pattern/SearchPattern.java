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

package org.eclipse.wst.common.core.search.pattern;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;

/**
 * A search defines how search results are found.
 * 
 * This class is intended to be subclassed by clients.
 * 
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still under development and expected to
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
 * (repeatedly) as the API evolves.
 * </p>
 */
public abstract class SearchPattern implements IAdaptable
{

	/**
	 * Match rule: The search pattern matches the search result only if cases
	 * are the same. Can be combined to previous rules, e.g.
	 * {@link #R_EXACT_MATCH} | {@link #R_CASE_SENSITIVE}
	 */
	public static final int R_CASE_SENSITIVE = 8;

	// Rules for pattern matching: (exact, prefix, pattern) [ | case sensitive]
	/**
	 * Match rule: The search pattern matches exactly the search result, that
	 * is, the source of the search result equals the search pattern.
	 */
	public static final int R_EXACT_MATCH = 0;

	/**
	 * Match rule: The search pattern contains one or more wild cards ('*')
	 * where a wild-card can replace 0 or more characters in the search result.
	 */
	public static final int R_PATTERN_MATCH = 2;

	/**
	 * Match rule: The search pattern is a prefix of the search result.
	 */
	public static final int R_PREFIX_MATCH = 1;

	/**
	 * Match rule: The search pattern contains a regular expression.
	 */
	public static final int R_REGEXP_MATCH = 4;

	private int matchRule;

	public SearchPattern()
	{
		this.matchRule = R_EXACT_MATCH | R_CASE_SENSITIVE;
	}

	public SearchPattern(int matchRule)
	{
		this.matchRule = matchRule;

	}

	public final int getMatchRule()
	{
		return this.matchRule;
	}

	public Object getAdapter(Class adapter)
	{
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

}
