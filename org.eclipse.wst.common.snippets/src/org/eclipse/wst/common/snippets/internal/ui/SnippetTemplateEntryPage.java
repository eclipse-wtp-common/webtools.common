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
package org.eclipse.wst.common.snippets.internal.ui;

import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.ui.palette.customize.DefaultEntryPage;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.snippets.core.ISnippetsEntry;
import org.eclipse.wst.common.snippets.internal.editors.ISnippetEditor;
import org.eclipse.wst.common.snippets.internal.editors.VariableItemEditor;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteItem;


public class SnippetTemplateEntryPage extends DefaultEntryPage implements ModifyListener {
	public static final Class DEFAULT_EDITOR_CLASS = VariableItemEditor.class;
	protected SnippetsCustomizer snippetsCustomizer = null;
	protected ISnippetEditor editor = null;

	public SnippetTemplateEntryPage(SnippetsCustomizer customizer) {
		this.snippetsCustomizer = customizer;
	}

	public void createControl(Composite parent, PaletteEntry entry) {
		super.createControl(parent, entry);
		editor = getEditor((SnippetPaletteItem) entry);
		if (editor != null) {
			snippetsCustomizer.activeEditors.add(editor);
			editor.addModifyListener(this);
			editor.setItem((SnippetPaletteItem) entry);
			editor.createContents((Composite) getControl());
			// it can't be known in advance since the editor is unknown as
			// well
			((Composite) getControl()).setTabList(null);
		}
	}

	protected ISnippetEditor getEditor(SnippetPaletteItem item) {
		ISnippetEditor snippetEditor = null;
		if (item.getSourceType() != ISnippetsEntry.SNIPPET_SOURCE_PLUGINS) {
			snippetEditor = new VariableItemEditor();
		}
		return snippetEditor;
	}

	public void modifyText(ModifyEvent e) {
		if (editor != null && editor.getItem() != null && editor.getItem().getLabel().trim().length() == 0) {
			getPageContainer().showProblem(""); //$NON-NLS-1$
		}
		else {
			getPageContainer().clearProblem();
		}
	}
}