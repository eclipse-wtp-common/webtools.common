/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.ui.provisional.editors;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.util.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.MultiPageSelectionProvider;

public class PostMultiPageSelectionProvider extends MultiPageSelectionProvider implements IPostSelectionProvider {
	private ListenerList postListeners;

	public PostMultiPageSelectionProvider(MultiPageEditorPart multiPageEditor) {
		super(multiPageEditor);
		postListeners = new ListenerList();
	}

	public void addPostSelectionChangedListener(ISelectionChangedListener listener) {
		postListeners.add(listener);
	}

	public void firePostSelectionChanged(final SelectionChangedEvent event) {
		Object[] listeners = postListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			final ISelectionChangedListener pl = (ISelectionChangedListener) listeners[i];
			Platform.run(new SafeRunnable() {
				public void run() {
					pl.selectionChanged(event);
				}
			});
		}
	}

	public void removePostSelectionChangedListener(ISelectionChangedListener listener) {
		postListeners.remove(listener);
	}
}
