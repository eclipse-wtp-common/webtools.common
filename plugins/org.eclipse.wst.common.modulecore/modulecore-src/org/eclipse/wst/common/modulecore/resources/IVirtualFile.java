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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

public interface IVirtualFile extends IVirtualResource {

	/**
	 * Creates a new file resource as a member of this handle's parent resource.
	 * The file's contents will be located in the file specified by the given
	 * file system path.  The given path must be either an absolute file system
	 * path, or a relative path whose first segment is the name of a workspace path
	 * variable.
	 * <p>
	 * The <code>ALLOW_MISSING_LOCAL</code> update flag controls how this 
	 * method deals with cases where the local file system file to be linked does
	 * not exist, or is relative to a workspace path variable that is not defined.
	 * If <code>ALLOW_MISSING_LOCAL</code> is specified, the operation will suceed
	 * even if the local file is missing, or the path is relative to an undefined
	 * variable. If <code>ALLOW_MISSING_LOCAL</code> is not specified, the operation
	 * will fail in the case where the local file system file does not exist or the
	 * path is relative to an undefined variable.
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
	 * that the file has been added to its parent.
	 * </p>
	 * <p>
	 * This method is long-running; progress and cancellation are provided
	 * by the given progress monitor. 
	 * </p>
	 *
	 * @param localLocation a file system path where the file should be linked 
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
	 *    by a directory (as opposed to a file).</li>
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
 
	
	public IFile getRealFile();
	
	public IFile[] getRealFiles();
}
