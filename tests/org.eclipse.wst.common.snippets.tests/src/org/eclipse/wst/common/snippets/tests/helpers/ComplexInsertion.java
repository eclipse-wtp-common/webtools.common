/*******************************************************************************
 * Copyright (c) 2009 by SAP AG, Walldorf. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    SAP AG - initial API and implementation
 *******************************************************************************/
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
