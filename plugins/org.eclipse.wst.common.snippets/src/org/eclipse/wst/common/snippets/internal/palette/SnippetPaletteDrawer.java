/*******************************************************************************
 * Copyright (c) 2004, 2024 IBM Corporation and others.
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


package org.eclipse.wst.common.snippets.internal.palette;

import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteTemplateEntry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.common.snippets.core.ISnippetCategory;
import org.eclipse.wst.common.snippets.core.ISnippetItem;

public class SnippetPaletteDrawer extends PaletteDrawer implements ISnippetCategory {
	protected String[] fFilters = new String[0];

	protected String fIconName;
	protected String fId;
	protected String fLargeIconName;

	protected Object fSourceDescriptor;
	protected Object fSourceType = SNIPPET_SOURCE_USER;

	/**
	 * @param label
	 */
	public SnippetPaletteDrawer(String label) {
		super(label);
		setDrawerType(PaletteTemplateEntry.PALETTE_TYPE_TEMPLATE);
		setType(PaletteDrawer.PALETTE_TYPE_DRAWER);
	}

	/**
	 * @param label
	 * @param icon
	 */
	public SnippetPaletteDrawer(String label, ImageDescriptor icon) {
		super(label, icon);
		setDrawerType(PaletteTemplateEntry.PALETTE_TYPE_TEMPLATE);
		setType(PaletteDrawer.PALETTE_TYPE_DRAWER);
	}

	public void add(SnippetPaletteItem item) {
		super.add(item);
		item.setCategory(this);
		item.setParent(this);
	}

	public String[] getFilters() {
		return fFilters;
	}

	/**
	 * Gets the id.
	 * 
	 * @return Returns a String
	 */
	public String getId() {
		return fId;
	}

	public ISnippetItem[] getItems() {
		return getChildren().toArray(new ISnippetItem[0]);
	}

	public ImageDescriptor getLargeIcon() {
		ImageDescriptor icon = super.getLargeIcon();
		if (icon == null) {
			icon = SnippetImageDescriptorHelper.getInstance().getImageDescriptor(this, true);
			setLargeIcon(icon);
		}
		return icon;
	}

	/**
	 * @return String
	 */
	public String getLargeIconName() {
		return fLargeIconName;
	}

	/**
	 * @see PaletteEntry#getSmallIcon()
	 */
	public ImageDescriptor getSmallIcon() {
		ImageDescriptor icon = super.getSmallIcon();
		if (icon == null) {
			icon = SnippetImageDescriptorHelper.getInstance().getImageDescriptor(this);
			setSmallIcon(icon);
		}
		return icon;
	}

	/**
	 * Gets the iconName.
	 * 
	 * @return Returns a String
	 */
	public String getSmallIconName() {
		return fIconName;
	}

	public Object getSourceDescriptor() {
		return fSourceDescriptor;
	}

	public Object getSourceType() {
		return fSourceType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.snippets.ui.ISnippetCategory#remove(org.eclipse.wst.common.snippets.ui.ISnippetItem)
	 */
	public void remove(SnippetPaletteItem item) {
		super.remove(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.snippets.ui.ISnippetsEntry#setFilters(java.lang.String[])
	 */
	public void setFilters(String[] filters) {
		fFilters = filters;
	}

	/**
	 * Sets the iconName.
	 * 
	 * @param iconName
	 *            The iconName to set
	 */
	public void setIconName(String iconName) {
		fIconName = iconName;
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
	 * Sets the largeIconName.
	 * 
	 * @param largeIconName
	 *            The largeIconName to set
	 */
	public void setLargeIconName(String largeIconName) {
		fLargeIconName = largeIconName;
	}

	/**
	 * Sets the sourceDescriptor.
	 * 
	 * @param sourceDescriptor
	 *            The sourceDescriptor to set
	 */
	public void setSourceDescriptor(Object sourceDescriptor) {
		fSourceDescriptor = sourceDescriptor;
	}

	/**
	 * Sets the sourceType.
	 * 
	 * @param sourceType
	 *            The sourceType to set
	 */
	public void setSourceType(Object sourceType) {
		fSourceType = sourceType;
	}
}
