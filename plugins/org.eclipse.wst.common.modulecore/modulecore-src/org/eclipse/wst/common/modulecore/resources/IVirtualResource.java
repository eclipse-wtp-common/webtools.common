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
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

public interface IVirtualResource extends ISchedulingRule {

	/*
	 * ==================================================================== Constants defining
	 * resource types: There are four possible resource types and their type constants are in the
	 * integer range 1 to 8 as defined below.
	 * ====================================================================
	 */

	/**
	 * Type constant (bit mask value 1) which identifies file resources.
	 * 
	 * @see IVirtualResource#getType()
	 * @see IVirtualFile
	 */
	public static final int FILE = 0x1;

	/**
	 * Type constant (bit mask value 2) which identifies folder resources.
	 * 
	 * @see IVirtualResource#getType()
	 * @see IVirtualFolder
	 */
	public static final int FOLDER = 0x2;

	/**
	 * Type constant (bit mask value 4) which identifies project resources.
	 * 
	 * @see IVirtualResource#getType()
	 * @see IProject
	 */
	public static final int PROJECT = 0x4;

	/**
	 * Type constant (bit mask value 8) which identifies the root resource.
	 * 
	 * @see IVirtualResource#getType()
	 * @see IWorkspaceRoot
	 */
	public static final int ROOT = 0x8;

	/*
	 * ==================================================================== Constants defining the
	 * depth of resource tree traversal:
	 * ====================================================================
	 */

	/**
	 * Depth constant (value 0) indicating this resource, but not any of its members.
	 */
	public static final int DEPTH_ZERO = 0;

	/**
	 * Depth constant (value 1) indicating this resource and its direct members.
	 */
	public static final int DEPTH_ONE = 1;

	/**
	 * Depth constant (value 2) indicating this resource and its direct and indirect members at any
	 * depth.
	 */
	public static final int DEPTH_INFINITE = 2;

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
	public static final int FORCE = 0x1;

	/**
	 * Update flag constant (bit mask value 2) indicating that the operation should maintain local
	 * history by taking snapshots of the contents of files just before being overwritten or
	 * deleted.
	 * 
	 * @see IVirtualFile#getHistory(IProgressMonitor)
	 * @since 2.0
	 */
	public static final int KEEP_HISTORY = 0x2;

	/**
	 * Update flag constant (bit mask value 4) indicating that the operation should delete the files
	 * and folders of a project.
	 * <p>
	 * Deleting a project that is open ordinarily deletes all its files and folders, whereas
	 * deleting a project that is closed retains its files and folders. Specifying
	 * <code>ALWAYS_DELETE_PROJECT_CONTENT</code> indicates that the contents of a project are to
	 * be deleted regardless of whether the project is open or closed at the time; specifying
	 * <code>NEVER_DELETE_PROJECT_CONTENT</code> indicates that the contents of a project are to
	 * be retained regardless of whether the project is open or closed at the time.
	 * </p>
	 * 
	 * @see #NEVER_DELETE_PROJECT_CONTENT
	 * @since 2.0
	 */
	public static final int ALWAYS_DELETE_PROJECT_CONTENT = 0x4;

	/**
	 * Update flag constant (bit mask value 8) indicating that the operation should preserve the
	 * files and folders of a project.
	 * <p>
	 * Deleting a project that is open ordinarily deletes all its files and folders, whereas
	 * deleting a project that is closed retains its files and folders. Specifying
	 * <code>ALWAYS_DELETE_PROJECT_CONTENT</code> indicates that the contents of a project are to
	 * be deleted regardless of whether the project is open or closed at the time; specifying
	 * <code>NEVER_DELETE_PROJECT_CONTENT</code> indicates that the contents of a project are to
	 * be retained regardless of whether the project is open or closed at the time.
	 * </p>
	 * 
	 * @see #ALWAYS_DELETE_PROJECT_CONTENT
	 * @since 2.0
	 */
	public static final int NEVER_DELETE_PROJECT_CONTENT = 0x8;

	/**
	 * Update flag constant (bit mask value 16) indicating that the link creation should proceed
	 * even if the local file system file or directory is missing.
	 * 
	 * @see IVirtualFolder#createLink(IPath, int, IProgressMonitor)
	 * @see IVirtualFile#createLink(IPath, int, IProgressMonitor)
	 * @since 2.1
	 */
	public static final int ALLOW_MISSING_LOCAL = 0x10;

	/**
	 * Update flag constant (bit mask value 32) indicating that a copy or move operation should only
	 * copy the link, rather than copy the underlying contents of the linked resource.
	 * 
	 * @see #copy(IPath, int, IProgressMonitor)
	 * @see #move(IPath, int, IProgressMonitor)
	 * @since 2.1
	 */
	public static final int SHALLOW = 0x20;

	/**
	 * Update flag constant (bit mask value 64) indicating that setting the project description
	 * should not attempt to configure and deconfigure natures.
	 * 
	 * @see IProject#setDescription(IProjectDescription, int, IProgressMonitor)
	 * @since 3.0
	 */
	public static final int AVOID_NATURE_CONFIG = 0x40;

