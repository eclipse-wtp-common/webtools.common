/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 *  $RCSfile: JavaProjectUtilities.java,v $
 *  $Revision: 1.2 $  $Date: 2005/02/15 23:04:14 $ 
 */
package org.eclipse.jem.util.emf.workbench;

import java.net.URL;
import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.internal.core.JavaModel;
import org.eclipse.jdt.internal.core.JavaModelManager;

import org.eclipse.jem.util.plugin.JEMUtilPlugin;
 

/**
 * Project utilities that are Java (JDT) specific. This class should be
 * referenced only if the JDT is installed, otherwise you will get
 * class definition not found errors. This is because JDT is optional
 * for thie org.eclipse.jem.util bundle.
 * @since 1.1.0
 */
public class JavaProjectUtilities extends ProjectUtilities {

	public JavaProjectUtilities() {
		super();
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
		URL out = ProjectUtilities.createFileURL(outPath);
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

	private static void collectClasspathEntryURL(IClasspathEntry entry, List urls) {
		URL url = ProjectUtilities.createFileURL(entry.getPath());
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
			defaultSourcePath = ProjectUtilities.createPath(p, defaultSourceName);
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

}
