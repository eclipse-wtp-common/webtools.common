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
package org.eclipse.wst.common.snippets.internal.util;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.wst.common.snippets.internal.Logger;


/**
 * NOT API
 */
public class VisibilityUtil {
	public static boolean isContentType(IEditorInput input, String[] filterContentTypes) {
		boolean isMatch = false;
		if (filterContentTypes == null) {
			isMatch = true;
		}
		else if (filterContentTypes.length >= 1) {
			String firstFilter = filterContentTypes[0];
			if (firstFilter.compareTo("*") == 0) //$NON-NLS-1$
				isMatch = true;
			else if (firstFilter.compareTo("!") == 0) //$NON-NLS-1$
				isMatch = false;
			else if (firstFilter.compareTo("-") == 0) //$NON-NLS-1$
				isMatch = false;
			else {
				isMatch = false;
				InputStream contents = null;
				IContentDescription contentDesc = null;
				try {
					// Try to find the IFile so we can use get
					// the content type from its description
					IFile file = (IFile) input.getAdapter(IFile.class);
					if (file == null) {
						Object resource = input.getAdapter(IResource.class);
						if (resource instanceof IFile) {
							file = (IFile) resource;
						}
					}
					if (file != null) {
						// Try the optimized method
						contentDesc = file.getContentDescription();
						if (contentDesc == null) {
							// Dig a little deeper using its
							// contents
							if (file != null) {
								contents = file.getContents();
							}
							else {
								if (input instanceof IStorageEditorInput) {
									IStorage storage = ((IStorageEditorInput) input).getStorage();
									if (storage != null) {
										contents = storage.getContents();
									}
								}
							}
							contentDesc = Platform.getContentTypeManager().getDescriptionFor(contents, input.getName(), null);
							if (contents != null) {
								try {
									contents.close();
									contents = null;
								}
								catch (IOException e) {
									// do nothing, it doesn't
									// matter
									// to us
								}
							}
						}
					}
					boolean findByname = true;
					if (contentDesc != null) {
						IContentType currentContentType = contentDesc.getContentType();
						if (currentContentType != null) {
							findByname = false;
							for (int i = 0; i < filterContentTypes.length; i++) {
								IContentType contentType = Platform.getContentTypeManager().getContentType(filterContentTypes[i]);
								if (contentType != null && currentContentType.isKindOf(contentType)) {
									isMatch = true;
									break;
								}
							}
						}
					}
					if (findByname) {
						IContentType[] contentTypes = Platform.getContentTypeManager().findContentTypesFor(input.getName());
						if (contentTypes == null || contentTypes.length == 0) {
							IWorkbenchAdapter adapter = (IWorkbenchAdapter) input.getAdapter(IWorkbenchAdapter.class);
							if (adapter != null) {
								contentTypes = Platform.getContentTypeManager().findContentTypesFor(adapter.getLabel(input));
							}
						}
						for (int j = 0; j < contentTypes.length; j++) {
							for (int i = 0; i < filterContentTypes.length; i++) {
								String filterContentTypeName = filterContentTypes[i];
								IContentType filterContentType = Platform.getContentTypeManager().getContentType(filterContentTypeName);
								if (filterContentType != null && contentTypes[j].isKindOf(filterContentType)) {
									isMatch = true;
									break;
								}
							}
						}
					}
				}
				catch (IOException e) {
					Logger.logException(e);
				}
				catch (CoreException e) {
					Logger.logException(e);
				}
				finally {
					if (contents != null) {
						try {
							contents.close();
						}
						catch (IOException e) {
							// do nothing, it doesn't matter
							// to us
						}
					}
				}
			}
		}
		return isMatch;
	}
}