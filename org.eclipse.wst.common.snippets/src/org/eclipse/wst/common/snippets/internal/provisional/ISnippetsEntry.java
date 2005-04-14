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


package org.eclipse.wst.common.snippets.internal.provisional;

/**
 * A Snippets Entry is an abstract notion encapsulating the values used to
 * represent Snippet Items and Containers. No object implements only this
 * interface.
 * 
 * This interface is not meant to be implemented by clients.
 */

public interface ISnippetsEntry {

	/**
	 * Denotes that this entry was contributed by a plug-in
	 */
	final String SNIPPET_SOURCE_PLUGINS = "PLUGINS"; //$NON-NLS-1$

	/**
	 * Denotes that this entry was created by the user
	 */
	final String SNIPPET_SOURCE_USER = "USER"; //$NON-NLS-1$

	/**
	 * Denotes that this entry was discovered within the workspace
	 * @deprecated
	 */
	final String SNIPPET_SOURCE_WORKSPACE = "WORKSPACE"; //$NON-NLS-1$

	/**
	 * @return the (lengthy) text description shown to the user for this entry
	 */
	String getDescription();

	/**
	 * @return the filters for which this entry will be shown (when filtering
	 *         is enabled)
	 */
	String[] getFilters();

	/**
	 * @return the path to the icon image for this entry, relative to the
	 *         entry's contributing plugin installation path or relative to
	 *         the Snippets plugin's installation path
	 */
	String getIconName();

	/**
	 * @return a unique ID for this entry
	 */
	String getId();

	/**
	 * @return the (short) string shown to the user for this entry
	 */
	String getLabel();

	/**
	 * @return the path to a large icon image for this entry, relative to the
	 *         entry's contributing plugin installation path or relative to
	 *         the Snippets plugin's installation path, or null if one is not
	 *         specified.
	 */
	String getLargeIconName();

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

	/**
	 * @return whether this entry is currently visible to the user or not
	 */
	boolean isVisible();

	/**
	 * @param description
	 * @deprecated
	 */
	void setDescription(String description);

	/**
	 * @param filters
	 * @deprecated
	 */
	void setFilters(String[] filters);

	/**
	 * @param icon
	 * @deprecated
	 */
	void setIconName(String icon);

	/**
	 * @param id
	 * @deprecated
	 */
	void setId(String id);

	/**
	 * @param label the (short) string shown to the user for this entry
	 * @deprecated
	 */
	void setLabel(String label);

	/**
	 * @param icon
	 * @deprecated
	 */
	void setLargeIconName(String icon);

	/**
	 * @param descriptor
	 * @deprecated
	 */
	void setSourceDescriptor(Object descriptor);

	/**
	 * @param type
	 * @deprecated
	 */
	void setSourceType(Object type);

	/**
	 * @param visible
	 * @deprecated
	 */
	void setVisible(boolean visible);
}