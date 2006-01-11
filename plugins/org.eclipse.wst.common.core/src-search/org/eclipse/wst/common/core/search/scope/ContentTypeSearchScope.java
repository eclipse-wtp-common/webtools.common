/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.core.search.scope;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.internal.content.ContentTypeManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;

/**
 * Scope that has filterers files based on content types. It can limit other
 * scopes with the given file content types.
 * 
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still under development and expected to
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
 * (repeatedly) as the API evolves.
 * </p>
 */
public class ContentTypeSearchScope extends SearchScopeImpl
{

	private List validContentIds = new ArrayList();

	public ContentTypeSearchScope(SearchScope scope, String[] validContentTypes)
	{
		if (validContentTypes != null)
		{
			for (int i = 0; i < validContentTypes.length; i++)
			{
				this.validContentIds.add(validContentTypes[i]);
			}
		}
		if (scope.enclosingFiles() != null)
		{
			for (int i = 0; i < scope.enclosingFiles().length; i++)
			{
				IFile file = (IFile) scope.enclosingFiles()[i];
				acceptFile(file);
			}
		}

	}

	protected boolean acceptFile(IFile file)
	{

		if (file == null)
		{
			return false;
		} else
		{
			try
			{
				IContentDescription description = file.getContentDescription();
				if (description != null)
				{
					IContentType contentType = description.getContentType();
					if (contentType != null)
					{
						// TODO use IContentType.isKindOf
						for (Iterator iter = validContentIds.iterator(); iter
								.hasNext();)
						{
							String contentId = (String) iter.next();
							IContentType supportedContentType = ContentTypeManager
									.getInstance().getContentType(contentId);
							if (supportedContentType != null)
							{
								if (contentType.isKindOf(supportedContentType))
								{
									files.add(file);
									projects.add(file.getProject());
									return true;
								}
							}

						}

					}
				}
			} catch (CoreException e)
			{
				// ignore the file
			}
		}
		return false;
	}

}
