/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
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
 * A snippet item is the actual object draggable from the Snippets view.
 * <p>
 * Clients are not intended to implement this interface.
 * </p>
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ISnippetItem extends ISnippetsEntry {
	/**
	 * @return the category holding this item
	 */
	ISnippetCategory getCategory();

	/**
	 * @return the content string for this item; the text inserted may not be
	 *         identical
	 */
	String getContentString();

	/**
	 * @return an array containing all of the valid variables for this item
	 */
	ISnippetVariable[] getVariables();
}
