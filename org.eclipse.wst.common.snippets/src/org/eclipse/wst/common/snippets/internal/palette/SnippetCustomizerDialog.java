/*******************************************************************************
 * Copyright (c) 2004, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.snippets.internal.palette;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.runtime.Platform;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.snippets.core.ISnippetCategory;
import org.eclipse.wst.common.snippets.core.ISnippetItem;
import org.eclipse.wst.common.snippets.internal.IHelpContextIds;
import org.eclipse.wst.common.snippets.internal.SnippetDefinitions;
import org.eclipse.wst.common.snippets.internal.SnippetsMessages;
import org.eclipse.wst.common.snippets.internal.SnippetsPlugin;
import org.eclipse.wst.common.snippets.internal.SnippetsPluginImageHelper;
import org.eclipse.wst.common.snippets.internal.SnippetsPluginImages;
import org.eclipse.wst.common.snippets.internal.model.SnippetManager;
import org.eclipse.wst.common.snippets.internal.ui.SnippetsCustomizer;
import org.osgi.framework.Bundle;

public class SnippetCustomizerDialog extends PaletteCustomizerDialog {

	private static class EXPORT_IMPORT_STRATEGY {
		static EXPORT_IMPORT_STRATEGY ARCHIVE = new EXPORT_IMPORT_STRATEGY();
		static EXPORT_IMPORT_STRATEGY XML = new EXPORT_IMPORT_STRATEGY();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.palette.customize.PaletteCustomizerDialog#handleDelete()
	 */
	protected void handleDelete() {
		clearProblem();
		super.handleDelete();
	}

	private class ExportAction extends PaletteCustomizationAction {
		public ExportAction() {
			setEnabled(false);
			setText(SnippetsMessages.SnippetCustomizerDialog_1);
			setImageDescriptor(SnippetsPluginImageHelper.getInstance().getImageDescriptor(SnippetsPluginImages.IMG_ELCL_EXPORT));
			setDisabledImageDescriptor(SnippetsPluginImageHelper.getInstance().getImageDescriptor(SnippetsPluginImages.IMG_DLCL_EXPORT));
			setHoverImageDescriptor(SnippetsPluginImageHelper.getInstance().getImageDescriptor(SnippetsPluginImages.IMG_CLCL_EXPORT));
			PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IHelpContextIds.CUSTOMIZE_EXPORT_BUTTON);
		}

		protected void handleExport() {
			PaletteDrawer exportCategory = (PaletteDrawer) getSelectedPaletteEntry();
			EXPORT_IMPORT_STRATEGY strategy = exportStrategy(exportCategory);

			if (EXPORT_IMPORT_STRATEGY.ARCHIVE == strategy) {
				exportArchive(exportCategory);
			}
			else {
				exportXML(exportCategory);
			}
			updateActions();
		}

		private EXPORT_IMPORT_STRATEGY exportStrategy(PaletteDrawer exportCategory) {
			List children = exportCategory.getChildren();
			for (int i = 0; i < children.size(); i++) {
				ISnippetItem snippetItem = (ISnippetItem) children.get(i);
				File folder = new File(SnippetManager.getInstance().getStorageLocation(snippetItem.getId()).toOSString());
				if (folder.exists()) {
					return EXPORT_IMPORT_STRATEGY.ARCHIVE;
				}
			}
			return EXPORT_IMPORT_STRATEGY.XML;
		}

		private void exportArchive(PaletteDrawer exportCategory) {
			String filename = openFileDialog("*.zip");//$NON-NLS-1$
			if (filename != null) {
				ZipOutputStream outputStream = null;
				try {
					SnippetDefinitions definitions = getCategory(exportCategory, filename);
					outputStream = new ZipOutputStream(new FileOutputStream(filename));
					ZipEntry descriptorFile = new ZipEntry("snippets.xml"); //$NON-NLS-1$
					outputStream.putNextEntry(descriptorFile);
					new UserModelDumper().write(definitions, outputStream);
					ISnippetCategory existingCategory = definitions.getCategory(exportCategory.getId());
					ISnippetItem[] items = existingCategory.getItems();
					for (int i = 0; i < items.length; i++) {
						File folder = new File(SnippetManager.getInstance().getStorageLocation(items[i].getId()).toOSString());
						if (folder.exists()) {
							addToZip(folder.getParentFile(), folder, outputStream);
						}
					}
				}
				catch (FileNotFoundException e) {
					// should not have problem finding the output file
					e.printStackTrace();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				finally {
					if (outputStream != null) {
						try {
							outputStream.close();
						}
						catch (IOException e) {
							// should not have problem closing the output file
							e.printStackTrace();
						}
					}
				}
			}
		}

		private void addToZip(File root, File folder, ZipOutputStream outputStream) throws IOException {
			File[] listedFiles = folder.listFiles();
			for (int i = 0; i < listedFiles.length; i++) {
				if (listedFiles[i].isDirectory()) {
					addToZip(root, listedFiles[i], outputStream);
				}
				else {
					ZipEntry ze = new ZipEntry(listedFiles[i].getAbsolutePath().substring(root.getAbsolutePath().length() + 1));
					outputStream.putNextEntry(ze);
					BufferedInputStream bis = new BufferedInputStream(new FileInputStream(listedFiles[i]), 1024);
					byte[] data = new byte[1024];
					int count;
					while ((count = bis.read(data, 0, 1024)) != -1) {
						outputStream.write(data, 0, count);
					}
					bis.close();
				}
			}
		}

		private void exportXML(PaletteDrawer exportCategory) {
			String filename = openFileDialog("*.xml");//$NON-NLS-1$

			OutputStream outputStream = null;
			if (filename != null) {
				try {
					SnippetDefinitions definitions = getCategory(exportCategory, filename);
					outputStream = new FileOutputStream(filename);
					new UserModelDumper().write(definitions, outputStream);
				}
				catch (FileNotFoundException e) {
					// should not have problem finding the output file
					e.printStackTrace();
				}
				finally {
					if (outputStream != null) {
						try {
							outputStream.close();
						}
						catch (IOException e) {
							// should not have problem closing the output file
							e.printStackTrace();
						}
					}
				}

			}


		}

		private SnippetDefinitions getCategory(PaletteDrawer exportCategory, String fileName) {
			SnippetDefinitions definitions = ModelFactoryForUser.getInstance().load(fileName);
			ISnippetCategory existingCategory = definitions.getCategory(exportCategory.getId());

			if (existingCategory == null)
				definitions.getCategories().add(exportCategory);
			else {
				String title = SnippetsMessages.SnippetCustomizerDialog_2;
				String message = NLS.bind(SnippetsMessages.SnippetCustomizerDialog_4, new String[]{existingCategory.getLabel()});
				boolean answer = MessageDialog.openConfirm(getShell(), title, message);
				if (answer) {
					definitions.getCategories().remove(existingCategory);
					definitions.getCategories().add(exportCategory);
				}
			}
			return definitions;
		}

		private String openFileDialog(String extension) {
			final FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);
			fileDialog.setText(SnippetsMessages.Export_Snippets);
			fileDialog.setFileName("snippets"+ extension.substring(1)); //$NON-NLS-1$
			String[] filterExtensions = new String[2];
			filterExtensions[0] = extension;
			filterExtensions[1] = "*.*"; //$NON-NLS-1$
			fileDialog.setFilterExtensions(filterExtensions);
			String filename = fileDialog.open();
			return filename;
		}

		public void run() {
			handleExport();
		}

		public void update() {
			boolean enabled = false;
			PaletteEntry entry = getSelectedPaletteEntry();
			if (entry != null) {
				if (getCustomizer() instanceof SnippetsCustomizer) {
					enabled = ((SnippetsCustomizer) getCustomizer()).canExport(entry);
				}
			}
			setEnabled(enabled);
		}
	}

	private class ImportAction extends PaletteCustomizationAction {
		public ImportAction() {
			setEnabled(false);
			setText(SnippetsMessages.SnippetCustomizerDialog_0);
			setImageDescriptor(SnippetsPluginImageHelper.getInstance().getImageDescriptor(SnippetsPluginImages.IMG_ELCL_IMPORT));
			setDisabledImageDescriptor(SnippetsPluginImageHelper.getInstance().getImageDescriptor(SnippetsPluginImages.IMG_DLCL_IMPORT));
			setHoverImageDescriptor(SnippetsPluginImageHelper.getInstance().getImageDescriptor(SnippetsPluginImages.IMG_CLCL_IMPORT));
			PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IHelpContextIds.CUSTOMIZE_IMPORT_BUTTON);
		}

		protected void handleImport() {
			final FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
			fileDialog.setText(SnippetsMessages.Import_Snippets);
//			fileDialog.setFileName("snippets.xml"); //$NON-NLS-1$
			String[] filterExtensions = new String[2];
			filterExtensions[0] = "*.xml; *.zip"; //$NON-NLS-1$
			filterExtensions[1] = "*.*"; //$NON-NLS-1$
			fileDialog.setFilterExtensions(filterExtensions);
			String filename = fileDialog.open();
			try {
				if (filename.toLowerCase().endsWith(".zip")) { //$NON-NLS-1$
					ZipFile zip = new ZipFile(new File(filename));
					ZipEntry entry = zip.getEntry("snippets.xml"); //$NON-NLS-1$
					loadMetadata(zip.getInputStream(entry));
					Bundle bundle = Platform.getBundle(SnippetsPlugin.BUNDLE_ID);
					unzip(zip, Platform.getStateLocation(bundle).toOSString());
				}
				else {
					loadMetadata(new FileInputStream(filename));
				}
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			catch (ZipException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private void loadMetadata(InputStream fileInputStream) {
			if (fileInputStream != null) {
				SnippetDefinitions definitions = ModelFactoryForUser.getInstance().load(fileInputStream);
				List importCategories = definitions.getCategories();
				List currentCategories = SnippetManager.getInstance().getDefinitions().getCategories();
				PaletteEntry lastImportEntry = null;

				for (int i = 0; i < importCategories.size(); i++) {
					boolean found = false;
					for (int j = 0; j < currentCategories.size(); j++) {
						if (((PaletteEntry) currentCategories.get(j)).getId().compareToIgnoreCase((((PaletteEntry) importCategories.get(i))).getId()) == 0) {
							String title = SnippetsMessages.SnippetCustomizerDialog_2;
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
				if (lastImportEntry != null) {
					fTreeviewer.setSelection(new StructuredSelection(lastImportEntry), true);
				}

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
				if (getCustomizer() instanceof SnippetsCustomizer) {
					enabled = ((SnippetsCustomizer) getCustomizer()).canImport(entry);
				}
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

	static final void copyInputStream(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[2048];
		int len;

		while ((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);

		in.close();
		out.close();
	}

	void unzip(ZipFile zipFile, String path) throws FileNotFoundException, IOException {
		Enumeration entries;
		entries = zipFile.entries();

		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();
			File file = new File(path + File.separator + entry.getName());
			if (entry.isDirectory()) {
				file.mkdir();
				continue;
			}
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			if (entry.getName().toLowerCase().equals("snippets.xml")) { //$NON-NLS-1$
				continue;
			}

			copyInputStream(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(file)));
		}

		zipFile.close();
	}

}
