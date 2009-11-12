package org.eclipse.wst.common.snippets.tests.helpers;

import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.common.snippets.core.ISnippetItem;
import org.eclipse.wst.common.snippets.ui.ISnippetInsertion;

public class ComplexInsertion implements ISnippetInsertion {

	public void dragSetData(DragSourceEvent event, ISnippetItem item) {
		// TODO Auto-generated method stub

	}

	public Transfer[] getTransfers() {
		// TODO Auto-generated method stub
		return new Transfer[0];
	}

	public void insert(IEditorPart editorPart) {
		// TODO Auto-generated method stub

	}

	public void setEditorPart(IEditorPart targetPart) {
		// TODO Auto-generated method stub

	}

	public void setItem(ISnippetItem item) {
		// TODO Auto-generated method stub

	}

}