	/**
	 * Update flag constant (bit mask value 128) indicating that opening a project for the first
	 * time should refresh in the background.
	 * 
	 * @see IProject#open(int, IProgressMonitor)
	 * @since 3.1
	 */
	public static final int BACKGROUND_REFRESH = 0x80;

	/*
	 * ==================================================================== Other constants:
	 * ====================================================================
	 */

	/**
	 * Modification stamp constant (value -1) indicating no modification stamp is available.
	 * 
	 * @see #getModificationStamp()
	 */
	public static final int NULL_STAMP = -1;

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

	/**
	 * Accepts the given visitor for an optimized traversal. The visitor's <code>visit</code>
	 * method is called, and is provided with a proxy to this resource. The proxy is a transient
	 * object that can be queried very quickly for information about the resource. If the actual
	 * resource handle is needed, it can be obtained from the proxy. Requesting the resource handle,
	 * or the full path of the resource, will degrade performance of the visit.
	 * <p>
	 * The entire subtree under the given resource is traversed to infinite depth, unless the
	 * visitor ignores a subtree by returning <code>false</code> from its <code>visit</code>
	 * method.
	 * </p>
	 * <p>
	 * No guarantees are made about the behavior of this method if resources are deleted or added
	 * during the traversal of this resource hierarchy. If resources are deleted during the
	 * traversal, they may still be passed to the visitor; if resources are created, they may not be
	 * passed to the visitor. If resources other than the one being visited are modified during the
	 * traversal, the resource proxy may contain stale information when that resource is visited.
	 * </p>
	 * <p>
	 * If the <code>INCLUDE_PHANTOMS</code> flag is not specified in the member flags
	 * (recommended), only member resources that exist will be visited. If the
	 * <code>INCLUDE_PHANTOMS</code> flag is specified, the visit will also include any phantom
	 * member resource that the workspace is keeping track of.
	 * </p>
	 * <p>
	 * If the <code>INCLUDE_TEAM_PRIVATE_MEMBERS</code> flag is not specified (recommended), team
	 * private members will not be visited. If the <code>INCLUDE_TEAM_PRIVATE_MEMBERS</code> flag
	 * is specified in the member flags, team private member resources are visited as well.
	 * </p>
	 * 
	 * @param visitor
	 *            the visitor
	 * @param memberFlags
	 *            bit-wise or of member flag constants (<code>IVirtualContainer.INCLUDE_PHANTOMS</code>
	 *            and <code>INCLUDE_TEAM_PRIVATE_MEMBERS</code>) indicating which members are of
	 *            interest
	 * @exception CoreException
	 *                if this request fails. Reasons include:
	 *                <ul>
	 *                <li> the <code>INCLUDE_PHANTOMS</code> flag is not specified and this
	 *                resource does not exist.</li>
	 *                <li> the <code>INCLUDE_PHANTOMS</code> flag is not specified and this
	 *                resource is a project that is not open.</li>
	 *                <li> The visitor failed with this exception.</li>
	 *                </ul>
	 * @see IVirtualContainer#INCLUDE_PHANTOMS
	 * @see IVirtualContainer#INCLUDE_TEAM_PRIVATE_MEMBERS
	 * @see IVirtualResource#isPhantom()
	 * @see IVirtualResource#isTeamPrivateMember()
	 * @see IVirtualResourceProxyVisitor#visit(IVirtualResourceProxy)
	 * @since 2.1
	 */
	public void accept(final IResourceProxyVisitor visitor, int memberFlags) throws CoreException;

	/**
	 * Accepts the given visitor. The visitor's <code>visit</code> method is called with this
	 * resource. If the visitor returns <code>true</code>, this method visits this resource's
	 * members.
	 * <p>
	 * This is a convenience method, fully equivalent to
	 * <code>accept(visitor,IVirtualResource.DEPTH_INFINITE, IVirtualResource.NONE)</code>.
	 * </p>
	 * 
	 * @param visitor
	 *            the visitor
	 * @exception CoreException
	 *                if this method fails. Reasons include:
	 *                <ul>
	 *                <li> This resource does not exist.</li>
	 *                <li> The visitor failed with this exception.</li>
	 *                </ul>
	 * @see IResourceVisitor#visit(IVirtualResource)
	 * @see #accept(IResourceVisitor,int,int)
	 */
	public void accept(IResourceVisitor visitor) throws CoreException;

