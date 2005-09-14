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

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * Adds posts selection notifications from contained editor parts to
 * listeners. This part was created as a workaround for
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=108324 and will be removed
 * once WTP is building on a platform milestone that includes a fix.
 */
public abstract class PostSelectionMultiPageEditorPart extends MultiPageEditorPart {
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		site.setSelectionProvider(new PostMultiPageSelectionProvider(this));
	}

	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		IEditorPart activeEditor = getEditor(newPageIndex);
		if (activeEditor != null) {
			ISelectionProvider selectionProvider = activeEditor.getSite().getSelectionProvider();
			if (selectionProvider != null) {
				SelectionChangedEvent event = new SelectionChangedEvent(selectionProvider, selectionProvider.getSelection());
				((PostMultiPageSelectionProvider) getSite().getSelectionProvider()).firePostSelectionChanged(event);
			}
		}
	}
}
