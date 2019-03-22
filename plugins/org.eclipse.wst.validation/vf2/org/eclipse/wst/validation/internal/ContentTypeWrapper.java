/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

/**
 * Since it can be expense to determine a content type, we provide a wrapper so that we only
 * need to get it once, as we validate a resource.
 * @author karasiuk
 *
 */
public class ContentTypeWrapper {
	
	private IContentType _type;
	private boolean 	_initialized;
	
	public IContentType getContentType(IFile file){
		if (_initialized)return _type;
		
		IContentDescription cd = null;
		try {
			cd = file.getContentDescription();
		}
		catch (CoreException e){
			try {
				file.refreshLocal(IResource.DEPTH_ZERO, null);
				cd = file.getContentDescription();
			}
			catch (CoreException e2){
				if (Tracing.isLogging())ValidationPlugin.getPlugin().handleException(e2);
			}
		}
		if (cd == null)return null;
		_type = cd.getContentType();
		_initialized = true;
		return _type;
	}

}
