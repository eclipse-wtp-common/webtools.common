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
/*
 * Created on May 4, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.eclipse.wst.validation.internal.operations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * @author vijayb
 */
public class ReferencialFileValidatorHelper {
	public ReferencialFileValidatorHelper() {
		super();
	}

	/**
	 * Return a list of all files contained in project to infinite depth
	 */
	public static List getAllProjectFiles(IProject project) {
		List result = new ArrayList();
		if (project == null)
			return result;
		try {
			result = collectFiles(project.members(), result);
		} catch (CoreException e) {
			//Ignore
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
}