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
package org.eclipse.wst.common.snippets.tests.scenarios;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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
import org.eclipse.wst.common.snippets.core.ISnippetsEntry;
import org.eclipse.wst.common.snippets.internal.SnippetsPlugin;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteDrawerFactory;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteItem;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteRoot;
import org.eclipse.wst.common.snippets.internal.ui.SnippetsView;
import org.eclipse.wst.common.snippets.tests.helpers.ComplexProvider;

public class ObjectOrientedSnippetScenario extends TestCase {


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
	
	public void testSave() throws Exception {
		SnippetsView view;
		SnippetPaletteRoot anchor = null;
		view = (SnippetsView) SnippetsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(SnippetsPlugin.NAMES.VIEW_ID);
		if (view == null) {
			view = (SnippetsView) SnippetsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(SnippetsPlugin.NAMES.VIEW_ID);
		}
		if (view != null) {
			anchor = view.getRoot();
		}
		Shell activeShell = SnippetsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();//Display.getDefault().getActiveShell();
		assertNotNull(activeShell);
		PaletteDrawer drawer = (PaletteDrawer) new SnippetPaletteDrawerFactory().createNewEntry(activeShell, anchor);
		drawer.setLabel("testName");
		
		ComplexProvider provider = new ComplexProvider();
		IEditorPart openEditor = IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), file);
		provider.setEditor(openEditor);
		SnippetPaletteItem createSnippet = provider.createSnippet(drawer);
		assertNotNull(createSnippet);
		File f = new File (createSnippet.getStorageLocation().toOSString());
		assertTrue(f.exists());
		assertEquals(1, f.list().length);
		assertEquals(ComplexProvider.TEST_TXT, f.listFiles()[0].getName());
		assertEquals(ComplexProvider.TESTING.trim(), getContents(f.listFiles()[0]).trim());
}	

	public void testProviderBasedSourceType() throws Exception {
		SnippetsView view;
		SnippetPaletteRoot anchor = null;
		view = (SnippetsView) SnippetsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(SnippetsPlugin.NAMES.VIEW_ID);
		if (view == null) {
			view = (SnippetsView) SnippetsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(SnippetsPlugin.NAMES.VIEW_ID);
		}
		if (view != null) {
			anchor = view.getRoot();
		}
		Shell activeShell = SnippetsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();//Display.getDefault().getActiveShell();
		assertNotNull(activeShell);
		PaletteDrawer drawer = (PaletteDrawer) new SnippetPaletteDrawerFactory().createNewEntry(activeShell, anchor);
		drawer.setLabel(getName());
		
		ComplexProvider provider = new ComplexProvider();
		IEditorPart openEditor = IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), file);
		provider.setEditor(openEditor);
		SnippetPaletteItem createSnippet = provider.createSnippet(drawer);
		assertNotNull(createSnippet);
		File f = new File (createSnippet.getStorageLocation().toOSString());
		assertTrue(f.exists());
		assertEquals(1, f.list().length);
		assertEquals(ComplexProvider.TEST_TXT, f.listFiles()[0].getName());
		assertEquals(ComplexProvider.TESTING.trim(), getContents(f.listFiles()[0]).trim());
		
		assertEquals(ISnippetsEntry.SNIPPET_SOURCE_USER, createSnippet.getSourceType());
}	

	
	public String getContents(File aFile) {
		StringBuffer contents = new StringBuffer();

		try {
			BufferedReader input =  new BufferedReader(new FileReader(aFile));
			try {
				String line = null; //not declared within while loop
				while (( line = input.readLine()) != null){
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
				}
			}
			finally {
				input.close();
			}
		}
		catch (IOException ex){
			ex.printStackTrace();
		}

		return contents.toString();
	}
}
