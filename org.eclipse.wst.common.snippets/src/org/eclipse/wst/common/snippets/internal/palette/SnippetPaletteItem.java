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


package org.eclipse.wst.common.snippets.internal.palette;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteTemplateEntry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.common.snippets.core.ISnippetItem;
import org.eclipse.wst.common.snippets.internal.ISnippetCategory;
import org.eclipse.wst.common.snippets.internal.ISnippetVariable;


public class SnippetPaletteItem extends PaletteTemplateEntry implements ISnippetItem {

	protected ISnippetCategory fCategory;
	protected String fCategoryName;

	protected String fClassName;
	protected String fContentString;
	protected String fEditorClassName;
	protected String[] fFilters = new String[0];
	protected String fIconName;
	protected String fId;
	protected String fLargeIconName;
	protected Object fSourceDescriptor;
	protected Object fSourceType = SNIPPET_SOURCE_USER;
	protected List fVariables;

	/**
	 * @param label
	 * @param shortDesc
	 * @param template
	 * @param iconSmall
	 * @param iconLarge
	 */
	public SnippetPaletteItem(String label) {
		super(label, label, null, null, null);
		fVariables = new ArrayList(0);
	}

	public void addVariable(ISnippetVariable variable) {
		fVariables.add(variable);
	}

	public ISnippetCategory getCategory() {
		return fCategory;
	}

	// public SnippetPaletteItem(Tool tool, String label) {
	// super(tool, label);
	// }
	//
	// public SnippetPaletteItem(Tool tool, String label, String shortDesc) {
	// super(tool, label, shortDesc);
	// }
	//
	// public SnippetPaletteItem(Tool tool, String label, String shortDesc,
	// ImageDescriptor iconSmall, ImageDescriptor iconLarge) {
	// super(tool, label, shortDesc, iconSmall, iconLarge);
	// }

	public String getCategoryName() {
		if (fCategory == null)
			return fCategoryName;
		return fCategory.getId();
	}

	public String getClassName() {
		return fClassName;
	}

	public String getContentString() {
		return fContentString;
	}

	public String getEditorClassName() {
		return fEditorClassName;
	}

	public String[] getFilters() {
		return fFilters;
	}

	public String getIconName() {
		return fIconName;
	}

	public String getId() {
		return fId;
	}

	public ImageDescriptor getLargeIcon() {
		ImageDescriptor icon = super.getLargeIcon();
		if (icon == null) {
			icon = SnippetImageDescriptorHelper.getInstance().getImageDescriptor(this, true);
			setLargeIcon(icon);
		}
		return icon;
	}

	public String getLargeIconName() {
		return fLargeIconName;
	}

	public ImageDescriptor getSmallIcon() {
		ImageDescriptor icon = super.getSmallIcon();
		if (icon == null) {
			icon = SnippetImageDescriptorHelper.getInstance().getImageDescriptor(this);
			setSmallIcon(icon);
		}
		return icon;
	}

	public Object getSourceDescriptor() {
		return fSourceDescriptor;
	}

	public Object getSourceType() {
		return fSourceType;
	}

	public ISnippetVariable[] getVariables() {
		return (ISnippetVariable[]) fVariables.toArray(new ISnippetVariable[fVariables.size()]);
	}

	public void removeVariable(ISnippetVariable variable) {
		fVariables.remove(variable);
	}

	public void setCategory(ISnippetCategory category) {
		fCategory = category;
		super.setParent((PaletteContainer) category);
		if (fCategory == null)
			setCategoryName(null);
		else
			setCategoryName(fCategory.getId());
	}

	public void setCategoryName(String categoryName) {
		fCategoryName = categoryName;
	}

	public void setClassName(String className) {
		fClassName = className;
	}

	public void setContentString(String contentString) {
		fContentString = contentString;
	}

	public void setEditorClassName(String editorClassName) {
		fEditorClassName = editorClassName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.snippets.core.ISnippetsEntry#setFilters(java.lang.String[])
	 */
	public void setFilters(String[] filters) {
		fFilters = filters;
	}

	public void setIconName(String iconName) {
		fIconName = iconName;
	}

	public void setId(String id) {
		fId = id;
	}

	public void setLargeIconName(String largeIconName) {
		fLargeIconName = largeIconName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.palette.PaletteEntry#setParent(org.eclipse.gef.palette.PaletteContainer)
	 */
	public void setParent(PaletteContainer newParent) {
		setCategory((ISnippetCategory) newParent);
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

	protected void setVariables(ISnippetVariable[] variables) {
		fVariables = new ArrayList(variables.length);
		fVariables.addAll(Arrays.asList(variables));
	}
}