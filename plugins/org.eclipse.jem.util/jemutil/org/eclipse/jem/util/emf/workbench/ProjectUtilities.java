/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 *  $$RCSfile: ProjectUtilities.java,v $$
 *  $$Revision: 1.1 $$  $$Date: 2005/01/07 20:19:23 $$ 
 */

package org.eclipse.jem.util.emf.workbench;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;

import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.internal.core.JavaModel;
import org.eclipse.jdt.internal.core.JavaModelManager;

import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.jem.util.plugin.JEMUtilPlugin;


/**
 * EMF Workbench Project Utilities.
 * 
 * @since 1.0.0
 */

public class ProjectUtilities {

	/**
	 * Project control file name in project.
	 * 
	 * @since 1.0.0
	 */
	public final static String DOT_PROJECT = ".project"; //$NON-NLS-1$

	/**
	 * Classpath control file name in project.
	 * 
	 * @since 1.0.0
	 */
	public final static String DOT_CLASSPATH = ".classpath"; //$NON-NLS-1$

	public ProjectUtilities() {
	}

	/**
	 * Add the nature id to the project ahead of all other nature ids.
	 * 
	 * @param proj
	 * @param natureId
	 * @throws CoreException
	 * 
	 * @since 1.0.0
	 */
	public static void addNatureToProject(IProject proj, String natureId) throws CoreException {
		IProjectDescription description = proj.getDescription();
		String[] prevNatures = description.getNatureIds();
		String[] newNatures = new String[prevNatures.length + 1];
		System.arraycopy(prevNatures, 0, newNatures, 1, prevNatures.length);
		newNatures[0] = natureId;
		description.setNatureIds(newNatures);
		proj.setDescription(description, null);
	}

	/**
	 * Add the nature id after all of the other nature ids for the project.
	 * 
	 * @param proj
	 * @param natureId
	 * @throws CoreException
	 * 
	 * @since 1.0.0
	 */
	public static void addNatureToProjectLast(IProject proj, String natureId) throws CoreException {
		IProjectDescription description = proj.getDescription();
		String[] prevNatures = description.getNatureIds();
		String[] newNatures = new String[prevNatures.length + 1];
		System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
		newNatures[prevNatures.length] = natureId;
		description.setNatureIds(newNatures);
		proj.setDescription(description, null);
	}

