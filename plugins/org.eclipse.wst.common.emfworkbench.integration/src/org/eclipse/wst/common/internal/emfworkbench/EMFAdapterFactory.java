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
 * Created on Feb 25, 2004
 *  
 */
package org.eclipse.wst.common.internal.emfworkbench;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;

/**
 * @author mdelder
 *  
 */
public class EMFAdapterFactory implements IAdapterFactory {

	protected static final Class IPROJECT_CLASS = IProject.class;
	protected static final Class IRESOURCE_CLASS = IResource.class;
	protected static final Class IFILE_CLASS = IFile.class;
	protected static final Class EOBJECT_CLASS = EObject.class;

	/**
	 *  
	 */
	public EMFAdapterFactory() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof EObject) {
			if (adapterType == IFILE_CLASS || adapterType == IRESOURCE_CLASS)
				return WorkbenchResourceHelper.getFile((EObject) adaptableObject);
			else if (adapterType == IPROJECT_CLASS)
				return ProjectUtilities.getProject((EObject) adaptableObject);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	public Class[] getAdapterList() {
		return new Class[]{IPROJECT_CLASS, IRESOURCE_CLASS, IFILE_CLASS};
	}

}