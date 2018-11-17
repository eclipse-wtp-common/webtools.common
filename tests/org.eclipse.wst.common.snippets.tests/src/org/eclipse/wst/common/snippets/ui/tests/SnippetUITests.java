/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.common.snippets.ui.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.common.snippets.core.ISnippetItem;
import org.eclipse.wst.common.snippets.internal.SnippetDefinitions;
import org.eclipse.wst.common.snippets.internal.model.SnippetManager;
import org.eclipse.wst.common.snippets.internal.ui.SnippetsView;
import org.eclipse.wst.common.snippets.tests.TestsPlugin;


public class SnippetUITests extends TestCase {
	private SnippetDefinitions fCurrentDefinitions;

	private IFile copyBundleEntryIntoWorkspace(String entryname, String fullPath) {
		IFile file = null;
		URL entry = TestsPlugin.getDefault().getBundle().getEntry(entryname);
		if (entry != null) {
			try {
				byte[] b = new byte[2048];
				InputStream input = entry.openStream();
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				int i = -1;
				while ((i = input.read(b)) > -1) {
					output.write(b, 0, i);
				}
				file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fullPath));
				if (file != null) {
					file.create(new ByteArrayInputStream(output.toByteArray()), true, new NullProgressMonitor());
				}
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return file;
	}

	private IProject createSimpleProject(String name, IPath location, String[] natureIds) {
		IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(name);
		if (location != null) {
			description.setLocation(location);
		}
		if (natureIds != null) {
			description.setNatureIds(natureIds);
		}
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
		try {
			project.create(description, new NullProgressMonitor());
			assertTrue(project.exists());
			project.open(new NullProgressMonitor());
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
		return project;
	}

	private IWorkbenchPage getActivePage() throws WorkbenchException {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			window = PlatformUI.getWorkbench().openWorkbenchWindow(null);
		}
		IWorkbenchPage page = window.getActivePage();
		if (page == null) {
			page = window.openPage(null);
		}
		return page;
	}

	protected void setUp() throws Exception {
		super.setUp();
		fCurrentDefinitions = SnippetManager.getInstance().getDefinitions();
	}

	public void testInsertIntoActiveEditorNoVariables() throws Exception {
		IWorkbenchPage page = getActivePage();
		SnippetsView view = null;
		Object o;
		assertNotNull(o = page.showView("org.eclipse.wst.common.snippets.internal.ui.SnippetsView"));
		view = (SnippetsView) o;
		assertEquals("view part is wrong type", SnippetsView.class.getName(), o.getClass().getName());

		ISnippetItem item = fCurrentDefinitions.getItem("org.eclipse.wst.common.snippets.tests.item0"); //$NON-NLS-1$
		assertNotNull("test item 1 not found", item); //$NON-NLS-1$

		//IProject p = 
			createSimpleProject("testInsertIntoActiveEditor", null, null);
		IFile testFile = copyBundleEntryIntoWorkspace("testfiles/testInsertIntoActiveEditor.txt", "testInsertIntoActiveEditor/testInsertIntoActiveEditor.txt");
		IEditorDescriptor descriptor = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(testFile.getName());
		assertTrue("no default editor found", descriptor != null);
		IEditorInput editorInput = new FileEditorInput(testFile);
		IEditorPart openedEditor = getActivePage().openEditor(editorInput, descriptor.getId(), true);
		assertTrue("no default text editor found", openedEditor instanceof ITextEditor);

		// use setSelectedEntry as that works the drag support
		view.setSelectedEntry(item);

		view.insert();

		IDocument document = ((ITextEditor) openedEditor).getDocumentProvider().getDocument(editorInput);
		assertEquals("resulting text does not match", "sample content 0", document.get());
		openedEditor.doSave(new NullProgressMonitor());

		PlatformUI.getWorkbench().saveAllEditors(false);
	}

	public void testOpenSnippetsView() throws CoreException {
		IWorkbenchPage page = getActivePage();
		Object o;
		assertNotNull(o = page.showView("org.eclipse.wst.common.snippets.internal.ui.SnippetsView"));
		assertEquals("view part is wrong type", SnippetsView.class.getName(), o.getClass().getName());
	}
}
