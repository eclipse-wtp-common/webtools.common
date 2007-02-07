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

/**
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still under development and expected to
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
 * (repeatedly) as the API evolves.
 * </p>
 *
 */
public class FileReferenceEntry extends Entry
{
	String relativeFilePath;
    String resolvedURI;
	String publicIdentifier;

	public String getPublicIdentifier()
	{
		return publicIdentifier;
	}

	public void setPublicIdentifier(String publicIdentifier)
	{
		this.publicIdentifier = publicIdentifier;
	}

	public String getRelativeFilePath()
	{
		return relativeFilePath;
	}

	public void setRelativeFilePath(String relativeFilePath)
	{
		this.relativeFilePath = relativeFilePath;
	}

  public String getResolvedURI()
  {
    return resolvedURI;
  }

  public void setResolvedURI(String resolvedURI)
  {
    this.resolvedURI = resolvedURI;
  }
}
