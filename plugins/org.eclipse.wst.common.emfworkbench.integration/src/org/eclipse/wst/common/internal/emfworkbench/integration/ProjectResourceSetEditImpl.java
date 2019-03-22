/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Mar 4, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.eclipse.wst.common.internal.emfworkbench.integration;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ContentHandler;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jem.internal.util.emf.workbench.ProjectResourceSetImpl;
import org.eclipse.wst.common.internal.emf.resource.ReferencedResource;
import org.eclipse.wst.common.internal.emfworkbench.WorkbenchResourceHelper;

/**
 * @author schacher
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ProjectResourceSetEditImpl extends ProjectResourceSetImpl {

	/**
	 * @param aProject
	 */
	public ProjectResourceSetEditImpl(IProject aProject) {
		super(aProject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.internal.util.emf.workbench.ProjectResourceSetImpl#createResource(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public Resource createResource(URI uri, String contentType) {
		Resource result = super.createResource(uri, contentType);
		if (result != null && WorkbenchResourceHelper.isReferencedResource(result))
			WorkbenchResourceHelper.cacheSynchronizationStamp((ReferencedResource) result);
		return result;
	}

	@Override
	public Resource createResource(URI uri) {
		return createResource(uri, ContentHandler.UNSPECIFIED_CONTENT_TYPE);
	}
}
