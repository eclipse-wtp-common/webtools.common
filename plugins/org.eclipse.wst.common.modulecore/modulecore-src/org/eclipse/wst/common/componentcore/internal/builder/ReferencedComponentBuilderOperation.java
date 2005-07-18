/*******************************************************************************
 * Copyright (c) 2003, 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.builder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.componentcore.UnresolveableURIException;
import org.eclipse.wst.common.componentcore.datamodel.properties.IReferencedComponentBuilderDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.impl.ModuleURIUtil;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualArchiveComponent;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.componentcore.internal.util.ZipFileExporter;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.internal.emfworkbench.integration.EMFWorkbenchEditPlugin;


public class ReferencedComponentBuilderOperation extends AbstractDataModelOperation implements IReferencedComponentBuilderDataModelProperties {
	private static String ERROR_EXPORTING_MSG = "Zip Error Message"; //$NON-NLS-1$

	private ZipFileExporter exporter = null;

	private List errorTable = new ArrayList(1); // IStatus

	private boolean useCompression = true;

	// private boolean createLeadupStructure = false;
	private boolean generateManifestFile = false;

	private IProgressMonitor monitor;

	private int inputContainerSegmentCount;

	/**
	 * @param model
	 */
	public ReferencedComponentBuilderOperation(IDataModel model) {
		super(model);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.commands.operations.IUndoableOperation#execute(org.eclipse.core.runtime.IProgressMonitor,
	 *      org.eclipse.core.runtime.IAdaptable)
	 */
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) {
		try {
			this.monitor = monitor;
			IVirtualReference vReference = (IVirtualReference) model.getProperty(VIRTUAL_REFERENCE);
			IVirtualComponent enclosingComponent = vReference.getEnclosingComponent();

			IPath absoluteOutputContainer = getAbsoluteOutputContainer(vReference);
			if (absoluteOutputContainer == null) // Project not accessible
				return OK_STATUS;
			// create output container folder if it does not exist
			IFolder outputContainerFolder = createFolder(absoluteOutputContainer);

			IVirtualComponent referencedComponent = vReference.getReferencedComponent();
			IPath absoluteInputContainer = getAbsoluteInputContainer(referencedComponent);

			if (absoluteOutputContainer == null) {
				return OK_STATUS;
			} else if (absoluteInputContainer == null || !referencedComponent.getProject().getFolder(absoluteInputContainer).exists()) {
				if (vReference.getReferencedComponent().isBinary()) {
					try {
						String osPath = "";
						VirtualArchiveComponent archiveComp = (VirtualArchiveComponent)referencedComponent;
						if( archiveComp.getArchiveType().equals(VirtualArchiveComponent.VARARCHIVETYPE)){
							IPath resolvedpath = (IPath)archiveComp.getAdapter(VirtualArchiveComponent.ADAPTER_TYPE);
							osPath = resolvedpath.toOSString();
						}	
						else{
							String fileString = ModuleURIUtil.getArchiveName(URI.createURI(referencedComponent.getComponentHandle().toString()));
							IPath path = new Path(fileString);
							osPath = path.toOSString();
						}
						if (vReference.getDependencyType() == IVirtualReference.DEPENDENCY_TYPE_CONSUMES) {
							expandZipFile(osPath, outputContainerFolder);
						} else {
							copyFile(osPath, outputContainerFolder);
						}
						return OK_STATUS;
					} catch (UnresolveableURIException e) {
						Logger.getLogger().logError(e);
					}
				}
			}

			if (vReference.getDependencyType() == IVirtualReference.DEPENDENCY_TYPE_CONSUMES) {
				// if consumes simply copy resources to output directory
				IResource sourceResource = getResource(absoluteInputContainer);
				if (sourceResource == null)
					return OK_STATUS;
				ComponentStructuralBuilder.smartCopy(sourceResource, absoluteOutputContainer, new NullProgressMonitor());
			} else {
				String zipName = getZipFileName(referencedComponent);
				IPath zipNameDestination = absoluteOutputContainer.append(zipName);
				IResource dependentZip = getResource(zipNameDestination);
				// TODO: this is needed to stop the copying of large dependent module. Incremental
				// build in M4 should allow for this
				// code to be removed.
				if (dependentZip == null || dependentZip.exists())
					return OK_STATUS;
				zipAndCopyResource(getResource(absoluteInputContainer), dependentZip);
				getResource(absoluteOutputContainer).refreshLocal(IResource.DEPTH_INFINITE, monitor);
			}

		} catch (CoreException ex) {
			Logger.getLogger().log(ex.getMessage());
		}
		return OK_STATUS;
	}

	private void copyFile(String osPath, IFolder outputContainerFolder) {
		File diskFile = new File(osPath);
		FileInputStream inputStream = null;
		try {
			IFile iFile = outputContainerFolder.getFile(new Path(diskFile.getName()));
			if (!iFile.exists()) {
				inputStream = new FileInputStream(diskFile);
				createFolder(iFile.getParent().getFullPath());
				iFile.create(inputStream, true, null);
			}
		} catch (FileNotFoundException e) {
			Logger.getLogger().logError(e);
		} catch (CoreException e) {
			Logger.getLogger().logError(e);
		} finally {
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					Logger.getLogger().logError(e);
				}
			}
		}
	}

	private void expandZipFile(String filePath, IFolder absoluteOutputContainer) {
		ZipFile zipFile;
		try {
			zipFile = new ZipFile(filePath);
			Enumeration entries = zipFile.entries();
			ZipEntry entry = null;
			InputStream inputStream = null;
			IFile file = null;
			while (entries.hasMoreElements()) {
				entry = (ZipEntry) entries.nextElement();
				entry.getName();
				try {
					file = absoluteOutputContainer.getFile(new Path(entry.getName()));
					if (!file.exists()) {
						inputStream = zipFile.getInputStream(entry);
						createFolder(file.getParent().getFullPath());
						file.create(inputStream, true, null);
					}
				} catch (CoreException e) {
					Logger.getLogger().logError(e);
				} finally {
					if (null != inputStream) {
						inputStream.close();
						inputStream = null;
					}
				}
			}
		} catch (IOException e) {
			Logger.getLogger().logError(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.commands.operations.IUndoableOperation#redo(org.eclipse.core.runtime.IProgressMonitor,
	 *      org.eclipse.core.runtime.IAdaptable)
	 */
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.commands.operations.IUndoableOperation#undo(org.eclipse.core.runtime.IProgressMonitor,
	 *      org.eclipse.core.runtime.IAdaptable)
	 */
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param inputResource
	 * @param zipName
	 * @return
	 */
	private void zipAndCopyResource(IResource inputResource, IResource outputResource) {
		try {
			IResource[] children;
			if (inputResource.exists()) {
				children = ((IContainer) inputResource).members();
				if(children.length == 0) {
					Logger.getLogger().log("Warning: Unable to zip empty archive from directory: " + inputResource.getName());
					return;
				}
			}
			else {
				Logger.getLogger().log("Warning: Unable to zip empty archive from directory: " + inputResource.getName());
				return;
			}
				
			String osPath = outputResource.getLocation().toOSString();
			exporter = new ZipFileExporter(osPath, true);
			inputContainerSegmentCount = inputResource.getFullPath().segmentCount();
			exportResource(inputResource);
			exporter.finished();
		} catch (CoreException e) {
				e.printStackTrace();
		} catch (IOException ioEx) {
			ioEx.printStackTrace();
		} catch (InterruptedException iEx) {
			iEx.printStackTrace();
		}
	}

	/**
	 * @return an IPath or null if not accessable
	 */
	private IPath getAbsoluteOutputContainer(IVirtualReference vReference) {
		IVirtualComponent vComponent = vReference.getEnclosingComponent();
		IFolder localWorkbenchModuleOuptutContainer = null;
		localWorkbenchModuleOuptutContainer = StructureEdit.getOutputContainerRoot(vComponent);
		if (localWorkbenchModuleOuptutContainer == null) // Project not found or is not
			// accessible
			return null;

		IPath localWorkbenchModuleOuptutContainerPath = localWorkbenchModuleOuptutContainer.getFullPath();
		return localWorkbenchModuleOuptutContainerPath.append(vReference.getRuntimePath().toString());
	}

	/**
	 * @return
	 */
	private IPath getAbsoluteInputContainer(IVirtualComponent virtualComponent) {
		return StructureEdit.getOutputContainerRoot(virtualComponent).getFullPath();
	}

	private String getZipFileName(IVirtualComponent vComponent) {
		String typeID = vComponent.getComponentTypeId();
		String zipFileName = vComponent.getName();
		zipFileName = zipFileName.replace('.', '_');
		if (typeID == null)
			return zipFileName;
		if (typeID.equals(IModuleConstants.JST_APPCLIENT_MODULE) || typeID.equals(IModuleConstants.JST_EJB_MODULE) || typeID.equals(IModuleConstants.JST_UTILITY_MODULE))
			return zipFileName + ".jar";
		else if (typeID.equals(IModuleConstants.JST_WEB_MODULE))
			return zipFileName + ".war";
		else if (typeID.equals(IModuleConstants.JST_CONNECTOR_MODULE))
			return zipFileName + ".rar";
		else if (typeID.equals(IModuleConstants.JST_EAR_MODULE))
			return zipFileName + ".ear";
		return zipFileName;
	}

	/**
	 * Get resource for given absolute path
	 * 
	 * @exception com.ibm.itp.core.api.resources.CoreException
	 */
	private IResource getResource(IPath absolutePath) throws CoreException {
		IResource resource = null;
		if (absolutePath != null && !absolutePath.isEmpty()) {
			resource = ResourcesPlugin.getWorkspace().getRoot().getFolder(absolutePath);
			if (resource == null || !(resource instanceof IFolder)) {
				resource = ResourcesPlugin.getWorkspace().getRoot().getFile(absolutePath);
			}
		}
		return resource;
	}

	/**
	 * Create a folder for given absolute path
	 * 
	 * @exception com.ibm.itp.core.api.resources.CoreException
	 */
	public IFolder createFolder(IPath absolutePath) throws CoreException {
		if (absolutePath == null || absolutePath.isEmpty())
			return null;
		IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(absolutePath);
		// check if the parent is there
		IContainer parent = folder.getParent();
		if (parent != null && !parent.exists() && (parent instanceof IFolder))
			createFolder(parent.getFullPath());
		if (!folder.exists())
			folder.create(true, true, new NullProgressMonitor());
		return folder;
	}

	/**
	 * Export the passed resource to the destination .zip
	 * 
	 * @param resource
	 *            org.eclipse.core.resources.IResource
	 * @param depth -
	 *            the number of resource levels to be included in the path including the resourse
	 *            itself.
	 */
	protected boolean exportResource(IResource resource) throws InterruptedException {
		if (!resource.isAccessible())
			return false;

		if (resource.getType() == IResource.FILE) {
			return writeResource(resource);
		} else {
			IResource[] children = null;

			try {
				children = ((IContainer) resource).members();
			} catch (CoreException e) {
				// this should never happen because an #isAccessible check is
				// done before #members is invoked
				addError(format(ERROR_EXPORTING_MSG, new Object[]{resource.getFullPath()}), e); //$NON-NLS-1$
			}

			boolean writeFolder = true;
			for (int i = 0; i < children.length; i++) {
				writeFolder = !exportResource(children[i]) && writeFolder;
			}
			if (writeFolder) {
				writeResource(resource);
			}
			return true;

		}
	}

	private boolean writeResource(IResource resource) throws InterruptedException {
		// if (resource.isDerived())
		// return false;
		String destinationName;
		IPath fullPath = resource.getFullPath();
		destinationName = fullPath.removeFirstSegments(inputContainerSegmentCount).toString();
		monitor.subTask(destinationName);

		try {
			if (resource.getType() == IResource.FILE)
				exporter.write((IFile) resource, destinationName);
			else
				exporter.writeFolder(destinationName);
		} catch (IOException e) {
			addError(format(ERROR_EXPORTING_MSG, //$NON-NLS-1$
						new Object[]{resource.getFullPath().makeRelative(), e.getMessage()}), e);
			return false;
		} catch (CoreException e) {
			addError(format(ERROR_EXPORTING_MSG, //$NON-NLS-1$
						new Object[]{resource.getFullPath().makeRelative(), e.getMessage()}), e);
			return false;
		}

		monitor.worked(1);
		return true;
	}

	/**
	 * @param ERROR_EXPORTING_MSG
	 * @param objects
	 * @return
	 */
	private String format(String pattern, Object[] arguments) {
		return MessageFormat.format(pattern, arguments);
	}

	/**
	 * Add a new entry to the error table with the passed information
	 */
	protected void addError(String message, Throwable e) {
		errorTable.add(new Status(IStatus.ERROR, EMFWorkbenchEditPlugin.ID, 0, message, e));
	}
}