	/**
	 * Remove the nature id from the project.
	 * 
	 * @param project
	 * @param natureId
	 * @throws CoreException
	 * 
	 * @since 1.0.0
	 */
	public static void removeNatureFromProject(IProject project, String natureId) throws CoreException {
		IProjectDescription description = project.getDescription();
		String[] prevNatures = description.getNatureIds();
		int size = prevNatures.length;
		int newsize = 0;
		String[] newNatures = new String[size];
		boolean matchfound = false;
		for (int i = 0; i < size; i++) {
			if (prevNatures[i].equals(natureId)) {
				matchfound = true;
				continue;
			} else
				newNatures[newsize++] = prevNatures[i];
		}
		if (!matchfound)
			throw new CoreException(new Status(IStatus.ERROR, JEMUtilPlugin.ID, 0,
					"The nature id " + natureId + " does not exist on the project " + project.getName(), null)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		else {
			String[] temp = newNatures;
			newNatures = new String[newsize];
			System.arraycopy(temp, 0, newNatures, 0, newsize);
			description.setNatureIds(newNatures);
			project.setDescription(description, null);
		}
	}

	/**
	 * Add the list of projects to end of the "referenced projects" list from the project's description.
	 * 
	 * @param project
	 * @param toBeAddedProjectsList
	 * @throws CoreException
	 * 
	 * @since 1.0.0
	 */
	public static void addReferenceProjects(IProject project, List toBeAddedProjectsList) throws CoreException {

		IProjectDescription description = project.getDescription();
		IProject[] projects = description.getReferencedProjects();

		ArrayList projectsList = new ArrayList();

		for (int i = 0; i < projects.length; i++) {
			projectsList.add(projects[i]);
		}

		for (int i = 0; i < toBeAddedProjectsList.size(); i++) {
			projectsList.add(toBeAddedProjectsList.get(i));
		}

		IProject[] refProjects = new Project[projectsList.size()];

		for (int i = 0; i < refProjects.length; i++) {
			refProjects[i] = (Project) (projectsList.get(i));
		}

		description.setReferencedProjects(refProjects);
		project.setDescription(description, null);
	}

	/**
	 * Add the single project to the end of the "referenced projects" list from the project's description.
	 * 
	 * @param project
	 * @param projectToBeAdded
	 * @throws CoreException
	 * 
	 * @since 1.0.0
	 */
	public static void addReferenceProjects(IProject project, IProject projectToBeAdded) throws CoreException {
		IProjectDescription description = project.getDescription();
		IProject[] projects = description.getReferencedProjects();

		ArrayList projectsList = new ArrayList();

		for (int i = 0; i < projects.length; i++) {
			projectsList.add(projects[i]);
		}

		projectsList.add(projectToBeAdded);

		IProject[] refProjects = new Project[projectsList.size()];

		for (int i = 0; i < refProjects.length; i++) {
			refProjects[i] = (Project) (projectsList.get(i));
		}

		description.setReferencedProjects(refProjects);
		project.setDescription(description, null);
	}

	/**
	 * Force a an immediate build of the project.
	 * 
	 * @param project
	 * @param progressMonitor
	 * 
	 * @since 1.0.0
	 */
	public static void forceAutoBuild(IProject project, IProgressMonitor progressMonitor) {
		try {
			project.build(IncrementalProjectBuilder.FULL_BUILD, progressMonitor);
		} catch (CoreException ce) {
			//Revisit: Need to use a Logger
			//Logger.getLogger().logError(ce);
		}
	}

	/**
	 * Return if auto build is turned on.
	 * 
	 * @return <code>true</code> if auto build is turned on.
	 * 
	 * @since 1.0.0
	 */
	public static boolean getCurrentAutoBuildSetting() {

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceDescription wd = workspace.getDescription();
		return wd.isAutoBuilding();
	}

	/**
	 * Get the project associated with the given object.
	 * 
	 * @param object
	 *            may be an <code>IProject, IResource, IAdaptable (to an IProject), EObject (gets IProject if object is in a ProjectResourceSet</code>.
	 * @param natureId
	 *            if <code>null</code> then returns project. If not <code>null</code> then returns project only if project has this nature id.
	 * @return project associated with the object or <code>null</code> if not found.
	 * 
	 * @since 1.0.0
	 */
	public static IProject getProject(Object object, String natureId) {
		IProject result = getProject(object);
		if (natureId == null)
			return result;
		if (result != null && result.isAccessible() && natureId != null)
			try {
				if (result.hasNature(natureId))
					return result;
			} catch (CoreException e) {
				Logger.getLogger().logError(e);
			}
		return null;
	}

	/**
	 * Get the project associated with the given object.
	 * 
	 * @param object
	 *            may be an <code>IProject, IResource, IAdaptable (to an IProject), EObject (gets IProject if object is in a ProjectResourceSet</code>.
	 * @return project associated with the object or <code>null</code> if not found.
	 * 
	 * @since 1.0.0
	 */
	public static IProject getProject(Object object) {
		IProject result = null;

		if (object instanceof IProject)
			result = (IProject) object;
		else if (object instanceof IResource)
			result = ((IResource) object).getProject();
		else if (object instanceof IAdaptable)
			result = (IProject) ((IAdaptable) object).getAdapter(IProject.class);
		else if (object instanceof EObject)
			result = getProject((EObject) object);

		return result;
	}

	/**
	 * Get the project associated with the given EObject. (If in a ProjectResourceSet, then the project from that resource set).
	 * 
	 * @param aRefObject
	 * @return project if associated or <code>null</code> if not found.
	 * 
	 * @since 1.0.0
	 */
	public static IProject getProject(EObject aRefObject) {
		if (aRefObject != null) {
			Resource resource = aRefObject.eResource();
			return getProject(resource);
		}
		return null;
	}

	/**
	 * Get the project associated with the given Resource. (If in a ProjectResourceSet, then the project from that resource set).
	 * 
	 * @param resource
	 * @return project if associated or <code>null</code> if not found.
	 * 
	 * @since 1.0.0
	 */
	public static IProject getProject(Resource resource) {
		ResourceSet set = resource == null ? null : resource.getResourceSet();
		if (set instanceof ProjectResourceSet)
			return ((ProjectResourceSet) set).getProject();
		URIConverter converter = set == null ? null : set.getURIConverter();
		if (converter != null && converter instanceof WorkbenchURIConverter && ((WorkbenchURIConverter) converter).getOutputContainer() != null)
			return ((WorkbenchURIConverter) converter).getOutputContainer().getProject();
		else
			return null;
	}

	/**
	 * Remove the list of projects from the list of "referenced projects" in the project's description.
	 * 
	 * @param project
	 * @param toBeRemovedProjectList
	 * @throws org.eclipse.core.runtime.CoreException
	 * 
	 * @since 1.0.0
	 */
	public static void removeReferenceProjects(IProject project, List toBeRemovedProjectList) throws org.eclipse.core.runtime.CoreException {
		IProjectDescription description = project.getDescription();
		IProject[] projects = description.getReferencedProjects();

		ArrayList projectsList = new ArrayList();

		for (int i = 0; i < projects.length; i++) {
			projectsList.add(projects[i]);
		}

		for (int i = 0; i < toBeRemovedProjectList.size(); i++) {
			projectsList.remove(toBeRemovedProjectList.get(i));
		}

		IProject[] refProjects = new Project[projectsList.size()];

		for (int i = 0; i < refProjects.length; i++) {
			refProjects[i] = (Project) (projectsList.get(i));
		}

		description.setReferencedProjects(refProjects);
		project.setDescription(description, null);
	}

	/**
	 * Remove the project from the list of "referenced projects" in the description for the given project.
	 * 
	 * @param project
	 * @param toBeRemovedProject
	 * @throws org.eclipse.core.runtime.CoreException
	 * 
	 * @since 1.0.0
	 */
	public static void removeReferenceProjects(IProject project, IProject toBeRemovedProject) throws org.eclipse.core.runtime.CoreException {
		IProjectDescription description = project.getDescription();
		IProject[] projects = description.getReferencedProjects();

		ArrayList projectsList = new ArrayList();

		for (int i = 0; i < projects.length; i++) {
			projectsList.add((projects[i]));
		}

		projectsList.remove(toBeRemovedProject);

		IProject[] refProjects = new Project[projectsList.size()];

		for (int i = 0; i < refProjects.length; i++) {
			refProjects[i] = (Project) (projectsList.get(i));
		}

		description.setReferencedProjects(refProjects);
		project.setDescription(description, null);
	}

	/**
	 * Turn auto-building off.
	 * 
	 * 
	 * @since 1.0.0
	 */
	public static void turnAutoBuildOff() {
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceDescription wd = workspace.getDescription();
			wd.setAutoBuilding(false);
			workspace.setDescription(wd);
		} catch (CoreException ce) {
			//Logger.getLogger().logError(ce);
		}
	}

