/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.core.search.pattern;

import org.eclipse.core.resources.IFile;

public class FileReferencePattern extends SearchPattern
{

	IFile file;

	public FileReferencePattern(IFile file)
	{
		this.file = file;
	}

	public FileReferencePattern(IFile file, int matchRule)
	{
		super(matchRule);
		this.file = file;
	}

}
