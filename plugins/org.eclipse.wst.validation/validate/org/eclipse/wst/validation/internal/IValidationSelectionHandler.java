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
package org.eclipse.wst.validation.internal;

import org.eclipse.core.resources.IResource;

/**
 * This is used by extensions to add valid validation types to the validate
 * menu action.
 */
public interface IValidationSelectionHandler {

	/**
	 * Return a valid IResource type for the extensible object selection,
	 * should be instance of IFolder, IFile, or IProject if this extension knows
	 * how to handle the selection, otherwise it should return null
	 */ 
	public IResource getBaseValidationType(Object selection);
	
	/**
	 * @return the classname of the validation type to register for validation
	 */
	public String getValidationTypeString();
	
	public void setValidationTypeString(String validationType);
	
}
