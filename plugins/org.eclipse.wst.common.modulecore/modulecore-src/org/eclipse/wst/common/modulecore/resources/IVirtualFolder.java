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
package org.eclipse.wst.common.modulecore.resources;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

public interface IVirtualFolder extends IVirtualContainer {

	/**
	 * Creates a new folder resource as a member of this handle's parent resource.
	 * <p>
	 * The <code>FORCE</code> update flag controls how this method deals with
	 * cases where the workspace is not completely in sync with the local file 
	 * system. If <code>FORCE</code> is not specified, the method will only attempt
	 * to create a directory in the local file system if there isn't one already. 
	 * This option ensures there is no unintended data loss; it is the recommended
	 * setting. However, if <code>FORCE</code> is specified, this method will 
	 * be deemed a success even if there already is a corresponding directory.
	 * </p>
	 * <p>
	 * Update flags other than <code>FORCE</code> are ignored.
	 * </p>
	 * <p>
	 * This method synchronizes this resource with the local file system.
	 * </p>
	 * <p>
	 * This method changes resources; these changes will be reported
	 * in a subsequent resource change event, including an indication 
	 * that the folder has been added to its parent.
	 * </p>
	 * <p>
	 * This method is long-running; progress and cancellation are provided
	 * by the given progress monitor. 
	 * </p>
	 * 
	 * @param updateFlags bit-wise or of update flag constants
	 *   (only <code>FORCE</code> is relevant here)
	 * @param local a flag controlling whether or not the folder will be local
	 *    after the creation
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting is not desired
	 * @exception CoreException if this method fails. Reasons include:
	 * <ul>
	 * <li> This resource already exists in the workspace.</li>
	 * <li> The workspace contains a resource of a different type 
	 *      at the same path as this resource.</li>
	 * <li> The parent of this resource does not exist.</li>
	 * <li> The parent of this resource is a project that is not open.</li>
	 * <li> The parent contains a resource of a different type 
	 *      at the same path as this resource.</li>
	 * <li> The name of this resource is not valid (according to 
	 *    <code>IWorkspace.validateName</code>).</li>
	 * <li> The corresponding location in the local file system is occupied
	 *    by a file (as opposed to a directory).</li>
	 * <li> The corresponding location in the local file system is occupied
	 *    by a folder and <code>FORCE</code> is not specified.</li>
	 * <li> Resource changes are disallowed during certain types of resource change 
	 *       event notification.  See <code>IVirtualResourceChangeEvent</code> for more details.</li>
	 * </ul>
	 * @exception OperationCanceledException if the operation is canceled. 
	 * Cancelation can occur even if no progress monitor is provided.
	 * @see IVirtualResourceRuleFactory#createRule(IVirtualResource)
	 * @since 2.0
	 */
	public void create(int updateFlags, IProgressMonitor monitor) throws CoreException;

	/**
	 * Creates a new folder resource as a member of this handle's parent resource.
	 * The folder's contents will be located in the directory specified by the given
	 * file system path.  The given path must be either an absolute file system
	 * path, or a relative path whose first segment is the name of a workspace path
	 * variable.
	 * <p>
	 * The <code>ALLOW_MISSING_LOCAL</code> update flag controls how this 
	 * method deals with cases where the local file system directory to be linked does
	 * not exist, or is relative to a workspace path variable that is not defined.
	 * If <code>ALLOW_MISSING_LOCAL</code> is specified, the operation will suceed
	 * even if the local directory is missing, or the path is relative to an
	 * undefined variable. If <code>ALLOW_MISSING_LOCAL</code> is not specified, the
	 * operation will fail in the case where the local file system directory does
	 * not exist or the path is relative to an undefined variable.
	 * </p>
	 * <p>
	 * Update flags other than <code>ALLOW_MISSING_LOCAL</code> are ignored.
	 * </p>
	 * <p>
	 * This method synchronizes this resource with the local file system at the given
	 * location.
	 * </p>
	 * <p>
	 * This method changes resources; these changes will be reported
	 * in a subsequent resource change event, including an indication 
	 * that the folder has been added to its parent.
	 * </p>
	 * <p>
	 * This method is long-running; progress and cancellation are provided
	 * by the given progress monitor. 
	 * </p>
	 * 
	 * @param localLocation a file system path where the folder should be linked
	 * @param updateFlags bit-wise or of update flag constants
	 *   (only ALLOW_MISSING_LOCAL is relevant here) 
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting is not desired
	 * @exception CoreException if this method fails. Reasons include:
	 * <ul>
	 * <li> This resource already exists in the workspace.</li>
	 * <li> The workspace contains a resource of a different type 
	 *      at the same path as this resource.</li>
	 * <li> The parent of this resource does not exist.</li>
	 * <li> The parent of this resource is not an open project</li>
	 * <li> The name of this resource is not valid (according to 
	 *    <code>IWorkspace.validateName</code>).</li>
	 * <li> The corresponding location in the local file system does not exist, or
	 * is relative to an undefined variable, and <code>ALLOW_MISSING_LOCAL</code> is
	 * not specified.</li>
	 * <li> The corresponding location in the local file system is occupied
	 *    by a file (as opposed to a directory).</li>
	 * <li> Resource changes are disallowed during certain types of resource change 
	 *       event notification.  See <code>IVirtualResourceChangeEvent</code> for more details.</li>
	 * <li>The team provider for the project which contains this folder does not permit
	 *       linked resources.</li>
	 * <li>This folder's project contains a nature which does not permit linked resources.</li>
	 * </ul>
	 * @exception OperationCanceledException if the operation is canceled. 
	 * Cancelation can occur even if no progress monitor is provided.
	 * @see IVirtualResource#isLinked()
	 * @see IVirtualResource#ALLOW_MISSING_LOCAL
	 * @since 2.1
	 */
	public void createLink(IPath localLocation, int updateFlags, IProgressMonitor monitor) throws CoreException; 

	/**
	 * Returns a handle to the file with the given name in this folder.
	 * <p> 
	 * This is a resource handle operation; neither the resource nor
	 * the result need exist in the workspace.
	 * The validation check on the resource name/path is not done
	 * when the resource handle is constructed; rather, it is done
	 * automatically as the resource is created.
	 * </p>
	 *
	 * @param name the string name of the member file
	 * @return the (handle of the) member file
	 * @see #getFolder(String)
	 */
	public IVirtualFile getFile(String name);

	/**
	 * Returns a handle to the folder with the given name in this folder.
	 * <p> 
	 * This is a resource handle operation; neither the container
	 * nor the result need exist in the workspace.
	 * The validation check on the resource name/path is not done
	 * when the resource handle is constructed; rather, it is done
	 * automatically as the resource is created.
	 * </p>
	 *
	 * @param name the string name of the member folder
	 * @return the (handle of the) member folder
	 * @see #getFile(String)
	 */
	public IVirtualFolder getFolder(String name);

	public IFolder getRealFolder();
	
	public IFolder[] getRealFolders();
}
