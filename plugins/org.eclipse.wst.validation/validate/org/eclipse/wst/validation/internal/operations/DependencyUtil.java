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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

import com.ibm.wtp.common.logger.LogEntry;
import com.ibm.wtp.common.logger.proxy.Logger;

/**
 * This singleton keeps an internal record of project dependencies. That is, every IProject can
 * depend on, and be depended on by, other IProjects. Ditto IJavaProject. This class is updated when
 * a project(s) classpath changes.
 */
public final class DependencyUtil {
	private static final IProject[] EMPTY_PROJECTS = new IProject[0];
	private static final IJavaProject[] EMPTY_JAVAPROJECTS = new IJavaProject[0];

	private static Set _tempSet = null;

	/**
	 * This is a set for temporary calculations.
	 */
	private static Set getTempSet() {
		if (_tempSet == null) {
			_tempSet = new HashSet();
		} else {
			_tempSet.clear();
		}
		return _tempSet;
	}

	/**
	 * Return an array of open IProjects which depend on the given IProject parameter.
	 */
	public static IProject[] getDependentProjects(IProject project) {
		if (project == null) {
			return EMPTY_PROJECTS;
		}

		IProject[] allProjects = project.getWorkspace().getRoot().getProjects();
		Set tempSet = getTempSet();
		for (int i = 0; i < allProjects.length; i++) {
			IProject p = allProjects[i];
			IProject[] requires = getRequiredProjects(p);
			for (int j = 0; j < requires.length; j++) {
				IProject r = requires[j];
				if (project.equals(r)) {
					tempSet.add(p);
					break;
				}
			}
		}
		IProject[] dependency = new IProject[tempSet.size()];
		tempSet.toArray(dependency);
		return dependency;
	}

	/**
	 * Return an array of open IProjects which the given IProject parameter depends on.
	 */
	public static IProject[] getRequiredProjects(IProject project) {
		if (project == null) {
			return EMPTY_PROJECTS;
		}

		// Check that each project in this list exists and is open
		try {
			IProject[] refProjects = project.getReferencedProjects();
			if ((refProjects == null) || (refProjects.length == 0)) {
				return EMPTY_PROJECTS;
			}

			IProject[] temp = new IProject[refProjects.length];
			int count = 0;
			for (int i = 0; i < refProjects.length; i++) {
				IProject rProject = refProjects[i];
				if (rProject.exists() && rProject.isOpen()) {
					temp[count++] = rProject;
				}
			}

			if (count == 0) {
				return EMPTY_PROJECTS;
			}

			if (count == temp.length) {
				return temp;
			}

			IProject[] result = new IProject[count];
			System.arraycopy(temp, 0, result, 0, count);
			return result;
		} catch (CoreException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceID("DependencyCache::getRequiredProjects(" + project.getName() + ")"); //$NON-NLS-1$  //$NON-NLS-2$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, exc);
			}

			return EMPTY_PROJECTS;
		}
	}

	/**
	 * Return an array of open IJavaProjects which depend on the given IJavaProject parameter.
	 */
	public static IJavaProject[] getDependentJavaProjects(IJavaProject javaproject) {
		if (javaproject == null) {
			return EMPTY_JAVAPROJECTS;
		}

		// calculate the dependencies now.
		try {
			IJavaProject[] allProjects = javaproject.getJavaModel().getJavaProjects();
			Set tempSet = getTempSet();
			for (int i = 0; i < allProjects.length; i++) {
				IJavaProject p = allProjects[i];
				IJavaProject[] requires = getRequiredJavaProjects(p);
				for (int j = 0; j < requires.length; j++) {
					IJavaProject r = requires[j];
					if (javaproject.equals(r)) {
						tempSet.add(p);
						break;
					}
				}
			}
			IJavaProject[] dependency = new IJavaProject[tempSet.size()];
			tempSet.toArray(dependency);
			return dependency;
		} catch (JavaModelException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceID("DependencyCache::getDependentJavaProjects(" + javaproject.getProject().getName() + ")"); //$NON-NLS-1$  //$NON-NLS-2$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, exc);
			}

			return EMPTY_JAVAPROJECTS;
		}
	}

	/**
	 * Return an array of open IJavaProjects which the given IJavaProject parameter depends on.
	 */
	public static IJavaProject[] getRequiredJavaProjects(IJavaProject javaproject) {
		if (javaproject == null) {
			return EMPTY_JAVAPROJECTS;
		}

		try {
			IJavaModel jm = javaproject.getJavaModel();
			if (jm == null) {
				Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
				if (logger.isLoggingLevel(Level.SEVERE)) {
					LogEntry entry = ValidationPlugin.getLogEntry();
					entry.setSourceID("DependencyCache::getRequiredJavaProjects(" + javaproject.getProject().getName() + ")"); //$NON-NLS-1$  //$NON-NLS-2$
					//entry.setText("IJavaModel == null"); //$NON-NLS-1$
					logger.write(Level.SEVERE, "IJavaModel == null"); //$NON-NLS-1$
				}
				return EMPTY_JAVAPROJECTS;
			}

			String[] requiredProjects = javaproject.getRequiredProjectNames();
			if ((requiredProjects == null) || (requiredProjects.length == 0)) {
				return EMPTY_JAVAPROJECTS;
			}

			IJavaProject[] temp = new IJavaProject[requiredProjects.length];
			int count = 0;
			for (int i = 0; i < requiredProjects.length; i++) {
				String projectName = requiredProjects[i];
				IJavaProject jp = jm.getJavaProject(projectName);
				try {
					if ((jp == null) || (!jp.getProject().exists())) {
						continue;
					}
					if (!jp.getProject().isAccessible()) {
						continue;
					}

					if (!jp.getProject().hasNature(JavaCore.NATURE_ID)) {
						continue;
					}

					temp[count++] = jp;
				} catch (CoreException exc) {
					Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
					if (logger.isLoggingLevel(Level.SEVERE)) {
						LogEntry entry = ValidationPlugin.getLogEntry();
						entry.setSourceID("DependencyCache::getRequiredJavaProjects(" + javaproject.getProject().getName() + ")"); //$NON-NLS-1$  //$NON-NLS-2$
						entry.setTargetException(exc);
						logger.write(Level.SEVERE, exc);
					}
					continue;
				}
			}

			if (count == 0) {
				return EMPTY_JAVAPROJECTS;
			}

			if (count == temp.length) {
				return temp;
			}

			IJavaProject[] result = new IJavaProject[count];
			System.arraycopy(temp, 0, result, 0, count);
			return result;
		} catch (JavaModelException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceID("DependencyCache::getRequiredJavaProjects(" + javaproject.getProject().getName() + ")"); //$NON-NLS-1$  //$NON-NLS-2$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, exc);
			}

			return EMPTY_JAVAPROJECTS;
		}
	}
}