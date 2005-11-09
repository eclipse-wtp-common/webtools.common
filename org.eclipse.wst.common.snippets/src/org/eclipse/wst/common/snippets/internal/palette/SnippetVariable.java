/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/


package org.eclipse.wst.common.snippets.internal.palette;

import org.eclipse.wst.common.snippets.core.ISnippetVariable;

public class SnippetVariable implements ISnippetVariable {
	protected String fDefaultValue;
	protected String fDescription;

	protected String fId;

	protected String fName;

	public SnippetVariable() {
		super();
	}

	/**
	 * Gets the defaultValue.
	 * 
	 * @return Returns a String
	 */
	public String getDefaultValue() {
		if (fDefaultValue == null)
			fDefaultValue = ""; //$NON-NLS-1$
		return fDefaultValue;
	}

	/**
	 * Gets the description.
	 * 
	 * @return Returns a String
	 */
	public String getDescription() {
		return fDescription;
	}

	/**
	 * Gets the id.
	 * 
	 * @return Returns a String
	 */
	public String getId() {
		return fId;
	}

	/**
	 * Gets the name.
	 * 
	 * @return Returns a String
	 */
	public String getName() {
		if (fName == null || fName.length() == 0)
			return fId;
		return fName;
	}

	/**
	 * Sets the defaultValue.
	 * 
	 * @param defaultValue
	 *            The defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		fDefaultValue = defaultValue;
	}

	/**
	 * Sets the description.
	 * 
	 * @param description
	 *            The description to set
	 */
	public void setDescription(String description) {
		fDescription = description;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            The id to set
	 */
	public void setId(String id) {
		fId = id;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            The name to set
	 */
	public void setName(String name) {
		fName = name;
	}

}