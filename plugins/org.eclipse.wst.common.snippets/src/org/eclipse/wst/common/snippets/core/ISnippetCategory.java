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
 * Represents a category containing individual snippets.
 * <p>
 * Clients are not intended to implement this interface.
 * </p>
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ISnippetCategory extends ISnippetsEntry {
	/**
	 * @return an array of the items within this category
	 */
	ISnippetItem[] getItems();
}
