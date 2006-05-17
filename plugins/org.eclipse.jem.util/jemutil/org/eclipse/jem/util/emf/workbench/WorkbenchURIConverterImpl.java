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
/*
 *  $$RCSfile: WorkbenchURIConverterImpl.java,v $$
 *  $$Revision: 1.6 $$  $$Date: 2006/05/17 20:13:45 $$ 
 */
package org.eclipse.jem.util.emf.workbench;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.URIConverterImpl;

import org.eclipse.jem.util.plugin.JEMUtilPlugin;


/**
 * A default implementation of the WorkbenchURIConverter interface.
 * 
 * @since 1.0.0
 */
public class WorkbenchURIConverterImpl extends URIConverterImpl implements WorkbenchURIConverter {

	private final static IWorkspaceRoot WORKSPACE_ROOT = URIConverterImpl.workspaceRoot;
	private final static String WORKSPACE_ROOT_LOCATION = WORKSPACE_ROOT.getLocation().toString();

	private static final String FILE_PROTOCOL = "file"; //$NON-NLS-1$

	private static final IPath INVALID_PATH = new Path("!!!!~!!!!"); //$NON-NLS-1$

	private static final IFile INVALID_FILE = WORKSPACE_ROOT.getFile(INVALID_PATH.append(INVALID_PATH));

	//Used to avoid trying to fixup the URI when getting the
	//OutputStream
	protected boolean forceSaveRelative = false;

	protected List inputContainers;

	protected IContainer outputContainer;

	protected ResourceSetWorkbenchSynchronizer resourceSetSynchronizer;
	
	/*
	 * KLUDGE: We need to know the meta data area. This is so that any uri that starts with the metadata directory
	 * is considered a file uri and NOT a workspace uri. The metadata is where plugin's store their working data.
	 * It is not part of the workspace root.
	 *  
	 * There is no request for simply the metadata area. The log file is in the metadata directory. So we will
	 * get the log file location and just remove the log file name. That should leave us with the metadata directory
	 * only. If Eclipse ever decides to move it from here, this will no longer work. But it hasn't moved in three 
	 * versions.
	 * 
	 * @since 1.1.0
	 */
	static protected final String METADATA_LOCATION = Platform.getLogFileLocation().removeLastSegments(1).toString();

	/**
	 * Default converter constructor, no containers.
	 * 
	 * 
	 * @since 1.0.0
	 */
	public WorkbenchURIConverterImpl() {
		super();
	}

	/**
	 * Construct with an input container.
	 * 
	 * @param anInputContainer
	 * 
	 * @since 1.0.0
	 */
	public WorkbenchURIConverterImpl(IContainer anInputContainer) {
		this(anInputContainer, (ResourceSetWorkbenchSynchronizer) null);
	}

	/**
	 * Construct with an input container and a synchronzier.
	 * 
	 * @param aContainer
	 * @param aSynchronizer
	 * 
	 * @since 1.0.0
	 */
	public WorkbenchURIConverterImpl(IContainer aContainer, ResourceSetWorkbenchSynchronizer aSynchronizer) {
		this(aContainer, null, aSynchronizer);
	}

	/**
	 * Construct with an input container and an output container.
	 * 
	 * @param anInputContainer
	 * @param anOutputContainer
	 * 
	 * @since 1.0.0
	 */
	public WorkbenchURIConverterImpl(IContainer anInputContainer, IContainer anOutputContainer) {
		this(anInputContainer, anOutputContainer, null);
	}

