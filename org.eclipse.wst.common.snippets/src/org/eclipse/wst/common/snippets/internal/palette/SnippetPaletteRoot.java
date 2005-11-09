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

import java.util.List;

import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteRoot;
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
		List oldChildren = null;
		if (oldDefinitions != null)
			oldChildren = oldDefinitions.getCategories();
		this.children = fDefinitions.getCategories();
		for (int i = 0; i < children.size(); i++) {
			((PaletteEntry) children.get(i)).setParent(this);
		}
		if (Logger.DEBUG_VIEWER_CONTENT)
			System.out.println(getClass().getName() + '@' + hashCode() + " setting categories to: " + children); //$NON-NLS-1$
		listeners.firePropertyChange(PROPERTY_CHILDREN, oldChildren, children);
	}
}