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
package org.eclipse.wst.common.snippets.internal.palette;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.palette.PaletteCustomizer;
import org.eclipse.gef.ui.palette.customize.PaletteCustomizationAction;
import org.eclipse.gef.ui.palette.customize.PaletteCustomizerDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.common.snippets.core.ISnippetCategory;
import org.eclipse.wst.common.snippets.internal.SnippetDefinitions;
import org.eclipse.wst.common.snippets.internal.SnippetsMessages;
import org.eclipse.wst.common.snippets.internal.SnippetsPluginImageHelper;
import org.eclipse.wst.common.snippets.internal.SnippetsPluginImages;
import org.eclipse.wst.common.snippets.internal.model.SnippetManager;
import org.eclipse.wst.common.snippets.internal.ui.SnippetsCustomizer;

public class SnippetCustomizerDialog extends PaletteCustomizerDialog {

	private class ExportAction extends PaletteCustomizationAction {
		public ExportAction() {
			setEnabled(false);
			setText(SnippetsMessages.SnippetCustomizerDialog_1); //$NON-NLS-1$
			setImageDescriptor(SnippetsPluginImageHelper.getInstance().getImageDescriptor(SnippetsPluginImages.IMG_ELCL_EXPORT));
			setDisabledImageDescriptor(SnippetsPluginImageHelper.getInstance().getImageDescriptor(SnippetsPluginImages.IMG_DLCL_EXPORT));
			setHoverImageDescriptor(SnippetsPluginImageHelper.getInstance().getImageDescriptor(SnippetsPluginImages.IMG_CLCL_EXPORT));
		}

		protected void handleExport() {
			PaletteDrawer exportCategory = (PaletteDrawer) getSelectedPaletteEntry();

			final FileDialog fileDialog = new FileDialog(getShell());
			fileDialog.setFileName("snippets.xml"); //$NON-NLS-1$
			String[] filterExtensions = new String[2];
			filterExtensions[0] = "*.xml"; //$NON-NLS-1$
			filterExtensions[1] = "*.*"; //$NON-NLS-1$
			fileDialog.setFilterExtensions(filterExtensions);
			String filename = fileDialog.open();
			if (filename != null) {
				SnippetDefinitions definitions = ModelFactoryForUser.getInstance().load(filename);
				ISnippetCategory existingCategory = definitions.getCategory(exportCategory.getId());

				if (existingCategory == null)
					definitions.getCategories().add(exportCategory);
				else {
					String title = SnippetsMessages.SnippetCustomizerDialog_2; //$NON-NLS-1$
					String message = NLS.bind(SnippetsMessages.SnippetCustomizerDialog_4, new String[]{existingCategory.getLabel()});
					boolean answer = MessageDialog.openConfirm(getShell(), title, message);
					if (answer) {
						definitions.getCategories().remove(existingCategory);
						definitions.getCategories().add(exportCategory);
					}
				}

				OutputStream outputStream = null;
				try {
					outputStream = new FileOutputStream(filename);
					new UserModelDumper().write(definitions, outputStream);
				}
				catch (FileNotFoundException e) {
					// should not have problem finding the output file
					e.printStackTrace();
				}
				finally {
					if (outputStream != null)
						try {
							outputStream.close();
						}
						catch (IOException e) {
							// should not have problem closing the output file
							e.printStackTrace();
						}
				}

				updateActions();
			}
		}

		public void run() {
			handleExport();
		}

		public void update() {
			boolean enabled = false;
			PaletteEntry entry = getSelectedPaletteEntry();
			if (entry != null) {
				if (getCustomizer() instanceof SnippetsCustomizer)
					enabled = ((SnippetsCustomizer) getCustomizer()).canExport(entry);
			}
			setEnabled(enabled);
		}

	}

