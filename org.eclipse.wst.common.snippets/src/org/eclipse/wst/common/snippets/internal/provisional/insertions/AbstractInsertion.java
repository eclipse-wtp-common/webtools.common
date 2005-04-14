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
package org.eclipse.wst.common.snippets.internal.provisional.insertions;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.common.snippets.internal.Logger;
import org.eclipse.wst.common.snippets.internal.provisional.ISnippetItem;
import org.eclipse.wst.common.snippets.internal.provisional.ISnippetsInsertion;


/**
 * An abstract insertion class that understands placing the snippet's content
 * string into a text editor.
 */
public abstract class AbstractInsertion implements ISnippetsInsertion {
	private IEditorPart activeEditorPart = null;

	private ISnippetItem fItem = null;
	private Transfer[] supportedTransfers = null;

	/**
	 * default constructor
	 */
	public AbstractInsertion() {
		super();
	}

	/**
	 * @return An array of Transfer objects supported by this insertion for
	 *         Drag and Drop.
	 */
	protected Transfer[] createTransfers() {
		return new Transfer[]{TextTransfer.getInstance()};
	}

	/**
	 * @param part
	 * @param textEditor
	 * @param document
	 * @param textSelection
	 * @throws BadLocationException
	 */
	protected void doInsert(IEditorPart part, ITextEditor textEditor, IDocument document, ITextSelection textSelection) throws BadLocationException {
		String replacement = getInsertString(part.getEditorSite().getShell());
		if (replacement != null && (replacement.length() > 0 || textSelection.getLength() > 0)) {
			document.replace(textSelection.getOffset(), textSelection.getLength(), replacement);
		}
	}

	/**
	 * Gets the activeEditorPart.
	 * 
	 * @return the active IEditorPart
	 */
	public IEditorPart getActiveEditorPart() {
		return activeEditorPart;
	}

	/**
	 * Return the string intended to be inserted; used by double-click
	 * behavior
	 * 
	 * @param host a shell from which UI elements may be opened to help
	 *            determine what String to return
	 * 
	 * @return the String to be inserted
	 */
	public String getInsertString(Shell host) {
		return ""; //$NON-NLS-1$
	}


	/**
	 * Gets the Item.
	 * 
	 * @return the ISnippetItem
	 */
	public ISnippetItem getItem() {
		return fItem;
	}

	public Transfer[] getTransfers() {
		if (supportedTransfers == null)
			supportedTransfers = createTransfers();
		return supportedTransfers;
	}

	/**
	 * Applies the current ISnippetItem to the given IEditorPart
	 */
	public void insert(IEditorPart editorPart) {
		if (editorPart == null)
			return;
		if (editorPart instanceof ITextEditor) {
			// find the text widget, its Document, and the current selection
			ITextEditor editor = (ITextEditor) editorPart;
			if (editor.isEditable()) {
				IDocumentProvider docprovider = editor.getDocumentProvider();
				ISelectionProvider selprovider = editor.getSelectionProvider();
				if (docprovider != null && selprovider != null) {
					IDocument document = docprovider.getDocument(editorPart.getEditorInput());
					ISelection selection = selprovider.getSelection();
					if (document != null && selection != null && selection instanceof ITextSelection) {
						ITextSelection textSel = (ITextSelection) selection;
						try {
							doInsert(editorPart, editor, document, textSel);
						}
						catch (Exception t) {
							Logger.logException("Could not insert " + getItem().getId(), t); //$NON-NLS-1$
							editor.getSite().getShell().getDisplay().beep();
						}
					}
				}
			}
		}
	}

	/**
	 * Sets the activeEditorPart.
	 * 
	 * @param activeEditorPart The activeEditorPart to set
	 */
	public void setActiveEditorPart(IEditorPart newActiveEditorPart) {
		this.activeEditorPart = newActiveEditorPart;
	}

	/**
	 * Sets the fItem.
	 * 
	 * @param fItem The ISnippetItem to use
	 */
	public void setItem(ISnippetItem item) {
		this.fItem = item;
	}
}