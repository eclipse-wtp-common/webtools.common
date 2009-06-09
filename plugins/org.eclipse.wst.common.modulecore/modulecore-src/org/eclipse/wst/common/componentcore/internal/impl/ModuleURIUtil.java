/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.impl;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jem.util.emf.workbench.WorkbenchResourceHelperBase;
import org.eclipse.wst.common.componentcore.UnresolveableURIException;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

/**
 * 
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public class ModuleURIUtil {

	protected static final String RESOURCE_URI_PROTOCOL = PlatformURLModuleConnection.MODULE_PROTOCOL+IPath.SEPARATOR+PlatformURLModuleConnection.RESOURCE_MODULE+IPath.SEPARATOR;

	protected static final String ARCHIVE_URI_PROTOCOL = PlatformURLModuleConnection.MODULE_PROTOCOL+IPath.SEPARATOR+PlatformURLModuleConnection.CLASSPATH +IPath.SEPARATOR;
	
	public static interface ModuleURI {
		public static final int SUB_PROTOCOL_INDX = 0;
		public static final int PROJECT_NAME_INDX = 1;
		public static final int MODULE_NAME_INDX = 2;
		public static final int CONTENT_TYPE_INDX = 3;
	}


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
	 * @return The deployed name of the {@see WorkbenchComponent}referenced by the module URI
	 * @throws UnresolveableURIException
	 *             If the supplied module URI is invalid or unresolveable.
	 */ 
	public static String getDeployedName(URI aModuleURI) throws UnresolveableURIException {
		ensureValidFullyQualifiedModuleURI(aModuleURI);
		return aModuleURI.segment(ModuleURI.MODULE_NAME_INDX);
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
	public static boolean isValidFullyQualifiedModuleURI(URI aModuleURI) {
		if (aModuleURI.segmentCount() < 3) {
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
		int trimIndex = (hasContentTypeName(aModuleResourcePath) ? 4 : 3);
		return aModuleResourcePath.trimSegments(aModuleResourcePath.segmentCount() - trimIndex);
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
		URI uri;
		int trimIndex = (hasContentTypeName(aFullyQualifiedModuleResourcePath) ? 5 : 4);
		if(segmentCount > trimIndex){
			uri = aFullyQualifiedModuleResourcePath.deresolve(aFullyQualifiedModuleResourcePath.trimSegments(segmentCount - trimIndex), false,false,true);
		} else {
			uri = URI.createURI(aFullyQualifiedModuleResourcePath.segment(segmentCount-1));
		}
		return uri;
	}

	public static URI concat(URI uri1, URI uri2){
	    URI concatURI = uri1.appendSegments(uri2.segments());
	    return concatURI;
	}
	
	public static URI fullyQualifyURI(WorkbenchComponent aComponent) {
		try {
			if(ensureValidFullyQualifiedModuleURI(aComponent.getHandle(), false))
				return aComponent.getHandle(); 
			IProject project = StructureEdit.getContainingProject(aComponent);
			if(project != null) {
				return URI.createURI(RESOURCE_URI_PROTOCOL + project.getName() + IPath.SEPARATOR + aComponent.getName());
			} 
		} catch (UnresolveableURIException e) {
			// Ignore
		}
		return null; 
	}
	

	public static URI fullyQualifyURI(IProject aProject) {
		return URI.createURI(RESOURCE_URI_PROTOCOL + aProject.getName() + IPath.SEPARATOR + aProject.getName());
	}
	public static URI fullyQualifyURI(IProject aProject, String contentTypeDescriber) {
		return URI.createURI(RESOURCE_URI_PROTOCOL + aProject.getName() + IPath.SEPARATOR + aProject.getName() + IPath.SEPARATOR + contentTypeDescriber);
	}

	public static URI archiveComponentfullyQualifyURI(String aComponentName) {
		return URI.createURI(ARCHIVE_URI_PROTOCOL + aComponentName);
	}
	
	public static boolean isClassPathURI(URI uri){
		if(uri!=null && PlatformURLModuleConnection.CLASSPATH.equals(uri.segment(0) )){
			return true;
		}
		return false;
	}
	
	public static String getArchiveType(URI aModuleURI)throws UnresolveableURIException {
		ensureValidFullyQualifiedModuleURI(aModuleURI);
		return aModuleURI.segment(ModuleURI.PROJECT_NAME_INDX);		
	}
	
	public static String getArchiveName(URI aModuleURI)throws UnresolveableURIException {
		ensureValidFullyQualifiedModuleURI(aModuleURI);
		URI uri = trimToRelativePath(aModuleURI, ModuleURI.MODULE_NAME_INDX);
		return uri.toString();
	}
	public static String getHandleString(IVirtualComponent aComponent){
		if (aComponent.isBinary())
			return ModuleURIUtil.archiveComponentfullyQualifyURI(aComponent.getName()).toString();
		else
			return "[" + aComponent.getProject().getFullPath() + "]:" + aComponent.getProject().getName(); //$NON-NLS-1$  //$NON-NLS-2$
	}

	public static String getContentTypeName(URI uri) {
		
		if (WorkbenchResourceHelperBase.isPlatformResourceURI(uri) || !isValidFullyQualifiedModuleURI(uri))
			return null;
		String contentTypeIdentifier = (uri.segmentCount() > 3 ? uri.segment(ModuleURI.CONTENT_TYPE_INDX) : null);
		if (contentTypeIdentifier != null && Platform.getContentTypeManager().getContentType(uri.segment(ModuleURI.CONTENT_TYPE_INDX)) != null)
			return contentTypeIdentifier;
		else
			return null;
	}
	public static boolean hasContentTypeName(URI uri) {
		
		if (WorkbenchResourceHelperBase.isPlatformResourceURI(uri))
			return false;
		String contentTypeIdentifier = (uri.segmentCount() > 3 ? uri.segment(ModuleURI.CONTENT_TYPE_INDX) : null);
		if (contentTypeIdentifier != null && Platform.getContentTypeManager().getContentType(uri.segment(ModuleURI.CONTENT_TYPE_INDX)) != null)
			return true;
		else
			return false;
	}
}
