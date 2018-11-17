/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
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
 * A Snippets Entry is an abstract notion encapsulating the values used to
 * represent Snippet Items and Containers. No object implements only this
 * interface.
 * <p>
 * Clients are not intended to implement this interface.
 * </p>
 * 
 * @since 1.0
 */

public interface ISnippetsEntry {

	/**
	 * Denotes that this entry was contributed by a plug-in
	 */
	String SNIPPET_SOURCE_PLUGINS = "PLUGINS"; //$NON-NLS-1$

	/**
	 * Denotes that this entry was created by the user
	 */
	String SNIPPET_SOURCE_USER = "USER"; //$NON-NLS-1$

	/**
	 * Denotes that this entry was discovered within the workspace
	 * 
	 */
	String SNIPPET_SOURCE_WORKSPACE = "WORKSPACE"; //$NON-NLS-1$

	/**
	 * @return a longer description to display for this item
	 */
	String getDescription();

	/**
	 * @return the filters for which this entry will be shown when filtering
	 *         is enabled
	 */
	String[] getFilters();


	/**
	 * @since 2.0
	 * @return the ID supplied when contributing this entry into the Snippets
	 *         model
	 */
	String getId();

	/**
	 * @return the label to display for this item
	 */
	String getLabel();

	/**
	 * @return the object defining where this entry originated from. Valid
	 *         values are instances of CategoryFileInfo and PluginRecord.
	 */
	Object getSourceDescriptor();

	/**
	 * @return the type of the source descriptor. Valid values are
	 *         SNIPPET_SOURCE_PLUGINS, SNIPPET_SOURCE_USER, and
	 *         SNIPPET_SOURCE_WORKSPACE.
	 */
	Object getSourceType();
}
