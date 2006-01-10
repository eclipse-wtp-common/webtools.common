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

package org.eclipse.wst.common.core.search.document;

import org.eclipse.wst.common.core.search.pattern.QualifiedName;

public class ComponentReferenceEntry extends Entry
{
	QualifiedName name;

	public QualifiedName getName()
	{
		return name;
	}

	public void setName(QualifiedName name)
	{
		this.name = name;
	}
}
