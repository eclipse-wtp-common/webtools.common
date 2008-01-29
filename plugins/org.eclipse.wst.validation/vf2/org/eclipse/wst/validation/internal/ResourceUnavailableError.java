package org.eclipse.wst.validation.internal;

import org.eclipse.core.resources.IResource;

/**
 * This internal error is used to signal that a resource is now unavailable.
 * <p>
 * This error is used to "exit" the validation framework. 
 * <p>
 * This is an error rather than a runtime exception, because some parts of Eclipse like to
 * trap RuntimeExceptions and log them.
 * @author karasiuk
 *
 */
public class ResourceUnavailableError extends Error {

	private static final long serialVersionUID = 200801290853L;
	
	private IResource _resource;
	
	public ResourceUnavailableError(IResource resource){
		super();
		_resource = resource;
	}

	public IResource getResource() {
		return _resource;
	}

}
