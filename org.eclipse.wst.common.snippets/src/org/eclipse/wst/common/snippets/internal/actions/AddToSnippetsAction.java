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
package org.eclipse.wst.common.snippets.internal.actions;

import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.ui.palette.customize.PaletteCustomizerDialog;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.common.snippets.internal.IHelpContextIds;
import org.eclipse.wst.common.snippets.internal.Logger;
import org.eclipse.wst.common.snippets.internal.SnippetsMessages;
import org.eclipse.wst.common.snippets.internal.SnippetsPlugin;
import org.eclipse.wst.common.snippets.internal.SnippetsPluginImageHelper;
import org.eclipse.wst.common.snippets.internal.SnippetsPluginImages;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteItem;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteItemFactory;
import org.eclipse.wst.common.snippets.internal.ui.SnippetsView;
import org.eclipse.wst.common.snippets.internal.util.UserDrawerSelector;
import org.eclipse.wst.sse.ui.internal.IExtendedEditorAction;
import org.eclipse.wst.sse.ui.internal.IExtendedSimpleEditor;

/**
 * A contributable IExtendedEditorAction capable of placing the current
 * editor's text selection into the Snippets model
 */

public class AddToSnippetsAction extends Action implements IExtendedEditorAction {
	public static String firstLineOf(String text) {
		if (text == null || text.length() < 1) {
			return text;
		}
		IDocument doc = new Document(text);
		try {
			int lineNumber = doc.getLineOfOffset(0);
			IRegion line = doc.getLineInformation(lineNumber);
			return doc.get(line.getOffset(), line.getLength());
		}
		catch (BadLocationException e) {
			Logger.logException(e);
		}
		return text;
	}

	protected IExtendedSimpleEditor fExtendedSimpleEditor = null;

	public AddToSnippetsAction() {
		super(SnippetsMessages.Add_to_Snippets____3); //$NON-NLS-1$
		setImageDescriptor(SnippetsPluginImageHelper.getInstance().getImageDescriptor(SnippetsPluginImages.IMG_OBJ_SNIPPETS));
		WorkbenchHelp.setHelp(this, IHelpContextIds.MENU_ADD_TO_SNIPPETS);
	}

	/**
	 * @return
	 */
	private String getSelectedText() {
		String selectedText = null;
		try {
			selectedText = fExtendedSimpleEditor.getDocument().get(fExtendedSimpleEditor.getSelectionRange().x, fExtendedSimpleEditor.getSelectionRange().y);
		}
		catch (BadLocationException e) {
			Logger.logException(e);
		}
		return selectedText;
	}

	/**
	 * Prompts the user as needed to obtain a category to contain the new
	 * Snippet
	 * 
	 * @return PaletteDrawer - a user modifiable drawer into which the new
	 *         snippet will be inserted
	 */
	protected PaletteDrawer getUserDrawer() {
		UserDrawerSelector selector = new UserDrawerSelector(fExtendedSimpleEditor.getEditorPart().getEditorSite().getShell());
		PaletteDrawer drawer = selector.getUserDrawer();
		return drawer;
	}

	/**
	 * @inheritdoc
	 * 
	 * @see com.ibm.sse.editor.extension.IExtendedEditorAction#isVisible()
	 */
	public boolean isVisible() {
		return true;
	}

	/**
	 * @inheritdoc
	 * 
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		super.run();
		PaletteDrawer drawer = getUserDrawer();

		if (drawer != null) {
			String selectedText = getSelectedText();

			SnippetPaletteItem item = (SnippetPaletteItem) new SnippetPaletteItemFactory().createNewEntry(fExtendedSimpleEditor.getEditorPart().getSite().getShell(), drawer);
			item.setDescription(""); //$NON-NLS-1$
			item.setContentString(selectedText);

			try {
				IViewPart snippets = fExtendedSimpleEditor.getEditorPart().getEditorSite().getPage().showView(SnippetsPlugin.NAMES.VIEW_ID); //$NON-NLS-1$
				PaletteCustomizerDialog dialog = ((SnippetsView) snippets).getViewer().getCustomizerDialog();
				dialog.setDefaultSelection(item);
				dialog.open();
			}
			catch (PartInitException e1) {
				Logger.logException(e1);
			}
		}
	}

	/**
	 * @inheritdoc
	 */
	public void setActiveExtendedEditor(IExtendedSimpleEditor targetEditor) {
		fExtendedSimpleEditor = targetEditor;
	}

	/**
	 * @inheritdoc
	 * 
	 * @see org.eclipse.ui.texteditor.IUpdate#update()
	 */
	public void update() {
		if (fExtendedSimpleEditor == null) {
			setEnabled(false);
		}
		else {
			setEnabled(fExtendedSimpleEditor.getSelectionRange().y > 0);
		}
	}
}