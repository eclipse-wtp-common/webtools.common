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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.common.snippets.core.ISnippetItem;
import org.eclipse.wst.common.snippets.core.ISnippetsEntry;
import org.eclipse.wst.common.snippets.internal.ISnippetCategory;
import org.eclipse.wst.common.snippets.internal.PluginRecord;
import org.eclipse.wst.common.snippets.internal.SnippetsPluginImageHelper;
import org.eclipse.wst.common.snippets.internal.SnippetsPluginImages;


public class SnippetImageDescriptorHelper {
	protected static SnippetImageDescriptorHelper instance = null;

	/**
	 * Gets the instance.
	 * 
	 * @return Returns a SnippetImageHelper
	 */
	public synchronized static SnippetImageDescriptorHelper getInstance() {
		if (instance == null)
			instance = new SnippetImageDescriptorHelper();
		return instance;
	}

	public SnippetImageDescriptorHelper() {
		super();
	}

	protected ImageDescriptor getDefaultDescriptor() {
		return getImageDescriptor(SnippetsPluginImages.IMG_OBJ_SNIPPETS);
	}

	public ImageDescriptor getImageDescriptor(ISnippetCategory category) {
		return getImageDescriptor(category, false);
	}

	public ImageDescriptor getImageDescriptor(ISnippetCategory category, boolean largeIcon) {
		String iconName = largeIcon ? category.getLargeIconName() : category.getIconName();
		if (largeIcon && (iconName == null || iconName.length() == 0))
			iconName = category.getIconName();
		if (category == null || iconName == null || iconName.length() == 0)
			return getDefaultDescriptor();
		ImageDescriptor image = null;
		if (category.getSourceType() == ISnippetsEntry.SNIPPET_SOURCE_PLUGINS) {
			PluginRecord record = (PluginRecord) category.getSourceDescriptor();
			if (record != null && record.getPluginName() != null) {
				image = getInstalledImage(record.getPluginName(), iconName);
			}
		}
		else {
			image = getImageDescriptor(iconName);
		}
		if (image == null || image.equals(ImageDescriptor.getMissingImageDescriptor()))
			image = getDefaultDescriptor();
		return image;
	}

	public ImageDescriptor getImageDescriptor(ISnippetItem item) {
		return getImageDescriptor(item, false);
	}

	public ImageDescriptor getImageDescriptor(ISnippetItem item, boolean largeIcon) {
		ImageDescriptor image = null;
		String iconName = largeIcon ? item.getLargeIconName() : item.getIconName();
		if (largeIcon && (iconName == null || iconName.length() == 0))
			iconName = item.getIconName();
		if (item.getSourceType() == ISnippetsEntry.SNIPPET_SOURCE_PLUGINS) {
			PluginRecord record = (PluginRecord) item.getSourceDescriptor();
			if (record != null && record.getPluginName() != null) {
				image = getInstalledImage(record.getPluginName(), iconName);
				if (image == null && item.getCategory() != null) {
					image = getImageDescriptor(item.getCategory(), largeIcon);
				}
			}
		}
		else {
			if (iconName == null || iconName.length() < 1)
				image = getImageDescriptor(item.getCategory(), largeIcon);
			else
				image = getImageDescriptor(iconName);
		}
		if (image == null || image.equals(ImageDescriptor.getMissingImageDescriptor()))
			image = getDefaultDescriptor();
		return image;
	}

	public ImageDescriptor getImageDescriptor(String resource) {
		ImageDescriptor image = null;
		if (resource != null) {
			image = SnippetsPluginImageHelper.getInstance().getImageDescriptor(resource);
		}
		if (image == null || image.equals(ImageDescriptor.getMissingImageDescriptor()))
			image = getDefaultDescriptor();
		return image;
	}

	/**
	 * @param image
	 * @param iconName
	 * @param plugin
	 * @return
	 */
	protected ImageDescriptor getInstalledImage(String plugin, String iconName) {
		ImageDescriptor image = null;
		if (iconName != null && iconName.length() > 0) {
			if (plugin != null) {
				image = SnippetsPluginImageHelper.getInstance().getImageDescriptor(iconName, plugin);
			}
		}
		if (image == null || image.equals(ImageDescriptor.getMissingImageDescriptor()))
			image = getDefaultDescriptor();
		return image;
	}
}