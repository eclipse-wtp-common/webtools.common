/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.ui.provisional.editors;

import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.MultiPageEditorSite;

public class PostMultiPageEditorSite extends MultiPageEditorSite {
	/**
	 * The post selection change listener, initialized lazily;
	 * <code>null</code> if not yet created.
	 */
	private ISelectionChangedListener postSelectionChangedListener = null;

	public PostMultiPageEditorSite(MultiPageEditorPart multiPageEditor, IEditorPart editor) {
		super(multiPageEditor, editor);
	}

	/**
	 * Returns the selection changed listener which listens to the nested
	 * editor's post selection changes, and calls
	 * <code>handlePostSelectionChanged</code>.
	 * 
	 * @return the selection changed listener
	 */
	private ISelectionChangedListener getPostSelectionChangedListener() {
		if (postSelectionChangedListener == null) {
			postSelectionChangedListener = new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					PostMultiPageEditorSite.this.handlePostSelectionChanged(event);
				}
			};
		}
		return postSelectionChangedListener;
	}

	/**
	 * Handles a post selection changed event from the nested editor. The
	 * default implementation gets the selection provider from the multi-page
	 * editor's site, and calls <code>firePostSelectionChanged</code> on it
	 * (only if it is an instance of <code>MultiPageSelectionProvider</code>),
	 * passing a new event object.
	 * <p>
	 * Subclasses may extend or reimplement this method.
	 * </p>
	 * 
	 * @param event
	 *            the event
	 */
	protected void handlePostSelectionChanged(SelectionChangedEvent event) {
		ISelectionProvider parentProvider = getMultiPageEditor().getSite().getSelectionProvider();
		if (parentProvider instanceof PostMultiPageSelectionProvider) {
			SelectionChangedEvent newEvent = new SelectionChangedEvent(parentProvider, event.getSelection());
			((PostMultiPageSelectionProvider) parentProvider).firePostSelectionChanged(newEvent);
		}
	}

	/**
	 * The <code>MultiPageEditorSite</code> implementation of this
	 * <code>IWorkbenchPartSite</code> method remembers the selection
	 * provider, and also hooks a listener on it, which calls
	 * <code>handleSelectionChanged</code> when a selection changed event
	 * occurs and <code>handlePostSelectionChanged</code> when a post
	 * selection changed event occurs.
	 * 
	 * @param provider
	 *            The selection provider.
	 * @see PostMultiPageEditorSite#handleSelectionChanged(SelectionChangedEvent)
	 */
	public void setSelectionProvider(ISelectionProvider provider) {
		ISelectionProvider oldSelectionProvider = getSelectionProvider();
		if (oldSelectionProvider != null) {
			if (oldSelectionProvider instanceof IPostSelectionProvider) {
				((IPostSelectionProvider) oldSelectionProvider).removePostSelectionChangedListener(getPostSelectionChangedListener());
			}
		}

		super.setSelectionProvider(provider);

		if (provider != null) {
			if (provider instanceof IPostSelectionProvider) {
				((IPostSelectionProvider) provider).addPostSelectionChangedListener(getPostSelectionChangedListener());
			}
		}
	}

}
