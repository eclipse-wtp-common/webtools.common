/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
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
package org.eclipse.wst.common.snippets.internal;

import org.eclipse.wst.common.snippets.core.ISnippetsEntry;

/**
 * <p>
 * Public interface for the Snippets manager.
 * </p>
 * <p>
 * Provides read-only access to the model for other plugins as well as model
 * change notification.
 * </p>
 */
public interface ISnippetManager {
	/**
	 * Adds a listener to the list of those notified when the model contents
	 * are replaced
	 * 
	 * @param listener -
	 *            the listener to add
	 */
	void addEntryChangeListener(IEntryChangeListener listener);

	/**
	 * Locates a Snippet within the model
	 * 
	 * @param id
	 * @return the Snippet or Category if found, null if not
	 */
	ISnippetsEntry findEntry(String id);

	/**
	 * @return the active SnippetDefinitions instance for this session
	 */
	SnippetDefinitions getDefinitions();

	/**
	 * Adds a listener to the list of those notified when the model contents
	 * are replaced
	 * 
	 * @param listener -
	 *            the listener to remove
	 */
	void removeEntryChangeListener(IEntryChangeListener listener);
}
