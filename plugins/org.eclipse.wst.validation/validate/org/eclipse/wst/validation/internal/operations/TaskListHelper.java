/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.operations;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.validation.internal.TaskListUtility;



/**
 * @deprecated This class is intended to be used only by the validation framework. The
 *             TaskListHelper class will be removed in Milestone 4.
 */
public class TaskListHelper {
	private static TaskListHelper _taskListHelper = null;

	public static TaskListHelper getTaskList() {
		if (_taskListHelper == null) {
			_taskListHelper = new TaskListHelper();
		}
		return _taskListHelper;
	}

	/**
	 * This method adds a message to a resource in the task list.
	 */
	public void addTask(String pluginId, IResource resource, String location, String messageId, String message, int markerType, String targetObjectName, String groupName, int offset, int length) throws CoreException {
		TaskListUtility.addTask(pluginId, resource, location, messageId, message, markerType, targetObjectName, groupName, offset, length);
	}


	public IMarker[] getValidationTasks(int severity, IProject project) {
		return TaskListUtility.getValidationTasks(severity, project);
	}

	public IMarker[] getValidationTasks(IResource resource, int severity) {
		return TaskListUtility.getValidationTasks(resource, severity);
	}

	public IMarker[] getValidationTasks(IResource resource, String messageOwner) {
		return TaskListUtility.getValidationTasks(resource, messageOwner);
	}

	public IMarker[] getValidationTasks(IResource resource, String[] messageOwners) {
		return TaskListUtility.getValidationTasks(resource, messageOwners);
	}

	/**
	 * Remove all validation messages from the resource and its children.
	 */
	public void removeAllTasks(IResource resource) {
		TaskListUtility.removeAllTasks(resource);
	}

	/**
	 * This method removes all tasks from the resource. If the resource is an IProject, all tasks
	 * are also removed from the project's children.
	 */
	public void removeAllTasks(IResource resource, String[] owners) throws CoreException {
		TaskListUtility.removeAllTasks(resource, owners);
	}

	/**
	 * This method removes all messages from a resource in the task list.
	 */
	public void removeAllTasks(IResource resource, String owner, String objectName) throws CoreException {
		TaskListUtility.removeAllTasks(resource, owner, objectName);
	}

	public void removeAllTasks(IResource resource, String[] owners, String objectName) throws CoreException {
		TaskListUtility.removeAllTasks(resource, owners, objectName);
	}

	/**
	 * This method removes a subset of tasks from the project, including child tasks. Every task
	 * which belongs to the group, identified by groupName, will be removed.
	 */
	public void removeTaskSubset(IResource resource, String[] owners, String objectName, String groupName) throws CoreException {
		TaskListUtility.removeTaskSubset(resource, owners, objectName, groupName);
	}

	/**
	 * This method changes all validator markers which are owned by "from" to make their owner "to".
	 */
	public void updateOwner(String from, String to) throws CoreException {
		TaskListUtility.updateOwner(from, to);
	}

	/**
	 * This method changes all validator markers on the IResource and its children. All markers
	 * owned by "from" have their owner reassigned to "to".
	 */
	public void updateOwner(String from, String to, IResource resource) throws CoreException {
		TaskListUtility.updateOwner(from, to, resource);
	}
}