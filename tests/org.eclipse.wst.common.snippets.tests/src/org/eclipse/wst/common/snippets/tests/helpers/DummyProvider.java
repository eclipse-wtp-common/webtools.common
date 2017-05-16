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
