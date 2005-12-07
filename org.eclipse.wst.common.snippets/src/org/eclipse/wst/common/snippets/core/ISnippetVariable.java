/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.snippets.core;

/**
 * A variable is a user-prompted value meant to be filled-in by the user
 * during insertion.
 * 
 * @since 1.0
 */
public interface ISnippetVariable {
	/**
	 * @return Returns the default value for this variable
	 */
	String getDefaultValue();

	/**
	 * @return Returns the description of this variable
	 */
	String getDescription();

	/**
	 * 
	 * @return Returns the name of this variable.
	 */
	String getName();
}