/*******************************************************************************
 * Copyright (c) 2003, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emfworkbench;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.wst.validation.internal.IValidationSelectionHandler;


/**
 * Emf validation extension for valaditemenuaction
 */
public class EmfValidationHandler implements IValidationSelectionHandler {

	private String validationType = null;
	
	/**
	 * Default constructor
	 */
	public EmfValidationHandler() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.frameworks.internal.IValidationSelectionHandler#getBaseValidationType(java.lang.Object)
	 */
	@Override
	public IResource getBaseValidationType(Object selection) {
		if (selection instanceof EObject) {
			EObject eObject = (EObject) selection;
			Resource resource = eObject.eResource();
			IProject project = ProjectUtilities.getProject(resource);
			return project;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.frameworks.internal.IValidationSelectionHandler#getValidationTypeString()
	 */
	@Override
	public String getValidationTypeString() {
		return validationType;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.frameworks.internal.IValidationSelectionHandler#setValidationTypeString(java.lang.String)
	 */
	@Override
	public void setValidationTypeString(String validationType) {
		this.validationType = validationType;
	}
}
