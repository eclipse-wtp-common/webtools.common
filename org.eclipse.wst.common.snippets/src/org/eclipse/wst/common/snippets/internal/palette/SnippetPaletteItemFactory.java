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

import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.ui.palette.customize.PaletteEntryFactory;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.common.snippets.internal.SnippetsMessages;
import org.eclipse.wst.common.snippets.internal.SnippetsPluginImageHelper;
import org.eclipse.wst.common.snippets.internal.SnippetsPluginImages;

public class SnippetPaletteItemFactory extends PaletteEntryFactory {

	/**
	 * 
	 */
	public SnippetPaletteItemFactory() {
		super();
		setLabel(SnippetsMessages.New_Item_Title); //$NON-NLS-1$
		setImageDescriptor(SnippetsPluginImageHelper.getInstance().getImageDescriptor(SnippetsPluginImages.IMG_CLCL_NEW_TEMPLATE));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.palette.customize.PaletteEntryFactory#createNewEntry(org.eclipse.swt.widgets.Shell)
	 */
	protected PaletteEntry createNewEntry(Shell shell) {
		SnippetPaletteItem item = new SnippetPaletteItem(SnippetsMessages.Unnamed_Template_1); //$NON-NLS-1$
		item.setId(SnippetsMessages.item + "_" + System.currentTimeMillis()); //$NON-NLS-1$ //$NON-NLS-2$
		item.setLabel(SnippetsMessages.Unnamed_Template_1); //$NON-NLS-1$
		item.setUserModificationPermission(PaletteEntry.PERMISSION_FULL_MODIFICATION);
		return item;
	}

}