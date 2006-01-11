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

/**
 * This class defines generic component pattern. Usually there are two types of
 * component search patterns: pattern for component declaration and for
 * component definition.
 * 
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still under development and expected to
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
 * (repeatedly) as the API evolves.
 * </p>
 */
public abstract class ComponentSearchPattern extends SearchPattern
{

	QualifiedName name;

	QualifiedName metaName;

	IFile file; // (optional) location where component is defined

	public ComponentSearchPattern(IFile file, QualifiedName elementQName,
			QualifiedName typeQName, int matchRule)
	{
		super(matchRule);
		this.file = file;
		name = elementQName;
		metaName = typeQName;

	}

	public ComponentSearchPattern(IFile file, QualifiedName elementQName,
			QualifiedName typeQName)
	{

		this.file = file;
		name = elementQName;
		metaName = typeQName;

	}

	public IFile getFile()
	{
		return file;
	}

	public QualifiedName getMetaName()
	{
		return metaName;
	}

	public QualifiedName getName()
	{
		return name;
	}

}
