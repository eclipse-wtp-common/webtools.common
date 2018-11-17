/*******************************************************************************
 * Copyright (c) 2009 by SAP AG, Walldorf. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    SAP AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.snippets.tests.providers;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.common.snippets.internal.SnippetsPlugin;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteDrawerFactory;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteItem;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteRoot;
import org.eclipse.wst.common.snippets.internal.ui.SnippetsView;
import org.eclipse.wst.common.snippets.ui.TextSnippetProvider;

public class TextProviderTests extends TestCase {
	
	private IFile file;

	protected void setUp() throws Exception {
		super.setUp();
		String projectName = System.currentTimeMillis() + "";
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if (!project.exists()){
			project.create(new NullProgressMonitor());
		}
		if (!project.isOpen()){
			project.open(new NullProgressMonitor());
		}
		file = project.getFile("testTextSnippet.txt");
		if (!file.exists()){
			ByteArrayInputStream sr = new ByteArrayInputStream("test Text Snippet Provider".getBytes());
			file.create(sr, true, new NullProgressMonitor());
		}
	}

	public void testTextSnippetCreation() throws Exception {
		SnippetsView view;
		SnippetPaletteRoot anchor = null;
		view = (SnippetsView) SnippetsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(SnippetsPlugin.NAMES.VIEW_ID);
		if (view == null) {
			view = (SnippetsView) SnippetsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(SnippetsPlugin.NAMES.VIEW_ID);
		}
		if (view != null) {
			anchor = view.getRoot();
		}
		Shell activeShell = SnippetsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(); //Display.getDefault().getActiveShell();
		assertNotNull("no active shell", activeShell);
		PaletteDrawer drawer = (PaletteDrawer) new SnippetPaletteDrawerFactory().createNewEntry(activeShell, anchor);
		drawer.setLabel("testName");
		TextSnippetProvider textSnippetProvider = new TextSnippetProvider();
		IEditorPart openEditor = IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), file);
		assertNotNull("no editor opened", openEditor);
		textSnippetProvider.setEditor(openEditor);
		
		
		SnippetPaletteItem createSnippet = textSnippetProvider.createSnippet(drawer);
		assertNotNull("no SnippetPaletteItem created", createSnippet);
		ITextEditor editor = (ITextEditor) openEditor;
		editor.selectAndReveal(0, 5);
		
		assertFalse("textSnippetProvider action is enabled with no selection", textSnippetProvider.isActionEnabled(null));
		assertTrue("textSnippetProvider action is not enabled with text selection", textSnippetProvider.isActionEnabled(editor.getSelectionProvider().getSelection()));
		editor.selectAndReveal(0, 0);
		assertFalse("textSnippetProvider action is not enabled with text selection", textSnippetProvider.isActionEnabled(editor.getSelectionProvider().getSelection()));
	}
}
