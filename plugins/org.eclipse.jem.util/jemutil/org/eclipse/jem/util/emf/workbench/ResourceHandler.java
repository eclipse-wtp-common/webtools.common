/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 *  $$RCSfile: ResourceHandler.java,v $$
 *  $$Revision: 1.2 $$  $$Date: 2005/02/15 23:04:14 $$ 
 */
package org.eclipse.jem.util.emf.workbench;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * Implementers of this interface are provide extension capabilities on resource set. Such as looking or creating in other resource sets for a
 * resource or an EObject.
 * 
 * @see org.eclipse.jem.util.emf.workbench.ProjectResourceSet#add(ResourceHandler)
 * @since 1.0.0
 */
public interface ResourceHandler {

	/**
	 * Each ResourceHandler for a WorkbenchContext (which holds a ProjectResourceSet) will get an oportunity to get the Resource given the uriString
	 * prior to the originatingResourceSet getting it in the normal manner.
	 * 
	 * If this handler loaded a Resource in its create(ResourceSet, uriString) then this method should be able to return it as well.
	 * 
	 * @param originatingResourceSet
	 * @param uri
	 * @return resource if found or <code>nulll/code> if this handler didn't find it.
	 * 
	 * @since 1.0.0
	 */
	Resource getResource(ResourceSet originatingResourceSet, URI uri);

	/**
	 * Get the EObject for the given URI, if it can. Load the resource if loadOnDemand is <code>true</code>.
	 * 
	 * @param originatingResourceSet
	 * @param uri
	 *            uri of EObject being requested
	 * @param loadOnDemand
	 *            <code>true</code> if resource should be loaded
	 * @return eobject if found or <code>null</code> if not.
	 */
	EObject getEObjectFailed(ResourceSet originatingResourceSet, URI uri, boolean loadOnDemand);

	/**
	 * Create the resource pointed to be the URI if this handler will handle it.
	 * 
	 * @param originatingResourceSet
	 * @param uri
	 * @return resource if created, or <code>null</code> if handler doesn't handle this type.
	 */
	Resource createResource(ResourceSet originatingResourceSet, URI uri);
}