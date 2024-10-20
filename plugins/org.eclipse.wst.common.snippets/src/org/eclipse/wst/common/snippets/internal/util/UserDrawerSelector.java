/*******************************************************************************
 * Copyright (c) 2004, 2024 IBM Corporation and others.
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
package org.eclipse.wst.common.snippets.internal.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.snippets.core.ISnippetCategory;
import org.eclipse.wst.common.snippets.internal.IHelpContextIds;
import org.eclipse.wst.common.snippets.internal.Logger;
import org.eclipse.wst.common.snippets.internal.SnippetsMessages;
import org.eclipse.wst.common.snippets.internal.SnippetsPlugin;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteDrawerFactory;
import org.eclipse.wst.common.snippets.internal.ui.SnippetsView;

public class UserDrawerSelector {
	protected static class CategoryNameValidator implements IInputValidator {
		public String isValid(String name) {
			// Don't allow blank names
			if (name == null || name.length() < 1) {
				return (SnippetsMessages.A_name_must_be_specified_1);
			}
			return null;
		}
	}

	private String fInputPrompt;
	private String fSelectionPrompt;

	private Shell fShell;

	/**
	 * 
	 */
	public UserDrawerSelector(Shell shell) {
		super();
		fShell = shell;
	}

	/**
	 * @return
	 */
	public String getInputPrompt() {
		return fInputPrompt;
	}

	/**
	 * @return
	 */
	public String getSelectionPrompt() {
		return fSelectionPrompt;
	}

	/**
	 * Returns a user modifiable drawer into which the new snippet will be
	 * inserted
	 * 
	 * @return PaletteDrawer
	 */
	public PaletteDrawer getUserDrawer() {
		// retrieve all of the categories in the Snippets model; then find the
		// ones that are actually user modifiable
		List<ISnippetCategory> categories = SnippetsPlugin.getSnippetManager().getDefinitions().getCategories();
		List<PaletteDrawer> modifiableCategories = new ArrayList<PaletteDrawer>();
		for (int i = 0; i < categories.size(); i++) {
			PaletteDrawer drawer = (PaletteDrawer) categories.get(i);
			if (drawer.getUserModificationPermission() == PaletteEntry.PERMISSION_FULL_MODIFICATION)
				modifiableCategories.add(drawer);
		}

		PaletteDrawer drawer = null;
		if (modifiableCategories.size() >= 1) {
			// find or create a drawer to hold the new snippet
			String[] userDrawers = new String[modifiableCategories.size()];
			for (int i = 0; i < modifiableCategories.size(); i++) {
				userDrawers[i] = modifiableCategories.get(i).getLabel();
			}
			CComboSelectionDialog dlg = new CComboSelectionDialog(fShell, SnippetsMessages.New_Category_Title, (fSelectionPrompt != null ? fSelectionPrompt : SnippetsMessages.choose_or_create), userDrawers, 0, new CategoryNameValidator()) {
				protected Control createDialogArea(Composite parent) {
					Control mainHook = super.createDialogArea(parent);
					PlatformUI.getWorkbench().getHelpSystem().setHelp(mainHook, IHelpContextIds.ADD_TO_SNIPPETS_DIALOG_CATEGORY);
					return mainHook;
				}
			};
			dlg.open();
			if (dlg.getReturnCode() == Window.OK) {
				String drawerName = dlg.getStringValue();
				int selectedDrawer = dlg.getSelectionIndex();
				PaletteEntry anchor = null;
				if (selectedDrawer >= 0) {
					drawer = modifiableCategories.get(selectedDrawer);
				}
				else {
					if (categories.size() > 0) {
						anchor = (PaletteEntry) categories.get(categories.size() - 1);
					}
					drawer = (PaletteDrawer) new SnippetPaletteDrawerFactory().createNewEntry(fShell, anchor);
					drawer.setLabel(drawerName);
					categories.add((ISnippetCategory) drawer);
				}
			}
		}
		else {
			// if none are modifiable, make a category
			PaletteEntry anchor = null;
			if (categories.size() > 0) {
				anchor = (PaletteEntry) categories.get(categories.size() - 1);
			}
			else {
				SnippetsView view;
				try {
					view = (SnippetsView) SnippetsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(SnippetsPlugin.NAMES.VIEW_ID);
					if (view == null) {
						view = (SnippetsView) SnippetsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(SnippetsPlugin.NAMES.VIEW_ID);
					}
					if (view != null) {
						anchor = view.getRoot();
					}
				}
				catch (PartInitException e) {
					Logger.logException(e);
				}
			}
			if (anchor != null) {
				String defaultNewDrawerName = null;
				IEditorPart currentEditor = SnippetsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
				if (currentEditor != null && currentEditor.getEditorInput() != null) {
					defaultNewDrawerName = currentEditor.getEditorInput().getName();
				}
				if (defaultNewDrawerName == null) {
					defaultNewDrawerName = SnippetsMessages.new_category_name;
				}
				InputDialog d = new InputDialog(fShell, SnippetsMessages.New_Category_Title, (fInputPrompt != null ? fInputPrompt : SnippetsMessages.force_create), defaultNewDrawerName, new CategoryNameValidator()) {
					protected Control createContents(Composite parent) {
						Control mainHook = super.createContents(parent);
						PlatformUI.getWorkbench().getHelpSystem().setHelp(mainHook, IHelpContextIds.ADD_TO_SNIPPETS_DIALOG_CATEGORY);
						return mainHook;
					}
				};
				d.open();
				if (d.getReturnCode() == Window.OK) {
					drawer = (PaletteDrawer) new SnippetPaletteDrawerFactory().createNewEntry(fShell, anchor);
					drawer.setLabel(d.getValue());
					categories.add((ISnippetCategory) drawer);
				}
			}
		}
		return drawer;
	}

	/**
	 * @param string
	 */
	public void setInputPrompt(String string) {
		fInputPrompt = string;
	}

	/**
	 * @param string
	 */
	public void setSelectionPrompt(String string) {
		fSelectionPrompt = string;
	}

}
