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

import org.eclipse.core.runtime.Platform;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.ui.palette.customize.DefaultEntryPage;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.snippets.core.ISnippetItem;
import org.eclipse.wst.common.snippets.core.ISnippetsEntry;
import org.eclipse.wst.common.snippets.editors.ISnippetEditor;
import org.eclipse.wst.common.snippets.editors.VariableItemEditor;
import org.eclipse.wst.common.snippets.internal.Logger;
import org.eclipse.wst.common.snippets.internal.PluginRecord;
import org.osgi.framework.Bundle;


public class SnippetTemplateEntryPage extends DefaultEntryPage implements ModifyListener {
	public static final Class DEFAULT_EDITOR_CLASS = VariableItemEditor.class;
	protected SnippetsCustomizer customizer = null;
	protected ISnippetEditor editor = null;

	public SnippetTemplateEntryPage(SnippetsCustomizer customizer) {
		this.customizer = customizer;
	}

	public void createControl(Composite parent, PaletteEntry entry) {
		super.createControl(parent, entry);
		editor = getEditor((ISnippetItem) entry);
		if (editor != null) {
			customizer.activeEditors.add(editor);
			editor.addModifyListener(this);
			editor.setItem((ISnippetItem) entry);
			editor.createContents((Composite) getControl());
			// it can't be known in advance since the editor is unknown as
			// well
			((Composite) getControl()).setTabList(null);
		}
	}

	protected ISnippetEditor getEditor(ISnippetItem item) {
		ISnippetEditor editor = null;

		String editorClassName = item.getEditorClassName();

		// ignore the version
		Bundle bundle = null;
		if (item.getSourceType() == ISnippetsEntry.SNIPPET_SOURCE_PLUGINS) {
			PluginRecord pluginRecord = (PluginRecord) item.getSourceDescriptor();
			if (pluginRecord != null) {
				bundle = Platform.getBundle(pluginRecord.getPluginName());
			}
		}

		boolean editorSpecified = editorClassName != null && editorClassName.length() > 0;

		if (editorSpecified) {
			Class theClass = null;
			try {
				if (editorClassName != null && editorClassName.length() > 0) {
					if ( bundle != null && bundle.getState() != Bundle.UNINSTALLED) {
						theClass = bundle.loadClass(editorClassName);
					} else {
						ClassLoader classLoader = getClass().getClassLoader();
						theClass = classLoader != null ? classLoader.loadClass(editorClassName) : Class.forName(editorClassName); 
					}
				}
			}
			catch (ClassNotFoundException e) {
				Logger.logException("Could not load ISnippetEditor class", e); //$NON-NLS-1$
			}
			if (theClass != null) {
				try {
					editor = (ISnippetEditor) theClass.newInstance();
				}
				catch (IllegalAccessException e) {
					Logger.logException("Could not access ISnippetEditor class", e); //$NON-NLS-1$
				}
				catch (InstantiationException e) {
					Logger.logException("Could not instantiate ISnippetEditor class", e); //$NON-NLS-1$
				}
			}
		}

		if (editor == null && !editorSpecified) {
			try {
				editor = (ISnippetEditor) DEFAULT_EDITOR_CLASS.newInstance();
			}
			catch (IllegalAccessException e) {
			}
			catch (InstantiationException e) {
			}
		}
		return editor;
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