	private class ImportAction extends PaletteCustomizationAction {
		public ImportAction() {
			setEnabled(false);
			setText(SnippetsMessages.SnippetCustomizerDialog_0); //$NON-NLS-1$
			setImageDescriptor(SnippetsPluginImageHelper.getInstance().getImageDescriptor(SnippetsPluginImages.IMG_ELCL_IMPORT));
			setDisabledImageDescriptor(SnippetsPluginImageHelper.getInstance().getImageDescriptor(SnippetsPluginImages.IMG_DLCL_IMPORT));
			setHoverImageDescriptor(SnippetsPluginImageHelper.getInstance().getImageDescriptor(SnippetsPluginImages.IMG_CLCL_IMPORT));
		}

		protected PaletteContainer determineContainerForNewEntry(PaletteEntry selected) {
			if (selected instanceof PaletteContainer)
				return (PaletteContainer) selected;
			return selected.getParent();
		}

		protected void handleImport() {
			final FileDialog fileDialog = new FileDialog(getShell());
			fileDialog.setFileName("snippets.xml"); //$NON-NLS-1$
			String[] filterExtensions = new String[2];
			filterExtensions[0] = "*.xml"; //$NON-NLS-1$
			filterExtensions[1] = "*.*"; //$NON-NLS-1$
			fileDialog.setFilterExtensions(filterExtensions);
			String filename = fileDialog.open();
			if (filename != null) {
				SnippetDefinitions definitions = ModelFactoryForUser.getInstance().load(filename);
				List importCategories = definitions.getCategories();
				List currentCategories = SnippetManager.getInstance().getDefinitions().getCategories();
				PaletteEntry lastImportEntry = null;

				for (int i = 0; i < importCategories.size(); i++) {
					boolean found = false;
					for (int j = 0; j < currentCategories.size(); j++) {
						if (((PaletteEntry) currentCategories.get(j)).getId().compareToIgnoreCase((((PaletteEntry) importCategories.get(i))).getId()) == 0) {
							String title = SnippetsMessages.SnippetCustomizerDialog_2; //$NON-NLS-1$
							String message = NLS.bind(SnippetsMessages.SnippetCustomizerDialog_3, new String[]{((PaletteEntry) currentCategories.get(j)).getLabel()});
							boolean answer = MessageDialog.openConfirm(getShell(), title, message);
							if (answer) {
								SnippetManager.getInstance().getPaletteRoot().remove((PaletteEntry) currentCategories.get(j));
								SnippetManager.getInstance().getPaletteRoot().add((PaletteEntry) importCategories.get(i));
								lastImportEntry = (PaletteEntry) importCategories.get(i);
							}

							found = true;
							break;
						}
					}
					if (!found) {
						SnippetManager.getInstance().getPaletteRoot().add((PaletteEntry) importCategories.get(i));
						lastImportEntry = (PaletteEntry) importCategories.get(i);
					}
				}
				if (lastImportEntry != null)
					fTreeviewer.setSelection(new StructuredSelection(lastImportEntry), true);

				updateActions();
			}
		}

		public void run() {
			handleImport();
		}

		public void update() {
			boolean enabled = false;
			PaletteEntry entry = getSelectedPaletteEntry();
			if (entry != null) {
				if (getCustomizer() instanceof SnippetsCustomizer)
					enabled = ((SnippetsCustomizer) getCustomizer()).canImport(entry);
			}
			setEnabled(enabled);
		}
	}

	private TreeViewer fTreeviewer = null;

	public SnippetCustomizerDialog(Shell shell, PaletteCustomizer customizer, PaletteRoot root) {
		super(shell, customizer, root);
	}

	protected List createOutlineActions() {
		List actions = super.createOutlineActions();

		actions.add(new ImportAction());
		actions.add(new ExportAction());

		return actions;
	}

	protected TreeViewer createOutlineTreeViewer(Composite composite) {
		fTreeviewer = super.createOutlineTreeViewer(composite);

		return fTreeviewer;
	}

	public int open() {
		// save the current state before open
		save();

		return super.open();
	}
}