/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.snippets.internal.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.ui.palette.customize.PaletteCustomizerDialog;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.common.snippets.core.ISnippetProvider;
import org.eclipse.wst.common.snippets.internal.Logger;
import org.eclipse.wst.common.snippets.internal.SnippetsPlugin;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteItem;
import org.eclipse.wst.common.snippets.internal.ui.SnippetsView;
import org.eclipse.wst.common.snippets.internal.util.SnippetProviderManager;
import org.eclipse.wst.common.snippets.internal.util.UserDrawerSelector;


public class AddToSnippetsEditorActionDelegate implements IEditorActionDelegate, IViewActionDelegate {

	private IEditorPart fEditorPart;
	private ISnippetProvider snippetProvider;

	public AddToSnippetsEditorActionDelegate() {
		super();
	}

	/**
	 * @deprecated
	 */
	public IDocument getDocument() {
		return getTextEditor().getDocumentProvider().getDocument(fEditorPart.getEditorInput());
	}

	/**
	 * @deprecated
	 */
	protected ITextSelection getSelection() {
		ITextEditor editor = getTextEditor();
		if (editor != null) {
			ISelection selection = editor.getSelectionProvider().getSelection();
			if (selection instanceof ITextSelection) {
				return (ITextSelection) selection;
			}
		}
		return new TextSelection(0, 0);
	}

	/**
	 * @deprecated
	 */
	protected ITextEditor getTextEditor() {
		ITextEditor editor = null;
		IWorkbenchPart activePart = fEditorPart;
		if (activePart == null) {
			activePart = fEditorPart;
		}
		if (activePart instanceof ITextEditor) {
			editor = (ITextEditor) activePart;
		}
		if (editor == null) {
			editor = (ITextEditor) activePart.getAdapter(ITextEditor.class);
		}
		return editor;
	}


	/**
	 * Prompts the user as needed to obtain a category to contain the new
	 * Snippet
	 * 
	 * @return PaletteDrawer - a user modifiable drawer into which the new
	 *         snippet will be inserted
	 */
	protected PaletteDrawer getUserDrawer() {
		UserDrawerSelector selector = new UserDrawerSelector(fEditorPart.getEditorSite().getShell());
		PaletteDrawer drawer = selector.getUserDrawer();
		return drawer;
	}

	public void init(IViewPart view) {
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		PaletteDrawer drawer = getUserDrawer();

		if (drawer != null) {
			if (snippetProvider == null) {
				snippetProvider = SnippetProviderManager.getApplicableProvider(fEditorPart);
				if (snippetProvider == null) {
					return;
				}
			}

			try {
				SnippetPaletteItem item = snippetProvider.createSnippet(drawer);
				IViewPart snippets = fEditorPart.getEditorSite().getPage().showView(SnippetsPlugin.NAMES.VIEW_ID);
				PaletteCustomizerDialog dialog = ((SnippetsView) snippets).getViewer().getCustomizerDialog();
				dialog.setDefaultSelection(item);
				dialog.open();
			}
			catch (PartInitException e) {
				Logger.logException(e);
			}
			catch (CoreException e) {
				Logger.logException(e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection != null && fEditorPart != null && snippetProvider != null) {
			action.setEnabled(snippetProvider.isActionEnabled(selection));
		}
		else {
			action.setEnabled(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorActionDelegate#setActiveEditor(org.eclipse.jface.action.IAction,
	 *      org.eclipse.ui.IEditorPart)
	 */
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		fEditorPart = targetEditor;
		action.setEnabled(fEditorPart != null);
		if (fEditorPart != null) {
			snippetProvider = SnippetProviderManager.getApplicableProvider(fEditorPart);
		}
		else {
			snippetProvider = null;
		}
	}

}
