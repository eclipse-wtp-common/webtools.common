/*******************************************************************************
 * Copyright (c) 2009, 2018 IBM Corporation and others.
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
package org.eclipse.wst.common.snippets.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.common.snippets.core.ISnippetProvider;
import org.eclipse.wst.common.snippets.internal.AbstractSnippetProvider;
import org.eclipse.wst.common.snippets.internal.Logger;
import org.eclipse.wst.common.snippets.internal.editors.ISnippetEditor;
import org.eclipse.wst.common.snippets.internal.editors.VariableItemEditor;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteItem;
import org.eclipse.wst.common.snippets.internal.util.StringUtils;

public class TextSnippetProvider extends AbstractSnippetProvider implements ISnippetProvider {

	public SnippetPaletteItem createSnippet(PaletteEntry drawer) throws CoreException {
		SnippetPaletteItem item = super.createSnippet(drawer);
		ITextSelection selection = getTextSelection();
		try {
			String selectedText = getDocument().get(selection.getOffset(), selection.getLength());			
			item.setDescription(StringUtils.firstLineOf(selectedText).trim() + "..."); //$NON-NLS-1$
			item.setContentString(selectedText);
		}
		catch (BadLocationException e) {
			Logger.logException(e);
		}
		return item;
	}
	

	protected ITextSelection getTextSelection() {
		ITextEditor editor = getTextEditor();
		if (editor != null) {
			ISelection selection = editor.getSelectionProvider().getSelection();
			if (selection instanceof ITextSelection) {
				return (ITextSelection) selection;
			}
		}
		return null;
	}

	protected ITextEditor getTextEditor() {
		ITextEditor editor = null;
		IWorkbenchPart activePart = fEditorPart;
		
		if (activePart instanceof ITextEditor) {
			editor = (ITextEditor) activePart;
		}
		if (editor == null) {
			editor = activePart.getAdapter(ITextEditor.class);
		}
		return editor;
	}

	public boolean isActionEnabled(ISelection selection) {
		boolean enable = false;
		if (selection != null) {
			if (selection instanceof ITextSelection) {
				if (((ITextSelection) selection).getLength() > 0) {
					enable = true;
				}
			}
			else {
				enable = !selection.isEmpty();
			}
		}
		return enable;
	}

	public IDocument getDocument() {
		return getTextEditor().getDocumentProvider().getDocument(fEditorPart.getEditorInput());
	}

	public IStatus saveAdditionalContent(IPath path) {
		return Status.OK_STATUS;
	}


	public ISnippetInsertion getSnippetInsertion() {
		return new DefaultSnippetInsertion();
	}


	public String getId() {
		return TextSnippetProvider.class.getName();
	}


	public ISnippetEditor getSnippetEditor() {
		return new VariableItemEditor();
	}

}
