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


package org.eclipse.wst.common.snippets.core;


/**
 * This interface is not meant to be implemented by clients.
 */
public interface ISnippetsEntry {
	final String SNIPPET_SOURCE_PLUGINS = "PLUGINS"; //$NON-NLS-1$
	final String SNIPPET_SOURCE_USER = "USER"; //$NON-NLS-1$
	final String SNIPPET_SOURCE_WORKSPACE = "WORKSPACE"; //$NON-NLS-1$

	String getDescription();

	String[] getFilters();

	String getIconName();

	String getId();

	String getLabel();

	String getLargeIconName();

	Object getSourceDescriptor();

	Object getSourceType();

	boolean isVisible();

	void setDescription(String description);

	void setFilters(String[] filters);

	void setIconName(String icon);

	void setId(String id);

	void setLabel(String label);

	void setLargeIconName(String icon);

	void setSourceDescriptor(Object descriptor);

	void setSourceType(Object type);

	void setVisible(boolean visible);
}