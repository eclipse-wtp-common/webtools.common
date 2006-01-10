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

public class Entry
{
  String key;
  String category;

	public Entry()
{
	super();
	
}

	public String getCategory()
	{
		return category;
	}

	

	public String getKey()
	{
		return key;
	}

	public void setCategory(String category)
	{
		this.category = category;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	
}