	/**
	 * Accepts the given visitor. The visitor's <code>visit</code> method is called with this
	 * resource. If the visitor returns <code>false</code>, this resource's members are not
	 * visited.
	 * <p>
	 * The subtree under the given resource is traversed to the supplied depth.
	 * </p>
	 * <p>
	 * This is a convenience method, fully equivalent to:
	 * 
	 * <pre>
	 * accept(visitor, depth, includePhantoms ? INCLUDE_PHANTOMS : IVirtualResource.NONE);
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param visitor
	 *            the visitor
	 * @param depth
	 *            the depth to which members of this resource should be visited. One of
	 *            <code>DEPTH_ZERO</code>, <code>DEPTH_ONE</code>, or
	 *            <code>DEPTH_INFINITE</code>.
	 * @param includePhantoms
	 *            <code>true</code> if phantom resources are of interest; <code>false</code> if
	 *            phantom resources are not of interest.
	 * @exception CoreException
	 *                if this request fails. Reasons include:
	 *                <ul>
	 *                <li> <code>includePhantoms</code> is <code>false</code> and this resource
	 *                does not exist.</li>
	 *                <li> <code>includePhantoms</code> is <code>true</code> and this resource
	 *                does not exist and is not a phantom.</li>
	 *                <li> The visitor failed with this exception.</li>
	 *                </ul>
	 * @see IVirtualResource#isPhantom()
	 * @see IResourceVisitor#visit(IVirtualResource)
	 * @see IVirtualResource#DEPTH_ZERO
	 * @see IVirtualResource#DEPTH_ONE
	 * @see IVirtualResource#DEPTH_INFINITE
	 * @see IVirtualResource#accept(IResourceVisitor,int,int)
	 */
	public void accept(IResourceVisitor visitor, int depth, boolean includePhantoms) throws CoreException;

	/**
	 * Accepts the given visitor. The visitor's <code>visit</code> method is called with this
	 * resource. If the visitor returns <code>false</code>, this resource's members are not
	 * visited.
	 * <p>
	 * The subtree under the given resource is traversed to the supplied depth.
	 * </p>
	 * <p>
	 * No guarantees are made about the behavior of this method if resources are deleted or added
	 * during the traversal of this resource hierarchy. If resources are deleted during the
	 * traversal, they may still be passed to the visitor; if resources are created, they may not be
	 * passed to the visitor.
	 * </p>
	 * <p>
	 * If the <code>INCLUDE_PHANTOMS</code> flag is not specified in the member flags
	 * (recommended), only member resources that exists are visited. If the
	 * <code>INCLUDE_PHANTOMS</code> flag is specified, the visit also includes any phantom member
	 * resource that the workspace is keeping track of.
	 * </p>
	 * <p>
	 * If the <code>INCLUDE_TEAM_PRIVATE_MEMBERS</code> flag is not specified (recommended), team
	 * private members are not visited. If the <code>INCLUDE_TEAM_PRIVATE_MEMBERS</code> flag is
	 * specified in the member flags, team private member resources are visited as well.
	 * </p>
	 * <p>
	 * If the <code>EXCLUDE_DERIVED</code> flag is not specified (recommended), derived resources
	 * are visited. If the <code>EXCLUDE_DERIVED</code> flag is specified in the member flags,
	 * derived resources are not visited.
	 * </p>
	 * 
	 * @param visitor
	 *            the visitor
	 * @param depth
	 *            the depth to which members of this resource should be visited. One of
	 *            <code>DEPTH_ZERO</code>, <code>DEPTH_ONE</code>, or
	 *            <code>DEPTH_INFINITE</code>.
	 * @param memberFlags
	 *            bit-wise or of member flag constants (<code>INCLUDE_PHANTOMS</code>,
	 *            <code>INCLUDE_TEAM_PRIVATE_MEMBERS</code> and <code>EXCLUDE_DERIVED</code>)
	 *            indicating which members are of interest
	 * @exception CoreException
	 *                if this request fails. Reasons include:
	 *                <ul>
	 *                <li> the <code>INCLUDE_PHANTOMS</code> flag is not specified and this
	 *                resource does not exist.</li>
	 *                <li> the <code>INCLUDE_PHANTOMS</code> flag is not specified and this
	 *                resource is a project that is not open.</li>
	 *                <li> The visitor failed with this exception.</li>
	 *                </ul>
	 * @see IVirtualContainer#INCLUDE_PHANTOMS
	 * @see IVirtualContainer#INCLUDE_TEAM_PRIVATE_MEMBERS
	 * @see IVirtualContainer#EXCLUDE_DERIVED
	 * @see IVirtualResource#isDerived()
	 * @see IVirtualResource#isPhantom()
	 * @see IVirtualResource#isTeamPrivateMember()
	 * @see IVirtualResource#DEPTH_ZERO
	 * @see IVirtualResource#DEPTH_ONE
	 * @see IVirtualResource#DEPTH_INFINITE
	 * @see IResourceVisitor#visit(IVirtualResource)
	 * @since 2.0
	 */
	public void accept(IResourceVisitor visitor, int depth, int memberFlags) throws CoreException; 

