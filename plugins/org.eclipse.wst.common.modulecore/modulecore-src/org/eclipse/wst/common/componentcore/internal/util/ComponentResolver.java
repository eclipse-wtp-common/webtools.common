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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.ModulecorePlugin;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverExtension;
import org.osgi.framework.Bundle;

public class ComponentResolver implements URIResolverExtension {
	private static boolean _DEBUG = "true".equals(Platform.getDebugOption("org.eclipse.wst.common.modulecore/ComponentResolver")); //$NON-NLS-1$ //$NON-NLS-2$ 
	private static final String FILE_PROTOCOL = "file:///"; //$NON-NLS-1$
	private static final String FILE_PROTOCOL2 = "file://"; //$NON-NLS-1$
	private static final String ROOT_PATH_STRING = Path.ROOT.toString(); //$NON-NLS-1$
	private static final String HTTP_PROTOCOL = "http:"; //$NON-NLS-1$

	/**
	 * Various resolvers disagree on how many preceding slashes should
	 * actually be used. On Win32, 2 slashes results in a URL object where the
	 * volume is stripped out of the file path as the host name, but on Unix 2
	 * is the correct number. On Win32, java.io.File.toURL adds only 1 slash,
	 * and on Unix it adds 2.
	 * 
	 * @param uri
	 * @return The IFile for this file location
	 */
	private IFile recalculateFile(String uri) {
		IFile file = null;
		String location = null;

		long time0 = -1;
		if (_DEBUG)
			time0 = System.currentTimeMillis();
		if (uri.startsWith(HTTP_PROTOCOL)) {
			IFile files[] = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(URI.create(uri));
			for (int i = 0; i < files.length && file == null; i++) {
				if (files[i].isAccessible()) {
					file = files[i];
				}
			}
			if (_DEBUG) {
				System.out.println("\"" + uri + "\" findFilesForLocationURI:" + (System.currentTimeMillis() - time0));
				time0 = System.currentTimeMillis();
			}
		}
		else {
			if (uri.startsWith(FILE_PROTOCOL)) {
				location = uri.substring(FILE_PROTOCOL.length());
			}
			else if (uri.startsWith(FILE_PROTOCOL2)) {
				location = uri.substring(FILE_PROTOCOL2.length());
			}
			else {
				location = uri;
			}
			IPath path = new Path(location);
			IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(path);
			for (int i = 0; i < files.length && file == null; i++) {
				if (files[i].isAccessible()) {
					file = files[i];
				}
			}
		}
		if (_DEBUG)
			System.out.println("\"" + location + "\" findFilesForLocation:" + (System.currentTimeMillis() - time0));
		return file;
	}

	public String resolve(IFile file, String baseLocation, String publicId, String systemId) {
		if (_DEBUG) {
			System.out.print("ComponentResolver: resolve \"[{" + publicId + "}{" + systemId + "}]\" from \"" + baseLocation + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
		// argument sanity checks
		/*
		 * Check for a system reference; without one, there's no point in
		 * continuing (we can't resolve just a public identifier).
		 */
		if (systemId == null || systemId.length() == 0) {
			if (_DEBUG) {
				System.out.println(" (no system reference)"); //$NON-NLS-1$
			}
			return null;
		}

		/* Recompute the IFile, if needed, from the base location. */
		if (file == null) {
			if (baseLocation == null || baseLocation.length() == 0 || baseLocation.startsWith("wbit:")) { //$NON-NLS-1$
				/*
				 * We can't proceed if we lack both an IFile and a system
				 * reference
				 */
				if (_DEBUG) {
					System.out.println(" (no base location or file given)"); //$NON-NLS-1$
				}
				return null;
			}
			file = recalculateFile(baseLocation);
		}

		/*
		 * If a workspace IFile is (still) not the base point of reference,
		 * don't continue.
		 */
		if (file == null) {
			if (_DEBUG) {
				System.out.println(" (not a workspace file)"); //$NON-NLS-1$
			}
			return null;
		}

		/* Check for an absolute URL as the system reference. */
		try {
			URL testURL = new URL(systemId);
			if (testURL != null) {
				if (_DEBUG) {
					System.out.println(" (reference is already a URL)"); //$NON-NLS-1$
				}
				return null;
			}
		}
		catch (MalformedURLException e) {
			// Continue resolving
		}

		/* Check for a URI without a scheme, but with a host */
		try
		{
			URI uri = new URI(systemId);
			if ((uri.getScheme() == null) && (uri.getHost() != null))
			{
				return null;
			}
		}
		catch (URISyntaxException use)
		{
			// do nothing- we need to check to see if this is a local file
		}

		/* Check for a system file name as the system reference. */
		IPath systemPath = new Path(systemId);
		if (systemPath.toFile().exists()) {
			if (_DEBUG) {
				System.out.println(" (reference is a system file)"); //$NON-NLS-1$
			}
			return null;
		}

		boolean prependFilePrefix = baseLocation.startsWith(FILE_PROTOCOL) && baseLocation.length() > 7;
		boolean prependFilePrefix2 = baseLocation.startsWith(FILE_PROTOCOL2) && baseLocation.length() > 8;

		String resolvedPath = null;

		IVirtualResource[] virtualResources = null;
		try {
			virtualResources = ComponentCore.createResources(file);
		}
		catch (Exception e) {
			Status statusObj = new Status(IStatus.ERROR, ModulecorePlugin.PLUGIN_ID, IStatus.ERROR, "Exception calling ComponentCore.createResources()", e);
			Bundle bundle = Platform.getBundle(ModulecorePlugin.PLUGIN_ID);
			if (bundle != null) {
				Platform.getLog(bundle).log(statusObj);
			}
		}

		// Only return results for Flexible projects
		if (virtualResources != null) {
			for (int i = 0; i < virtualResources.length && resolvedPath == null; i++) {
				IPath resolvedRuntimePath = null;
				if (systemId.startsWith(ROOT_PATH_STRING)) {
					resolvedRuntimePath = new Path(systemId);
				}
				else {
					resolvedRuntimePath = new Path(virtualResources[i].getRuntimePath().removeLastSegments(1).append(systemId).toString());
				}
				IVirtualFile virtualFile = ComponentCore.createFile(file.getProject(), resolvedRuntimePath);
				IFile resolvedFile = null;
				if (virtualFile.getWorkspaceRelativePath().segmentCount() > 1) {
					resolvedFile = virtualFile.getUnderlyingFile();
				}
				if (resolvedFile != null && resolvedFile.getLocation() != null) {
					if (prependFilePrefix) {
						resolvedPath = FILE_PROTOCOL + resolvedFile.getLocation().toString();
					}
					else if (prependFilePrefix2) {
						resolvedPath = FILE_PROTOCOL2 + resolvedFile.getLocation().toString();
					}
					else {
						resolvedPath = resolvedFile.getLocation().toString();
					}
				}
			}
		}
		else {
			if (_DEBUG) {
				System.out.println(" (not in flexible project)"); //$NON-NLS-1$
			}
		}
		if (_DEBUG) {
			System.out.println(" -> \"" + resolvedPath + "\""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return resolvedPath;
	}
}
