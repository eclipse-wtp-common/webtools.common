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
 * This interface is not intended to be implemented by clients.
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
	 * Create a mapping from the supplied location to the runtime path of this 
	 * virtual resource. Model changes will occur as a result of this method, 
	 * and potentially resource-level creations as well.
	 * 
	 * @param aProjectRelativeLocation
	 * @param updateFlags
	 * @param monitor
	 * @throws CoreException
	 */
	public void createLink(IPath aProjectRelativeLocation, int updateFlags, IProgressMonitor monitor) throws CoreException;

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
	 * Returns a relative path of the underlying resource with respect to its project.  
	 * <p>
	 * This is a resource handle operation; the resource need not exist. If this resource does
	 * exist, its path can be safely assumed to be valid.
	 * </p>
	 * <p>
	 * Project-relative paths are recommended over absolute paths, since the former are not affected
	 * if the project is renamed.
	 * </p>
	 * 
	 * @return the relative path of this resource with respect to its project
	 * @see #getWorkspaceRelativePath()
	 * @see #getProject()
	 * @see IResource#getProjectRelativePath()
	 */
	public IPath getProjectRelativePath();

	/**
	 * Returns the runtime path of this virtual resource. The runtime path
	 * is determined through the metamodel and represents the path that the
	 * underlying resource will be accessed at runtime. 
	 * 
	 * @return the runtime path of this virtual resource
	 */
	public IPath getRuntimePath();

	/**
	 * Returns the name of this virtual resource. The name of a virtual resource 
	 * is synonymous with the last segment of its runtime path.
	 * <p>
	 * This is a resource handle operation; the resource need not exist.
	 * </p>
	 * <p>
	 * If this resource exists, its name can be safely assumed to be valid.
	 * </p>
	 * 
	 * @return the name of the virtual resource
	 * @see #getRuntimePath()
	 */
	public String getName();

	/**
	 * Returns the name of the component that contains this virtual resource.
	 * <p>
	 * Each virtual resource is contained by at least one component. A component
	 * represents a logical collection of files. If the underlying resource is 
	 * contained by multiple components, then the component name returned by
	 * this method will be determined by how the virtual resource was created. 
	 * For each virtual resource, the component name will be the same as the 
	 * component name of the parent.
	 *  
	 * @return the name of the component that contains the virtual resource
	 */
	public String getComponentName();

	/**
	 * Returns the virtual resource which contains this virtual resource, or <code>null</code> if it has
	 * no parent (that is, the virtual resource represents the root of the component).
	 * <p>
	 * The full path of the parent resource is the same as this resource's full path with the last
	 * segment removed.
	 * </p>
	 * <p>
	 * This is a resource handle operation; neither the resource nor the resulting resource need
	 * exist.
	 * </p>
	 * 
	 * @return the container of the virtual resource, or <code>null</code> if this virtual resource represents the root of the component
	 */
	public IVirtualContainer getParent();

	/**
	 * Returns the project which contains the component which contains this virtual resource. 
	 * <p>
	 * The name of the project may not (and most likely will not) be referenced in the 
	 * runtime path of this virtual path, but will be referenced by the workspace-relative path. 
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
	 * <code>FOLDER</code>, <code>COMPONENT</code>
	 * <p>
	 * <ul>
	 * <li> All resources of type <code>FILE</code> implement <code>IVirtualFile</code>.</li>
	 * <li> All resources of type <code>FOLDER</code> implement <code>IVirtualFolder</code>.</li>
	 * <li> All resources of type <code>COMPONENT</code> implement <code>IVirtualContainer</code>.</li>
	 * </ul>
	 * </p>
	 * <p>
	 * This is a resource handle operation; the resource need not exist in the workspace.
	 * </p>
	 * 
	 * @return the type of this resource
	 * @see #FILE
	 * @see #FOLDER
	 * @see #COMPONENT
	 */
	public int getType();
	
	/**
	 * A virtual resource is a representation of one or more Eclipse Platform resources. 
	 * <p>
	 * Returns the "primary" underlying resource. The resource may or may not exist. The resource
	 * will be contained by the project returned by {@link #getProject()}.  
	 * </p>
	 * <p>
	 * Since a virtual resource could represent multiple resources, this method will return 
	 * the "primary" resource. For clients that wish to take advantage of the multiple resources
	 * at a single path, use {@link #getUnderlyingResources()}. 
	 * @return The primary resource that backs this virtual resource.
	 */
	public IResource getUnderlyingResource();
	
	/**
	 * A virtual resource is a representation of one or more Eclipse Platform resources. 
	 * <p>
	 * Returns all underlying resources. The resources may or may not exist. The resources
	 * will be contained by the project returned by {@link #getProject()}.  
	 * </p>
	 * <p>
	 * Since a virtual resource could represent multiple resources, this method will return 
	 * all underlying resources. For clients that prefer to acknowledge only one resource, 
	 * at a single path, use {@link #getUnderlyingResource()}. 
	 * @return All resources that back this virtual resource.
	 */
	public IResource[] getUnderlyingResources(); 

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