	/**
	 * Makes a copy of this resource at the given path.
	 * <p>
	 * This is a convenience method, fully equivalent to:
	 * 
	 * <pre>
	 * copy(destination, (force ? FORCE : IVirtualResource.NONE), monitor);
	 * </pre>
	 * 
	 * </p>
	 * <p>
	 * This operation changes resources; these changes will be reported in a subsequent resource
	 * change event that will include an indication that the resource copy has been added to its new
	 * parent.
	 * </p>
	 * <p>
	 * This operation is long-running; progress and cancellation are provided by the given progress
	 * monitor.
	 * </p>
	 * 
	 * @param destination
	 *            the destination path
	 * @param force
	 *            a flag controlling whether resources that are not in sync with the local file
	 *            system will be tolerated
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting is not desired
	 * @exception CoreException
	 *                if this resource could not be copied. Reasons include:
	 *                <ul>
	 *                <li> This resource does not exist.</li>
	 *                <li> This resource or one of its descendents is not local.</li>
	 *                <li> The source or destination is the workspace root.</li>
	 *                <li> The source is a project but the destination is not.</li>
	 *                <li> The destination is a project but the source is not.</li>
	 *                <li> The resource corresponding to the parent destination path does not exist.</li>
	 *                <li> The resource corresponding to the parent destination path is a closed
	 *                project.</li>
	 *                <li> A resource at destination path does exist.</li>
	 *                <li> This resource or one of its descendents is out of sync with the local
	 *                file system and <code>force</code> is <code>false</code>.</li>
	 *                <li> The workspace and the local file system are out of sync at the
	 *                destination resource or one of its descendents.</li>
	 *                <li> The source resource is a file and the destination path specifies a
	 *                project.</li>
	 *                <li> Resource changes are disallowed during certain types of resource change
	 *                event notification. See <code>IVirtualResourceChangeEvent</code> for more details.</li>
	 *                </ul>
	 * @exception OperationCanceledException
	 *                if the operation is canceled. Cancelation can occur even if no progress
	 *                monitor is provided.
	 */
	public void copy(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException;

	/**
	 * Makes a copy of this resource at the given path. The resource's descendents are copied as
	 * well. The path of this resource must not be a prefix of the destination path. The workspace
	 * root may not be the source or destination location of a copy operation, and a project can
	 * only be copied to another project. After successful completion, corresponding new resources
	 * will exist at the given path; their contents and properties will be copies of the originals.
	 * The original resources are not affected.
	 * <p>
	 * The supplied path may be absolute or relative. Absolute paths fully specify the new location
	 * for the resource, including its project. Relative paths are considered to be relative to the
	 * container of the resource being copied. A trailing separator is ignored.
	 * </p>
	 * <p>
	 * Calling this method with a one segment absolute destination path is equivalent to calling:
	 * 
	 * <pre>
	 * copy(workspace.newProjectDescription(folder.getName()), updateFlags, monitor);
	 * </pre>
	 * 
	 * </p>
	 * <p>
	 * When a resource is copied, its persistent properties are copied with it. Session properties
	 * and markers are not copied.
	 * </p>
	 * <p>
	 * The <code>FORCE</code> update flag controls how this method deals with cases where the
	 * workspace is not completely in sync with the local file system. If <code>FORCE</code> is
	 * not specified, the method will only attempt to copy resources that are in sync with the
	 * corresponding files and directories in the local file system; it will fail if it encounters a
	 * resource that is out of sync with the file system. However, if <code>FORCE</code> is
	 * specified, the method copies all corresponding files and directories from the local file
	 * system, including ones that have been recently updated or created. Note that in both settings
	 * of the <code>FORCE</code> flag, the operation fails if the newly created resources in the
	 * workspace would be out of sync with the local file system; this ensures files in the file
	 * system cannot be accidentally overwritten.
	 * </p>
	 * <p>
	 * The <code>SHALLOW</code> update flag controls how this method deals with linked resources.
	 * If <code>SHALLOW</code> is not specified, then the underlying contents of the linked
	 * resource will always be copied in the file system. In this case, the destination of the copy
	 * will never be a linked resource or contain any linked resources. If <code>SHALLOW</code> is
	 * specified when a linked resource is copied into another project, a new linked resource is
	 * created in the destination project that points to the same file system location. When a
	 * project containing linked resources is copied, the new project will contain the same linked
	 * resources pointing to the same file system locations. For both of these shallow cases, no
	 * files on disk under the linked resource are actually copied. With the <code>SHALLOW</code>
	 * flag, copying of linked resources into anything other than a project is not permitted. The
	 * <code>SHALLOW</code> update flag is ignored when copying non- linked resources.
	 * </p>
	 * <p>
	 * Update flags other than <code>FORCE</code> and <code>SHALLOW</code> are ignored.
	 * </p>
	 * <p>
	 * This operation changes resources; these changes will be reported in a subsequent resource
	 * change event that will include an indication that the resource copy has been added to its new
	 * parent.
	 * </p>
	 * <p>
	 * An attempt will be made to copy the local history for this resource and its children, to the
	 * destination. Since local history existence is a safety-net mechanism, failure of this action
	 * will not result in automatic failure of the copy operation.
	 * </p>
	 * <p>
	 * This operation is long-running; progress and cancellation are provided by the given progress
	 * monitor.
	 * </p>
	 * 
	 * @param destination
	 *            the destination path
	 * @param updateFlags
	 *            bit-wise or of update flag constants (<code>FORCE</code> and
	 *            <code>SHALLOW</code>)
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting is not desired
	 * @exception CoreException
	 *                if this resource could not be copied. Reasons include:
	 *                <ul>
	 *                <li> This resource does not exist.</li>
	 *                <li> This resource or one of its descendents is not local.</li>
	 *                <li> The source or destination is the workspace root.</li>
	 *                <li> The source is a project but the destination is not.</li>
	 *                <li> The destination is a project but the source is not.</li>
	 *                <li> The resource corresponding to the parent destination path does not exist.</li>
	 *                <li> The resource corresponding to the parent destination path is a closed
	 *                project.</li>
	 *                <li> The source is a linked resource, but the destination is not a project,
	 *                and <code>SHALLOW</code> is specified.</li>
	 *                <li> A resource at destination path does exist.</li>
	 *                <li> This resource or one of its descendents is out of sync with the local
	 *                file system and <code>FORCE</code> is not specified.</li>
	 *                <li> The workspace and the local file system are out of sync at the
	 *                destination resource or one of its descendents.</li>
	 *                <li> The source resource is a file and the destination path specifies a
	 *                project.</li>
	 *                <li> The source is a linked resource, and the destination path does not
	 *                specify a project.</li>
	 *                <li> The location of the source resource on disk is the same or a prefix of
	 *                the location of the destination resource on disk.</li>
	 *                <li> Resource changes are disallowed during certain types of resource change
	 *                event notification. See <code>IVirtualResourceChangeEvent</code> for more details.</li>
	 *                </ul>
	 * @exception OperationCanceledException
	 *                if the operation is canceled. Cancelation can occur even if no progress
	 *                monitor is provided.
	 * @see #FORCE
	 * @see #SHALLOW
	 * @see IVirtualResourceRuleFactory#copyRule(IVirtualResource, IVirtualResource)
	 * @since 2.0
	 */
	public void copy(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException;

	/**
	 * Deletes this resource from the workspace.
	 * <p>
	 * This is a convenience method, fully equivalent to:
	 * 
	 * <pre>
	 * delete(force ? FORCE : IVirtualResource.NONE, monitor);
	 * </pre>
	 * 
	 * </p>
	 * <p>
	 * This method changes resources; these changes will be reported in a subsequent resource change
	 * event.
	 * </p>
	 * <p>
	 * This method is long-running; progress and cancellation are provided by the given progress
	 * monitor.
	 * </p>
	 * 
	 * @param force
	 *            a flag controlling whether resources that are not in sync with the local file
	 *            system will be tolerated
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting is not desired
	 * @exception CoreException
	 *                if this method fails. Reasons include:
	 *                <ul>
	 *                <li> This resource could not be deleted for some reason.</li>
	 *                <li> This resource or one of its descendents is out of sync with the local
	 *                file system and <code>force</code> is <code>false</code>.</li>
	 *                <li> Resource changes are disallowed during certain types of resource change
	 *                event notification. See <code>IVirtualResourceChangeEvent</code> for more details.</li>
	 *                </ul>
	 * @exception OperationCanceledException
	 *                if the operation is canceled. Cancelation can occur even if no progress
	 *                monitor is provided.
	 * 
	 * @see IVirtualResource#delete(int,IProgressMonitor)
	 */
	public void delete(boolean force, IProgressMonitor monitor) throws CoreException;

	/**
	 * Deletes this resource from the workspace. Deletion applies recursively to all members of this
	 * resource in a "best- effort" fashion. That is, all resources which can be deleted are
	 * deleted. Resources which could not be deleted are noted in a thrown exception. The method
	 * does not fail if resources do not exist; it fails only if resources could not be deleted.
	 * <p>
	 * Deleting a non-linked resource also deletes its contents from the local file system. In the
	 * case of a file or folder resource, the corresponding file or directory in the local file
	 * system is deleted. Deleting an open project recursively deletes its members; deleting a
	 * closed project just gets rid of the project itself (closed projects have no members); files
	 * in the project's local content area are retained; referenced projects are unaffected.
	 * </p>
	 * <p>
	 * Deleting a linked resource does not delete its contents from the file system, it just removes
	 * that resource and its children from the workspace. Deleting children of linked resources does
	 * remove the contents from the file system.
	 * </p>
	 * <p>
	 * Deleting a resource also deletes its session and persistent properties and markers.
	 * </p>
	 * <p>
	 * Deleting a non-project resource which has sync information converts the resource to a phantom
	 * and retains the sync information for future use.
	 * </p>
	 * <p>
	 * Deleting the workspace root resource recursively deletes all projects, and removes all
	 * markers, properties, sync info and other data related to the workspace root; the root
	 * resource itself is not deleted, however.
	 * </p>
	 * <p>
	 * This method changes resources; these changes will be reported in a subsequent resource change
	 * event.
	 * </p>
	 * <p>
	 * This method is long-running; progress and cancellation are provided by the given progress
	 * monitor.
	 * </p>
	 * <p>
	 * The <code>FORCE</code> update flag controls how this method deals with cases where the
	 * workspace is not completely in sync with the local file system. If <code>FORCE</code> is
	 * not specified, the method will only attempt to delete files and directories in the local file
	 * system that correspond to, and are in sync with, resources in the workspace; it will fail if
	 * it encounters a file or directory in the file system that is out of sync with the workspace.
	 * This option ensures there is no unintended data loss; it is the recommended setting. However,
	 * if <code>FORCE</code> is specified, the method will ruthlessly attempt to delete
	 * corresponding files and directories in the local file system, including ones that have been
	 * recently updated or created.
	 * </p>
	 * <p>
	 * The <code>KEEP_HISTORY</code> update flag controls whether or not files that are about to
	 * be deleted from the local file system have their current contents saved in the workspace's
	 * local history. The local history mechanism serves as a safety net to help the user recover
	 * from mistakes that might otherwise result in data loss. Specifying <code>KEEP_HISTORY</code>
	 * is recommended except in circumstances where past states of the files are of no conceivable
	 * interest to the user. Note that local history is maintained with each individual project, and
	 * gets discarded when a project is deleted from the workspace. Hence <code>KEEP_HISTORY</code>
	 * is only really applicable when deleting files and folders, but not projects.
	 * </p>
	 * <p>
	 * The <code>ALWAYS_DELETE_PROJECT_CONTENTS</code> update flag controls how project deletions
	 * are handled. If <code>ALWAYS_DELETE_PROJECT_CONTENTS</code> is specified, then the files
	 * and folders in a project's local content area are deleted, regardless of whether the project
	 * is open or closed; <code>FORCE</code> is assumed regardless of whether it is specified. If
	 * <code>NEVER_DELETE_PROJECT_CONTENTS</code> is specified, then the files and folders in a
	 * project's local content area are retained, regardless of whether the project is open or
	 * closed; the <code>FORCE</code> flag is ignored. If neither of these flags is specified,
	 * files and folders in a project's local content area from open projects (subject to the
	 * <code>FORCE</code> flag), but never from closed projects.
	 * </p>
	 * 
	 * @param updateFlags
	 *            bit-wise or of update flag constants ( <code>FORCE</code>,
	 *            <code>KEEP_HISTORY</code>, <code>ALWAYS_DELETE_PROJECT_CONTENTS</code>, and
	 *            <code>NEVER_DELETE_PROJECT_CONTENTS</code>)
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting is not desired
	 * @exception CoreException
	 *                if this method fails. Reasons include:
	 *                <ul>
	 *                <li> This resource could not be deleted for some reason.</li>
	 *                <li> This resource or one of its descendents is out of sync with the local
	 *                file system and <code>FORCE</code> is not specified.</li>
	 *                <li> Resource changes are disallowed during certain types of resource change
	 *                event notification. See <code>IVirtualResourceChangeEvent</code> for more details.</li>
	 *                </ul>
	 * @exception OperationCanceledException
	 *                if the operation is canceled. Cancelation can occur even if no progress
	 *                monitor is provided.
	 * @see IVirtualFile#delete(boolean, boolean, IProgressMonitor)
	 * @see IVirtualFolder#delete(boolean, boolean, IProgressMonitor)
	 * @see #FORCE
	 * @see #KEEP_HISTORY
	 * @see #ALWAYS_DELETE_PROJECT_CONTENT
	 * @see #NEVER_DELETE_PROJECT_CONTENT
	 * @see IVirtualResourceRuleFactory#deleteRule(IVirtualResource)
	 * @since 2.0
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
	 * @see #getWorkspace()
	 */
	public boolean equals(Object other);

	/**
	 * Returns whether this resource exists in the workspace.
	 * <p>
	 * <code>IVirtualResource</code> objects are lightweight handle objects used to access resources in
	 * the workspace. However, having a handle object does not necessarily mean the workspace really
	 * has such a resource. When the workspace does have a genuine resource of a matching type, the
	 * resource is said to <em>exist</em>, and this method returns <code>true</code>; in all
	 * other cases, this method returns <code>false</code>. In particular, it returns
	 * <code>false</code> if the workspace has no resource at that path, or if it has a resource
	 * at that path with a type different from the type of this resource handle.
	 * </p>
	 * <p>
	 * Note that no resources ever exist under a project that is closed; opening a project may bring
	 * some resources into existence.
	 * </p>
	 * <p>
	 * The name and path of a resource handle may be invalid. However, validation checks are done
	 * automatically as a resource is created; this means that any resource that exists can be
	 * safely assumed to have a valid name and path.
	 * </p>
	 * 
	 * @return <code>true</code> if the resource exists, otherwise <code>false</code>
	 */
	public boolean exists(); 
	
	/**
	 * Returns the file extension portion of this resource's name, or <code>null</code> if it does
	 * not have one.
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
	 * Returns the full, absolute path of this resource relative to the workspace.
	 * <p>
	 * This is a resource handle operation; the resource need not exist. If this resource does
	 * exist, its path can be safely assumed to be valid.
	 * </p>
	 * <p>
	 * A resource's full path indicates the route from the root of the workspace to the resource.
	 * Within a workspace, there is exactly one such path for any given resource. The first segment
	 * of these paths name a project; remaining segments, folders and/or files within that project.
	 * The returned path never has a trailing separator. The path of the workspace root is
	 * <code>Path.ROOT</code>.
	 * </p>
	 * <p>
	 * Since absolute paths contain the name of the project, they are vulnerable when the project is
	 * renamed. For most situations, project-relative paths are recommended over absolute paths.
	 * </p>
	 * 
	 * @return the absolute path of this resource
	 * @see #getProjectRelativePath()
	 * @see Path#ROOT
	 */
	public IPath getWorkspaceRelativePath(); 
	

	/**
	 * Returns a relative path of this resource with respect to its project. Returns the empty path
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
	
	/**
	 * Returns whether this resource is marked as read-only in the file system.
	 * 
	 * @return <code>true</code> if this resource is read-only, <code>false</code> otherwise
	 * @deprecated use <tt>IVirtualResource#getResourceAttributes()</tt>
	 */
	public boolean isReadOnly();  

	/**
	 * Moves this resource so that it is located at the given path.
	 * <p>
	 * This is a convenience method, fully equivalent to:
	 * 
	 * <pre>
	 * move(destination, force ? FORCE : IVirtualResource.NONE, monitor);
	 * </pre>
	 * 
	 * </p>
	 * <p>
	 * This method changes resources; these changes will be reported in a subsequent resource change
	 * event that will include an indication that the resource has been removed from its parent and
	 * that a corresponding resource has been added to its new parent. Additional information
	 * provided with resource delta shows that these additions and removals are related.
	 * </p>
	 * <p>
	 * This method is long-running; progress and cancellation are provided by the given progress
	 * monitor.
	 * </p>
	 * 
	 * @param destination
	 *            the destination path
	 * @param force
	 *            a flag controlling whether resources that are not in sync with the local file
	 *            system will be tolerated
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting is not desired
	 * @exception CoreException
	 *                if this resource could not be moved. Reasons include:
	 *                <ul>
	 *                <li> This resource does not exist.</li>
	 *                <li> This resource or one of its descendents is not local.</li>
	 *                <li> The source or destination is the workspace root.</li>
	 *                <li> The source is a project but the destination is not.</li>
	 *                <li> The destination is a project but the source is not.</li>
	 *                <li> The resource corresponding to the parent destination path does not exist.</li>
	 *                <li> The resource corresponding to the parent destination path is a closed
	 *                project.</li>
	 *                <li> A resource at destination path does exist.</li>
	 *                <li> A resource of a different type exists at the destination path.</li>
	 *                <li> This resource or one of its descendents is out of sync with the local
	 *                file system and <code>force</code> is <code>false</code>.</li>
	 *                <li> The workspace and the local file system are out of sync at the
	 *                destination resource or one of its descendents.</li>
	 *                <li> Resource changes are disallowed during certain types of resource change
	 *                event notification. See <code>IVirtualResourceChangeEvent</code> for more details.</li>
	 *                <li> The source resource is a file and the destination path specifies a
	 *                project.</li>
	 *                </ul>
	 * @exception OperationCanceledException
	 *                if the operation is canceled. Cancelation can occur even if no progress
	 *                monitor is provided.
	 * @see IVirtualResourceDelta#getFlags()
	 */
	public void move(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException;

	/**
	 * Moves this resource so that it is located at the given path. The path of the resource must
	 * not be a prefix of the destination path. The workspace root may not be the source or
	 * destination location of a move operation, and a project can only be moved to another project.
	 * After successful completion, the resource and any direct or indirect members will no longer
	 * exist; but corresponding new resources will now exist at the given path.
	 * <p>
	 * The supplied path may be absolute or relative. Absolute paths fully specify the new location
	 * for the resource, including its project. Relative paths are considered to be relative to the
	 * container of the resource being moved. A trailing slash is ignored.
	 * </p>
	 * <p>
	 * Calling this method with a one segment absolute destination path is equivalent to calling:
	 * 
	 * <pre>
	 * IProjectDescription description = getDescription();
	 * description.setName(path.lastSegment());
	 * move(description, updateFlags, monitor);
	 * </pre>
	 * 
	 * </p>
	 * <p>
	 * When a resource moves, its session and persistent properties move with it. Likewise for all
	 * other attributes of the resource including markers.
	 * </p>
	 * <p>
	 * The <code>FORCE</code> update flag controls how this method deals with cases where the
	 * workspace is not completely in sync with the local file system. If <code>FORCE</code> is
	 * not specified, the method will only attempt to move resources that are in sync with the
	 * corresponding files and directories in the local file system; it will fail if it encounters a
	 * resource that is out of sync with the file system. However, if <code>FORCE</code> is
	 * specified, the method moves all corresponding files and directories from the local file
	 * system, including ones that have been recently updated or created. Note that in both settings
	 * of the <code>FORCE</code> flag, the operation fails if the newly created resources in the
	 * workspace would be out of sync with the local file system; this ensures files in the file
	 * system cannot be accidentally overwritten.
	 * </p>
	 * <p>
	 * The <code>KEEP_HISTORY</code> update flag controls whether or not file that are about to be
	 * deleted from the local file system have their current contents saved in the workspace's local
	 * history. The local history mechanism serves as a safety net to help the user recover from
	 * mistakes that might otherwise result in data loss. Specifying <code>KEEP_HISTORY</code> is
	 * recommended except in circumstances where past states of the files are of no conceivable
	 * interest to the user. Note that local history is maintained with each individual project, and
	 * gets discarded when a project is deleted from the workspace. Hence <code>KEEP_HISTORY</code>
	 * is only really applicable when moving files and folders, but not whole projects.
	 * </p>
	 * <p>
	 * If this resource is not a project, an attempt will be made to copy the local history for this
	 * resource and its children, to the destination. Since local history existence is a safety-net
	 * mechanism, failure of this action will not result in automatic failure of the move operation.
	 * </p>
	 * <p>
	 * The <code>SHALLOW</code> update flag controls how this method deals with linked resources.
	 * If <code>SHALLOW</code> is not specified, then the underlying contents of the linked
	 * resource will always be moved in the file system. In this case, the destination of the move
	 * will never be a linked resource or contain any linked resources. If <code>SHALLOW</code> is
	 * specified when a linked resource is moved into another project, a new linked resource is
	 * created in the destination project that points to the same file system location. When a
	 * project containing linked resources is moved, the new project will contain the same linked
	 * resources pointing to the same file system locations. For either of these cases, no files on
	 * disk under the linked resource are actually moved. With the <code>SHALLOW</code> flag,
	 * moving of linked resources into anything other than a project is not permitted. The
	 * <code>SHALLOW</code> update flag is ignored when moving non- linked resources.
	 * </p>
	 * <p>
	 * Update flags other than <code>FORCE</code>, <code>KEEP_HISTORY</code>and
	 * <code>SHALLOW</code> are ignored.
	 * </p>
	 * <p>
	 * This method changes resources; these changes will be reported in a subsequent resource change
	 * event that will include an indication that the resource has been removed from its parent and
	 * that a corresponding resource has been added to its new parent. Additional information
	 * provided with resource delta shows that these additions and removals are related.
	 * </p>
	 * <p>
	 * This method is long-running; progress and cancellation are provided by the given progress
	 * monitor.
	 * </p>
	 * 
	 * @param destination
	 *            the destination path
	 * @param updateFlags
	 *            bit-wise or of update flag constants (<code>FORCE</code>,
	 *            <code>KEEP_HISTORY</code> and <code>SHALLOW</code>)
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting is not desired
	 * @exception CoreException
	 *                if this resource could not be moved. Reasons include:
	 *                <ul>
	 *                <li> This resource does not exist.</li>
	 *                <li> This resource or one of its descendents is not local.</li>
	 *                <li> The source or destination is the workspace root.</li>
	 *                <li> The source is a project but the destination is not.</li>
	 *                <li> The destination is a project but the source is not.</li>
	 *                <li> The resource corresponding to the parent destination path does not exist.</li>
	 *                <li> The resource corresponding to the parent destination path is a closed
	 *                project.</li>
	 *                <li> The source is a linked resource, but the destination is not a project and
	 *                <code>SHALLOW</code> is specified.</li>
	 *                <li> A resource at destination path does exist.</li>
	 *                <li> A resource of a different type exists at the destination path.</li>
	 *                <li> This resource or one of its descendents is out of sync with the local
	 *                file system and <code>force</code> is <code>false</code>.</li>
	 *                <li> The workspace and the local file system are out of sync at the
	 *                destination resource or one of its descendents.</li>
	 *                <li> The source resource is a file and the destination path specifies a
	 *                project.</li>
	 *                <li> The location of the source resource on disk is the same or a prefix of
	 *                the location of the destination resource on disk.</li>
	 *                <li> Resource changes are disallowed during certain types of resource change
	 *                event notification. See <code>IVirtualResourceChangeEvent</code> for more details.</li>
	 *                </ul>
	 * @exception OperationCanceledException
	 *                if the operation is canceled. Cancelation can occur even if no progress
	 *                monitor is provided.
	 * @see IVirtualResourceDelta#getFlags()
	 * @see #FORCE
	 * @see #KEEP_HISTORY
	 * @see #SHALLOW
	 * @see IVirtualResourceRuleFactory#moveRule(IVirtualResource, IVirtualResource)
	 * @since 2.0
	 */
	public void move(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException; 

	/**
	 * Sets or unsets this resource as read-only in the file system.
	 * 
	 * @param readOnly
	 *            <code>true</code> to set it to read-only, <code>false</code> to unset
	 * @deprecated use <tt>IVirtualResource#setResourceAttributes(ResourceAttributes)</tt>
	 */
	public void setReadOnly(boolean readOnly);
 
}
