/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import org.eclipse.core.resources.IResource;



/**
 * This class represents the plugin.xml tags, for a validator, for both name filters and type
 * filters. i.e., if an object has name filter and type filter specified, the filter filters out
 * objects which are not both of that type and named like the filter.
 */
public class ValidatorFilter {
	private ValidatorNameFilter _nameFilter = null;
	private ValidatorTypeFilter _typeFilter = null;
	private ValidatorActionFilter _actionFilter = null;

	/* package */ValidatorFilter() {
		super();
		_nameFilter = new ValidatorNameFilter();
		_typeFilter = new ValidatorTypeFilter();
		_actionFilter = new ValidatorActionFilter();
	}

	/* package */ValidatorFilter(String mustImplementClass) {
		this();
		_nameFilter = new ValidatorNameFilter();
		_typeFilter.setMustImplementClass(mustImplementClass);
		_actionFilter = new ValidatorActionFilter();
	}

	public boolean isApplicableAction(int resourceDelta) {
		return ((resourceDelta & _actionFilter.getActionType()) != 0);
	}

	/**
	 * Returns true if the given resource's name matches the name filter.
	 * 
	 * e.g. if the name filter is "*.java", and this resource is "readme.txt", this method will
	 * return false. If the resource is named "readme.java", this method will return true.
	 */
	/* package */boolean isApplicableName(IResource resource) {
		return _nameFilter.isApplicableName(resource);
	}

	/**
	 * Returns true if the given resource's type matches the type filter.
	 * 
	 * e.g. if the type filter is "IFile", and this resource is "IProject", this method will return
	 * false. If the resource is an IFile, this method will return true.
	 */
	/* package */boolean isApplicableType(IResource resource) {
		return _typeFilter.isApplicableType(resource);
	}

	public void setActionFilter(String actions) {
		_actionFilter.setActionTypes(actions);
	}

	/**
	 * Sets the name filter.
	 */
	/* package */void setNameFilter(String filter, String isCaseSensitiveString) {
		_nameFilter.setNameFilter(filter);
		_nameFilter.setCaseSensitive(isCaseSensitiveString);
	}

	/**
	 * Sets the type filter.
	 */
	/* package */void setTypeFilter(String filter) {
		_typeFilter.setTypeFilter(filter);
	}

	public String toString() {
		final String lineSep = System.getProperty("line.separator"); //$NON-NLS-1$
		StringBuffer buffer = new StringBuffer();
		buffer.append("ValidatorFilter:"); //$NON-NLS-1$
		buffer.append(lineSep);
		buffer.append("     nameFilter = " + _nameFilter); //$NON-NLS-1$
		buffer.append(lineSep);
		buffer.append("     typeFilter = " + _typeFilter); //$NON-NLS-1$
		buffer.append(lineSep);
		buffer.append("     actionFilter = " + _actionFilter); //$NON-NLS-1$
		buffer.append(lineSep);
		return buffer.toString();
	}

	/**
	 * @return Returns the _nameFilter.
	 */
	public ValidatorNameFilter get_nameFilter() {
		return _nameFilter;
	}
}