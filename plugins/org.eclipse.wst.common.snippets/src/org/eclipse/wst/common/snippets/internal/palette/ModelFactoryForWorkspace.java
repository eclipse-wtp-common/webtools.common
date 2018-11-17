/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.snippets.internal.palette;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.snippets.internal.SnippetDefinitions;
import org.eclipse.wst.common.snippets.internal.SnippetsPlugin;
import org.eclipse.wst.common.snippets.internal.util.XMLDocumentProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class ModelFactoryForWorkspace extends ModelFactoryForUser {
	private static ModelFactoryForWorkspace instance = null;

	public static ModelFactoryForWorkspace getWorkspaceInstance() {
		if (instance == null)
			instance = new ModelFactoryForWorkspace();
		return instance;
	}

	public ModelFactoryForWorkspace() {
		super();
	}

	public SnippetDefinitions loadCurrent() {
		return null;
	}

	protected void loadDefinitions(SnippetDefinitions definitions, Node library) {
		NodeList children = library.getChildNodes();
		int length = children.getLength();
		Node child = null;
		for (int i = 0; i < length; i++) {
			child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				if (child.getNodeName().equals(SnippetsPlugin.NAMES.ITEM)) {
					addChild(definitions, (Element) child);
				}
				else if (child.getNodeName().equals(SnippetsPlugin.NAMES.CATEGORY)) {
					addCategory(definitions, (Element) child);
				}
			}
		}
	}

	public SnippetDefinitions loadFrom(IFile input) {
		SnippetDefinitions definitions = new SnippetDefinitions();
		XMLDocumentProvider provider = new XMLDocumentProvider();
		Document document = null;
		try {
			provider.setInputStream(input.getContents());
			document = provider.getDocument();
		}
		catch (CoreException e) {
			// typical of new workspace, don't log it
			document = null;
		}
		if (document == null)
			return definitions;
		Element library = document.getDocumentElement();
		if (library == null || !library.getNodeName().equals(SnippetsPlugin.NAMES.SNIPPETS))
			return definitions;
		loadDefinitions(definitions, library);

		connectItemsAndCategories(definitions);

		return definitions;
	}

}
