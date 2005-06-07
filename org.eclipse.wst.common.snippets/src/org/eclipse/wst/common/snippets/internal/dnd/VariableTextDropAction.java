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
package org.eclipse.wst.common.snippets.internal.dnd;



import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.common.snippets.internal.VariableItemHelper;
import org.eclipse.wst.common.snippets.internal.provisional.ISnippetItem;
import org.eclipse.wst.common.snippets.internal.ui.EntryDeserializer;
import org.eclipse.wst.sse.ui.internal.AbstractDropAction;

public class VariableTextDropAction extends AbstractDropAction {

	protected String getInsertString(Shell host, ISnippetItem item) {
		return VariableItemHelper.getInsertString(host, item, false);
	}


	protected ISnippetItem getItemData(DropTargetEvent event) {
		ISnippetItem item = null;
		if (event.data instanceof byte[]) {
			item = (ISnippetItem) EntryDeserializer.getInstance().fromXML((byte[]) event.data);
		}
		return item;
	}

	/**
	 * @see AbstractDropAction#run(DropTargetEvent, IEditorPart)
	 */
	public boolean run(DropTargetEvent event, IEditorPart targetEditor) {
		boolean success = false;
		if (event.data instanceof byte[]) {
			final ISnippetItem item = getItemData(event);
			final Shell shell = event.display.getActiveShell();
			final IEditorPart editor = targetEditor;
			if (item != null) {
				Runnable inserter = new Runnable() {
					public void run() {
						String insertion = getInsertString(shell, item);
						insert(insertion, editor);
						editor.setFocus();
					}
				};
				shell.getDisplay().asyncExec(inserter);
				success = true;
			}
			else {
				success = insert(new String((byte[]) event.data), targetEditor);
			}
		}
		else if (event.data instanceof String) {
			success = insert((String) event.data, targetEditor);
		}
		return success;
	}

}