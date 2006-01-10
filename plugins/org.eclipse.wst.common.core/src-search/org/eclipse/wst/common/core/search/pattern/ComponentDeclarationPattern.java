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

public class ComponentDeclarationPattern extends ComponentSearchPattern
{

	public ComponentDeclarationPattern(QualifiedName name,
			QualifiedName metaName, int matchRule)
	{
		super(null, name, metaName, matchRule);

	}

	public ComponentDeclarationPattern(QualifiedName name,
			QualifiedName metaName)
	{
		super(null, name, metaName);
	}

}
