/*
 * Created on Nov 23, 2004
 */
package org.eclipse.wst.common.frameworks.internal;

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
