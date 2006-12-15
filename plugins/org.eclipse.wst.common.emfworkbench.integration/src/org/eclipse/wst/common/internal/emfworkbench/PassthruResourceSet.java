/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emfworkbench;


import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jem.internal.util.emf.workbench.ProjectResourceSetImpl;
import org.eclipse.jem.util.emf.workbench.WorkbenchResourceHelperBase;

/**
 * This ResourceSet is to be used by plugins that are currently loading to a cached ResourceSet that
 * always loads relative to the Workbench. This ResourceSet will direct the loads to the appropriate
 * Project ResourceSet. It will only load in this ResourceSet if it was unable to load via the
 * Workspace.
 * 
 * If you set want to used this ResourceSet so that the loaded resources are isolated from everyone
 * else, you should use the
 * 
 * @link PassthruResourceSet(IProject) constructor. This will load all resources locally and it will
 *       not delegate to another ProjectResourceSet. This would be equivalent to creating a
 *       ProjectResourceSet without setting any handlers.
 *  
 */
public class PassthruResourceSet extends ProjectResourceSetImpl {
	protected boolean isIsolated = false;

	public class PassthruResourcesEList extends ResourceSetImpl.ResourcesEList {
		public boolean add(Object object) {
			if (object == null)
				return false;
			ResourceSet set = WorkbenchResourceHelperBase.getResourceSet(((Resource) object).getURI());
			if (set != null)
				return set.getResources().add((Resource)object);

			return super.add(object);
		}


		public boolean addAll(Collection collection) {
			if (collection.isEmpty())
				return false;
			Iterator it = collection.iterator();
			Resource res;
			while (it.hasNext()) {
				res = (Resource) it.next();
				if (!WorkbenchResourceHelperBase.cacheResource(res))
					super.add(res);
			}
			return true;
		}
	}

	public PassthruResourceSet() {
		isIsolated = false;
	}

	/**
	 * This constructor should only be used if you want to use this ResourceSet isolated from the
	 * actual cached ProjectResourcSet for the passed IProject.
	 */
	public PassthruResourceSet(IProject project) {
		setProject(project);
		isIsolated = true;
	}

	public boolean isIsolated() {
		return isIsolated;
	}

	public Resource createResource(URI uri) {
		Resource result = WorkbenchResourceHelperBase.getExistingOrCreateResource(uri);
		if (result == null)
			return super.createResource(uri);
		return result;
	}

	/**
	 * @see org.eclipse.emf.ecore.resource.impl.ResourceSetImpl#demandCreateResource(URI)
	 */
	protected Resource demandCreateResource(URI uri) {
		Resource result = WorkbenchResourceHelperBase.createResource(uri);
		if (result == null)
			return super.createResource(uri); //We do want to call super.createResource and not
		// demandCreateResource
		return result;
	}

	/*
	 * Javadoc copied from interface.
	 */
	public EList getResources() {
		if (isIsolated)
			return super.getResources();
		if (resources == null) {
			resources = new PassthruResourcesEList();
		}
		return resources;
	}

	/**
	 * @see org.eclipse.jem.internal.util.emf.workbench.ProjectResourceSetImpl#createResourceFromHandlers(URI)
	 */
	protected Resource createResourceFromHandlers(URI uri) {
		if (!isIsolated)
			return super.createResourceFromHandlers(uri);
		return null;
	}

	/**
	 * @see org.eclipse.jem.internal.util.emf.workbench.ProjectResourceSetImpl#getResourceFromHandlers(URI)
	 */
	protected Resource getResourceFromHandlers(URI uri) {
		if (!isIsolated)
			return super.getResourceFromHandlers(uri);
		return null;
	}

	/**
	 * @see com.ibm.etools.emf.workbench.ProjectResourceSetImpl#getEObjectFromHandlers(URI, boolean)
	 */
	protected EObject getEObjectFromHandlers(URI uri, boolean loadOnDemand) {
		if (!isIsolated)
			return super.getEObjectFromHandlers(uri, loadOnDemand);
		return null;
	}

}
