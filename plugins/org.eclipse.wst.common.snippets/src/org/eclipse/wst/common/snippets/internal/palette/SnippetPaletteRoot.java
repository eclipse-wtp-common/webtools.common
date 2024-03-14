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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.wst.common.snippets.core.ISnippetCategory;
import org.eclipse.wst.common.snippets.internal.IEntryChangeListener;
import org.eclipse.wst.common.snippets.internal.Logger;
import org.eclipse.wst.common.snippets.internal.SnippetDefinitions;
import org.eclipse.wst.common.snippets.internal.model.SnippetManager;


public class SnippetPaletteRoot extends PaletteRoot implements IEntryChangeListener {

	protected SnippetDefinitions fDefinitions = null;

	public SnippetPaletteRoot(SnippetDefinitions definitions) {
		super();
		setDefinitions(definitions);
	}

	public void connect() {
		SnippetManager.getInstance().addEntryChangeListener(this);
		addPropertyChangeListener(SnippetManager.getInstance());
	}

	public void disconnect() {
		removePropertyChangeListener(SnippetManager.getInstance());
		SnippetManager.getInstance().removeEntryChangeListener(this);
	}

	/**
	 * Gets the definitions.
	 * 
	 * @return Returns a SnippetDefinitions
	 */
	public SnippetDefinitions getDefinitions() {
		return fDefinitions;
	}


	public void modelChanged(SnippetDefinitions oldDefinitions, SnippetDefinitions newDefinitions) {
		setDefinitions(newDefinitions);
	}

	/**
	 * @param definitions
	 */
	public void setDefinitions(SnippetDefinitions newDefinitions) {
		SnippetDefinitions oldDefinitions = getDefinitions();
		this.fDefinitions = newDefinitions;
		List<ISnippetCategory> oldChildren = null;
		if (oldDefinitions != null) {
			oldChildren = oldDefinitions.getCategories();
		}
		List<ISnippetCategory> categories = fDefinitions.getCategories();
		this.children = new ArrayList<>(categories.size());
		for (int i = 0; i < categories.size(); i++) {
			PaletteEntry e = (PaletteEntry) categories.get(i);
			children.add(e);
			e.setParent(this);
		}
		if (Logger.DEBUG_VIEWER_CONTENT) {
			System.out.println(getClass().getName() + '@' + hashCode() + " setting categories to: " + children); //$NON-NLS-1$
		}
		listeners.firePropertyChange(PROPERTY_CHILDREN, oldChildren, children);
	}
}