	/**
	 * Construct with an input container, output container, and a synchronizer.
	 * 
	 * @param anInputContainer
	 * @param anOutputContainer
	 * @param aSynchronizer
	 * 
	 * @since 1.0.0
	 */
	public WorkbenchURIConverterImpl(IContainer anInputContainer, IContainer anOutputContainer, ResourceSetWorkbenchSynchronizer aSynchronizer) {
		addInputContainer(anInputContainer);
		setOutputContainer(anOutputContainer);
		resourceSetSynchronizer = aSynchronizer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.emf.workbench.WorkbenchURIConverter#addInputContainer(org.eclipse.core.resources.IContainer)
	 */
	public void addInputContainer(IContainer aContainer) {
		if (aContainer != null && !getInputContainers().contains(aContainer))
			getInputContainers().add(aContainer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.emf.workbench.WorkbenchURIConverter#addInputContainers(java.util.List)
	 */
	public void addInputContainers(List containers) {
		for (int i = 0; i < containers.size(); i++) {
			addInputContainer((IContainer) containers.get(i));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.emf.workbench.WorkbenchURIConverter#removeInputContainer(org.eclipse.core.resources.IContainer)
	 */
	public boolean removeInputContainer(IContainer aContainer) {
		return getInputContainers().remove(aContainer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.emf.workbench.WorkbenchURIConverter#getInputContainers()
	 */
	public List getInputContainers() {
		if (inputContainers == null)
			inputContainers = new ArrayList();
		return inputContainers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.emf.workbench.WorkbenchURIConverter#getInputContainer()
	 */
	public IContainer getInputContainer() {
		if (!getInputContainers().isEmpty())
			return (IContainer) getInputContainers().get(0);
		else
			return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.emf.workbench.WorkbenchURIConverter#getOutputContainer()
	 */
	public IContainer getOutputContainer() {
		if (outputContainer == null)
			outputContainer = getInputContainer();
		return outputContainer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.emf.workbench.WorkbenchURIConverter#setOutputContainer(org.eclipse.core.resources.IContainer)
	 */
	public void setOutputContainer(IContainer newOutputContainer) {
		outputContainer = newOutputContainer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.emf.workbench.WorkbenchURIConverter#getOutputFile(org.eclipse.core.runtime.IPath)
	 */
	public IFile getOutputFile(IPath aPath) {
		IFile file = null;
		if (getOutputContainer() != null) {
			if (forceSaveRelative)
				return primGetOutputFile(aPath);
			file = getOutputFileForPathWithContainerSegments(aPath);
			if (file != null)
				return file;
			else
				return primGetOutputFile(aPath);
		}
		return file;
	}

	protected IFile primGetOutputFile(IPath aPath) {
		return primGetFile(getOutputContainer(), aPath);
	}

	protected IFile getOutputFileForPathWithContainerSegments(IPath aPath) {
		IContainer out = getOutputContainer();
		return getFileForPathWithContainerSegments(aPath, out, false);
	}

	protected IFile getFileForPathWithContainerSegments(IPath aPath, IContainer container, boolean testExists) {
		IPath containerPath = null;
		IFile file = null;
		if (testExists) {
			containerPath = container.getProjectRelativePath();
			if (!containerPath.isEmpty()) {
				file = getFileForMatchingPath(aPath, containerPath, container);
				if (file != null && file.exists())
					return file;
			}
		}
		containerPath = container.getFullPath();
		file = getFileForMatchingPath(aPath, containerPath, container);
		return file;
	}

	protected IFile getFileForMatchingPath(IPath containerPath, IPath sourcePath, IContainer container) {
		int matches = 0;
		matches = containerPath.matchingFirstSegments(sourcePath);
		if (matches > 0 && matches == sourcePath.segmentCount()) {
			IPath loadPath = containerPath.removeFirstSegments(matches);
			return primGetFile(container, loadPath);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.emf.workbench.WorkbenchURIConverter#getFile(java.lang.String)
	 */
	public IFile getFile(String uri) {
		return getFile(new Path(uri));
	}

	/**
	 * Get the file from the path.
	 * 
	 * @param path
	 * @return
	 * @see WorkbenchURIConverter#getFile(String)
	 * @since 1.0.0
	 */
	public IFile getFile(IPath path) {
		IFile file = null;
		if (getInputContainer() != null) {
			path = path.makeRelative();
			java.util.Iterator it = getInputContainers().iterator();
			while (it.hasNext()) {
				IContainer con = (IContainer) it.next();
				file = getInputFile(con, path);
				if (file != null && file.exists())
					return file;
			}
		}
		if (file == null)
			return INVALID_FILE;
		return file;
	}

	/**
	 * Get output file from string path.
	 * 
	 * @param uri
	 * @return
	 * 
	 * @see WorkbenchURIConverter#getOutputFile(IPath)
	 * @since 1.0.0
	 */
	public IFile getOutputFile(String uri) {
		return getOutputFile(new Path(uri));
	}

	/**
	 * Get the input file from the container and path.
	 * 
	 * @param con
	 * @param path
	 * @return
	 * 
	 * @since 1.0.0
	 */
	public IFile getInputFile(IContainer con, IPath path) {
		IFile file = null;
		if (WORKSPACE_ROOT.equals(con) && path.segmentCount() < 2)
			path = INVALID_PATH.append(path);
		file = primGetFile(con, path);
		if (file == null || !file.exists())
			file = getFileForPathWithContainerSegments(path, con, true);
		return file;
	}

	protected IFile primGetFile(IContainer container, IPath path) {
		try {
			return container.getFile(path);
		} catch (IllegalArgumentException ex) {
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.emf.workbench.WorkbenchURIConverter#canGetUnderlyingResource(java.lang.String)
	 */
	public boolean canGetUnderlyingResource(String aFileName) {
		IFile file = getFile(aFileName);
		return file != null && file.exists();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.emf.workbench.WorkbenchURIConverter#isForceSaveRelative()
	 */
	public boolean isForceSaveRelative() {
		return forceSaveRelative;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.emf.workbench.WorkbenchURIConverter#setForceSaveRelative(boolean)
	 */
	public void setForceSaveRelative(boolean forceSaveRelative) {
		this.forceSaveRelative = forceSaveRelative;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.ecore.resource.URIConverter#normalize(org.eclipse.emf.common.util.URI)
	 */public URI normalize(URI uri) {
		URI result = uri;
		String fragment = null;
		if (uri.hasFragment()) {
			fragment = uri.fragment();
			result = uri.trimFragment();
		}
		result = getInternalURIMap().getURI(result);
		if (WorkbenchResourceHelperBase.isPlatformResourceURI(result))
			return appendFragment(result, fragment);
		if (WorkbenchResourceHelperBase.isPlatformPluginResourceURI(result)) {
			URI normalized = normalizePluginURI(result, fragment);
			return (normalized != null) ? normalized : uri;
		}
		String protocol = result.scheme();
		URI fileSearchURI = null;
		if (protocol == null) {
			fileSearchURI = normalizeEmptyProtocol(result, fragment);
			if (fileSearchURI != null)
				return fileSearchURI;
		} else if (FILE_PROTOCOL.equals(protocol)) {
			fileSearchURI = normalizeFileProtocol(result, fragment);
			if (fileSearchURI != null)
				return fileSearchURI;
		} else if (JEMUtilPlugin.WORKSPACE_PROTOCOL.equals(protocol))
			return normalizeWorkspaceProtocol(result, fragment);
		return super.normalize(uri);
	}

	/*
	 * Resolves a plugin format into the actual.
	 */
	protected URI normalizePluginURI(URI uri, String fragment) {
		if (uri.segmentCount() < 2)
			return uri; // Invalid, just let it go on.
		// See if already normalized.
		int u_scoreNdx = uri.segment(1).lastIndexOf('_');
		if (u_scoreNdx != -1) {
			// Not normalized. Remove the version to make it normalized.
			String[] segments = uri.segments();
			segments[1] = segments[1].substring(0, u_scoreNdx);
			return URI.createHierarchicalURI(uri.scheme(), uri.authority(), uri.device(), segments, uri.query(), fragment);
		} else
			return uri;
	}

	protected URI normalizeWorkspaceProtocol(URI aWorkspaceURI, String fragment) {
		URI result;
		String uriString = aWorkspaceURI.toString();
		uriString = uriString.substring(JEMUtilPlugin.WORKSPACE_PROTOCOL.length() + 1);
		result = URI.createPlatformResourceURI(uriString);
		if (fragment != null)
			result = appendFragment(aWorkspaceURI, fragment);
		return result;
	}
	
	protected URI normalizeEmptyProtocol(URI aFileUri, String fragment) {
		//Make the relative path absolute and return a platform URI.
		IPath path = new Path(aFileUri.toString());
		return normalizeToWorkspaceURI(path, fragment);
	}
	
	private URI normalizeToWorkspaceURI(IPath path, String fragment) {
		URI result = null;
		IFile file = getFile(path);
		if (file == null || !file.exists())
			file = getOutputFile(path);
		if (file != null) {
			result = URI.createPlatformResourceURI(file.getFullPath().toString());
			result = appendFragment(result, fragment);
		}
		return result;
	}
	
	protected URI normalizeFileProtocol(URI aFileUri, String fragment) {
		URI result = null;
		//Make the relative path absolute and return a platform URI.
		String devicePath = aFileUri.devicePath();
		//Test for workspace location.
		if (!devicePath.startsWith(METADATA_LOCATION) &&
			devicePath.startsWith(WORKSPACE_ROOT_LOCATION) && devicePath.length() > WORKSPACE_ROOT_LOCATION.length()) {
			//test for workspace location
			result = normalizeToWorkspaceURI(new Path(devicePath.substring(WORKSPACE_ROOT_LOCATION.length())), fragment);
		} else if (aFileUri.isRelative()) {
			result = normalizeToWorkspaceURI(new Path(aFileUri.toString()), fragment);
		} else {
			result = aFileUri;
		}
		return result;
	}
	
	protected URI appendFragment(URI result, String fragment) {
		if (fragment != null)
			return result.appendFragment(fragment);
		else
			return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.emf.workbench.WorkbenchURIConverter#getOutputFileWithMappingApplied(java.lang.String)
	 */
	public IFile getOutputFileWithMappingApplied(String uri) {
		URI converted = getInternalURIMap().getURI(URI.createURI(uri));
		return getOutputFile(new Path(converted.toString()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.ecore.resource.impl.URIConverterImpl#createPlatformResourceOutputStream(java.lang.String)
	 */
	public OutputStream createPlatformResourceOutputStream(String platformResourcePath) throws IOException {
		IFile file = WORKSPACE_ROOT.getFile(new Path(platformResourcePath));
		ProjectUtilities.ensureContainerNotReadOnly(file);
		return new WorkbenchByteArrayOutputStream(file, resourceSetSynchronizer);
	}

	protected URI getContainerRelativeURI(IFile aFile) {
		IPath path = WorkbenchResourceHelperBase.getPathFromContainers(inputContainers, aFile.getFullPath());
		if (path != null)
			return URI.createURI(path.toString());
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.ecore.resource.impl.URIConverterImpl#createPlatformResourceInputStream(java.lang.String)
	 */
	public InputStream createPlatformResourceInputStream(String platformResourcePath) throws IOException {
		IFile file = WORKSPACE_ROOT.getFile(new Path(platformResourcePath));
		try {
			if (!file.isSynchronized(IResource.DEPTH_ONE)) {
				try {
					File iofile = file.getFullPath().toFile();
					if (iofile.exists() || file.exists())
						file.refreshLocal(IResource.DEPTH_ONE, null);
				} catch (CoreException ce) {
					if (ce.getStatus().getCode() != IResourceStatus.WORKSPACE_LOCKED)
						throw ce;
				}
			}
			// CHANGED from <no-args> to <true> [94015]
			return file.getContents(true);
		} catch (CoreException exception) {
			throw new Resource.IOWrappedException(exception);
		}		
	}

}
