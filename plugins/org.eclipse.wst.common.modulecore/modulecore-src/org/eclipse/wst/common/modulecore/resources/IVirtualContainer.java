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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
/**
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IVirtualContainer extends IVirtualResource { 
 
	/**
	 * Returns whether a resource of some type with the given path 
	 * exists relative to this resource.
	 * The supplied path may be absolute or relative; in either case, it is
	 * interpreted as relative to this resource.  Trailing separators are ignored.
	 * If the path is empty this container is checked for existence.
	 *
	 * @param path the path of the resource
	 * @return <code>true</code> if a resource of some type with the given path 
	 *     exists relative to this resource, and <code>false</code> otherwise
	 * @see IVirtualResource#exists()
	 */
	public boolean exists(IPath path);

	/**
	 * Finds and returns the member resource (project, folder, or file)
	 * with the given name in this container, or <code>null</code> if no such
	 * resource exists.
	 * 
	 * <p> N.B. Unlike the methods which traffic strictly in resource
	 * handles, this method infers the resulting resource's type from the
	 * resource existing at the calculated path in the workspace.
	 * </p>
	 *
	 * @param name the string name of the member resource
	 * @return the member resource, or <code>null</code> if no such
	 * 		resource exists
	 */
	public IVirtualResource findMember(String name);

	/**
	 * Finds and returns the member resource (project, folder, or file)
	 * with the given name in this container, or <code>null</code> if 
	 * there is no such resource.
	 * <p>
	 * If the <code>includePhantoms</code> argument is <code>false</code>, 
	 * only a member resource with the given name that exists will be returned.
	 * If the <code>includePhantoms</code> argument is <code>true</code>,
	 * the method also returns a resource if the workspace is keeping track of a
	 * phantom with that name.
	 * </p><p>
	 * Note that no attempt is made to exclude team-private member resources
	 * as with <code>members</code>.
	 * </p><p>
	 * N.B. Unlike the methods which traffic strictly in resource
	 * handles, this method infers the resulting resource's type from the
	 * existing resource (or phantom) in the workspace.
	 * </p>
	 *
	 * @param name the string name of the member resource
	 * @param includePhantoms <code>true</code> if phantom resources are
	 *   of interest; <code>false</code> if phantom resources are not of
	 *   interest
	 * @return the member resource, or <code>null</code> if no such
	 * 		resource exists
	 * @see #members()
	 * @see IVirtualResource#isPhantom()
	 */
	public IVirtualResource findMember(String name, boolean includePhantoms);

	/**
	 * Finds and returns the member resource identified by the given path in
	 * this container, or <code>null</code> if no such resource exists.
	 * The supplied path may be absolute or relative; in either case, it is
	 * interpreted as relative to this resource.   Trailing separators and the path's
	 * device are ignored. If the path is empty this container is returned.
	 * <p>
	 * Note that no attempt is made to exclude team-private member resources
	 * as with <code>members</code>.
	 * </p><p> 
	 * N.B. Unlike the methods which traffic strictly in resource
	 * handles, this method infers the resulting resource's type from the
	 * resource existing at the calculated path in the workspace.
	 * </p>
	 *
	 * @param path the path of the desired resource
	 * @return the member resource, or <code>null</code> if no such
	 * 		resource exists
	 */
	public IVirtualResource findMember(IPath path);

	/**
	 * Finds and returns the member resource identified by the given path in
	 * this container, or <code>null</code> if there is no such resource.
	 * The supplied path may be absolute or relative; in either case, it is
	 * interpreted as relative to this resource.  Trailing separators and the path's
	 * device are ignored.
	 * If the path is empty this container is returned.
	 * <p>
	 * If the <code>includePhantoms</code> argument is <code>false</code>, 
	 * only a resource that exists at the given path will be returned.
	 * If the <code>includePhantoms</code> argument is <code>true</code>,
	 * the method also returns a resource if the workspace is keeping track of
	 * a phantom member resource at the given path.
	 * </p><p>
	 * Note that no attempt is made to exclude team-private member resources
	 * as with <code>members</code>.
	 * </p><p>
	 * N.B. Unlike the methods which traffic strictly in resource
	 * handles, this method infers the resulting resource's type from the
	 * existing resource (or phantom) at the calculated path in the workspace.
	 * </p>
	 *
	 * @param path the path of the desired resource
	 * @param includePhantoms <code>true</code> if phantom resources are
	 *   of interest; <code>false</code> if phantom resources are not of
	 *   interest
	 * @return the member resource, or <code>null</code> if no such
	 * 		resource exists
	 * @see #members(boolean)
	 * @see IVirtualResource#isPhantom()
	 */
	public IVirtualResource findMember(IPath path, boolean includePhantoms); 

	/**
	 * Returns a handle to the file identified by the given path in this
	 * container.
	 * <p> 
	 * This is a resource handle operation; neither the resource nor
	 * the result need exist in the workspace.
	 * The validation check on the resource name/path is not done
	 * when the resource handle is constructed; rather, it is done
	 * automatically as the resource is created.
	 * <p>
	 * The supplied path may be absolute or relative; in either case, it is
	 * interpreted as relative to this resource and is appended
	 * to this container's full path to form the full path of the resultant resource.
	 * A trailing separator is ignored. The path of the resulting resource must 
	 * have at least two segments.
	 * </p>
	 *
	 * @param path the path of the member file
	 * @return the (handle of the) member file
	 * @see #getFolder(IPath)
	 */
	public IVirtualFile getFile(IPath path);

	/**
	 * Returns a handle to the folder identified by the given path in this
	 * container.
	 * <p> 
	 * This is a resource handle operation; neither the resource nor
	 * the result need exist in the workspace.
	 * The validation check on the resource name/path is not done
	 * when the resource handle is constructed; rather, it is done
	 * automatically as the resource is created. 
	 * <p>
	 * The supplied path may be absolute or relative; in either case, it is
	 * interpreted as relative to this resource and is appended
	 * to this container's full path to form the full path of the resultant resource.
	 * A trailing separator is ignored. The path of the resulting resource must
	 * have at least two segments.
	 * </p>
	 *
	 * @param path the path of the member folder
	 * @return the (handle of the) member folder
	 * @see #getFile(IPath)
	 */
	public IVirtualFolder getFolder(IPath path); 

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

	/**
	 * Returns a list of existing member resources (projects, folders and files)
	 * in this resource, in no particular order.
	 * <p>
	 * This is a convenience method, fully equivalent to <code>members(IVirtualResource.NONE)</code>.
	 * Team-private member resources are <b>not</b> included in the result.
	 * </p><p>
	 * Note that the members of a project or folder are the files and folders
	 * immediately contained within it.  The members of the workspace root
	 * are the projects in the workspace.
	 * </p>
	 *
	 * @return an array of members of this resource
	 * @exception CoreException if this request fails. Reasons include:
	 * <ul>
	 * <li> This resource does not exist.</li>
	 * <li> This resource is a project that is not open.</li>
	 * </ul>
	 * @see #findMember(IPath)
	 * @see IVirtualResource#isAccessible()
	 */
	public IVirtualResource[] members() throws CoreException;

	/**
	 * Returns a list of all member resources (projects, folders and files)
	 * in this resource, in no particular order.
	 * <p>
	 * If the <code>INCLUDE_PHANTOMS</code> flag is not specified in the member 
	 * flags (recommended), only member resources that exist will be returned.
	 * If the <code>INCLUDE_PHANTOMS</code> flag is specified,
	 * the result will also include any phantom member resources the
	 * workspace is keeping track of.
	 * </p><p>
	 * If the <code>INCLUDE_TEAM_PRIVATE_MEMBERS</code> flag is specified 
	 * in the member flags, team private members will be included along with
	 * the others. If the <code>INCLUDE_TEAM_PRIVATE_MEMBERS</code> flag
	 * is not specified (recommended), the result will omit any team private
	 * member resources.
	 * </p>
	 * <p>
	 * If the <code>EXCLUDE_DERIVED</code> flag is not specified, derived 
	 * resources are included. If the <code>EXCLUDE_DERIVED</code> flag is 
	 * specified in the member flags, derived resources are not included.
	 * </p>
	 *
	 * @param memberFlags bit-wise or of member flag constants
	 *   (<code>INCLUDE_PHANTOMS</code>, <code>INCLUDE_TEAM_PRIVATE_MEMBERS</code>
	 *   and <code>EXCLUDE_DERIVED</code>) indicating which members are of interest
	 * @return an array of members of this resource
	 * @exception CoreException if this request fails. Reasons include:
	 * <ul>
	 * <li> This resource does not exist.</li>
	 * <li> the <code>INCLUDE_PHANTOMS</code> flag is not specified and
	 *     this resource does not exist.</li>
	 * <li> the <code>INCLUDE_PHANTOMS</code> flag is not specified and
	 *     this resource is a project that is not open.</li>
	 * </ul>
	 * @see IVirtualResource#exists()
	 * @since 2.0
	 */
	public IVirtualResource[] members(int memberFlags) throws CoreException;
	
	/**
	 * Create the underlying model elements if they do not already exist. Resources
	 * may be created as a result of this method if the mapped path does not exist. 
	 * 
	 * @param updateFlags Any of IVirtualResource or IResource update flags. If a 
	 * 			resource must be created, the updateFlags will be supplied to the 
	 * 			resource creation operation.
	 * @param aMonitor
	 * @throws CoreException
	 */
	public void create(int updateFlags, IProgressMonitor aMonitor) throws CoreException;
	
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
	public void createLink(IPath aProjectRelativePath, int updateFlags, IProgressMonitor monitor) throws CoreException;
 
}
 