	/**
	 * Turn auto-building on.
	 * 
	 * 
	 * @since 1.0.0
	 */
	public static void turnAutoBuildOn() {
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceDescription wd = workspace.getDescription();
			wd.setAutoBuilding(true);
			workspace.setDescription(wd);
		} catch (CoreException ce) {
			//Logger.getLogger().logError(ce);
		}
	}

	/**
	 * Set the auto-building state.
	 * 
	 * @param aBoolean
	 *            <code>true</code> to turn auto-building on.
	 * 
	 * @since 1.0.0
	 */
	public static void turnAutoBuildOn(boolean aBoolean) {
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceDescription wd = workspace.getDescription();
			wd.setAutoBuilding(aBoolean);
			workspace.setDescription(wd);
		} catch (CoreException ce) {
			//Logger.getLogger().logError(ce);

		}
	}

	/**
	 * Adds a builder to the build spec for the given project.
	 * 
	 * @param builderID
	 *            The id of the builder.
	 * @param project
	 *            Project to add to.
	 * @return whether the builder id was actually added (it may have already existed)
	 * @throws CoreException
	 * @since 1.0.0
	 */
	public static boolean addToBuildSpec(String builderID, IProject project) throws CoreException {
		return addToBuildSpecBefore(builderID, null, project);
	}

	/**
	 * Adds a builder to the build spec for the given project, immediately before the specified successor builder.
	 * 
	 * @param builderID
	 *            The id of the builder.
	 * @param successorID
	 *            The id to put the builder before.
	 * @return whether the builder id was actually added (it may have already existed)
	 * @throws CoreException
	 * @since 1.0.0
	 */
	public static boolean addToBuildSpecBefore(String builderID, String successorID, IProject project) throws CoreException {
		IProjectDescription description = project.getDescription();
		ICommand[] commands = description.getBuildSpec();
		boolean found = false;
		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(builderID)) {
				found = true;
				break;
			}
		}
		if (!found) {
			boolean successorFound = false;
			ICommand command = description.newCommand();
			command.setBuilderName(builderID);
			ICommand[] newCommands = new ICommand[commands.length + 1];
			for (int j = 0, index = 0; j < commands.length; j++, index++) {
				if (successorID != null && commands[j].getBuilderName().equals(successorID)) {
					successorFound = true;
					newCommands[index++] = command;
				}
				newCommands[index] = commands[j];
			}
			if (!successorFound)
				newCommands[newCommands.length - 1] = command;
			description.setBuildSpec(newCommands);
			project.setDescription(description, null);
		}
		return !found;
	}

	/**
	 * Remove the builder from the build spec.
	 * 
	 * @param builderID
	 *            The id of the builder.
	 * @param project
	 *            Project to remove from.
	 * @return boolean if the builder id was found and removed
	 * @throws CoreException
	 * @since 1.0.0
	 */
	public static boolean removeFromBuildSpec(String builderID, IProject project) throws CoreException {
		IProjectDescription description = project.getDescription();
		ICommand[] commands = description.getBuildSpec();
		boolean found = false;
		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(builderID)) {
				found = true;
				break;
			}
		}
		if (found) {
			ICommand[] newCommands = new ICommand[commands.length - 1];
			int newCount = 0;
			for (int i = 0; i < commands.length; ++i) {
				if (!(commands[i].getBuilderName().equals(builderID))) {
					//Add the existng to the new array
					newCommands[newCount] = commands[i];
					newCount++;
				}
			}

			description.setBuildSpec(newCommands);
			project.setDescription(description, null);

		}
		return found;

	}

	/**
	 * Ensure the container is not read-only.
	 * <p>
	 * For Linux, a Resource cannot be created in a ReadOnly folder. This is only necessary for new files.
	 * 
	 * @param resource
	 *            workspace resource to make read/write
	 * @since 1.0.0
	 */
	public static void ensureContainerNotReadOnly(IResource resource) {
		if (resource != null && !resource.exists()) { //it must be new
			IContainer container = resource.getParent();
			while (container != null && !container.isReadOnly())
				container = container.getParent();
			if (container != null)
				container.setReadOnly(false);
		}
	}

	/**
	 * Is this project a binary project.
	 * <p>
	 * Typically a Java project is considered binary if it does not have a source entry in the classpath.
	 * 
	 * @param project
	 *            Project to test
	 * @return <code>true</code> if project is a binary project.
	 */
	public static boolean isBinaryProject(IProject aProject) {

		IJavaProject javaProj = getJavaProject(aProject);
		if (javaProj == null)
			return false;
		IClasspathEntry[] entries = null;
		try {
			entries = javaProj.getRawClasspath();
		} catch (JavaModelException jme) {
			return false;
		}
		for (int i = 0; i < entries.length; i++) {
			IClasspathEntry entry = entries[i];
			if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE)
				return false;
		}
		return true;
	}

	/**
	 * Get the java project nature for the given project.
	 * 
	 * @param p
	 *            project
	 * @return the java project nature for the project or <code>null</code> if not a java project.
	 * 
	 * @since 1.0.0
	 */
	public static IJavaProject getJavaProject(IProject p) {
		try {
			return (IJavaProject) p.getNature(JavaCore.NATURE_ID);
		} catch (CoreException ignore) {
			return null;
		}
	}

	/**
	 * Get projects from primary nature.
	 * 
	 * @param natureID
	 * @return All projects that have the given nature id as the first nature id.
	 * 
	 * @since 1.0.0
	 */
	public static IProject[] getProjectsForPrimaryNature(String natureID) {
		IProject[] projectsWithNature = new IProject[] {};
		List result = new ArrayList();
		IProject[] projects = getAllProjects();
		for (int i = 0; i < projects.length; i++) {
			if (isProjectPrimaryNature(projects[i], natureID))
				result.add(projects[i]);
		}
		return (IProject[]) result.toArray(projectsWithNature);
	}

	/**
	 * Get all projects in the workspace
	 * 
	 * @return all workspace projects
	 * 
	 * @since 1.0.0
	 */
	public static IProject[] getAllProjects() {
		return ResourcesPlugin.getWorkspace().getRoot().getProjects();
	}

	/**
	 * Is this nature id the primary nature id for the project
	 * 
	 * @param project
	 * @param natureID
	 * @return <code>true</code> if first nature id for the project.
	 * 
	 * @since 1.0.0
	 */
	public static boolean isProjectPrimaryNature(IProject project, String natureID) {
		String[] natures = null;
		try {
			natures = project.getDescription().getNatureIds();
		} catch (Exception e1) {
		}
		return (natures != null && natures.length > 0 && natures[0].equals(natureID));
	}

	/**
	 * Append to java class path.
	 * <p>
	 * Append a list of IClasspathEntry's to the build path of the passed project. Updated to remove existing occurrences of the passed entries before
	 * appending.
	 * </p>
	 * 
	 * @param p
	 *            project
	 * @param appendClasspathEntries
	 *            list of entries
	 * @throws JavaModelException
	 * 
	 * @since 1.0.0
	 */
	public static void appendJavaClassPath(IProject p, List appendClasspathEntries) throws JavaModelException {
		IJavaProject javaProject = null;
		try {
			javaProject = (IJavaProject) p.getNature(JavaCore.NATURE_ID);
		} catch (CoreException ignore) {
		}
		if (javaProject != null) {
			IClasspathEntry[] classpath = javaProject.getRawClasspath();
			List newPathList = new ArrayList(classpath.length);
			for (int i = 0; i < classpath.length; i++) {
				IClasspathEntry entry = classpath[i];
				// Skip entries which are in the append list
				if (appendClasspathEntries.indexOf(entry) < 0)
					newPathList.add(entry);
			}
			newPathList.addAll(appendClasspathEntries);
			IClasspathEntry[] newClasspath = (IClasspathEntry[]) newPathList.toArray(new IClasspathEntry[newPathList.size()]);
			javaProject.setRawClasspath(newClasspath, new NullProgressMonitor());
		}
	}

	/**
	 * Append classpath entry.
	 * <p>
	 * Append one IClasspathEntry to the build path of the passed project. If a classpath entry having the same path as the parameter already exists,
	 * then does nothing.
	 * </p>
	 * 
	 * @param p
	 *            Project
	 * @param newEntry
	 *            Entry
	 * @throws JavaModelException
	 * 
	 * @since 1.0.0
	 */
	public static void appendJavaClassPath(IProject p, IClasspathEntry newEntry) throws JavaModelException {
		IJavaProject javaProject = getJavaProject(p);
		if (javaProject == null)
			return;
		IClasspathEntry[] classpath = javaProject.getRawClasspath();
		List newPathList = new ArrayList(classpath.length);
		for (int i = 0; i < classpath.length; i++) {
			IClasspathEntry entry = classpath[i];
			// fix dup class path entry for .JETEmitter project
			// Skip the entry to be added if it already exists
			if (Platform.getOS().equals(Platform.OS_WIN32)) {
				if (!entry.getPath().toString().equalsIgnoreCase(newEntry.getPath().toString()))
					newPathList.add(entry);
				else
					return;
			} else {
				if (!entry.getPath().equals(newEntry.getPath()))
					newPathList.add(entry);
				else
					return;
			}
		}
		newPathList.add(newEntry);
		IClasspathEntry[] newClasspath = (IClasspathEntry[]) newPathList.toArray(new IClasspathEntry[newPathList.size()]);
		javaProject.setRawClasspath(newClasspath, new NullProgressMonitor());
	}

	/**
	 * Touch classpath. It simply takes the classpath and sets it back in.
	 * 
	 * @param javaProject
	 * @throws JavaModelException
	 * 
	 * @since 1.0.0
	 */
	public static void updateClasspath(IJavaProject javaProject) throws JavaModelException {
		if (javaProject != null)
			javaProject.setRawClasspath(javaProject.getRawClasspath(), new NullProgressMonitor());
	}

	protected static IPath createPath(IProject p, String defaultSourceName) {
		IPath path = new Path(p.getName());
		path = path.append(defaultSourceName);
		path = path.makeAbsolute();
		return path;
	}

	/**
	 * Return the source folder matching the parameter; if the parameter is null, or if the source folder is not on the classpath, return the first
	 * source folder on the classpath
	 * 
	 * @param p
	 *            project
	 * @param defaultSourceName
	 *            source folder to find if on classpath, or if <code>null</code> the first folder
	 * @return container searched for or <code>null</code> if not java project or some other problem.
	 * 
	 * @since 1.0.0
	 */
	public static IContainer getSourceFolderOrFirst(IProject p, String defaultSourceName) {
		try {
			IPath sourcePath = getSourcePathOrFirst(p, defaultSourceName);
			if (sourcePath == null)
				return null;
			else if (sourcePath.isEmpty())
				return p;
			else
				return p.getFolder(sourcePath);
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	/**
	 * Return the source path matching the parameter; if the parameter is null, or if the source folder is not on the classpath, return the first
	 * source path on the classpath
	 * 
	 * @param p
	 *            project
	 * @param defaultSourceName
	 *            source folder to find if on classpath, or if <code>null</code> the first folder
	 * @return path searched for or <code>null</code> if not java project or some other problem.
	 * 
	 * @since 1.0.0
	 */
	public static IPath getSourcePathOrFirst(IProject p, String defaultSourceName) {
		IJavaProject javaProj = getJavaProject(p);
		if (javaProj == null)
			return null;
		IClasspathEntry[] cp = null;
		try {
			cp = javaProj.getRawClasspath();
		} catch (JavaModelException ex) {
			JEMUtilPlugin.getLogger().logError(ex);
			return null;
		}
		IClasspathEntry firstSource = null;
		IPath defaultSourcePath = null;
		if (defaultSourceName != null)
			defaultSourcePath = createPath(p, defaultSourceName);
		for (int i = 0; i < cp.length; i++) {
			if (cp[i].getEntryKind() == IClasspathEntry.CPE_SOURCE) {
				if (firstSource == null) {
					firstSource = cp[i];
					if (defaultSourcePath == null)
						break;
				}
				if (cp[i].getPath().equals(defaultSourcePath))
					return defaultSourcePath.removeFirstSegments(1);
			}
		}
		if (firstSource == null)
			return null;
		if (firstSource.getPath().segment(0).equals(p.getName()))
			return firstSource.getPath().removeFirstSegments(1);
		return null;
	}

	/**
	 * Returns a list of IFolder that represents each source folder in a java project
	 * 
	 * @deprecated Use {@link #getSourceContainers(IProject)}because the project itself might be a source container
	 * 
	 * @param p
	 *            project
	 * @return
	 * 
	 * @since 1.0.0
	 */
	public static List getSourceFolders(IProject p) {
		try {
			List sourceFolders = new ArrayList();
			List sourcePaths = getSourcePaths(p);
			if (sourcePaths != null && !sourcePaths.isEmpty()) {
				for (int i = 0; i < sourcePaths.size(); i++) {
					IPath path = (IPath) sourcePaths.get(i);
					if (!path.isEmpty())
						sourceFolders.add(p.getFolder(path));
				}
			}
			return sourceFolders;
		} catch (IllegalArgumentException ex) {
			return Collections.EMPTY_LIST;
		}
	}

	/**
	 * Get source containers for the project.
	 * 
	 * @param p
	 *            project
	 * @return list of source containers.
	 * 
	 * @since 1.0.0
	 */
	public static List getSourceContainers(IProject p) {
		try {
			List sourceContainers = new ArrayList();
			List sourcePaths = getSourcePaths(p);
			if (sourcePaths != null && !sourcePaths.isEmpty()) {
				for (int i = 0; i < sourcePaths.size(); i++) {
					IPath path = (IPath) sourcePaths.get(i);
					if (path.isEmpty())
						sourceContainers.add(p);
					else
						sourceContainers.add(p.getFolder(path));
				}
			}
			return sourceContainers;
		} catch (IllegalArgumentException ex) {
			return Collections.EMPTY_LIST;
		}
	}

	protected static List getSourcePaths(IProject p) {
		IJavaProject javaProj = getJavaProject(p);
		if (javaProj == null)
			return null;
		IClasspathEntry[] cp = null;
		try {
			cp = javaProj.getRawClasspath();
		} catch (JavaModelException ex) {
			JEMUtilPlugin.getLogger().logError(ex);
			return null;
		}
		List sourcePaths = new ArrayList();
		for (int i = 0; i < cp.length; i++) {
			if (cp[i].getEntryKind() == IClasspathEntry.CPE_SOURCE) {
				sourcePaths.add(cp[i].getPath().removeFirstSegments(1));
			}
		}
		return sourcePaths;
	}

	/**
	 * Return the location of the binary output files for the JavaProject.
	 * 
	 * @param p
	 *            project
	 * @return path to binary output folder or <code>null</code> if not java project or other problem.
	 * 
	 * @since 1.0.0
	 */
	public static IPath getJavaProjectOutputLocation(IProject p) {
		try {
			IJavaProject javaProj = getJavaProject(p);
			if (javaProj == null)
				return null;
			if (!javaProj.isOpen())
				javaProj.open(null);
			return javaProj.getOutputLocation();
		} catch (JavaModelException e) {
			return null;
		}
	}

	/**
	 * Get the project's binary output container.
	 * 
	 * @param p
	 *            project
	 * @return project's output container or <code>null</code> if not java project or some other error.
	 * 
	 * @since 1.0.0
	 */
	public static IContainer getJavaProjectOutputContainer(IProject p) {
		IPath path = getJavaProjectOutputLocation(p);
		if (path == null)
			return null;
		if (path.segmentCount() == 1)
			return p;
		return p.getFolder(path.removeFirstSegments(1));
	}

	/**
	 * Get the binary output absolute (local file system) path.
	 * 
	 * @param p
	 *            project
	 * @return project's output path or <code>null</code> if not java project or some other error.
	 * 
	 * @since 1.0.0
	 */
	public static IPath getJavaProjectOutputAbsoluteLocation(IProject p) {
		IContainer container = getJavaProjectOutputContainer(p);
		if (container != null)
			return container.getLocation();
		return null;
	}

	/**
	 * Hack to force a reload of the .classpath file
	 * 
	 * @param project
	 *            project to reload
	 * @since 1.0.0
	 */
	public static void forceClasspathReload(IProject project) throws JavaModelException {
		IJavaProject javaProj = getJavaProject(project);
		if (javaProj != null) {
			IClasspathEntry[] entries = javaProj.readRawClasspath();
			if (entries != null) {
				IPath output = javaProj.readOutputLocation();
				if (output != null)
					javaProj.setRawClasspath(entries, output, null);
			}
		}
	}

	/**
	 * Get the JDT JavaModel.
	 * 
	 * @return JDT's JavaModel
	 * 
	 * @since 1.0.0
	 */
	public static JavaModel getJavaModel() {
		return JavaModelManager.getJavaModelManager().getJavaModel();
	}

	/**
	 * Get all source package fragment roots.
	 * 
	 * @param javaProj
	 * @return source package fragment roots
	 * @throws JavaModelException
	 * 
	 * @since 1.0.0
	 */
	public static List getSourcePackageFragmentRoots(IJavaProject javaProj) throws JavaModelException {
		List result = new ArrayList();
		IPackageFragmentRoot[] roots = javaProj.getPackageFragmentRoots();
		for (int i = 0; i < roots.length; i++) {
			IPackageFragmentRoot root = roots[i];
			if (root.getKind() == IPackageFragmentRoot.K_SOURCE)
				result.add(result);
		}
		return result;
	}

	/**
	 * Returns a list of existing files which will be modified if the classpath changes for the given proeject.
	 * 
	 * @param p
	 *            project
	 * @return list of affected files.
	 * 
	 * @since 1.0.0
	 */
	public static List getFilesAffectedByClasspathChange(IProject p) {
		List result = new ArrayList(2);
		addFileIfExists(p, result, DOT_CLASSPATH);
		addFileIfExists(p, result, DOT_PROJECT);
		return result;
	}

	protected static void addFileIfExists(IProject p, List aList, String filename) {
		IFile aFile = p.getFile(filename);
		if (aFile != null && aFile.exists())
			aList.add(aFile);
	}

	/**
	 * Strip off a leading "/" from each project name in the array, if it has one.
	 * 
	 * @param projecNames
	 * @return array of project names with all leading '/' removed.
	 * 
	 * @since 1.0.0
	 */
	public static String[] getProjectNamesWithoutForwardSlash(String[] projecNames) {
		String[] projNames = new String[projecNames.length];
		List temp = java.util.Arrays.asList(projecNames);
		for (int i = 0; i < temp.size(); i++) {
			String name = (String) (temp.get(i));
			if (name.startsWith("/")) { //$NON-NLS-1$
				projNames[i] = name.substring(1, name.length());
			} else {
				projNames[i] = name;
			}
		}
		return projNames;
	}

	/**
	 * Get the paths of all of the local jars in the classpath for the project. It does not recurse into referenced projects.
	 * 
	 * @param proj
	 *            project to search (should be a java project).
	 * @return A list of IPath, where each entry is a project relative path to a JAR contained in the project.
	 */
	public static List getLocalJARPathsFromClasspath(IProject proj) {
		IJavaProject javaProj = getJavaProject(proj);
		if (javaProj == null)
			return null;
		IPath projectPath = proj.getFullPath();
		List result = new ArrayList();
		try {
			IClasspathEntry[] entries = javaProj.getRawClasspath();
			for (int i = 0; i < entries.length; i++) {
				IClasspathEntry entry = entries[i];
				if (entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
					IPath path = entry.getPath();
					int segments = path.matchingFirstSegments(projectPath);
					if (segments > 0)
						result.add(path.removeFirstSegments(segments));
				}
			}
		} catch (JavaModelException e) {
			JEMUtilPlugin.getLogger().logError(e);
		}
		return result;
	}

	/**
	 * Remove the resource from the classpath
	 * 
	 * @param p
	 *            project
	 * @param res
	 *            resource
	 * @throws JavaModelException
	 * 
	 * @since 1.0.0
	 */
	public static void removeFromJavaClassPath(IProject p, IResource res) throws JavaModelException {
		IClasspathEntry entry = JavaCore.newLibraryEntry(res.getFullPath(), null, null);
		removeFromJavaClassPath(p, entry);
	}

	/**
	 * Remove the path from the classpath
	 * 
	 * @param p
	 *            project
	 * @param path
	 *            path to remove
	 * @throws JavaModelException
	 * 
	 * @since 1.0.0
	 */
	public static void removeFromJavaClassPath(IProject p, IPath path) throws JavaModelException {
		org.eclipse.core.resources.IFile f = p.getFile(path);
		removeFromJavaClassPath(p, f);
	}

	/**
	 * Remove the classpath entry from the project's classpath.
	 * 
	 * @param p
	 * @param entry
	 * @throws JavaModelException
	 * 
	 * @since 1.0.0
	 */
	public static void removeFromJavaClassPath(IProject p, IClasspathEntry entry) throws JavaModelException {
		IJavaProject javaProject = null;
		try {
			javaProject = (IJavaProject) p.getNature(JavaCore.NATURE_ID);
		} catch (CoreException ignore) {
		}
		if (javaProject != null) {
			IClasspathEntry[] classpath = javaProject.getRawClasspath();
			javaProject.setRawClasspath(primRemoveFromJavaClassPath(classpath, entry), new NullProgressMonitor());
		}
	}

	/**
	 * Remove the list of entries from the classpath of the project.
	 * 
	 * @param p
	 *            project
	 * @param entries
	 *            list of IClassPathEntry's
	 * @throws JavaModelException
	 * 
	 * @since 1.0.0
	 */
	public static void removeFromJavaClassPath(IProject p, List entries) throws JavaModelException {
		IJavaProject javaProject = null;
		try {
			javaProject = (IJavaProject) p.getNature(JavaCore.NATURE_ID);
		} catch (CoreException ignore) {
		}
		if (javaProject != null) {
			IClasspathEntry[] classpath = javaProject.getRawClasspath();
			javaProject.setRawClasspath(primRemoveFromJavaClassPath(classpath, entries), new NullProgressMonitor());
		}
	}

	protected static IClasspathEntry[] primRemoveFromJavaClassPath(IClasspathEntry[] classpath, IClasspathEntry entry) throws JavaModelException {
		List result = new ArrayList();
		boolean didRemove = false;
		for (int i = 0; i < classpath.length; i++) {
			IClasspathEntry cpEntry = classpath[i];
			if (!entry.getPath().equals(classpath[i].getPath()))
				result.add(cpEntry);
			else
				didRemove = true;
		}
		if (!didRemove)
			return classpath;
		return (IClasspathEntry[]) result.toArray(new IClasspathEntry[result.size()]);
	}

	protected static IClasspathEntry[] primRemoveFromJavaClassPath(IClasspathEntry[] classpath, List entries) throws JavaModelException {
		List arrayList = Arrays.asList(classpath);
		List removeable = new ArrayList(arrayList);
		IClasspathEntry entry;
		boolean didRemove = false;
		int size = entries.size();
		for (int i = 0; i < size; i++) {
			entry = (IClasspathEntry) entries.get(i);
			for (int j = 0; j < classpath.length; j++) {
				IClasspathEntry cpEntry = classpath[j];
				if (entry.getPath().equals(classpath[j].getPath())) {
					if (removeable.remove(cpEntry))
						didRemove = true;
				}
			}
		}
		if (!didRemove)
			return classpath;
		return (IClasspathEntry[]) removeable.toArray(new IClasspathEntry[removeable.size()]);
	}

	/**
	 * Get the classpath as an array or URL's.
	 * 
	 * @param javaProject
	 * @return array of URL's or <code>null</code> if javaProject was <code>null</code>.
	 * 
	 * @since 1.0.0
	 */
	public static URL[] getClasspathAsURLArray(IJavaProject javaProject) {
		if (javaProject == null)
			return null;
		Set visited = new HashSet();
		List urls = new ArrayList(20);
		collectClasspathURLs(javaProject, urls, visited, true);
		URL[] result = new URL[urls.size()];
		urls.toArray(result);
		return result;
	}

	private static void collectClasspathURLs(IJavaProject javaProject, List urls, Set visited, boolean isFirstProject) {
		if (visited.contains(javaProject))
			return;
		visited.add(javaProject);
		IPath outPath = getJavaProjectOutputAbsoluteLocation(javaProject.getProject());
		outPath = outPath.addTrailingSeparator();
		URL out = createFileURL(outPath);
		urls.add(out);
		IClasspathEntry[] entries = null;
		try {
			entries = javaProject.getResolvedClasspath(true);
		} catch (JavaModelException e) {
			return;
		}
		IClasspathEntry entry;
		for (int i = 0; i < entries.length; i++) {
			entry = entries[i];
			switch (entry.getEntryKind()) {
				case IClasspathEntry.CPE_LIBRARY:
				case IClasspathEntry.CPE_CONTAINER:
				case IClasspathEntry.CPE_VARIABLE:
					collectClasspathEntryURL(entry, urls);
					break;
				case IClasspathEntry.CPE_PROJECT: {
					if (isFirstProject || entry.isExported())
						collectClasspathURLs(getJavaProject(entry), urls, visited, false);
					break;
				}
			}
		}
	}

	private static URL createFileURL(IPath path) {
		try {
			return path.toFile().toURL();
		} catch (MalformedURLException e) {
			Logger.getLogger().log(e, Level.WARNING);
			return null;
		}
	}

	private static void collectClasspathEntryURL(IClasspathEntry entry, List urls) {
		URL url = createFileURL(entry.getPath());
		if (url != null)
			urls.add(url);
	}

	private static IJavaProject getJavaProject(IClasspathEntry entry) {
		IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(entry.getPath().segment(0));
		if (proj != null)
			return getJavaProject(proj);
		return null;
	}

	/**
	 * Return list of IContainers that are Libraries in the classpath.
	 * 
	 * @param p
	 *            project
	 * @return list of library IContainers.
	 * 
	 * @since 1.0.0
	 */
	public static List getLibraryContainers(IProject p) {
		try {
			List libraryContainers = new ArrayList();
			List libraryPaths = getlibraryPaths(p);
			if (libraryPaths != null && !libraryPaths.isEmpty()) {
				for (int i = 0; i < libraryPaths.size(); i++) {
					IPath path = (IPath) libraryPaths.get(i);
					if (path.isEmpty())
						libraryContainers.add(p);
					else
						libraryContainers.add(p.getFolder(path));
				}
			}
			return libraryContainers;
		} catch (IllegalArgumentException ex) {
			return Collections.EMPTY_LIST;
		}
	}

	/**
	 * Find first newObject that is not in the oldObjects array (using "==").
	 * 
	 * @param oldObjects
	 * @param newObjects
	 * @return first newObject not found in oldObjects, or <code>null</code> if all found.
	 * 
	 * @since 1.0.0
	 */
	public static Object getNewObject(Object[] oldObjects, Object[] newObjects) {
		if (oldObjects != null && newObjects != null && oldObjects.length < newObjects.length) {
			for (int i = 0; i < newObjects.length; i++) {
				boolean found = false;
				Object object = newObjects[i];
				for (int j = 0; j < oldObjects.length; j++) {
					if (oldObjects[j] == object) {
						found = true;
						break;
					}
				}
				if (!found)
					return object;
			}
		}
		if (oldObjects == null && newObjects != null && newObjects.length == 1)
			return newObjects[0];
		return null;
	}

	/*
	 * return list of path that may contain classes
	 */
	protected static List getlibraryPaths(IProject p) {
		IJavaProject javaProj = getJavaProject(p);
		if (javaProj == null)
			return null;
		IClasspathEntry[] cp = null;
		try {
			cp = javaProj.getRawClasspath();
		} catch (JavaModelException ex) {
			JEMUtilPlugin.getLogger().logError(ex);
			return null;
		}
		List libraryPaths = new ArrayList();
		for (int i = 0; i < cp.length; i++) {
			if (cp[i].getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
				libraryPaths.add(cp[i].getPath().removeFirstSegments(1));
			}
		}
		return libraryPaths;
	}

	/**
	 * List of all files in the project.
	 * <p>
	 * Note: A more efficient way to do this is to use {@link IResource#accept(org.eclipse.core.resources.IResourceProxyVisitor, int)}
	 * 
	 * @param 1.0.0
	 * @return list of files in the project
	 * 
	 * @see IResource#accept(org.eclipse.core.resources.IResourceProxyVisitor, int)
	 * @since 1.0.0
	 */
	public static List getAllProjectFiles(IProject project) {
		List result = new ArrayList();
		if (project == null)
			return result;
		try {
			result = collectFiles(project.members(), result);
		} catch (CoreException e) {
		}
		return result;
	}

	private static List collectFiles(IResource[] members, List result) throws CoreException {
		// recursively collect files for the given members
		for (int i = 0; i < members.length; i++) {
			IResource res = members[i];
			if (res instanceof IFolder) {
				collectFiles(((IFolder) res).members(), result);
			} else if (res instanceof IFile) {
				result.add(res);
			}
		}
		return result;
	}

	/**
	 * Get the project.
	 * 
	 * @param projectName
	 * @return a IProject given the projectName
	 * @since 1.0.0
	 */
	public static IProject getProject(String projectName) {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
	}

	/**
	 * Return whether the given builder name is attached to the project.
	 * 
	 * @param project
	 * @param builderName
	 * @return <code>true</code> if builder name is attached to the project.
	 * 
	 * @since 1.0.0
	 */
	public static boolean hasBuilder(IProject project, String builderName) {
		try {
			ICommand[] builders = project.getDescription().getBuildSpec();
			for (int i = 0; i < builders.length; i++) {
				ICommand builder = builders[i];
				if (builder != null) {
					if (builder.getBuilderName().equals(builderName))
						return true;
				}
			}
		} catch (Exception e) {
		}
		return false;
	}
}