/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.util;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverExtension;

public class ComponentResolver implements URIResolverExtension {
	private static boolean _DEBUG = false;
	private static final String FILE_PROTOCOL = "file://";

	public String resolve(IFile file, String baseLocation, String publicId, String systemId) {
		if (_DEBUG) {
			System.out.print(getClass().getName() + ": resolve \"" + systemId + "\" from \"" + baseLocation + "\"");
		}
		if (baseLocation == null || systemId == null || file == null) {
			if (_DEBUG) {
				System.out.println();
			}
			return null;
		}
		try {
			URL testURL = new URL(systemId);
			if (testURL != null) {
				// an absolute system ID, no need to "resolve" it
				if (_DEBUG) {
					System.out.println("reference is a URL");
				}
				return null;
			}
		}
		catch (MalformedURLException e) {
			// Continue resolving
		}

		boolean prependFilePrefix = baseLocation.startsWith(FILE_PROTOCOL) && baseLocation.length() > 7;

		String resolvedPath = null;
		IVirtualResource[] virtualResources = ComponentCore.createResources(file);
		// only return results for Flexible projects
		if (virtualResources != null) {
			for (int i = 0; i < virtualResources.length && resolvedPath == null; i++) {
				IPath resolvedRuntimePath = null;
				if (systemId.startsWith("/")) {
					resolvedRuntimePath = new Path(systemId);
				}
				else {
					resolvedRuntimePath = virtualResources[i].getRuntimePath().removeLastSegments(1).append(systemId);
				}
				IVirtualFile virtualFile = ComponentCore.createFile(file.getProject(), virtualResources[i].getComponent().getName(), resolvedRuntimePath);
				IFile resolvedFile = virtualFile.getUnderlyingFile();
				if (resolvedFile != null && resolvedFile.getLocation() != null) {
					if (prependFilePrefix) {
						resolvedPath = FILE_PROTOCOL + resolvedFile.getLocation().toString();
					}
					else {
						resolvedPath = resolvedFile.getLocation().toString();
					}
				}
			}
		}
		else {
			if (_DEBUG) {
				System.out.print(" (not in flexible project) ");
			}
		}
		if (_DEBUG) {
			System.out.println(" -> \"" + resolvedPath + "\"");
		}
		return resolvedPath;
	}
}
