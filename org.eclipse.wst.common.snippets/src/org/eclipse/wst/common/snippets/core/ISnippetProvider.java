/*******************************************************************************
 * Copyright (c) 2009 by SAP AG, Walldorf. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.snippets.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.common.snippets.internal.editors.ISnippetEditor;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteItem;
import org.eclipse.wst.common.snippets.ui.ISnippetInsertion;

/**
 * ISnippetProvider presents the possibility for plug-in developers to develop
 * logic that can take advantage of the snippet concept. Thus allowing the 
 * end user to apply frequently reusable content.
 * 
 * Using AbstractSnippetProveider is recommended where most of the methods 
 * have default implementation.
 * @author Dimitar Giormov
 */
public interface ISnippetProvider {
	
	/**
	 * Implementors should create new SnippetPaletteItem instance.
	 * Using AbstractSnippetProveider is recommended where this method is implemented.
	 * 
	 * @param drawer
	 * @return
	 * @throws CoreException
	 */
	public SnippetPaletteItem createSnippet(PaletteEntry drawer) throws CoreException ;
	
	/**
	 * Specifies if the Add to Snippets action will be available 
	 * on the current selection. 
	 * 
	 * @param selection
	 * @return true if the Add to Snippets action will be available in the popup menu.
	 */
	public boolean isActionEnabled(ISelection selection);

	/**
	 * Saves additional content is special designated folder.
	 * 
	 * @param path - where the resources can be saved.
	 * @return status of the operation.
	 */
	public IStatus saveAdditionalContent(IPath path);
	
	/**
	 * Returns snippet insertion class for reproducing the saved content in the editor.
	 * 
	 * @return
	 */
	public ISnippetInsertion getSnippetInsertion();
	
	/**
	 * UID for the provider.
	 * 
	 * @return
	 */
	public String getId();
	
	/**
	 * Snippet enabled editor, on which the snippet will be applied or taken from.
	 * 
	 * @param editor
	 */
	public void setEditor(IEditorPart editor);
	
	/**
	 * Returns custom snippet editor, which will be shown in customize dialog
	 * User can return the VariableItemEditor if variables will be reused.
	 * Null if no editor is necessary.
	 * Or custom editor.
	 * 
	 * @return
	 */
	public ISnippetEditor getSnippetEditor();
	
}
