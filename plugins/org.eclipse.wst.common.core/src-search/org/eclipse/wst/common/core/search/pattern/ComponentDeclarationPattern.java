/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.core.search.pattern;

/**
 *
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still under development and expected to
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
 * (repeatedly) as the API evolves.
 * </p>
 *
 */
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
