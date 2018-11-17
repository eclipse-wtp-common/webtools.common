/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.snippets.core;

/**
 * A variable is a user-prompted value meant to be filled-in by the user
 * during insertion.
 * <p>
 * Clients are not intended to implement this interface.
 * </p>
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
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
