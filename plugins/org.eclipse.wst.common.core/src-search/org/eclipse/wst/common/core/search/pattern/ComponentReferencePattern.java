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

public class ComponentReferencePattern extends ComponentSearchPattern
{

	public ComponentReferencePattern(IFile file, QualifiedName elementQName,
			QualifiedName typeQName, int matchRule)
	{
		super(file, elementQName, typeQName, matchRule);

	}

	public ComponentReferencePattern(IFile file, QualifiedName elementQName,
			QualifiedName typeQName)
	{
		super(file, elementQName, typeQName);

	}

}
