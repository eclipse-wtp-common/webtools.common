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
package org.eclipse.wst.common.snippets.internal;

public interface ISnippetVariable {
	/**
	 * Gets the defaultValue.
	 * 
	 * @return Returns a String
	 */
	String getDefaultValue();

	/**
	 * Gets the description.
	 * 
	 * @return Returns a String
	 */
	String getDescription();

	/**
	 * Gets the id.
	 * 
	 * @return Returns a String
	 */
	String getId();

	/**
	 * Gets the name.
	 * 
	 * @return Returns a String
	 */
	String getName();
}