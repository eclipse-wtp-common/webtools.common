package org.eclipse.wst.common.snippets.tests.helpers;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.wst.common.snippets.internal.AbstractSnippetProvider;
import org.eclipse.wst.common.snippets.internal.editors.ISnippetEditor;
import org.eclipse.wst.common.snippets.ui.ISnippetInsertion;

public class DummyProvider extends AbstractSnippetProvider {

	public String getId() {
		return "dummy";
	}

	public ISnippetInsertion getSnippetInsertion() {
		return null;
	}

	public boolean isActionEnabled(ISelection selection) {
		return false;
	}

	public IStatus saveAdditionalContent(IPath path) {
		return null;
	}

	public ISnippetEditor getSnippetEditor() {
		// TODO Auto-generated method stub
		return null;
	}


}
