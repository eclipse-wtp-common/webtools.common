/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *   IBM - Initial API and implementation
 * /
 *******************************************************************************/
package org.eclipse.wst.validation.internal.ui;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * A helper class for the save dirty files dialog.
 */
public class SaveFilesHelper {

	/**
	 * Retreive an array of IEditorParts representing all the dirty
	 * editors open for the files provided in the list.
	 * 
	 * @param files
	 * 			A list of IFiles.
	 * @return
	 * 			An array of IEditorParts containing all the dirty editors for the files in the list.
	 */
	public static IEditorPart[] getDirtyEditors(List files) {
		Set<IEditorInput> inputs = new HashSet<IEditorInput>();
		List<IEditorPart> result = new LinkedList<IEditorPart>();
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
		for (int i = 0; i < windows.length; i++) {
			IWorkbenchPage[] pages = windows[i].getPages();
			for (int x = 0; x < pages.length; x++) {
				IEditorPart[] editors = pages[x].getDirtyEditors();
				for (int z = 0; z < editors.length; z++) {
					IEditorPart ep = editors[z];
					IEditorInput input = ep.getEditorInput();
					if (input instanceof IFileEditorInput) {
						IFileEditorInput fileInput = (IFileEditorInput) input;
						if (files.contains(fileInput.getFile())) {
							if (!inputs.contains(input)) {
								inputs.add(input);
								result.add(ep);
							}
						}
					}
				}
			}
		}
		return result.toArray(new IEditorPart[result.size()]);
	}
}
