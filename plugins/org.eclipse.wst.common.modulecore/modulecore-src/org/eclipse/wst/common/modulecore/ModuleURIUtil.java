/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.modulecore;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;

/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public class ModuleURIUtil {

	public static final String PLATFORM_SCHEME = "platform"; //$NON-NLS-1$
	public static final String RESOURCE_PROTOCOL = "resource"; //$NON-NLS-1$

	/**
	 * <p>
	 * A fully-qualified module URI will contain enough information to determine the deployed name
	 * of the module.
	 * </p>
	 * 
	 * @param aModuleURI
	 *            A valid, fully-qualified module URI
	 * @return The deployed name of the {@see WorkbenchModule}referenced by the module URI
	 * @throws UnresolveableURIException
	 *             If the supplied module URI is invalid or unresolveable.
	 */ 
	public static String getDeployedName(URI aModuleURI) throws UnresolveableURIException {
		ensureValidFullyQualifiedModuleURI(aModuleURI);
		return aModuleURI.segment(ModuleCore.ModuleURI.MODULE_NAME_INDX);
	}

	public static boolean ensureValidFullyQualifiedModuleURI(URI aModuleURI) throws UnresolveableURIException {
		return ensureValidFullyQualifiedModuleURI(aModuleURI, true);
	}

	public static boolean ensureValidFullyQualifiedModuleURI(URI aModuleURI, boolean toThrowExceptionIfNecessary) throws UnresolveableURIException {
		if (aModuleURI.segmentCount() < 3) {
			if(toThrowExceptionIfNecessary)
				throw new UnresolveableURIException(aModuleURI);
			return false;
		}
		return true;
	}

	public static boolean ensureValidFullyQualifiedPlatformURI(URI aFullyQualifiedResourceURI, boolean toThrowExceptionIfNecessary) throws UnresolveableURIException {
		if (aFullyQualifiedResourceURI.segmentCount() < 2 || !PLATFORM_SCHEME.equals(aFullyQualifiedResourceURI.scheme()) || !RESOURCE_PROTOCOL.equals(aFullyQualifiedResourceURI.segment(0))) {
			if (toThrowExceptionIfNecessary)
				throw new UnresolveableURIException(aFullyQualifiedResourceURI);
			return false;
		}
		return true;
	}

	public static void ensureValidFullyQualifiedPlatformURI(URI aFullyQualifiedResourceURI) throws UnresolveableURIException {
		ensureValidFullyQualifiedPlatformURI(aFullyQualifiedResourceURI, true);
	}

	public static URI trimModuleResourcePathToModuleURI(URI aModuleResourcePath) throws UnresolveableURIException {
		ensureValidFullyQualifiedModuleURI(aModuleResourcePath);
		return aModuleResourcePath.trimSegments(aModuleResourcePath.segmentCount() - 3);
	}

	public static URI trimWorkspacePathToProjectRelativeURI(URI aFullyQualifiedResourceURI) throws UnresolveableURIException {
		URI projectRelativePath = null;
		/* Check for a non-hierachical Workspace-relative path of the form platform:/resource/<project>/...*/
		if (ensureValidFullyQualifiedPlatformURI(aFullyQualifiedResourceURI, false)) {
			if (aFullyQualifiedResourceURI.segmentCount() == 2)
				/* The URI points to a project, resolve to an empty URI */
				return URI.createURI(""); //$NON-NLS-1$

			/*
			 * The URI has to be > 2 since the validation method above checks for < 2, and just
			 * checked for == 2 so if X NOT < 2 and X NOT == 2, THEN > 2
			 */
			projectRelativePath = trimToRelativePath(aFullyQualifiedResourceURI, 2);

		} else if(aFullyQualifiedResourceURI.isHierarchical() && aFullyQualifiedResourceURI.path().startsWith(""+IPath.SEPARATOR)) {
			projectRelativePath = trimToRelativePath(aFullyQualifiedResourceURI, 1);
		} else {
			throw new UnresolveableURIException(aFullyQualifiedResourceURI);
		}
		return projectRelativePath;
	}
	
	public static URI trimToRelativePath(URI aURI, int aStartIndex) {
		StringBuffer relativePath = new StringBuffer();
		for (int segmentIndex = aStartIndex; segmentIndex < aURI.segmentCount(); segmentIndex++) {
			relativePath.append(aURI.segment(segmentIndex));
			if (segmentIndex < (aURI.segmentCount() - 1))
				relativePath.append(IPath.SEPARATOR);
		}
		return URI.createURI(relativePath.toString());
	}
	
	
	/**
	 * @param aModuleResourcePath
	 * @return
	 */
	public static URI trimToDeployPathSegment(URI aFullyQualifiedModuleResourcePath) {
		int segmentCount = aFullyQualifiedModuleResourcePath.segmentCount(); 
		return aFullyQualifiedModuleResourcePath.deresolve(aFullyQualifiedModuleResourcePath.trimSegments(segmentCount - 4));
	}

	public static URI concat(URI uri1, URI uri2){
	    URI concatURI = uri1.appendSegments(uri2.segments());
	    return concatURI;
	}
}
