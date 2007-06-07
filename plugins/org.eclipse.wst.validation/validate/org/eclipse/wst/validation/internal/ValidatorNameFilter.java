/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;


import java.util.StringTokenizer;

import org.eclipse.core.resources.IResource;

/**
 * Represents a name filter tag in a validator's plugin.xml file. e.g. &lt;filter name="*.*"> Then
 * this class would store the "*.*", and provide the wildcard matching functionality.
 */
public class ValidatorNameFilter {
	private String _nameFilter;
	private String nameFilterExtension;
	private boolean _include = true; // by default, filter in the IFile specified
	private final static String WILDCARD = "*"; //$NON-NLS-1$
	private boolean _isCaseSensitive = true; // by default, the filter name is case-sensitive

	/**
	 * Insert the method's description here. Creation date: (12/4/00 11:08:41 AM)
	 */
	ValidatorNameFilter() {
		//default
	}

	/**
	 * Get the filter, as specified in plugin.xml
	 */
	String getNameFilter() {
		return _nameFilter;
	}

	boolean isCaseSensitive() {
		return _isCaseSensitive;
	}

	boolean isInclude() {
		return _include;
	}

	/**
	 * Return true if the given resource is both applicable and include="true".
	 */
	boolean isApplicableTo(IResource resource) {
		return (isApplicableName(resource) && isInclude());
	}
	
	protected void setNameFilterExtension(String filterExt) {
		nameFilterExtension = filterExt;
	}

	/**
	 * Returns true if the name of the resource matches the filter, or if there is no filter
	 * specified.
	 */
	public boolean isApplicableName(IResource resource) {
		// If name filter is null, means filter out no names.
		// Otherwise, return true only if the given name matches
		// the name filter.
		if (_nameFilter == null)
			return true;
		
		String name = resource.getName();
//		return true if the file name is exact match of the _nameFilter
		if (name.equalsIgnoreCase(_nameFilter))
			return true;

		int indexOfStarDot = _nameFilter.indexOf("*.");

		//return value if the fileter name extension matches the extension
		//of the resource 
		if (indexOfStarDot != -1) {
			String nameExtension = name.substring(name.lastIndexOf(".") + 1);
			return nameFilterExtension.equalsIgnoreCase(nameExtension);
		}

		if (!isCaseSensitive()) {
			name = name.toLowerCase();
		}

		return verifyNameMatch(_nameFilter, name);
	}

	void setInclude(String includeValue) {
		if (includeValue != null) {
			setInclude(Boolean.valueOf(includeValue).booleanValue());
		}
	}

	void setInclude(boolean includeBool) {
		_include = includeBool;
	}

	/**
	 * Set the filter, as specified in plugin.xml
	 */
	void setNameFilter(String filter) {
		_nameFilter = filter;
	}

	void setCaseSensitive(String isCaseSensitiveString) {
		if (isCaseSensitiveString != null) {
			// only change the value from the default if the case-sensitive attribute is defined
			_isCaseSensitive = Boolean.valueOf(isCaseSensitiveString).booleanValue();
		}
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("NameFilter:\n\tnameFilter = "); //$NON-NLS-1$
		buffer.append(_nameFilter);
		return buffer.toString();
	}

	/**
	 * Return true if the given name matches the given filter.
	 * 
	 * The only filter wildcard allowed is '*'.
	 */
	static boolean verifyNameMatch(final String filter, String name) {
		/*
		 * There are eight possible wildcard combinations, given that a wildcard may, if present, be
		 * at the beginning, middle, or end of a name; or any combination of those positions. i.e.,
		 * 
		 * beginning middle end 0 0 0 0 0 1 0 1 0 0 1 1 1 0 0 1 0 1 1 1 0 1 1 1
		 *  
		 */
		StringTokenizer tokenizer = new StringTokenizer(filter, WILDCARD, true);
		boolean wildcardFlag = false;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (token.equals(WILDCARD)) {
				wildcardFlag = true;
			} else {
				if (wildcardFlag) {
					int tokenIndex = name.indexOf(token);
					if (tokenIndex >= 0) {
						name = name.substring(tokenIndex + token.length());
					} else {
						return false;
					}
				} else {
					if (name.startsWith(token)) {
						int tokenIndex = token.length();
						name = name.substring(tokenIndex);
					} else {
						return false;
					}
				}
				wildcardFlag = false;
			}
		}
		if (!name.equals("")) { //$NON-NLS-1$
			if (!wildcardFlag) {
				return false;
			}
		}
		return true;
	}
}
