/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.modulecore.resources;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;


/**
 * <p>
 * Allows clients to work with flexible project structures 
 * using API similar to the Eclipse Platform IResource model.
 * </p>
 * <p>
 * IVirtualResource allows clients to acquire
 * information about the underlying resource including the name, 
 * and the paths which are relevant to the current resource, such
 * as the {@link #getRuntimePath() runtime path}, the 
 * {@link #getWorkspaceRelativePath() workspace-relative path}
 * of the underlying resource, and the 
 * {@link #getProjectRelativePath() project-relative path}
 * of the underlying resource.
 * </p>
 * <a base="references" />
 * <p>Each IVirtualResource can represent an <b>implicit</b> reference or
 * an <b>explicit</b> reference. An <b>explicit</b> reference is a formal
 * mapping from some path within the file structure to a runtime path. Changing
 * or removing an explicit reference only requires model modifications. An 
 * <b>implicit</b> reference has a root which is derived from an explicit path,
 * but some fragment of the path towards the end is derived from the literal 
 * path within the file structure. Modifications to implicit paths may cause 
 * changes to structure on disk. For resource modifications that should not modify
 * structures on disk, use {@link #IGNORE_UNDERLYING_RESOURCE}.
 * </p>
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public interface IVirtualResource extends ISchedulingRule {

	/*
	 * ==================================================================== 
	 * Constants defining resource types: There are four possible resource 
	 * types and their type constants are in the hex range 0x10 to 0x80 
	 * as defined below.
	 * ====================================================================
	 */

	/**
	 * Type constant (bit mask value 1) which identifies file resources.
	 * 
	 * @see IVirtualResource#getType()
	 * @see IVirtualFile
	 */
	public static final int FILE = 0x10;

	/**
	 * Type constant (bit mask value 2) which identifies folder resources.
	 * 
	 * @see IVirtualResource#getType()
	 * @see IVirtualFolder
	 */
	public static final int FOLDER = 0x20;

	/**
	 * Type constant (bit mask value 8) which identifies the root resource.
	 * 
	 * @see IVirtualResource#getType()
	 * @see IWorkspaceRoot
	 */
	public static final int COMPONENT = 0x80;
	
	/**
	 * General purpose zero-valued bit mask constant. Useful whenever you need to supply a bit mask
	 * with no bits set.
	 * <p>
	 * Example usage: <code>
	 * <pre>
	 * delete(IVirtualResource.NONE, null)
	 * </pre>
	 * </code>
	 * </p>
	 * 
	 * @since 2.0
	 */
	public static final int NONE = 0;

	/*
	 * ==================================================================== Constants for update
	 * flags for delete, move, copy, open, etc.:
	 * ====================================================================
	 */

	/**
	 * Update flag constant (bit mask value 1) indicating that the operation should proceed even if
	 * the resource is out of sync with the local file system.
	 * 
	 * @since 2.0
	 */
	public static final int FORCE = 0x100; 

	/**
	 * Indicates that exclusions enumerated in the model should be ignored. 
	 */
	public static final int IGNORE_EXCLUSIONS = 0x200;

	/**
	 * Indicates that modifications should only modify the metamodel and ignore the underlying
	 * resources where applicable.  See the 
	 * <a href="IVirtualResource.html#references">documentation on references</a> 
	 * for more information on why this flag is relevant.
	 */
	public static final int IGNORE_UNDERLYING_RESOURCE = 0x400;   

	/**   
	 * Remove the resource from the flexible structure. Removing the resource could require
	 * changes to the underlying metamodel or changes to the file structure. To avoid
	 * changes the actual disk structure, use {@link #IGNORE_UNDERLYING_RESOURCE}.
	 * <p>
	 * Update flags supplied to this method will be passed into any IResource modification
	 * API which is called as a result of this method's invocation.  
	 * </p>
	 * @see #IGNORE_UNDERLYING_RESOURCE
	 * @see IResource#delete(int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void delete(int updateFlags, IProgressMonitor monitor) throws CoreException;

	/**
	 * Compares two objects for equality; for resources, equality is defined in terms of their
	 * handles: same resource type, equal full paths, and identical workspaces. Resources are not
	 * equal to objects other than resources.
	 * 
	 * @param other
	 *            the other object
	 * @return an indication of whether the objects are equals
	 * @see #getType()
	 * @see #getWorkspaceRelativePath() 
	 */
	public boolean equals(Object other);

	/**
	 * Returns whether this virtual resource is backed by an accessible IResource. 
	 * <p>
	 * <code>IVirtualResource</code> objects are lightweight handle objects used to access
	 * IResources. However, having an IVirtualResource handle does not necessarily mean the
	 * underlying resource represented by the handle exists in the workspace, or is accessible.
	 * For more detailed information concerning the existence or accessibility of the underlying 
	 * resource, {@link #getUnderlyingResource()}.
	 * </p>
	 * 
	 * @return <code>true</code> if the underlying resource exists and is accessible, otherwise <code>false</code>
	 * @see org.eclipse.core.resources.IResource#exists()
	 * @see #getUnderlyingResource();
	 */
	public boolean exists();

	/**
	 * Returns the file extension portion of this virtual resource's name, or <code>null</code> if it does
	 * not have one. The file extension of the virtual resource will be returned, which may or may not match
	 * that of the underlying resource. 
	 * <p>
	 * The file extension portion is defined as the string following the last period (".") character
	 * in the name. If there is no period in the name, the path has no file extension portion. If
	 * the name ends in a period, the file extension portion is the empty string.
	 * </p>
	 * <p>
	 * This is a resource handle operation; the resource need not exist.
	 * </p>
	 * 
	 * @return a string file extension
	 * @see #getName()
	 */
	public String getFileExtension();

	/**
	 * Returns the full, absolute path of the underlying resource relative to the workspace.
	 * <p>
	 * This is a resource handle operation; the resource need not exist. If this resource does
	 * exist, its path can be safely assumed to be valid.
	 * </p>
	 * 
	 * @return the absolute path of this resource
	 * @see #getProjectRelativePath()
	 * @see IResource#getFullPath()
	 */
	public IPath getWorkspaceRelativePath();


	/**
	 * Returns a relative path of the underlying resource with respect to its project. Returns the empty path
	 * for projects and the workspace root.
	 * <p>
	 * This is a resource handle operation; the resource need not exist. If this resource does
	 * exist, its path can be safely assumed to be valid.
	 * </p>
	 * <p>
	 * A resource's project-relative path indicates the route from the project to the resource.
	 * Within a workspace, there is exactly one such path for any given resource. The returned path
	 * never has a trailing slash.
	 * </p>
	 * <p>
	 * Project-relative paths are recommended over absolute paths, since the former are not affected
	 * if the project is renamed.
	 * </p>
	 * 
	 * @return the relative path of this resource with respect to its project
	 * @see #getWorkspaceRelativePath()
	 * @see #getProject()
	 * @see Path#EMPTY
	 */
	public IPath getProjectRelativePath();

	public IPath getRuntimePath();

	/**
	 * Returns the name of this resource. The name of a resource is synonymous with the last segment
	 * of its full (or project-relative) path for all resources other than the workspace root. The
	 * workspace root's name is the empty string.
	 * <p>
	 * This is a resource handle operation; the resource need not exist.
	 * </p>
	 * <p>
	 * If this resource exists, its name can be safely assumed to be valid.
	 * </p>
	 * 
	 * @return the name of the resource
	 * @see #getWorkspaceRelativePath()
	 * @see #getProjectRelativePath()
	 */
	public String getName();

	public String getComponentName();

	/**
	 * Returns the resource which is the parent of this resource, or <code>null</code> if it has
	 * no parent (that is, this resource is the workspace root).
	 * <p>
	 * The full path of the parent resource is the same as this resource's full path with the last
	 * segment removed.
	 * </p>
	 * <p>
	 * This is a resource handle operation; neither the resource nor the resulting resource need
	 * exist.
	 * </p>
	 * 
	 * @return the parent resource of this resource, or <code>null</code> if it has no parent
	 */
	public IVirtualContainer getParent();

	/**
	 * Returns the project which contains this resource. Returns itself for projects and
	 * <code>null</code> for the workspace root.
	 * <p>
	 * A resource's project is the one named by the first segment of its full path.
	 * </p>
	 * <p>
	 * This is a resource handle operation; neither the resource nor the resulting project need
	 * exist.
	 * </p>
	 * 
	 * @return the project handle
	 */
	public IProject getProject();

	/**
	 * Returns the type of this resource. The returned value will be one of <code>FILE</code>,
	 * <code>FOLDER</code>, <code>PROJECT</code>, <code>ROOT</code>.
	 * <p>
	 * <ul>
	 * <li> All resources of type <code>FILE</code> implement <code>IVirtualFile</code>.</li>
	 * <li> All resources of type <code>FOLDER</code> implement <code>IVirtualFolder</code>.</li>
	 * <li> All resources of type <code>PROJECT</code> implement <code>IProject</code>.</li>
	 * <li> All resources of type <code>ROOT</code> implement <code>IWorkspaceRoot</code>.</li>
	 * </ul>
	 * </p>
	 * <p>
	 * This is a resource handle operation; the resource need not exist in the workspace.
	 * </p>
	 * 
	 * @return the type of this resource
	 * @see #FILE
	 * @see #FOLDER
	 * @see #PROJECT
	 * @see #ROOT
	 */
	public int getType();
	
	public IResource getUnderlyingResource();


	/**
	 * Returns whether this resource is accessible. For files and folders, this is equivalent to
	 * existing; for projects, this is equivalent to existing and being open. The workspace root is
	 * always accessible.
	 * 
	 * @return <code>true</code> if this resource is accessible, and <code>false</code>
	 *         otherwise
	 * @see #exists()
	 * @see IProject#isOpen()
	 */
	public boolean isAccessible();

}
