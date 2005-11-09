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
package org.eclipse.wst.common.snippets.internal.team;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.common.snippets.core.ISnippetCategory;


/**
 * Describes
 */

public class CategoryFileInfo {
//	private final SnippetDefinitionResourceChangeListener fListener;
	public ISnippetCategory ownerCategory;
	public IFile ownerFile;

	public CategoryFileInfo(SnippetDefinitionResourceChangeListener listener, IFile file, ISnippetCategory category) {
		super();
//		this.fListener = listener;
		ownerFile = file;
		ownerCategory = category;
	}

	public ISnippetCategory getCategory() {
		return ownerCategory;
	}

	public IFile getFile() {
		return ownerFile;
	}
}