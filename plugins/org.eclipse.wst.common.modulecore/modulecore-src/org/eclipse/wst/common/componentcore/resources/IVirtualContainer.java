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
package org.eclipse.wst.common.componentcore.resources;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
/**
 * Represents a component as defined by the WTP Modules file. A 
 * virtual container represents the root of the component. 
 * <p>
 * Virtual containers can provide handles to virtual files and 
 * virtual folders that it contains. 
 * </p>
 * <p>
 * To acquire a handle to a component, use the following snippet:<br> 
 * <code>ModuleCore.createContainer(containingProject, componentName)</code>
 * which will create a handle to a virtual container contained by 
 * <i>containingProject</i> with the name <i>componentName</i>. If the 
 * a component of the given name exists, then 
 * {@link org.eclipse.wst.common.componentcore.resources.IVirtualResource#exists()}
 * will return true. Otherwise, use {@link #create(int, IProgressMonitor)} to 
 * create the relevant model elements. If a client needs to add a 
 * mapping from a folder in the same project to root, use 
 * {@link #createLink(IPath, int, IProgressMonitor)} with a 
 * project-relative path. 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IVirtualContainer extends IVirtualResource {  

	
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
	 * Returns whether a virtual resource of some type with the given path 
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
	 * Finds and returns the member virtual resource (folder or file)
	 * with the given name in this container, or <code>null</code> if no such
	 * resource exists.
	 * 
	 *
	 * @param name the string name of the member resource
	 * @return the member resource, or <code>null</code> if no such
	 * 		resource exists
	 */
	public IVirtualResource findMember(String name);

	/**
	 * Finds and returns the member resource (folder, or file)
	 * with the given name in this container, or <code>null</code> if 
	 * there is no such resource.
	 * <p>
	 * If the <code>includePhantoms</code> argument is <code>false</code>, 
	 * only a member resource with the given name that exists will be returned.
	 * If the <code>includePhantoms</code> argument is <code>true</code>,
	 * the method also returns a resource if the workspace is keeping track of a
	 * phantom with that name.
	 * </p>
	 * <p>
	 * Note that no attempt is made to exclude team-private member resources
	 * as with <code>members</code>.
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
	public IVirtualResource findMember(String name, int searchFlags);

	/**
	 * Finds and returns the member resource identified by the given path in
	 * this container, or <code>null</code> if no such resource exists.
	 * The supplied path may be absolute or relative; in either case, it is
	 * interpreted as relative to this resource.   Trailing separators and the path's
	 * device are ignored. If the path is empty this container is returned.  
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
	public IVirtualResource findMember(IPath path, int searchFlags); 

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
}
 
