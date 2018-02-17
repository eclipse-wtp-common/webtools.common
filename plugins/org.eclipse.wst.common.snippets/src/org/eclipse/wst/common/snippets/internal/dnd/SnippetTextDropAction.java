/*******************************************************************************
 * Copyright (c) 2004, 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.snippets.internal.dnd;



import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.common.snippets.core.ISnippetItem;
import org.eclipse.wst.common.snippets.internal.VariableItemHelper;
import org.eclipse.wst.common.snippets.internal.ui.EntryDeserializer;

public class SnippetTextDropAction {

	protected String getInsertString(Shell host, ISnippetItem item) {
		return VariableItemHelper.getInsertString(host, item, false);
	}


	protected ISnippetItem getItemData(DropTargetEvent event) {
		ISnippetItem item = null;
		if (event.data instanceof byte[]) {
			item = (ISnippetItem) EntryDeserializer.getInstance().fromXML((byte[]) event.data);
		}
		return item;
	}

	/**
	 * @see AbstractDropAction#run(DropTargetEvent, IEditorPart)
	 */
	public boolean run(DropTargetEvent event, IEditorPart targetEditor) {
		boolean success = false;
		if (event.data instanceof byte[]) {
			final ISnippetItem item = getItemData(event);
			final Shell shell = event.display.getActiveShell();
			final IEditorPart editor = targetEditor;
			if (item != null) {
				Runnable inserter = new Runnable() {
					public void run() {
						String insertion = getInsertString(shell, item);
						insert(insertion, editor);
						editor.setFocus();
					}
				};
				shell.getDisplay().asyncExec(inserter);
				success = true;
			}
			else {
				success = insert(new String((byte[]) event.data), targetEditor);
			}
		}
		else if (event.data instanceof String) {
			success = insert((String) event.data, targetEditor);
		}
		return success;
	}
	/*
	 * Replaces targetEditor's current selection by "text"
	 */
	protected boolean insert(String text, IEditorPart targetEditor) {
		if (text == null || text.length() == 0) {
			return true;
		}

		ITextSelection textSelection = null;
		IDocument doc = null;
		ISelection selection = null;

		ITextEditor textEditor = null;
		if (targetEditor instanceof ITextEditor) {
			textEditor = (ITextEditor) targetEditor;
		}
		if (textEditor == null) {
			textEditor = ((IAdaptable) targetEditor).getAdapter(ITextEditor.class);
		}

		if (selection == null && textEditor != null) {
			selection = textEditor.getSelectionProvider().getSelection();
		}
		if (doc == null && textEditor != null) {
			doc = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		}

		if (selection instanceof ITextSelection) {
			textSelection = (ITextSelection) selection;
			try {
				doc.replace(textSelection.getOffset(), textSelection.getLength(), text);
			}
			catch (BadLocationException e) {
				return false;
			}
		}
		if (textEditor != null && textSelection != null) {
			ISelectionProvider sp = textEditor.getSelectionProvider();
			ITextSelection sel = new TextSelection(textSelection.getOffset(), text.length());
			sp.setSelection(sel);
			textEditor.selectAndReveal(sel.getOffset(), sel.getLength());
		}

		return true;
	}

}
