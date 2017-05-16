/*******************************************************************************
 * Copyright (c) 2009 SAP AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     SAP AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.snippets.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.common.snippets.core.ISnippetProvider;
import org.eclipse.wst.common.snippets.internal.editors.ISnippetEditor;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteItem;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteItemFactory;

/**
 * Default implementation of common methods required by ISnippetProvider
 * 
 * @author Dimitar Giormov
 *
 */
public abstract class AbstractSnippetProvider implements ISnippetProvider {
	
	protected IEditorPart fEditorPart;

	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.snippets.core.ISnippetProvider#setEditor(org.eclipse.ui.IEditorPart)
	 */
	public void setEditor(IEditorPart editor) {
		fEditorPart = editor;

	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.snippets.core.ISnippetProvider#createSnippet(org.eclipse.gef.palette.PaletteEntry)
	 */
	public SnippetPaletteItem createSnippet(PaletteEntry drawer) throws CoreException {
		SnippetPaletteItem snippet = createSnippetMetadata(drawer);
		return snippet;
	}
	
	/**
	 * Creates snippet metadata and saves additional content.
	 * 
	 * 
	 * @param drawer - drawer to which the snippet will be added.
	 * @return
	 * @throws CoreException
	 */
	protected SnippetPaletteItem createSnippetMetadata(PaletteEntry drawer) throws CoreException {
		SnippetPaletteItem item = (SnippetPaletteItem) new SnippetPaletteItemFactory().createNewEntry(fEditorPart.getSite().getShell(), drawer);
//		item.setSourceType(ISnippetsEntry.SNIPPET_SOURCE_PLUGINS);
		item.setProvider(this);
		IStatus status = saveAdditionalContent(item.getStorageLocation());
		if (status!= null && status.getSeverity() == IStatus.ERROR){
			throw new CoreException(status);
		}
		return item;
	}
	

	public ISnippetEditor getSnippetEditor() {
		return null;
	}

}
