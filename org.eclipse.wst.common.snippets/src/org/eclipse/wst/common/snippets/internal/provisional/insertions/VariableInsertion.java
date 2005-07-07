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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.common.snippets.internal.Logger;
import org.eclipse.wst.common.snippets.internal.SnippetsPlugin;
import org.eclipse.wst.common.snippets.internal.VariableItemHelper;
import org.eclipse.wst.common.snippets.internal.dnd.VariableTextTransfer;
import org.eclipse.wst.common.snippets.internal.provisional.ISnippetItem;
import org.eclipse.wst.common.snippets.internal.ui.EntrySerializer;
import org.eclipse.wst.common.snippets.internal.util.StringUtils;

/**
 * An insertion implementation that supports ISnippetVariables. The content
 * string of the item can contain markers, in the form ${+variable+}, that
 * will be replaced with user-supplied values at insertion time.
 */
public class VariableInsertion extends AbstractInsertion {

	/**
	 * Default public constructor
	 */
	public VariableInsertion() {
		super();
	}

	protected Transfer[] createTransfers() {
		return new Transfer[]{VariableTextTransfer.getTransferInstance(), TextTransfer.getInstance()};
	}

	public void dragSetData(DragSourceEvent event, ISnippetItem item) {
		boolean isSimpleText = TextTransfer.getInstance().isSupportedType(event.dataType);
		if (isSimpleText) {
			// set variable values to ""
			IWorkbenchWindow window = SnippetsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
			Shell shell = null;
			if (window != null) {
				shell = window.getShell();
			}
			String content = VariableItemHelper.getInsertString(shell, item);
			// String content = item.getContentString();
			// ISnippetVariable[] variables = item.getVariables();
			// for (int i = 0; i < variables.length; i++) {
			// content = StringUtils.replace(content, "${" +
			// variables[i].getName() + '}', ""); //$NON-NLS-1$ //$NON-NLS-2$
			// }
			event.data = content;
		}
		else {
			/*
			 * All complex insertions send an XML encoded version of the item
			 * itself as the data. The drop action must use this to prompt the
			 * user for the correct insertion data
			 */
			event.data = EntrySerializer.getInstance().toXML(item);
		}
	}

	public String getInsertString(Shell host) {
		if (getItem() == null)
			return ""; //$NON-NLS-1$
		String insertString = null;
		ISnippetItem item = getItem();
		if (item.getVariables().length > 0) {
			insertString = VariableItemHelper.getInsertString(host, item);
		}
		else {
			insertString = StringUtils.replace(item.getContentString(), "${cursor}", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return insertString;
	}

	public void insert(IEditorPart editorPart) {
		IEditorPart anEditorPart = editorPart;
		if (anEditorPart == null)
			return;
		if (anEditorPart instanceof ITextEditor) {
			super.insert(editorPart);
		}
		else {
			// MultiPageEditorPart has no accessors for the source EditorPart
			ITextEditor textEditor = null;
			if (anEditorPart instanceof ITextEditor) {
				textEditor = (ITextEditor) anEditorPart;
			}
			else {
				textEditor = (ITextEditor) anEditorPart.getAdapter(ITextEditor.class);
			}
			if (textEditor != null) {
				if (textEditor.isEditable()) {
					IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
					ISelection selection = textEditor.getSelectionProvider().getSelection();
					if (document != null && selection instanceof ITextSelection) {
						ITextSelection textSel = (ITextSelection) selection;
						try {
							doInsert(anEditorPart, textEditor, document, textSel);
						}
						catch (Exception t) {
							Logger.logException("Could not insert " + getItem().getId(), t); //$NON-NLS-1$
							textEditor.getSite().getShell().getDisplay().beep();
						}
					}
				}
			}
			else {
				// any errors here probably aren't really exceptional
				Method getTextEditor = null;
				try {
					getTextEditor = anEditorPart.getClass().getMethod("getTextEditor", new Class[0]); //$NON-NLS-1$
				}
				catch (NoSuchMethodException e) {
					// nothing, not unusual
				}
				Object editor = null;
				if (getTextEditor != null) {
					try {
						editor = getTextEditor.invoke(anEditorPart, new Object[0]);
					}
					catch (IllegalAccessException e) {
						// nothing, not unusual for a non-visible method
					}
					catch (InvocationTargetException e) {
						// nothing, not unusual for a protected implementation
					}
					if (editor instanceof IEditorPart && editor != anEditorPart)
						insert((IEditorPart) editor);
				}
			}
		}
	}

}