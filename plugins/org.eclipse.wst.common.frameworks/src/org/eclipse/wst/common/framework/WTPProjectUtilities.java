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
/*
 * Created on Nov 4, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.eclipse.wst.common.framework;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclispe.wst.common.framework.plugin.WTPCommonPlugin;


/**
 * @author jsholl
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WTPProjectUtilities {

	/**
	 * Adds a old nauture to a project, FIRST, this is used to make project backward compatible
	 */
	public static void addOldNatureToProject(IProject proj, String natureId) throws CoreException {
		IProjectDescription description = proj.getDescription();
		String[] prevNatures = description.getNatureIds();

		//check if the old nature does not exist
		boolean addNature = true;
		for (int i = 0; i < prevNatures.length; i++) {
			String nature = prevNatures[i];
			if (nature.equals(natureId)) {
				addNature = false;
				break;
			}
		}
		if (addNature) {
			String[] newNatures = new String[prevNatures.length + 1];
			System.arraycopy(prevNatures, 0, newNatures, 1, prevNatures.length);
			newNatures[0] = natureId;
			description.setNatureIds(newNatures);
			proj.setDescription(description, IResource.AVOID_NATURE_CONFIG, null);
		}
	}

	/**
	 * Adds a nature in the project in the index specified
	 */
	public static void addOldNatureToProject(IProject proj, String natureId, int index) throws CoreException {
		IProjectDescription description = proj.getDescription();
		String[] prevNatures = description.getNatureIds();

		//check if the old nature does not exist
		boolean addNature = true;
		for (int i = 0; i < prevNatures.length; i++) {
			String nature = prevNatures[i];
			if (nature.equals(natureId)) {
				addNature = false;
				break;
			}
		}
		if (addNature) {
			String[] newNatures = new String[prevNatures.length + 1];
			System.arraycopy(prevNatures, 0, newNatures, 0, index);

			newNatures[index] = natureId;
			System.arraycopy(prevNatures, index, newNatures, index + 1, prevNatures.length - index);
			description.setNatureIds(newNatures);
			proj.setDescription(description, IResource.AVOID_NATURE_CONFIG, null);
		}
	}

	/**
	 * Adds a nauture to a project, FIRST
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
	 * Adds a nature to a project, LAST
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
	 * remove a nature from the project
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
			}
			newNatures[newsize++] = prevNatures[i];
		}
		if (!matchfound)
			throw new CoreException(new Status(IStatus.ERROR, WTPCommonPlugin.PLUGIN_ID, 0, "The nature id " + natureId + " does not exist on the project " + project.getName(), null)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		String[] temp = newNatures;
		newNatures = new String[newsize];
		System.arraycopy(temp, 0, newNatures, 0, newsize);
		description.setNatureIds(newNatures);
		project.setDescription(description, null);
	}

	public static IProject getProject(Object object) {
		IProject result = null;

		if (object instanceof IProject)
			result = (IProject) object;
		else if (object instanceof IResource)
			result = ((IResource) object).getProject();
		else if (object instanceof IAdaptable)
			result = (IProject) ((IAdaptable) object).getAdapter(IProject.class);

		return result;
	}


}